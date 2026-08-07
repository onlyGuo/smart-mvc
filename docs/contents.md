---
title: 全部文档
description: 按照由浅入深的顺序浏览 SmartMVC 的入门指南、核心功能、认证授权、完整示例与参考手册。
sidebar: false
prev:
  text: 首页
  link: /
next:
  text: 认识 SmartMVC
  link: /guide/introduction.html
---

# 全部文档

这里是 SmartMVC 的完整文档地图。你不必一次读完所有内容：第一次接触 SmartMVC 时，建议从“入门”开始顺序阅读；已经在项目中使用时，可以直接进入对应的功能章节。越往后，内容会越具体，也更适合在实现和排查问题时查阅。

## 入门

先了解 SmartMVC 解决什么问题，再完成第一次接入，并看懂它如何参与一次请求。

1. [认识 SmartMVC](./guide/introduction.html)  
   了解 SmartMVC 的职责、提供的能力，以及它与 Spring MVC 的关系。
2. [快速开始](./guide/getting-started.html)  
   显式引入应用所需依赖与 SmartMVC，并运行第一个接口；无需为了启动额外编写配置。
3. [一次请求如何经过 SmartMVC](./guide/how-it-works.html)  
   从请求进入到响应返回，认识各项增强能力的执行位置与配合方式。

## 核心功能

掌握大多数 Web 项目会直接使用的响应、异常、时间和日志能力。

4. [统一响应](./features/response.html)  
   使用一致的成功响应、分页结果和响应包装规则。
5. [异常与参数校验](./features/exceptions.html)  
   让业务异常、HTTP 异常与字段校验错误以清晰、稳定的格式返回。
6. [日期与时间](./features/date-time.html)  
   统一日期时间的输入、输出、时区和缺失部分补全策略。
7. [请求日志](./features/request-logging.html)  
   记录请求路径、状态码、耗时与 Controller 来源，并按需要调整日志级别。

## 认证授权

从认证模型入门，再进一步接入自己的用户、角色、权限与 Token 逻辑。

8. [认证授权概览](./auth/overview.html)  
   理解全量认证、按注解认证、匿名访问和路径排除等基本规则。
9. [自定义认证与当前用户](./auth/custom-authentication.html)  
   实现认证处理器，并在业务代码中安全地获取当前用户及其权限信息。

## 完整示例

10. [登录鉴权示例](./examples/login.html)  
    把登录、Token 解析、当前用户和接口权限检查串成一条完整流程。

## 参考手册

当你需要确认某个选项、类型或方法时，可以从这里快速定位。

11. [配置参考](./reference/configuration.html)  
    查阅 `spring.smart.mvc.*` 下的全部配置项、默认值与使用说明。
12. [API 速查](./reference/api.html)  
    查阅常用注解、响应模型、异常类型、认证接口和当前会话 API。
