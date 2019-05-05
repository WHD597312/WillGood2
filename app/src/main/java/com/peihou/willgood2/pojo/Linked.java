package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

@Entity
public class Linked implements Serializable {



    @Id(autoincrement = true)
    private Long id;
    private String deviceMac;//设备的mac地址
    private int mcuVersion;//mcu版本
    private int type;//联动的类型
    private String lines;//联动的线路
    private String name;//联动的名称
    private double condition;//联动触发条件值
    private int triState;//触发条件状态 0低于，1高于
    private int conditionState;//控制条件状态，0关闭 1打开
    private int preLines;//联动控制前8路线路
    private int lastLines;//联动控制后8路线路
    private int triType;//触发类型 0为单次触发，1为循环触发
    private int state;//联动的状态 0关闭，1打开，2删除
    private int analog;//模拟量 0,1,2,3,4,5,6,7 0-3为电流1-4,   4-7为电压1-4
    private int switchLine;//开关量线路
    private int visitity;//数据是否显示 0不显示1，不显示

    /**
     *
     * @param deviceMac
     * @param type
     * @param name
     * @param condition
     * @param triState
     * @param conditionState
     * @param state
     * @param preLines
     * @param lastLines
     * @param triType
     */

    public Linked(String deviceMac, int type, String name, double condition, int triState,int conditionState,int state, int preLines, int lastLines, int triType) {
        this.deviceMac = deviceMac;
        this.type = type;
        this.name = name;
        this.condition = condition;
        this.triState=triState;
        this.conditionState=conditionState;
        this.state = state;
        this.preLines = preLines;
        this.lastLines = lastLines;
        this.triType = triType;
    }

    /**
     *
     * @param deviceMac
     * @param type
     * @param name
     * @param condition
     * @param conditionState
     * @param state
     * @param preLines
     * @param lastLines
     * @param triType
     */
    public Linked(String deviceMac, int type, String name, double condition,int conditionState,int state, int preLines, int lastLines, int triType) {
        this.deviceMac = deviceMac;
        this.type = type;
        this.name = name;
        this.condition = condition;
        this.conditionState=conditionState;
        this.state = state;
        this.preLines = preLines;
        this.lastLines = lastLines;
        this.triType = triType;
    }




    @Generated(hash = 871094865)
    public Linked() {
    }

    @Generated(hash = 2054883553)
    public Linked(Long id, String deviceMac, int mcuVersion, int type, String lines, String name, double condition, int triState, int conditionState, int preLines,
            int lastLines, int triType, int state, int analog, int switchLine, int visitity) {
        this.id = id;
        this.deviceMac = deviceMac;
        this.mcuVersion = mcuVersion;
        this.type = type;
        this.lines = lines;
        this.name = name;
        this.condition = condition;
        this.triState = triState;
        this.conditionState = conditionState;
        this.preLines = preLines;
        this.lastLines = lastLines;
        this.triType = triType;
        this.state = state;
        this.analog = analog;
        this.switchLine = switchLine;
        this.visitity = visitity;
    }





   
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Linked)){
            return false;
        }
        Linked linked= (Linked) obj;
        if (this.type==linked.type){
            return true;
        }else {
            return false;
        }
    }

    public int getTriType() {
        return this.triType;
    }

    public void setTriType(int triType) {
        this.triType = triType;
    }

    public int getLastLines() {
        return this.lastLines;
    }

    public void setLastLines(int lastLines) {
        this.lastLines = lastLines;
    }

    public int getPreLines() {
        return this.preLines;
    }

    public void setPreLines(int preLines) {
        this.preLines = preLines;
    }

    public int getConditionState() {
        return this.conditionState;
    }

    public void setConditionState(int conditionState) {
            this.conditionState = conditionState;
    }

    public double getCondition() {
        return this.condition;
    }

    public void setCondition(double condition) {
        this.condition = condition;
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

    public int getAnalog() {
        return this.analog;
    }

    public void setAnalog(int analog) {
        this.analog = analog;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }





    public int getTriState() {
        return this.triState;
    }





    public void setTriState(int triState) {
        this.triState = triState;
    }

    public int getSwitchLine() {
        return this.switchLine;
    }

    public void setSwitchLine(int switchLine) {
        this.switchLine = switchLine;
    }

    public int getVisitity() {
        return this.visitity;
    }

    public void setVisitity(int visitity) {
        this.visitity = visitity;
    }


}
