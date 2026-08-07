---
title: 异常与参数校验
description: 使用 SmartMvcException、HTTP 状态模式和字段校验详情构建一致的错误响应。
---

# 异常与参数校验

统一异常处理的目标是让客户端收到稳定的错误结构，同时让服务端保留足够的诊断信息。

## 抛出 SmartMVC 异常

```java
public UserView get(Long id) {
    return repository.findById(id)
            .map(UserView::from)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User " + id + " was not found"));
}
```

默认响应状态为 HTTP 404，响应体为：

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## 统一父类

所有 SmartMVC 异常都继承 `SmartMvcException`。父类保存：

- HTTP 状态码；
- 业务错误码；
- 错误消息；
- 可选的详情数据；
- 可选的原始异常。

这些异常不会生成堆栈，也关闭了 suppression，适合表达预期内的业务拒绝和 HTTP 失败。未知程序错误仍由全局处理器记录完整堆栈，并向客户端返回安全的 500 响应。

## 常用异常

| 场景 | 异常 | HTTP |
| --- | --- | ---: |
| 请求参数或内容不合法 | `BadRequestException` | 400 |
| 身份认证失败 | `UnauthorizedException` | 401 |
| 已登录但权限不足 | `ForbiddenException` | 403 |
| 资源不存在 | `ResourceNotFoundException` | 404 |
| 当前状态与操作冲突 | `ConflictException` | 409 |
| 业务规则不满足 | `BusinessException` | 422 |
| 请求次数过多 | `TooManyRequestsException` | 429 |
| 业务执行发生内部错误 | `BusinessExecutionException` | 500 |
| 下游服务暂时不可用 | `ServiceUnavailableException` | 503 |

完整列表见 [API 参考](../reference/api.md)。

## 自定义业务错误码

`BusinessException` 适合客户端可以识别并处理的业务失败：

```java
throw new BusinessException(
        "ORDER_ALREADY_PAID",
        "The order has already been paid",
        Map.of("orderId", orderId)
);
```

它默认对应 HTTP 422，`details` 会进入响应的 `data` 字段。

## Bean Validation

Bean Validation 的 API 与实现由应用负责提供。请在应用中显式添加 `spring-boot-starter-validation`；SmartMVC 不会传递校验 Provider，只会接入 Spring MVC 产生的校验结果，并将其转换为统一的错误响应。

```java
public record CreateUserRequest(
        @NotBlank String username,
        @Email String email
) {
}

@PostMapping
public UserView create(@Valid @RequestBody CreateUserRequest request) {
    return userService.create(request);
}
```

校验失败时，SmartMVC 返回 `PARAMETER_VALIDATION_FAILED`，并在 `data` 中提供字段详情：

```json
{
  "success": false,
  "code": "PARAMETER_VALIDATION_FAILED",
  "message": "Request validation failed",
  "data": [
    {
      "field": "email",
      "rejectedValue": "invalid",
      "message": "must be a well-formed email address"
    }
  ],
  "timestamp": "1786005000000"
}
```

客户端可以据此把错误信息展示在对应字段旁边。

## 常见 Spring MVC 异常

全局处理器还会转换：

- 缺少请求参数、类型转换失败和无法读取 JSON：HTTP 400；
- 找不到静态或 MVC 资源：HTTP 404；
- 请求方法不支持：HTTP 405；
- Content-Type 不支持：HTTP 415；
- 方法参数校验失败：HTTP 400。

## HTTP 状态模式

推荐保留真实 HTTP 状态：

```yaml
spring.smart.mvc.exception.status-mode: HTTP_STATUS
```

兼容必须使用 HTTP 200 的既有协议时，可设置：

```yaml
spring.smart.mvc.exception.status-mode: ALWAYS_OK
```

此时错误码、消息和详情仍保留在响应体中。新项目通常应使用 `HTTP_STATUS`。

## 关闭默认异常处理

```yaml
spring.smart.mvc.exception.enabled: false
```

关闭后可完全使用应用自己的 `@RestControllerAdvice`。
