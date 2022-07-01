/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.junit.extension;

import java.util.Optional;
import com.github.rev1an.core.driver.WrappedDriver;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * Close {@link WrappedDriver} instance after test has been finished and all hooks
 * has been executed.
 *
 * @author rev1an (Sergey Alekseev)
 */
public class CloseDriverTestWatcher implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        shutdown(context);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        shutdown(context);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        shutdown(context);
    }

    /**
     * Close {@link WrappedDriver} if it's present in current {@link ExtensionContext}.
     *
     * @param context the current extension context; never {@code null} (from JUnit docs)
     */
    protected void shutdown(ExtensionContext context) {
        driver(context).ifPresent(WrappedDriver::quit);
    }

    /**
     * Find {@link WrappedDriver} in current {@link ExtensionContext}.
     *
     * @param context the current extension context; never {@code null} (from JUnit docs)
     * @return {@code Optional} that contains {@link WrappedDriver} instance,
     * never {@code null} but potentially empty
     */
    protected Optional<WrappedDriver> driver(ExtensionContext context) {
        return Optional.ofNullable(context.getRoot()
                                          .getStore(WrappedDriverFactoryExtension.NAMESPACE)
                                          .remove(context.getRequiredTestClass(), WrappedDriver.class));
    }

}
