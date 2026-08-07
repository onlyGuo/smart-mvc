---
title: SmartMVC について
description: SmartMVC が解決する課題、提供する機能、Spring MVC との関係を説明します。
prev:
  text: ホーム
  link: /ja/
next:
  text: クイックスタート
  link: /ja/guide/getting-started.html
---

# SmartMVC について

SmartMVC は、Spring Boot をベースにした MVC 拡張フレームワークです。Web アプリケーションで繰り返し実装されがちな共通処理を整理し、Controller をシンプルに保ちながら、API 全体に一貫した振る舞いを与えます。

主に次の機能を提供します。

- 成功レスポンスとページング結果の統一モデル
- 例外レスポンスと入力検証エラーの統一処理
- 日付・時刻の解析、整形、タイムゾーン処理
- ステータスコードと処理時間を含むリクエスト概要ログ
- 拡張可能な認証、ロール、権限モデル
- `spring.smart.mvc.*` 配下の設定と IDE の入力補完情報

## Spring MVC との関係

SmartMVC は Spring MVC を置き換えるものではありません。これまでどおり、`@RestController`、`@GetMapping`、`@RequestBody`、Bean Validation などを使って Controller を実装します。

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

SmartMVC は、その一般的な書き方の外側に、統一レスポンス、例外処理、日時変換、リクエストログ、認証・認可を追加します。フレームワーク専用の基底クラスを継承したり、既存の業務オブジェクトを書き換えたりする必要はありません。

## Core と Starter に分かれている理由

プロジェクトは、主に次の 2 モジュールで構成されています。

| モジュール | 役割 |
| --- | --- |
| `core` | アノテーション、レスポンスモデル、設定モデル、認証情報と権限のモデル、例外体系を提供します。Spring には依存しません。 |
| `spring-boot-starter-smart-mvc` | Spring Boot の設定バインド、自動構成、MVC インターセプター、Jackson、ログ、例外処理を実装します。 |

この分離により、認証情報や例外といった中核の概念は特定の Web 実装に縛られません。一方、Spring 固有の処理は Starter に集約されているため、アプリケーション側では必要な拡張ポイントだけを差し替えられます。

## どのようなプロジェクトに向いているか

SmartMVC は、REST API の振る舞いを統一したい Spring Boot プロジェクトに適しています。特に、次のような場合に役立ちます。

- 複数の Controller で同じレスポンス形式を使いたい
- 入力検証エラーや業務例外を一か所で扱いたい
- Java の日時型に対する入出力形式をまとめて設定したい
- シンプルで置き換え可能な認証・認可の入口が必要
- 新しいプロジェクトを始めるたびに同じ MVC 基盤を作り直したくない

## このドキュメントの読み進め方

初めて使う場合は、次の順序がおすすめです。

1. [クイックスタート](./getting-started.md)で最初の API を動かす
2. [リクエスト処理の流れ](./how-it-works.md)で全体像をつかむ
3. 統一レスポンス、例外、日時、ログを必要に応じて確認する
4. ログインが必要になった段階で認証・認可の章へ進む
5. 最後に設定リファレンスと API リファレンスで細部を調べる

まずは小さな API を 1 つ動かして、SmartMVC がどこを支えているのかを見ていきましょう。
