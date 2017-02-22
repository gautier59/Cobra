package com.example.gauti.cobra.fragments;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gauti.cobra.MainActivity;
import com.example.gauti.cobra.R;
import com.example.gauti.cobra.enumeration.EnumSms;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends CobraFragment {

    // Private fields
    // --------------------------------------------------------------------------------------------
    private NavigationView mNavigationView;
    private static HomeFragment inst;

    private String mName = "";

    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.tv_name)
    TextView mTvName;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static HomeFragment getInstance() {
        return inst;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if ((ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)) {
            mName = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName();

            refresh();
        }
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

    @OnClick(R.id.btn_home_localiser)
    void onClickLocaliser() {
        mNavigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        mNavigationView.getMenu().getItem(1).setChecked(true);
        ((MainActivity) getActivity()).onNavigationItemSelected(mNavigationView.getMenu().getItem(1));
    }

    @OnClick(R.id.btn_lock)
    void onClickLock() {
        sendSMSMessage(getResources().getString(EnumSms.LOCK.getSms()));
    }

    @OnClick(R.id.btn_unlock)
    void onClickUnlock() {
        sendSMSMessage(getResources().getString(EnumSms.UNLOCK.getSms()));
    }

    private void refresh() {
        if (mName != null) {
            mTvName.setText(mName);
        }
    }

    public void lockSuccess(boolean lock) {
        String message = null;
        if (lock) {
            message = "Voiture vérouillé";
        } else {
            message = "Voiture dévérouillé";
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
