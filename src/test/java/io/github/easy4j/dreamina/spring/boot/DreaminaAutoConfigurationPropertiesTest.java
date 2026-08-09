package io.github.easy4j.dreamina.spring.boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Additional tests for {@link DreaminaAutoConfiguration} using ApplicationContextRunner.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 */
class DreaminaAutoConfigurationPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DreaminaAutoConfiguration.class));

    @Test
    void whenEnabled_shouldCreateBeans() {
        contextRunner
                .withPropertyValues(
                        "dreamina.cli.enabled=true",
                        "dreamina.cli.executable=dreamina",
                        "dreamina.cli.startup-check-enabled=false"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(DreaminaProperties.class);
                    assertThat(context).hasSingleBean(io.github.easy4j.dreamina.cli.DreaminaCliExecutor.class);
                    assertThat(context).hasSingleBean(io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker.class);
                });
    }

    @Test
    void whenStartupCheckEnabled_shouldCreateCheckerBean() {
        contextRunner
                .withPropertyValues(
                        "dreamina.cli.enabled=true",
                        "dreamina.cli.executable=dreamina",
                        "dreamina.cli.startup-check-enabled=true"
                )
                .run(context -> {
                    assertThat(context).hasBean("dreaminaCliStartupChecker");
                });
    }

    @Test
    void whenStartupCheckDisabled_shouldNotCreateCheckerBean() {
        contextRunner
                .withPropertyValues(
                        "dreamina.cli.enabled=true",
                        "dreamina.cli.executable=dreamina",
                        "dreamina.cli.startup-check-enabled=false"
                )
                .run(context -> {
                    assertThat(context).doesNotHaveBean("dreaminaCliStartupChecker");
                });
    }

    @Test
    void propertiesBinding_shouldWork() {
        contextRunner
                .withPropertyValues(
                        "dreamina.cli.enabled=true",
                        "dreamina.cli.executable=/usr/bin/dreamina",
                        "dreamina.cli.startup-check-enabled=false",
                        "dreamina.cli.fail-fast-on-unavailable=true"
                )
                .run(context -> {
                    DreaminaProperties props = context.getBean(DreaminaProperties.class);
                    assertThat(props.isEnabled()).isTrue();
                    assertThat(props.getExecutable()).isEqualTo("/usr/bin/dreamina");
                    assertThat(props.isStartupCheckEnabled()).isFalse();
                    assertThat(props.isFailFastOnUnavailable()).isTrue();
                });
    }
}
