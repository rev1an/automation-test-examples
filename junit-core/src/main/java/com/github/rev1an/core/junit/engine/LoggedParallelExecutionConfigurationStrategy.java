/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.engine;

import org.apache.logging.log4j.LogManager;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * Simple implementation of {@link ParallelExecutionConfigurationStrategy} with only purpose is to
 * log detailed information of {@link ParallelExecutionConfiguration} configuration.
 *
 * @author rev1an (Sergey Alekseev)
 */
abstract class LoggedParallelExecutionConfigurationStrategy implements ParallelExecutionConfigurationStrategy {

    /**
     * Log {@link ParallelExecutionConfiguration}
     *
     * @param configuration initialized {@link ParallelExecutionConfiguration} config
     * @return same {@code configuration} that been provided as parameter
     */
    protected ParallelExecutionConfiguration log(final ParallelExecutionConfiguration configuration) {
        var log = LogManager.getLogger(getClass()); // called only once during engine configuration
        log.info("\n\tParallelism: " + configuration.getParallelism() +
                 "\n\tMinimum Runnable: " + configuration.getMinimumRunnable() +
                 "\n\tMax Pool Size: " + configuration.getMaxPoolSize() +
                 "\n\tCore Pool Size: " + configuration.getCorePoolSize() +
                 "\n\tKeep Alive Seconds: " + configuration.getKeepAliveSeconds());
        return configuration;
    }

}
