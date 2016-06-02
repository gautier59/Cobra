package com.example.gauti.cobra;

import android.*;
import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalisationFragment extends Fragment {

    private GoogleMap googleMap;
    static final LatLng point = new LatLng(21 , 57);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_localisation, container, false);
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        try {
            if(googleMap == null) {
                googleMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
            }

            if(googleMap != null) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.setMyLocationEnabled(true);

                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("YAOUNDE");
                markerOptions.visible(true);
                markerOptions.position(point);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                //ajout du marqueur sur la carte
                googleMap.addMarker(markerOptions);
                //zoom de la caméra sur la position qu'on désire afficher
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                //animation le zoom toute les 2000ms
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);*/
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(googleMap != null) {
            getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.map)).commit();
            googleMap = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
