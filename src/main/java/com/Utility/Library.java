package com.Utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.MediaEntityBuilder;

/**
 * Core Selenium automation utility library used by all Page Objects and Test
 * Classes in the framework.
 *
 * <p>This class centralizes element interactions, waits, navigation,
 * JavaScript actions, alerts, window/frame handling and rich, consistent
 * logging (console, {@code ExtentReports} and TestNG listeners) for every
 * action performed against the browser.</p>
 *
 * <p><b>Backward compatibility:</b> All existing public method signatures are
 * preserved exactly as-is so that no Page Object or Test Class requires any
 * modification. Only internal logging, masking and helper logic has been
 * enhanced.</p>
 */
public class Library extends BaseClass {

    private static final int DEFAULT_WAIT_SECONDS = 20;

    /** Mask applied to sensitive field values in all logs and reports. */
    private static final String MASK = "********";

    /**
     * Field name fragments (case-insensitive) that are treated as sensitive.
     * Any field whose name contains one of these fragments will have its
     * value masked in every log line, screenshot label and report entry.
     */
    private static final String[] SENSITIVE_FIELD_KEYWORDS = {
            "password", "confirm password", "new password", "old password",
            "otp", "pin", "cvv", "security code", "passcode"
    };

    /**
     * Types the given value into the supplied element after waiting for it
     * to become visible, clicking it, and clearing any existing content using
     * {@code CTRL+A} followed by {@code DELETE}.
     *
     * <p>Adds null-safety (a {@code null} value is treated as an empty
     * string), trims incidental leading/trailing whitespace, masks the value
     * in logs if the field is sensitive (password, OTP, PIN, CVV, etc.), and
     * records the execution time of the action.</p>
     *
     * @param element   the target input element
     * @param value     the value to type (may be {@code null})
     * @param fieldName the human-readable field name used in logs/reports
     */
    public static void type(WebElement element, String value, String fieldName) {
        String safeValue = value == null ? "" : value.trim();
        String displayValue = maskIfSensitive(fieldName, safeValue);
        performStep("Typed '" + displayValue + "' into " + fieldName, fieldName, () -> {
            WebElement visibleElement = waitForElementVisible(element);
            visibleElement.click();
            visibleElement.sendKeys(Keys.CONTROL + "a");
            visibleElement.sendKeys(Keys.DELETE);
            visibleElement.sendKeys(safeValue);
        });
    }

    /**
     * Waits until the element is clickable and clicks it.
     *
     * @param element     the element to click
     * @param elementName the human-readable element name used in logs/reports
     */
    public static void click(WebElement element, String elementName) {
        performStep("Clicked " + elementName, elementName, () -> waitForElementClickable(element).click());
    }

    /**
     * Waits until the element is visible and clears its content.
     *
     * @param element     the element to clear
     * @param elementName the human-readable element name used in logs/reports
     */
    public static void clear(WebElement element, String elementName) {
        performStep("Cleared " + elementName, elementName, () -> waitForElementVisible(element).clear());
    }

    /**
     * Waits until the element is clickable and submits its enclosing form.
     *
     * @param element     the element to submit
     * @param elementName the human-readable element name used in logs/reports
     */
    public static void submit(WebElement element, String elementName) {
        performStep("Submitted " + elementName, elementName, () -> waitForElementClickable(element).submit());
    }

    /**
     * Uploads a file by sending its absolute path to a file-input element.
     *
     * <p>Only the file name is ever written to logs/reports; the full file
     * system path is never printed.</p>
     *
     * @param element   the file-input element
     * @param filePath  the absolute path of the file to upload
     * @param fieldName the human-readable field name used in logs/reports
     */
    public static void uploadFile(WebElement element, String filePath, String fieldName) {
        String fileName = extractFileName(filePath);
        performStep("Uploaded '" + fileName + "' into " + fieldName, fieldName, () -> {
            WebElement uploadElement = waitForElementVisible(element);
            uploadElement.sendKeys(filePath);
        });
    }
    
    public static void uploadMultipleFiles(
            WebElement element,
            String[] filePaths,
            String fieldName) {

        String fileNames = Arrays.stream(filePaths)
                .map(path -> extractFileName(path))
                .collect(Collectors.joining(", "));

        performStep("Uploaded '" + fileNames + "' into " + fieldName,
                fieldName, () -> {

            WebElement uploadElement = waitForElementVisible(element);

            uploadElement.sendKeys(
                    String.join("\n", filePaths)
            );
        });
    }

    /**
     * Captures a full business-step screenshot (after waiting for the page
     * to finish loading) and logs it as a passing step in the report.
     *
     * @param stepName the name of the business step being captured
     */
    public static void captureStep(String stepName) {
        try {
            waitForPageToLoad();
            String path = ScreenshotUtil.captureBusinessStepScreenshot(getDriver(), stepName);
            if (listeners.getTest() != null && !path.isEmpty()) {
                listeners.getTest().pass(stepName, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            } else {
                logPass(stepName);
            }
        } catch (Exception e) {
            logWarning("Unable to attach business step screenshot: " + stepName);
        }
    }

    /**
     * Captures a standard viewport screenshot.
     *
     * @param screenshotName the label used for the saved screenshot
     * @return the file path of the saved screenshot
     */
    public static String captureScreenshot(String screenshotName) {
        return ScreenshotUtil.captureScreenshot(getDriver(), screenshotName);
    }

    /**
     * Captures a full-page (scrolling) screenshot.
     *
     * @param screenshotName the label used for the saved screenshot
     * @return the file path of the saved screenshot
     */
    public static String captureFullPageScreenshot(String screenshotName) {
        return ScreenshotUtil.captureFullPageScreenshot(getDriver(), screenshotName);
    }

    /**
     * Waits until the given element is visible.
     *
     * @param element the element to wait for
     * @return the same element once visible
     */
    public static WebElement waitForElementVisible(WebElement element) {
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until the given element is clickable.
     *
     * @param element the element to wait for
     * @return the same element once clickable
     */
    public static WebElement waitForElementClickable(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until an element located by the given locator is present in the DOM.
     *
     * @param locator the locator to search with
     * @return the located element
     */
    public static WebElement waitForPresence(By locator) {
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until all elements in the given list are visible.
     *
     * @param elements the elements to wait for
     * @return the same list once all elements are visible
     */
    public static List<WebElement> waitForAllElements(List<WebElement> elements) {
        return getWait().until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Waits until {@code document.readyState} reports {@code complete}.
     * No-op if the driver is not yet initialized.
     */
    public static void waitForPageToLoad() {
        WebDriver webDriver = getDriver();
        if (webDriver == null) {
            return;
        }

        getWait().until(driverInstance ->
                "complete".equals(((JavascriptExecutor) driverInstance).executeScript("return document.readyState")));
    }

    /**
     * Checks whether the element is displayed, logging the outcome.
     *
     * @param element     the element to check
     * @param elementName the human-readable element name used in logs
     * @return {@code true} if displayed, {@code false} otherwise (including on error)
     */
    public static boolean isDisplayed(WebElement element, String elementName) {
        try {
            boolean status = waitForElementVisible(element).isDisplayed();
            logInfo(elementName + " displayed: " + status);
            return status;
        } catch (RuntimeException e) {
            logWarning(elementName + " is not displayed");
            return false;
        }
    }

    /**
     * Checks whether the element is enabled, logging the outcome.
     *
     * @param element     the element to check
     * @param elementName the human-readable element name used in logs
     * @return {@code true} if enabled, {@code false} otherwise (including on error)
     */
    public static boolean isEnabled(WebElement element, String elementName) {
        try {
            boolean status = waitForElementVisible(element).isEnabled();
            logInfo(elementName + " enabled: " + status);
            return status;
        } catch (RuntimeException e) {
            logWarning(elementName + " is not enabled");
            return false;
        }
    }

    /**
     * Checks whether the element is selected, logging the outcome.
     *
     * @param element     the element to check
     * @param elementName the human-readable element name used in logs
     * @return {@code true} if selected, {@code false} otherwise (including on error)
     */
    public static boolean isSelected(WebElement element, String elementName) {
        try {
            boolean status = waitForElementVisible(element).isSelected();
            logInfo(elementName + " selected: " + status);
            return status;
        } catch (RuntimeException e) {
            logWarning(elementName + " is not selected");
            return false;
        }
    }

    /**
     * Fetches the visible text of an element, logging the exact text found.
     *
     * @param element     the element to read text from
     * @param elementName the human-readable element name used in logs
     * @return the element's visible text
     */
    public static String getText(WebElement element, String elementName) {
        try {
            String text = waitForElementVisible(element).getText();
            logInfo("Fetched text from " + elementName + " = " + text);
            return text;
        } catch (RuntimeException e) {
            captureAndLogFailure("Fetched text from " + elementName, elementName, e);
            throw e;
        }
    }

    /**
     * Fetches an attribute value from an element, logging the attribute name
     * and value (masked if the element/attribute is deemed sensitive).
     *
     * @param element       the element to read the attribute from
     * @param attributeName the attribute to fetch (e.g. {@code value}, {@code class})
     * @param elementName   the human-readable element name used in logs
     * @return the attribute's value
     */
    public static String getAttribute(WebElement element, String attributeName, String elementName) {
        try {
            String value = waitForElementVisible(element).getAttribute(attributeName);
            String displayValue = maskIfSensitive(elementName, value);
            logInfo("Fetched attribute '" + attributeName + "' = '" + displayValue + "'");
            return value;
        } catch (RuntimeException e) {
            captureAndLogFailure("Fetched attribute '" + attributeName + "' from " + elementName, elementName, e);
            throw e;
        }
    }

    /**
     * Asserts that an element's trimmed text equals the expected value.
     *
     * @param element      the element to verify
     * @param expectedText the expected text
     * @param elementName  the human-readable element name used in logs
     */
    public static void verifyTextEquals(WebElement element, String expectedText, String elementName) {
        Assert.assertEquals(getText(element, elementName).trim(), expectedText, elementName + " text mismatch");
        logPass("Verified text for " + elementName);
    }

    /**
     * Asserts that an element is displayed.
     *
     * @param element     the element to verify
     * @param elementName the human-readable element name used in logs
     */
    public static void verifyElementDisplayed(WebElement element, String elementName) {
        Assert.assertTrue(isDisplayed(element, elementName), elementName + " should be displayed");
        logPass("Verified " + elementName + " is displayed");
    }

    /**
     * Selects a dropdown option by its visible text.
     *
     * @param element   the {@code <select>} element
     * @param text      the visible text to select
     * @param fieldName the human-readable field name used in logs
     */
    public static void selectByVisibleText(WebElement element, String text, String fieldName) {
        performStep("Selected '" + text + "' from " + fieldName, fieldName,
                () -> new Select(waitForElementVisible(element)).selectByVisibleText(text));
    }

    /**
     * Selects a dropdown option by its underlying {@code value} attribute.
     *
     * @param element   the {@code <select>} element
     * @param value     the option value to select
     * @param fieldName the human-readable field name used in logs
     */
    public static void selectByValue(WebElement element, String value, String fieldName) {
        performStep("Selected value '" + value + "' from " + fieldName, fieldName,
                () -> new Select(waitForElementVisible(element)).selectByValue(value));
    }

    /**
     * Selects a dropdown option by its zero-based index.
     *
     * @param element   the {@code <select>} element
     * @param index     the zero-based option index to select
     * @param fieldName the human-readable field name used in logs
     */
    public static void selectByIndex(WebElement element, int index, String fieldName) {
        performStep("Selected index '" + index + "' from " + fieldName, fieldName,
                () -> new Select(waitForElementVisible(element)).selectByIndex(index));
    }

    /**
     * Retrieves the currently selected option's visible text from a dropdown.
     *
     * @param element   the {@code <select>} element
     * @param fieldName the human-readable field name used in logs
     * @return the selected option's visible text
     */
    public static String getFirstSelectedOption(WebElement element, String fieldName) {
        String selected = new Select(waitForElementVisible(element)).getFirstSelectedOption().getText();
        logInfo("Selected option for " + fieldName + " = '" + selected + "'");
        return selected;
    }

    /**
     * Scrolls the given element into the center of the viewport via JavaScript.
     *
     * @param element the element to scroll to
     */
    public static void scrollToElement(WebElement element) {
        jsExecutor().executeScript("arguments[0].scrollIntoView({block:'center', inline:'nearest'});", element);
    }

    /**
     * Scrolls the window by the given horizontal and vertical offsets.
     *
     * @param x horizontal offset in pixels
     * @param y vertical offset in pixels
     */
    public static void scrollBy(int x, int y) {
        jsExecutor().executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    /** Scrolls the window to the bottom of the page. */
    public static void scrollToBottom() {
        jsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /** Scrolls the window to the top of the page. */
    public static void scrollToTop() {
        jsExecutor().executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Executes an arbitrary JavaScript snippet against the current driver.
     *
     * @param script    the JavaScript to execute
     * @param arguments optional arguments passed to the script
     * @return the script's return value, if any
     */
    public static Object executeJavaScript(String script, Object... arguments) {
        return jsExecutor().executeScript(script, arguments);
    }

    /**
     * Clicks an element via JavaScript after scrolling it into view. Useful
     * for elements that are obscured or not reliably clickable natively.
     *
     * @param element     the element to click
     * @param elementName the human-readable element name used in logs
     */
    public static void jsClick(WebElement element, String elementName) {
        performStep("JavaScript clicked " + elementName, elementName, () -> {
            WebElement visibleElement = waitForElementVisible(element);
            scrollToElement(visibleElement);
            jsExecutor().executeScript("arguments[0].click();", visibleElement);
        });
    }

    /**
     * Moves the mouse pointer over the given element.
     *
     * @param element     the element to hover on
     * @param elementName the human-readable element name used in logs
     */
    public static void mouseHover(WebElement element, String elementName) {
        performStep("Mouse hovered on " + elementName, elementName,
                () -> actions().moveToElement(waitForElementVisible(element)).perform());
    }

    /**
     * Double-clicks the given element.
     *
     * @param element     the element to double click
     * @param elementName the human-readable element name used in logs
     */
    public static void doubleClick(WebElement element, String elementName) {
        performStep("Double clicked " + elementName, elementName,
                () -> actions().doubleClick(waitForElementClickable(element)).perform());
    }

    /**
     * Right-clicks (context clicks) the given element.
     *
     * @param element     the element to right click
     * @param elementName the human-readable element name used in logs
     */
    public static void rightClick(WebElement element, String elementName) {
        performStep("Right clicked " + elementName, elementName,
                () -> actions().contextClick(waitForElementVisible(element)).perform());
    }

    /**
     * Performs a drag-and-drop from a source element to a target element.
     *
     * @param source     the element being dragged
     * @param target     the drop target element
     * @param actionName the human-readable action name used in logs
     */
    public static void dragAndDrop(WebElement source, WebElement target, String actionName) {
        performStep(actionName, actionName,
                () -> actions().dragAndDrop(waitForElementVisible(source), waitForElementVisible(target)).perform());
    }

    /**
     * Moves the mouse to the given element and clicks it via the Actions API.
     *
     * @param element     the element to move to and click
     * @param elementName the human-readable element name used in logs
     */
    public static void moveToElementAndClick(WebElement element, String elementName) {
        performStep("Moved to and clicked " + elementName, elementName,
                () -> actions().moveToElement(waitForElementClickable(element)).click().perform());
    }

    /**
     * Sends the {@code ENTER} key to the given element.
     *
     * @param element     the target element
     * @param elementName the human-readable element name used in logs
     */
    public static void pressEnter(WebElement element, String elementName) {
        performStep("Pressed ENTER on " + elementName, elementName,
                () -> waitForElementVisible(element).sendKeys(Keys.ENTER));
    }

    /**
     * Sends the {@code TAB} key to the given element.
     *
     * @param element     the target element
     * @param elementName the human-readable element name used in logs
     */
    public static void pressTab(WebElement element, String elementName) {
        performStep("Pressed TAB on " + elementName, elementName,
                () -> waitForElementVisible(element).sendKeys(Keys.TAB));
    }

    /**
     * Presses and holds the given key.
     *
     * @param key the key to press down
     */
    public static void keyDown(Keys key) {
        actions().keyDown(key).perform();
    }

    /**
     * Releases a previously held key.
     *
     * @param key the key to release
     */
    public static void keyUp(Keys key) {
        actions().keyUp(key).perform();
    }

    /**
     * Waits for and returns a JavaScript alert.
     *
     * @return the alert once present
     */
    public static Alert waitForAlert() {
        return getWait().until(ExpectedConditions.alertIsPresent());
    }

    /** Accepts (clicks OK on) the current alert. */
    public static void acceptAlert() {
        performStep("Accepted alert", "Alert", () -> waitForAlert().accept());
    }

    /** Dismisses (clicks Cancel on) the current alert. */
    public static void dismissAlert() {
        performStep("Dismissed alert", "Alert", () -> waitForAlert().dismiss());
    }

    /**
     * Fetches the text displayed in the current alert.
     *
     * @return the alert's text
     */
    public static String getAlertText() {
        String text = waitForAlert().getText();
        logInfo("Alert text: " + text);
        return text;
    }

    /**
     * Types the given value into a prompt-style alert.
     *
     * @param value the value to enter into the alert
     */
    public static void sendKeysToAlert(String value) {
        performStep("Entered text into alert", "Alert", () -> waitForAlert().sendKeys(value));
    }

    /**
     * Switches the driver's focus into the given frame element.
     *
     * @param frameElement the frame/iframe element to switch into
     */
    public static void switchToFrame(WebElement frameElement) {
        getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
        logInfo("Switched to frame element");
    }

    /**
     * Switches the driver's focus into the frame at the given index.
     *
     * @param index the zero-based frame index
     */
    public static void switchToFrame(int index) {
        getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
        logInfo("Switched to frame index: " + index);
    }

    /**
     * Switches the driver's focus into the frame with the given name or id.
     *
     * @param nameOrId the frame's {@code name} or {@code id} attribute
     */
    public static void switchToFrame(String nameOrId) {
        getWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
        logInfo("Switched to frame: " + nameOrId);
    }

    /** Switches the driver's focus back to the top-level document. */
    public static void switchToDefaultContent() {
        getDriver().switchTo().defaultContent();
        logInfo("Switched to default content");
    }

    /** Switches the driver's focus to the immediate parent frame. */
    public static void switchToParentFrame() {
        getDriver().switchTo().parentFrame();
        logInfo("Switched to parent frame");
    }

    /**
     * Returns the handle of the currently active window.
     *
     * @return the current window handle
     */
    public static String getParentWindow() {
        return getDriver().getWindowHandle();
    }

    /**
     * Waits for a new window/tab to open and switches focus to it.
     */
    public static void switchToNewWindow() {
        String parent = getParentWindow();
        getWait().until(driver -> driver.getWindowHandles().size() > 1);
        for (String window : getDriver().getWindowHandles()) {
            if (!window.equals(parent)) {
                getDriver().switchTo().window(window);
                logInfo("Switched to new window");
                return;
            }
        }
    }

    /**
     * Switches focus to the window whose title contains the given text.
     *
     * @param title the (partial) window title to match
     */
    public static void switchToWindow(String title) {
        Set<String> windows = getDriver().getWindowHandles();
        for (String window : windows) {
            getDriver().switchTo().window(window);
            if (getDriver().getTitle().contains(title)) {
                logInfo("Switched to window: " + title);
                return;
            }
        }
        throw new NoSuchElementException("Window not found with title: " + title);
    }

    /**
     * Switches focus back to a previously captured window handle.
     *
     * @param parentWindow the window handle to switch back to
     */
    public static void switchToMainWindow(String parentWindow) {
        getDriver().switchTo().window(parentWindow);
        logInfo("Switched back to main window");
    }

    /** Closes the currently focused window/tab. */
    public static void closeCurrentWindow() {
        getDriver().close();
        logInfo("Closed current window");
    }

    /** Refreshes the current page and waits for it to finish loading. */
    public static void refreshPage() {
        getDriver().navigate().refresh();
        waitForPageToLoad();
        logInfo("Page refreshed");
    }

    /** Navigates back in browser history and waits for the page to load. */
    public static void navigateBack() {
        getDriver().navigate().back();
        waitForPageToLoad();
        logInfo("Navigated back");
    }

    /** Navigates forward in browser history and waits for the page to load. */
    public static void navigateForward() {
        getDriver().navigate().forward();
        waitForPageToLoad();
        logInfo("Navigated forward");
    }

    /**
     * Navigates to the given URL and waits for the page to load.
     *
     * @param url the URL to open
     */
    public static void openURL(String url) {
        getDriver().get(url);
        waitForPageToLoad();
        logInfo("Opened URL: " + url);
    }

    /**
     * Returns the current page URL.
     *
     * @return the current URL
     */
    public static String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    /**
     * Returns the current page title.
     *
     * @return the current page title
     */
    public static String getTitle() {
        return getDriver().getTitle();
    }

    /** Maximizes the browser window. */
    public static void maximizeBrowser() {
        getDriver().manage().window().maximize();
        logInfo("Browser maximized");
    }

    /** Minimizes the browser window. */
    public static void minimizeBrowser() {
        getDriver().manage().window().minimize();
        logInfo("Browser minimized");
    }

    /** Puts the browser window into full-screen mode. */
    public static void fullScreenBrowser() {
        getDriver().manage().window().fullscreen();
        logInfo("Browser full screen");
    }

    /**
     * Sets the browser window to a specific width and height.
     *
     * @param width  the desired window width in pixels
     * @param height the desired window height in pixels
     */
    public static void setBrowserSize(int width, int height) {
        getDriver().manage().window().setSize(new Dimension(width, height));
        logInfo("Browser size set: " + width + " x " + height);
    }

    /**
     * Waits until the current URL contains the given fragment.
     *
     * @param url the URL fragment to wait for
     */
    public static void waitForURL(String url) {
        getWait().until(ExpectedConditions.urlContains(url));
        logInfo("URL contains: " + url);
    }

    /**
     * Waits until the page title contains the given fragment.
     *
     * @param title the title fragment to wait for
     */
    public static void waitForTitle(String title) {
        getWait().until(ExpectedConditions.titleContains(title));
        logInfo("Title contains: " + title);
    }

    /**
     * Waits until the given text is present within an element.
     *
     * @param element the element to observe
     * @param text    the text to wait for
     */
    public static void waitForText(WebElement element, String text) {
        getWait().until(ExpectedConditions.textToBePresentInElement(element, text));
        logInfo("Text appeared: " + text);
    }

    /**
     * Waits until the given element becomes invisible.
     *
     * @param element the element to observe
     */
    public static void waitForInvisibility(WebElement element) {
        getWait().until(ExpectedConditions.invisibilityOf(element));
        logInfo("Element became invisible");
    }

    /**
     * Checks whether an element located by the given locator becomes present
     * within the given timeout, without throwing on failure.
     *
     * @param locator        the locator to search with
     * @param timeoutSeconds how long to wait, in seconds
     * @return {@code true} if the element became present, {@code false} on timeout
     */
    public static boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Logs an informational message to console, the report and listeners.
     *
     * @param message the message to log
     */
    public static void logInfo(String message) {
        System.out.println("[INFO] " + message);
        listeners.logInfo(message);
    }

    /**
     * Logs a passing step message to console, the report and listeners.
     *
     * @param message the message to log
     */
    public static void logPass(String message) {
        System.out.println("[PASS] " + message);
        listeners.logPass(message);
    }

    /**
     * Logs a warning message to console, the report and listeners.
     *
     * @param message the message to log
     */
    public static void logWarning(String message) {
        System.out.println("[WARNING] " + message);
        listeners.logWarning(message);
    }

    /**
     * Logs a failure message to console, the report and listeners.
     *
     * @param message the message to log
     */
    public static void logFail(String message) {
        System.out.println("[FAIL] " + message);
        listeners.logFail(message, null);
    }

    /**
     * Legacy alias for {@link #type(WebElement, String, String)}, retained
     * for backward compatibility with existing Page Objects.
     *
     * @param element   the target input element
     * @param value     the value to type
     * @param fieldName the human-readable field name used in logs
     */
    public static void custom_sendkeys(WebElement element, String value, String fieldName) {
        type(element, value, fieldName);
    }

    /**
     * Legacy alias for {@link #click(WebElement, String)}, retained for
     * backward compatibility with existing Page Objects.
     *
     * @param element     the element to click
     * @param elementName the human-readable element name used in logs
     */
    public static void custom_click(WebElement element, String elementName) {
        click(element, elementName);
    }

    /**
     * Legacy overload of {@link #click(WebElement, String)} that falls back
     * to {@code message} when {@code elementName} is {@code null}, retained
     * for backward compatibility with existing Page Objects.
     *
     * @param element     the element to click
     * @param elementName the human-readable element name used in logs (may be {@code null})
     * @param message     fallback name used when {@code elementName} is {@code null}
     */
    public static void custom_click(WebElement element, String elementName, String message) {
        click(element, elementName == null ? message : elementName);
    }

    /**
     * Builds a {@link WebDriverWait} using the configured explicit wait
     * timeout (defaulting to {@value #DEFAULT_WAIT_SECONDS} seconds).
     *
     * @return a configured {@link WebDriverWait}
     */
    private static WebDriverWait getWait() {
        int timeout = config == null ? DEFAULT_WAIT_SECONDS : config.getIntProperty("explicitWait", DEFAULT_WAIT_SECONDS);
        return getWait(timeout);
    }

    /**
     * Builds a {@link WebDriverWait} with a specific timeout.
     *
     * @param timeoutSeconds the wait timeout, in seconds
     * @return a configured {@link WebDriverWait}
     */
    private static WebDriverWait getWait(int timeoutSeconds) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Casts the current driver to {@link JavascriptExecutor}.
     *
     * @return the current driver as a {@link JavascriptExecutor}
     */
    private static JavascriptExecutor jsExecutor() {
        return (JavascriptExecutor) getDriver();
    }

    /**
     * Creates a new {@link Actions} instance bound to the current driver.
     *
     * @return a new {@link Actions} instance
     */
    private static Actions actions() {
        return new Actions(getDriver());
    }

    /**
     * Determines whether a field/element name should be treated as sensitive
     * (password, OTP, PIN, CVV, security code, passcode, etc.).
     *
     * @param fieldName the field/element name to check
     * @return {@code true} if the name matches a known sensitive keyword
     */
    private static boolean isSensitiveField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }
        String normalized = fieldName.toLowerCase(Locale.ROOT);
        for (String keyword : SENSITIVE_FIELD_KEYWORDS) {
            if (normalized.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the masked placeholder ({@value #MASK}) if the field is
     * sensitive, otherwise returns the value unchanged.
     *
     * @param fieldName the field/element name associated with the value
     * @param value     the actual value
     * @return the masked or original value
     */
    private static String maskIfSensitive(String fieldName, String value) {
        return isSensitiveField(fieldName) ? MASK : (value == null ? "" : value);
    }

    /**
     * Extracts just the file name from a full file system path, ensuring the
     * full path is never written to logs or reports.
     *
     * @param filePath the full or partial file path
     * @return the file name only, or an empty string if {@code filePath} is {@code null}
     */
    private static String extractFileName(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        try {
            Path path = Paths.get(filePath);
            Path fileName = path.getFileName();
            return fileName == null ? filePath : fileName.toString();
        } catch (RuntimeException e) {
            return filePath;
        }
    }

    /**
     * Executes a step action, timing it and logging a rich success message
     * (including elapsed milliseconds) on success, or capturing a screenshot
     * and logging a detailed failure on error.
     *
     * @param successMessage the base message to log on success (elapsed time is appended)
     * @param screenshotName the label used for the failure screenshot, if needed
     * @param action         the action to execute
     */
    private static void performStep(String successMessage, String screenshotName, StepAction action) {
        long startTime = System.currentTimeMillis();
        try {
            action.execute();
            long elapsedMs = System.currentTimeMillis() - startTime;
            logInfo(successMessage + " (" + elapsedMs + " ms)");
        } catch (RuntimeException e) {
            captureAndLogFailure(successMessage, screenshotName, e);
            throw e;
        }
    }

    /**
     * Captures a screenshot and logs a detailed failure entry containing the
     * failure message, exception type and exception message.
     *
     * @param message        the base action/step description that failed
     * @param screenshotName the label used for the failure screenshot
     * @param exception      the exception that caused the failure
     */
    private static void captureAndLogFailure(String message, String screenshotName, RuntimeException exception) {
        String exceptionType = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage() == null ? "No additional details" : exception.getMessage();
        String detailedMessage = "FAILED: " + message + " | Reason: " + exceptionType + " - " + exceptionMessage;

        String path = ScreenshotUtil.captureScreenshot(getDriver(), screenshotName);
        if (listeners.getTest() != null && path != null && !path.isEmpty()) {
            try {
                listeners.getTest().fail(detailedMessage, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
            } catch (Exception reportException) {
                listeners.logFail(detailedMessage, exception);
            }
        } else {
            listeners.logFail(detailedMessage, exception);
        }
    }

    /**
     * Functional contract for a single step's executable action, used
     * internally by {@link #performStep(String, String, StepAction)}.
     */
    @FunctionalInterface
    private interface StepAction {
        void execute();
    }
}