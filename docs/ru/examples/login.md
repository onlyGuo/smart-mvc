---
title: Пример входа и проверки доступа
description: Запустите модуль Example и проверьте анонимный доступ, вход, текущего пользователя, роли и разрешения.
prev:
  text: Собственная аутентификация и текущий пользователь
  link: /ru/auth/custom-authentication.html
next:
  text: Справочник настроек
  link: /ru/reference/configuration.html
---

# Пример входа и проверки доступа

Модуль `spring-boot-starter-smart-mvc-example` содержит полный пример аутентификации в памяти. Ему не нужны внешние сервисы: пример последовательно показывает, как подключить SmartMVC и какие ответы получать на каждом этапе.

В нём используются фиксированные учётные записи, открытые демонстрационные пароли и постоянные токены. Такой подход предназначен только для локального изучения и автоматических тестов.

## Запуск Example

Сначала установите все модули из корня репозитория:

```bash
mvn install -DskipTests
```

Затем запустите Example:

```bash
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

## Демонстрационные учётные записи

| Имя пользователя | Пароль | Токен | Роль |
| --- | --- | --- | --- |
| `admin` | `admin123` | `example-admin-token` | `admin` |
| `user` | `user123` | `example-user-token` | `user` |

## 1. Вызовите анонимный метод API

```bash
curl http://localhost:8080/auth/public
```

Метод помечен `@Anonymous`. В данных ответа поле `authenticated` равно `false`: для анонимного запроса данные текущего пользователя не создаются.

## 2. Войдите и получите токен

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

Поле `data` содержит:

```json
{
  "tokenType": "Bearer",
  "token": "example-admin-token"
}
```

## 3. Получите данные текущего пользователя

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

Возвращённый `AuthPrincipal` содержит:

- ID пользователя и объект `ExampleUser`;
- роль `admin`;
- именованные разрешения, например `admin:read`;
- разрешения на запросы, например `GET:/auth/**`;
- дополнительный атрибут `authenticationType`.

Без токена или с недействительным токеном метод возвращает HTTP 401.

## 4. Проверьте административный метод API

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-admin-token'
```

Для этого метода одновременно проверяются четыре условия:

1. пользователь успешно прошёл аутентификацию;
2. у него есть роль `admin`;
3. у него есть именованное разрешение `admin:read`;
4. у него есть разрешение `GET:/auth/**`, соответствующее текущему запросу.

Выполните тот же запрос с токеном обычного пользователя:

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-user-token'
```

Ответ получит статус HTTP 403 и код ошибки `FORBIDDEN`.

## За что отвечают классы примера

| Класс | Назначение |
| --- | --- |
| `ExampleAuthController` | Методы входа, анонимного доступа, текущего пользователя и администратора |
| `ExampleAuthenticationService` | Хранение учётных записей в памяти, проверка пароля и поиск по токену |
| `ExampleAuthInterceptor` | Подключение сервиса токенов к SmartMVC |
| `ExampleUser` | Типизированное чтение пользователя через `CurrentAuth` |

Интеграционный тест `ExampleAuthenticationIntegrationTest` проверяет анонимный доступ, вход, запрос без аутентификации, недостаточные права и успешный доступ администратора.
