---
title: Собственная аутентификация и текущий пользователь
description: Реализация AuthInterceptor, создание AuthPrincipal и безопасная работа с CurrentAuth в бизнес-коде.
prev:
  text: Основы аутентификации и авторизации
  link: /ru/auth/overview.html
next:
  text: Пример входа и проверки доступа
  link: /ru/examples/login.html
---

# Собственная аутентификация и текущий пользователь

В этом разделе мы реализуем аутентификацию с Bearer-токеном, а затем разберём роли, разрешения на запрос и жизненный цикл `CurrentAuth`.

## 1. Определите пользователя приложения

```java
public record AppUser(
        String id,
        String username,
        String displayName
) {
}
```

SmartMVC не требует, чтобы пользователь реализовывал специальный интерфейс. Можно использовать уже существующую сущность или DTO приложения.

## 2. Реализуйте `AuthInterceptor`

```java
@Component
public class TokenAuthInterceptor implements AuthInterceptor<AppUser> {

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public AuthPrincipal<AppUser> authenticate(
            String token,
            HttpServletRequest request) {
        AppUser user = tokenService.findUser(token);
        if (user == null) {
            return null;
        }

        return new AuthPrincipal<>(
                user.id(),
                user,
                Set.of("admin"),
                Set.of("user:read", "GET:/api/users/**"),
                Map.of("tenantId", "tenant-001")
        );
    }
}
```

Результат `null` означает, что аутентификация не удалась. SmartMVC выбросит `UnauthorizedException` и вернёт HTTP 401.

Как только приложение регистрирует этот бин, стандартный `PermitAllAuthInterceptor` автоматически перестаёт применяться.

## 3. Настройте расположение учётных данных

```yaml
spring:
  smart:
    mvc:
      auth:
        authorization-header: Authorization
        token-prefix: Bearer
```

Для запроса с заголовком:

```text
Authorization: Bearer eyJhbGciOi...
```

метод `authenticate` получает токен уже без префикса.

Если учётные данные хранятся в Cookie, Session или другом месте, можно переопределить метод:

```java
@Override
public String resolveToken(
        HttpServletRequest request,
        AuthConfig config) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
        return null;
    }
    return Arrays.stream(cookies)
            .filter(cookie -> "session_token".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
}
```

## `AuthPrincipal<T>`

`AuthPrincipal`, возвращаемый после успешной аутентификации, содержит:

| Значение | Назначение |
| --- | --- |
| `id` | Стабильный идентификатор пользователя; не может быть пустым |
| `user` | Объект пользователя приложения; может быть `null` |
| `roles` | Набор ролей |
| `permissions` | Набор именованных разрешений и разрешений на запросы |
| `attributes` | Дополнительные атрибуты: арендатор, способ аутентификации и другие данные |

Наборы и Map защитно копируются и становятся неизменяемыми. Это не позволяет случайно изменить данные пользователя после завершения аутентификации.

## Использование `CurrentAuth` в бизнес-коде

```java
@Service
public class ProfileService {

    private final CurrentAuth currentAuth;

    public ProfileService(CurrentAuth currentAuth) {
        this.currentAuth = currentAuth;
    }

    public ProfileView currentProfile() {
        AppUser user = currentAuth.getUser(AppUser.class);
        return ProfileView.from(user);
    }
}
```

Часто используемые методы:

```java
currentAuth.isAuthenticated();
currentAuth.getPrincipal();
currentAuth.requirePrincipal();
currentAuth.getUserId();
currentAuth.getUser(AppUser.class);
currentAuth.getRoles();
currentAuth.getPermissions();
currentAuth.hasRole("admin");
currentAuth.hasPermission("GET", "/api/users/1001");
```

Если текущий пользователь не установлен, `requirePrincipal()` выбрасывает `IllegalStateException`. Метод удобен в коде, который выполняется только внутри защищённого запроса.

## Безопасно ли это при параллельных запросах

`CurrentAuth` — синглтон под управлением Spring, но пользователь не хранится в поле этого объекта. Все запросы делегируются `AuthContext`, основанному на `ThreadLocal`:

- синхронные Servlet-запросы изолированы друг от друга;
- перед началом запроса старый контекст очищается;
- после завершения запроса контекст очищается снова, чтобы при повторном использовании потока из пула в нём не остались данные другого пользователя.

Контекст не переносится автоматически в `@Async`, самостоятельно созданные потоки, задачи пула или реактивные цепочки. При переходе между потоками явно передавайте необходимые данные: ID пользователя, роли или неизменяемый снимок `AuthPrincipal`.

### Асинхронная обработка Spring MVC

`Callable`, `DeferredResult`, `WebAsyncTask` и `StreamingResponseBody` также переходят границу Servlet-потока запроса. Текущий перехватчик аутентификации очищает пользователя в синхронном обратном вызове `afterCompletion` и не обрабатывает переход `afterConcurrentHandlingStarted`. Поэтому описанная выше гарантия автоматической очистки действует только для синхронных Servlet-запросов: асинхронная задача не наследует `CurrentAuth`, а контекст исходного потока не очищается автоматически в момент начала асинхронной обработки.

При использовании асинхронного возвращаемого типа Spring MVC скопируйте минимальный неизменяемый набор данных пользователя до выхода из синхронного потока и организуйте очистку в рамках асинхронного жизненного цикла. Другой вариант — предоставить собственный перехватчик, который явно обрабатывает переход в асинхронный режим. Не полагайтесь на `CurrentAuth` непосредственно внутри асинхронного callback.

## Выражения разрешений на запрос

Включите проверку разрешения уровня запроса:

```yaml
spring.smart.mvc.auth.check-request-permission: true
```

Разрешение записывается в формате `METHOD:/path/pattern`:

| Выражение | Что оно разрешает |
| --- | --- |
| `GET:/api/users/**` | GET-запросы на этот путь и все вложенные уровни |
| `POST:/api/users/*` | POST-запросы на один вложенный сегмент |
| `*:/api/public/**` | Любой HTTP-метод |
| `*:*` | Любой метод и любой путь |

В шаблоне пути поддерживаются:

- `**` — несколько сегментов пути;
- `*` — любое количество символов внутри одного сегмента;
- `?` — один символ.

HTTP-метод сопоставляется без учёта регистра.

## Собственная логика авторизации

Если разрешение нужно проверять в реальном времени во внешнем сервисе или приложение использует совсем другую модель авторизации, переопределите `authorize`:

```java
@Override
public boolean authorize(
        AuthPrincipal<AppUser> principal,
        Auth requirement,
        HttpServletRequest request,
        AuthConfig config) {
    return permissionService.isAllowed(
            principal.getId(),
            request.getMethod(),
            request.getRequestURI()
    );
}
```

При необходимости также можно переопределить `bind`, `clear` и `resolveRequestPath`. Обычно стоит менять только ту часть, поведение которой действительно отличается, а остальной жизненный цикл оставить стандартной реализации.

## Рекомендуемая конфигурация

```yaml
spring:
  smart:
    mvc:
      auth:
        enabled: true
        mode: GLOBAL
        authorization-header: Authorization
        token-prefix: Bearer
        check-request-permission: true
        exclude-paths:
          - /actuator/health
```

После подключения собственной реализации можно проверить весь процесс на примере без базы данных из следующего раздела.
