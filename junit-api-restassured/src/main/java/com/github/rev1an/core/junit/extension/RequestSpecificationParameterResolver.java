/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.extension;

import com.github.rev1an.core.junit.annotation.HttpConfig;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.ConfigurationParameters;

/**
 * Allows to inject {@link RequestSpecification} as dependency into {@code constructor} or {@code test method}.
 */
public class RequestSpecificationParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
                                                                                                           ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(RequestSpecification.class);
    }

    @Override
    public RequestSpecification resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
                                                                                                                       ParameterResolutionException {
        var httpConfig = findConfigAnnotation(extensionContext);
        if (httpConfig != null) {
            return RestAssured.given()
                              .baseUri(httpConfig.basePath());
        } else {
            var basePath = extensionContext.getConfigurationParameter(Config.BASE_URI)
                                           .orElseThrow(() -> new RuntimeException("""
                                                                                   Required property [%s] not found!
                                                                                   1. Provide base path using annotation %s on a) class or b) test level
                                                                                   2. Create a %s file and set it there""".formatted(
                                                   Config.BASE_URI,
                                                   HttpConfig.class,
                                                   ConfigurationParameters.CONFIG_FILE_NAME)));
            return RestAssured.given()
                              .baseUri(basePath);
        }
    }

    private HttpConfig findConfigAnnotation(ExtensionContext extensionContext) {
        return AnnotationSupport.findAnnotation(extensionContext.getTestMethod(), HttpConfig.class)
                                .or(() -> AnnotationSupport.findAnnotation(extensionContext.getTestClass(), HttpConfig.class))
                                .orElse(null);
    }

    public static final class Config {

        public static final String BASE_URI = "restassured.base.uri";

    }

}
