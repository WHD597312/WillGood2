package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class LinkedType implements Serializable {
    static final long serialVersionUID = 42L;


    @Id(autoincrement = true)
    private Long id;
    private String macAddress;
    private int type;
    private String name;
    private int mcuVersion;
    private int state;//联动开关状态

    public LinkedType(String macAddress, int type, String name, int mcuVersion, int state) {
        this.macAddress = macAddress;
        this.type = type;
        this.name = name;
        this.mcuVersion = mcuVersion;
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMcuVersion() {
        return this.mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1274164140)
    public LinkedType(Long id, String macAddress, int type, String name, int mcuVersion,
            int state) {
        this.id = id;
        this.macAddress = macAddress;
        this.type = type;
        this.name = name;
        this.mcuVersion = mcuVersion;
        this.state = state;
    }

    @Generated(hash = 1974331412)
    public LinkedType() {
    }
}
