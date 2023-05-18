/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.extension;

import com.github.rev1an.core.json.JacksonMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Allows to inject {@link JacksonParameterResolver} as dependency into {@code constructor} or {@code test method}.
 */
public class JacksonParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
                                                                                                           ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(JacksonMapper.class);
    }

    @Override
    public JacksonMapper resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
                                                                                                                ParameterResolutionException {
        return new JacksonMapper();
    }

}
