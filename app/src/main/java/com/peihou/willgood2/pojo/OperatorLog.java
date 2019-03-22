package com.peihou.willgood2.pojo;

public class OperatorLog {
    private String username;
    private String timer;
    private boolean open;


    String deviceLine;
    String deviceLogTime;
    String deviceLogType;
    String deviceName;
    String userPhone;
    int deviceControll;

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getDeviceControll() {
        return deviceControll;
    }

    public void setDeviceControll(int deviceControll) {
        this.deviceControll = deviceControll;
    }

    public String getDeviceLine() {
        return deviceLine;
    }

    public void setDeviceLine(String deviceLine) {
        this.deviceLine = deviceLine;
    }

    public String getDeviceLogTime() {
        return deviceLogTime;
    }

    public void setDeviceLogTime(String deviceLogTime) {
        this.deviceLogTime = deviceLogTime;
    }

    public String getDeviceLogType() {
        return deviceLogType;
    }

    public void setDeviceLogType(String deviceLogType) {
        this.deviceLogType = deviceLogType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }




    public OperatorLog() {
    }

    public OperatorLog(String username, String timer, boolean open) {
        this.username = username;
        this.timer = timer;
        this.open = open;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
