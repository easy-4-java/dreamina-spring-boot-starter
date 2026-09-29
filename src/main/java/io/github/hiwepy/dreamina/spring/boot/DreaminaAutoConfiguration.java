package io.github.hiwepy.dreamina.spring.boot;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
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
public class DreaminaAutoConfiguration {

    /** 独立绑定 Canvas 配置，避免旧 CLI 属性 Bean 出现同类型歧义。 */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "dreamina.canvas", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DreaminaCanvasCliExecutor dreaminaCanvasCliExecutor(Environment environment) {
        DreaminaCanvasCliProperties properties = new DreaminaCanvasCliProperties();
        Binder.get(environment).bind("dreamina.canvas", Bindable.ofInstance(properties));
        return new DreaminaCanvasCliExecutor(properties);
    }

    /**
     * 基于配置属性构造 Dreamina CLI 执行器。
     *
     * @param properties Dreamina CLI 运行时配置
     * @return 可直接注入业务层的执行器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DreaminaProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public DreaminaCliExecutor dreaminaCliExecutor(DreaminaProperties properties) {
        return new DreaminaCliExecutor(properties);
    }
}
