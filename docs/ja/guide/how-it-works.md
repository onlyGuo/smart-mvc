---
title: リクエスト処理の流れ
description: リクエストの受信からレスポンスの返却まで、SmartMVC の各コンポーネントが担う処理を説明します。
prev:
  text: クイックスタート
  link: /ja/guide/getting-started.html
next:
  text: 統一レスポンス
  link: /ja/features/response.html
---

# リクエスト処理の流れ

処理の順序を知っておくと、ある振る舞いを変更したいときに、どの設定や拡張ポイントを確認すればよいか判断しやすくなります。

## 全体の流れ

一般的なリクエストは、次の段階を通過します。

1. **リクエストログの計測開始**：フィルターが開始時刻を記録します。
2. **Handler の決定**：Spring MVC が対象の Controller メソッドを見つけます。
3. **認証と認可**：保護対象の API を SmartMVC の認証インターセプターが処理します。
4. **引数変換と検証**：日時文字列を Java 型へ変換し、Bean Validation で入力を検証します。
5. **Controller の実行**：アプリケーション固有の業務処理を実行します。
6. **レスポンスまたは例外の整形**：通常の戻り値を `ApiResponse` でラップし、例外は統一エラーへ変換します。
7. **リクエスト概要の記録**：実際の Controller をロガーカテゴリとして、ステータスコードと処理時間を出力します。

## 正常に完了する場合

Controller は、業務データだけを返せます。

```java
@GetMapping("/users/{id}")
public UserView get(@PathVariable Long id) {
    return userService.get(id);
}
```

メソッドの実行後、レスポンス拡張が戻り値を次の形式に整えます。

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": { "id": "1001", "name": "Ada" },
  "timestamp": "1786005000000"
}
```

Controller 側で毎回レスポンスオブジェクトを組み立てる必要はありません。

## エラーが発生する場合

見つからないリソースを、SmartMVC の例外で表します。

```java
throw new ResourceNotFoundException("User 1001 was not found");
```

グローバル例外ハンドラーは、例外が持つ HTTP ステータス、エラーコード、メッセージを読み取り、デフォルトでは HTTP 404 と次のレスポンスを返します。

```json
{
  "success": false,
  "code": "RESOURCE_NOT_FOUND",
  "message": "User 1001 was not found",
  "data": null,
  "timestamp": "1786005000000"
}
```

## 置き換えられる部分

SmartMVC は Spring Boot の条件付き自動構成を利用しています。アプリケーション側では、必要に応じて次の変更ができます。

- 統一レスポンスまたはデフォルトの例外処理を無効にする
- 独自の `AuthInterceptor` を登録する
- 独自の `CurrentAuth` または関連コンポーネントを提供する
- 日時形式、タイムゾーン、リクエストログレベルを変更する
- 特定の API だけ `ApiResponse` を直接返して、レスポンス内容を細かく制御する

SmartMVC は実用的なデフォルトを用意しますが、業務ルールまで決めることはありません。次の章から、利用頻度の高い[統一レスポンス](../features/response.md)を起点に、各機能を順番に見ていきます。
