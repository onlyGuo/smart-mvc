---
title: What is SmartMVC?
description: Learn what SmartMVC solves, which capabilities it provides, and how it works with Spring MVC.
prev:
  text: Home
  link: /en/
next:
  text: Quick start
  link: /en/guide/getting-started.html
---

# What is SmartMVC?

SmartMVC is an MVC enhancement framework built on Spring Boot. It gathers the infrastructure that REST applications often implement repeatedly, so controllers can stay focused and API behavior can remain consistent across the application.

Its main capabilities are:

- a consistent response envelope and a reusable pagination model;
- consistent error responses, including request validation errors;
- shared rules for parsing, formatting, and applying time zones to date-time values;
- concise request logs containing the status code and elapsed time;
- an extensible model for authentication, roles, and permissions;
- configuration under `spring.smart.mvc.*`, with metadata for IDE completion.

## How does it relate to Spring MVC?

SmartMVC does not replace Spring MVC. You continue to use familiar annotations such as `@RestController`, `@GetMapping`, `@RequestBody`, and Bean Validation.

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

SmartMVC adds response handling, exception handling, date-time support, request logging, and authentication around this familiar programming model. Your controllers do not need to extend a framework base class, and your domain objects do not need to implement framework interfaces.

## Why are Core and Starter separate?

The project has two main modules:

| Module | Responsibility |
| --- | --- |
| `core` | Annotations, response models, configuration models, identity and permission models, and the exception hierarchy. It has no Spring dependency. |
| `spring-boot-starter-smart-mvc` | Spring Boot configuration binding, auto-configuration, MVC interceptors, Jackson integration, request logging, and exception handling. |

This separation keeps the core concepts independent of a particular web container. Spring-specific behavior stays in the Starter, where an application can replace the extension points it needs.

## Which projects benefit from it?

SmartMVC is intended for Spring Boot applications that want predictable REST API behavior. It is especially useful when:

- several controllers should return the same response shape;
- validation errors and business errors need one stable contract;
- Java date-time formats should be configured in one place;
- authentication and authorization need a small, replaceable integration point;
- the same MVC foundation would otherwise be rebuilt in every project.

## A recommended reading path

If this is your first time using SmartMVC, follow the documentation in this order:

1. [Quick start](./getting-started.md) — run your first endpoint;
2. [How a request moves through SmartMVC](./how-it-works.md) — build a mental model of the framework;
3. read the response, exception, date-time, and logging guides as you need them;
4. move to authentication and authorization when your application needs sign-in;
5. use the configuration and API references when you need exact details.
