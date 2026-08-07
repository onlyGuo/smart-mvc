---
title: 认识 SmartMVC
description: 了解 SmartMVC 解决的问题、提供的能力以及它与 Spring MVC 的关系。
prev:
  text: 首页
  link: /
---

# 认识 SmartMVC

SmartMVC 是一个基于 Spring Boot 的 MVC 增强框架。它整理了 Web 项目中经常重复实现的基础能力，让 Controller 保持简单，让接口行为保持一致。

它主要提供：

- 统一的成功响应与分页模型；
- 统一的异常响应和参数校验错误；
- 一致的日期时间解析、格式化与时区处理；
- 包含状态码和耗时的请求摘要日志；
- 可扩展的认证、角色与权限模型；
- `spring.smart.mvc.*` 配置项及 IDE 补全信息。

## 它和 Spring MVC 是什么关系

SmartMVC 不会替代 Spring MVC。你仍然使用熟悉的 `@RestController`、`@GetMapping`、`@RequestBody` 和 Bean Validation。

```java
@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public UserView get(@PathVariable Long id) {
        return userService.get(id);
    }
}
```

SmartMVC 在这套编程方式之外补充统一响应、异常、时间、日志和鉴权。你不需要继承框架基类，也不需要改变业务对象。

## 为什么分为 Core 和 Starter

项目由两个主要模块组成：

| 模块 | 职责 |
| --- | --- |
| `core` | 注解、响应模型、配置模型、身份与权限模型、异常体系；不依赖 Spring |
| `spring-boot-starter-smart-mvc` | Spring Boot 配置绑定、自动配置、MVC 拦截器、Jackson、日志和异常处理 |

这样设计后，核心概念不会和具体 Web 容器耦合；Spring 相关逻辑则集中在 Starter 中，应用仍可替换关键扩展点。

## 适合哪些项目

SmartMVC 适合希望统一 REST API 行为的 Spring Boot 项目，尤其是：

- 多个 Controller 需要遵循同一响应结构；
- 希望统一处理校验错误和业务异常；
- 需要集中配置 Java 日期时间格式；
- 需要简单、可替换的认证授权入口；
- 不想在每个新项目中重复搭建相同的 MVC 基础设施。

## 接下来怎么读

第一次使用时，建议按照下面的顺序：

1. [快速开始](./getting-started.md)：运行第一个接口；
2. [一次请求如何经过 SmartMVC](./how-it-works.md)：建立整体认识；
3. 按需阅读响应、异常、日期时间和日志；
4. 需要登录鉴权时，再进入认证授权章节；
5. 最后使用配置与 API 参考查找细节。
