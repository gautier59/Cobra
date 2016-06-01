package com.example.gauti.cobra;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

public class HomeFragment extends Fragment {

    private Button btn_localisation;
    private TextView tv_name;
    private ImageView iv_picture;
    private ImageButton btn_lock;
    private ImageButton btn_unlock;

    private NavigationView navigationView;
    private static HomeFragment inst;

    private String mUrlImg;
    private String mName = "";

    public static HomeFragment getInstance() {
        return inst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btn_localisation = (Button) view.findViewById(R.id.btn_home_localiser);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_picture = (ImageView) view.findViewById(R.id.iv_home_picture);
        btn_lock = (ImageButton) view.findViewById(R.id.btn_lock);
        btn_unlock = (ImageButton) view.findViewById(R.id.btn_unlock);

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},1);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if ((ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsPicture() != null)
                && (ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)) {
            mUrlImg = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsPicture();
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

    private void refresh() {
        if (mName != null) {
            tv_name.setText(mName);
        }

        if (mUrlImg != null && !mUrlImg.equals("")) {
            Picasso.with(getActivity()).load(mUrlImg).fit().centerInside().into(iv_picture);
        } else {
            int paddingPicture = getActivity().getResources().getDimensionPixelSize(R.dimen.padding_picture_home);
            iv_picture.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            iv_picture.setPadding(paddingPicture, paddingPicture, paddingPicture, paddingPicture);
        }
    }

    protected void sendSMSMessage(String message) {
        String phoneNo = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
        Log.i("Send SMS", message + " to " + phoneNo);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getActivity().getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void Success() {
        Toast.makeText(getActivity(), "Commande réussi.", Toast.LENGTH_SHORT).show();
    }
}
