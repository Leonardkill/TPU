package com.todasporuma.model;

public class Event {

    private String address;
    private Double latitude;
    private Double longitude;
    private String nameReciver;
    private String phoneReciver;


    public Event() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNameReciver() {
        return nameReciver;
    }

    public void setNameReciver(String nameReciver) {
        this.nameReciver = nameReciver;
    }

    public String getPhoneReciver() {
        return phoneReciver;
    }

    public void setPhoneReciver(String phoneReciver) {
        this.phoneReciver = phoneReciver;
    }
}
