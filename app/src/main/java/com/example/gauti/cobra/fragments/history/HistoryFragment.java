package com.example.gauti.cobra.fragments.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.event.EventAlerte;
import com.example.gauti.cobra.fragments.CobraFragment;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class HistoryFragment extends CobraFragment {

    private HistoryAdapterMenu historyAdapterMenu;

    @Bind(R.id.lvHistory)
    ListView lvHistory;

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
