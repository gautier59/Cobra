package com.example.gauti.cobra.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.gauti.cobra.MainActivity;
import com.example.gauti.cobra.R;
import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class LocalisationFragment extends CobraFragment implements OnMapReadyCallback {

    // Private fields
    // --------------------------------------------------------------------------------------------
    protected boolean mIsShowingLegend;
    private static LocalisationFragment inst;
    private int etapes = 0;
    private boolean locFirst = false;
    private boolean run = false;
    private int timeSms;

    ArrayList<LatLng> points = null;
    PolylineOptions polyLineOptions = null;

    private ArrayList<com.google.android.gms.maps.model.Marker> mMarkers = new ArrayList<>();

    private GoogleMap googleMap;
    private MapView mapView;


    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.rv_history)
    protected RecyclerView mRvHistory;

    @Bind(R.id.tv_marker_info)
    TextView mTvMarkerInfo;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    public static LocalisationFragment getInstance() {
        if (inst == null) {
            return new LocalisationFragment();
        }
        return inst;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localisation, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        timeSms = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsDelai();

        if (googleMap == null) {
            Log.i("GOOGLEMAP", "START");
            mapView = (MapView) view.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().getString(MainActivity.DATE) != null) {
            ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setDateSms(getArguments().getString(MainActivity.DATE));
            Double latitude = getArguments().getDouble(MainActivity.LATITUDE, 0);
            Double longitude = getArguments().getDouble(MainActivity.LONGITUDE, 0);
            String speed = getArguments().getString(MainActivity.SPEED);
            String date = getArguments().getString(MainActivity.DATE);
            locFirst = true;
            addMarker(latitude, longitude, speed, date);
        }

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
            //getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.map)).commitAllowingStateLoss();
            googleMap.clear();
            googleMap = null;
            mapView.onDestroy();
        }
        mMarkers.clear();
        EventBus.getDefault().unregister(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Log.i("GOOGLEMAP", "START2");
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

        final GoogleMap mGoogleMap = googleMap;
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getResources().getString(R.string.popover_location_no_activated_title))
                            .setMessage(getResources().getString(R.string.popover_location_no_activated_text))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                } else {
                    if (mGoogleMap.getMyLocation() != null) {
                        LatLng loc = new LatLng(mGoogleMap.getMyLocation().getLatitude(), mGoogleMap.getMyLocation().getLongitude());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    }
                }
                return true;
            }
        });
    }

    @OnClick(R.id.btn_search)
    void onClickSearch() {
        if (sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()))) {
            locFirst = true;
        }
    }

    @OnClick(R.id.btn_stop)
    void onClickStop() {
        locFirst = false;
        run = false;
    }

    @OnClick(R.id.btn_play)
    void onClickPlay() {
        if (sendSMSMessage(getResources().getString(EnumSms.WHERE.getSms()))) {
            locFirst = false;
            run = true;
            launchSearch();
        }
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
                mTvMarkerInfo.setText(marker.getTitle());
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

    private void setHistorique() {
        LinearLayoutManager teamLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        teamLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvHistory.setLayoutManager(teamLinearLayoutManager);
        //POIAdapter poiAdapter = new POIAdapter(getActivity(), map.getPoi());
        //mRvHistory.setAdapter(poiAdapter);
    }

    // EventBus
    // --------------------------------------------------------------------------------------------
    public void onEvent(MainActivity.HistoryClickEvent event) {
        if (!mIsShowingLegend) {
            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mRvHistory.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mRvHistory.startAnimation(slideUp);
            mIsShowingLegend = true;
        } else {
            Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRvHistory.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mRvHistory.startAnimation(slideDown);
            mIsShowingLegend = false;
        }

    }
}
