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

    AlertDialog.Builder builderNoPhone;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builderNoPhone = new AlertDialog.Builder(getActivity());
        builderNoPhone.setTitle("Attention !");
        builderNoPhone.setMessage("Veuillez entrer le numéro de téléphone du traceur.");
        builderNoPhone.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(2).setChecked(true);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new SettingsFragment());
                transaction.commit();
            }
        });
        builderNoPhone.setNegativeButton("Annuler", null);
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
