package com.peihou.willgood2.database.dao.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.DBManager;
import com.peihou.willgood2.database.dao.DaoMaster;
import com.peihou.willgood2.database.dao.DaoSession;
import com.peihou.willgood2.database.dao.Line2Dao;
import com.peihou.willgood2.database.dao.LinkedDao;
import com.peihou.willgood2.pojo.Linked;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class DeviceLinkDaoImpl {
    private Context context;
    private SQLiteDatabase db;
    private DaoMaster master;
    private LinkedDao linkedDao;
    private DaoSession session;
    public DeviceLinkDaoImpl(Context context) {
        this.context = context;
        db= DBManager.getInstance(context).getWritableDasebase();
        master=new DaoMaster(db);
        session=master.newSession();
        linkedDao=session.getLinkedDao();
    }
    public void insert(Linked linked){
        linkedDao.insert(linked);
    }
    public void update(Linked linked){
        linkedDao.update(linked);
    }

    /**
     * 批量更新联动
     * @param linkeds
     */
    public void updates(List<Linked> linkeds){
        linkedDao.updateInTx(linkeds);
    }
    public void delete(Linked linked){
        linkedDao.delete(linked);
    }

//        links.add(new Link(0,true,"温度联动"));
//        links.add(new Link(1,true,"湿度联动"));
//        links.add(new Link(2,true,"开关量联动"));
//        links.add(new Link(3,true,"电流联动"));
//        links.add(new Link(4,true,"电压联动"));
//        links.add(new Link(5,true,"模拟量联动"));



    /**
     *
     * @param deviceMac
     * @param type
     * @param condition
     * @param triState
     * @param preline
     * @param lastline
     * @param triType
     * @return
     */
    public Linked findLinked(String deviceMac,int type,int condition,int triState,int preline,int lastline,int triType){
        WhereCondition whereCondition=linkedDao.queryBuilder().and(
                LinkedDao.Properties.DeviceMac.eq(deviceMac),
                LinkedDao.Properties.Type.eq(type),
                LinkedDao.Properties.Condition.eq(condition),
                LinkedDao.Properties.TriState.eq(triState),
                LinkedDao.Properties.PreLines.eq(preline),LinkedDao.Properties.PreLines.eq(preline),
                LinkedDao.Properties.LastLines.eq(lastline),LinkedDao.Properties.TriType.eq(triType));
        return linkedDao.queryBuilder().where(whereCondition).unique();
    }
    /**
     *
     * @param deviceMac
     * @param type
     * @param condition
     * @param conditionState 为1时，查询>condition的某些联动,为0时，查询<=condition的某些联动
     * @return
     */
    public List<Linked> findLinkeds(String deviceMac,int type,int condition,int conditionState){
        WhereCondition whereCondition=null;
        if (conditionState==1){
            whereCondition=linkedDao.queryBuilder().and(LinkedDao.Properties.DeviceMac.eq(deviceMac)
                    ,LinkedDao.Properties.Type.eq(type),LinkedDao.Properties.Condition.gt(condition));
        }else if (conditionState==0){
            whereCondition=linkedDao.queryBuilder().and(LinkedDao.Properties.DeviceMac.eq(deviceMac)
                    ,LinkedDao.Properties.Type.eq(type),LinkedDao.Properties.Condition.le(condition));
        }
        return linkedDao.queryBuilder().where(whereCondition).list();
    }

    /**
     * 查询设备的某一类型的所有联动
     * @param deviceMac
     * @param type
     * @return
     */
    public List<Linked>findLinkeds(String deviceMac,int type){
        WhereCondition whereCondition=linkedDao.queryBuilder().and(
                LinkedDao.Properties.DeviceMac.eq(deviceMac),
                LinkedDao.Properties.Type.eq(type),LinkedDao.
                        Properties.Visitity.eq(1));
        return linkedDao.queryBuilder().where(whereCondition).list();
    }

    /**
     * 查询开关量联动
     * @param deviceMac
     * @param triState
     * @param preline
     * @param lastline
     * @param triType
     * @return
     */
    public Linked findLinked(String deviceMac,int triState,int preline,int lastline,int triType,int switchLine){
        WhereCondition whereCondition=linkedDao.queryBuilder().and(
                LinkedDao.Properties.DeviceMac.eq(deviceMac),
                LinkedDao.Properties.Type.eq(2),
                LinkedDao.Properties.SwitchLine.eq(switchLine),
                LinkedDao.Properties.TriState.eq(triState),
                LinkedDao.Properties.PreLines.eq(preline),LinkedDao.Properties.PreLines.eq(preline),
                LinkedDao.Properties.LastLines.eq(lastline),LinkedDao.Properties.TriType.eq(triType));

        return linkedDao.queryBuilder().where(whereCondition).unique();
    }
    /**
     * 删除某设备一类型的所有联动
     * @param deviceMac
     * @param type
     */
    public void deleteLindeds(String deviceMac,int type){
        WhereCondition whereCondition=linkedDao.queryBuilder().and(LinkedDao.Properties.DeviceMac.eq(deviceMac),LinkedDao.Properties.Type.eq(type));
        List<Linked> list=linkedDao.queryBuilder().where(whereCondition).list();
        linkedDao.deleteInTx(list);
    }
    public void deleteLinekeds(String deviceMac){
        List<Linked> list=linkedDao.queryBuilder().where(LinkedDao.Properties.DeviceMac.eq(deviceMac)).list();
        if (list!=null && !list.isEmpty()){
            linkedDao.deleteInTx(list);
        }
    }

    /**
     * 删除所有的联动数据
     */
    public void deleteAll(){
        linkedDao.deleteAll();
    }
}
