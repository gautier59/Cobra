package com.example.gauti.cobra;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.gauti.cobra.global.ApplicationSharedPreferences;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    // Private fields
    // --------------------------------------------------------------------------------------------
    private EditText et_name;
    private EditText et_number;
    private Button btn_sauv;
    private Spinner spiDelai;

    private String mName;
    private String mNumero;
    private int mDelai;

    private NavigationView navigationView;

    // Views
    // --------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_number = (EditText) view.findViewById(R.id.et_number);
        btn_sauv = (Button) view.findViewById(R.id.btn_settings_sauv);
        spiDelai = (Spinner) view.findViewById(R.id.spinnerTime);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_sauv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingName(et_name.getText().toString());
                ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).setSettingsNumero(et_number.getText().toString().replaceFirst("0","+33"));

                navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(0).setChecked(true);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment());
                transaction.commit();
            }
        });

        // Spinner click listener
        spiDelai.setOnItemSelectedListener(this);

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
        spiDelai.setAdapter(dataAdapter);

        if ((ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName() != null)
                && (ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero() != null)) {
            mName = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsName();
            mNumero = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsNumero();
            mDelai = ApplicationSharedPreferences.getInstance(getActivity().getApplicationContext()).getSettingsDelai();

            updateTextAndTelNumber();
        }
    }

    private void updateTextAndTelNumber() {
        if (mName != null) {
            et_name.setText(mName);
        } else {
            et_name.setText("");
        }

        if (mNumero != null) {
            et_number.setText(mNumero);
        } else {
            et_number.setText("");
        }

        spiDelai.setSelection(mDelai);
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
