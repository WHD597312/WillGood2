package com.peihou.willgood2.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.peihou.willgood2.pojo.Alerm;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ALERM".
*/
public class AlermDao extends AbstractDao<Alerm, Long> {

    public static final String TABLENAME = "ALERM";

    /**
     * Properties of entity Alerm.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceMac = new Property(1, String.class, "deviceMac", false, "DEVICE_MAC");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Content = new Property(4, String.class, "content", false, "CONTENT");
        public final static Property State = new Property(5, int.class, "state", false, "STATE");
        public final static Property DeviceId = new Property(6, long.class, "deviceId", false, "DEVICE_ID");
        public final static Property Value = new Property(7, double.class, "value", false, "VALUE");
        public final static Property DeviceAlarmBroadcast = new Property(8, int.class, "deviceAlarmBroadcast", false, "DEVICE_ALARM_BROADCAST");
        public final static Property DeviceAlarmFlag = new Property(9, int.class, "deviceAlarmFlag", false, "DEVICE_ALARM_FLAG");
        public final static Property State2 = new Property(10, int.class, "state2", false, "STATE2");
    }


    public AlermDao(DaoConfig config) {
        super(config);
    }
    
    public AlermDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ALERM\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_MAC\" TEXT," + // 1: deviceMac
                "\"NAME\" TEXT," + // 2: name
                "\"TYPE\" INTEGER NOT NULL ," + // 3: type
                "\"CONTENT\" TEXT," + // 4: content
                "\"STATE\" INTEGER NOT NULL ," + // 5: state
                "\"DEVICE_ID\" INTEGER NOT NULL ," + // 6: deviceId
                "\"VALUE\" REAL NOT NULL ," + // 7: value
                "\"DEVICE_ALARM_BROADCAST\" INTEGER NOT NULL ," + // 8: deviceAlarmBroadcast
                "\"DEVICE_ALARM_FLAG\" INTEGER NOT NULL ," + // 9: deviceAlarmFlag
                "\"STATE2\" INTEGER NOT NULL );"); // 10: state2
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ALERM\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Alerm entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindLong(4, entity.getType());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
        stmt.bindLong(6, entity.getState());
        stmt.bindLong(7, entity.getDeviceId());
        stmt.bindDouble(8, entity.getValue());
        stmt.bindLong(9, entity.getDeviceAlarmBroadcast());
        stmt.bindLong(10, entity.getDeviceAlarmFlag());
        stmt.bindLong(11, entity.getState2());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Alerm entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindLong(4, entity.getType());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(5, content);
        }
        stmt.bindLong(6, entity.getState());
        stmt.bindLong(7, entity.getDeviceId());
        stmt.bindDouble(8, entity.getValue());
        stmt.bindLong(9, entity.getDeviceAlarmBroadcast());
        stmt.bindLong(10, entity.getDeviceAlarmFlag());
        stmt.bindLong(11, entity.getState2());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Alerm readEntity(Cursor cursor, int offset) {
        Alerm entity = new Alerm( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceMac
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // content
            cursor.getInt(offset + 5), // state
            cursor.getLong(offset + 6), // deviceId
            cursor.getDouble(offset + 7), // value
            cursor.getInt(offset + 8), // deviceAlarmBroadcast
            cursor.getInt(offset + 9), // deviceAlarmFlag
            cursor.getInt(offset + 10) // state2
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Alerm entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setContent(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setState(cursor.getInt(offset + 5));
        entity.setDeviceId(cursor.getLong(offset + 6));
        entity.setValue(cursor.getDouble(offset + 7));
        entity.setDeviceAlarmBroadcast(cursor.getInt(offset + 8));
        entity.setDeviceAlarmFlag(cursor.getInt(offset + 9));
        entity.setState2(cursor.getInt(offset + 10));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Alerm entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Alerm entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Alerm entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
