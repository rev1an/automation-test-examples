/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Collection of {@link  WebDriverWait} shortcuts, gathered in one place for usability purposes.
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class Waiter {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_POLLING_F = Duration.ofMillis(250);

    private final WrappedDriver driver;
    private final Duration timeout;
    private final Duration pollingFrequency;

    /**
     * Instance of this class always attached to one concrete {@link WrappedDriver}.
     *
     * @param driver   fully initialized {@link WrappedDriver} instance.
     * @param timeouts encapsulated timeout settings.
     */
    public Waiter(WrappedDriver driver, DriverTimeouts timeouts) {
        this.driver = driver;
        this.timeout = timeouts.timeout().isZero() ? DEFAULT_TIMEOUT : timeouts.timeout();
        this.pollingFrequency = timeouts.pollingFrequency().isZero() ? DEFAULT_POLLING_F : timeouts.pollingFrequency();
    }

    /**
     * @return timeout set for this {@link Waiter}
     */
    public Duration timeout() {
        return this.timeout;
    }

    /**
     * Create new {@link WebDriverWait} with preconfigured {@code pollingFrequency}
     *
     * @param timeout desired timeout
     * @return new {@link WebDriverWait} with provided {@code timeout}
     */
    public WebDriverWait newWait(Duration timeout) {
        return new WebDriverWait(this.driver, timeout, this.pollingFrequency);
    }

    public WebElement forElementPresenceBy(By locator) {
        return forElementPresenceBy(timeout, locator);
    }

    public WebElement forElementPresenceBy(Duration timeout, By locator) {
        return newWait(timeout).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public Boolean forElementAbsenceBy(By locator) {
        return forElementAbsenceBy(timeout, locator);
    }

    public Boolean forElementAbsenceBy(Duration timeout, By locator) {
        return newWait(timeout).until(new Function<>() {
            public Boolean apply(WebDriver driver) {
                try {
                    driver.findElement(locator).isDisplayed();
                    return false;
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return true;
                }
            }

            public String toString() {
                return "absence of element located by: " + locator;
            }
        });
    }

    public WebElement forElementVisibleBy(By locator) {
        return forElementVisibleBy(timeout, locator);
    }

    public WebElement forElementVisibleBy(Duration timeout, By locator) {
        return newWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public Boolean forElementInvisibleBy(By locator) {
        return forElementInvisibleBy(timeout, locator);
    }

    public Boolean forElementInvisibleBy(Duration timeout, By locator) {
        return newWait(timeout).until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement forElementVisible(WebElement element) {
        return forElementVisible(timeout, element);
    }

    public WebElement forElementVisible(Duration timeout, WebElement element) {
        return newWait(timeout).until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement forElementToBeClickableBy(By locator) {
        return newWait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement forElementToBeClickableBy(Duration timeout, By locator) {
        return newWait(timeout).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement forElementSafelyClicked(By locator) {
        return forElementSafelyClicked(timeout, locator);
    }

    public WebElement forElementSafelyClicked(Duration timeout, By locator) {
        return newWait(timeout).until(new Function<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    final var btn = driver.findElement(locator);
                    btn.click();
                    return btn;
                } catch (ElementNotInteractableException | StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "element safely clicked: " + locator;
            }
        });
    }

    public WebElement forElementToContainText(WebElement element) {
        return forElementToContainText(timeout, element);
    }

    public WebElement forElementToContainText(Duration timeout, WebElement element) {
        return newWait(timeout).until(new Function<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    String text = element.getText();
                    if (text.isEmpty()) {
                        return null;
                    } else {
                        return element;
                    }
                } catch (StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "any text to be present in element: " + element;
            }
        });
    }

    public void ticks(long ticks) {
        this.ticks(this.timeout, ticks);
    }

    public void ticks(Duration timeout, long ticks) {
        newWait(timeout).until(new Function<>() {

            int current = 0;

            @Override
            public Boolean apply(WebDriver driver) {
                if (current <= ticks) {
                    current++;
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public String toString() {
                return "numbers of poll ticks: " + ticks; // only when sum of polling ticks exceed timeout
            }
        });
    }

    public Boolean waitForLoad() {
        return documentReady() & jQueryToFinish();
    }

    public Boolean documentReady() {
        return documentReady(timeout);
    }

    public Boolean documentReady(Duration timeout) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }

            @Override
            public String toString() {
                return "document.readyState == complete";
            }
        });
    }

    public Boolean jQueryToFinish() {
        return jQueryToFinish(timeout);
    }

    public Boolean jQueryToFinish(Duration timeout) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return (Boolean) ((JavascriptExecutor) driver).executeScript(
                            "return !!window.jQuery && window.jQuery.active == 0");
                } catch (TimeoutException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "jQuery to finish";
            }
        });
    }

    public Boolean forAttributeToContain(WebElement element, String attribute, String value) {
        return forAttributeToContain(timeout, element, attribute, value);
    }

    public Boolean forAttributeToContain(Duration timeout, WebElement element, String attribute, String value) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return element.getAttribute(attribute).contains(value);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "attribute '%s' to contain '%s'".formatted(attribute, value);
            }
        });
    }

    public Boolean forAttributeToNotContain(WebElement element, String attribute, String value) {
        return forAttributeToNotContain(timeout, element, attribute, value);
    }

    public Boolean forAttributeToNotContain(Duration timeout, WebElement element, String attribute, String value) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return !element.getAttribute(attribute).contains(value);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "attribute '%s' to NOT contain '%s'".formatted(attribute, value);
            }
        });
    }

    public Boolean forAttributeToDissapear(Duration timeout, WebElement element, String attribute) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return element.getAttribute(attribute) == null;
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "attribute '%s' to disappear".formatted(attribute);
            }
        });
    }

    public Boolean forAttributeToChange(WebElement element, String targetAttribute) {
        return forAttributeToChange(timeout, element, targetAttribute);
    }

    public Boolean forAttributeToChange(Duration timeout, WebElement element, String targetAttribute) {
        return newWait(timeout).until(new Function<>() {

            private String attribute = null;

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    if (attribute == null) {
                        attribute = element.getAttribute(targetAttribute);
                    }
                    return !element.getAttribute(targetAttribute).equals(attribute);
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "attribute '%s' to change it's value '%s'".formatted(targetAttribute, attribute);
            }
        });
    }

    public List<WebElement> numberElementsMoreThanOrEqual(By locator, int minimum) {
        return numberElementsMoreThanOrEqual(timeout, locator, minimum);
    }

    public List<WebElement> numberElementsMoreThanOrEqual(Duration timeout, By locator, int minimum) {
        return newWait(timeout).until(new Function<>() {

            private Integer current = 0;

            @Override
            public List<WebElement> apply(WebDriver driver) {
                try {
                    final var elements = driver.findElements(locator);
                    this.current = elements.size();
                    return (this.current >= minimum) ? elements : null;
                } catch (NoSuchElementException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "number of elements to be equal or more than %s. Last size: %s".formatted(minimum, current);
            }
        });
    }

    /*
     * Wait for Child elements
     * */

    public WebElement forChildElementPresenceBy(WebElement root, By locator) {
        return forChildElementPresenceBy(timeout, root, locator);
    }

    public WebElement forChildElementPresenceBy(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    return root.findElement(locator);
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "presence of Child Element '%s' from root: %s".formatted(locator, root);
            }
        });
    }

    public Boolean forChildElementDisappearBy(WebElement root, By locator) {
        return forChildElementDisappearBy(timeout, root, locator);
    }

    public Boolean forChildElementDisappearBy(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return !root.findElement(locator).isDisplayed();
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return true;
                }
            }

            @Override
            public String toString() {
                return "Child Element '%s' to disappear from root: %s".formatted(locator, root);
            }
        });
    }

    public WebElement forChildElementVisibleBy(WebElement root, By locator) {
        return forChildElementVisibleBy(timeout, root, locator);
    }

    public WebElement forChildElementVisibleBy(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    return isVisible(root.findElement(locator));
                } catch (StaleElementReferenceException | NoSuchElementException e) {
                    return null;
                }
            }

            private WebElement isVisible(WebElement element) {
                return element.isDisplayed() ? element : null;
            }

            @Override
            public String toString() {
                return "visibility of Child Element '%s' from root: %s".formatted(locator, root);
            }
        });
    }

    public WebElement forChildElementSafelyClicked(WebElement root, By locator) {
        return forChildElementSafelyClicked(timeout, root, locator);
    }

    public WebElement forChildElementSafelyClicked(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {
            @Override
            public WebElement apply(WebDriver driver) {
                try {
                    final var btn = root.findElement(locator);
                    btn.click();
                    return btn;
                } catch (ElementNotInteractableException | StaleElementReferenceException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "Child Element safely clicked: " + locator;
            }
        });
    }

    public List<WebElement> numberChildElementsMoreThanOrEqual(WebElement root, By locator, int minimum) {
        return numberChildElementsMoreThanOrEqual(timeout, root, locator, minimum);
    }

    public List<WebElement> numberChildElementsMoreThanOrEqual(Duration timeout, WebElement root, By locator,
                                                               int minimum) {
        return newWait(timeout).until(new Function<>() {
            private Integer current = 0;

            @Override
            public List<WebElement> apply(WebDriver driver) {
                try {
                    final var elements = root.findElements(locator);
                    this.current = elements.size();
                    return (this.current >= minimum) ? elements : null;
                } catch (NoSuchElementException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "number of Child Elements to be equal or more than %s. Last size: %s".formatted(minimum,
                                                                                                       current);
            }
        });
    }

    public List<WebElement> allChildElementsVisibleBy(WebElement root, By locator) {
        return allChildElementsVisibleBy(timeout, root, locator);
    }

    public List<WebElement> allChildElementsVisibleBy(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public List<WebElement> apply(WebDriver driver) {
                try {
                    final var elements = root.findElements(locator);
                    if (elements.stream().allMatch(WebElement::isDisplayed)) {
                        return elements;
                    } else {
                        return null;
                    }
                } catch (NoSuchElementException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "all of Child Elements '%s' to be visible from root: %s".formatted(locator, root);
            }
        });
    }

    public List<WebElement> anyChildElementsVisibleBy(WebElement root, By locator) {
        return anyChildElementsVisibleBy(timeout, root, locator);
    }

    public List<WebElement> anyChildElementsVisibleBy(Duration timeout, WebElement root, By locator) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public List<WebElement> apply(WebDriver driver) {
                try {
                    final var elements = root.findElements(locator);
                    if (elements.stream().anyMatch(WebElement::isDisplayed)) {
                        return elements;
                    } else {
                        return null;
                    }
                } catch (NoSuchElementException | IndexOutOfBoundsException | NullPointerException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "any Child Elements '%s' to be visible from root: %s".formatted(locator, root);
            }
        });
    }

    public WebDriver foriFrameToBeAvailableAndSwitchToIt(By locator) {
        return newWait(timeout).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public Object javaScriptReturnValue(String script, Object... args) {
        return javaScriptReturnValue(timeout, script, args);
    }

    public Object javaScriptReturnValue(Duration timeout, String script, Object... args) {
        return newWait(timeout).until(new Function<>() {

            @Override
            public Object apply(WebDriver driver) {
                try {
                    Object value = ((JavascriptExecutor) driver).executeScript(script, args);

                    if (value instanceof List) {
                        return ((List<?>) value).isEmpty() ? null : value;
                    }
                    if (value instanceof String) {
                        return ((String) value).isEmpty() ? null : value;
                    }

                    return value;
                } catch (WebDriverException e) {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "Script\n%s\nto return value (not null)".formatted(script);
            }
        });
    }

}
