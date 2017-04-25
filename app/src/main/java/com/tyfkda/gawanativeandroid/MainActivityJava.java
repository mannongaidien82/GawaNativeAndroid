package com.tyfkda.gawanativeandroid;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by tran-ngocdien on 2017/04/25.
 */

public class MainActivityJava extends AppCompatActivity implements View.OnClickListener  {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        connectView();
    }

    private void connectView() {
        webView = (WebView) findViewById(R.id.webView);
        //リンクをタップしたときに標準ブラウザを起動させない
//http://stackoverflow.com/questions/21749425/android-webview-addjavascriptinterface-does-not-work-if-the-webview-is-created-i
        webView.addJavascriptInterface(new MyJavaScriptInterfaceJava(this), "Native");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 別のActivityやアプリを起動する場合
                //return true;
                // WebView内に読み込み結果を表示する場合
                if (Uri.parse(url).getScheme() != "native") {
                    return false;
                }
                Toast.makeText(MainActivityJava.this, "Java Url requested: " + url, Toast.LENGTH_SHORT).show();
               // return true;
                return false;
            }
        });

        //最初にgoogleのページを表示する。
        webView.loadUrl("file:///android_asset/index.html");

        //jacascriptを許可する
        webView.getSettings().setJavaScriptEnabled(true);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                doClickFabBtn();
                break;
        }
    }

    private void doClickFabBtn() {
        evaluateJs(webView, "addTextNode('[Java Button clicked]')");
    }

    private void evaluateJs(WebView webView, String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            webView.evaluateJavascript(script, null);
        else
            webView.loadUrl("javascript:" + script);
    }

}
