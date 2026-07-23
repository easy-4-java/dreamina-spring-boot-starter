package io.github.easy4j.dreamina.spring.boot;

import io.github.easy4j.dreamina.DreaminaCliProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dreamina Spring Boot 配置属性。
 * <p>
 * 该对象直接复用 SDK 的 {@link DreaminaCliProperties} 字段定义，并补充 starter 开关，
 * 保持应用侧继续使用 {@code dreamina.cli.*} 键完成配置即可。
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = DreaminaProperties.PREFIX)
public class DreaminaProperties extends DreaminaCliProperties {

    /**
     * Dreamina CLI 配置前缀。
     */
    public static final String PREFIX = "dreamina.cli";

    /**
     * 是否启用 Starter 提供的自动配置。
     */
    private boolean enabled = true;

    /**
     * 是否在应用启动时执行 {@code dreamina version} 探测。
     */
    private boolean startupCheckEnabled = true;

    /**
     * 启动探测失败时是否中断应用启动；默认 false 仅打 WARN，生产可设为 true。
     */
    private boolean failFastOnUnavailable = false;
}
