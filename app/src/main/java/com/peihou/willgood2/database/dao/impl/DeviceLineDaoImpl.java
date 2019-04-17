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
    private Line2Dao lineDao;
    private Context context;
    public DeviceLineDaoImpl(Context context) {
        this.context=context;
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
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

    public void update(List<Line2> list){
        lineDao.updateInTx(list);
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
//        Line2Dao lineDao=DBManager.getInstance(context).getDaoSession2().getLine2Dao();
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

    public void deleteAll(){
        lineDao.deleteAll();
    }
}
