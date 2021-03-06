package io.b1ackr0se.carrental.application;

import android.app.Application;

import com.parse.Parse;

public class CustomApplication extends Application {
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_USER = 1;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_BANNED = 1;

    public static final int ORDER_STATUS_PENDING = 0;
    public static final int ORDER_STATUS_DONE = 1;
    public static final int ORDER_STATUS_DENIED = 2;

    public static boolean isLoggedIn = false;
    public static int userType = -1;
    public static String userId;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "gqIxl8ihURsASgs8u3U8R7qruU5xoMreWqeQXOZQ", "JcNBpoxzUXhArQHfCAmkM9LWDHZUxDRlmxeQH9D4");
    }
}
