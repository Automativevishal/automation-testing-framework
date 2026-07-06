// ============================================================================
// Local Test Dashboard Server
// Place this "TestDashboard" folder INSIDE your Hybrid_Framework project,
// next to pom.xml. Run with:  npm install   then   npm start
// Open http://localhost:4545 in your browser.
// ============================================================================

const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const fs = require("fs");
const path = require("path");
const { spawn } = require("child_process");
const xml2js = require("xml2js");
const XLSX = require("xlsx");

// PROJECT_ROOT = the folder that contains pom.xml (parent of this TestDashboard folder)
const PROJECT_ROOT = path.resolve(__dirname, "..");
const TEST_JAVA_DIR = path.join(PROJECT_ROOT, "src", "test", "java");
const TESTNG_XML = path.join(PROJECT_ROOT, "testng.xml");
const TESTNG_BACKUP = path.join(PROJECT_ROOT, "testng.xml.dashboard-backup");
const REPORTS_DIR = path.join(PROJECT_ROOT, "Reports");
const TEST_OUTPUT_DIR = path.join(PROJECT_ROOT, "test-output");
const TESTDATA_XLSX = path.join(PROJECT_ROOT, "TestData", "data.xlsx");
const CONFIG_PROPS = path.join(PROJECT_ROOT, "Config", "Config.Properties");
const REGISTRY_FILE = path.join(__dirname, "registry.json");

const app = express();
app.use(express.json({ limit: "20mb" }));
app.use(express.static(__dirname));

const server = http.createServer(app);
const io = new Server(server);

let isRunning = false;

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function walkJavaFiles(dir) {
  let results = [];
  if (!fs.existsSync(dir)) return results;
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      results = results.concat(walkJavaFiles(full));
    } else if (entry.name.endsWith(".java")) {
      results.push(full);
    }
  }
  return results;
}

// Scan a .java file for package, public class name, and @Test method names
function parseJavaTestFile(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const pkgMatch = content.match(/package\s+([\w.]+)\s*;/);
  const classMatch = content.match(/public\s+class\s+(\w+)/);
  if (!classMatch) return null;

  const pkg = pkgMatch ? pkgMatch[1] : "";
  const className = classMatch[1];

  // Find every @Test occurrence, then the next method signature after it
  const methods = [];
  const testRegex = /@Test\b[^\n]*/g;
  let m;
  while ((m = testRegex.exec(content)) !== null) {
    const after = content.slice(m.index);
    const methodMatch = after.match(/(?:public|private|protected)?\s*(?:static\s*)?void\s+(\w+)\s*\(/);
    if (methodMatch) {
      methods.push(methodMatch[1]);
    }
  }

  if (methods.length === 0) return null; // not a real test class

  return {
    package: pkg,
    className,
    fqcn: pkg ? `${pkg}.${className}` : className,
    file: path.relative(PROJECT_ROOT, filePath).replace(/\\/g, "/"),
    methods,
  };
}

function scanTestClasses() {
  const files = walkJavaFiles(TEST_JAVA_DIR);
  const classes = [];
  for (const f of files) {
    try {
      const parsed = parseJavaTestFile(f);
      if (parsed) classes.push(parsed);
    } catch (e) {
      // skip unparsable file
    }
  }
  return classes;
}

function scanSuiteFiles() {
  const suites = [];
  if (!fs.existsSync(PROJECT_ROOT)) return suites;
  for (const entry of fs.readdirSync(PROJECT_ROOT, { withFileTypes: true })) {
    if (entry.isFile() && entry.name.endsWith(".xml") && entry.name !== "testng.xml.dashboard-backup" && entry.name !== "pom.xml") {
      suites.push(entry.name);
    }
  }
  return suites;
}

function loadRegistry() {
  if (!fs.existsSync(REGISTRY_FILE)) return { manualClasses: [] };
  try {
    return JSON.parse(fs.readFileSync(REGISTRY_FILE, "utf8"));
  } catch (e) {
    return { manualClasses: [] };
  }
}

function saveRegistry(reg) {
  fs.writeFileSync(REGISTRY_FILE, JSON.stringify(reg, null, 2));
}

// ---------------------------------------------------------------------------
// API: scan classes + suites
// ---------------------------------------------------------------------------

app.get("/api/scan", (req, res) => {
  try {
    const classes = scanTestClasses();
    const suites = scanSuiteFiles();
    res.json({ classes, suites, projectRoot: PROJECT_ROOT });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ---------------------------------------------------------------------------
// API: create a brand new test class from the UI ("Add Test Case")
// ---------------------------------------------------------------------------

app.post("/api/create-class", (req, res) => {
  try {
    const { className, methodName, baseClass } = req.body;
    if (!className || !/^[A-Z]\w*$/.test(className)) {
      return res.status(400).json({ error: "Invalid class name. Must start with a capital letter, no spaces." });
    }
    const safeMethod = (methodName && /^[a-zA-Z_]\w*$/.test(methodName)) ? methodName : "tc_01_newTest";
    const base = baseClass === "APIBaseClass" ? "APIBaseClass" : "BaseClass";

    const targetFile = path.join(TEST_JAVA_DIR, "com", "Test", `${className}.java`);
    if (fs.existsSync(targetFile)) {
      return res.status(400).json({ error: "A class with this name already exists." });
    }

    const template = `package com.Test;\r\n\r\nimport org.testng.annotations.Test;\r\nimport com.Utility.${base};\r\n\r\npublic class ${className} extends ${base} {\r\n\r\n\t@Test\r\n\tpublic void ${safeMethod}() {\r\n\t\t// TODO: add your test steps here\r\n\t}\r\n\r\n}\r\n`;

    fs.mkdirSync(path.dirname(targetFile), { recursive: true });
    fs.writeFileSync(targetFile, template);
    res.json({ success: true, file: path.relative(PROJECT_ROOT, targetFile) });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ---------------------------------------------------------------------------
// API: run selected classes or a suite file
// body: { mode: "classes" | "suite", classes: ["com.Test.Foo", ...], suiteFile: "login.xml",
//         browser: "chrome", url: "" }
// ---------------------------------------------------------------------------

app.post("/api/run", (req, res) => {
  if (isRunning) {
    return res.status(409).json({ error: "A test run is already in progress. Please wait for it to finish." });
  }

  const { mode, classes, suiteFile, browser, url } = req.body;
  let xmlContent;

  try {
    if (mode === "suite") {
      const suitePath = path.join(PROJECT_ROOT, suiteFile);
      if (!fs.existsSync(suitePath)) {
        return res.status(400).json({ error: "Suite file not found: " + suiteFile });
      }
      xmlContent = fs.readFileSync(suitePath, "utf8");
    } else {
      if (!classes || classes.length === 0) {
        return res.status(400).json({ error: "No classes selected." });
      }
      const classTags = classes.map((c) => `      <class name="${c}"/>`).join("\r\n");
      xmlContent =
        `<?xml version="1.0" encoding="UTF-8"?>\r\n` +
        `<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">\r\n` +
        `<suite name="DashboardRun">\r\n` +
        `<listeners>\r\n<listener class-name="com.Utility.listeners"></listener>\r\n</listeners>\r\n` +
        `  <test thread-count="5" name="DashboardTest">\r\n` +
        `  <parameter name="BrowserName" value="${browser || "chrome"}"></parameter>\r\n` +
        `  <parameter name="url" value="${url || ""}"></parameter>\r\n` +
        `    <classes>\r\n${classTags}\r\n    </classes>\r\n` +
        `  </test>\r\n` +
        `</suite>\r\n`;
    }
  } catch (e) {
    return res.status(500).json({ error: e.message });
  }

  // Backup existing testng.xml, swap in our generated/selected suite, run mvn test
  isRunning = true;
  const startTime = Date.now();
  const reportsBefore = fs.existsSync(REPORTS_DIR) ? new Set(fs.readdirSync(REPORTS_DIR)) : new Set();

  try {
    if (fs.existsSync(TESTNG_XML)) {
      fs.copyFileSync(TESTNG_XML, TESTNG_BACKUP);
    }
    fs.writeFileSync(TESTNG_XML, xmlContent);
  } catch (e) {
    isRunning = false;
    return res.status(500).json({ error: "Could not prepare testng.xml: " + e.message });
  }

  res.json({ started: true });

  const mvnCmd = process.platform === "win32" ? "mvn.cmd" : "mvn";
  io.emit("log", `\n>>> Starting: ${mvnCmd} test\n>>> Working directory: ${PROJECT_ROOT}\n\n`);

  const proc = spawn(mvnCmd, ["test", "-Dsurefire.useFile=false"], {
    cwd: PROJECT_ROOT,
    shell: true,
  });

  proc.stdout.on("data", (data) => io.emit("log", data.toString()));
  proc.stderr.on("data", (data) => io.emit("log", data.toString()));

  proc.on("close", async (code) => {
    // restore original testng.xml
    try {
      if (fs.existsSync(TESTNG_BACKUP)) {
        fs.copyFileSync(TESTNG_BACKUP, TESTNG_XML);
        fs.unlinkSync(TESTNG_BACKUP);
      }
    } catch (e) {
      io.emit("log", `\n[WARN] Could not restore testng.xml: ${e.message}\n`);
    }

    io.emit("log", `\n>>> Finished with exit code ${code}\n`);

    // Parse testng-results.xml for structured pass/fail
    let results = null;
    try {
      results = await parseTestngResults();
    } catch (e) {
      io.emit("log", `\n[WARN] Could not parse test results: ${e.message}\n`);
    }

    // Find newest Extent report generated during this run
    let newReport = null;
    try {
      if (fs.existsSync(REPORTS_DIR)) {
        const after = fs.readdirSync(REPORTS_DIR).filter((f) => !reportsBefore.has(f) && f.endsWith(".html"));
        if (after.length > 0) {
          newReport = after.sort((a, b) => {
            return fs.statSync(path.join(REPORTS_DIR, b)).mtimeMs - fs.statSync(path.join(REPORTS_DIR, a)).mtimeMs;
          })[0];
        }
      }
    } catch (e) {
      // ignore
    }

    isRunning = false;
    io.emit("run-finished", { exitCode: code, results, report: newReport, durationMs: Date.now() - startTime });
  });

  proc.on("error", (err) => {
    isRunning = false;
    io.emit("log", `\n[ERROR] Failed to start mvn: ${err.message}\n`);
    io.emit("run-finished", { exitCode: -1, results: null, report: null, error: err.message });
  });
});

async function parseTestngResults() {
  const resultsFile = path.join(TEST_OUTPUT_DIR, "testng-results.xml");
  if (!fs.existsSync(resultsFile)) return null;
  const xml = fs.readFileSync(resultsFile, "utf8");
  const parsed = await xml2js.parseStringPromise(xml);

  const summary = { pass: 0, fail: 0, skip: 0 };
  const classResults = {};

  const suites = parsed["testng-results"]?.suite || [];
  for (const suite of suites) {
    const tests = suite.test || [];
    for (const test of tests) {
      const classesArr = test.class || [];
      for (const cls of classesArr) {
        const clsName = cls.$.name;
        if (!classResults[clsName]) classResults[clsName] = [];
        const methods = cls["test-method"] || [];
        for (const method of methods) {
          if (method.$.is_config === "true") continue; // skip @Before/@After
          const status = method.$.status;
          const name = method.$.name;
          const durationMs = method.$["duration-ms"] || "0";
          if (status === "PASS") summary.pass++;
          else if (status === "FAIL") summary.fail++;
          else summary.skip++;

          let error = null;
          if (method.exception && method.exception[0]) {
            const ex = method.exception[0];
            error = (ex.message && ex.message[0]) || ex.$.class || "Failed";
          }

          classResults[clsName].push({ name, status, durationMs, error });
        }
      }
    }
  }

  return { summary, classResults };
}

// ---------------------------------------------------------------------------
// API: reports list + fetch
// ---------------------------------------------------------------------------

app.get("/api/reports", (req, res) => {
  try {
    if (!fs.existsSync(REPORTS_DIR)) return res.json([]);
    const files = fs
      .readdirSync(REPORTS_DIR)
      .filter((f) => f.endsWith(".html"))
      .map((f) => {
        const stat = fs.statSync(path.join(REPORTS_DIR, f));
        return { name: f, mtime: stat.mtimeMs };
      })
      .sort((a, b) => b.mtime - a.mtime);
    res.json(files);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get("/api/reports/:name", (req, res) => {
  const name = req.params.name;
  if (name.includes("..") || name.includes("/") || name.includes("\\")) {
    return res.status(400).send("Invalid file name");
  }
  const filePath = path.join(REPORTS_DIR, name);
  if (!fs.existsSync(filePath)) return res.status(404).send("Not found");
  res.sendFile(filePath);
});

// ---------------------------------------------------------------------------
// API: test data (Excel) — read / write TestData/data.xlsx
// ---------------------------------------------------------------------------

app.get("/api/testdata", (req, res) => {
  try {
    if (!fs.existsSync(TESTDATA_XLSX)) {
      return res.json({ sheets: {}, exists: false, path: TESTDATA_XLSX });
    }
    const wb = XLSX.readFile(TESTDATA_XLSX);
    const sheets = {};
    for (const name of wb.SheetNames) {
      const ws = wb.Sheets[name];
      sheets[name] = XLSX.utils.sheet_to_json(ws, { header: 1, raw: false, defval: "" });
    }
    res.json({ sheets, exists: true, path: TESTDATA_XLSX });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.post("/api/testdata", (req, res) => {
  try {
    const { sheets } = req.body;
    if (!sheets) return res.status(400).json({ error: "No sheet data provided" });

    const wb = XLSX.utils.book_new();
    for (const [sheetName, rows] of Object.entries(sheets)) {
      const ws = XLSX.utils.aoa_to_sheet(rows);
      XLSX.utils.book_append_sheet(wb, ws, sheetName.slice(0, 31));
    }
    fs.mkdirSync(path.dirname(TESTDATA_XLSX), { recursive: true });
    XLSX.writeFile(wb, TESTDATA_XLSX);
    res.json({ success: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ---------------------------------------------------------------------------
// API: Config.Properties — read / write
// ---------------------------------------------------------------------------

app.get("/api/config", (req, res) => {
  try {
    if (!fs.existsSync(CONFIG_PROPS)) return res.json({ entries: [] });
    const content = fs.readFileSync(CONFIG_PROPS, "utf8");
    const entries = content
      .split(/\r?\n/)
      .filter((l) => l.trim().length > 0)
      .map((line) => {
        const idx = line.indexOf("=");
        if (idx === -1) return { key: line, value: "" };
        return { key: line.slice(0, idx), value: line.slice(idx + 1) };
      });
    res.json({ entries });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.post("/api/config", (req, res) => {
  try {
    const { entries } = req.body;
    if (!Array.isArray(entries)) return res.status(400).json({ error: "Invalid payload" });
    const content = entries.map((e) => `${e.key}=${e.value}`).join("\r\n") + "\r\n";
    fs.mkdirSync(path.dirname(CONFIG_PROPS), { recursive: true });
    fs.writeFileSync(CONFIG_PROPS, content);
    res.json({ success: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// ---------------------------------------------------------------------------
// Serve dashboard.html at root
// ---------------------------------------------------------------------------

app.get("/", (req, res) => {
  res.sendFile(path.join(__dirname, "dashboard.html"));
});

const PORT = 4545;
server.listen(PORT, () => {
  console.log(`\n  Test Dashboard running: http://localhost:${PORT}`);
  console.log(`  Project root detected: ${PROJECT_ROOT}\n`);
});
