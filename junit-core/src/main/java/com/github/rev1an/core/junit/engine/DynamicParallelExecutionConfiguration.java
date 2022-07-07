package com.github.rev1an.core.junit.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.DefaultParallelExecutionConfigurationStrategy;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

/**
 * A simple extension for JUnit default {@link DefaultParallelExecutionConfigurationStrategy#DYNAMIC} with only aim
 * is to log parallelism settings.
 * <p>
 * To enable, pass properties:
 * <pre>
 * junit.jupiter.execution.parallel.config.strategy=custom
 * junit.jupiter.execution.parallel.config.custom.class=com.github.rev1an.core.junit.engine.DynamicParallelExecutionConfiguration
 * </pre>
 *
 * @see ParallelExecutionConfigurationStrategy
 */
public class DynamicParallelExecutionConfiguration implements ParallelExecutionConfigurationStrategy {

    private final Logger log = LogManager.getLogger(getClass());

    @Override
    public ParallelExecutionConfiguration createConfiguration(ConfigurationParameters configurationParameters) {
        final var configuration = DefaultParallelExecutionConfigurationStrategy.DYNAMIC.createConfiguration(configurationParameters);
        log.info("\n\tParallelism: " + configuration.getParallelism() +
                 "\n\tMinimum Runnable: " + configuration.getMinimumRunnable() +
                 "\n\tMax Pool Size: " + configuration.getMaxPoolSize() +
                 "\n\tCore Pool Size: " + configuration.getCorePoolSize() +
                 "\n\tKeep Alive Seconds: " + configuration.getKeepAliveSeconds());
        return configuration;
    }

}
