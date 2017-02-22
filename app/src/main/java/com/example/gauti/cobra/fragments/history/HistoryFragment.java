package com.example.gauti.cobra.fragments.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.event.EventAlerte;
import com.example.gauti.cobra.fragments.CobraFragment;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryFragment extends CobraFragment implements OnMapReadyCallback {

    // Private fields
    // --------------------------------------------------------------------------------------------
    private HistoryAdapterMenu historyAdapterMenu;
    private GoogleMap googleMap;
    private MapView mapView;

    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.lvHistoryMenu)
    ListView lvHistory;

    @Bind(R.id.rlMapHostoryMenu)
    RelativeLayout rlMapHostoryMenu;

    @Bind(R.id.btn_delete_all)
    Button btnDeleteAll;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        refreshList();

        if (googleMap == null) {
            Log.i("GOOGLEMAP_HISTORY", "START");
            mapView = (MapView) view.findViewById(R.id.mapHistoryMenu);
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (googleMap != null) {
            Log.i("GOOGLEMAP_HISTORY", "STOP");
            googleMap.clear();
            googleMap = null;
            mapView.onDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    private void refreshList() {
        List<Alerte> alertes = AlerteProvider.getAlerte(getActivity());
        historyAdapterMenu = new HistoryAdapterMenu(getActivity(), alertes);
        lvHistory.setAdapter(historyAdapterMenu);
    }

    // OnClick
    // --------------------------------------------------------------------------------------------
    @OnItemClick(R.id.lvHistoryMenu)
    void onItemClickHistoryMenu(int position) {
        List<Alerte> alerteList = AlerteProvider.getAlerte(getActivity());
        Alerte alerte = alerteList.get(position);
        addMarker(alerte.getLatitude(), alerte.getLongitude(), alerte.getSpeed(), alerte.getDate());
        mapVisible(true);
    }

    @OnClick(R.id.ivExit)
    void onClickExitMap() {
        mapVisible(false);
    }

    @OnClick(R.id.btn_delete_all)
    void onClickDeleteAll() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.popover_history_delete_all_title))
                .setMessage(getActivity().getResources().getString(R.string.popover_history_delete_all_text))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlerteProvider.deleteAllAlerte(getActivity());
                        refreshList();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .create().show();
    }

    // GoogleMap
    // --------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Log.i("GOOGLEMAP_HISTORY", "START2");
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void addMarker(Double latitude, Double longitude, String speed, String date) {
        googleMap.clear();

        LatLng point = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("vit : " + speed + " - " + date);
        markerOptions.visible(true);
        markerOptions.position(point);


        //zoom de la caméra sur la position qu'on désire afficher
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
        //animation le zoom toute les 2000ms
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        //ajout du marqueur sur la carte
        googleMap.addMarker(markerOptions);
    }

    private void mapVisible(boolean b) {
        if (b) {
            rlMapHostoryMenu.setVisibility(View.VISIBLE);
            btnDeleteAll.setVisibility(View.GONE);
        } else {
            rlMapHostoryMenu.setVisibility(View.GONE);
            btnDeleteAll.setVisibility(View.VISIBLE);
        }
    }

    // EventBus
    // --------------------------------------------------------------------------------------------
    public void onEvent(EventAlerte event) {
        refreshList();
    }
}
