package io.github.easy4j.dreamina.spring.boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DreaminaProperties}.
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 */
class DreaminaPropertiesTest {

    @Test
    void prefix_shouldBeCorrect() {
        assertThat(DreaminaProperties.PREFIX).isEqualTo("dreamina.cli");
    }

    @Test
    void defaultValues_shouldBeCorrect() {
        DreaminaProperties props = new DreaminaProperties();
        assertThat(props.isEnabled()).isTrue();
        assertThat(props.isStartupCheckEnabled()).isTrue();
        assertThat(props.isFailFastOnUnavailable()).isFalse();
    }

    @Test
    void settersAndGetters_shouldWork() {
        DreaminaProperties props = new DreaminaProperties();
        props.setEnabled(false);
        props.setStartupCheckEnabled(false);
        props.setFailFastOnUnavailable(true);
        assertThat(props.isEnabled()).isFalse();
        assertThat(props.isStartupCheckEnabled()).isFalse();
        assertThat(props.isFailFastOnUnavailable()).isTrue();
    }

    @Test
    void dataAnnotation_shouldGenerateEqualsAndHashCode() {
        DreaminaProperties props1 = new DreaminaProperties();
        DreaminaProperties props2 = new DreaminaProperties();
        assertThat(props1).isEqualTo(props2);
        assertThat(props1.hashCode()).isEqualTo(props2.hashCode());
    }

    @Test
    void dataAnnotation_shouldGenerateToString() {
        DreaminaProperties props = new DreaminaProperties();
        String str = props.toString();
        assertThat(str).contains("enabled=true");
        assertThat(str).contains("startupCheckEnabled=true");
    }

    @Test
    void extendsDreaminaCliProperties_shouldInheritFields() {
        DreaminaProperties props = new DreaminaProperties();
        // DreaminaCliProperties fields should be accessible
        props.setExecutable("/usr/local/bin/dreamina");
        assertThat(props.getExecutable()).isEqualTo("/usr/local/bin/dreamina");
    }
}
