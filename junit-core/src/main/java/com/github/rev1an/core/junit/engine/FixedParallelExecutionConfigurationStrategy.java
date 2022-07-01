/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.engine;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.DefaultParallelExecutionConfigurationStrategy;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * A simple extension for JUnit default {@link DefaultParallelExecutionConfigurationStrategy#FIXED} with only aim
 * is to log parallelism settings.
 * <p>
 * To enable, pass properties:
 * <pre>
 * junit.jupiter.execution.parallel.config.strategy=custom
 * junit.jupiter.execution.parallel.config.custom.class=com.github.rev1an.core.junit.engine.FixedParallelExecutionConfigurationStrategy
 * </pre>
 *
 * @author rev1an (Sergey Alekseev)
 * @see ParallelExecutionConfigurationStrategy
 * @see DefaultParallelExecutionConfigurationStrategy#FIXED
 */
public class FixedParallelExecutionConfigurationStrategy extends LoggedParallelExecutionConfigurationStrategy {

    @Override
    public ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        return log(DefaultParallelExecutionConfigurationStrategy.FIXED.createConfiguration(configurationParameters));
    }

}
