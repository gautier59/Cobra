package com.example.gauti.cobra;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;

/**
 * Created by gautier on 02/08/16.
 */
public class CobraFragment extends Fragment {

    // Private fields
    // --------------------------------------------------------------------------------------------
    AlertDialog.Builder builderNoPhone;
    private NavigationView mNavigationView;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builderNoPhone = new AlertDialog.Builder(getActivity());
        builderNoPhone.setTitle(getResources().getString(R.string.popover_no_phone_title));
        builderNoPhone.setMessage(getResources().getString(R.string.popover_no_phone_text));
        builderNoPhone.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mNavigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                mNavigationView.getMenu().getItem(2).setChecked(true);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SettingsFragment());
                transaction.commit();
            }
        });
    }

    protected boolean sendSMSMessage(String message) {
        boolean numPhoneIsSave = false;
        String phoneNo = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
        if (phoneNo != null) {
            Log.i("Send SMS", message + " to " + phoneNo);

            try {
                numPhoneIsSave = true;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getActivity().getApplicationContext(), "SMS envoyé", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "SMS non envoyé, veuillez réessayer", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            numPhoneIsSave = false;
            builderNoPhone.create().show();
        }
        return numPhoneIsSave;
    }
}
