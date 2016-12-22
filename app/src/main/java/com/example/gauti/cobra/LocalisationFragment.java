package com.example.gauti.cobra;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;

public class LocalisationFragment extends CobraFragment {

    private static LocalisationFragment inst;
    private GoogleMap googleMap;
    private int etapes = 0;
    private ImageButton btn_search, btn_stop, btn_play;
    private TextView tv_marker_info;
    private boolean locFirst = false;
    private boolean run = false;
    private int timeSms;

    ArrayList<LatLng> points = null;
    PolylineOptions polyLineOptions = null;

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
        tv_marker_info = (TextView) view.findViewById(R.id.tv_marker_info);

        timeSms = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsDelai();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (googleMap == null) {
                Log.i("GOOGLEMAP", "START");
                googleMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            if (googleMap != null) {
                Log.i("GOOGLEMAP", "START2");
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setMyLocationEnabled(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getArguments() != null && getArguments().getString(MainActivity.DATE) != null) {
            Double latitude = getArguments().getDouble(MainActivity.LATITUDE, 0);
            Double longitude = getArguments().getDouble(MainActivity.LONGITUDE, 0);
            String speed = getArguments().getString(MainActivity.SPEED);
            String date = getArguments().getString(MainActivity.DATE);
            locFirst = true;
            addMarker(latitude, longitude, speed, date);
        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()))) {
                    locFirst = true;
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locFirst = false;
                run = false;
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()))) {
                    locFirst = false;
                    run = true;
                    launchSearch();
                }
            }
        });

        switch (timeSms) {
            case 0:
                timeSms = 30000;
                break;
            case 1:
                timeSms = 45000;
                break;
            case 2:
                timeSms = 60000;
                break;
            case 3:
                timeSms = 75000;
                break;
            case 4:
                timeSms = 90000;
                break;
            default:
                timeSms = 30000;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (googleMap != null) {
            Log.i("GOOGLEMAP", "STOP");
            getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.map)).commitAllowingStateLoss();
            googleMap.clear();
            googleMap = null;
        }
        mMarkers.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onStop() {
        super.onStop();
        inst = null;
    }

    private void launchSearch() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (run) {
                    if (sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()))) {
                        launchSearch();
                    }
                }
            }
        }, timeSms);
    }

    public void addMarker(Double latitude, Double longitude, String speed, String date) {
        googleMap.clear();
        points = new ArrayList<LatLng>();
        polyLineOptions = new PolylineOptions();

        LatLng point = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(Integer.toString(etapes) + " : " + speed + " - " + date);
        markerOptions.visible(true);
        markerOptions.position(point);

        //zoom de la caméra sur la position qu'on désire afficher
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
        //animation le zoom toute les 2000ms
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                tv_marker_info.setText(marker.getTitle());
            }
        });

        if (locFirst) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

            //ajout du marqueur sur la carte
            googleMap.addMarker(markerOptions);
        } else {
            for (int i = 0; i < mMarkers.size(); i++) {
                googleMap.addMarker(new MarkerOptions().position(mMarkers.get(i).getPosition()).title(mMarkers.get(i).getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                points.add(mMarkers.get(i).getPosition());
            }
            points.add(point);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            //ajout du marqueur sur la carte
            mMarkers.add(googleMap.addMarker(markerOptions));

            etapes++;

            polyLineOptions.addAll(points);
            polyLineOptions.width(4);
            polyLineOptions.color(Color.BLUE);
            googleMap.addPolyline(polyLineOptions);
        }
    }

    public void setLocFirst(boolean locFirst) {
        this.locFirst = locFirst;
    }
}
