package com.example.gauti.cobra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gauti on 01/06/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    private String smsBody;
    private String address;
    private HomeFragment instHome = HomeFragment.getInstance();
    private LocalisationFragment instLoc = LocalisationFragment.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                if (smsMessage.getOriginatingAddress().equals(ApplicationSharedPreferences.getInstance(context).getSettingsNumero())) {
                    smsBody = smsMessage.getMessageBody().toString();
                    address = smsMessage.getOriginatingAddress();

                    if (smsBody.contains("Cut off")) { //Lock
                        instHome.lockSuccess(true);
                    } else if (smsBody.contains("Restore")) { //Unlock
                        instHome.lockSuccess(false);
                    } else if (smsBody.contains("Lat")) { //Localisation
                        extractData(smsBody, smsMessage.getTimestampMillis());
                    }
                }
            }
            Log.i("SMS RETURN", "SMS From: " + address + "\n" + smsBody + "\n");
        }
    }

    private void extractData(String data, long timeStamp) {
        Double latitude;
        Double longitude;
        String speed;
        String date = null;

        latitude = Double.parseDouble(data.substring(data.indexOf("Lat:") + 5, data.indexOf(",")));
        longitude = Double.parseDouble(data.substring(data.indexOf("Lon:") + 5, data.indexOf(",", data.indexOf(",") + 1)));
        speed = data.substring(data.indexOf("Speed:") + 6, data.indexOf(",", data.indexOf("Speed:")));

        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date netDate = (new Date(timeStamp));
            date = sdf.format(netDate);
        } catch (Exception e) {
            e.getStackTrace();
        }
        Log.i("LatLong", "Lat : " + latitude + " - Long : " + longitude + " - Time : " + date);

        instLoc.addMarker(latitude, longitude, speed, date);
    }
}
