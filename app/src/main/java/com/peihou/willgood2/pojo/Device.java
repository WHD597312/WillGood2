package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class Device implements Serializable {

    static final long serialVersionUID = 42L;


    @Id(autoincrement = false)
    private Long deviceId;
    private String deviceName;
    private String deviceOnlyMac;
    private String devicePassword;
    private int deviceSellerId;
    private int deviceCreatorId;
    private int deviceModel;
    private int choice;//选中状态
    @Transient
    private boolean isOpen;
    private String name;
    private String imei;
    private String share;
    int deviceAuthority_Alarm;//报警权限
    int deviceAuthority_Map;//地图定位权限
    int deviceAuthority_LineSwitch;//线路开关权限
    int deviceAuthority_Analog;//模拟量检测权限
    int deviceAuthority_Switch;//开关权限
    int deviceAuthority_Poweroff;//掉电记忆权限
    int deviceAuthority_Inching;//互锁权限
    int deviceAuthority_Timer;//定时权限
    int deviceAuthority_Lock;//互锁权限
    int deviceAuthority_Linked;//联动权限

    private int mcuVersion;//mcu版本号
    private int deviceState;//设备开关状态
    private int prelines;//设备的前8路线路
    private int lastlines;//设备的后8路线路
    private int prelineswitch;//设备的前8路线路的开关状态
    private int lastlineswitch;//设备的后8路线路的开关状态
    private int prelinesjog;//设备线路的前8路线路的点动状态
    private int lastlinesjog;//设备线路的后8路线路的点动状态
    private boolean online;//设备在线状态
    private int plMemory=1;//掉电记忆
    private double lineJog=1;//线路点动设定时间
    private double line;//线路1-16路点动剩余时间
    private double line2;
    private double line3;
    private double line4;
    private double line5;
    private double line6;
    private double line7;
    private double line8;
    private double line9;
    private double line10;
    private double line11;
    private double line12;
    private double line13;
    private double line14;
    private double line15;
    private double line16;

    private double temp;//温度
    private double hum;//湿度
    private double current;//电流
    private double votage;//电压
    private String re485;
    private int voice=1;//1设置成功 0设置失败




    public Device(String name) {
        this.name = name;
    }

    public Device(Long deviceId, String deviceName, String deviceOnlyMac, String devicePassword,String share, int deviceAuthority_Alarm, int deviceAuthority_Map, int deviceAuthority_LineSwitch, int deviceAuthority_Analog, int deviceAuthority_Switch, int deviceAuthority_Poweroff, int deviceAuthority_Inching, int deviceAuthority_Timer, int deviceAuthority_Lock) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceOnlyMac = deviceOnlyMac;
        this.devicePassword = devicePassword;
        this.share=share;
        this.deviceAuthority_Alarm = deviceAuthority_Alarm;
        this.deviceAuthority_Map = deviceAuthority_Map;
        this.deviceAuthority_LineSwitch = deviceAuthority_LineSwitch;
        this.deviceAuthority_Analog = deviceAuthority_Analog;
        this.deviceAuthority_Switch = deviceAuthority_Switch;
        this.deviceAuthority_Poweroff = deviceAuthority_Poweroff;
        this.deviceAuthority_Inching = deviceAuthority_Inching;
        this.deviceAuthority_Timer = deviceAuthority_Timer;
        this.deviceAuthority_Lock = deviceAuthority_Lock;
    }

    public Device(boolean isOpen, String name, String imei, String share) {
        this.isOpen = isOpen;
        this.name = name;
        this.imei = imei;
        this.share = share;
    }





    @Generated(hash = 1469582394)
    public Device() {
    }

    @Generated(hash = 1831104911)
    public Device(Long deviceId, String deviceName, String deviceOnlyMac, String devicePassword, int deviceSellerId, int deviceCreatorId, int deviceModel, int choice, String name, String imei, String share, int deviceAuthority_Alarm, int deviceAuthority_Map, int deviceAuthority_LineSwitch, int deviceAuthority_Analog, int deviceAuthority_Switch,
            int deviceAuthority_Poweroff, int deviceAuthority_Inching, int deviceAuthority_Timer, int deviceAuthority_Lock, int deviceAuthority_Linked, int mcuVersion, int deviceState, int prelines, int lastlines, int prelineswitch, int lastlineswitch, int prelinesjog, int lastlinesjog, boolean online, int plMemory, double lineJog, double line, double line2,
            double line3, double line4, double line5, double line6, double line7, double line8, double line9, double line10, double line11, double line12, double line13, double line14, double line15, double line16, double temp, double hum, double current, double votage, String re485, int voice) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceOnlyMac = deviceOnlyMac;
        this.devicePassword = devicePassword;
        this.deviceSellerId = deviceSellerId;
        this.deviceCreatorId = deviceCreatorId;
        this.deviceModel = deviceModel;
        this.choice = choice;
        this.name = name;
        this.imei = imei;
        this.share = share;
        this.deviceAuthority_Alarm = deviceAuthority_Alarm;
        this.deviceAuthority_Map = deviceAuthority_Map;
        this.deviceAuthority_LineSwitch = deviceAuthority_LineSwitch;
        this.deviceAuthority_Analog = deviceAuthority_Analog;
        this.deviceAuthority_Switch = deviceAuthority_Switch;
        this.deviceAuthority_Poweroff = deviceAuthority_Poweroff;
        this.deviceAuthority_Inching = deviceAuthority_Inching;
        this.deviceAuthority_Timer = deviceAuthority_Timer;
        this.deviceAuthority_Lock = deviceAuthority_Lock;
        this.deviceAuthority_Linked = deviceAuthority_Linked;
        this.mcuVersion = mcuVersion;
        this.deviceState = deviceState;
        this.prelines = prelines;
        this.lastlines = lastlines;
        this.prelineswitch = prelineswitch;
        this.lastlineswitch = lastlineswitch;
        this.prelinesjog = prelinesjog;
        this.lastlinesjog = lastlinesjog;
        this.online = online;
        this.plMemory = plMemory;
        this.lineJog = lineJog;
        this.line = line;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        this.line5 = line5;
        this.line6 = line6;
        this.line7 = line7;
        this.line8 = line8;
        this.line9 = line9;
        this.line10 = line10;
        this.line11 = line11;
        this.line12 = line12;
        this.line13 = line13;
        this.line14 = line14;
        this.line15 = line15;
        this.line16 = line16;
        this.temp = temp;
        this.hum = hum;
        this.current = current;
        this.votage = votage;
        this.re485 = re485;
        this.voice = voice;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public int getDeviceCreatorId() {
        return this.deviceCreatorId;
    }

    public void setDeviceCreatorId(int deviceCreatorId) {
        this.deviceCreatorId = deviceCreatorId;
    }

    public int getDeviceSellerId() {
        return this.deviceSellerId;
    }

    public void setDeviceSellerId(int deviceSellerId) {
        this.deviceSellerId = deviceSellerId;
    }

    public String getDevicePassword() {
        return this.devicePassword;
    }

    public void setDevicePassword(String devicePassword) {
        this.devicePassword = devicePassword;
    }

    public String getDeviceOnlyMac() {
        return this.deviceOnlyMac;
    }

    public void setDeviceOnlyMac(String deviceOnlyMac) {
        this.deviceOnlyMac = deviceOnlyMac;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceModel() {
        return this.deviceModel;
    }

    public void setDeviceModel(int deviceModel) {
        this.deviceModel = deviceModel;
    }

    public boolean getOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getDeviceState() {
        return this.deviceState;
    }

    public void setDeviceState(int deviceState) {
        this.deviceState = deviceState;
    }

    public int getMcuVersion() {
        return this.mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public int getPlMemory() {
        return this.plMemory;
    }

    public void setPlMemory(int plMemory) {
        this.plMemory = plMemory;
    }

    public int getChoice() {
        return this.choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public int getLastlines() {
        return this.lastlines;
    }

    public void setLastlines(int lastlines) {
        this.lastlines = lastlines;
    }

    public int getPrelines() {
        return this.prelines;
    }

    public void setPrelines(int prelines) {
        this.prelines = prelines;
    }

    public int getLastlineswitch() {
        return this.lastlineswitch;
    }

    public void setLastlineswitch(int lastlineswitch) {
        this.lastlineswitch = lastlineswitch;
    }

    public int getPrelineswitch() {
        return this.prelineswitch;
    }

    public void setPrelineswitch(int prelineswitch) {
        this.prelineswitch = prelineswitch;
    }

    
    public int getPrelinesjog() {
        return this.prelinesjog;
    }

    public void setPrelinesjog(int prelinesjog) {
        this.prelinesjog = prelinesjog;
    }

    public int getLastlinesjog() {
        return this.lastlinesjog;
    }

    public void setLastlinesjog(int lastlinesjog) {
        this.lastlinesjog = lastlinesjog;
    }

    public void setLineJog(int lineJog) {
        this.lineJog = lineJog;
    }

    public double getLine15() {
        return this.line15;
    }

    public void setLine15(double line15) {
        this.line15 = line15;
    }

    public double getLine14() {
        return this.line14;
    }

    public void setLine14(double line14) {
        this.line14 = line14;
    }

    public double getLine13() {
        return this.line13;
    }

    public void setLine13(double line13) {
        this.line13 = line13;
    }

    public double getLine12() {
        return this.line12;
    }

    public void setLine12(double line12) {
        this.line12 = line12;
    }

    public double getLine11() {
        return this.line11;
    }

    public void setLine11(double line11) {
        this.line11 = line11;
    }

    public double getLine10() {
        return this.line10;
    }

    public void setLine10(double line10) {
        this.line10 = line10;
    }

    public double getLine9() {
        return this.line9;
    }

    public void setLine9(double line9) {
        this.line9 = line9;
    }

    public double getLine8() {
        return this.line8;
    }

    public void setLine8(double line8) {
        this.line8 = line8;
    }

    public double getLine7() {
        return this.line7;
    }

    public void setLine7(double line7) {
        this.line7 = line7;
    }

    public double getLine6() {
        return this.line6;
    }

    public void setLine6(double line6) {
        this.line6 = line6;
    }

    public double getLine5() {
        return this.line5;
    }

    public void setLine5(double line5) {
        this.line5 = line5;
    }

    public double getLine4() {
        return this.line4;
    }

    public void setLine4(double line4) {
        this.line4 = line4;
    }

    public double getLine3() {
        return this.line3;
    }

    public void setLine3(double line3) {
        this.line3 = line3;
    }

    public double getLine2() {
        return this.line2;
    }

    public void setLine2(double line2) {
        this.line2 = line2;
    }

    public double getLine() {
        return this.line;
    }

    public void setLine(double line) {
        this.line = line;
    }

    public double getLineJog() {
        return this.lineJog;
    }

    public void setLineJog(double lineJog) {
        this.lineJog = lineJog;
    }

    public double getLine16() {
        return this.line16;
    }

    public void setLine16(double line16) {
        this.line16 = line16;
    }

    public double getVotage() {
        return this.votage;
    }

    public void setVotage(double votage) {
        this.votage = votage;
    }

    public double getCurrent() {
        return this.current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getHum() {
        return this.hum;
    }

    public void setHum(double hum) {
        this.hum = hum;
    }

    public double getTemp() {
        return this.temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getDeviceAuthority_Lock() {
        return this.deviceAuthority_Lock;
    }

    public void setDeviceAuthority_Lock(int deviceAuthority_Lock) {
        this.deviceAuthority_Lock = deviceAuthority_Lock;
    }

    public int getDeviceAuthority_Timer() {
        return this.deviceAuthority_Timer;
    }

    public void setDeviceAuthority_Timer(int deviceAuthority_Timer) {
        this.deviceAuthority_Timer = deviceAuthority_Timer;
    }

    public int getDeviceAuthority_Inching() {
        return this.deviceAuthority_Inching;
    }

    public void setDeviceAuthority_Inching(int deviceAuthority_Inching) {
        this.deviceAuthority_Inching = deviceAuthority_Inching;
    }

    public int getDeviceAuthority_Poweroff() {
        return this.deviceAuthority_Poweroff;
    }

    public void setDeviceAuthority_Poweroff(int deviceAuthority_Poweroff) {
        this.deviceAuthority_Poweroff = deviceAuthority_Poweroff;
    }

    public int getDeviceAuthority_Switch() {
        return this.deviceAuthority_Switch;
    }

    public void setDeviceAuthority_Switch(int deviceAuthority_Switch) {
        this.deviceAuthority_Switch = deviceAuthority_Switch;
    }

    public int getDeviceAuthority_Analog() {
        return this.deviceAuthority_Analog;
    }

    public void setDeviceAuthority_Analog(int deviceAuthority_Analog) {
        this.deviceAuthority_Analog = deviceAuthority_Analog;
    }

    public int getDeviceAuthority_LineSwitch() {
        return this.deviceAuthority_LineSwitch;
    }

    public void setDeviceAuthority_LineSwitch(int deviceAuthority_LineSwitch) {
        this.deviceAuthority_LineSwitch = deviceAuthority_LineSwitch;
    }

    public int getDeviceAuthority_Map() {
        return this.deviceAuthority_Map;
    }

    public void setDeviceAuthority_Map(int deviceAuthority_Map) {
        this.deviceAuthority_Map = deviceAuthority_Map;
    }

    public int getDeviceAuthority_Alarm() {
        return this.deviceAuthority_Alarm;
    }

    public void setDeviceAuthority_Alarm(int deviceAuthority_Alarm) {
        this.deviceAuthority_Alarm = deviceAuthority_Alarm;
    }

    public int getDeviceAuthority_Linked() {
        return this.deviceAuthority_Linked;
    }

    public void setDeviceAuthority_Linked(int deviceAuthority_Linked) {
        this.deviceAuthority_Linked = deviceAuthority_Linked;
    }

    public String getRe485() {
        return this.re485;
    }

    public void setRe485(String re485) {
        this.re485 = re485;
    }

    public int getVoice() {
        return this.voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }


}
