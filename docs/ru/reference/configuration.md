---
title: Справочник настроек
description: Все свойства пространства spring.smart.mvc, их типы, значения по умолчанию и поведение.
prev:
  text: Пример входа и проверки доступа
  link: /ru/examples/login.html
next:
  text: Краткий справочник API
  link: /ru/reference/api.html
---

# Справочник настроек

Все свойства имеют префикс `spring.smart.mvc`. Значения по умолчанию в таблицах взяты из моделей конфигурации модуля `core` и доступны IDE через метаданные Spring Boot, включённые в Starter.

## exception

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `exception.enabled` | `boolean` | `true` | Регистрирует глобальный обработчик исключений SmartMVC |
| `exception.status-mode` | `HTTP_STATUS \| ALWAYS_OK` | `HTTP_STATUS` | Использует настоящий HTTP-статус или всегда передаёт ошибки со статусом 200 |

## response

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `response.wrap-enabled` | `boolean` | `true` | Оборачивает обычные результаты контроллеров |
| `response.wrap-void` | `boolean` | `true` | Оборачивает результаты `void` / `Void` |
| `response.success-message` | `String` | `success` | Значение `message` в автоматически созданном успешном ответе |
| `response.long-as-string` | `boolean` | `true` | Сериализует `long` / `Long` как строки |

`ApiResponse`, `byte[]`, Spring `Resource`, `StreamingResponseBody` и `ProblemDetail` не оборачиваются автоматически.

## date-time

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `date-time.request-format` | `String` | `yyyy-MM-dd HH:mm:ss` | Формат даты и времени во входящем запросе |
| `date-time.response-format` | `String` | `yyyy-MM-dd HH:mm:ss` | Формат даты и времени в исходящем JSON |
| `date-time.date-request-format` | `String` | `yyyy-MM-dd` | Формат `LocalDate` во входящем запросе |
| `date-time.date-response-format` | `String` | `yyyy-MM-dd` | Формат `LocalDate` в ответе |
| `date-time.time-request-format` | `String` | `HH:mm:ss` | Формат `LocalTime` во входящем запросе |
| `date-time.time-response-format` | `String` | `HH:mm:ss` | Формат `LocalTime` в ответе |
| `date-time.zone-id` | `String` | `system-default` | Часовой пояс IANA для преобразования типов Instant и `Date` |
| `date-time.incomplete-input-policy` | `FILL_MISSING \| REJECT` | `FILL_MISSING` | Дополняет или отклоняет неполное значение даты и времени |

Поддерживаются `LocalDateTime`, `LocalDate`, `LocalTime`, `Instant`, `OffsetDateTime`, `ZonedDateTime` и `java.util.Date`. Для четырёх последних при выводе применяется настроенный часовой пояс.

## validation

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `validation.enabled` | `boolean` | `true` | Разрешает SmartMVC получать и обрабатывать результаты проверки параметров Spring MVC; при отключении используется валидатор-заглушка |

Этот параметр не устанавливает реализацию Bean Validation. Приложение должно явно добавить `spring-boot-starter-validation` и самостоятельно управлять его версией; SmartMVC не подключает Provider валидации как транзитивную зависимость.

## request-log

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `request-log.enabled` | `boolean` | `true` | Записывает метод, URI, статус и время выполнения |
| `request-log.level` | `TRACE \| DEBUG \| INFO \| WARN \| ERROR` | `INFO` | Уровень итоговой записи |

Если запросу соответствует контроллер, его класс используется как категория логгера.

## auth

| Свойство | Тип | По умолчанию | Назначение |
| --- | --- | --- | --- |
| `auth.enabled` | `boolean` | `true` | Включает проверки аутентификации и авторизации SmartMVC; сам перехватчик остаётся зарегистрированным |
| `auth.mode` | `GLOBAL \| ANNOTATED` | `ANNOTATED` | Защищает все методы API или только методы с `@Auth` |
| `auth.check-request-permission` | `boolean` | `false` | Проверяет разрешения пользователя в формате `METHOD:PATH` |
| `auth.authorization-header` | `String` | `Authorization` | Заголовок запроса, из которого читаются учётные данные |
| `auth.token-prefix` | `String` | `Bearer` | Префикс, удаляемый из значения заголовка |
| `auth.exclude-paths` | `String[]` | `[]` | Шаблоны путей Spring MVC, полностью исключённые из аутентификации |

`@Anonymous` пропускает аутентификацию в обоих режимах. `exclude-paths` также действует всегда: в режиме `ANNOTATED` исключённый путь не попадёт в перехватчик, даже если на нём есть `@Auth`. Для остальных путей `ANNOTATED` обрабатывает только обработчики с `@Auth`.

## Полный YAML

```yaml
spring:
  smart:
    mvc:
      exception:
        enabled: true
        status-mode: HTTP_STATUS
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
      date-time:
        request-format: yyyy-MM-dd HH:mm:ss
        response-format: yyyy-MM-dd HH:mm:ss
        date-request-format: yyyy-MM-dd
        date-response-format: yyyy-MM-dd
        time-request-format: HH:mm:ss
        time-response-format: HH:mm:ss
        zone-id: system-default
        incomplete-input-policy: FILL_MISSING
      validation:
        enabled: true
      request-log:
        enabled: true
        level: INFO
      auth:
        enabled: true
        mode: ANNOTATED
        check-request-permission: false
        authorization-header: Authorization
        token-prefix: Bearer
        exclude-paths: []
```
