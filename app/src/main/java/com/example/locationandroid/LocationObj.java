package com.example.locationandroid;

public class LocationObj {
    public static double latitude;
    public static double longitude;

    public LocationObj() {
    }

    public LocationObj(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
