package io.b1ackr0se.carrental.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String formatNotificationDate(long date) {
        SimpleDateFormat initFormat = new SimpleDateFormat(
                "MMM dd", Locale.US);
        SimpleDateFormat hours = new SimpleDateFormat("HH:mm",
                Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        String finalDateString = initFormat.format(calendar
                .getTime());
        Date now = new Date();
        String strDate = initFormat.format(now);
        if (finalDateString.equals(strDate)) {
            finalDateString = hours.format(calendar.getTime());
        } else {
            finalDateString = initFormat.format(calendar.getTime());
        }
        return finalDateString;
    }

    public static boolean isDateThisWeek(long time) {

        Date dateInQuestion = new Date(time);

        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);

        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date monday = c.getTime();
        Date nextMonday= new Date(monday.getTime()+7*24*60*60*1000);

        return dateInQuestion.after(monday) && dateInQuestion.before(nextMonday);

    }
}
