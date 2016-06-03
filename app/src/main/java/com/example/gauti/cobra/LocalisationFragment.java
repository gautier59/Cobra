package com.example.gauti.cobra;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;

public class LocalisationFragment extends Fragment {

    private static final int TIME_SMS = 60000;

    private static LocalisationFragment inst;
    private GoogleMap googleMap;
    private int etapes = 0;
    private ImageButton btn_search, btn_stop, btn_play;
    private boolean locFist = false;
    private boolean run = false;

    private ArrayList<com.google.android.gms.maps.model.Marker> mMarkers = new ArrayList<>();

    public static LocalisationFragment getInstance() {
        return inst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localisation, container, false);

        btn_search = (ImageButton) view.findViewById(R.id.btn_search);
        btn_stop = (ImageButton) view.findViewById(R.id.btn_stop);
        btn_play = (ImageButton) view.findViewById(R.id.btn_play);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            if (googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFist = true;
                sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()));
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFist = false;
                run = false;
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFist = false;
                run = true;
                sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()));
                launchSearch();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (googleMap != null) {
            getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.map)).commit();
            googleMap = null;
        }
        mMarkers.clear();
    }

    private void launchSearch() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (run) {
                    sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()));
                    launchSearch();
                }
            }
        }, TIME_SMS);
    }

    public void addMarker(Double latitude, Double longitude, String speed, String date) {
        googleMap.clear();
        if (locFist) {
            LatLng point = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(Integer.toString(etapes) + " : " + speed + "\n" + date);
            markerOptions.visible(true);
            markerOptions.position(point);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            //ajout du marqueur sur la carte
            googleMap.addMarker(markerOptions);
            //zoom de la caméra sur la position qu'on désire afficher
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
            //animation le zoom toute les 2000ms
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        } else {
            for (int i = 0; i < mMarkers.size(); i++) {
                googleMap.addMarker(new MarkerOptions().position(mMarkers.get(i).getPosition()).title(mMarkers.get(i).getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            LatLng point = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(Integer.toString(etapes) + " : " + speed + "\n" + date);
            markerOptions.visible(true);
            markerOptions.position(point);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            //ajout du marqueur sur la carte
            mMarkers.add(googleMap.addMarker(markerOptions));
            //zoom de la caméra sur la position qu'on désire afficher
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
            //animation le zoom toute les 2000ms
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

            etapes++;
        }
    }

    protected void sendSMSMessage(String message) {
        String phoneNo = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
        Log.i("Send SMS", message + " to " + phoneNo);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getActivity().getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
