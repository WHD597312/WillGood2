package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class TimerTask implements Serializable {

    static final long serialVersionUID = 42L;


    @Id(autoincrement = true)
    private Long id;
    private long deviceId;
    private boolean open;//开关状态
    private String deviceMac;
    private String name;
    private String timer;
    private int mcuVersion;//mcu版本号
    private int choice;//定时选择 0为单次定时，1为循环定时
    private int year;//年
    private int month;//定时月
    private int day;//定时日期
    private int week;//定时星期
    private int hour;//定时时
    private int min;//定时分
    private int prelines;//定时前8路线路
    private int lastlines;//定时后8路线路
    private int controlState;//定时控制状态
    private int state;//定时状态0为关闭 1打开，2删除
    private int timers;
    private long seconds;
    private int visitity;//数据是否显示 0不显示1，不显示


    public TimerTask(String deviceMac, int choice, int week,int hour, int min,int controlState,int prelines,int lastlines,int state) {
        this.deviceMac = deviceMac;
        this.choice = choice;
        this.week = week;
        this.hour = hour;
        this.min = min;
        this.controlState=controlState;
        this.prelines=prelines;
        this.lastlines=lastlines;
        this.state=state;
    }

    public TimerTask(String deviceMac, int choice, int year, int month, int day, int hour, int min, int controlState,int prelines,int lastlines,int state) {
        this.deviceMac = deviceMac;
        this.choice = choice;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.controlState = controlState;
        this.prelines=prelines;
        this.lastlines=lastlines;
        this.state=state;
    }

    public TimerTask(boolean open, String name, String timer) {
        this.open = open;
        this.name = name;
        this.timer = timer;
    }

    public TimerTask(boolean open, String name, String timer, int timers) {
        this.open = open;
        this.name = name;
        this.timer = timer;
        this.timers = timers;
    }


    @Generated(hash = 589238981)
    public TimerTask() {
    }

    @Generated(hash = 1413399793)
    public TimerTask(Long id, long deviceId, boolean open, String deviceMac, String name, String timer, int mcuVersion, int choice, int year, int month, int day,
            int week, int hour, int min, int prelines, int lastlines, int controlState, int state, int timers, long seconds, int visitity) {
        this.id = id;
        this.deviceId = deviceId;
        this.open = open;
        this.deviceMac = deviceMac;
        this.name = name;
        this.timer = timer;
        this.mcuVersion = mcuVersion;
        this.choice = choice;
        this.year = year;
        this.month = month;
        this.day = day;
        this.week = week;
        this.hour = hour;
        this.min = min;
        this.prelines = prelines;
        this.lastlines = lastlines;
        this.controlState = controlState;
        this.state = state;
        this.timers = timers;
        this.seconds = seconds;
        this.visitity = visitity;
    }

    public int getTimers() {
        return timers;
    }

    public void setTimers(int timers) {
        this.timers = timers;
    }

    public int getMcuVersion() {
        return mcuVersion;
    }

    public void setMcuVersion(int mcuVersion) {
        this.mcuVersion = mcuVersion;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }


    public int getControlState() {
        return controlState;
    }

    public void setControlState(int controlState) {
        this.controlState = controlState;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public boolean getOpen() {
        return this.open;
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

    public int getLastlines() {
        return this.lastlines;
    }

    public void setLastlines(int lastlines) {
        this.lastlines = lastlines;
    }

    public int getPrelines() {
        return this.prelines;
    }

    public void setPrelines(int prelines) {
        this.prelines = prelines;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return this.month;
    }

    public void setMonth(int month) {
        this.month = month;
    }



    public int getChoice() {
        return this.choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public int getVisitity() {
        return this.visitity;
    }

    public void setVisitity(int visitity) {
        this.visitity = visitity;
    }
}
