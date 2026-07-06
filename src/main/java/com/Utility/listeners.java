package com.Utility;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

public class listeners implements ITestListener {

    private static final ExtentReports extent = ExtendReportGenerator.getReports();
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void logInfo(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.info(message);
        }
    }

    public static void logPass(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.pass(message);
        }
    }

    public static void logWarning(String message) {
        ExtentTest test = getTest();
        if (test != null) {
            test.warning(message);
        }
    }

    public static void logFail(String message, Throwable throwable) {
        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message);
            if (throwable != null) {
                test.fail(throwable);
            }
        }
    }

    @Override
    public void onStart(ITestContext context) {
        extent.setSystemInfo("Suite Name", context.getSuite().getName());
        extent.setSystemInfo("Test Name", context.getName());
        extent.setSystemInfo("Execution Start", String.valueOf(context.getStartDate()));
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        ExtentTest test = extent.createTest(testName, description == null ? "" : description);

        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            test.assignCategory(groups);
        }

        extentTest.set(test);
        logInfo("Execution started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logPass("Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            String screenshotPath = ScreenshotUtil.captureFailureScreenshot(BaseClass.getDriver(), result.getMethod().getMethodName());
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                getTest().fail(result.getThrowable(),
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                logFail("Test failed", result.getThrowable());
            }
        } catch (Exception e) {
            logFail("Test failed and screenshot attachment failed", result.getThrowable());
            logFail("Screenshot error", e);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logWarning("Test skipped");
        if (result.getThrowable() != null) {
            getTest().skip(result.getThrowable());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.setSystemInfo("Execution End", String.valueOf(context.getEndDate()));
        extent.flush();
        extentTest.remove();
    }
}
