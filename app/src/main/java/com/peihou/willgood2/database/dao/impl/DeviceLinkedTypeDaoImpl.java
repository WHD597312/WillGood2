package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.LinkedTypeDao;
import com.peihou.willgood2.pojo.LinkedType;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceLinkedTypeDaoImpl {
    private LinkedTypeDao linkedTypeDao;
    public DeviceLinkedTypeDaoImpl(Context context) {
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
        linkedTypeDao=session.getLinkedTypeDao();
    }

    /**
     * 批量插入设备联动类型
     * @param linkedTypes
     */
    public void insertLinkedTypes(List<LinkedType> linkedTypes){
        linkedTypeDao.insertInTx(linkedTypes);
    }

    /**
     * 更新设备联动类型
     * @param linkedType
     */
    public void update(LinkedType linkedType){
        linkedTypeDao.update(linkedType);
    }

    /**
     * 批量更新联动类型
     * @param linkedTypes
     */
    public void updateLinkedTypes(List<LinkedType> linkedTypes){
        linkedTypeDao.updateInTx(linkedTypes);
    }
    /**
     * 批量删除设备的联动
     * @param deviceMac
     */
    public void deleteLinkedTypes(String deviceMac){
        List<LinkedType> list=linkedTypeDao.queryBuilder().where(LinkedTypeDao.Properties.MacAddress.eq(deviceMac)).list();
        linkedTypeDao.deleteInTx(list);
    }

    /**
     * 查询设备的所有联动
     * @param deviceMac
     * @return
     */
    public List<LinkedType> findLinkdType(String deviceMac){
        return linkedTypeDao.queryBuilder().where(LinkedTypeDao.Properties.MacAddress.eq(deviceMac)).orderAsc(LinkedTypeDao.Properties.Type).list();
    }


    /**
     * 删除所有的联动类型
     */
    public void deleteAll(){
        linkedTypeDao.deleteAll();
    }
}
