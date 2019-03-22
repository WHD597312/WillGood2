package com.peihou.willgood2.pojo;

public class DeviceMenu {
    private int i;
    private String name;
    private int img;

    public DeviceMenu(int i, String name, int img) {
        this.i = i;
        this.name = name;
        this.img = img;
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

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
