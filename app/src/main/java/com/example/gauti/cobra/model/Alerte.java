package com.example.gauti.cobra.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mobilefactory on 31/01/2017.
 */

@DatabaseTable(tableName = "alerte")
public class Alerte {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String speed;

    @DatabaseField
    public String date;

    @DatabaseField
    public Double latitude;

    @DatabaseField
    public Double longitude;

    public Alerte(String speed, String date, Double latitude, Double longitude) {
        this.speed = speed;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Alerte() {
    }

    public String getSpeed() {
        return speed;
    }

    public String getDate() {
        return date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
