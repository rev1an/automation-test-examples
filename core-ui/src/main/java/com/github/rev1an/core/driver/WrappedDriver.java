/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A simple wrapper around any {@link WebDriver} implementation.
 * <p>
 * Helps to avoid casting.
 *
 * @author rev1an (Sergey Alekseev)
 * @see WebDriver
 * @see WrapsDriver
 * @see TakesScreenshot
 * @see JavascriptExecutor
 */
public class WrappedDriver implements WebDriver,
                                      WrapsDriver,
                                      TakesScreenshot,
                                      JavascriptExecutor {

    private final WebDriver internal;
    private final Waiter waiter;
    private final DriverUtilities utilities;

    public WrappedDriver(WebDriver driver, DriverTimeouts timeouts) {
        this.internal = driver;
        this.waiter = new Waiter(this, timeouts);
        this.utilities = new DriverUtilities(this);
    }

    /**
     * @return attached {@link Waiter} instance
     */
    public Waiter waiter() {
        return this.waiter;
    }

    /**
     * @return attached {@link DriverUtilities} instance
     */
    public DriverUtilities utilities() {
        return this.utilities;
    }

    @Override
    public void get(String url) {
        internal.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return internal.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return internal.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return internal.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return internal.findElement(by);
    }

    @Override
    public String getPageSource() {
        return internal.getPageSource();
    }

    @Override
    public void close() {
        internal.close();
    }

    @Override
    public void quit() {
        internal.quit();
    }

    @Override
    public LinkedHashSet<String> getWindowHandles() {
        return (LinkedHashSet<String>) internal.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return internal.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return internal.switchTo();
    }

    @Override
    public Navigation navigate() {
        return internal.navigate();
    }

    @Override
    public Options manage() {
        return internal.manage();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return ((TakesScreenshot) internal).getScreenshotAs(target);
    }

    @Override
    public Object executeScript(String script, Object... args) {
        try {
            return ((JavascriptExecutor) internal).executeScript(script, args);
        } catch (WebDriverException e) {
            throw new RuntimeException(
                    "Error executing JavaScript:\n%s\n%s".formatted(script, Arrays.toString(args)), e);
        }
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        try {
            return ((JavascriptExecutor) internal).executeAsyncScript(script, args);
        } catch (WebDriverException e) {
            throw new RuntimeException(
                    "Error executing Async JavaScript:\n%s\n%s".formatted(script, Arrays.toString(args)), e);
        }
    }

    @Override
    public WebDriver getWrappedDriver() {
        return this.internal;
    }

    public LocalStorage localStorage() {
        return ((WebStorage) internal).getLocalStorage();
    }

    public SessionStorage sessionStorage() {
        return ((WebStorage) internal).getSessionStorage();
    }

}
