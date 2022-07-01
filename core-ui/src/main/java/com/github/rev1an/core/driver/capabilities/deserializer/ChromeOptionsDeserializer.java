/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.capabilities.deserializer;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

/**
 * Support initialization and property setting for {@link ChromeOptions}.
 *
 * @author rev1an (Sergey Alekseev)
 * @see AbstractDriverOptionsDeserializer
 */
@SuppressWarnings("unchecked")
public class ChromeOptionsDeserializer extends AbstractDriverOptionsDeserializer<ChromeOptions> {

    @Override
    protected void setCapability(ChromeOptions capabilities, String key, Object value) {
        switch (key) {
            case CapabilityType.PAGE_LOAD_STRATEGY ->
                    capabilities.setPageLoadStrategy(PageLoadStrategy.fromString(value.toString()));
            case "args" -> capabilities.addArguments((List<String>) value);
            case "binary" -> capabilities.setBinary(value.toString());
            case "extensions" -> capabilities.addEncodedExtensions((List<String>) value);
            case "experimentalOptions" -> ((Map<String, Object>) value).forEach(capabilities::setExperimentalOption);
            default -> capabilities.setCapability(key, value);
        }
    }

}
