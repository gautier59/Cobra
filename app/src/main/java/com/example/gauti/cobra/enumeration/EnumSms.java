package com.example.gauti.cobra.enumeration;

import com.example.gauti.cobra.R;

/**
 * Created by gauti on 01/06/2016.
 */
public enum EnumSms {
    // Values
    // --------------------------------------------------------------------------------------------
    URL("0", R.string.sms_url),
    WHERE("1", R.string.sms_where),
    LOCK("2", R.string.sms_lock),
    UNLOCK("3", R.string.sms_unlock);

    // Fields
    // --------------------------------------------------------------------------------------------
    private String mId;
    private int mSms;

    // Constructor
    // --------------------------------------------------------------------------------------------
    EnumSms(String id, int sms) {
        mId = id;
        mSms = sms;
    }

    // Getters
    // --------------------------------------------------------------------------------------------
    public String getId() {
        return mId;
    }

    public int getSms() {
        return mSms;
    }

    // Static methods
    // --------------------------------------------------------------------------------------------
    public static EnumSms getSmsWithId(int id) {
        for (EnumSms language : values()) {
            if (Integer.parseInt(language.getId()) == id) {
                return language;
            }
        }
        return null;
    }
}
