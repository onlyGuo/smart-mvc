---
title: 认证授权概览
description: 理解认证模式、@Auth、@Anonymous、角色和权限的基本规则。
prev:
  text: 请求日志
  link: /features/request-logging.html
---

# 认证授权概览

SmartMVC 定义认证授权的接入方式和请求生命周期，但不规定用户必须存在哪里，也不负责签发具体类型的 Token。

应用负责：

- 校验 Token、Cookie 或 Session；
- 查询用户、角色和权限；
- 决定账户是否可用；
- 提供自己的 `AuthInterceptor` 实现。

SmartMVC 负责：

- 判断当前接口是否需要认证；
- 从请求中提取凭据；
- 调用应用的认证实现；
- 校验注解和请求权限；
- 将身份绑定到 `CurrentAuth`；
- 请求结束后清理身份。

## 两种认证模式

### ANNOTATED

```yaml
spring.smart.mvc.auth.mode: ANNOTATED
```

只认证带有 `@Auth` 的 Controller 或方法。没有认证注解的接口保持公开。

适合：

- 逐步为既有项目增加认证；
- 公开接口较多的服务；
- 希望每个受保护接口都显式可见。

### GLOBAL

```yaml
spring.smart.mvc.auth.mode: GLOBAL
```

默认认证所有 Controller 方法，只有 `@Anonymous` 和 `exclude-paths` 跳过认证。

适合：

- 后台管理系统；
- 大部分接口都需要登录的服务；
- 希望采用默认保护策略的应用。

## `@Auth`

只要求登录：

```java
@Auth
@GetMapping("/profile")
public ProfileView profile() {
    // ...
}
```

同时要求角色和命名权限：

```java
@Auth(
    roles = "admin",
    permissions = "user:read"
)
@GetMapping("/users")
public List<UserView> users() {
    // ...
}
```

`@Auth` 可以放在类、方法或组合注解上。

## `@Anonymous`

```java
@Anonymous
@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    // ...
}
```

匿名接口会跳过认证、授权和身份绑定。因此在默认行为下：

- `currentAuth.isAuthenticated()` 返回 `false`；
- `currentAuth.getUser()` 返回 `null`；
- 角色和权限集合为空。

方法上的 `@Auth` 或 `@Anonymous` 优先于类上的声明，可以用来覆盖 Controller 的默认规则。

## 排除路径

```yaml
spring:
  smart:
    mvc:
      auth:
        exclude-paths:
          - /actuator/health
          - /assets/**
```

排除路径在 `GLOBAL` 和 `ANNOTATED` 模式下都生效。被排除的路径不会进入 SmartMVC 认证拦截器，即使 Handler 上存在 `@Auth`。

## 多个角色和权限

默认 `AuthMode.ALL` 要求每个声明值都存在：

```java
@Auth(
    roles = { "admin", "operator" },
    permissions = { "user:read", "user:update" }
)
```

`AuthMode.ANY` 要求在每个非空分组中至少命中一个值：

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read", "report:export" },
    mode = AuthMode.ANY
)
```

上面的规则是“至少一个角色”并且“至少一个权限”。角色组和权限组之间始终是 AND。

## 默认认证实现

没有应用自定义 `AuthInterceptor` 时，Starter 注册 `PermitAllAuthInterceptor`。它创建拥有通配角色 `*` 和权限 `*:*` 的身份，使空项目和功能演示能够直接运行。

生产项目需要真实认证时，必须提供自己的实现。下一节将完整实现这一过程。

