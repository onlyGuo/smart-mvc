# SmartMVC

[![Deploy documentation](https://github.com/onlyGuo/smart-mvc/actions/workflows/deploy-docs.yml/badge.svg)](https://github.com/onlyGuo/smart-mvc/actions/workflows/deploy-docs.yml)

> 该做的，都帮你做好；不该做的，一律不碰。

SmartMVC 是一个基于 Spring Boot 的轻量 MVC 增强框架。它把统一响应、异常处理、日期时间、请求日志和认证授权这些经常重复出现的基础工作整理成一致、可配置、可替换的能力，让 Controller 专注于业务本身。

它不会替代 Spring MVC，不要求业务类继承框架基类，也不会引入数据库或替应用决定 Spring Web、Bean Validation 的版本。你仍然使用熟悉的 `@RestController`、`@GetMapping` 和 `@RequestBody`，SmartMVC 只在合适的位置补齐通用能力。

## 能力概览

| 能力 | 说明 |
| --- | --- |
| 统一响应 | 自动包装普通 Controller 返回值，提供 `ApiResponse`、分页结果以及可配置的 `long` 序列化策略 |
| 异常与校验 | 将业务异常、常见 HTTP 异常和参数校验错误转换为稳定的错误结构 |
| 日期与时间 | 统一处理 `LocalDateTime`、`LocalDate`、`LocalTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime` 和 `Date` 的输入、输出与时区 |
| 请求日志 | 记录请求方法、地址、状态码和耗时，并使用实际 Controller 作为 logger 类别 |
| 认证与授权 | 支持全量认证或 `@Auth` 按需认证、`@Anonymous`、排除路径、当前用户、角色与请求权限 |
| 配置体验 | 所有选项集中在 `spring.smart.mvc.*`，并提供 Spring Boot 配置元数据供 IDE 补全 |

每项能力都可以独立配置。认证逻辑也不会被框架写死：应用可以实现自己的 `AuthInterceptor`，完成 Token 解析、身份构建和权限校验；没有自定义实现时，默认实现保持放行。

## 项目结构

| 模块 | Maven 坐标 | 职责 |
| --- | --- | --- |
| `core` | `ink.icoding.mvc:core:1.0.0` | 不依赖 Spring 的注解、响应模型、配置模型、身份与权限模型以及异常体系 |
| `spring-boot-starter-smart-mvc` | `ink.icoding:spring-boot-starter-smart-mvc:1.0.0` | 配置绑定、自动配置、MVC 增强、Jackson、日志、异常处理与认证拦截 |
| `spring-boot-starter-smart-mvc-example` | `ink.icoding:spring-boot-starter-smart-mvc-example:1.0.0` | 无数据库的完整示例应用，包含响应、校验、时间处理和内存登录鉴权 |
| `docs` | — | 基于 VuePress 的中、英、日、俄多语言文档 |

## 环境与依赖边界

- Java 17 或更高版本；
- Spring Boot 3.2 或更高版本；
- 当前项目的构建与测试基线为 Spring Boot 3.5.7；
- 使用 Maven 构建 Java 模块，使用 Node.js 与 npm 构建文档。

Starter 不会把 Spring Web 或 Bean Validation 打进发布产物，也不会把它们的版本传递给应用：

- `spring-boot-starter-web` 在 Starter 中使用 `provided` 作用域，由应用显式引入；
- `spring-boot-starter-validation` 不属于 Starter 依赖，需要参数校验时由应用显式引入；
- 两者的实际版本由应用自己的 Spring Boot Parent 或 BOM 管理。

这样，SmartMVC 可以增强已有的 Spring MVC 应用，同时不干预应用的依赖治理。

## 快速接入

在已经使用 Spring Boot Parent 或 BOM 管理依赖版本的项目中添加：

```xml
<!-- 由应用提供：Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 由应用按需提供：Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- SmartMVC -->
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>spring-boot-starter-smart-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

这里的 `1.0.0` 是当前仓库版本。若该构件尚未存在于你配置的 Maven 仓库中，请先在本仓库根目录执行 `mvn install` 安装到本地仓库。

接下来照常编写 Controller，无需为了启动 SmartMVC 添加额外配置：

```java
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public Greeting hello() {
        return new Greeting("Hello, SmartMVC", LocalDateTime.now());
    }

    public record Greeting(String message, LocalDateTime time) {
    }
}
```

普通返回值会按默认规则包装为统一响应，日期时间也会使用一致的格式输出。需要调整行为时，再在 `application.yaml` 中配置 `spring.smart.mvc.*` 即可；完整选项见[配置参考](docs/reference/configuration.md)。

## 文档

第一次使用，建议从对应语言的完整目录开始。文档按照“认识框架 → 快速接入 → 核心功能 → 认证授权 → 完整示例 → 参考手册”的顺序组织：

- [中文文档](docs/contents.md)
- [English documentation](docs/en/contents.md)
- [日本語ドキュメント](docs/ja/contents.md)
- [Документация на русском](docs/ru/contents.md)

## 运行 Example

Example 使用内存账户和固定 Token 演示完整登录鉴权流程，不需要数据库或其他外部服务。

```bash
# 在本地安装各模块
mvn install -DskipTests

# 启动示例应用
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

启动后可以先访问匿名接口：

```bash
curl http://localhost:8080/auth/public
```

登录、当前用户、角色与权限检查的完整请求过程见[登录鉴权示例](docs/examples/login.md)。示例账户仅用于本地学习与自动化测试，请勿用于生产环境。

## 本地构建

验证全部 Java 模块与测试：

```bash
mvn clean verify
```

本地预览文档：

```bash
cd docs
npm ci
npm run dev
```

构建静态文档：

```bash
cd docs
npm ci
npm run build
```

静态文件会生成到 `docs/.vuepress/dist`。

## 文档发布

推送到 `main` 分支或在 Actions 页面手动运行 [Deploy documentation to GitHub Pages](.github/workflows/deploy-docs.yml)，都会重新构建并发布文档。工作流会读取 GitHub Pages 的实际基础路径，因此项目站点与自定义域名都不需要在源码中硬编码地址。

第一次启用时，需要在仓库的 **Settings → Pages → Build and deployment → Source** 中选择 **GitHub Actions**。如果仓库的默认分支不是 `main`，请同步修改工作流中的触发分支。

## 许可证

项目根 POM 将许可证声明为 [GNU Affero General Public License v3.0](https://www.gnu.org/licenses/agpl-3.0.html)。使用、修改或分发本项目之前，请阅读并遵守该许可证条款。
