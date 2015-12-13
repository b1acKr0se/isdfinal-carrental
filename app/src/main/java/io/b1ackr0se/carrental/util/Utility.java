package io.b1ackr0se.carrental.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Locale;

public class Utility {
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showInfoDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Info")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String showCurrency(int amount) {
        return String.format(Locale.US, "%,d", amount).replace(',', '.');
    }
}
