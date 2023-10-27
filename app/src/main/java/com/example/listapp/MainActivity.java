package com.example.listapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceDataStore;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
//import Gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private Controller controller;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String HISTORY = "history";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new Controller(this, savedInstanceState);
        loadData();
    }

    public void onBackPressed(){
        Log.d("ONBACKPRESSED","Calling controller on back..");
        controller.onBackPress();
    }

//    @Override
//    public void onStop() {
//        Log.d("DESTROY", "onStop");
//        controller.saveHistory();
//        Log.d("TEST", "BEFORE");
//        saveData();
//        Log.d("TEST", "AFTER");
//        super.onStop();
//    }

    @Override
    public void onPause() {
        Log.d("PAUSE", "onPause");
        controller.saveHistory();
//        Log.d("TEST", "BEFORE");
        saveData();
//        Log.d("TEST", "AFTER");
        super.onPause();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(controller.getHistory());
        editor.putString(HISTORY, json);
        editor.apply();
        Log.d("SAVE", "History Successfully Saved");
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String gsonHistory = sharedPreferences.getString(HISTORY, "");

        if (gsonHistory.length() == 0) {
            Log.d("LOAD", "No History To Load");
            return;
        }

        Gson gson = new Gson();
        Stack<String> history = gson.fromJson(gsonHistory, Stack.class);
        controller.loadHistory(history);
        Log.d("LOAD", "History Successfully Loaded");

        for (int i=0; i<history.size(); i++) {
            Log.d("HISTORYLIST", i + ": " + history.get(i));
        }
    }
}