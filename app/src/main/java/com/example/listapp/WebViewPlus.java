package com.example.listapp;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WebViewPlus extends WebView {
    private EditText urlInputText;

    public void addUrlInputText(EditText urlInputText) {
        this.urlInputText = urlInputText;
    }

    @Override
    public void invalidate(Rect dirty) {
        if (getContentHeight() > 0 && urlInputText != null) {
            urlInputText.setText(this.getUrl());
        }
        super.invalidate(dirty);
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        if (getContentHeight() > 0 && urlInputText != null) {
            urlInputText.setText(this.getUrl());
        }
        super.invalidate(l, t, r, b);
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
}
