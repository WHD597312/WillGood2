package com.peihou.willgood2.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Line2 {
    @Id(autoincrement = true)
    private Long id;
    @Transient
    private boolean onClick;
    private int click=0;
    @Transient
    private int click2;
    private String deviceMac;
    private String name;
    private double seconds;
    private boolean open;//线路开关状态
    private int deviceLineNum;
    private long deviceId;
    private boolean online;//线路在线状态
    private boolean jog;//线路点动状态
    private long timerId;//定时Id
    private int timerChecked;//线路有定时任务
    private int lock;//线路的互锁1 1为两个线路互锁 0为不互锁
    private String interLock;//两个线路的所有 格式为deviceLineNum1&deviceLineNum2
    private int visitity;//显示数据 0不显示，1，显示,


    public Line2(boolean onClick, String name, int seconds, boolean open, int deviceLineNum, long deviceId,String deviceMac) {
        this.onClick = onClick;
        this.name = name;
        this.seconds = seconds;
        this.open = open;
        this.deviceLineNum = deviceLineNum;
        this.deviceId = deviceId;
        this.deviceMac=deviceMac;
    }

    public Line2(boolean onClick, String name, int seconds, boolean open) {
        this.onClick = onClick;
        this.name = name;
        this.seconds = seconds;
        this.open = open;
    }


    @Generated(hash = 1309823932)
    public Line2() {
    }

    public int getClick2() {
        return click2;
    }

    public void setClick2(int click2) {
        this.click2 = click2;
    }

    @Generated(hash = 1074787947)
    public Line2(Long id, int click, String deviceMac, String name, double seconds, boolean open, int deviceLineNum, long deviceId,
            boolean online, boolean jog, long timerId, int timerChecked, int lock, String interLock, int visitity) {
        this.id = id;
        this.click = click;
        this.deviceMac = deviceMac;
        this.name = name;
        this.seconds = seconds;
        this.open = open;
        this.deviceLineNum = deviceLineNum;
        this.deviceId = deviceId;
        this.online = online;
        this.jog = jog;
        this.timerId = timerId;
        this.timerChecked = timerChecked;
        this.lock = lock;
        this.interLock = interLock;
        this.visitity = visitity;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOnClick() {
        return onClick;
    }

    public void setOnClick(boolean onClick) {
        this.onClick = onClick;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public long getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceLineNum() {
            return this.deviceLineNum;
    }

    public void setDeviceLineNum(int deviceLineNum) {
        this.deviceLineNum = deviceLineNum;
    }

    public boolean getOpen() {
        return this.open;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getJog() {
        return this.jog;
    }

    public void setJog(boolean jog) {
        this.jog = jog;
    }

    public boolean getOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getTimerChecked() {
        return this.timerChecked;
    }

    public void setTimerChecked(int timerChecked) {
        this.timerChecked = timerChecked;
    }

    public long getTimerId() {
        return this.timerId;
    }

    public void setTimerId(long timerId) {
        this.timerId = timerId;
    }

    public double getSeconds() {
        return this.seconds;
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public String getDeviceMac() {
        return this.deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getLock() {
        return this.lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    public String getInterLock() {
        return this.interLock;
    }

    public void setInterLock(String interLock) {
        this.interLock = interLock;
    }

    public int getClick() {
        return this.click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public int getVisitity() {
        return this.visitity;
    }

    public void setVisitity(int visitity) {
        this.visitity = visitity;
    }

}
