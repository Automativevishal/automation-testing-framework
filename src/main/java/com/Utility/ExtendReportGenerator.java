package com.Utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public final class ExtendReportGenerator {

    private static ExtentReports extent;
    private static String reportPath;

    private ExtendReportGenerator() {
    }

    public static synchronized ExtentReports getReports() {
        if (extent != null) {
            return extent;
        }

        try {
            Path reportDirectory = ScreenshotUtil.getReportDirectory();
            Files.createDirectories(reportDirectory);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            reportPath = reportDirectory.resolve("ExtentReport_" + timestamp + ".html").toString();

            ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);
            reporter.config().setDocumentTitle("Hybrid Automation Execution Report");
            reporter.config().setReportName("Enterprise Selenium Hybrid Framework");
            reporter.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Framework", "Selenium Java Hybrid Framework");
            extent.setSystemInfo("Automation Type", "UI/API");
            extent.setSystemInfo("Operating System", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Environment", System.getProperty("environment", "qa"));
            extent.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
            extent.setSystemInfo("Headless", System.getProperty("headless", "false"));

            return extent;
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize Extent Report", e);
        }
    }

    public static String getReportPath() {
        return reportPath;
    }
}
