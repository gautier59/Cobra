package com.example.gauti.cobra.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.fragments.CobraFragment;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryFragment extends CobraFragment {

    @Bind(R.id.lvHistory)
    ListView lvHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        List<Alerte> alertes = AlerteProvider.getAlerte(getActivity());
        HistoryAdapter historyAdapter = new HistoryAdapter(getActivity(), alertes);
        lvHistory.setAdapter(historyAdapter);

        return view;
    }
}
