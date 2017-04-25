http://qiita.com/tyfkda/items/786b9000b62b7874fa92
Androidでガワネイティブ
=====================

ネイティブアプリなんだけどネイティブのコードは極力書かず、WebViewを使ってhtml+JavaScriptを使ってアプリを組みたい。今回はAndroid, Javaで作ってみる。

[コード](https://github.com/tyfkda/GawaNativeAndroid)

## WebViewを全画面の大きさで配置する
レイアウトのxmlで指定する

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.tyfkda.gawanativeandroid.MainActivity" >

    <WebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>
```

* `layout_width`、`layout_height`ともに`match_parent`を指定することで全画面にする

## htmlの表示

配置したWebViewを使ってJavaからブラウザを操作する。

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val webView = findViewById(R.id.webView) as WebView
    webView.loadUrl("http://www.example.com/")
  }
}
```

* `findViewById`でWebViewを取得して、[`WebView#loadUrl`](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#loadUrl(java.lang.String))
  でページの読み込みを指定できる
* インターネットからウェブページを読み込むためにはAndroidManifest.xmlで`<uses-permission android:name="android.permission.INTERNET"/>`
  を指定して、アプリでのインターネット利用を有効にする必要がある

## アセット内のhtmlの表示
外部のurlではなく、アセットに含めたhtmlファイルを読み込むにはURLに`file:///android_asset/〜`と指定する

```kotlin
// Kotlin
    webView.loadUrl("file:///android_asset/index.html")
```

  * Android Stuidoでassetsフォルダを追加するには、appを選択して、メニューのFile > New > Folder > Assets Folder

## アセット内のhtmlから画像、JavaScript、CSSを読み込む
html内で相対パスで書けばアセット内のファイルが自動的に読み込まれる

```html
<link rel="stylesheet" type="text/css" href="main.css" />
<img src="hydlide.png" />
<script type="text/javascript" src="main.js"></script>
```

## JavaScriptとネイティブの連携
### JavaScriptからネイティブ(Java)を呼び出す
JavaScriptからネイティブに対してなにか起動するにはWebViewにインタフェース用のクラスを定義し

```kotlin
class MyJavaScriptInterface(private val context: Context) {
  @JavascriptInterface
  fun showToast(string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
  }
}
```

オブジェクトを登録してやると

```kotlin
// Kotlin
    webView.addJavascriptInterface(MyJavaScriptInterface(this), "Native")
```

JavaScriptから呼び出すことができる：

```js
// JavaScript
Native.showToast('PushMe clicked!')
```

* [`WebView#addJavascriptInterface`](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String))
  でインタフェースの登録
* 登録したインタフェースのpublicメソッドをJavaScriptから呼び出せる
  * 型を自動的に変換してくれる、便利
  * セキュリティ的に、Jelly Bean以降は[JavascriptInterfaceアノテーション](http://developer.android.com/intl/ja/reference/android/webkit/JavascriptInterface.html)がついたpublicメソッドだけが呼び出せる。（[リファレンス](http://developer.android.com/intl/ja/reference/android/webkit/JavascriptInterface.html)を参照すること）
  * それだとセキュリティ的に危険なので、[WebChromeClient#onJsAlert](http://developer.android.com/intl/ja/reference/android/webkit/WebChromeClient.html#onJsAlert(android.webkit.WebView, java.lang.String, java.lang.String, android.webkit.JsResult))を使う方法がよいらしい（[Android の WebView で addJavascriptInterface を使わず情報を渡す - Qiita](http://qiita.com/ka_/items/f8dcde7893f3a029f151)）

### ネイティブからJavaScriptを呼び出す
[`WebView#loadUrl`](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#loadUrl(java.lang.String))を使用する：

```kotlin
// Kotlin
    webView.evaluateJavascript("（JavaScriptのコード）", null)
```

* [`WebView#evaluateJavascript`](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#evaluateJavascript(java.lang.String, android.webkit.ValueCallback<java.lang.String>))を使う
  * 第二引数は結果受け取りコールバック
* API level 19(KITKAT)より前の場合には上のメソッドがないので、JavaScriptのコードを表す文字列の前に`"javascript:"`を追加した内容をurlとして[loadUrl](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#loadUrl(java.lang.String))を呼び出すことでJavaScriptコードが実行される

## URLリクエストを横取りする
Javaで登録したインタフェースのメソッドをJavaScriptから呼び出せるので、使うかどうかわからないけど、URLリクエストも横取りできる。

```kotlin
// Kotlin
    webView.setWebViewClient(object : WebViewClient() {
      override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        ...
      }
    })
```

* WebViewに対して [`setWebViewClient`](http://developer.android.com/intl/ja/reference/android/webkit/WebView.html#setWebViewClient(android.webkit.WebViewClient))
  で[`WebViewClient`](http://developer.android.com/intl/ja/reference/android/webkit/WebViewClient.html)
  を登録できて、その[`shouldOverrideUrlLoading`](http://developer.android.com/intl/ja/reference/android/webkit/WebViewClient.html#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String))でURLに対する処理を扱える
  * 横取りしてなにか処理した時には`true`を返す、そうでなくて通常の読み込みを継続する場合には `false` を返す


## 実行例

![スクリーンショット](ss.png)

## 注意
* [スマートフォンアプリへのブラウザ機能の実装に潜む危険　――WebViewクラスの問題について：CodeZine](http://codezine.jp/article/detail/6618)
