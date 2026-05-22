# dreamina-spring-boot-starter

Spring Boot Starter，自动装配 [dreamina-java-sdk](../dreamina-java-sdk)，让应用通过注入 `DreaminaCliExecutor` 即可使用本地 `dreamina` CLI。

**CLI 命令完整说明、Agent 编排 SOP、flag 速查与 FAQ** 见 [dreamina-java-sdk README](../dreamina-java-sdk/README.md)。

## Maven 依赖

```xml
<dependency>
  <groupId>io.github.hiwepy</groupId>
  <artifactId>dreamina-spring-boot-starter</artifactId>
  <version>1.0.x.20260515-SNAPSHOT</version>
</dependency>
```

## 自动配置能力

Starter 会自动完成：

- 绑定 `dreamina.cli.*` 配置到 `DreaminaProperties`
- 注册 `DreaminaCliExecutor`
- 通过 `spring.factories` 与 `AutoConfiguration.imports` 同时兼容 Spring Boot 2.x / 3.x 自动配置发现

主自动配置类：

- [`DreaminaAutoConfiguration`](src/main/java/io/github/hiwepy/dreamina/spring/boot/DreaminaAutoConfiguration.java)
- [`DreaminaProperties`](src/main/java/io/github/hiwepy/dreamina/spring/boot/DreaminaProperties.java)

## 示例配置

### application.yml

```yaml
dreamina:
  cli:
    enabled: true
    executable: dreamina
    working-directory: /opt/dreamina
    command-timeout-millis: 120000
    default-poll-interval-seconds: 5
```

## 使用示例

```java
import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.cli.DreaminaCliTypedResult;
import io.github.hiwepy.dreamina.cli.DreaminaQueryResult;
import io.github.hiwepy.dreamina.cli.opts.DreaminaQueryResultRequest;
import org.springframework.stereotype.Service;

@Service
public class DreaminaFacade {

    private final DreaminaCliExecutor executor;

    public DreaminaFacade(DreaminaCliExecutor executor) {
        this.executor = executor;
    }

    public Long currentCredit() {
        return executor.userCreditInfo().getStructured().getTotalCredit();
    }

    public DreaminaQueryResult queryAndDownload(String submitId, String downloadDir) {
        DreaminaQueryResultRequest request = DreaminaQueryResultRequest.builder()
            .submitId(submitId)
            .downloadDir(downloadDir)
            .build();
        DreaminaCliTypedResult<DreaminaQueryResult> result = executor.queryResultInfo(request);
        return result.getStructured();
    }
}
```

## 条件装配

默认 `dreamina.cli.enabled=true`，当设置为 `false` 时，Starter 不会注册 `DreaminaCliExecutor`。

## 测试与验证

```bash
cd dreamina-spring-boot-starter
mvn test -Dtest=DreaminaAutoConfigurationTest
```

## 发布说明

```bash
mvn clean install -DskipTests
mvn -Prelease clean deploy
```
