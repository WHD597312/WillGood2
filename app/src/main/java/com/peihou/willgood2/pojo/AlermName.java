package com.peihou.willgood2.pojo;

public class AlermName {
     /**"deviceAlarmName8": "开关量报警,请注意!",
             "deviceAlarmName7": "功率报警,请注意!",
             "deviceAlarmName6": "电流报警,请注意!",
             "deviceAlarmName5": "电压报警,请注意!",
             "deviceAlarmName3": "温度报警,请注意!",
             "deviceAlarmName2": "设备已经断电，请及时处理!",
             "deviceAlarmName1": "设备已经来电!",
             "deviceAlarmBroadcast": 0,
             "deviceId": 5,
             "deviceAlarmName4": "湿度报警,请注意!",
             "deviceAlarmId": 1003,
             "deviceAlarmFlag": 0*/
     int deviceId;
     int deviceAlarmBroadcast;//语音报警次数
     int deviceAlarmFlag;//弹框提醒
     String deviceAlarmName1;//来电报警
     String deviceAlarmName2;//断电报警
    String  deviceAlarmName3;//温度报警
    String deviceAlarmName4;//湿度报警
    String deviceAlarmName5;//电压报警
    String deviceAlarmName6;//电流报警
    String deviceAlarmName7;//功率报警
    String deviceAlarmName8;//开关量报警;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceAlarmBroadcast() {
        return deviceAlarmBroadcast;
    }

    public void setDeviceAlarmBroadcast(int deviceAlarmBroadcast) {
        this.deviceAlarmBroadcast = deviceAlarmBroadcast;
    }

    public int getDeviceAlarmFlag() {
        return deviceAlarmFlag;
    }

    public void setDeviceAlarmFlag(int deviceAlarmFlag) {
        this.deviceAlarmFlag = deviceAlarmFlag;
    }

    public String getDeviceAlarmName1() {
        return deviceAlarmName1;
    }

    public void setDeviceAlarmName1(String deviceAlarmName1) {
        this.deviceAlarmName1 = deviceAlarmName1;
    }

    public String getDeviceAlarmName2() {
        return deviceAlarmName2;
    }

    public void setDeviceAlarmName2(String deviceAlarmName2) {
        this.deviceAlarmName2 = deviceAlarmName2;
    }

    public String getDeviceAlarmName3() {
        return deviceAlarmName3;
    }

    public void setDeviceAlarmName3(String deviceAlarmName3) {
        this.deviceAlarmName3 = deviceAlarmName3;
    }

    public String getDeviceAlarmName4() {
        return deviceAlarmName4;
    }

    public void setDeviceAlarmName4(String deviceAlarmName4) {
        this.deviceAlarmName4 = deviceAlarmName4;
    }

    public String getDeviceAlarmName5() {
        return deviceAlarmName5;
    }

    public void setDeviceAlarmName5(String deviceAlarmName5) {
        this.deviceAlarmName5 = deviceAlarmName5;
    }

    public String getDeviceAlarmName6() {
        return deviceAlarmName6;
    }

    public void setDeviceAlarmName6(String deviceAlarmName6) {
        this.deviceAlarmName6 = deviceAlarmName6;
    }

    public String getDeviceAlarmName7() {
        return deviceAlarmName7;
    }

    public void setDeviceAlarmName7(String deviceAlarmName7) {
        this.deviceAlarmName7 = deviceAlarmName7;
    }

    public String getDeviceAlarmName8() {
        return deviceAlarmName8;
    }

    public void setDeviceAlarmName8(String deviceAlarmName8) {
        this.deviceAlarmName8 = deviceAlarmName8;
    }
}
