package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.DeviceDao;
import com.peihou.willgood2.pojo.Device;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceDaoImpl {
        private Context context;
        private SQLiteDatabase db;
        private DaoMaster master;
        private DeviceDao deviceDao;
        private DaoSession session;
        public DeviceDaoImpl(Context context) {
            this.context = context;
            db= DBManager.getInstance(context).getWritableDasebase();
            master=new DaoMaster(db);
            session=master.newSession();
            deviceDao=session.getDeviceDao();
        }

    /**
     * 添加设备
     * @param device
     * @return
     */
    public void insert(Device device){
       long n=deviceDao.insert(device);
        Log.i("boolean","--->"+n);
    }

    /**
     * 删除设备
     * @param device
     */
    public void delete(Device device){
        deviceDao.delete(device);
    }

    /**
     * 批量删除设备
     * @param devices
     */
    public void deleteDevices(List<Device> devices){
        deviceDao.deleteInTx(devices);
    }

    /**
     * 删除所有设备
     */
    public void deleteAll(){
        deviceDao.deleteAll();
    }
    /**
     * 修改设备
     * @param device
     */
    public void update(Device device){
        deviceDao.update(device);
    }

    /**
     * 根据设备id,查询设备
     * @param deviceId
     * @return
     */
    public Device findDeviceById(long deviceId){
        return deviceDao.queryBuilder().where(DeviceDao.Properties.DeviceId.eq(deviceId)).unique();
    }

    /**
     * 查询同一个macd的所有设备
     * @param deviceMac
     * @return
     */
    public List<Device> findDevicesByMac(String deviceMac){
        return deviceDao.queryBuilder().where(DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac)).list();
    }
    public Device findDeviceByMac(String deviceMac){
        return deviceDao.queryBuilder().where(DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac)).unique();
    }
    public Device findDeviceByMac2(String deviceMac){
        WhereCondition whereCondition=deviceDao.queryBuilder().and(DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac),DeviceDao.Properties.Share.isNull());
        return deviceDao.queryBuilder().where(DeviceDao.Properties.DeviceOnlyMac.eq(deviceMac)).unique();
    }


    /**
     * 查询设备
     * @return
     */
    public List<Device> findAllDevice(){
        return deviceDao.loadAll();
    }
}
