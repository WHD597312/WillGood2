package com.peihou.willgood2.pojo;

import java.io.Serializable;

public class DeviceTrajectory implements Serializable {
     private String deviceTrajectoryTime;
     private int deviceTrajectoryId;
     private double  deviceTrajectoryLatitude;
     private double deviceTrajectoryLongitude;
     private int deviceId;
     private String address;

    public String getDeviceTrajectoryTime() {
        return deviceTrajectoryTime;
    }

    public void setDeviceTrajectoryTime(String deviceTrajectoryTime) {
        this.deviceTrajectoryTime = deviceTrajectoryTime;
    }

    public int getDeviceTrajectoryId() {
        return deviceTrajectoryId;
    }

    public void setDeviceTrajectoryId(int deviceTrajectoryId) {
        this.deviceTrajectoryId = deviceTrajectoryId;
    }

    public double getDeviceTrajectoryLatitude() {
        return deviceTrajectoryLatitude;
    }

    public void setDeviceTrajectoryLatitude(double deviceTrajectoryLatitude) {
        this.deviceTrajectoryLatitude = deviceTrajectoryLatitude;
    }

    public double getDeviceTrajectoryLongitude() {
        return deviceTrajectoryLongitude;
    }

    public void setDeviceTrajectoryLongitude(double deviceTrajectoryLongitude) {
        this.deviceTrajectoryLongitude = deviceTrajectoryLongitude;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
