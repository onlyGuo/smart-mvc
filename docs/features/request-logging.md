---
title: 请求日志
description: 了解 Controller 日志类别、摘要内容、输出级别和敏感信息边界。
next:
  text: 认证授权概览
  link: /auth/overview.html
---

# 请求日志

请求日志用于快速回答三个问题：哪个接口被调用、返回了什么状态、处理了多久。

## 默认输出

```text
INFO  com.example.OrderController : HTTP GET /orders/1001 -> 200 (18 ms)
```

摘要包含：

- HTTP 方法；
- 请求 URI 和查询字符串；
- 最终 HTTP 状态码；
- 毫秒耗时。

## 为什么 logger 是 Controller

请求完成后，SmartMVC 读取 Spring MVC 匹配到的 Handler。如果 Handler 是 Controller 方法，就使用 Controller 类作为 logger 类别。

因此可以直接配置某个 Controller 的日志级别：

```yaml
logging:
  level:
    com.example.OrderController: DEBUG
```

如果 Spring 匹配到的 Handler 不是 Controller 方法，例如静态资源处理器，SmartMVC 会使用该 Handler 的实际类作为 logger 类别。只有请求上不存在已匹配的 Handler 时，才使用回退类别 `ink.icoding.mvc.request`。

## 同步与异步请求的边界

对于普通同步 Servlet 请求，日志中的状态码和耗时对应这次请求处理的最终结果。

对于 `Callable`、`DeferredResult`、`WebAsyncTask` 或 `StreamingResponseBody` 等 Spring MVC 异步处理，当前过滤器会在初始 Servlet dispatch 返回时记录日志。此时的状态码和耗时不一定代表异步任务或流式响应真正完成时的结果。若需要精确记录异步完成时间，应在应用中使用 `AsyncListener` 或专门的异步观测组件。

## 配置 SmartMVC 日志级别

```yaml
spring:
  smart:
    mvc:
      request-log:
        enabled: true
        level: INFO
```

支持 `TRACE`、`DEBUG`、`INFO`、`WARN` 和 `ERROR`，默认是 `INFO`。

## 敏感信息边界

默认摘要不会记录：

- 请求 Header；
- 请求体；
- 响应体；
- Controller 返回的业务对象。

但它会记录原始查询字符串。不要把密码、Token、证件号等敏感值放在 URL 参数中，因为 Web 服务器、代理和日志平台都可能保留 URL。

需要业务审计时，应在业务层读取 `CurrentAuth`，只记录必要且经过脱敏的数据。

## 关闭请求日志

```yaml
spring.smart.mvc.request-log.enabled: false
```

关闭后，请求日志过滤器不会注册。
