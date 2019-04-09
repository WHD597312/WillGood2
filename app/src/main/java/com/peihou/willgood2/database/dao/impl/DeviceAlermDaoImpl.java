package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.AlermDao;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.pojo.Alerm;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceAlermDaoImpl {
    private AlermDao alermDao;
    public DeviceAlermDaoImpl(Context context) {
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
        alermDao=session.getAlermDao();
    }
    public void insert(Alerm alerm){
        alermDao.insert(alerm);
    }
    public void insertDeviceAlerms(List<Alerm> alerms){
        alermDao.insertInTx(alerms);
    }
    public void update(Alerm alerm){
        alermDao.update(alerm);
    }
    public void updateDeviceAlerms(List<Alerm> alerms){
        alermDao.updateInTx(alerms);
    }
    public void delete(Alerm alerm){
        alermDao.delete(alerm);
    }
    public void deleteDeviceAlerms(long deviceId){
        List<Alerm> list=findDeviceAlerms(deviceId);
        alermDao.deleteInTx(list);
    }
    public void deleteDeviceAlerms(String deviceMac){
        List<Alerm> list=findDeviceAlerms(deviceMac);
        if (list!=null && !list.isEmpty())
            alermDao.deleteInTx(list);
    }

    public void deleteAll(){
        alermDao.deleteAll();
    }
    public Alerm findDeviceAlerm(long deviceId,int type){
        WhereCondition whereCondition=alermDao.queryBuilder().and(AlermDao.Properties.DeviceId.eq(deviceId),AlermDao.Properties.Type.eq(type));
        return alermDao.queryBuilder().where(whereCondition).unique();
    }
    public Alerm findDeviceAlerm(String deviceMac,int type){
        WhereCondition whereCondition=alermDao.queryBuilder().and(AlermDao.Properties.DeviceMac.eq(deviceMac),AlermDao.Properties.Type.eq(type));
        return alermDao.queryBuilder().where(whereCondition).unique();
    }
    public List<Alerm> findDeviceAlerms(long deviceId){
        return alermDao.queryBuilder().where(AlermDao.Properties.DeviceId.eq(deviceId)).orderAsc(AlermDao.Properties.Type).list();
    }
    public List<Alerm> findDeviceAlerms(String deviceMac){
        return alermDao.queryBuilder().where(AlermDao.Properties.DeviceMac.eq(deviceMac)).orderAsc(AlermDao.Properties.Type).list();
    }

}
