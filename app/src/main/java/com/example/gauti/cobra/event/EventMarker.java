package com.example.gauti.cobra.event;

/**
 * Created by mobilefactory on 17/02/2017.
 */

public class EventMarker {
    private Double latitude;
    private Double longitude;
    private String date;
    private String Speed;

    public EventMarker(Double latitude, Double longitude, String date, String speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        Speed = speed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public String getSpeed() {
        return Speed;
    }
}
