package com.comp259.huber7517.comp262final;

public class Location {
    private int id;
    private double latitude;
    private double longitude;
    private String date;
    private String marker;

    public Location(int _id, double lat, double lon, String date, String marker){
        setId(_id);
        setLatitude(lat);
        setLongitude(lon);
        setDate(date);
        setMarker(marker);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }
}
