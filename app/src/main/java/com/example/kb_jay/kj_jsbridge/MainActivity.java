package com.example.kb_jay.kj_jsbridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lib_jsbridge.BridgeHandler;
import com.example.lib_jsbridge.BridgeWebView;
import com.example.lib_jsbridge.CallBackFunction;

/**
 * jsbridge使用参考：https://juejin.im/post/5ac044a8518825557459d603
 * <p>
 * jsbridge 源码参考：https://juejin.im/entry/573534f82e958a0069b27646
 * <p>
 * webview 参考：https://juejin.im/post/5a94fb046fb9a0635865a2d6
 * <p>
 * https://juejin.im/post/59a56b2151882524424a1862
 *
 * @author kb_jay
 */
public class MainActivity extends AppCompatActivity {

    private BridgeWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //addWebView
        LinearLayout rootView = findViewById(R.id.root_view);
        webView = new BridgeWebView(this.getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootView.addView(webView, lp);

        findViewById(R.id.bt_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("file:///android_asset/test.html");
            }
        });
        findViewById(R.id.java_to_js_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java2jsDefault();
            }
        });
        findViewById(R.id.java_to_js_spec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java2jsSpec();
            }
        });
        initJs();
    }

    /**
     * 初始化jsbridge用于接受js发来的消息，并利用回调将传递数据给js。
     */
    private void initJs() {
        webView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                function.onCallBack("java默认接收完毕，并回传数据给js");
            }
        });
        //js2java-> step2
        webView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                function.onCallBack("java指定接收完毕，并回传数据给js");
            }
        });
    }

    /**
     * java 发送消息给 js
     */
    private void java2jsSpec() {
        // java2js-> step1
        webView.callHandler("functionInJs", "发送消息给js（spec方式！！）", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * java 发送消息给 js
     */
    private void java2jsDefault() {
        webView.send("发送消息给js（默认方式！）", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁webview
        if (webView != null) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
    }
}
