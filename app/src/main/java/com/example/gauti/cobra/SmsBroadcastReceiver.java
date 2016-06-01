package com.example.gauti.cobra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.AppLaunchChecker;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;

/**
 * Created by gauti on 01/06/2016.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    private String smsBody;
    private String address;
    private HomeFragment inst = HomeFragment.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                if(smsMessage.getOriginatingAddress().equals(ApplicationSharedPreferences.getInstance(context).getSettingsNumero())) {
                    smsBody = smsMessage.getMessageBody().toString();
                    address = smsMessage.getOriginatingAddress();

                    smsMessageStr += "SMS From: " + address + "\n";
                    smsMessageStr += smsBody + "\n";
                    if(smsBody.contains("Success!")) {
                        inst.Success();
                    }
                }
            }
            Log.i("SMS RETURN", smsMessageStr);
            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
        }
    }
}
