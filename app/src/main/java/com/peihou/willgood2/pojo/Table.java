package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class Table implements Serializable {
    static final long serialVersionUID = 42L;


    @Id(autoincrement = true)
    private Long id;
    private long deviceId;
    private String deviceMac;
    private int i;
    private String name;
    private double data;
    private double factor;
    private double result;
    private String unit;


    public Table(int i,String name, double data, float factor, float result, String unit,String deviceMac,long deviceId) {
        this.i=i;
        this.name = name;
        this.data = data;
        this.factor = factor;
        this.result = result;
        this.unit = unit;
        this.deviceMac=deviceMac;
        this.deviceId=deviceId;
    }

    @Generated(hash = 752389689)
    public Table() {
    }

    @Generated(hash = 1784495848)
    public Table(Long id, long deviceId, String deviceMac, int i, String name, double data, double factor, double result,
            String unit) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceMac = deviceMac;
        this.i = i;
        this.name = name;
        this.data = data;
        this.factor = factor;
        this.result = result;
        this.unit = unit;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public double getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
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
