package io.github.easy4j.dreamina.spring.boot;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 验证 Starter 实际解析的 SDK 同时提供历史执行器与完整 Canvas 契约。 */
class DreaminaSdkContractIntegrationTest {
    @Test
    void injectedExecutorsExposeCompleteBundledContract() {
        new ApplicationContextRunner().withUserConfiguration(DreaminaAutoConfiguration.class)
                .withPropertyValues("dreamina.cli.startup-check-enabled=false")
                .run(context -> {
                    assertNotNull(context.getBean(DreaminaCliExecutor.class));
                    assertNotNull(context.getBean(DreaminaCanvasCliExecutor.class));
                    assertEquals(60, DreaminaCanvasCommand.values().length,
                            "Starter must resolve the SDK containing director detail routes");
                    for (DreaminaCanvasCommand command : DreaminaCanvasCommand.values()) {
                        assertNotNull(DreaminaCanvasContract.describe(command));
                    }
                });
    }

    @Test
    void directorEntryPointsAreAvailableThroughInjectedExecutor() {
        new ApplicationContextRunner().withUserConfiguration(DreaminaAutoConfiguration.class)
                .withPropertyValues("dreamina.cli.enabled=false")
                .run(context -> {
                    Class<?> executor = context.getBean(DreaminaCanvasCliExecutor.class).getClass();
                    for (String action : new String[]{"Create", "Pull", "Show", "Apply", "Push",
                            "Catalog", "Export", "Freeze", "Preview", "ResumeExport"}) {
                        Class<?> request = Class.forName("io.github.easy4j.dreamina.cli.opts.DreaminaCanvasDirector"
                                + action + "Request");
                        assertNotNull(executor.getMethod("director" + action, request));
                    }
                    assertNotNull(executor.getMethod("schemaDirector"));
                });
    }
    @Test
    void parentManagedJacksonPreservesRawPrecisionAndRejectsScalarCoercion() {
        DreaminaCanvasResponseParser parser =
                new DreaminaCanvasResponseParser();
        String document = "{\"camera\":{\"position\":[1,0.12345678901234567890123456789,3]},\"custom\":[true,null,\"x\"]}";
        DreaminaCliResult raw = DreaminaCliResult.builder()
                .stdout("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"document\":" + document + "}}")
                .exitCode(0).build();
        assertEquals(document, parser.parse("director show", raw,
                DreaminaCanvasDirectorShowResult.class)
                .getData().getDocument().asJson());
        DreaminaCliResult invalid = DreaminaCliResult.builder()
                .stdout("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":7}}")
                .exitCode(0).build();
        assertThrows(DreaminaCanvasCliException.class,
                () -> parser.parse("version", invalid, DreaminaVersion.class));
    }

}
