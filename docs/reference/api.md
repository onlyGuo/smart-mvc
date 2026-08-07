---
title: API 速查
description: SmartMVC 核心注解、身份模型、响应对象和异常的快速参考。
---

# API 速查

## 注解

### `@Auth`

可用于 Controller 类、方法或组合注解。

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read" },
    mode = AuthMode.ANY
)
```

| 成员 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `roles` | `String[]` | `{}` | 所需角色 |
| `permissions` | `String[]` | `{}` | 所需命名权限 |
| `mode` | `AuthMode` | `ALL` | 在角色组和权限组内部使用全部或任意匹配；两个非空分组之间始终为 AND |

### `@Anonymous`

标记无需认证的类或方法。方法级定义优先，并且不会创建当前用户身份。

## `AuthPrincipal<T>`

不可变的当前身份：

| 方法 | 返回值 |
| --- | --- |
| `getId()` | 稳定用户标识 |
| `getUser()` | 应用自己的用户对象，允许为空 |
| `getRoles()` | 不可变角色集合 |
| `getPermissions()` | 不可变权限集合 |
| `getAttributes()` | 不可变扩展属性 Map |
| `getAttribute(name)` | 单个扩展属性 |

构造器会防御性复制集合与 Map。`id` 不能为空。

## `CurrentAuth`

Spring 管理的单例门面，背后使用请求线程上下文：

```java
currentAuth.isAuthenticated();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/42");
```

`requirePrincipal()` 在没有身份时抛出 `IllegalStateException`。通常只在已受 `@Auth` 保护的路径中使用。

## `AuthInterceptor<T>`

必须实现：

```java
AuthPrincipal<T> authenticate(String token, HttpServletRequest request);
```

可选覆写：`resolveToken`、`authorize`、`bind`、`clear` 和 `resolveRequestPath`。应用提供 Bean 后，自动配置的 `PermitAllAuthInterceptor` 会退让。

## `ApiResponse<T>`

字段：`success`、`code`、`message`、`data`、`timestamp`。

```java
ApiResponse.success(data);
ApiResponse.success("created", data);
ApiResponse.failure("ORDER_CLOSED", "Order is already closed");
ApiResponse.failure("INVALID_LINES", "Some lines are invalid", details);
```

## `PageResult<T>`

```java
new PageResult<>(items, total, page, pageSize);
```

字段：`items`、`total`、`page`、`pageSize`；`getTotalPages()` 向上取整计算总页数。

## 异常矩阵

所有异常继承 `SmartMvcException`，且不生成堆栈。

| 异常 | code | HTTP |
| --- | --- | ---: |
| `BadRequestException` | `BAD_REQUEST` | 400 |
| `ParameterValidationException` | `PARAMETER_VALIDATION_FAILED` | 400 |
| `UnauthorizedException` | `UNAUTHORIZED` | 401 |
| `ForbiddenException` | `FORBIDDEN` | 403 |
| `ResourceNotFoundException` | `RESOURCE_NOT_FOUND` | 404 |
| `MethodNotAllowedException` | `METHOD_NOT_ALLOWED` | 405 |
| `NotAcceptableException` | `NOT_ACCEPTABLE` | 406 |
| `RequestTimeoutException` | `REQUEST_TIMEOUT` | 408 |
| `ConflictException` | `CONFLICT` | 409 |
| `GoneException` | `GONE` | 410 |
| `PayloadTooLargeException` | `PAYLOAD_TOO_LARGE` | 413 |
| `UnsupportedMediaTypeException` | `UNSUPPORTED_MEDIA_TYPE` | 415 |
| `BusinessException` | `BUSINESS_ERROR` / 自定义 | 422 |
| `UnprocessableEntityException` | `UNPROCESSABLE_ENTITY` | 422 |
| `LockedException` | `LOCKED` | 423 |
| `TooManyRequestsException` | `TOO_MANY_REQUESTS` | 429 |
| `BusinessExecutionException` | `BUSINESS_EXECUTION_FAILED` | 500 |
| `InternalServerException` | `INTERNAL_SERVER_ERROR` | 500 |
| `NotImplementedException` | `NOT_IMPLEMENTED` | 501 |
| `BadGatewayException` | `BAD_GATEWAY` | 502 |
| `ServiceUnavailableException` | `SERVICE_UNAVAILABLE` | 503 |
| `GatewayTimeoutException` | `GATEWAY_TIMEOUT` | 504 |

`new BusinessException(message)` 使用默认 code `BUSINESS_ERROR`。也可以传入业务 code、消息和 details，表达客户端可以理解并处理的业务拒绝。
