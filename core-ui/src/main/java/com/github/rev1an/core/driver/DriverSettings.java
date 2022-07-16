/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

/**
 * Encapsulated parameters used for creating new {@link org.openqa.selenium.WebDriver} class.
 *
 * @param hubUrl      a link to {@code Selenium Hub}
 *                    <p>
 *                    or {@code Selenoid}
 *                    <p>
 *                    or {@code Selenium Hub}
 *                    <p>
 *                    or {@code Standalone Browser Container}
 *                    <p>
 *                    or already started  <em>driver executable</em>
 * @param driverClass full class reference, i.e. {@code org.openqa.selenium.chrome.ChromeDriver} or
 *                    {@code org.openqa.selenium.firefox.FirefoxDriver}
 * @param timeouts    container with Selenium timeouts settings
 * @author rev1an (Sergey Alekseev)
 * @see DriverSettings
 */
public record DriverSettings(String hubUrl,
                             String driverClass,
                             DriverTimeouts timeouts) {

}
