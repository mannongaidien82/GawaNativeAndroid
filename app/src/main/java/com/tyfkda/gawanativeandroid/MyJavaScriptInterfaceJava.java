package com.tyfkda.gawanativeandroid;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by tran-ngocdien on 2017/04/25.
 */
public class MyJavaScriptInterfaceJava {
    Context mContext;

    /** Instantiate the interface and set the context */

    MyJavaScriptInterfaceJava(Context c) {
        mContext = c;
    }

    @JavascriptInterface   // must be added for API 17 or higher
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
