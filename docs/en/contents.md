---
title: All documentation
description: Explore SmartMVC from the fundamentals through core features, authentication, a complete example, and reference material.
sidebar: false
prev:
  text: Home
  link: /en/
next:
  text: What is SmartMVC?
  link: /en/guide/introduction.html
---

# All documentation

This is the complete map of the SmartMVC documentation. You do not need to read everything at once. If SmartMVC is new to you, begin with “Getting started” and follow the chapters in order. If you are already using it, go directly to the feature you need. Later chapters become more detailed and are useful while implementing or troubleshooting an application.

## Getting started

Learn what SmartMVC is for, add it to an application, and see where it participates in a request.

1. [What is SmartMVC?](./guide/introduction.html)  
   Understand SmartMVC’s responsibilities, the capabilities it provides, and its relationship with Spring MVC.
2. [Quick start](./guide/getting-started.html)  
   Declare the application dependencies, add SmartMVC, and run your first endpoint without extra startup configuration.
3. [How a request moves through SmartMVC](./guide/how-it-works.html)  
   Follow a request from arrival to response and see how the enhancement layers work together.

## Core features

Learn the response, exception, time, and logging features that most web applications use directly.

4. [Consistent responses](./features/response.html)  
   Use a stable success envelope, pagination model, and response-wrapping rules.
5. [Exceptions and request validation](./features/exceptions.html)  
   Return business errors, HTTP errors, and field validation details in a clear, predictable format.
6. [Date and time](./features/date-time.html)  
   Keep date-time input, output, time zones, and missing-part completion consistent.
7. [Request logging](./features/request-logging.html)  
   Record the request path, status, duration, and controller source, with a configurable log level.

## Authentication and authorization

Start with the authentication model, then connect your own users, roles, permissions, and token handling.

8. [Authentication and authorization](./auth/overview.html)  
   Understand full authentication, annotation-based authentication, anonymous access, and excluded paths.
9. [Custom authentication and the current user](./auth/custom-authentication.html)  
   Implement the authentication handler and safely access the current user and permissions in application code.

## Complete example

10. [Sign-in and authorization example](./examples/login.html)  
    Put sign-in, token parsing, the current user, and endpoint authorization together in one complete flow.

## Reference

Use these pages when you need to confirm a specific option, type, or method.

11. [Configuration reference](./reference/configuration.html)  
    Find every `spring.smart.mvc.*` property, its default value, and guidance for using it.
12. [API reference](./reference/api.html)  
    Look up common annotations, response models, exception types, authentication interfaces, and current-session APIs.
