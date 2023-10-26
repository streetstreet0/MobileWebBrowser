package com.example.listapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ClientCertRequest;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import java.util.Stack;

public class Controller {
    Activity activity;
    Stack<String> history;
    String url;
    WebView webView;
    WebBackForwardList temp;

    public Controller(Activity activity){
        this.activity = activity;
        setupHomeScreen();
    }

    private void setupHomeScreen(){
        activity.setContentView(R.layout.activity_main);

        EditText input = (EditText) activity.findViewById(R.id.urlinput);

        WebViewClient myWebViewClient = new WebViewClient();
        webView = (WebView) activity.findViewById(R.id.webview);

        webView.copyBackForwardList();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);

        webView.copyBackForwardList();

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("INPUT","url entered: "+input.getText());
                    // Perform action on key press
                    url = parseUrl(String.valueOf(input.getText()));

                    webView.loadUrl(url);
                    if (webView.copyBackForwardList().equals(temp)) {
                        Log.d("TEST", "is same object");
                    }
//                    input.setText(url);
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
            searchUrl = searchUrl.replace("\'", "%27");
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
}
