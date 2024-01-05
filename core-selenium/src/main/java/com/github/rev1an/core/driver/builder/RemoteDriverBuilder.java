/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.builder;

import java.net.MalformedURLException;
import java.net.URL;
import com.github.rev1an.core.driver.DriverBuilder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Builds a new {@link RemoteWebDriver} instance.
 *
 * @author rev1an (Sergey Alekseev)
 */
public class RemoteDriverBuilder implements DriverBuilder<WebDriver> {

    private final Capabilities capabilities;
    private final URL hubUrl;

    public RemoteDriverBuilder(Capabilities capabilities, String hubUrl) {
        this.capabilities = capabilities;
        this.hubUrl = parseUrl(hubUrl);
    }

    @Override
    public RemoteWebDriver build() {
        return new RemoteWebDriver(hubUrl, capabilities, false);
    }

    private URL parseUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error parsing URL: %s".formatted(url), e);
        }
    }

}
