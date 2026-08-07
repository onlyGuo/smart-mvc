---
title: Быстрый старт
description: Подготовьте зависимости Spring MVC, подключите SmartMVC и создайте первый метод API.
prev:
  text: Знакомство со SmartMVC
  link: /ru/guide/introduction.html
next:
  text: Как SmartMVC обрабатывает запрос
  link: /ru/guide/how-it-works.html
---

# Быстрый старт

В этом разделе мы сделаем только три вещи: добавим зависимости приложения для Web и валидации, подключим SmartMVC и посмотрим на единый ответ первого метода API.

## Требования

- Java 17 или новее;
- Spring Boot 3.2 или новее (текущая базовая версия для сборки и тестирования — 3.5.7);
- проект на Maven.

## 1. Добавьте зависимости приложения

```xml
<!-- Предоставляется приложением: Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Предоставляется приложением: Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Добавляет SmartMVC в существующее приложение Spring MVC -->
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>spring-boot-starter-smart-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

SmartMVC не добавляет Spring Web и Bean Validation как транзитивные зависимости. Приложение объявляет обе зависимости явно, а их версиями управляет собственный Spring Boot Parent или BOM приложения. Поэтому SmartMVC не фиксирует версии Spring Web и Bean Validation, выбранные приложением.

## 2. Создайте метод API

```java
package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Greeting hello() {
        return new Greeting("Hello, SmartMVC", LocalDateTime.now());
    }

    public record Greeting(String message, LocalDateTime time) {
    }
}
```

## 3. Запустите приложение и выполните запрос

```bash
mvn spring-boot:run
```

Вызовите метод API:

```bash
curl http://localhost:8080/hello
```

Ответ по умолчанию выглядит так:

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {
    "message": "Hello, SmartMVC",
    "time": "2026-08-06 16:30:00"
  },
  "timestamp": "1786005000000"
}
```

Здесь SmartMVC выполнил две операции:

1. обернул возвращённый контроллером объект `Greeting` в `ApiResponse`;
2. преобразовал `LocalDateTime` в строку формата `yyyy-MM-dd HH:mm:ss`.

По умолчанию `timestamp` сериализуется как строка, чтобы JavaScript не потерял точность при работе с длинным целым числом.

## 4. Добавьте минимальную настройку

Все параметры находятся в пространстве `spring.smart.mvc`. В этом примере изменим только текст успешного ответа и часовой пояс:

```yaml
spring:
  smart:
    mvc:
      response:
        success-message: ok
      date-time:
        zone-id: Asia/Shanghai
```

В Starter включены метаданные конфигурации Spring Boot, поэтому IDE может подсказывать имена свойств, значения по умолчанию и варианты перечислений.

## Что включено по умолчанию

Без дополнительной настройки:

- обычные значения, возвращаемые контроллерами, оборачиваются в единый ответ;
- методы с типом результата `void` получают успешный ответ;
- распространённые исключения Spring MVC преобразуются в единый формат ошибки;
- проверка параметров включена;
- краткий журнал запросов пишется на уровне `INFO`;
- используется режим аутентификации `ANNOTATED`: обрабатываются только методы API с `@Auth`;
- если приложение не предоставило собственную реализацию аутентификации, Starter использует реализацию, разрешающую все запросы.

Этого достаточно для первого знакомства. В следующем разделе мы рассмотрим эти возможности как части полного жизненного цикла одного запроса.
