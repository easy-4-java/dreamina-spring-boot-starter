package io.github.hiwepy.dreamina.spring.boot;

import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.DreaminaCliProperties;
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
                "dreamina.cli.command-timeout-millis=120000"
        }
)
class DreaminaAutoConfigurationTest {

    @Autowired
    private DreaminaCliProperties properties;

    @Autowired
    private DreaminaCliExecutor executor;

    /**
     * 验证 Starter 能注册属性对象与执行器 Bean。
     */
    @Test
    void shouldRegisterDreaminaBeans() {
        assertNotNull(properties);
        assertNotNull(executor);
    }
}
