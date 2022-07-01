/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.github.rev1an.core.driver.DriverBuilder;
import com.github.rev1an.core.util.ReflectionUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

/**
 * Builds a new {@link WebDriver} instance using provided {@code driverClass} parameter.
 *
 * @author rev1an (Sergey Alekseev)
 */
public class LocalDriverBuilder implements DriverBuilder<WebDriver> {

    private final Capabilities capabilities;
    private final Constructor<? extends WebDriver> constructor;

    /**
     * @param capabilities desired browser settings
     * @param driverClass  full class reference of target {@link WebDriver} implementation
     */
    public LocalDriverBuilder(Capabilities capabilities, String driverClass) {
        this.capabilities = capabilities;
        // cache constructor, or fail fast - don't wait till #build() is called.
        this.constructor = resolveConstructor(capabilities, driverClass);
    }

    /**
     * @return new instance of {@link WebDriver} implementation
     */
    @Override
    public WebDriver build() {
        try {
            return constructor.newInstance(capabilities);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Cannot create instance of WebDriver: %s".formatted(constructor), e);
        }
    }

    /**
     * Cache constructor, no reason to do class and constructor lookup each time new driver is required.
     *
     * @param capabilities used to what constructor is required
     * @param driverClass  full class reference of target {@link WebDriver} implementation
     * @return constructor of {@code capabilities#getClass()} type
     * @throws RuntimeException if no constructor found
     */
    @SuppressWarnings("unchecked")
    private Constructor<? extends WebDriver> resolveConstructor(Capabilities capabilities, String driverClass) {
        final var clazz = (Class<? extends WebDriver>) ReflectionUtils.findClass(driverClass);
        try {
            return clazz.getConstructor(capabilities.getClass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find required constructor in class: %s".formatted(driverClass), e);
        }
    }

}
