/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.capabilities;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.github.rev1an.core.driver.CapabilityProvider;
import com.github.rev1an.core.driver.capabilities.deserializer.ChromeOptionsDeserializer;
import com.github.rev1an.core.driver.capabilities.deserializer.FirefoxOptionsDeserializer;
import com.github.rev1an.core.json.JacksonHolder;
import com.github.rev1an.core.json.JacksonMapper;
import com.github.rev1an.core.util.ReflectionUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

/**
 * Parse {@code .json} file into instance of {@link AbstractDriverOptions} ({@code ***Options}) class.
 * <p>
 * The first key in {@code .json} file must be a <b>full class reference</b> of desired capability class.
 * <p>
 * <h3>Examples</h3>
 * {@link org.openqa.selenium.firefox.FirefoxOptions}:
 * <pre>
 * {
 *   "org.openqa.selenium.firefox.FirefoxOptions": {
 *     "pageLoadStrategy": "eager",
 *     "moz:firefoxOptions": {
 *       "binary": "/Applications/Firefox Developer Edition.app/Contents/MacOS/firefox",
 *       "log": "info",
 *       "args": [
 *         "--window-size=1600,900"
 *       ]
 *     }
 *   }
 * }
 * </pre>
 * {@link org.openqa.selenium.chrome.ChromeOptions}:
 * <pre>
 * {
 *   "org.openqa.selenium.chrome.ChromeOptions": {
 *     "pageLoadStrategy": "eager",
 *     "goog:chromeOptions": {
 *       "args": [
 *         "window-size=1600,900",
 *         "enable-automation"
 *       ]
 *     }
 *   }
 * }
 * </pre>
 *
 * @author rev1an (Sergey Alekseev)
 * @since <em>Selenium 4.3.0</em> we need to pass a <em>type hit</em> into capability file and
 * apply additional deserialization logic to {@code ***Options} classes, which
 * greatly complicates the implementation.
 */
public class JsonFileCapabilityProvider implements CapabilityProvider<AbstractDriverOptions<?>> {

    private final String filePath;
    private final JacksonMapper mapper;

    /**
     * @param filePath {@code .json} file in <b>resources</b> folder.
     */
    public JsonFileCapabilityProvider(String filePath) {
        this.filePath = filePath; //TODO: Support 'filePath' outside of resources folder. Use 'Path'?
        this.mapper = JacksonHolder.DEFAULT.copy();
        this.mapper.registerModule(new JsonCapabilitiesModule());
    }

    /**
     * @return new instance of {@link Capabilities} class
     * @since <em>Selenium 4.3.0</em>, additional magic should be applied.
     * Previously, it was enough to deserialize {@code .json} file into simple {@link Map}  instance,
     * and pass it to {@link MutableCapabilities#MutableCapabilities(Map)} constructor.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractDriverOptions<?> get() {
        try {
            var is = ClassLoader.getSystemResourceAsStream(filePath);
            var raw = mapper.readTree(is);
            var firstNode = raw.fields().next();
            var capabilityClass = (Class<AbstractDriverOptions<?>>) ReflectionUtils.findClass(firstNode.getKey());
            return mapper.convertValue(firstNode.getValue(), capabilityClass);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read capabilities file: %s".formatted(filePath), e);
        }
    }

    /**
     * Simple Jackson {@link Module} that registers deserializers for capability classes.
     *
     * @see Module
     * @see SimpleDeserializers
     * @since <em>Selenium 4.3.0</em> each {@code ***Options} class requires a respective approach
     * to initializing capabilities.
     */
    private static class JsonCapabilitiesModule extends Module {

        @Override
        public String getModuleName() {
            return "Selenium Capabilities Support";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(SetupContext context) {
            final var deserializers = new SimpleDeserializers();
            // more deserializers must be registered here
            deserializers.addDeserializer(FirefoxOptions.class, new FirefoxOptionsDeserializer());
            deserializers.addDeserializer(ChromeOptions.class, new ChromeOptionsDeserializer());
            context.addDeserializers(deserializers);
        }

    }

}
