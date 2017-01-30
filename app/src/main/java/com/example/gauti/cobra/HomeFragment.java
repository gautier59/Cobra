package com.example.gauti.cobra;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.squareup.picasso.Picasso;

public class HomeFragment extends CobraFragment {

    private Button btn_localisation;
    private TextView tv_name;
    private ImageButton btn_lock;
    private ImageButton btn_unlock;

    private NavigationView navigationView;
    private static HomeFragment inst;

    private String mName = "";

    public static HomeFragment getInstance() {
        if (inst == null) {
            return new HomeFragment();
        }
        return inst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btn_localisation = (Button) view.findViewById(R.id.btn_home_localiser);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        btn_lock = (ImageButton) view.findViewById(R.id.btn_lock);
        btn_unlock = (ImageButton) view.findViewById(R.id.btn_unlock);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if ((ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)) {
            mName = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName();

            refresh();
        }

        btn_localisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(1).setChecked(true);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new LocalisationFragment());
                transaction.commit();
            }
        });

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(getResources().getString(EnumSms.LOCK.getSms()));
            }
        });

        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(getResources().getString(EnumSms.UNLOCK.getSms()));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        inst = null;
    }

    private void refresh() {
        if (mName != null) {
            tv_name.setText(mName);
        }
    }

    protected void lockSuccess(boolean lock) {
        String message = null;
        if (lock) {
            message = "Voiture vérouillé";
        } else {
            message = "Voiture dévérouillé";
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
