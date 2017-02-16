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
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gauti.cobra.MainActivity;
import com.example.gauti.cobra.R;
import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.event.EventAlerte;
import com.example.gauti.cobra.fragments.history.HistoryAdapter;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
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
    private List<String> mCurrentDate = new LinkedList<String>();
    private Parcelable stateListView;


    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.lv_history)
    protected ListView mLvHistory;

    @Bind(R.id.tv_marker_info)
    TextView mTvMarkerInfo;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    public static LocalisationFragment getInstance() {
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
    public void onPause() {
        super.onPause();
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

    @OnItemClick(R.id.lv_history)
    public void onItemClick(int position) {
        int dateFound = -1;
        List<Alerte> alerteList = AlerteProvider.getAlerte(getActivity());
        Alerte alerte = alerteList.get(position);
        if (!mCurrentDate.isEmpty()) {
            for (int i = 0; i < mCurrentDate.size(); i++) {
                if (mCurrentDate.get(i).equals(alerte.getDate())) {
                    dateFound = i;
                }
            }
            if (dateFound != -1) {
                mCurrentDate.remove(dateFound);
                deleteMarker(alerte.getDate());
            } else {
                mCurrentDate.add(alerte.getDate());
                addMarker(alerte.getLatitude(), alerte.getLongitude(), alerte.getSpeed(), alerte.getDate());
            }
        } else {
            mCurrentDate.add(alerte.getDate());
            addMarker(alerte.getLatitude(), alerte.getLongitude(), alerte.getSpeed(), alerte.getDate());
        }
    }

    private void deleteMarker(String date) {
        for (int i = 0; i < mMarkers.size(); i++) {
            if (mMarkers.get(i).getTitle().contains(date)) {
                mMarkers.remove(i);
                addMarkerFromListMarker();
                etapes--;
            }
        }
    }

    private void addMarkerFromListMarker() {
        googleMap.clear();
        points = new ArrayList<LatLng>();
        polyLineOptions = new PolylineOptions();
        if (!mMarkers.isEmpty()) {
            LatLng point = null;
            for (int i = 0; i < mMarkers.size(); i++) {
                point = mMarkers.get(0).getPosition();
                googleMap.addMarker(new MarkerOptions().position(mMarkers.get(i).getPosition()).title(mMarkers.get(i).getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                points.add(mMarkers.get(i).getPosition());
            }

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

            polyLineOptions.addAll(points);
            polyLineOptions.width(4);
            polyLineOptions.color(Color.BLUE);
            googleMap.addPolyline(polyLineOptions);
        }
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
        HistoryAdapter historyAdapter = new HistoryAdapter(getActivity(), AlerteProvider.getAlerte(getActivity()));
        mLvHistory.setAdapter(historyAdapter);
    }

    // EventBus
    // --------------------------------------------------------------------------------------------
    public void onEvent(MainActivity.HistoryClickEvent event) {
        if (!mIsShowingLegend) {
            Animation slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mLvHistory.setVisibility(View.VISIBLE);
                    setHistorique();
                    if (stateListView != null) {
                        mLvHistory.onRestoreInstanceState(stateListView);
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLvHistory.startAnimation(slideUp);
            mIsShowingLegend = true;
        } else {
            Animation slideDown = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    stateListView = mLvHistory.onSaveInstanceState();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mLvHistory.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mLvHistory.startAnimation(slideDown);
            mIsShowingLegend = false;
        }
    }

    public void onEvent(EventAlerte event) {
        setHistorique();
    }
}
