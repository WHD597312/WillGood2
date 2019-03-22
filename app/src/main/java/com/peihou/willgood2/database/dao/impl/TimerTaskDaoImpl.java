package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.TimerTaskDao;
import com.peihou.willgood2.pojo.TimerTask;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class TimerTaskDaoImpl {

    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private TimerTaskDao  taskDao;
    private DaoSession session;
    public TimerTaskDaoImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        taskDao=session.getTimerTaskDao();
    }
    public void insert(TimerTask timerTask){
        taskDao.insert(timerTask);
    }
    public void update(TimerTask timerTask){
        taskDao.update(timerTask);
    }
    public void delete(TimerTask timerTask){
        taskDao.delete(timerTask);
    }
    public List<TimerTask> findDeviceTimerTask(long deviceId){
        return taskDao.queryBuilder().where(TimerTaskDao.Properties.DeviceId.eq(deviceId)).list();
    }
    public List<TimerTask> findDeviceTimeTask(String deviceMac){
        return taskDao.queryBuilder().where(TimerTaskDao.Properties.DeviceMac.eq(deviceMac)).list();
    }

    /**
     * 根据设备的
     * @param deviceMac deviceMac，定时的hour与min,确定一个循环定时任务
     * @param hour
     * @param min
     * @return
     */
    public TimerTask findUniqueTimerTask(String deviceMac,int hour,int min,int week,int prelines,int lastlines){
        WhereCondition whereCondition=taskDao.queryBuilder().and(TimerTaskDao.Properties.DeviceMac.eq(deviceMac),
                TimerTaskDao.Properties.Choice.eq(0x22),TimerTaskDao.Properties.Hour.eq(hour),TimerTaskDao.Properties.Min.eq(min),
                TimerTaskDao.Properties.Week.eq(week),
                TimerTaskDao.Properties.Prelines.eq(prelines),TimerTaskDao.Properties.Lastlines.eq(lastlines));
        return taskDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 确定一个单次定时任务
     * @param deviceMac
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @return
     */
    public TimerTask findUniqueTimerTask(String deviceMac,int year,int month,int day,int hour,int min,int prelines,int lastlines){
        WhereCondition whereCondition=taskDao.queryBuilder().and(TimerTaskDao.Properties.DeviceMac.eq(deviceMac),TimerTaskDao.Properties.Choice.eq(0x11),
                TimerTaskDao.Properties.Year.eq(year),
                TimerTaskDao.Properties.Month.eq(month),
                TimerTaskDao.Properties.Day.eq(day),TimerTaskDao.Properties.Hour.eq(hour),TimerTaskDao.Properties.Min.eq(min),
                TimerTaskDao.Properties.Prelines.eq(prelines),TimerTaskDao.Properties.Lastlines.eq(lastlines));
        return taskDao.queryBuilder().where(whereCondition).unique();
    }
    public void deleteAll(){
        taskDao.deleteAll();
    }
}
