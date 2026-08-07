---
title: 统一响应
description: 理解 ApiResponse、自动包装、跳过包装的类型和分页结果。
prev:
  text: 一次请求如何经过 SmartMVC
  link: /guide/how-it-works.html
---

# 统一响应

统一响应让客户端可以用同一套规则判断请求是否成功、读取数据和显示消息。

## 响应结构

`ApiResponse<T>` 包含五个字段：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| `success` | `boolean` | 请求是否按预期完成 |
| `code` | `String` | 机器可读的结果码 |
| `message` | `String` | 面向使用者的说明 |
| `data` | `T` | 业务数据或错误详情 |
| `timestamp` | `long` | 创建响应时的毫秒时间戳 |

## 自动包装

启用 `response.wrap-enabled` 后，普通 Controller 返回值会自动进入成功响应：

```java
@GetMapping("/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

无需在每个方法中手工调用 `ApiResponse.success(...)`。

`String` 返回值也能正确包装。即使 Spring 选择了 `StringHttpMessageConverter`，SmartMVC 仍会输出 JSON，而不是把对象当普通字符串写出。

## 不会自动包装的类型

下面这些返回值会保持原样：

- 已经是 `ApiResponse` 的对象；
- `byte[]`；
- Spring `Resource`；
- `StreamingResponseBody`；
- `ProblemDetail`。

这些类型通常用于文件、二进制内容、流式响应或标准问题详情，不适合再套一层 JSON 响应体。

## 手工创建响应

需要自定义消息或错误码时，可以直接使用工厂方法：

```java
return ApiResponse.success("User created", userView);
```

```java
return ApiResponse.failure(
        "ORDER_CLOSED",
        "The order is already closed",
        Map.of("orderId", orderId)
);
```

直接返回 `ApiResponse` 时，响应增强不会再次包装。

## `void` 返回值

默认情况下，`void` 和 `Void` 会包装成 `data: null` 的成功响应。

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-void: false
```

关闭后，`void` 返回值不再生成统一响应体。

## 分页结果

`PageResult<T>` 是一个不依赖数据库或分页插件的通用模型：

```java
PageResult<UserView> result = new PageResult<>(
        users,
        total,
        page,
        pageSize
);
```

它提供：

- `items`：当前页数据；
- `total`：总记录数；
- `page`：当前页码；
- `pageSize`：每页数量；
- `totalPages`：根据总数和每页数量计算的总页数。

## Long 为什么默认输出为字符串

JavaScript 无法精确表示所有 64 位整数。默认配置会把 `long` 和 `Long` 序列化为字符串：

```yaml
spring.smart.mvc.response.long-as-string: true
```

如果所有客户端都能安全处理 64 位整数，可以将其关闭。

## 配置项

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
```

关闭 `wrap-enabled` 后，自动响应增强 Bean 不会注册，Controller 返回值由 Spring MVC 原样处理。

