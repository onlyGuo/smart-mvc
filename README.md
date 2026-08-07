<h1 align="center">SmartMVC</h1>

<p align="center">
  <strong>The MVC essentials, thoughtfully handled. Everything else stays yours.</strong>
</p>

<p align="center">
  A lightweight, focused enhancement layer for building consistent Spring MVC applications.
</p>

<p align="center">
  <strong>English</strong> · <a href="./README_CN.md">简体中文</a>
</p>

<p align="center">
  <img alt="Java 17+" src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&amp;logo=openjdk&amp;logoColor=white">
  <img alt="Spring Boot 3.5.7" src="https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?style=flat-square&amp;logo=springboot&amp;logoColor=white">
  <a href="https://onlyguo.github.io/smart-mvc/en/"><img alt="Documentation deployment" src="https://github.com/onlyGuo/smart-mvc/actions/workflows/deploy-docs.yml/badge.svg"></a>
  <img alt="Apache License 2.0" src="https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square">
</p>

<p align="center">
  <a href="https://onlyguo.github.io/smart-mvc/en/contents.html">Documentation</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/en/guide/getting-started.html">Quick start</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/en/auth/overview.html">Authentication</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/en/reference/configuration.html">Configuration</a> ·
  <a href="https://onlyguo.github.io/smart-mvc/en/examples/login.html">Example</a>
</p>

---

SmartMVC is a lightweight enhancement framework built on Spring Boot. It brings the infrastructure that REST applications repeatedly build—consistent responses, exception handling, date and time support, request logging, validation integration, and extensible authentication—into one focused Starter.

You keep writing familiar `@RestController`, `@GetMapping`, and `@RequestBody` code. SmartMVC quietly handles the shared MVC concerns around it.

> [!IMPORTANT]
> SmartMVC enhances Spring MVC; it does not replace it. It requires no framework base classes, introduces no database, and leaves your business model, dependency versions, and authentication strategy under your control.

## Features

| | Capability | What it gives you |
| --- | --- | --- |
| 📦 | **Consistent responses** | Automatic wrapping for ordinary controller results, `ApiResponse<T>`, `PageResult<T>`, `void` handling, and configurable `long` serialization |
| 🧯 | **Exceptions and validation** | A stack-trace-free exception hierarchy for common HTTP statuses, business errors, and stable validation responses |
| 🕒 | **Date and time** | Consistent parsing, formatting, JSON serialization, time zones, and incomplete-input policies for Java temporal types |
| 🧾 | **Request logging** | Method, URI, status, and elapsed time at a configurable level, using the Controller logger whenever a handler method is matched |
| 🔐 | **Authentication and authorization** | `GLOBAL` and `ANNOTATED` modes, `@Auth`, `@Anonymous`, excluded paths, roles, named permissions, and `METHOD:/path/**` permissions |
| 👤 | **Current identity** | A Spring-managed `CurrentAuth` facade for the current request's user, roles, permissions, and attributes |
| 🧭 | **IDE-friendly configuration** | Metadata for every `spring.smart.mvc.*` property, including descriptions, defaults, and enum completion |

Every capability is configurable. Authentication is also replaceable: provide an `AuthInterceptor<T>` bean to connect SmartMVC to your own token, user, role, and permission model.

## Design boundaries

SmartMVC is intentionally narrow:

- no custom web runtime and no replacement for Spring MVC;
- no required Controller or Service base classes;
- no database, user table, session store, or prescribed token format;
- no ownership of the application's Spring Web or Bean Validation versions;
- no shared singleton field containing request identity.

This keeps the framework useful without making the application feel like framework code.

## Modules

| Module | Maven coordinates | Responsibility |
| --- | --- | --- |
| [`core`](https://onlyguo.github.io/smart-mvc/en/guide/introduction.html) | `ink.icoding.mvc:core:1.0.0` | Spring-independent annotations, response and configuration models, authentication concepts, permission matching, and exception types |
| [`spring-boot-starter-smart-mvc`](https://onlyguo.github.io/smart-mvc/en/guide/introduction.html) | `ink.icoding:spring-boot-starter-smart-mvc:1.0.0` | Auto-configuration, MVC advice, Jackson integration, request logging, validation integration, and authentication interception |
| [`spring-boot-starter-smart-mvc-example`](https://onlyguo.github.io/smart-mvc/en/examples/login.html) | `ink.icoding:spring-boot-starter-smart-mvc-example:1.0.0` | A database-free application demonstrating responses, validation, temporal handling, exceptions, and in-memory authentication |
| [`docs`](https://onlyguo.github.io/smart-mvc/en/contents.html) | — | VuePress documentation in Chinese, English, Japanese, and Russian |

The split is deliberate: shared contracts remain in the Spring-free Core, while Spring-specific behavior stays in the Starter.

## Requirements

- Java 17 or later for the Starter and Example;
- Spring Boot 3, with 3.5.7 as the current build and test baseline;
- Maven for building the Java modules;
- Node.js 24 and npm only when developing the documentation.

The standalone Core module targets Java 8.

### Dependency ownership

> [!NOTE]
> The Starter deliberately does not supply Spring Web or Bean Validation as application dependencies. Your Spring Boot parent or BOM remains the single source of their versions.

- `spring-boot-starter-web` has `provided` scope inside the SmartMVC Starter, so the application declares it explicitly.
- `spring-boot-starter-validation` is not a Starter dependency; add it only when the application needs Bean Validation.
- Neither dependency is bundled into the published SmartMVC artifacts.

## Quick start

### 1. Add the dependencies

In an application already using the Spring Boot parent or BOM:

```xml
<!-- Required: supplied and versioned by the application -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Optional: add when using Bean Validation -->
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

`1.0.0` is the current repository version. If it is not available from your configured Maven repository yet, install this project locally first:

```bash
mvn install
```

### 2. Write a normal Controller

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

No SmartMVC base class or startup configuration is required.

### 3. Call the endpoint

```bash
curl http://localhost:8080/hello
```

The ordinary return value is wrapped automatically:

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

Date and time values use a consistent format. `long` values are strings by default to prevent precision loss in JavaScript clients.

## Configuration

SmartMVC works with sensible defaults. Change only what your application needs under the `spring.smart.mvc` namespace:

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

The Starter includes Spring Boot configuration metadata, so supported properties, defaults, descriptions, and enum values appear in IDE completion. See the [complete configuration reference](https://onlyguo.github.io/smart-mvc/en/reference/configuration.html) for every option.

## Authentication and the current user

SmartMVC supports two authentication scopes:

| Mode | Behavior |
| --- | --- |
| `ANNOTATED` | Authenticate only Controller types and methods marked with `@Auth` |
| `GLOBAL` | Authenticate every Controller endpoint except `@Anonymous` handlers and `exclude-paths` |

`ANNOTATED` is the default.

```java
@Auth(roles = "admin", permissions = "user:read")
@GetMapping("/users")
public List<UserView> users() {
    return userService.findAll();
}
```

Connect your own authentication logic by registering an `AuthInterceptor<T>` bean:

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

Then inject `CurrentAuth` anywhere Spring manages the object:

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

`CurrentAuth` is a singleton facade backed by request-thread context. Synchronous Servlet requests are isolated and the identity is cleared after completion.

> [!WARNING]
> Without a custom `AuthInterceptor`, the default implementation permits requests so a new project can start immediately. Applications that require real authentication must provide their own implementation. The request context is not automatically propagated to `@Async` methods, manually created threads, reactive pipelines, or Spring MVC asynchronous callbacks.

Read [authentication and authorization](https://onlyguo.github.io/smart-mvc/en/auth/overview.html) and [custom authentication](https://onlyguo.github.io/smart-mvc/en/auth/custom-authentication.html) for the full lifecycle and extension points.

## Run the Example

The Example uses in-memory accounts and fixed demonstration tokens. It needs no database or external service.

```bash
# Install all modules from the repository root
mvn install -DskipTests

# Start the Example application
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

Try the anonymous endpoint:

```bash
curl http://localhost:8080/auth/public
```

Sign in as the demonstration administrator:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

Use the returned token to read the current identity:

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

The fixed users, plain-text passwords, and tokens exist only for local learning and automated tests. See the [complete authentication example](https://onlyguo.github.io/smart-mvc/en/examples/login.html) for the administrator and rejection flows.

## Documentation

The guides move from first principles to detailed references:

| Language | Documentation |
| --- | --- |
| English | [Read the English documentation](https://onlyguo.github.io/smart-mvc/en/contents.html) |
| 简体中文 | [阅读中文文档](https://onlyguo.github.io/smart-mvc/contents.html) |
| 日本語 | [日本語ドキュメントを読む](https://onlyguo.github.io/smart-mvc/ja/contents.html) |
| Русский | [Читать документацию](https://onlyguo.github.io/smart-mvc/ru/contents.html) |

Useful English entry points:

- [What is SmartMVC?](https://onlyguo.github.io/smart-mvc/en/guide/introduction.html)
- [Quick start](https://onlyguo.github.io/smart-mvc/en/guide/getting-started.html)
- [How a request moves through SmartMVC](https://onlyguo.github.io/smart-mvc/en/guide/how-it-works.html)
- [Core features](https://onlyguo.github.io/smart-mvc/en/features/response.html)
- [Authentication and authorization](https://onlyguo.github.io/smart-mvc/en/auth/overview.html)
- [Configuration reference](https://onlyguo.github.io/smart-mvc/en/reference/configuration.html)
- [API reference](https://onlyguo.github.io/smart-mvc/en/reference/api.html)

## Build from source

Run the complete Java build and test suite:

```bash
mvn clean verify
```

Install the documentation dependencies and start the local preview:

```bash
cd docs
npm ci
npm run dev
```

Generate the static site in `docs/.vuepress/dist`:

```bash
npm run build
```

The GitHub Pages workflow builds and deploys the documentation on pushes to `main`, and can also be started manually.

## Contributing

Issues and focused pull requests are welcome. Please preserve the module boundaries, add tests for behavior changes, and run the relevant verification commands before submitting a change.

For Java changes:

```bash
mvn clean verify
```

For documentation changes:

```bash
cd docs
npm ci
npm run build
```

## License

SmartMVC is available under the Apache License 2.0. Copyright and attribution information is provided in the repository's `NOTICE` file.
