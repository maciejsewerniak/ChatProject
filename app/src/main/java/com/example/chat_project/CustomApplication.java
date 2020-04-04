package com.example.chat_project;

import android.app.Application;

public class CustomApplication extends Application {

    private static CustomApplication application;

    public static CustomApplication getContext() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
