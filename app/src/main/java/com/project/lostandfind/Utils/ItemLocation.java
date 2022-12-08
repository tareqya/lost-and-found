package com.project.lostandfind.Utils;

public class ItemLocation {

    private String locationName;
    private double longitude;
    private double latitude;

    public ItemLocation(){}

    public String getLocationName() {
        return locationName;
    }

    public ItemLocation setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public ItemLocation setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public ItemLocation setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getDist(ItemLocation other){
        double dx = this.latitude - other.latitude;
        double dy = this.longitude - other.longitude;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

    }
}
