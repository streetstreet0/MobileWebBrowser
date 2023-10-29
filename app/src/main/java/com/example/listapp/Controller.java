package com.example.listapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ClientCertRequest;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
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
    private MainActivity activity;
    private Bundle instanceState;
    private Stack<String> history;
    private Stack<String> pastHistory; // used to refer to history before this session
    private String url;
    private final String defaultHomePage = "https:www.duckduckgo.com";
    private String homePage;
    private EditText input;
    private WebViewPlus webView;
    private TabWindow currentView;

    public Controller(MainActivity activity, Bundle savedInstanceState){
        if (savedInstanceState == null) {
            this.instanceState = new Bundle();
        }
        else {
            this.instanceState = savedInstanceState;
        }
        pastHistory = new Stack<String>();
        this.activity = activity;
        this.homePage = defaultHomePage;
    }

    public void start() {
        setupHomeScreen();
    }

    public Stack<String> getHistory() {
        saveHistory();
        return history;
    }

    public String getHomepage() {
        return homePage;
    }

    public void loadHistory(Stack<String> pastHistory) {
        this.pastHistory = pastHistory;
    }

    public void loadHomepage(String homePage) {
        this.homePage = homePage;
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

        Button homeButton = (Button) this.activity.findViewById(R.id.homeButton);
        homeButton.setOnClickListener((view -> {
            webView.loadUrl(homePage);
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

    private void setupTabs() {
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

    private void setupHistoryScreen() {
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

    private void setupSettingsScreen() {
        webView.saveState(instanceState);
        activity.setContentView(R.layout.activity_settings);
        currentView = TabWindow.SETTINGS;

        Button clearHistoryButton = (Button) activity.findViewById(R.id.clearHistoryButton);
        AlertDialog.Builder clearHistoryAlertBuilder = new AlertDialog.Builder(activity);
        clearHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistoryAlertBuilder.setTitle("Clear History?");
                clearHistoryAlertBuilder.setMessage("Clearing history is permanent. \nClearing history will close the current session.");
                clearHistoryAlertBuilder.setCancelable(true);
                clearHistoryAlertBuilder.setPositiveButton("Clear History", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearHistory(false);
                    }
                });

                clearHistoryAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                clearHistoryAlertBuilder.show();
            }
        });

        Button changeHomepageButton = (Button) activity.findViewById(R.id.changeHomepageButton);
        AlertDialog.Builder changeHomepageAlertBuilder = new AlertDialog.Builder(activity);
        changeHomepageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHomepageAlertBuilder.setTitle("Change Homepage");
                final EditText newHomepageInput = new EditText(activity);
                newHomepageInput.setHint("Type new homepage...");
                changeHomepageAlertBuilder.setView(newHomepageInput);
                changeHomepageAlertBuilder.setCancelable(true);

                changeHomepageAlertBuilder.setPositiveButton("Set Homepage", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeHomePage(String.valueOf(newHomepageInput.getText()));
                    }
                });

                changeHomepageAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                changeHomepageAlertBuilder.show();
            }
        });

//        Button changeThemeButton = (Button) activity.findViewById(R.id.changeThemeButton);
//        changeThemeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeTheme();
//            }
//        });

        Button clearAllButton = (Button) activity.findViewById(R.id.clearPermanentDataButton);
        AlertDialog.Builder clearAllAlertBuilder = new AlertDialog.Builder(activity);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllAlertBuilder.setTitle("Clear Permanent Data?");
                clearAllAlertBuilder.setMessage("Clearing permanent data will close the current session.");
                clearAllAlertBuilder.setCancelable(true);
                clearAllAlertBuilder.setPositiveButton("Clear Permanent Data", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        homePage = defaultHomePage;
                        Toast.makeText(activity, activity.getString(R.string.data_cleared), Toast.LENGTH_LONG).show();
                        clearHistory(true);
                    }
                });

                clearAllAlertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                clearAllAlertBuilder.show();
            }
        });

        setupTabs();
    }

    public void clearHistory(Boolean silent) {
        pastHistory = new Stack<String>();
        history = new Stack<String>();
        webView.clearHistory();
        webView.destroy();
        Log.d("CLEAR", "history deleted");
        if (!silent) {
            Toast.makeText(activity, activity.getString(R.string.history_cleared), Toast.LENGTH_LONG).show();
        }
        activity.saveData();
        activity.finish();
    }

    public void changeHomePage(String url) {
        homePage = parseUrl(url);
        Toast.makeText(activity, activity.getString(R.string.homepage_set), Toast.LENGTH_LONG).show();
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

//    public void changeTheme() {
//        Resources.Theme currentTheme = activity.getTheme();
//        Log.d("TEST", "Theme was: " + String.valueOf(currentTheme));
//        activity.setTheme(R.style.Theme_ThemeTwo);
//        Log.d("TEST", "Theme Changed to: " + String.valueOf(activity.getTheme()));
//        Log.d("TEST", "Theme Should be: " + String.valueOf(activity.(R.style.Theme_ThemeTwo));
//    }
}
