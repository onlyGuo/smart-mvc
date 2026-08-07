---
title: Исключения и проверка параметров
description: SmartMvcException, режимы HTTP-статуса и подробности ошибок проверки полей.
prev:
  text: Единый формат ответа
  link: /ru/features/response.html
next:
  text: Дата и время
  link: /ru/features/date-time.html
---

# Исключения и проверка параметров

Единая обработка исключений даёт клиенту устойчивую структуру ошибок и при этом сохраняет на сервере сведения, необходимые для диагностики.

## Исключения SmartMVC

```java
public UserView get(Long id) {
    return repository.findById(id)
            .map(UserView::from)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User " + id + " was not found"));
}
```

По умолчанию ответ получит статус HTTP 404 и следующее тело:

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## Общий базовый класс

Все исключения SmartMVC наследуют `SmartMvcException`. Базовый класс хранит:

- HTTP-статус;
- бизнес-код ошибки;
- текст ошибки;
- необязательные дополнительные данные;
- необязательное исходное исключение.

Эти исключения не создают стек вызовов, а подавление исключений для них отключено. Они предназначены для ожидаемых отказов бизнес-логики и HTTP-ошибок. Неизвестные программные ошибки по-прежнему записываются глобальным обработчиком с полным стеком, а клиент получает безопасный ответ 500.

## Часто используемые исключения

| Ситуация | Исключение | HTTP |
| --- | --- | ---: |
| Некорректные параметры или содержимое запроса | `BadRequestException` | 400 |
| Не удалось аутентифицировать пользователя | `UnauthorizedException` | 401 |
| Пользователь вошёл, но не имеет нужных прав | `ForbiddenException` | 403 |
| Ресурс не найден | `ResourceNotFoundException` | 404 |
| Операция конфликтует с текущим состоянием | `ConflictException` | 409 |
| Не выполнено бизнес-правило | `BusinessException` | 422 |
| Слишком много запросов | `TooManyRequestsException` | 429 |
| Внутренняя ошибка выполнения бизнес-операции | `BusinessExecutionException` | 500 |
| Зависимый сервис временно недоступен | `ServiceUnavailableException` | 503 |

Полный перечень приведён в [справочнике API](../reference/api.md).

## Собственный бизнес-код ошибки

`BusinessException` подходит для бизнес-ошибок, которые клиент может распознать и обработать:

```java
throw new BusinessException(
        "ORDER_ALREADY_PAID",
        "The order has already been paid",
        Map.of("orderId", orderId)
);
```

По умолчанию исключение соответствует HTTP 422, а значение `details` попадает в поле `data` ответа.

## Bean Validation

API и реализацию Bean Validation предоставляет приложение. Явно добавьте в приложение `spring-boot-starter-validation`: SmartMVC не подключает Provider валидации как транзитивную зависимость. Он только получает результаты проверки от Spring MVC и преобразует их в единый ответ об ошибке.

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

Если проверка не пройдена, SmartMVC возвращает код `PARAMETER_VALIDATION_FAILED` и добавляет в `data` сведения о полях:

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

Клиент может использовать эти данные, чтобы показать сообщение рядом с соответствующим полем формы.

## Распространённые исключения Spring MVC

Глобальный обработчик также преобразует:

- отсутствие параметра, ошибку преобразования типа и нечитаемый JSON — в HTTP 400;
- отсутствие статического ресурса или MVC-маршрута — в HTTP 404;
- неподдерживаемый метод запроса — в HTTP 405;
- неподдерживаемый Content-Type — в HTTP 415;
- ошибку проверки аргументов метода — в HTTP 400.

## Режим HTTP-статуса

Рекомендуется сохранять настоящий HTTP-статус:

```yaml
spring.smart.mvc.exception.status-mode: HTTP_STATUS
```

Для совместимости с существующим протоколом, который требует всегда отвечать HTTP 200, можно указать:

```yaml
spring.smart.mvc.exception.status-mode: ALWAYS_OK
```

Код, сообщение и подробности ошибки при этом остаются в теле ответа. Для новых проектов обычно следует выбирать `HTTP_STATUS`.

## Отключение стандартного обработчика

```yaml
spring.smart.mvc.exception.enabled: false
```

После отключения приложение может полностью использовать собственный `@RestControllerAdvice`.
