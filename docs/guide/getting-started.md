---
title: 快速开始
description: 准备 Spring MVC 应用依赖，添加 SmartMVC，并创建第一个接口。
---

# 快速开始

这一节只完成三件事：准备应用所需的 Web 与校验依赖、额外加入 SmartMVC、查看第一个接口的统一响应。

## 环境要求

- Java 17 或更高版本；
- Spring Boot 3.2 或更高版本（当前构建与测试基线为 3.5.7）；
- Maven 项目。

## 1. 添加应用依赖

```xml
<!-- 由应用提供：Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- 由应用提供：Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- 在现有 Spring MVC 应用上增加 SmartMVC 能力 -->
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>spring-boot-starter-smart-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

SmartMVC 不会传递 Spring Web 或 Bean Validation。应用需要显式声明前两个依赖，它们的版本由应用自己的 Spring Boot Parent 或 BOM 统一管理，因此 SmartMVC 不会锁定应用使用的 Spring Web 与 Bean Validation 版本。

## 2. 创建接口

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

## 3. 启动并访问

```bash
mvn spring-boot:run
```

请求接口：

```bash
curl http://localhost:8080/hello
```

默认响应如下：

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

SmartMVC 完成了两项处理：

1. 将 Controller 返回的 `Greeting` 包装为 `ApiResponse`；
2. 按默认格式 `yyyy-MM-dd HH:mm:ss` 输出 `LocalDateTime`。

`timestamp` 默认序列化为字符串，以避免 JavaScript 长整数精度丢失。

## 4. 做一个最小配置

所有配置都位于 `spring.smart.mvc` 下。下面只修改成功消息和时区：

```yaml
spring:
  smart:
    mvc:
      response:
        success-message: ok
      date-time:
        zone-id: Asia/Shanghai
```

Starter 自带 Spring Boot 配置元数据，IDE 可以补全属性、默认值和枚举选项。

## 默认启用了什么

在没有额外配置时：

- 普通 Controller 返回值会被统一包装；
- `void` 返回值会得到成功响应；
- 常见 MVC 异常会得到统一错误结构；
- 参数校验启用；
- 请求摘要日志使用 `INFO` 级别；
- 认证模式为 `ANNOTATED`，只处理带 `@Auth` 的接口；
- 未提供自定义认证实现时，Starter 使用默认放行实现。

快速开始到这里已经完成。下一节会把这些能力放进一次完整的请求流程中说明。
