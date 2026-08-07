---
title: Sign-in and authorization example
description: Run the Example module and verify anonymous access, sign-in, the current user, roles, and request permissions.
prev:
  text: Custom authentication and the current user
  link: /en/auth/custom-authentication.html
next:
  text: Configuration reference
  link: /en/reference/configuration.html
---

# Sign-in and authorization example

`spring-boot-starter-smart-mvc-example` contains a complete in-memory authentication example. It has no external service dependency; its purpose is to show how the SmartMVC integration is assembled and what each request returns.

The example uses fixed accounts, plain-text demonstration passwords, and fixed tokens. Use it only for local learning and automated tests.

## Start the Example module

First, install every module from the repository root:

```bash
mvn install -DskipTests
```

Then start the Example module:

```bash
mvn -f spring-boot-starter-smart-mvc-example/pom.xml spring-boot:run
```

## Example accounts

| Username | Password | Token | Role |
| --- | --- | --- | --- |
| `admin` | `admin123` | `example-admin-token` | `admin` |
| `user` | `user123` | `example-user-token` | `user` |

## 1. Call the anonymous endpoint

```bash
curl http://localhost:8080/auth/public
```

This endpoint has `@Anonymous`. The response data contains `authenticated: false`, confirming that an anonymous endpoint does not create a current identity.

## 2. Sign in and receive a token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin123"}'
```

The response `data` contains:

```json
{
  "tokenType": "Bearer",
  "token": "example-admin-token"
}
```

## 3. Read the current identity

```bash
curl http://localhost:8080/auth/me \
  -H 'Authorization: Bearer example-admin-token'
```

The returned `AuthPrincipal` contains:

- the user ID and `ExampleUser` object;
- the `admin` role;
- named permissions such as `admin:read`;
- request permissions such as `GET:/auth/**`;
- the additional `authenticationType` attribute.

Calling the endpoint without a token, or with an invalid token, returns HTTP 401.

## 4. Verify the administrator endpoint

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-admin-token'
```

This endpoint checks all of the following:

1. the user has been authenticated;
2. the identity has the `admin` role;
3. the identity has the named permission `admin:read`;
4. the identity has a request permission matching `GET:/auth/**`.

Call the same endpoint with the regular user token:

```bash
curl http://localhost:8080/auth/admin \
  -H 'Authorization: Bearer example-user-token'
```

The response uses HTTP 403 with the error code `FORBIDDEN`.

## Responsibility of each example class

| Class | Responsibility |
| --- | --- |
| `ExampleAuthController` | Exposes sign-in, anonymous, current-identity, and administrator endpoints |
| `ExampleAuthenticationService` | Stores in-memory accounts and performs password and token lookup |
| `ExampleAuthInterceptor` | Connects the token service to SmartMVC |
| `ExampleUser` | Demonstrates typed user access through `CurrentAuth` |

The integration test `ExampleAuthenticationIntegrationTest` covers anonymous access, sign-in, missing authentication, insufficient permissions, and successful administrator access.
