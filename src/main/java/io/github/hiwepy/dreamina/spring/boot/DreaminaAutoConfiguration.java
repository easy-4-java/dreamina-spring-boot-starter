package io.github.hiwepy.dreamina.spring.boot;

import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
