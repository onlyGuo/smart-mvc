---
title: 登录鉴权示例
description: 运行 Example 模块，验证匿名访问、登录、当前用户、角色和请求权限。
prev:
  text: 自定义认证与当前用户
  link: /auth/custom-authentication.html
next:
  text: 配置参考
  link: /reference/configuration.html
---

# 登录鉴权示例

`spring-boot-starter-smart-mvc-example` 提供了一个完整的内存认证示例。它不依赖外部服务，目的是清楚展示 SmartMVC 的接入方式和请求结果。

示例使用固定账户、明文示例密码和固定 Token，只用于本地学习与自动化测试。

## 启动 Example

先在仓库根目录安装所有模块：

```bash
mvn install -DskipTests
```

再启动 Example：

```bash
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

## 示例账户

| 用户名 | 密码 | Token | 角色 |
| --- | --- | --- | --- |
| `admin` | `admin123` | `example-admin-token` | `admin` |
| `user` | `user123` | `example-user-token` | `user` |

## 1. 访问匿名接口

```bash
curl http://localhost:8080/auth/public
```

接口带有 `@Anonymous`。响应数据中的 `authenticated` 为 `false`，说明匿名接口不会创建当前身份。

## 2. 登录并获取 Token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

`data` 中返回：

```json
{
  "tokenType": "Bearer",
  "token": "example-admin-token"
}
```

## 3. 读取当前身份

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

返回的 `AuthPrincipal` 包含：

- 用户 ID 和 `ExampleUser`；
- `admin` 角色；
- `admin:read` 等命名权限；
- `GET:/auth/**` 等请求权限；
- `authenticationType` 扩展属性。

不提供 Token 或提供无效 Token 时，接口返回 HTTP 401。

## 4. 验证管理员接口

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-admin-token'
```

这个接口同时检查：

1. 用户已经通过认证；
2. 拥有 `admin` 角色；
3. 拥有 `admin:read` 命名权限；
4. 拥有匹配当前请求的 `GET:/auth/**` 权限。

使用普通用户 Token 请求相同接口：

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-user-token'
```

返回 HTTP 403，错误码为 `FORBIDDEN`。

## 示例代码的职责

| 类 | 职责 |
| --- | --- |
| `ExampleAuthController` | 提供登录、匿名、当前身份和管理员接口 |
| `ExampleAuthenticationService` | 保存内存账户并完成密码与 Token 查询 |
| `ExampleAuthInterceptor` | 将 Token 服务接入 SmartMVC |
| `ExampleUser` | 演示 `CurrentAuth` 的类型化用户读取 |

集成测试 `ExampleAuthenticationIntegrationTest` 覆盖匿名访问、登录、未认证、权限不足和管理员访问成功等路径。

