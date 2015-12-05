package io.b1ackr0se.carrental.util;

import android.content.Context;
import android.widget.Toast;

public class Utility {
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
