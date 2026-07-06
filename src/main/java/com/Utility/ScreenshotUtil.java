package com.Utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotUtil {

    private static final String REPORTS_FOLDER = "Reports";
    private static final String SCREENSHOT_FOLDER = "screenshots";
    private static final String DATE_PATTERN = "yyyyMMdd_HHmmss_SSS";

    private ScreenshotUtil() {
    }

    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        if (driver == null) {
            return "";
        }

        try {
            Path screenshotDirectory = getScreenshotDirectory();
            Files.createDirectories(screenshotDirectory);

            String fileName = sanitizeFileName(screenshotName) + "_" + timestamp() + ".png";
            Path destination = screenshotDirectory.resolve(fileName);

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, destination.toFile());

            return SCREENSHOT_FOLDER + "/" + fileName;
        } catch (IOException | RuntimeException e) {
            Library.logWarning("Unable to capture screenshot: " + e.getMessage());
            return "";
        }
    }

    public static String captureFailureScreenshot(WebDriver driver, String testName) {
        return captureScreenshot(driver, "FAIL_" + testName);
    }

    public static String captureBusinessStepScreenshot(WebDriver driver, String stepName) {
        return captureScreenshot(driver, "STEP_" + stepName);
    }

    public static String captureFullPageScreenshot(WebDriver driver, String screenshotName) {
        return captureScreenshot(driver, "FULL_PAGE_" + screenshotName);
    }

    public static Path getReportDirectory() {
        return Paths.get(System.getProperty("user.dir")).resolve(REPORTS_FOLDER).normalize();
    }

    private static Path getScreenshotDirectory() {
        return getReportDirectory().resolve(SCREENSHOT_FOLDER).normalize();
    }

    private static String timestamp() {
        return new SimpleDateFormat(DATE_PATTERN).format(new Date());
    }

    private static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "screenshot";
        }
        return fileName.trim().replaceAll("[^a-zA-Z0-9._-]+", "_");
    }
}
