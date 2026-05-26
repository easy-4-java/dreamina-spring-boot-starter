package io.github.hiwepy.dreamina.spring.boot;

import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import io.github.hiwepy.dreamina.exception.DreaminaCliStartupException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link DreaminaCliStartupChecker} 行为测试。
 */
class DreaminaCliStartupCheckerTest {

    @Test
    void shouldFailFastWhenCliUnavailable() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("/nonexistent/dreamina-startup-test");
        properties.setFailFastOnUnavailable(true);
        DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);
        DreaminaCliStartupChecker startupChecker = new DreaminaCliStartupChecker(
                executor, properties, new DreaminaCliAvailabilityChecker(), new MockEnvironment());

        assertThrows(DreaminaCliStartupException.class,
                () -> startupChecker.run(new DefaultApplicationArguments(new String[0])));
    }

    @Test
    void shouldWarnOnlyWhenFailFastDisabled() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("/nonexistent/dreamina-startup-test");
        properties.setFailFastOnUnavailable(false);
        DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);
        DreaminaCliStartupChecker startupChecker = new DreaminaCliStartupChecker(
                executor, properties, new DreaminaCliAvailabilityChecker(), new MockEnvironment());

        assertDoesNotThrow(() -> startupChecker.run(new DefaultApplicationArguments(new String[0])));
    }
}
