package com.peihou.willgood2.pojo;

public class InterLock{

    private Long id;
    private String name;
    private String name2;

    private int deviceLineNum;
    private int deviceLineNum2;
    private int operate;

    public InterLock() {
    }

    public InterLock(String name, String name2, int deviceLineNum,int deviceLineNum2,int operate) {
        this.name = name;
        this.name2 = name2;
        this.deviceLineNum=deviceLineNum;
        this.deviceLineNum2=deviceLineNum2;
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
}
