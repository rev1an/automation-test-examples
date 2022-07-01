/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
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
        this.internal.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return this.internal.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return this.internal.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return this.internal.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return this.internal.findElement(by);
    }

    @Override
    public String getPageSource() {
        return this.internal.getPageSource();
    }

    @Override
    public void close() {
        this.internal.close();
    }

    @Override
    public void quit() {
        this.internal.quit();
    }

    @Override
    public LinkedHashSet<String> getWindowHandles() {
        return (LinkedHashSet<String>) this.internal.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return this.internal.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return this.internal.switchTo();
    }

    @Override
    public Navigation navigate() {
        return this.internal.navigate();
    }

    @Override
    public Options manage() {
        return this.internal.manage();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return ((TakesScreenshot) this.internal).getScreenshotAs(target);
    }

    @Override
    public Object executeScript(String script, Object... args) {
        try {
            return ((JavascriptExecutor) this.internal).executeScript(script, args);
        } catch (WebDriverException e) {
            throw new RuntimeException("Error executing JavaScript:\n%s\n%s".formatted(script, Arrays.toString(args)), e);
        }
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        try {
            return ((JavascriptExecutor) this.internal).executeAsyncScript(script, args);
        } catch (WebDriverException e) {
            throw new RuntimeException("Error executing Async JavaScript:\n%s\n%s".formatted(script, Arrays.toString(args)), e);
        }
    }

    @Override
    public WebDriver getWrappedDriver() {
        return this.internal;
    }

    public LocalStorage localStorage() {
        return ((WebStorage) this.internal).getLocalStorage();
    }

    public SessionStorage sessionStorage() {
        return ((WebStorage) this.internal).getSessionStorage();
    }

}
