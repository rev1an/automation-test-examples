/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.extension;

import java.util.Optional;
import com.github.rev1an.core.driver.WrappedDriver;
import com.github.rev1an.core.driver.WrappedDriverFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import static com.github.rev1an.core.junit.extension.WrappedDriverFactoryExtension.NAMESPACE;

/**
 * Handle parameter resolution for {@link WrappedDriver}.
 * Each test requires a new, clean browser.
 *
 * @author rev1an (Sergey Alekseev)
 * @see ParameterResolver
 */
public class WrappedDriverParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return WrappedDriver.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public WrappedDriver resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        final var store = extensionContext.getRoot().getStore(NAMESPACE);
        return Optional.ofNullable(store.get(WrappedDriverFactory.class, WrappedDriverFactory.class))
                       .map(factory -> store.getOrComputeIfAbsent(extensionContext.getRequiredTestClass(),
                                                                  ignore -> factory.build(), WrappedDriver.class))
                       .orElseThrow(() -> new IllegalStateException(("No %1$s is found in root extension context. " +
                                                                     "Looks like %1$s is missing.")
                                                                            .formatted(WrappedDriverFactory.class)));
    }

}
