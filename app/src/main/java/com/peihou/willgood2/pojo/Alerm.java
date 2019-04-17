package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class Alerm implements Serializable {
    /**
     * "deviceAlarmName8": "开关量报警,请注意!",
     *         "deviceAlarmName7": "功率报警,请注意!",
     *         "deviceAlarmName6": "电流报警,请注意!",
     *         "deviceAlarmName5": "电压报警,请注意!",
     *         "deviceAlarmName3": "温度报警,请注意!",
     *         "deviceAlarmName2": "设备已经断电，请及时处理!",
     *         "deviceAlarmName1": "设备已经来电!",
     *         "deviceAlarmBroadcast": 0,
     *         "deviceId": 5,
     *         "deviceAlarmNam4": "湿度报警,请注意!",
     *         "deviceAlarmId": 1003,
     *         "deviceAlarmFlag": 0
     */
    static final long serialVersionUID = 42L;


    @Id(autoincrement = true)
    private Long id;
    private String deviceMac;
    private String name;
    private int type;
    private String content;
    @Transient
    private boolean open;
    private int state;//报警开关状态
    private long deviceId;
    private double value;
    private int deviceAlarmBroadcast=1;//语音播报
    private int deviceAlarmFlag=1;//弹框提醒
    private int state2;//报警值高于，低于 0x11高于 0x22低于
    public Alerm(String name, int type,String content, boolean open,long deviceId,String deviceMac,double value) {
        this.name = name;
        this.type = type;
        this.content=content;
        this.open = open;
        this.deviceId=deviceId;
        this.deviceMac=deviceMac;
        this.value=value;
    }


    @Generated(hash = 1517740106)
    public Alerm(Long id, String deviceMac, String name, int type, String content, int state, long deviceId,
            double value, int deviceAlarmBroadcast, int deviceAlarmFlag, int state2) {
        this.id = id;
        this.deviceMac = deviceMac;
        this.name = name;
        this.type = type;
        this.content = content;
        this.state = state;
        this.deviceId = deviceId;
        this.value = value;
        this.deviceAlarmBroadcast = deviceAlarmBroadcast;
        this.deviceAlarmFlag = deviceAlarmFlag;
        this.state2 = state2;
    }


    @Generated(hash = 1949804767)
    public Alerm() {
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

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

  

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return this.id;
    }


    public double getValue() {
        return this.value;
    }


    public void setValue(double value) {
        this.value = value;
    }


    public int getState2() {
        return this.state2;
    }


    public void setState2(int state2) {
        this.state2 = state2;
    }
}
