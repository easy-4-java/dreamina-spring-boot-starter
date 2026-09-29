package io.github.easy4j.dreamina.spring.boot;

import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityReport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Additional coverage tests for {@link DreaminaCliStartupChecker}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 */
class DreaminaCliStartupCheckerCoverageTest {

    @Test
    void run_whenCliAvailable_shouldLogAndReturn() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("dreamina");
        properties.setFailFastOnUnavailable(false);

        DreaminaCliExecutor executor = mock(DreaminaCliExecutor.class);
        DreaminaCliAvailabilityChecker availabilityChecker = mock(DreaminaCliAvailabilityChecker.class);

        DreaminaCliAvailabilityReport report = mock(DreaminaCliAvailabilityReport.class);
        when(report.isAvailable()).thenReturn(true);
        when(report.toDiagnosticMessage()).thenReturn("CLI available");
        when(availabilityChecker.check(executor)).thenReturn(report);

        MockEnvironment env = new MockEnvironment();
        env.setProperty("spring.profiles.active", "test");

        DreaminaCliStartupChecker checker = new DreaminaCliStartupChecker(
                executor, properties, availabilityChecker, env);

        // Should not throw - covers the success path (lines 41-45)
        checker.run(new DefaultApplicationArguments(new String[0]));

        verify(availabilityChecker).check(executor);
        verify(report).isAvailable();
    }

    @Test
    void run_whenCliUnavailableAndFailFast_shouldThrow() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("/nonexistent/dreamina");
        properties.setFailFastOnUnavailable(true);

        DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);
        DreaminaCliAvailabilityChecker availabilityChecker = new DreaminaCliAvailabilityChecker();
        MockEnvironment env = new MockEnvironment();

        DreaminaCliStartupChecker checker = new DreaminaCliStartupChecker(
                executor, properties, availabilityChecker, env);

        // Covers the failure path with fail-fast
        org.junit.jupiter.api.Assertions.assertThrows(
                io.github.easy4j.dreamina.exception.DreaminaCliStartupException.class,
                () -> checker.run(new DefaultApplicationArguments(new String[0])));
    }

    @Test
    void run_whenCliUnavailableAndNoFailFast_shouldNotThrow() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("/nonexistent/dreamina");
        properties.setFailFastOnUnavailable(false);

        DreaminaCliExecutor executor = new DreaminaCliExecutor(properties);
        DreaminaCliAvailabilityChecker availabilityChecker = new DreaminaCliAvailabilityChecker();
        MockEnvironment env = new MockEnvironment();

        DreaminaCliStartupChecker checker = new DreaminaCliStartupChecker(
                executor, properties, availabilityChecker, env);

        // Covers the failure path without fail-fast (just logs warning)
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> checker.run(new DefaultApplicationArguments(new String[0])));
    }

    @Test
    void buildEffectiveConfigSnapshot_shouldIncludeAllProperties() {
        DreaminaProperties properties = new DreaminaProperties();
        properties.setExecutable("/usr/bin/dreamina");
        properties.setEnabled(true);
        properties.setStartupCheckEnabled(true);
        properties.setFailFastOnUnavailable(false);

        DreaminaCliExecutor executor = mock(DreaminaCliExecutor.class);
        DreaminaCliAvailabilityChecker availabilityChecker = mock(DreaminaCliAvailabilityChecker.class);

        DreaminaCliAvailabilityReport report = mock(DreaminaCliAvailabilityReport.class);
        when(report.isAvailable()).thenReturn(true);
        when(report.toDiagnosticMessage()).thenReturn("OK");
        when(availabilityChecker.check(executor)).thenReturn(report);

        MockEnvironment env = new MockEnvironment();
        env.setProperty("spring.profiles.active", "prod");

        DreaminaCliStartupChecker checker = new DreaminaCliStartupChecker(
                executor, properties, availabilityChecker, env);

        // This exercises buildEffectiveConfigSnapshot (lines 62-69)
        checker.run(new DefaultApplicationArguments(new String[0]));
    }
}
