---
title: クイックスタート
description: Spring MVC アプリケーションの依存関係を用意し、SmartMVC と最初の API を追加します。
prev:
  text: SmartMVC について
  link: /ja/guide/introduction.html
next:
  text: リクエスト処理の流れ
  link: /ja/guide/how-it-works.html
---

# クイックスタート

この章で行うことは 3 つだけです。アプリケーション側で Web と検証の依存関係を用意し、SmartMVC を追加し、最初の API の統一レスポンスを確認します。

## 動作環境

- Java 17 以降
- Spring Boot 3.2 以降（現在のビルド・テスト基準は 3.5.7）
- Maven プロジェクト

## 1. アプリケーションの依存関係を追加する

アプリケーションの `pom.xml` に次の依存関係を追加します。

```xml
<!-- アプリケーション側で提供：Spring MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- アプリケーション側で提供：Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- 既存の Spring MVC アプリケーションに SmartMVC を追加 -->
<dependency>
    <groupId>ink.icoding</groupId>
    <artifactId>spring-boot-starter-smart-mvc</artifactId>
    <version>1.0.0</version>
</dependency>
```

SmartMVC は Spring Web と Bean Validation を推移的依存関係として提供しません。アプリケーションがこの 2 つを明示的に宣言し、そのバージョンはアプリケーション自身の Spring Boot Parent または BOM で管理します。そのため、SmartMVC がアプリケーションの Spring Web や Bean Validation のバージョンを固定することはありません。

## 2. 最初の API を作成する

通常の Spring MVC Controller を作成します。SmartMVC 固有の基底クラスやインターフェースは必要ありません。

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

## 3. 起動してアクセスする

```bash
mvn spring-boot:run
```

別のターミナルから API を呼び出します。

```bash
curl http://localhost:8080/hello
```

デフォルトでは、次のようなレスポンスが返ります。

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

ここでは SmartMVC が 2 つの処理を行っています。

1. Controller が返した `Greeting` を `ApiResponse` でラップする
2. `LocalDateTime` をデフォルト形式 `yyyy-MM-dd HH:mm:ss` で出力する

`timestamp` はデフォルトで文字列としてシリアライズされます。これは、JavaScript が大きな 64 ビット整数を正確に表現できない場合があるためです。

## 4. 最小限の設定を追加する

SmartMVC の設定は、すべて `spring.smart.mvc` 配下にあります。ここでは、成功メッセージとタイムゾーンだけを変更します。

```yaml
spring:
  smart:
    mvc:
      response:
        success-message: ok
      date-time:
        zone-id: Asia/Shanghai
```

Starter には Spring Boot の設定メタデータが含まれているため、対応する IDE ではプロパティ名、デフォルト値、列挙値の候補が補完されます。

## デフォルトで有効になる機能

設定を追加しなくても、次の機能が有効です。

- 通常の Controller 戻り値を統一レスポンスでラップする
- `void` の戻り値にも成功レスポンスを生成する
- 一般的な MVC 例外を統一されたエラー形式に変換する
- 入力値検証を有効にする
- `INFO` レベルでリクエスト概要ログを出力する
- 認証モードは `ANNOTATED` とし、`@Auth` が付いた API だけを対象にする
- 独自の認証実装がない場合は、デフォルトの許可実装を使用する

ここまでで最初の API は完成です。次の章では、リクエストが入ってからレスポンスが返るまでの間に、各機能がどの順番で動くのかを確認します。
