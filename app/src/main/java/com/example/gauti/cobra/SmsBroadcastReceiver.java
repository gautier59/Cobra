package com.example.gauti.cobra;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.gauti.cobra.fragments.HomeFragment;
import com.example.gauti.cobra.fragments.LocalisationFragment;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by gauti on 01/06/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    private static final int NOTIFICATION = new Random().nextInt();

    private String smsBody;
    private String address;
    private HomeFragment instHome = HomeFragment.getInstance();
    private LocalisationFragment instLoc = LocalisationFragment.getInstance();
    private Context context;
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        this.context = context;
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                Log.i("NUM sauv: ", ApplicationSharedPreferences.getInstance(context).getSettingsNumero() + " - Num recu:" + smsMessage.getOriginatingAddress());
                if (smsMessage.getOriginatingAddress().equals(ApplicationSharedPreferences.getInstance(context).getSettingsNumero())) {
                    smsBody = smsMessage.getMessageBody().toString();
                    address = smsMessage.getOriginatingAddress();

                    if (smsBody.contains("Cut off") && instHome != null) { //Lock
                        instHome.lockSuccess(true);
                    } else if (smsBody.contains("Restore") && instHome != null) { //Unlock
                        instHome.lockSuccess(false);
                    } else if (smsBody.contains("Lat")) { //Localisation
                        extractData(smsBody, smsMessage.getTimestampMillis(), true);
                    } else if (smsBody.contains("http")) {
                        extractData(smsBody, smsMessage.getTimestampMillis(), false);
                    }
                }
            }
            Log.i("SMS RETURN", "SMS From: " + address + "\n" + smsBody + "\n");
        }
    }

    private void extractData(String data, long timeStamp, boolean codeIsWHERE) {
        Double latitude = null;
        Double longitude = null;
        String speed = null;
        String date = null;

        if (codeIsWHERE) {
            latitude = Double.parseDouble(data.substring(data.indexOf("Lat:") + 5, data.indexOf(",")));
            longitude = Double.parseDouble(data.substring(data.indexOf("Lon:") + 5, data.indexOf(",", data.indexOf(",") + 1)));
            speed = data.substring(data.indexOf("Speed:") + 6, data.indexOf(",", data.indexOf("Speed:")));
        } else {
            latitude = Double.parseDouble(data.substring(data.indexOf("=") + 2, data.indexOf(",")));
            longitude = Double.parseDouble(data.substring(data.indexOf(",") + 2, data.length()));
            if (instLoc != null) {
                instLoc.setLocFirst(true);
            }
        }

        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date netDate = (new Date(timeStamp));
            date = sdf.format(netDate);
        } catch (Exception e) {
            e.getStackTrace();
        }
        Log.i("LatLong", "Lat : " + latitude + " - Long : " + longitude + " - Time : " + date);

        if (instLoc != null) {
            instLoc.addMarker(latitude, longitude, speed, date);
        } else {
            addNotification(context.getResources().getString(R.string.notification_text) + " - " + date, latitude, longitude, speed, date);

        }
        Alerte alerte = new Alerte(speed, date, latitude, longitude);
        AlerteProvider.setAlerte(context, alerte);
    }

    private void addNotification(String text, Double latitude, Double longitude, String speed, String date) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(MainActivity.LATITUDE, latitude);
        resultIntent.putExtra(MainActivity.LONGITUDE, longitude);
        resultIntent.putExtra(MainActivity.SPEED, speed);
        resultIntent.putExtra(MainActivity.DATE, date);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, NOTIFICATION, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(text)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setPriority(1)
                .setLights(Color.RED, 100, 600);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION, notification);
    }
}
