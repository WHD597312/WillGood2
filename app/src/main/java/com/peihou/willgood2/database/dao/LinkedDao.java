package com.peihou.willgood2.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.peihou.willgood2.pojo.Linked;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LINKED".
*/
public class LinkedDao extends AbstractDao<Linked, Long> {

    public static final String TABLENAME = "LINKED";

    /**
     * Properties of entity Linked.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceMac = new Property(1, String.class, "deviceMac", false, "DEVICE_MAC");
        public final static Property McuVersion = new Property(2, int.class, "mcuVersion", false, "MCU_VERSION");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Lines = new Property(4, String.class, "lines", false, "LINES");
        public final static Property Name = new Property(5, String.class, "name", false, "NAME");
        public final static Property Condition = new Property(6, int.class, "condition", false, "CONDITION");
        public final static Property TriState = new Property(7, int.class, "triState", false, "TRI_STATE");
        public final static Property ConditionState = new Property(8, int.class, "conditionState", false, "CONDITION_STATE");
        public final static Property PreLines = new Property(9, int.class, "preLines", false, "PRE_LINES");
        public final static Property LastLines = new Property(10, int.class, "lastLines", false, "LAST_LINES");
        public final static Property TriType = new Property(11, int.class, "triType", false, "TRI_TYPE");
        public final static Property State = new Property(12, int.class, "state", false, "STATE");
        public final static Property Analog = new Property(13, int.class, "analog", false, "ANALOG");
        public final static Property SwitchLine = new Property(14, int.class, "switchLine", false, "SWITCH_LINE");
        public final static Property Visitity = new Property(15, int.class, "visitity", false, "VISITITY");
    }


    public LinkedDao(DaoConfig config) {
        super(config);
    }
    
    public LinkedDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LINKED\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_MAC\" TEXT," + // 1: deviceMac
                "\"MCU_VERSION\" INTEGER NOT NULL ," + // 2: mcuVersion
                "\"TYPE\" INTEGER NOT NULL ," + // 3: type
                "\"LINES\" TEXT," + // 4: lines
                "\"NAME\" TEXT," + // 5: name
                "\"CONDITION\" INTEGER NOT NULL ," + // 6: condition
                "\"TRI_STATE\" INTEGER NOT NULL ," + // 7: triState
                "\"CONDITION_STATE\" INTEGER NOT NULL ," + // 8: conditionState
                "\"PRE_LINES\" INTEGER NOT NULL ," + // 9: preLines
                "\"LAST_LINES\" INTEGER NOT NULL ," + // 10: lastLines
                "\"TRI_TYPE\" INTEGER NOT NULL ," + // 11: triType
                "\"STATE\" INTEGER NOT NULL ," + // 12: state
                "\"ANALOG\" INTEGER NOT NULL ," + // 13: analog
                "\"SWITCH_LINE\" INTEGER NOT NULL ," + // 14: switchLine
                "\"VISITITY\" INTEGER NOT NULL );"); // 15: visitity
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LINKED\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Linked entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
        stmt.bindLong(3, entity.getMcuVersion());
        stmt.bindLong(4, entity.getType());
 
        String lines = entity.getLines();
        if (lines != null) {
            stmt.bindString(5, lines);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(6, name);
        }
        stmt.bindLong(7, entity.getCondition());
        stmt.bindLong(8, entity.getTriState());
        stmt.bindLong(9, entity.getConditionState());
        stmt.bindLong(10, entity.getPreLines());
        stmt.bindLong(11, entity.getLastLines());
        stmt.bindLong(12, entity.getTriType());
        stmt.bindLong(13, entity.getState());
        stmt.bindLong(14, entity.getAnalog());
        stmt.bindLong(15, entity.getSwitchLine());
        stmt.bindLong(16, entity.getVisitity());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Linked entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String deviceMac = entity.getDeviceMac();
        if (deviceMac != null) {
            stmt.bindString(2, deviceMac);
        }
        stmt.bindLong(3, entity.getMcuVersion());
        stmt.bindLong(4, entity.getType());
 
        String lines = entity.getLines();
        if (lines != null) {
            stmt.bindString(5, lines);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(6, name);
        }
        stmt.bindLong(7, entity.getCondition());
        stmt.bindLong(8, entity.getTriState());
        stmt.bindLong(9, entity.getConditionState());
        stmt.bindLong(10, entity.getPreLines());
        stmt.bindLong(11, entity.getLastLines());
        stmt.bindLong(12, entity.getTriType());
        stmt.bindLong(13, entity.getState());
        stmt.bindLong(14, entity.getAnalog());
        stmt.bindLong(15, entity.getSwitchLine());
        stmt.bindLong(16, entity.getVisitity());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Linked readEntity(Cursor cursor, int offset) {
        Linked entity = new Linked( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // deviceMac
            cursor.getInt(offset + 2), // mcuVersion
            cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // lines
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // name
            cursor.getInt(offset + 6), // condition
            cursor.getInt(offset + 7), // triState
            cursor.getInt(offset + 8), // conditionState
            cursor.getInt(offset + 9), // preLines
            cursor.getInt(offset + 10), // lastLines
            cursor.getInt(offset + 11), // triType
            cursor.getInt(offset + 12), // state
            cursor.getInt(offset + 13), // analog
            cursor.getInt(offset + 14), // switchLine
            cursor.getInt(offset + 15) // visitity
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Linked entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMcuVersion(cursor.getInt(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setLines(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCondition(cursor.getInt(offset + 6));
        entity.setTriState(cursor.getInt(offset + 7));
        entity.setConditionState(cursor.getInt(offset + 8));
        entity.setPreLines(cursor.getInt(offset + 9));
        entity.setLastLines(cursor.getInt(offset + 10));
        entity.setTriType(cursor.getInt(offset + 11));
        entity.setState(cursor.getInt(offset + 12));
        entity.setAnalog(cursor.getInt(offset + 13));
        entity.setSwitchLine(cursor.getInt(offset + 14));
        entity.setVisitity(cursor.getInt(offset + 15));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Linked entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Linked entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Linked entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
