---
title: 統一レスポンス
description: ApiResponse、自動ラップ、ラップ対象外の型、ページング結果について説明します。
prev:
  text: リクエスト処理の流れ
  link: /ja/guide/how-it-works.html
next:
  text: 例外と入力値検証
  link: /ja/features/exceptions.html
---

# 統一レスポンス

レスポンス形式を統一すると、クライアントは同じルールで成功・失敗を判断し、データやメッセージを取り出せます。API ごとに異なる判定ロジックを持つ必要がなくなります。

## レスポンスの構造

`ApiResponse<T>` には 5 つのフィールドがあります。

| フィールド | 型 | 意味 |
| --- | --- | --- |
| `success` | `boolean` | リクエストが想定どおり完了したかどうか |
| `code` | `String` | クライアントが判定に使える結果コード |
| `message` | `String` | 利用者に伝える説明 |
| `data` | `T` | 業務データまたはエラーの詳細 |
| `timestamp` | `long` | レスポンスを生成した時点のミリ秒タイムスタンプ |

## Controller の戻り値を自動でラップする

`response.wrap-enabled` が有効な場合、通常の Controller 戻り値は成功レスポンスに自動変換されます。

```java
@GetMapping("/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

各メソッドで `ApiResponse.success(...)` を呼び出す必要はありません。Controller は業務データを返すことに集中できます。

`String` の戻り値も正しくラップされます。Spring が `StringHttpMessageConverter` を選択した場合でも、SmartMVC はレスポンスオブジェクトを単なる文字列としてではなく JSON として出力します。

## 自動ラップされない型

次の戻り値は、そのままレスポンスへ渡されます。

- すでに `ApiResponse` であるオブジェクト
- `byte[]`
- Spring の `Resource`
- `StreamingResponseBody`
- `ProblemDetail`

これらはファイル、バイナリ、ストリーミング、標準の問題詳細などに使われる型です。JSON レスポンスをさらに重ねると本来の用途を損なうため、自動ラップの対象外です。

## `ApiResponse` を直接作成する

メッセージやエラーコードを個別に指定したい場合は、ファクトリメソッドを使って `ApiResponse` を直接返せます。

```java
return ApiResponse.success("User created", userView);
```

```java
return ApiResponse.failure(
        "ORDER_CLOSED",
        "The order is already closed",
        Map.of("orderId", orderId)
);
```

直接返した `ApiResponse` が、もう一度ラップされることはありません。

## `void` の戻り値

デフォルトでは、`void` と `Void` も `data: null` の成功レスポンスになります。

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-void: false
```

`wrap-void` を無効にすると、`void` の戻り値に対して統一レスポンス本文を生成しません。

## ページング結果

`PageResult<T>` は、データベースや特定のページングライブラリに依存しない共通モデルです。

```java
PageResult<UserView> result = new PageResult<>(
        users,
        total,
        page,
        pageSize
);
```

次の情報を持ちます。

- `items`：現在のページに含まれるデータ
- `total`：全件数
- `page`：現在のページ番号
- `pageSize`：1 ページあたりの件数
- `totalPages`：全件数とページサイズから計算される総ページ数

`PageResult` 自体はデータの取得方法を決めません。アプリケーションが取得した一覧と件数を渡すだけなので、任意の永続化方式や外部 API と組み合わせられます。

## `long` を文字列で出力する理由

JavaScript は、すべての 64 ビット整数を正確に表現できるわけではありません。そのため、デフォルトでは `long` と `Long` を文字列としてシリアライズします。

```yaml
spring.smart.mvc.response.long-as-string: true
```

すべてのクライアントが 64 ビット整数を安全に扱える場合は、無効にできます。

## 設定一覧

```yaml
spring:
  smart:
    mvc:
      response:
        wrap-enabled: true
        wrap-void: true
        success-message: success
        long-as-string: true
```

`wrap-enabled` を無効にすると、自動レスポンス拡張の Bean は登録されません。Controller の戻り値は Spring MVC によってそのまま処理されます。
