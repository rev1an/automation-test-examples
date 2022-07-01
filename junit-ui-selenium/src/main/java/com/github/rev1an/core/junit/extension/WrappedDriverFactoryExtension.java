/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.extension;

import java.time.Duration;
import java.util.List;
import com.github.rev1an.core.driver.DriverSettings;
import com.github.rev1an.core.driver.DriverTimeouts;
import com.github.rev1an.core.driver.WrappedDriver;
import com.github.rev1an.core.driver.WrappedDriverFactory;
import com.github.rev1an.core.driver.capabilities.JsonFileCapabilityProvider;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.openqa.selenium.Capabilities;

/**
 * JUnit 5 {@link BeforeAllCallback} extensions that handles setup of {@link WrappedDriverFactory}
 * once per test run.
 *
 * @author rev1an (Sergey Alekseev)
 * @see BeforeAllCallback
 * @see org.junit.jupiter.api.extension.Extension
 */
public class WrappedDriverFactoryExtension implements BeforeAllCallback {

    public static final Namespace NAMESPACE = Namespace.create(WrappedDriver.class);

    /**
     * <ul>
     *     <li>resolve {@link com.github.rev1an.core.driver.CapabilityProvider} and read desired {@link Capabilities}</li>
     *     <li>read {@link DriverSettings} from JUnit {@link org.junit.platform.engine.ConfigurationParameters}</li>
     *     <li>move {@code webdriver.***.driver} settings from {@link org.junit.platform.engine.ConfigurationParameters}
     *     to {@code system properties}</li>
     *     <li>store {@link WrappedDriverFactory} in <b>root</b> {@link ExtensionContext}</li>
     * </ul>
     *
     * @see BeforeAllCallback#beforeAll(ExtensionContext) docs.
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        final var capabilities = resolveCapabilities(context);
        final var settings = readSettings(context);
        setBinaries(context);
        final var factory = new WrappedDriverFactory(capabilities, settings);
        context.getRoot()
               .getStore(NAMESPACE)
               .put(WrappedDriverFactory.class, factory);
    }

    private void setBinaries(ExtensionContext context) {
        List.of("webdriver.chrome.driver", "webdriver.gecko.driver")
            .forEach(key -> context.getConfigurationParameter(key)
                                   .ifPresent(path -> System.setProperty(key, path)));
    }

    private DriverSettings readSettings(ExtensionContext context) {
        final var hubUrl = context.getConfigurationParameter("selenium.hub.url")
                                  .orElse(null);
        final var driverClass = context.getConfigurationParameter("selenium.driver.class")
                                       .orElse(null);
        final var timeout = context.getConfigurationParameter("selenium.waiter.timeout")
                                   .map(Duration::parse)
                                   .orElse(Duration.ofSeconds(10));
        final var pollingFrequency = context.getConfigurationParameter("selenium.waiter.frequency")
                                            .map(Duration::parse)
                                            .orElse(Duration.ofMillis(200));
        final var pageLoadTimeout = Duration.ofSeconds(30);
        return new DriverSettings(hubUrl, driverClass, new DriverTimeouts(pageLoadTimeout, timeout, pollingFrequency));
    }

    private Capabilities resolveCapabilities(ExtensionContext context) {
        final var provider = context.getConfigurationParameter("selenium.capabilities.file")
                                    .map(filePath -> {
                                        if (filePath.endsWith(".json")) {
                                            return new JsonFileCapabilityProvider(filePath);
                                        } else {
                                            throw new RuntimeException(
                                                    "Only .json files are allowed as capabilities at the moment.");
                                        }
                                    })
                                    .orElseThrow(() -> new IllegalArgumentException("Required property is missing"));
        return provider.get();
    }

}
