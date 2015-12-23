package com.changhongit.loving.model;

public class GpsLocation {

    private Float longitude;

    private Float latitude;

    public GpsLocation() {
    }

    public GpsLocation(Float longitude, Float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }
}
