package com.peihou.willgood2.pojo;

import com.baidu.mapapi.model.LatLng;

public class Position {
    int position;
    private String address;
    LatLng latLng;

    public Position(int position, String address, LatLng latLng) {
        this.position = position;
        this.address = address;
        this.latLng = latLng;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
