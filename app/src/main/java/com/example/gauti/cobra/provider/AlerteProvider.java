package com.example.gauti.cobra.provider;

import android.content.Context;
import android.util.Log;

import com.example.gauti.cobra.database.DatabaseHelper;
import com.example.gauti.cobra.model.Alerte;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobilefactory on 31/01/2017.
 */

public class AlerteProvider {

    // Constants
    // --------------------------------------------------------------------------------------------
    private static final String LOG_TAG = AlerteProvider.class.getSimpleName();

    public static void setAlerte(Context context, Alerte alerte) {
        try {
            if (context != null && alerte != null) {
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                Dao<Alerte, String> daoScore = helper.getAlerteDao();

                daoScore.create(alerte);

                helper.close();
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "An error occurred while saving progression event into database", e);
        }
    }

    public static List<Alerte> getAlerte(Context context) {
        try {
            if (context != null) {
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                Dao<Alerte, String> daoAlerte = helper.getAlerteDao();

                List<Alerte> alerteList = daoAlerte.queryForAll();

                return alerteList;
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "An error occurred while retrieving progression events from database", e);
        }
        return new ArrayList<>();
    }

    public static void deleteAllAlerte(Context context) {
        try {
            if (context != null) {
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                Dao<Alerte, String> daoAlerte = helper.getAlerteDao();

                daoAlerte.delete(daoAlerte.queryForAll());

                helper.close();
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "An error occurred while retrieving progression events from database", e);
        }
    }

    public static void deleteAlerte(Context context, String id) {
        try {
            if (context != null) {
                DatabaseHelper helper = DatabaseHelper.getInstance(context);
                Dao<Alerte, String> daoAlerte = helper.getAlerteDao();

                daoAlerte.deleteById(id);

                helper.close();
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "An error occurred while retrieving progression events from database", e);
        }
    }
}
