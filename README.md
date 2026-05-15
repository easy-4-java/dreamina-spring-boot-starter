# dreamina-spring-boot-starter

Spring Boot Starter，自动装配 [dreamina-java-sdk](../dreamina-java-sdk)，让应用通过注入 `DreaminaCliExecutor` 即可使用本地 `dreamina` CLI。

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

### application.properties

```properties
dreamina.cli.enabled=true
dreamina.cli.executable=dreamina
dreamina.cli.working-directory=/opt/dreamina
dreamina.cli.command-timeout-millis=120000
dreamina.cli.default-poll-interval-seconds=5
```

## 使用示例

```java
import io.github.hiwepy.dreamina.cli.DreaminaCliExecutor;
import io.github.hiwepy.dreamina.cli.DreaminaCliTypedResult;
import io.github.hiwepy.dreamina.cli.DreaminaUserCreditResult;
import org.springframework.stereotype.Service;

@Service
public class DreaminaFacade {

    private final DreaminaCliExecutor executor;

    public DreaminaFacade(DreaminaCliExecutor executor) {
        this.executor = executor;
    }

    public Long currentCredit() {
        DreaminaCliTypedResult<DreaminaUserCreditResult> result = executor.userCreditInfo();
        return result.getStructured().getTotalCredit();
    }
}
```

## 条件装配

默认 `dreamina.cli.enabled=true`，当设置为 `false` 时，Starter 不会注册 `DreaminaCliExecutor`。

## 测试与验证

Starter 已提供基础自动配置测试：

```bash
cd dreamina-spring-boot-starter
mvn test -Dtest=DreaminaAutoConfigurationTest
```

## 发布说明

Starter 已补齐与 `openclaw-spring-boot-starter` 同风格的发布信息与 release profile：

- `url`
- `licenses`
- `scm`
- `developers`
- `distributionManagement`
- `release` profile

本地安装：

```bash
mvn clean install -DskipTests
```

正式发布：

```bash
mvn -Prelease clean deploy
```

前提同样是本机已配置 GPG 签名与 Central 发布凭据。
