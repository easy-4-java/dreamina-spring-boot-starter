package io.github.hiwepy.dreamina.spring.boot;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasLsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 验证 Canvas CLI 的独立配置和执行器由 Starter 正确装配。 */
class DreaminaCanvasAutoConfigurationTest {

    private final ApplicationContextRunner contexts = new ApplicationContextRunner()
            .withUserConfiguration(DreaminaAutoConfiguration.class)
            .withPropertyValues("dreamina.cli.startup-check-enabled=false");

    @Test
    void bindsCanvasProfileWithoutChangingLegacyCli() {
        contexts.withPropertyValues("dreamina.canvas.profile=work", "dreamina.canvas.region=cn")
                .run(context -> {
                    assertNotNull(context.getBean(DreaminaCanvasCliExecutor.class));
                    DreaminaCanvasCliProperties properties = context.getBean(DreaminaCanvasCliExecutor.class).getProperties();
                    assertEquals("work", properties.getProfile());
                    assertEquals("dreamina-canvas", properties.getExecutable());
                    assertEquals("work", context.getBean(DreaminaCanvasCliExecutor.class).getProperties().getProfile());
                    assertEquals("dreamina", context.getBean(DreaminaProperties.class).getExecutable());
                });
    }

    @Test
    void canDisableCanvasIndependently() {
        contexts.withPropertyValues("dreamina.canvas.enabled=false")
                .run(context -> assertFalse(context.containsBean("dreaminaCanvasCliExecutor")));
    }

    @Test
    void canvasWorksWhenLegacyCliIsDisabled() {
        contexts.withPropertyValues("dreamina.cli.enabled=false", "dreamina.canvas.profile=personal")
                .run(context -> {
                    assertFalse(context.containsBean("dreaminaCliExecutor"));
                    assertNotNull(context.getBean(DreaminaCanvasCliExecutor.class));
                    assertEquals("personal", context.getBean(DreaminaCanvasCliExecutor.class).getProperties().getProfile());
                });
    }

    @Test
    void installedCanvasVersionWorksThroughSpringBean() {
        Assumptions.assumeTrue("1".equals(System.getenv("DREAMINA_CANVAS_REAL_SMOKE")));
        contexts.withPropertyValues("dreamina.canvas.profile=starter-smoke")
                .run(context -> assertTrue(context.getBean(DreaminaCanvasCliExecutor.class).version().isSuccess()));
    }

    @Test
    void authenticatedCanvasListWorksThroughSpringBean() {
        Assumptions.assumeTrue("1".equals(System.getenv("DREAMINA_CANVAS_REAL_SMOKE")));
        contexts.withPropertyValues("dreamina.canvas.profile=default")
                .run(context -> assertNotNull(context.getBean(DreaminaCanvasCliExecutor.class)
                        .canvasLs(DreaminaCanvasLsRequest.builder().limit(1L).build()).getData()));
    }
}
