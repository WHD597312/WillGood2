package com.peihou.willgood2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.peihou.willgood2.database.dao.DaoMaster;

/**
 * Created by win7 on 2018/3/22.
 */

public class DBManager {
    private final static String dbName="willgood2";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;
    private DBManager(Context context){
        this.context=context;
        openHelper=new DaoMaster.DevOpenHelper(context,dbName,null);
    }

    /**
     * 获取单例
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context){
        if (mInstance==null){
            synchronized (DBManager.class){
                if (mInstance==null){
                    mInstance=new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取刻度数据库
     * @return
     */
    public SQLiteDatabase getReadableDatabase(){
        if (openHelper==null){
            openHelper=new DaoMaster.DevOpenHelper(context,dbName,null);
        }
        SQLiteDatabase db=openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     * @return
     */
    public SQLiteDatabase getWritableDasebase(){
        if (openHelper==null){
            openHelper=new DaoMaster.DevOpenHelper(context,dbName,null);
        }
        SQLiteDatabase db=openHelper.getWritableDatabase();
        return db;
    }
}
