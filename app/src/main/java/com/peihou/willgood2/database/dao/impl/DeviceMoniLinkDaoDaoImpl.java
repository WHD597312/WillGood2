package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.MoniLinkDao;
import com.peihou.willgood2.pojo.MoniLink;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceMoniLinkDaoDaoImpl {
    private MoniLinkDao deviceMoniLinkDao;
    public DeviceMoniLinkDaoDaoImpl(Context context) {
        DBManager dbManager=DBManager.getInstance(context);//获取数据库管理者单例对象
        DaoSession session=dbManager.getDaoSession();//获取数据库会话对象
        deviceMoniLinkDao=session.getMoniLinkDao();
    }

    /**
     * 插入模拟量联动
     * @param moniLink
     */
    public void insert(MoniLink moniLink){
        deviceMoniLinkDao.insert(moniLink);
    }

    public void updateMoniLinks(List<MoniLink> moniLinks){
        deviceMoniLinkDao.updateInTx(moniLinks);
    }
    /**
     * 插入模拟量联动
     * @param moniLink
     */
    public void update(MoniLink moniLink){
        deviceMoniLinkDao.update(moniLink);
    }

    /**
     * 删除模拟量量联动
     * @param moniLink
     */
    public void delete(MoniLink moniLink){
        deviceMoniLinkDao.delete(moniLink);
    }

    /**
     * 删除与这个设备相关的联动
     * @param deviceMac
     */
    public void deletes(String deviceMac){
        List moniLinks=deviceMoniLinkDao.queryBuilder().where(MoniLinkDao.Properties.DeviceMac.eq(deviceMac)).list();
        if (moniLinks!=null && !moniLinks.isEmpty()){
            deviceMoniLinkDao.deleteInTx(moniLinks);
        }
    }
    /**
     * 查询唯一的模拟量联动
     * @param deviceMac
     * @param type
     * @param num
     * @param contition
     * @param triState
     * @return
     */
    public MoniLink findMoniLink(String deviceMac,int type,int num,int contition,int triState,int preline,int lastline,int triType){
        WhereCondition whereCondition=deviceMoniLinkDao.queryBuilder().and(
                MoniLinkDao.Properties.DeviceMac.eq(deviceMac),
                MoniLinkDao.Properties.Type.eq(type),
                MoniLinkDao.Properties.Num.eq(num),
                MoniLinkDao.Properties.Contition.eq(contition),
                MoniLinkDao.Properties.TriState.eq(triState),
                MoniLinkDao.Properties.PreLine.eq(preline),
                MoniLinkDao.Properties.LastLine.eq(lastline),
                MoniLinkDao.Properties.TriType.eq(triType)
        );

        return deviceMoniLinkDao.queryBuilder().where(whereCondition).unique();
    }

    /**
     * 查询一系列的模拟量联动
     * @param deviceMac
     * @param type
     * @param num
     * @return
     */
    public List<MoniLink> findMoniLinks(String deviceMac,int type,int num){
        WhereCondition whereCondition=deviceMoniLinkDao.queryBuilder().and(
                MoniLinkDao.Properties.DeviceMac.eq(deviceMac),
                MoniLinkDao.Properties.Type.eq(type),
                MoniLinkDao.Properties.Num.eq(num),
                MoniLinkDao.Properties.Visitity.eq(1)
        );
        return deviceMoniLinkDao.queryBuilder().where(whereCondition).list();
    }


    /**
     * 删除所有的模拟量联动
     */
    public void deleteAll(){
        deviceMoniLinkDao.deleteAll();
    }
}
