package com.example.gauti.cobra.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gauti.cobra.model.Alerte;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by mobilefactory on 31/01/2017.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // Constants
    // --------------------------------------------------------------------------------------------
    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "alerte.db";
    private static final int DATABASE_VERSION = 1;

    // Private members
    // --------------------------------------------------------------------------------------------
    private static DatabaseHelper sInstance;
    private static AtomicInteger usageCounter = new AtomicInteger(0);
    private Dao<Alerte, Integer> mAlerteDao;

    // Singleton
    // --------------------------------------------------------------------------------------------
    public static DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context);
        }
        usageCounter.incrementAndGet();
        return sInstance;
    }

    // Constructor
    // --------------------------------------------------------------------------------------------
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Life cycle methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Alerte.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Alerte.class, true);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Can't drop database", e);
            throw new RuntimeException(e);
        }
    }

    // DAOs
    // --------------------------------------------------------------------------------------------
    public Dao<Alerte, Integer> getAlerteDao() throws SQLException {
        if (mAlerteDao == null) {
            mAlerteDao = getDao(Alerte.class);
        }
        return mAlerteDao;
    }

    // Close method
    // --------------------------------------------------------------------------------------------

    @Override
    public void close() {
        if (usageCounter.decrementAndGet() == 0) {
            super.close();
            mAlerteDao = null;
            sInstance = null;
        }
    }
}
