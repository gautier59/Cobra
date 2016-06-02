package com.example.gauti.cobra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;

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
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                if (smsMessage.getOriginatingAddress().equals(ApplicationSharedPreferences.getInstance(context).getSettingsNumero())) {
                    smsBody = smsMessage.getMessageBody().toString();
                    address = smsMessage.getOriginatingAddress();

                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";
                    if (smsBody.contains("Success!")) {
                        instHome.Success();
                    } else if (smsBody.contains("Lat")) {
                        extractData(smsBody);
                    }
                }
            }
            Log.i("SMS RETURN", smsMessageStr);
        }
    }

    private void extractData(String data) {
        Double latitude;
        Double longitude;

        latitude = Double.parseDouble(data.substring(data.indexOf("Lat:") + 5, data.indexOf(",")));
        longitude = Double.parseDouble(data.substring(data.indexOf("Lon:") + 5, data.indexOf(",", data.indexOf(",") + 1)));
        Log.i("LatLong", "Lat : " + latitude + " : Long " + longitude);

        instLoc.addMarker(latitude, longitude);
    }
}
