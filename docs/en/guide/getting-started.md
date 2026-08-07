---
title: Quick start
description: Prepare the Spring MVC application dependencies, add SmartMVC, and create your first endpoint.
prev:
  text: What is SmartMVC?
  link: /en/guide/introduction.html
next:
  text: How a request moves through SmartMVC
  link: /en/guide/how-it-works.html
---

# Quick start

This guide does three things: prepares the application's web and validation dependencies, adds SmartMVC, and shows the standardized response from the first endpoint.

## Requirements

- Java 17 or later;
- Spring Boot 3.2 or later (the current build and test baseline is 3.5.7);
- a Maven project.

## 1. Add the application dependencies

```xml
<!-- Provided by the application: Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Provided by the application: Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Adds SmartMVC to the existing Spring MVC application -->
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>spring-boot-starter-smart-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

SmartMVC does not bring in Spring Web or Bean Validation transitively. The application declares both dependencies explicitly, and its own Spring Boot parent or BOM manages their versions. SmartMVC therefore does not pin the Spring Web or Bean Validation versions used by the application.

## 2. Create an endpoint

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

## 3. Start the application and call the endpoint

```bash
mvn spring-boot:run
```

Send a request:

```bash
curl http://localhost:8080/hello
```

The default response looks like this:

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "message": "Hello, SmartMVC",
    "time": "2026-08-06 16:30:00"
  },
  "timestamp": "1786005000000"
}
```

SmartMVC performed two operations:

1. it wrapped the `Greeting` returned by the controller in an `ApiResponse`;
2. it formatted the `LocalDateTime` with the default pattern `yyyy-MM-dd HH:mm:ss`.

`timestamp` is serialized as a string by default so JavaScript clients do not lose precision when handling 64-bit integers.

## 4. Add a minimal configuration

All settings live under `spring.smart.mvc`. This example changes only the success message and time zone:

```yaml
spring:
  smart:
    mvc:
      response:
        success-message: ok
      date-time:
        zone-id: Asia/Shanghai
```

The Starter provides Spring Boot configuration metadata, so supported properties, defaults, and enum values are available through IDE completion.

## What is enabled by default?

Without additional configuration:

- ordinary controller return values are wrapped in a consistent response;
- `void` return values produce a successful response;
- common MVC exceptions use the same error structure;
- request validation is enabled;
- request summaries are logged at `INFO` level;
- authentication uses `ANNOTATED` mode, so only endpoints marked with `@Auth` are processed;
- if the application does not provide an authentication implementation, the Starter uses its default permit-all implementation.

You now have a working SmartMVC endpoint. The next guide places these capabilities into the complete lifecycle of a request.
