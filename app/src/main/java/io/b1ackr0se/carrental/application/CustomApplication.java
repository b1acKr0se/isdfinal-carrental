package io.b1ackr0se.carrental.application;

import android.app.Application;

import com.parse.Parse;

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gqIxl8ihURsASgs8u3U8R7qruU5xoMreWqeQXOZQ", "JcNBpoxzUXhArQHfCAmkM9LWDHZUxDRlmxeQH9D4");
    }
}
