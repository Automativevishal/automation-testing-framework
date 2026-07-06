package com.Utility;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

    public static WebDriver driver;
    public static ConfigDataProvider config;
    public static ExelDataProvider exel;

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void initializationSetup() {
        try {
            config = new ConfigDataProvider();
            exel = new ExelDataProvider();
            Library.logInfo("Config and Excel test data initialized successfully");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize config or Excel test data", e);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestContext context) {

        Map<String, String> parameters = context.getCurrentXmlTest().getAllParameters();

        String browserName = firstNonBlank(
                parameters.get("BrowserName"),
                parameters.get("browser"),
                System.getProperty("BrowserName"),
                System.getProperty("browser"),
                config.getBrowser());

        WebDriver webDriver = createDriver(browserName);

        DRIVER.set(webDriver);
        driver = webDriver;

        configureBrowser(webDriver);

        String finalUrl = resolveApplicationUrl(parameters);

        Library.logInfo("Launching application URL : " + finalUrl);

        webDriver.get(finalUrl);

        Library.waitForPageToLoad();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        WebDriver webDriver = getDriver();

        try {

            if (webDriver != null) {
                webDriver.quit();
                Library.logInfo("Browser closed successfully");
            }

        } finally {

            DRIVER.remove();
            driver = null;

        }
    }

    public static WebDriver getDriver() {

        WebDriver webDriver = DRIVER.get();

        return webDriver != null ? webDriver : driver;
    }

    private WebDriver createDriver(String browserName) {

        String browser = browserName == null ? "chrome" : browserName.trim().toLowerCase();

        boolean headless = config.getBooleanProperty("headless", false);

        System.out.println("Headless = " + headless);
        System.out.println("Config Headless = " + config.getProperty("headless"));
        System.out.println("Boolean Headless = " + config.getBooleanProperty("headless", false));

        switch (browser) {

            case "chrome":

                WebDriverManager.chromedriver().setup();

                ChromeOptions chromeOptions = new ChromeOptions();

                chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");

                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }

                return new ChromeDriver(chromeOptions);

            case "edge":

                WebDriverManager.edgedriver().setup();

                EdgeOptions edgeOptions = new EdgeOptions();

                edgeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                edgeOptions.addArguments("--disable-notifications");
                edgeOptions.addArguments("--disable-popup-blocking");

                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--window-size=1920,1080");
                }

                return new EdgeDriver(edgeOptions);

            default:

                throw new IllegalArgumentException(
                        "Unsupported browser : " + browserName + ". Supported browsers : chrome, edge");
        }
    }

    private void configureBrowser(WebDriver webDriver) {

        webDriver.manage().window().maximize();

        webDriver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getIntProperty("implicitWait", 0)));

        webDriver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(config.getIntProperty("pageLoadTimeout", 60)));
    }

    // ================= URL LOGIC =================

    private String resolveApplicationUrl(Map<String, String> parameters) {

        String xmlUrl = firstNonBlank(
                parameters.get("url"),
                parameters.get("URL"),
                System.getProperty("url"));

        // Priority 1 : URL from testng.xml
        if (!xmlUrl.isEmpty()) {
            Library.logInfo("URL picked from testng.xml");
            return xmlUrl;
        }

        // Priority 2 : URL from Config.properties
        Library.logInfo("URL picked from Config.properties");

        return config.getOrangeHRM();

        // OR
        // return config.getProperty("OrangeHRM");
    }

    private String firstNonBlank(String... values) {

        if (values == null) {
            return "";
        }

        for (String value : values) {

            if (value != null &&
                    !value.trim().isEmpty() &&
                    !value.trim().startsWith("${")) {

                return value.trim();
            }
        }

        return "";
    }
}