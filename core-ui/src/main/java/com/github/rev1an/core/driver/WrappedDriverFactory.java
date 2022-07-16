/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import com.github.rev1an.core.driver.builder.LocalDriverBuilder;
import com.github.rev1an.core.driver.builder.RemoteDriverBuilder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * Entry point for creating new {@link WrappedDriver} instance.
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class WrappedDriverFactory {

    private final Capabilities capabilities;
    private final DriverSettings settings;
    private final DriverBuilder<WebDriver> builder;

    /**
     * {@link Capabilities} and {@link DriverSettings} must be initialized before creating a new
     * {@link WrappedDriverFactory}.
     *
     * @param capabilities desired browser settings
     * @param settings     fully initialized driver settings
     */
    public WrappedDriverFactory(Capabilities capabilities, DriverSettings settings) {
        this.capabilities = capabilities;
        this.settings = settings;
        this.builder = resolveBuilder();
    }

    /**
     * Create a new {@link WrappedDriver}.
     *
     * @return new {@link WebDriver} instance wrapped in {@link WrappedDriver}
     */
    public WrappedDriver build() {
        final var driver = builder.build();
        driver.manage()
              .timeouts()
              .implicitlyWait(Duration.ZERO)
              .pageLoadTimeout(settings.timeouts().pageLoadTimeout());
        return new WrappedDriver(driver, settings.timeouts());
    }

    /**
     * Usually, passing {@code hub url} property to {@link org.openqa.selenium.remote.RemoteWebDriver} means that
     * connection to
     * <ul>
     *     <li>Selenium Hub / Selenoid / Standalone Container is required,</li>
     *     <li>already running 'chromedriver', 'geckodriver' service is required.</li>
     * </ul>
     * In this case, the process to create a new {@link WebDriver} is quite simple - parse {@link java.net.URL},
     * and pass it to constructor in addition to capabilities.
     * <p>
     * Absence of {@code hub url} property means that a <em>local</em> driver service is required.
     * In this case, after introduction of <em>Selenium 4.3.0</em>, a bit more sophisticated approach is required.
     *
     * @return implementation of {@link DriverBuilder}
     * @see DriverBuilder
     * @see RemoteDriverBuilder
     * @see LocalDriverBuilder
     */
    private DriverBuilder<WebDriver> resolveBuilder() {
        final var hubUrl = settings.hubUrl();
        return (hubUrl != null && !hubUrl.isBlank()) ?
               new RemoteDriverBuilder(capabilities, hubUrl) :
               new LocalDriverBuilder(capabilities, settings.driverClass());
    }

}
