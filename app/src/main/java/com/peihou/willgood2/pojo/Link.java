package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Link {
    @Id(autoincrement = true)
    private Long id;
    private long deviceId;
    private int type;//联动的类型
    private boolean check;
    private int mcuVersion;//mcu版本
    private int control;//转化为二进制数据，bit7表示温度，bit6表示湿度，bit5表示开关，bit4表示电流，bit3表示电压，bit2表示表示模拟量，动bit1:忽略(注:1为开启0为关闭 如果bit1为1则5必须有值)
    private int setting;//联动设置
    private int condition;
    private int preline;//bit7:1路设备 0表示未选中，1表示选中;bit6:2路设备 0表示未选中，1表示选中bit5:3路设备 0表示未选中，1表示选中。。。
    private int lastline;//bit7:9路设备 0表示未选中，1表示选中;bit6:10路设备 0表示未选中，1表示选中bit5:11路设备 0表示未选中，1表示选中。。。
    private int triType;//bit7-6: 11高于 10低于 00 忽略 bit5-4: 11开启 10关闭 00忽略 bit3: 1单次触发 0循环触发（注：若5有值则bit7-6有值 bit5-4为00；否则bit7-6为00，bit5-4有值

    public Link(int type, boolean check, String name) {
        this.type = type;
        this.check = check;
        this.name = name;
    }

    @Generated(hash = 482895079)
    public Link(Long id, long deviceId, int type, boolean check, int mcuVersion, int control, int setting, int condition, int preline,
            int lastline, int triType, String name) {
        this.id = id;
        this.deviceId = deviceId;
        this.type = type;
        this.check = check;
        this.mcuVersion = mcuVersion;
        this.control = control;
        this.setting = setting;
        this.condition = condition;
        this.preline = preline;
        this.lastline = lastline;
        this.triType = triType;
        this.name = name;
    }

    @Generated(hash = 225969300)
    public Link() {
    }

    private String name;




    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
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

    public int getTriType() {
        return this.triType;
    }

    public void setTriType(int triType) {
        this.triType = triType;
    }

    public int getLastline() {
        return this.lastline;
    }

    public void setLastline(int lastline) {
        this.lastline = lastline;
    }

    public int getPreline() {
        return this.preline;
    }

    public void setPreline(int preline) {
        this.preline = preline;
    }

    public int getCondition() {
        return this.condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getSetting() {
        return this.setting;
    }

    public void setSetting(int setting) {
        this.setting = setting;
    }

    public int getControl() {
        return this.control;
    }

    public void setControl(int control) {
        this.control = control;
    }

    public int getMcuVersion() {
        return this.mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public boolean getCheck() {
        return this.check;
    }

    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
