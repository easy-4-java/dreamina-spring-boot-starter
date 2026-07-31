package io.github.easy4j.dreamina.spring.boot;

import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 注册 Dreamina CLI SDK 所需的 Spring Bean。
 * <p>
 * Starter 负责将 Spring 环境中的 {@code dreamina.cli.*} 配置绑定为属性对象，并暴露
 * {@link DreaminaCliExecutor} 供业务层直接注入使用。
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(DreaminaCliExecutor.class)
@EnableConfigurationProperties(DreaminaProperties.class)
@ConditionalOnProperty(prefix = DreaminaProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DreaminaAutoConfiguration {

    /**
     * 基于配置属性构造 Dreamina CLI 执行器。
     *
     * @param properties Dreamina CLI 运行时配置
     * @return 可直接注入业务层的执行器
     */
    @Bean
    @ConditionalOnMissingBean
    public DreaminaCliExecutor dreaminaCliExecutor(DreaminaProperties properties) {
        return new DreaminaCliExecutor(properties);
    }

    /**
     * CLI 可用性探测器（无状态，可被业务或 Starter 复用）。
     */
    @Bean
    @ConditionalOnMissingBean
    public DreaminaCliAvailabilityChecker dreaminaCliAvailabilityChecker() {
        return new DreaminaCliAvailabilityChecker();
    }

    /**
     * 启动时校验本机 {@code dreamina} 可执行且 {@code version} 成功。
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = DreaminaProperties.PREFIX,
            name = "startup-check-enabled",
            havingValue = "true",
            matchIfMissing = true)
    public DreaminaCliStartupChecker dreaminaCliStartupChecker(
            DreaminaCliExecutor dreaminaCliExecutor,
            DreaminaProperties dreaminaProperties,
            DreaminaCliAvailabilityChecker availabilityChecker,
            Environment environment) {
        return new DreaminaCliStartupChecker(
                dreaminaCliExecutor, dreaminaProperties, availabilityChecker, environment);
    }
}
