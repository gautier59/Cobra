package com.example.gauti.cobra.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gauti.cobra.MainActivity;
import com.example.gauti.cobra.R;
import com.example.gauti.cobra.global.ApplicationSharedPreferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // Private fields
    // --------------------------------------------------------------------------------------------
    private String mName;
    private String mNumero;
    private int mDelai;

    private NavigationView navigationView;

    // Views
    // --------------------------------------------------------------------------------------------
    @Bind(R.id.et_name)
    EditText mEtName;

    @Bind(R.id.et_number)
    EditText mEtNumber;

    @Bind(R.id.spinnerTime)
    Spinner mSpiDelai;

    // Life cycle
    // --------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Spinner click listener
        mSpiDelai.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> time = new ArrayList<String>();
        time.add("30 sec");
        time.add("45 sec");
        time.add("1 min");
        time.add("1 min 15");
        time.add("1 min 30");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, time);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mSpiDelai.setAdapter(dataAdapter);

        if ((ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)
                && (ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero() != null)) {
            mName = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName();
            mNumero = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
            mDelai = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsDelai();

            updateTextAndTelNumber();
        }
    }

    @OnClick(R.id.btn_settings_sauv)
    void onClickSave() {
        ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingName(mEtName.getText().toString());
        ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingsNumero(mEtNumber.getText().toString().replaceFirst("0", "+33"));

        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        ((MainActivity) getActivity()).onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    private void updateTextAndTelNumber() {
        if (mName != null) {
            mEtName.setText(mName);
        } else {
            mEtName.setText("");
        }

        if (mNumero != null) {
            mEtNumber.setText(mNumero);
        } else {
            mEtNumber.setText("");
        }

        mSpiDelai.setSelection(mDelai);
    }

    // Listener
    // --------------------------------------------------------------------------------------------
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
        ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingDelai(i);

        // Showing selected spinner item
        //Toast.makeText(adapterView.getContext(), "Selected: " + item + " : " + i, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
