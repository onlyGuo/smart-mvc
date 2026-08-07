---
title: 一次请求如何经过 SmartMVC
description: 从请求进入到响应返回，理解 SmartMVC 各组件的职责和执行顺序。
next:
  text: 统一响应
  link: /features/response.html
---

# 一次请求如何经过 SmartMVC

理解执行顺序后，后续配置会更容易判断应该放在哪里。

## 请求处理流程

一次典型请求会经过下面几个阶段：

1. **请求日志开始计时**：过滤器记录开始时间；
2. **Spring MVC 匹配 Handler**：找到 Controller 方法；
3. **认证与授权**：需要保护的接口由 SmartMVC 认证拦截器处理；
4. **参数转换与校验**：日期时间字符串转换为 Java 类型，Bean Validation 校验输入；
5. **执行 Controller**：运行应用自己的业务代码；
6. **整理响应或异常**：普通返回值包装为 `ApiResponse`，异常转为统一错误；
7. **记录请求摘要**：使用实际 Controller 作为 logger 类别，输出状态码和耗时。

## 成功路径

```java
@GetMapping("/users/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

Controller 只返回业务数据。响应增强在方法执行后将其包装为：

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": { "id": "1001", "name": "Ada" },
  "timestamp": "1786005000000"
}
```

## 失败路径

```java
throw new ResourceNotFoundException("User 1001 was not found");
```

全局异常处理器读取异常中的 HTTP 状态、错误码和消息，默认返回 HTTP 404：

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## 哪些部分可以替换

SmartMVC 使用 Spring Boot 的条件装配机制。应用可以：

- 关闭统一响应或异常处理；
- 提供自己的 `AuthInterceptor`；
- 提供自己的 `CurrentAuth` 或相关组件；
- 调整日期格式、时区和请求日志级别；
- 直接返回 `ApiResponse`，精确控制某个接口的响应。

框架提供默认实现，但业务规则仍由应用决定。接下来从最常用的[统一响应](../features/response.md)开始逐项了解。

