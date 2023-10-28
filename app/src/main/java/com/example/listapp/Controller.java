package com.example.listapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.Stack;

public class Controller {
    Activity activity;
    Bundle instanceState;
    Stack<String> history;
    Stack<String> pastHistory; // used to refer to history before this session
    String url;
    String homePage = "https:www.duckduckgo.com";
    EditText input;
    WebViewPlus webView;
    TabWindow currentView;

    public Controller(Activity activity, Bundle savedInstanceState){
        if (savedInstanceState == null) {
            this.instanceState = new Bundle();
        }
        else {
            this.instanceState = savedInstanceState;
        }
        pastHistory = new Stack<String>();
        this.activity = activity;
        setupHomeScreen();
    }

    public Stack<String> getHistory() {
        saveHistory();
        return history;
    }

    public void loadHistory(Stack<String> pastHistory) {
        this.pastHistory = pastHistory;
    }

    private void setupHomeScreen(){
        activity.setContentView(R.layout.activity_main);
        currentView = TabWindow.BROSWER;

        setupTabs();

        input = (EditText) activity.findViewById(R.id.urlinput);

        webView = (WebViewPlus) activity.findViewById(R.id.webview);
        webView.restoreState(instanceState);
        if (webView.copyBackForwardList().getSize() == 0) {
            webView = (WebViewPlus) activity.findViewById(R.id.webview);
            webView.loadUrl(homePage);
        }

        WebViewClient myWebViewClient = new WebViewClient();

        webView.addUrlInputText(input);
        webView.copyBackForwardList();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);

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

        saveHistory();
        ListView historyView = (ListView) activity.findViewById(R.id.historyList);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(activity, R.layout.history_item, R.id.historyItem, history);
        historyView.setAdapter(listAdapter);
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LOAD HISTORY ITEM", "loading url: " + history.get(position));
                setupHomeScreen();
                webView.loadUrl(history.get(position));
            }
        });

        setupTabs();
    }

    public void setupSettingsScreen() {
        webView.saveState(instanceState);
        activity.setContentView(R.layout.activity_settings);
        currentView = TabWindow.SETTINGS;

        Button clearHistoryButton = (Button) activity.findViewById(R.id.clearHistoryButton);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBuilder.setTitle("Clear History?");
                alertBuilder.setMessage("Clearing history is permanent. \nClearing history will reset the current session. \nDo you wish to clear history?");
                alertBuilder.setCancelable(true);
                alertBuilder.setPositiveButton("Clear History", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearHistory();
                    }
                });

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertBuilder.show();
            }
        });

        setupTabs();
    }

    public void clearHistory() {
        pastHistory = new Stack<String>();
        webView.clearHistory();
        Log.d("CLEAR", "history deleted");
        saveHistory();
    }

    public void saveHistory() {
        Log.d("SAVE", "saving history");
        webView.saveState(instanceState);
        history = new Stack<String>();
        // need to have previous history at start
        for (int i=0; i<pastHistory.size(); i++) {
            history.add(pastHistory.get(i));
        }
        // now add the current session's history
        WebBackForwardList backForwardList = webView.copyBackForwardList();
        for (int i=0; i<backForwardList.getSize(); i++) {
            history.add(backForwardList.getItemAtIndex(i).getUrl());
        }
        Log.d("SAVE", "history saved");
    }
}
