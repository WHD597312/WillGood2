package com.peihou.willgood2.pojo;

import java.io.Serializable;

public class SwtichState implements Serializable {
    private int type;
    private String name;
    private String pic;//图片地址
    private int state;//状态，0为异常，1为正常，2为无效

    public SwtichState(int type, String name, String pic,int state) {
        this.type = type;
        this.name = name;
        this.pic=pic;
        this.state = state;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
