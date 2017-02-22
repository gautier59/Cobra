package com.example.gauti.cobra.fragments.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryFragment extends CobraFragment {

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

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnItemClick(R.id.lvHistoryMenu)
    void onItemClickHistoryMenu(int position) {
        mapVisible(true);
    }

    @OnClick(R.id.ivExit)
    void onClickExitMap() {
        mapVisible(false);
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

    private void refreshList() {
        List<Alerte> alertes = AlerteProvider.getAlerte(getActivity());
        historyAdapterMenu = new HistoryAdapterMenu(getActivity(), alertes);
        lvHistory.setAdapter(historyAdapterMenu);
    }

    // EventBus
    // --------------------------------------------------------------------------------------------
    public void onEvent(EventAlerte event) {
        refreshList();
    }
}
