package com.example.gauti.cobra.global;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Created by gauti on 01/06/2016.
 */
public class ApplicationSharedPreferences {

    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String LOG_TAG = ApplicationSharedPreferences.class.getSimpleName();

    private static final String PREFS_NAME = "CorbaAppPreferences";

    private static final String SETTINGS_NAME = "settingsName";
    private static final String SETTINGS_NUMERO = "settingsNumero";
    private static final String SETTINGS_DELAI = "settingsDelai";

    private static final String DATE_SMS = "dateSms";


    // Private fields
    // ---------------------------------------------------------------------------------------------
    private static ApplicationSharedPreferences instance;
    private static SharedPreferences applicationSharedPreferences;
    private static SharedPreferences.Editor editor;

    // Singleton
    // ---------------------------------------------------------------------------------------------
    public static ApplicationSharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationSharedPreferences(context);
        }
        return instance;
    }

    // Constructor
    // ---------------------------------------------------------------------------------------------
    private ApplicationSharedPreferences(Context context) {
        applicationSharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = applicationSharedPreferences.edit();
    }

    // Public methods
    // ---------------------------------------------------------------------------------------------
    public String getSettingsNumero() {
        return applicationSharedPreferences.getString(SETTINGS_NUMERO, null);
    }

    public void setSettingsNumero(String num) {
        editor.putString(SETTINGS_NUMERO, num);
        editor.commit();
    }

    public String getSettingsName() {
        return applicationSharedPreferences.getString(SETTINGS_NAME, null);
    }

    public void setSettingName(String name) {
        editor.putString(SETTINGS_NAME, name);
        editor.commit();
    }

    public int getSettingsDelai() {
        return applicationSharedPreferences.getInt(SETTINGS_DELAI, -1);
    }

    public void setSettingDelai(int delai) {
        editor.putInt(SETTINGS_DELAI, delai);
        editor.commit();
    }

    public String getDateSms() {
        return applicationSharedPreferences.getString(DATE_SMS, "");
    }

    public void setDateSms(String dateSms) {
        editor.putString(DATE_SMS, dateSms);
        editor.commit();
    }
}
