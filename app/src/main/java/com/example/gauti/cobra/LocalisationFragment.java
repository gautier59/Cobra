package com.example.gauti.cobra;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalisationFragment extends Fragment {

    private static LocalisationFragment inst;
    private GoogleMap googleMap;
    private int etapes = 0;
    private ImageButton btn_search, btn_stop, btn_play;
    private boolean loc1 = false;

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
                loc1 = true;
                sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()));
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc1 = false;
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loc1 = false;
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
    }

    private void launchSearch() {

    }

    public void addMarker(Double latitude, Double longitude) {
        LatLng point = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(etapes));
        markerOptions.visible(true);
        markerOptions.position(point);

        if (loc1) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        } else {
            if (etapes == 0) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
        }

        //ajout du marqueur sur la carte
        googleMap.addMarker(markerOptions);
        //zoom de la caméra sur la position qu'on désire afficher
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
        //animation le zoom toute les 2000ms
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
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
