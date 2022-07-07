package com.github.rev1an.core.junit.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * Remove unnecessary JUnit noise in stacktrace, thus making it more readable.
 *
 * @see org.junit.jupiter.api.extension.TestExecutionExceptionHandler
 * @see org.junit.jupiter.api.extension.LifecycleMethodExecutionExceptionHandler
 */
public final class TrimmingExceptionHandler implements TestExecutionExceptionHandler,
                                                       LifecycleMethodExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleExceptionForClass(context, throwable);
    }

    @Override
    public void handleBeforeAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleExceptionForClass(context, throwable);
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleExceptionForClass(context, throwable);
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleExceptionForClass(context, throwable);
    }

    @Override
    public void handleAfterAllMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleExceptionForClass(context, throwable);
    }

    private void handleExceptionForClass(ExtensionContext context, Throwable throwable) throws Throwable {
        context.getTestClass().ifPresent(testClass -> {
            final var cause = throwable.getCause();
            if (cause != null) {
                trim(testClass, cause); // trim cause too
            }
            trim(testClass, throwable);
        });

        throw throwable;
    }

    private void trim(Class<?> testClass, Throwable throwable) {
        final var stackTrace = throwable.getStackTrace();
        int index = 0;
        for (; index < stackTrace.length; index++) {
            if (stackTrace[index].getClassName().equals(testClass.getName())) {
                index += 1;
                break;
            }
        }
        final var newTrace = new StackTraceElement[index];
        System.arraycopy(stackTrace, 0, newTrace, 0, index);
        throwable.setStackTrace(newTrace);
    }

}
