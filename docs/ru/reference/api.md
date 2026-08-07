---
title: Краткий справочник API
description: Основные аннотации, модель текущего пользователя, объекты ответов и исключения SmartMVC.
prev:
  text: Справочник настроек
  link: /ru/reference/configuration.html
---

# Краткий справочник API

## Аннотации

### `@Auth`

Может применяться к классу контроллера, методу или составной аннотации.

```java
@Auth(
    roles = { "admin", "auditor" },
    permissions = { "report:read" },
    mode = AuthMode.ANY
)
```

| Член | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `roles` | `String[]` | `{}` | Необходимые роли |
| `permissions` | `String[]` | `{}` | Необходимые именованные разрешения |
| `mode` | `AuthMode` | `ALL` | Требовать все или любое из нескольких значений отдельно для ролей и разрешений |

### `@Anonymous`

Помечает класс или метод, для которого аутентификация не требуется. Определение на методе имеет приоритет; данные текущего пользователя не создаются.

## `AuthPrincipal<T>`

Неизменяемые данные аутентифицированного пользователя:

| Метод | Возвращаемое значение |
| --- | --- |
| `getId()` | Стабильный идентификатор пользователя |
| `getUser()` | Пользовательский объект приложения; может быть `null` |
| `getRoles()` | Неизменяемый набор ролей |
| `getPermissions()` | Неизменяемый набор разрешений |
| `getAttributes()` | Неизменяемая Map дополнительных атрибутов |
| `getAttribute(name)` | Отдельный дополнительный атрибут |

Конструктор защитно копирует наборы и Map. Значение `id` не может быть пустым.

## `CurrentAuth`

Синглтон-фасад под управлением Spring, использующий контекст потока запроса:

```java
currentAuth.isAuthenticated();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/42");
```

Если текущий пользователь не установлен, `requirePrincipal()` выбрасывает `IllegalStateException`. Обычно его вызывают только на пути, уже защищённом `@Auth`.

## `AuthInterceptor<T>`

Необходимо реализовать:

```java
AuthPrincipal<T> authenticate(String token, HttpServletRequest request);
```

Дополнительно можно переопределить `resolveToken`, `authorize`, `bind`, `clear` и `resolveRequestPath`. Когда приложение предоставляет такой бин, автоматически настроенный `PermitAllAuthInterceptor` перестаёт применяться.

## `ApiResponse<T>`

Поля: `success`, `code`, `message`, `data`, `timestamp`.

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

Поля: `items`, `total`, `page`, `pageSize`. Метод `getTotalPages()` вычисляет общее число страниц с округлением вверх.

## Матрица исключений

Все исключения наследуют `SmartMvcException` и не создают стек вызовов.

| Исключение | code | HTTP |
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
| `BusinessException` | `BUSINESS_ERROR` / пользовательский | 422 |
| `UnprocessableEntityException` | `UNPROCESSABLE_ENTITY` | 422 |
| `LockedException` | `LOCKED` | 423 |
| `TooManyRequestsException` | `TOO_MANY_REQUESTS` | 429 |
| `BusinessExecutionException` | `BUSINESS_EXECUTION_FAILED` | 500 |
| `InternalServerException` | `INTERNAL_SERVER_ERROR` | 500 |
| `NotImplementedException` | `NOT_IMPLEMENTED` | 501 |
| `BadGatewayException` | `BAD_GATEWAY` | 502 |
| `ServiceUnavailableException` | `SERVICE_UNAVAILABLE` | 503 |
| `GatewayTimeoutException` | `GATEWAY_TIMEOUT` | 504 |

`new BusinessException(message)` использует код по умолчанию `BUSINESS_ERROR`. При необходимости можно передать собственный бизнес-код, сообщение и `details` для ожидаемого отказа, который клиент умеет распознать и обработать.
