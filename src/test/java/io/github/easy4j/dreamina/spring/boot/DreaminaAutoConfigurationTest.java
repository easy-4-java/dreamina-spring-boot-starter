package io.github.easy4j.dreamina.spring.boot;

import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Starter 自动配置基础测试。
 *
 * @author wandl
 * @since 1.0.0
 */
@SpringBootTest(
        classes = DreaminaAutoConfiguration.class,
        properties = {
                "dreamina.cli.enabled=true",
                "dreamina.cli.executable=dreamina",
                "dreamina.cli.command-timeout-millis=120000",
                "dreamina.cli.startup-check-enabled=false"
        }
)
class DreaminaAutoConfigurationTest {

    @Autowired
    private DreaminaCliProperties properties;

    @Autowired
    private DreaminaCliExecutor executor;

    @Autowired
    private DreaminaCliAvailabilityChecker availabilityChecker;

    /**
     * 验证 Starter 能注册属性对象与执行器 Bean。
     */
    @Test
    void shouldRegisterDreaminaBeans() {
        assertNotNull(properties);
        assertNotNull(executor);
        assertNotNull(availabilityChecker);
    }
}
