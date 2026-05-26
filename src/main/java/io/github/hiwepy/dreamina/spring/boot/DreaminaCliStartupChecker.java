package io.github.hiwepy.dreamina.spring.boot;

import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import io.github.hiwepy.dreamina.cli.availability.DreaminaCliAvailabilityReport;
import io.github.hiwepy.dreamina.exception.DreaminaCliStartupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;

/**
 * 应用启动时探测 Dreamina CLI 是否可用，避免业务在运行期才发现「未装配 / 命令不存在 / version 失败」。
 * <p>
 * 逻辑源自业务侧 {@code DreaminaImageStartupChecker} 中与 CLI 相关的部分，剥离配图队列等业务 Bean，
 * 下沉到 Starter 统一管理。
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class DreaminaCliStartupChecker implements ApplicationRunner {

    private final DreaminaCliExecutor dreaminaCliExecutor;
    private final DreaminaProperties dreaminaProperties;
    private final DreaminaCliAvailabilityChecker availabilityChecker;
    private final Environment environment;

    /**
     * 启动阶段执行 {@code dreamina version} 探测；失败时按配置 fail-fast 或仅告警。
     */
    @Override
    public void run(ApplicationArguments args) {
        DreaminaCliAvailabilityReport report = availabilityChecker.check(dreaminaCliExecutor);
        String configSnapshot = buildEffectiveConfigSnapshot();

        if (report.isAvailable()) {
            log.info(
                    "Dreamina CLI ready: {} effectiveConfig={}",
                    report.toDiagnosticMessage(),
                    configSnapshot);
            return;
        }

        String message = report.toDiagnosticMessage()
                + "。请确认 dreamina.cli.enabled=true 且 dreamina.cli.executable 指向可执行的 dreamina（如 /usr/local/bin/dreamina）。"
                + " effectiveConfig={" + configSnapshot + "}";
        if (dreaminaProperties.isFailFastOnUnavailable()) {
            throw new DreaminaCliStartupException(message, report);
        }
        log.warn("Dreamina CLI startup check failed (fail-fast disabled): {}", message);
    }

    /**
     * 汇总当前 JVM 读到的关键配置，便于区分「配置中心已写」与「进程未生效」。
     *
     * @return 配置快照字符串
     */
    private String buildEffectiveConfigSnapshot() {
        String profiles = environment.getProperty("spring.profiles.active", "(unset)");
        return "profiles=" + profiles
                + ", dreamina.cli.enabled=" + dreaminaProperties.isEnabled()
                + ", dreamina.cli.executable=" + dreaminaProperties.getExecutable()
                + ", dreamina.cli.startup-check-enabled=" + dreaminaProperties.isStartupCheckEnabled()
                + ", dreamina.cli.fail-fast-on-unavailable=" + dreaminaProperties.isFailFastOnUnavailable()
                + ", dreaminaStarterOnClasspath=" + isDreaminaStarterOnClasspath();
    }

    /**
     * @return starter 自动配置类是否在 classpath
     */
    private static boolean isDreaminaStarterOnClasspath() {
        try {
            Class.forName("io.github.hiwepy.dreamina.spring.boot.DreaminaAutoConfiguration");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
