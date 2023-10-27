package com.example.listapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ClientCertRequest;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

public class Controller {
    Activity activity;
    Bundle instanceState;
//    Stack<String> history;
    String url;
    String homePage = "https:www.duckduckgo.com";
    EditText input;
    WebViewPlus webView;
    WebBackForwardList historyList;
    TabWindow currentView;

    public Controller(Activity activity, Bundle savedInstanceState){
        if (savedInstanceState == null) {
            this.instanceState = new Bundle();
        }
        else {
            this.instanceState = savedInstanceState;
        }
        this.activity = activity;
        setupHomeScreen();

    }

    private void setupHomeScreen(){
        activity.setContentView(R.layout.activity_main);
        currentView = TabWindow.BROSWER;

        setupTabs();

        input = (EditText) activity.findViewById(R.id.urlinput);

        webView = (WebViewPlus) activity.findViewById(R.id.webview);
        webView.restoreState(instanceState);
        if (webView.copyBackForwardList().getSize() != 0) {
            Log.d("TEST ORIGINAL SIZE", String.valueOf(historyList.getSize()));
            for (int i=0; i<historyList.getSize(); i++) {
                webView.loadUrl(historyList.getCurrentItem().getUrl());
                Log.d("TEST CURRENT SIZE", String.valueOf(webView.copyBackForwardList().getSize()));
            }
        }
        else {
            webView = (WebViewPlus) activity.findViewById(R.id.webview);
            webView.loadUrl(homePage);
        }

        WebViewClient myWebViewClient = new WebViewClient();

        webView.addUrlInputText(input);
        webView.copyBackForwardList();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);
        historyList = webView.copyBackForwardList();

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("INPUT","url entered: "+input.getText());
                    // Perform action on key press
                    url = parseUrl(String.valueOf(input.getText()));

                    webView.loadUrl(url);
                    input.setText(url);
                    return true;
                }
                return false;
            }
        });

        Button button = (Button) this.activity.findViewById(R.id.backButton);
        button.setOnClickListener((view -> {
            if (webView.canGoBack()){
                webView.goBack();
            }
        }));
    }

    public void onBackPress(){
        Log.d("ONBACK","Loading previous site if exists");
        if (webView.canGoBack()){
            webView.goBack();
            input.setText(webView.getUrl());
        } else {
            Toast.makeText(activity, activity.getString(R.string.shut_down), Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    public String parseUrl(String url) {
        if (url.startsWith("https:") || url.startsWith("http:")) {
            return url;
        }
        else if (url.startsWith("www.")) {
            return "https:/" + url;
        }
        else {
            String searchUrl = url;
            // need to encode all of the symbols before we can search
            searchUrl = url.replace("%", "%25");
            // need to replace % first
            // ~ does not need to be encoded
            searchUrl = searchUrl.replace("!", "%21");
            searchUrl = searchUrl.replace("@", "%40");
            searchUrl = searchUrl.replace("#", "%23");
            searchUrl = searchUrl.replace("$", "%24");
            searchUrl = searchUrl.replace("^", "%5E");
            searchUrl = searchUrl.replace("&", "%26");
            searchUrl = searchUrl.replace("*", "%2A");
            searchUrl = searchUrl.replace("(", "%28");
            searchUrl = searchUrl.replace(")", "%29");
            // _ does not need encoding
            searchUrl = searchUrl.replace("+", "%2B");
            searchUrl = searchUrl.replace("`", "%60");
            // - does not need encoding
            searchUrl = searchUrl.replace("=", "%3D");
            searchUrl = searchUrl.replace("{", "%7B");
            searchUrl = searchUrl.replace("}", "7D");
            searchUrl = searchUrl.replace("|", "%7C");
            searchUrl = searchUrl.replace("[", "%5B");
            searchUrl = searchUrl.replace("]", "%5D");
            // note: || means \
            // since \ has another meaning in java strings
            searchUrl = searchUrl.replace("\\", "%5C");
            searchUrl = searchUrl.replace(":", "%3A");
            // note: \" means " inside a string
            searchUrl = searchUrl.replace("\"", "%22");
            searchUrl = searchUrl.replace(";", "%3B");
            // note: \' means ' inside a string
            searchUrl = searchUrl.replace("'", "%27");
            searchUrl = searchUrl.replace("<", "%3C");
            searchUrl = searchUrl.replace(">", "%3E");
            searchUrl = searchUrl.replace("?", "%3F");
            searchUrl = searchUrl.replace(",", "%2C");
            // . does not need encoding
            searchUrl = searchUrl.replace("/", "%2F");
            searchUrl = searchUrl.replace(" ", "%20");


            // finally need to search it
            searchUrl = "https:/www.duckduckgo.com/?t=ffab&q=" + searchUrl + "&ia=web";
            return searchUrl;
        }
    }

    public void setupTabs() {
        if (currentView != TabWindow.BROSWER) {
            Button browserButton = (Button) activity.findViewById(R.id.webButton);
            browserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupHomeScreen();
                }
            });
        }

        if (currentView != TabWindow.HISTORY) {
            Button historyButton = (Button) activity.findViewById(R.id.historyButton);
            historyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupHistoryScreen();
                }
            });
        }

        if (currentView != TabWindow.SETTINGS) {
            Button settingsButton = (Button) activity.findViewById(R.id.settingsButton);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupSettingsScreen();
                }
            });
        }
    }

    public void setupHistoryScreen() {
        webView.saveState(instanceState);
        activity.setContentView(R.layout.activity_history);
        currentView = TabWindow.HISTORY;

        setupTabs();
    }

    public void setupSettingsScreen() {
        webView.saveState(instanceState);
        activity.setContentView(R.layout.activity_history);
        currentView = TabWindow.SETTINGS;

        setupTabs();
    }
}
