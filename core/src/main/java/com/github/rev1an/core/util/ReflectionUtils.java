/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.util;

/**
 * Reflection magic
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Class<?> findClass(String driverClass) {
        try {
            return Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class with name: %s".formatted(driverClass), e);
        }
    }

}
