<h1 align="center">SmartMVC</h1>

<p align="center">
  <strong>该做的，都帮你做好；不该做的，一律不碰。</strong>
</p>

<p align="center">
  一个轻量、克制且可替换的 Spring MVC 增强层，让应用保持一致，也保留自由。
</p>

<p align="center">
  <a href="./README.md">English</a> · <strong>简体中文</strong>
</p>

<p align="center">
  <img alt="Java 17+" src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&amp;logo=openjdk&amp;logoColor=white">
  <img alt="Spring Boot 3.5.7" src="https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?style=flat-square&amp;logo=springboot&amp;logoColor=white">
  <a href="https://onlyguo.github.io/smart-mvc/"><img alt="文档部署" src="https://github.com/onlyGuo/smart-mvc/actions/workflows/deploy-docs.yml/badge.svg"></a>
  <img alt="Apache License 2.0" src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square">
</p>

<p align="center">
  <a href="https://onlyguo.github.io/smart-mvc/contents.html">完整文档</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/guide/getting-started.html">快速开始</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/auth/overview.html">认证授权</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/reference/configuration.html">配置参考</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/examples/login.html">示例项目</a>
</p>

---

SmartMVC 是一个基于 Spring Boot 的轻量 MVC 增强框架。它把 REST 应用中反复出现的通用工作——统一响应、异常处理、日期时间、请求日志、参数校验与认证授权——收拢成一个专注的 Starter。

你仍然使用熟悉的 `@RestController`、`@GetMapping` 和 `@RequestBody` 编写业务代码；SmartMVC 只在合适的位置，安静地补齐 MVC 层的公共能力。

> [!IMPORTANT]
> SmartMVC 增强 Spring MVC，但不会替代它。它不要求继承框架基类，不引入数据库，也不会替应用决定业务模型、依赖版本与认证方案。

## 能力概览

| | 能力 | SmartMVC 为你处理什么 |
| --- | --- | --- |
| 📦 | **统一响应** | 自动包装普通 Controller 返回值，提供 `ApiResponse<T>`、`PageResult<T>`、`void` 处理与可配置的 `long` 序列化策略 |
| 🧯 | **异常与校验** | 提供不记录堆栈的异常体系，覆盖常见 HTTP 状态、业务错误与稳定一致的参数校验响应 |
| 🕒 | **日期与时间** | 统一 Java 日期时间类型的请求解析、格式化、JSON 序列化、时区与不完整输入策略 |
| 🧾 | **请求日志** | 记录方法、地址、状态码和耗时；匹配到处理方法时使用实际 Controller 作为 logger，并支持配置日志级别 |
| 🔐 | **认证与授权** | 支持 `GLOBAL`、`ANNOTATED`、`@Auth`、`@Anonymous`、排除路径、角色、命名权限与 `METHOD:/path/**` 请求权限 |
| 👤 | **当前身份** | 提供 Spring 托管的 `CurrentAuth`，读取当前请求的用户、角色、权限与扩展属性 |
| 🧭 | **友好的配置体验** | 为全部 `spring.smart.mvc.*` 配置提供说明、默认值与枚举补全元数据 |

每项能力都可以配置。认证过程也可以替换：应用只需提供自己的 `AuthInterceptor<T>` Bean，就能接入已有的 Token、用户、角色与权限模型。

## 设计边界

SmartMVC 刻意保持克制：

- 不创造另一套 Web 运行时，也不替代 Spring MVC；
- 不要求 Controller 或 Service 继承框架基类；
- 不引入数据库、用户表、会话存储，也不规定 Token 格式；
- 不接管应用的 Spring Web 与 Bean Validation 版本；
- 不在共享单例字段中保存请求身份。

框架把公共部分认真做好，同时让应用仍然像你自己的应用。

## 项目结构

| 模块 | Maven 坐标 | 职责 |
| --- | --- | --- |
| [`core`](https://onlyguo.github.io/smart-mvc/guide/introduction.html) | `ink.icoding.mvc:core:1.0.0` | 不依赖 Spring 的注解、响应与配置模型、认证概念、权限匹配及异常类型 |
| [`spring-boot-starter-smart-mvc`](https://onlyguo.github.io/smart-mvc/guide/introduction.html) | `ink.icoding:spring-boot-starter-smart-mvc:1.0.0` | 自动配置、MVC Advice、Jackson 集成、请求日志、校验集成与认证拦截 |
| [`spring-boot-starter-smart-mvc-example`](https://onlyguo.github.io/smart-mvc/examples/login.html) | `ink.icoding:spring-boot-starter-smart-mvc-example:1.0.0` | 无需数据库的示例应用，演示响应、校验、时间处理、异常与内存登录鉴权 |
| [`docs`](https://onlyguo.github.io/smart-mvc/contents.html) | — | 基于 VuePress 的中文、英文、日文与俄文文档 |

这样的拆分是有意为之：公共契约留在不依赖 Spring 的 Core 中，所有 Spring 相关实现都放在 Starter 中。

## 环境要求

- Starter 与 Example 需要 Java 17 或更高版本；
- Spring Boot 3，当前构建与测试基线为 3.5.7；
- 使用 Maven 构建 Java 模块；
- 只有开发文档时才需要 Node.js 24 与 npm。

独立的 Core 模块以 Java 8 为目标版本。

### 依赖边界

> [!NOTE]
> Starter 不会替应用提供 Spring Web 或 Bean Validation。应用自己的 Spring Boot Parent 或 BOM 仍然是这些依赖版本的唯一管理者。

- `spring-boot-starter-web` 在 SmartMVC Starter 中使用 `provided` 作用域，由应用显式引入；
- `spring-boot-starter-validation` 不属于 Starter 依赖，只有应用需要 Bean Validation 时才引入；
- 这两个依赖都不会被打包进 SmartMVC 的发布产物。

## 快速开始

### 1. 添加依赖

在已经使用 Spring Boot Parent 或 BOM 的应用中添加：

```xml
<!-- 必需：由应用引入并管理版本 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 可选：使用 Bean Validation 时引入 -->
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

`1.0.0` 是当前仓库版本。若它暂时不存在于你配置的 Maven 仓库中，请先在本项目根目录安装到本地仓库：

```bash
mvn install
```

### 2. 像平常一样编写 Controller

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Greeting hello() {
        return new Greeting("Hello, SmartMVC", LocalDateTime.now());
    }

    public record Greeting(String message, LocalDateTime time) {
    }
}
```

无需继承 SmartMVC 基类，也无需为了启动框架添加额外配置。

### 3. 调用接口

```bash
curl http://localhost:8080/hello
```

普通返回值会自动包装成统一响应：

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "message": "Hello, SmartMVC",
    "time": "2026-08-07 10:30:00"
  },
  "timestamp": "1786069800000"
}
```

日期时间会按统一格式输出；`long` 默认序列化为字符串，避免 JavaScript 长整数精度丢失。

## 配置

SmartMVC 提供了合理的默认行为。只需在 `spring.smart.mvc` 命名空间下调整应用真正需要改变的部分：

```yaml
spring:
  smart:
    mvc:
      response:
        success-message: ok
        long-as-string: true
      date-time:
        zone-id: Asia/Shanghai
      request-log:
        level: INFO
      auth:
        mode: ANNOTATED
```

Starter 自带 Spring Boot 配置元数据，因此 IDE 可以补全属性名称、默认值、说明与枚举选项。所有配置见[完整配置参考](https://onlyguo.github.io/smart-mvc/reference/configuration.html)。

## 认证与当前用户

SmartMVC 支持两种认证范围：

| 模式 | 行为 |
| --- | --- |
| `ANNOTATED` | 只认证带有 `@Auth` 的 Controller 类型或方法 |
| `GLOBAL` | 认证所有 Controller 接口，但跳过 `@Anonymous` 与 `exclude-paths` |

`ANNOTATED` 是默认模式。

```java
@Auth(roles = "admin", permissions = "user:read")
@GetMapping("/users")
public List<UserView> users() {
    return userService.findAll();
}
```

通过注册一个 `AuthInterceptor<T>` Bean，即可接入应用自己的认证逻辑：

```java
@Component
public class TokenAuthInterceptor implements AuthInterceptor<AppUser> {

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public AuthPrincipal<AppUser> authenticate(
            String token,
            HttpServletRequest request) {
        return tokenService.authenticate(token);
    }
}
```

随后，可以在任何由 Spring 管理的类中注入 `CurrentAuth`：

```java
@Service
public class ProfileService {

    private final CurrentAuth currentAuth;

    public ProfileService(CurrentAuth currentAuth) {
        this.currentAuth = currentAuth;
    }

    public AppUser currentUser() {
        return currentAuth.getUser(AppUser.class);
    }
}
```

`CurrentAuth` 是一个单例门面，底层身份数据按请求线程隔离；同步 Servlet 请求之间不会互相污染，结束后也会自动清理。

> [!WARNING]
> 没有自定义 `AuthInterceptor` 时，默认实现会放行请求，让空项目可以直接启动。需要真实认证的应用必须提供自己的实现。当前请求身份不会自动传播到 `@Async`、手动线程、响应式链路或 Spring MVC 异步回调中。

完整生命周期与扩展方法见[认证授权](https://onlyguo.github.io/smart-mvc/auth/overview.html)和[自定义认证](https://onlyguo.github.io/smart-mvc/auth/custom-authentication.html)。

## 运行 Example

Example 使用内存账户和固定 Token 演示认证流程，不需要数据库或外部服务。

```bash
# 在项目根目录安装全部模块
mvn install -DskipTests

# 启动 Example
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

访问匿名接口：

```bash
curl http://localhost:8080/auth/public
```

使用示例管理员登录：

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

携带返回的 Token 读取当前身份：

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

固定用户、明文密码和 Token 只用于本地学习与自动化测试。管理员与拒绝流程见[完整认证示例](https://onlyguo.github.io/smart-mvc/examples/login.html)。

## 文档

文档按照从基础概念到详细参考的顺序组织：

| 语言 | 文档入口 |
| --- | --- |
| 简体中文 | [阅读中文文档](https://onlyguo.github.io/smart-mvc/contents.html) |
| English | [Read the English documentation](https://onlyguo.github.io/smart-mvc/en/contents.html) |
| 日本語 | [日本語ドキュメントを読む](https://onlyguo.github.io/smart-mvc/ja/contents.html) |
| Русский | [Читать документацию](https://onlyguo.github.io/smart-mvc/ru/contents.html) |

常用中文入口：

- [认识 SmartMVC](https://onlyguo.github.io/smart-mvc/guide/introduction.html)
- [快速开始](https://onlyguo.github.io/smart-mvc/guide/getting-started.html)
- [请求如何经过 SmartMVC](https://onlyguo.github.io/smart-mvc/guide/how-it-works.html)
- [核心功能](https://onlyguo.github.io/smart-mvc/features/response.html)
- [认证授权](https://onlyguo.github.io/smart-mvc/auth/overview.html)
- [配置参考](https://onlyguo.github.io/smart-mvc/reference/configuration.html)
- [API 参考](https://onlyguo.github.io/smart-mvc/reference/api.html)

## 从源码构建

运行全部 Java 构建与测试：

```bash
mvn clean verify
```

安装文档依赖并启动本地预览：

```bash
cd docs
npm ci
npm run dev
```

生成静态文档到 `docs/.vuepress/dist`：

```bash
npm run build
```

GitHub Pages 工作流会在代码推送到 `main` 时构建并部署文档，也支持在 Actions 页面手动运行。

## 参与贡献

欢迎提交 Issue 和聚焦明确的 Pull Request。请保持模块边界，为行为变化补充测试，并在提交前运行对应的检查命令。

Java 改动：

```bash
mvn clean verify
```

文档改动：

```bash
cd docs
npm ci
npm run build
```

## 开源协议

SmartMVC 基于 Apache License 2.0 开源，版权与署名信息见仓库中的 `NOTICE` 文件。
