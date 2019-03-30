package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.TableDao;
import com.peihou.willgood2.pojo.Table;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceAnalogDaoImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private TableDao tableDao;
    private DaoSession session;
    public DeviceAnalogDaoImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        tableDao=session.getTableDao();
    }

    /**
     * 添加设备模拟量
     * @param table
     */
    public void insert(Table table){
        tableDao.insert(table);
    }

    /**
     * 批量插入设备模拟量
     * @param tables
     */
    public void inserts(List<Table> tables){
        tableDao.insertInTx(tables);
    }

    /**
     * 更新设备模拟量
     * @param table
     */
    public void update(Table table){
        tableDao.update(table);
    }

    /**
     * 批量更新设备模拟量
     * @param tables
     */
    public void updates(List<Table> tables){
        tableDao.updateInTx(tables);
    }

    /**
     * 删除设备模拟量
     * @param table
     */
    public void delete(Table table){
        tableDao.delete(table);
    }

    /**
     * 批量删除设备模拟量
     * @param deviceId
     */
    public void deleteDeviceTables(long deviceId){
       List<Table> tables=tableDao.queryBuilder().where(TableDao.Properties.DeviceId.eq(deviceId)).list();
       tableDao.deleteInTx(tables);
    }

    /**
     * 批量删除设备模拟量
     * @param deviceMac
     */
    public void deleteDeviceTables(String  deviceMac){
        List<Table> tables=tableDao.queryBuilder().where(TableDao.Properties.DeviceMac.eq(deviceMac)).list();
        tableDao.deleteInTx(tables);
    }

    /**
     * 使用deviceMac查询设备唯一模拟量
     * @param deviceMac
     * @param i
     * @return
     */
    public Table findDeviceAnalog(String deviceMac,int i){
        WhereCondition whereCondition=tableDao.queryBuilder().and(TableDao.Properties.DeviceMac.eq(deviceMac),TableDao.Properties.I.eq(i));
        return tableDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 使用deviceId查询设备唯一模拟量
     * @param deviceId
     * @param i
     * @return
     */
    public Table findDeviceAnalog(long deviceId,int i){
        WhereCondition whereCondition=tableDao.queryBuilder().and(TableDao.Properties.DeviceId.eq(deviceId),TableDao.Properties.I.eq(i));
        return tableDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 查询设备模拟量
     * @param deviceMac
     * @return
     */
    public List<Table> findDeviceAnalogs(String deviceMac){
        return tableDao.queryBuilder().where(TableDao.Properties.DeviceMac.eq(deviceMac)).orderAsc(TableDao.Properties.I).list();
    }

    /**
     * 批量更新设备模拟量
     * @param lists
     */
    public void updateDeviceAnalogs(List<Table> lists){
        tableDao.updateInTx(lists);
    }
    /**
     * 查询设备模拟量
     * @param deviceId
     * @return
     */
    public List<Table> findDeviceAnalogs(long deviceId){
        return tableDao.queryBuilder().where(TableDao.Properties.DeviceId.eq(deviceId)).orderAsc(TableDao.Properties.I).list();
    }

    /**
     * 删除所有模拟量
     */
    public void deleteAll(){
        tableDao.deleteAll();
    }
}
