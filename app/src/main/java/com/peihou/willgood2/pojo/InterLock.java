package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class InterLock{

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String name2;

    private String deviceMac;
    private int i;//设备互锁的个数
    private int deviceLineNum;
    private int deviceLineNum2;
    private int visitity;//可见的互锁线路
    private int operate;

    public InterLock(String deviceMac, int i) {
        this.deviceMac = deviceMac;
        this.i = i;
    }

    public InterLock(String name, String name2, String deviceMac, int deviceLineNum, int deviceLineNum2) {
        this.name = name;
        this.name2 = name2;
        this.deviceMac = deviceMac;
        this.deviceLineNum = deviceLineNum;
        this.deviceLineNum2 = deviceLineNum2;
    }

    public InterLock() {
    }

    public InterLock(String name, String name2, int deviceLineNum,int deviceLineNum2,int operate) {
        this.name = name;
        this.name2 = name2;
        this.deviceLineNum=deviceLineNum;
        this.deviceLineNum2=deviceLineNum2;
        this.operate = operate;
    }

    @Generated(hash = 458043605)
    public InterLock(Long id, String name, String name2, String deviceMac, int i, int deviceLineNum,
            int deviceLineNum2, int visitity, int operate) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
        this.deviceMac = deviceMac;
        this.i = i;
        this.deviceLineNum = deviceLineNum;
        this.deviceLineNum2 = deviceLineNum2;
        this.visitity = visitity;
        this.operate = operate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }


    public int getOperate() {
        return operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getDeviceLineNum() {
        return deviceLineNum;
    }

    public void setDeviceLineNum(int deviceLineNum) {
        this.deviceLineNum = deviceLineNum;
    }

    public int getDeviceLineNum2() {
        return deviceLineNum2;
    }

    public void setDeviceLineNum2(int deviceLineNum2) {
        this.deviceLineNum2 = deviceLineNum2;
    }

    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getVisitity() {
        return this.visitity;
    }

    public void setVisitity(int visitity) {
        this.visitity = visitity;
    }
}
