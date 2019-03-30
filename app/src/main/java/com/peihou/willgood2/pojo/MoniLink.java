package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;


import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class MoniLink implements Serializable {



    @Id(autoincrement = true)
    private Long id;
    private String name;//模拟量联动名称
    private int type;//o为电流，1为电压
    private int num;//1，2，3，4
    private int contition;//触发条件
    private int triState;//触发条件状态 0低于，1高于
    private int preLine;//前8路
    private int lastLine;//后8路
    private int controlState;//控制状态
    private int triType;//触发类型 为单次触发，0为循环触发
    private int state;
    private int controlType;//联动控制
    private String deviceMac;
    private int mcuVersion;
    private String lines;
    private int visitity;//显示数据 0不显示，1，显示
    @Transient
    private boolean open;

    public MoniLink() {
    }

    public MoniLink(int type, int num, int contition, int triState, int preLine, int lastLine, int controlState, int triType, int state, String deviceMac, int mcuVersion) {
        this.type = type;
        this.num = num;
        this.contition = contition;
        this.triState = triState;
        this.preLine = preLine;
        this.lastLine = lastLine;
        this.controlState = controlState;
        this.triType = triType;
        this.state = state;
        this.deviceMac = deviceMac;
        this.mcuVersion = mcuVersion;
    }

    public MoniLink(String name, int type, int num, int contition, int triState, int preLine, int lastLine, int controlState, int triType, int state, int controlType, String deviceMac, int mcuVersion) {
        this.name = name;
        this.type = type;
        this.num = num;
        this.contition = contition;
        this.triState = triState;
        this.preLine = preLine;
        this.lastLine = lastLine;
        this.controlState = controlState;
        this.triType = triType;
        this.state = state;
        this.controlType = controlType;
        this.deviceMac = deviceMac;
        this.mcuVersion = mcuVersion;
    }

    public MoniLink(String name, int type, boolean open) {
        this.name = name;
        this.type = type;
        this.open = open;
    }

    public MoniLink(String name, int type, int num, String deviceMac) {
        this.name = name;
        this.type = type;
        this.num = num;
        this.deviceMac = deviceMac;
    }

    @Generated(hash = 1226823812)
    public MoniLink(Long id, String name, int type, int num, int contition, int triState, int preLine, int lastLine, int controlState, int triType, int state, int controlType, String deviceMac,
            int mcuVersion, String lines, int visitity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.num = num;
        this.contition = contition;
        this.triState = triState;
        this.preLine = preLine;
        this.lastLine = lastLine;
        this.controlState = controlState;
        this.triType = triType;
        this.state = state;
        this.controlType = controlType;
        this.deviceMac = deviceMac;
        this.mcuVersion = mcuVersion;
        this.lines = lines;
        this.visitity = visitity;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getMcuVersion() {
        return this.mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public boolean getOpen() {
        return this.open;
    }

    public int getControlType() {
        return this.controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTriType() {
        return this.triType;
    }

    public void setTriType(int triType) {
        this.triType = triType;
    }

    public int getControlState() {
        return this.controlState;
    }

    public void setControlState(int controlState) {
        this.controlState = controlState;
    }

    public int getTriState() {
        return this.triState;
    }

    public void setTriState(int triState) {
        this.triState = triState;
    }

    public int getContition() {
        return this.contition;
    }

    public void setContition(int contition) {
        this.contition = contition;
    }

    public int getLastLine() {
        return this.lastLine;
    }

    public void setLastLine(int lastLine) {
        this.lastLine = lastLine;
    }

    public int getPreLine() {
        return this.preLine;
    }

    public void setPreLine(int preLine) {
        this.preLine = preLine;
    }

    public String getLines() {
        return this.lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public int getVisitity() {
        return this.visitity;
    }

    public void setVisitity(int visitity) {
        this.visitity = visitity;
    }
}
