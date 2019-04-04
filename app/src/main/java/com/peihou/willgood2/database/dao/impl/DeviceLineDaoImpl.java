package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.Line2Dao;
import com.peihou.willgood2.pojo.Line2;
import com.peihou.willgood2.utils.TenTwoUtil;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceLineDaoImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private Line2Dao lineDao;
    private DaoSession session;
    public DeviceLineDaoImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        lineDao=session.getLine2Dao();
    }
    public void insert(Line2 line2){
        lineDao.insert(line2);
    }
    public void insertDeviceLines(List<Line2> list){
        lineDao.insertInTx(list);
    }
    public void deleteDeviceLines(List<Line2> list){
        lineDao.deleteInTx(list);
    }
    public void deleteDeviceLines(String deviceMac){
        List<Line2> list=findDeviceLines(deviceMac);
        if (list!=null &&!list.isEmpty()){
            lineDao.deleteInTx(list);
        }
    }

    /**
     * 查询出互锁的线路
     * @param deviceId
     * @param deviceLineNum
     * @param lock
     * @return
     */
    public Line2 findInterLockLine(long deviceId,int deviceLineNum,int lock){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceId.eq(deviceId),Line2Dao.Properties.DeviceLineNum.eq(deviceLineNum),Line2Dao.Properties.Lock.eq(lock));
        return lineDao.queryBuilder().where(whereCondition).unique();
    }

    Map<String,String> map=new HashMap<>();

    /**
     * 查询设备的互锁线路
     * @param deviceId
     * @return
     */
    public Map<String,String> findInterLockLine(long deviceId){
        map.clear();
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceId.eq(deviceId),Line2Dao.Properties.InterLock.isNotNull());
        List<Line2> list=lineDao.queryBuilder().where(whereCondition).list();
        for (int i = 0; i <list.size() ; i++) {
            Line2 line2=list.get(i);
            String interLock=line2.getInterLock();
            map.put(interLock,interLock);
        }
        return map;
    }
    public void update(List<Line2> list){
        lineDao.updateInTx(list);
    }
    public Map<String,String> findInterLockLine(String deviceMac){
        Map map=new HashMap();
        WhereCondition whereCondition=lineDao.queryBuilder().and(
                Line2Dao.Properties.DeviceMac.eq(deviceMac),
                Line2Dao.Properties.Lock.eq(1)
        );
        List<Line2> list=lineDao.queryBuilder().where(whereCondition).list();
        for (int i = 0; i <list.size() ; i++) {
            Line2 line2=list.get(i);
            String interLock=line2.getInterLock();
            map.put(interLock,interLock);
        }
        return map;
    }

    /**
     * 查询不是互锁的路线
     * @param deviceMac
     * @return
     */
    public List<Line2> findInterLockLines(String deviceMac){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceMac.eq(deviceMac),Line2Dao.Properties.InterLock.isNull(),Line2Dao.Properties.Lock.eq(0));
        return lineDao.queryBuilder().where(whereCondition).list();
    }

    public void update(Line2 line){
        lineDao.update(line);
    }
    public void updates(List<Line2> list){
        lineDao.updateInTx(list);
    }

    /**
     * 根据设备id查询线路
     * @param deviceId
     * @return
     */
    public List<Line2> findDeviceLines(long deviceId){
        return lineDao.queryBuilder().where(Line2Dao.Properties.DeviceId.eq(deviceId)).orderAsc(Line2Dao.Properties.DeviceLineNum).list();
    }
    public List<Line2> findDeviceOnlineLines(long deviceId){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceId.eq(deviceId),Line2Dao.Properties.Online.eq(true));
        return lineDao.queryBuilder().where(whereCondition).orderAsc(Line2Dao.Properties.DeviceLineNum).list();
    }

    public List<Line2> findDeviceOnlineLines(String deviceMac){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceMac.eq(deviceMac),Line2Dao.Properties.Online.eq(true));
        return lineDao.queryBuilder().where(whereCondition).orderAsc(Line2Dao.Properties.DeviceLineNum).list();
    }

    public List<Line2> findDeviceLines(String deviceMac){
        return lineDao.queryBuilder().where(Line2Dao.Properties.DeviceMac.eq(deviceMac)).orderAsc(Line2Dao.Properties.DeviceLineNum).list();
    }
    
    /**
     * 根据设备的devcieMac 与线路的deviceLineNum可查询出一个唯一的线路
     * @param deviceMac
     * @param deviceLineNum
     * @return
     */
    public Line2 findDeviceLine(String deviceMac,int deviceLineNum){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceMac.eq(deviceMac),Line2Dao.Properties.DeviceLineNum.eq(deviceLineNum));
        return lineDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 根据设备的Id，与线路的deviceLineNum可查询出一个唯一的线路
     * @param deviceId
     * @param deviceLineNum
     * @return
     */
    public Line2 findDeviceLine(long deviceId,int deviceLineNum){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceId.eq(deviceId),Line2Dao.Properties.DeviceLineNum.eq(deviceLineNum));
        return lineDao.queryBuilder().where(whereCondition).unique();
    }


    /**
     * 根据设备的Id,查询定时任务的线路
     * @param deviceId
     * @param
     * @return
     */
    public List<Line2> findDeviceTimeTasks(long deviceId,long timerId){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceId.eq(deviceId),Line2Dao.Properties.TimerId.eq(timerId),Line2Dao.Properties.TimerChecked.eq(1));
        return lineDao.queryBuilder().where(whereCondition).list();
    }
    public List<Line2> findDeviceTimeTasks(String deviceMac,long timerId){
        WhereCondition whereCondition=lineDao.queryBuilder().and(Line2Dao.Properties.DeviceMac.eq(deviceMac),Line2Dao.Properties.TimerId.eq(timerId),Line2Dao.Properties.TimerChecked.eq(1));
        return lineDao.queryBuilder().where(whereCondition).list();
    }

    public void deleteAll(){
        lineDao.deleteAll();
    }
}
