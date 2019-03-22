package com.peihou.willgood2.pojo;

public class Lock {
    private String name1;
    private String name2;
    private String inter;

    public Lock(String name1, String name2,String inter) {
        this.name1 = name1;
        this.name2 = name2;
        this.inter=inter;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getInter() {
        return inter;
    }

    public void setInter(String inter) {
        this.inter = inter;
    }
}
