package io.b1ackr0se.carrental.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String formatDate(long time) {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return df.format(date);
    }

    public static String formatOrderDate(long time) {
        Date date = new Date(time);
        DateFormat df = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
        df.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        return df.format(date);
    }
}
