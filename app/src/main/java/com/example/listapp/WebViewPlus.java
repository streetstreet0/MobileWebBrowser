package com.example.listapp;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WebViewPlus extends WebView {
    public WebBackForwardList getHistory() {
        return null;
    }


    public WebViewPlus(@NonNull Context context) {
        super(context);
    }

    public WebViewPlus(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewPlus(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebViewPlus(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param privateBrowsing
     * @deprecated
     */
    public WebViewPlus(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }
}
