/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Shortcuts for {@link WrappedDriver} actions.
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class DriverUtilities {

    private final WrappedDriver driver;

    /**
     * Instance of this class always attached to one concrete {@link WrappedDriver}.
     *
     * @param driver fully initialized {@link WrappedDriver} instance.
     */
    public DriverUtilities(WrappedDriver driver) {
        this.driver = driver;
    }

    public void jsClick(WebElement element) {
        driver.executeScript("arguments[0].click();", element);
    }

    public void highlight(WebElement element) {
        driver.executeScript("arguments[0].setAttribute('style', 'border: 2px dotted red;');", element);
    }

    public Boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public Boolean isElementPresent(By locator) {
        try {
            return driver.findElement(locator) != null;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /*
     * Scrolls
     * */

    public void scrollIntoView(WebElement element) {
        driver.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /*
     * Actions
     * */

    public void mouseOver(WebElement element) {
        newAction().moveToElement(element).build().perform();
    }

    public void mouseOverAndClick(WebElement element) {
        newAction().moveToElement(element).click(element).build().perform();
    }

    public Actions newAction() {
        return new Actions(driver);
    }

}
