---
title: 配置参考
description: spring.smart.mvc 下所有配置项、类型、默认值与行为说明。
prev:
  text: 登录鉴权示例
  link: /examples/login.html
---

# 配置参考

所有属性以 `spring.smart.mvc` 为前缀。表格中的默认值来自 `core` 配置模型，并通过 Starter 的 Spring Boot 配置元数据暴露给 IDE。

## exception

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `exception.enabled` | `boolean` | `true` | 注册 SmartMVC 全局异常处理器 |
| `exception.status-mode` | `HTTP_STATUS \| ALWAYS_OK` | `HTTP_STATUS` | 使用真实 HTTP 状态，或将错误传输状态统一为 200 |

## response

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `response.wrap-enabled` | `boolean` | `true` | 包装普通 Controller 返回值 |
| `response.wrap-void` | `boolean` | `true` | 包装 `void` / `Void` 返回值 |
| `response.success-message` | `String` | `success` | 自动成功响应的 `message` |
| `response.long-as-string` | `boolean` | `true` | 将 `long` / `Long` 序列化为字符串 |

`ApiResponse`、`byte[]`、Spring `Resource`、`StreamingResponseBody` 和 `ProblemDetail` 不会被自动包装。

## date-time

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `date-time.request-format` | `String` | `yyyy-MM-dd HH:mm:ss` | 日期时间类的请求格式 |
| `date-time.response-format` | `String` | `yyyy-MM-dd HH:mm:ss` | 日期时间类的 JSON 输出格式 |
| `date-time.date-request-format` | `String` | `yyyy-MM-dd` | `LocalDate` 请求格式 |
| `date-time.date-response-format` | `String` | `yyyy-MM-dd` | `LocalDate` 输出格式 |
| `date-time.time-request-format` | `String` | `HH:mm:ss` | `LocalTime` 请求格式 |
| `date-time.time-response-format` | `String` | `HH:mm:ss` | `LocalTime` 输出格式 |
| `date-time.zone-id` | `String` | `system-default` | Instant 类和 `Date` 转换使用的 IANA 时区 |
| `date-time.incomplete-input-policy` | `FILL_MISSING \| REJECT` | `FILL_MISSING` | 补全或拒绝不完整日期时间输入 |

支持的类型包括 `LocalDateTime`、`LocalDate`、`LocalTime`、`Instant`、`OffsetDateTime`、`ZonedDateTime` 和 `java.util.Date`；后四者输出时应用配置时区。

## validation

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `validation.enabled` | `boolean` | `true` | 允许 SmartMVC 接入并处理 Spring MVC 的参数校验结果；关闭时使用 no-op validator |

此开关不会安装 Bean Validation 实现。应用必须显式添加 `spring-boot-starter-validation`，并由应用管理它的版本；SmartMVC 不会传递校验 Provider。

## request-log

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `request-log.enabled` | `boolean` | `true` | 输出方法、URI、状态码与耗时 |
| `request-log.level` | `TRACE \| DEBUG \| INFO \| WARN \| ERROR` | `INFO` | 摘要日志级别 |

有匹配 Controller 时，logger 类别为该 Controller 类。

## auth

| 属性 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `auth.enabled` | `boolean` | `true` | 启用 SmartMVC 认证与授权检查；拦截器本身仍会注册 |
| `auth.mode` | `GLOBAL \| ANNOTATED` | `ANNOTATED` | 全局认证，或只认证 `@Auth` 接口 |
| `auth.check-request-permission` | `boolean` | `false` | 校验身份中的 `METHOD:PATH` 权限 |
| `auth.authorization-header` | `String` | `Authorization` | 读取凭据的请求头 |
| `auth.token-prefix` | `String` | `Bearer` | 从请求头值中移除的前缀 |
| `auth.exclude-paths` | `String[]` | `[]` | 完全跳过认证的 Spring MVC 路径模式 |

`@Anonymous` 在两种模式下都会跳过认证。`exclude-paths` 也始终生效；在 `ANNOTATED` 模式下，被排除路径即使带有 `@Auth` 也不会进入认证拦截。除此之外，`ANNOTATED` 只处理带 `@Auth` 的 Handler。

## 完整 YAML

```yaml
spring:
  smart:
    mvc:
      exception:
        enabled: true
        status-mode: HTTP_STATUS
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
      date-time:
        request-format: yyyy-MM-dd HH:mm:ss
        response-format: yyyy-MM-dd HH:mm:ss
        date-request-format: yyyy-MM-dd
        date-response-format: yyyy-MM-dd
        time-request-format: HH:mm:ss
        time-response-format: HH:mm:ss
        zone-id: system-default
        incomplete-input-policy: FILL_MISSING
      validation:
        enabled: true
      request-log:
        enabled: true
        level: INFO
      auth:
        enabled: true
        mode: ANNOTATED
        check-request-permission: false
        authorization-header: Authorization
        token-prefix: Bearer
        exclude-paths: []
```
