/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.capabilities.deserializer;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.List;
import java.util.Map;

/**
 * Support initialization and property setting for {@link FirefoxOptions}.
 *
 * @author rev1an (Sergey Alekseev)
 * @see AbstractDriverOptionsDeserializer
 */
@SuppressWarnings("unchecked")
public class FirefoxOptionsDeserializer extends AbstractDriverOptionsDeserializer<FirefoxOptions> {

    @Override
    protected void setCapability(FirefoxOptions capabilities, String key, Object value) {
        switch (key) {
            case CapabilityType.PAGE_LOAD_STRATEGY ->
                    capabilities.setPageLoadStrategy(PageLoadStrategy.fromString(value.toString()));
            case "args" -> capabilities.addArguments((List<String>) value);
            case "binary" -> capabilities.setBinary(value.toString());
            case "log" -> capabilities.setLogLevel(FirefoxDriverLogLevel.fromString(value.toString()));
            case "prefs" -> ((Map<String, Object>) value).forEach(capabilities::addPreference);
            default -> capabilities.setCapability(key, value);
        }
    }

}
