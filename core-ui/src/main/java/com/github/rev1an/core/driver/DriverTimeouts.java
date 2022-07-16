/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import java.time.Duration;

/**
 * Encapsulated parameters used to tune timeout settings in attached {@link Waiter} instances.
 *
 * @param pageLoadTimeout  to be set into {@link org.openqa.selenium.WebDriver.Timeouts#pageLoadTimeout(Duration)}
 * @param timeout          'The timeout in seconds when an expectation is called.'
 * @param pollingFrequency 'The duration in milliseconds to sleep between polls.'
 * @author rev1an (Sergey Alekseev)
 * @see Waiter
 * @see org.openqa.selenium.support.ui.WebDriverWait#WebDriverWait(org.openqa.selenium.WebDriver, Duration, Duration)
 * @see org.openqa.selenium.support.ui.FluentWait
 */
public record DriverTimeouts(Duration pageLoadTimeout,
                             Duration timeout,
                             Duration pollingFrequency) {

}
