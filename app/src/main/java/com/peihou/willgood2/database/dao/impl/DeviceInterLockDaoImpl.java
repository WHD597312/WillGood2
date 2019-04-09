package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.InterLockDao;
import com.peihou.willgood2.pojo.InterLock;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceInterLockDaoImpl {
    private InterLockDao interLockDao;

    public DeviceInterLockDaoImpl(Context context) {
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
        interLockDao=session.getInterLockDao();
    }

    /**
     * 批量插入设备互锁线路
     * @param list
     */
    public void inserts(List<InterLock> list){
        interLockDao.insertInTx(list);
    }

    /**
     * 更新单个设备互锁线路
     * @param lock
     */
    public void update(InterLock lock){
        interLockDao.update(lock);
    }

    /**
     * 批量更新设备
     * @param list
     */
    public void updates(List<InterLock> list){
        interLockDao.updateInTx(list);
    }

    /**
     * 删除设备互锁线路
     * @param deviceMac
     */
    public void deletes(String deviceMac){
        List<InterLock> interLocks=interLockDao.queryBuilder().where(InterLockDao.Properties.DeviceMac.eq(deviceMac)).list();
        if (interLocks!=null && !interLocks.isEmpty()){
            interLockDao.deleteInTx(interLocks);
        }
    }
    public void deleteAll(){
        interLockDao.deleteAll();
    }

    /**
     * 查询出唯一的设备线路互锁
     * @param deviceMac
     * @param i
     * @return
     */
    public InterLock findDeviceInterLock(String deviceMac,int i){
        WhereCondition whereCondition=interLockDao.queryBuilder().and(InterLockDao.Properties.DeviceMac.eq(deviceMac),InterLockDao.Properties.I.eq(i));
        return interLockDao.queryBuilder().where(whereCondition).unique();
    }
    /**
     * 查询设备互锁线路
     * @param deviceMac
     * @return
     */
    public List<InterLock> findDeviceInterLock(String deviceMac){
        List<InterLock> interLocks=interLockDao.queryBuilder().where(InterLockDao.Properties.DeviceMac.eq(deviceMac)).list();
        return interLocks;
    }

    /**
     * 查询在线的设备互锁线路
     * @param deviceMac
     * @return
     */
    public List<InterLock> findDeviceVisityInterLock(String deviceMac){
        WhereCondition whereCondition=interLockDao.queryBuilder().and(InterLockDao.Properties.DeviceMac.eq(deviceMac),InterLockDao.Properties.Visitity.eq(1));
        return interLockDao.queryBuilder().where(whereCondition).orderAsc(InterLockDao.Properties.I).list();
    }
}
