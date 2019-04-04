package com.peihou.willgood2.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.peihou.willgood2.pojo.Device;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DEVICE".
*/
public class DeviceDao extends AbstractDao<Device, Long> {

    public static final String TABLENAME = "DEVICE";

    /**
     * Properties of entity Device.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property DeviceId = new Property(1, long.class, "deviceId", false, "DEVICE_ID");
        public final static Property DeviceName = new Property(2, String.class, "deviceName", false, "DEVICE_NAME");
        public final static Property DeviceOnlyMac = new Property(3, String.class, "deviceOnlyMac", false, "DEVICE_ONLY_MAC");
        public final static Property DevicePassword = new Property(4, String.class, "devicePassword", false, "DEVICE_PASSWORD");
        public final static Property DeviceSellerId = new Property(5, int.class, "deviceSellerId", false, "DEVICE_SELLER_ID");
        public final static Property DeviceCreatorId = new Property(6, int.class, "deviceCreatorId", false, "DEVICE_CREATOR_ID");
        public final static Property DeviceModel = new Property(7, int.class, "deviceModel", false, "DEVICE_MODEL");
        public final static Property Name = new Property(8, String.class, "name", false, "NAME");
        public final static Property Imei = new Property(9, String.class, "imei", false, "IMEI");
        public final static Property Share = new Property(10, String.class, "share", false, "SHARE");
        public final static Property DeviceAuthority_Alarm = new Property(11, int.class, "deviceAuthority_Alarm", false, "DEVICE_AUTHORITY__ALARM");
        public final static Property DeviceAuthority_Map = new Property(12, int.class, "deviceAuthority_Map", false, "DEVICE_AUTHORITY__MAP");
        public final static Property DeviceAuthority_LineSwitch = new Property(13, int.class, "deviceAuthority_LineSwitch", false, "DEVICE_AUTHORITY__LINE_SWITCH");
        public final static Property DeviceAuthority_Analog = new Property(14, int.class, "deviceAuthority_Analog", false, "DEVICE_AUTHORITY__ANALOG");
        public final static Property DeviceAuthority_Switch = new Property(15, int.class, "deviceAuthority_Switch", false, "DEVICE_AUTHORITY__SWITCH");
        public final static Property DeviceAuthority_Poweroff = new Property(16, int.class, "deviceAuthority_Poweroff", false, "DEVICE_AUTHORITY__POWEROFF");
        public final static Property DeviceAuthority_Inching = new Property(17, int.class, "deviceAuthority_Inching", false, "DEVICE_AUTHORITY__INCHING");
        public final static Property DeviceAuthority_Timer = new Property(18, int.class, "deviceAuthority_Timer", false, "DEVICE_AUTHORITY__TIMER");
        public final static Property DeviceAuthority_Lock = new Property(19, int.class, "deviceAuthority_Lock", false, "DEVICE_AUTHORITY__LOCK");
        public final static Property DeviceAuthority_Linked = new Property(20, int.class, "deviceAuthority_Linked", false, "DEVICE_AUTHORITY__LINKED");
        public final static Property McuVersion = new Property(21, int.class, "mcuVersion", false, "MCU_VERSION");
        public final static Property DeviceState = new Property(22, int.class, "deviceState", false, "DEVICE_STATE");
        public final static Property Prelines = new Property(23, int.class, "prelines", false, "PRELINES");
        public final static Property Lastlines = new Property(24, int.class, "lastlines", false, "LASTLINES");
        public final static Property Prelineswitch = new Property(25, int.class, "prelineswitch", false, "PRELINESWITCH");
        public final static Property Lastlineswitch = new Property(26, int.class, "lastlineswitch", false, "LASTLINESWITCH");
        public final static Property Prelinesjog = new Property(27, int.class, "prelinesjog", false, "PRELINESJOG");
        public final static Property Lastlinesjog = new Property(28, int.class, "lastlinesjog", false, "LASTLINESJOG");
        public final static Property Online = new Property(29, boolean.class, "online", false, "ONLINE");
        public final static Property PlMemory = new Property(30, int.class, "plMemory", false, "PL_MEMORY");
        public final static Property LineJog = new Property(31, double.class, "lineJog", false, "LINE_JOG");
        public final static Property Line = new Property(32, double.class, "line", false, "LINE");
        public final static Property Line2 = new Property(33, double.class, "line2", false, "LINE2");
        public final static Property Line3 = new Property(34, double.class, "line3", false, "LINE3");
        public final static Property Line4 = new Property(35, double.class, "line4", false, "LINE4");
        public final static Property Line5 = new Property(36, double.class, "line5", false, "LINE5");
        public final static Property Line6 = new Property(37, double.class, "line6", false, "LINE6");
        public final static Property Line7 = new Property(38, double.class, "line7", false, "LINE7");
        public final static Property Line8 = new Property(39, double.class, "line8", false, "LINE8");
        public final static Property Line9 = new Property(40, double.class, "line9", false, "LINE9");
        public final static Property Line10 = new Property(41, double.class, "line10", false, "LINE10");
        public final static Property Line11 = new Property(42, double.class, "line11", false, "LINE11");
        public final static Property Line12 = new Property(43, double.class, "line12", false, "LINE12");
        public final static Property Line13 = new Property(44, double.class, "line13", false, "LINE13");
        public final static Property Line14 = new Property(45, double.class, "line14", false, "LINE14");
        public final static Property Line15 = new Property(46, double.class, "line15", false, "LINE15");
        public final static Property Line16 = new Property(47, double.class, "line16", false, "LINE16");
        public final static Property Temp = new Property(48, double.class, "temp", false, "TEMP");
        public final static Property Hum = new Property(49, double.class, "hum", false, "HUM");
        public final static Property Current = new Property(50, double.class, "current", false, "CURRENT");
        public final static Property Votage = new Property(51, double.class, "votage", false, "VOTAGE");
        public final static Property Re485 = new Property(52, String.class, "re485", false, "RE485");
        public final static Property Vlice2 = new Property(53, int.class, "vlice2", false, "VLICE2");
    }


    public DeviceDao(DaoConfig config) {
        super(config);
    }
    
    public DeviceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DEVICE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"DEVICE_ID\" INTEGER NOT NULL ," + // 1: deviceId
                "\"DEVICE_NAME\" TEXT," + // 2: deviceName
                "\"DEVICE_ONLY_MAC\" TEXT," + // 3: deviceOnlyMac
                "\"DEVICE_PASSWORD\" TEXT," + // 4: devicePassword
                "\"DEVICE_SELLER_ID\" INTEGER NOT NULL ," + // 5: deviceSellerId
                "\"DEVICE_CREATOR_ID\" INTEGER NOT NULL ," + // 6: deviceCreatorId
                "\"DEVICE_MODEL\" INTEGER NOT NULL ," + // 7: deviceModel
                "\"NAME\" TEXT," + // 8: name
                "\"IMEI\" TEXT," + // 9: imei
                "\"SHARE\" TEXT," + // 10: share
                "\"DEVICE_AUTHORITY__ALARM\" INTEGER NOT NULL ," + // 11: deviceAuthority_Alarm
                "\"DEVICE_AUTHORITY__MAP\" INTEGER NOT NULL ," + // 12: deviceAuthority_Map
                "\"DEVICE_AUTHORITY__LINE_SWITCH\" INTEGER NOT NULL ," + // 13: deviceAuthority_LineSwitch
                "\"DEVICE_AUTHORITY__ANALOG\" INTEGER NOT NULL ," + // 14: deviceAuthority_Analog
                "\"DEVICE_AUTHORITY__SWITCH\" INTEGER NOT NULL ," + // 15: deviceAuthority_Switch
                "\"DEVICE_AUTHORITY__POWEROFF\" INTEGER NOT NULL ," + // 16: deviceAuthority_Poweroff
                "\"DEVICE_AUTHORITY__INCHING\" INTEGER NOT NULL ," + // 17: deviceAuthority_Inching
                "\"DEVICE_AUTHORITY__TIMER\" INTEGER NOT NULL ," + // 18: deviceAuthority_Timer
                "\"DEVICE_AUTHORITY__LOCK\" INTEGER NOT NULL ," + // 19: deviceAuthority_Lock
                "\"DEVICE_AUTHORITY__LINKED\" INTEGER NOT NULL ," + // 20: deviceAuthority_Linked
                "\"MCU_VERSION\" INTEGER NOT NULL ," + // 21: mcuVersion
                "\"DEVICE_STATE\" INTEGER NOT NULL ," + // 22: deviceState
                "\"PRELINES\" INTEGER NOT NULL ," + // 23: prelines
                "\"LASTLINES\" INTEGER NOT NULL ," + // 24: lastlines
                "\"PRELINESWITCH\" INTEGER NOT NULL ," + // 25: prelineswitch
                "\"LASTLINESWITCH\" INTEGER NOT NULL ," + // 26: lastlineswitch
                "\"PRELINESJOG\" INTEGER NOT NULL ," + // 27: prelinesjog
                "\"LASTLINESJOG\" INTEGER NOT NULL ," + // 28: lastlinesjog
                "\"ONLINE\" INTEGER NOT NULL ," + // 29: online
                "\"PL_MEMORY\" INTEGER NOT NULL ," + // 30: plMemory
                "\"LINE_JOG\" REAL NOT NULL ," + // 31: lineJog
                "\"LINE\" REAL NOT NULL ," + // 32: line
                "\"LINE2\" REAL NOT NULL ," + // 33: line2
                "\"LINE3\" REAL NOT NULL ," + // 34: line3
                "\"LINE4\" REAL NOT NULL ," + // 35: line4
                "\"LINE5\" REAL NOT NULL ," + // 36: line5
                "\"LINE6\" REAL NOT NULL ," + // 37: line6
                "\"LINE7\" REAL NOT NULL ," + // 38: line7
                "\"LINE8\" REAL NOT NULL ," + // 39: line8
                "\"LINE9\" REAL NOT NULL ," + // 40: line9
                "\"LINE10\" REAL NOT NULL ," + // 41: line10
                "\"LINE11\" REAL NOT NULL ," + // 42: line11
                "\"LINE12\" REAL NOT NULL ," + // 43: line12
                "\"LINE13\" REAL NOT NULL ," + // 44: line13
                "\"LINE14\" REAL NOT NULL ," + // 45: line14
                "\"LINE15\" REAL NOT NULL ," + // 46: line15
                "\"LINE16\" REAL NOT NULL ," + // 47: line16
                "\"TEMP\" REAL NOT NULL ," + // 48: temp
                "\"HUM\" REAL NOT NULL ," + // 49: hum
                "\"CURRENT\" REAL NOT NULL ," + // 50: current
                "\"VOTAGE\" REAL NOT NULL ," + // 51: votage
                "\"RE485\" TEXT," + // 52: re485
                "\"VLICE2\" INTEGER NOT NULL );"); // 53: vlice2
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DEVICE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Device entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getDeviceId());
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
 
        String deviceOnlyMac = entity.getDeviceOnlyMac();
        if (deviceOnlyMac != null) {
            stmt.bindString(4, deviceOnlyMac);
        }
 
        String devicePassword = entity.getDevicePassword();
        if (devicePassword != null) {
            stmt.bindString(5, devicePassword);
        }
        stmt.bindLong(6, entity.getDeviceSellerId());
        stmt.bindLong(7, entity.getDeviceCreatorId());
        stmt.bindLong(8, entity.getDeviceModel());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(9, name);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(10, imei);
        }
 
        String share = entity.getShare();
        if (share != null) {
            stmt.bindString(11, share);
        }
        stmt.bindLong(12, entity.getDeviceAuthority_Alarm());
        stmt.bindLong(13, entity.getDeviceAuthority_Map());
        stmt.bindLong(14, entity.getDeviceAuthority_LineSwitch());
        stmt.bindLong(15, entity.getDeviceAuthority_Analog());
        stmt.bindLong(16, entity.getDeviceAuthority_Switch());
        stmt.bindLong(17, entity.getDeviceAuthority_Poweroff());
        stmt.bindLong(18, entity.getDeviceAuthority_Inching());
        stmt.bindLong(19, entity.getDeviceAuthority_Timer());
        stmt.bindLong(20, entity.getDeviceAuthority_Lock());
        stmt.bindLong(21, entity.getDeviceAuthority_Linked());
        stmt.bindLong(22, entity.getMcuVersion());
        stmt.bindLong(23, entity.getDeviceState());
        stmt.bindLong(24, entity.getPrelines());
        stmt.bindLong(25, entity.getLastlines());
        stmt.bindLong(26, entity.getPrelineswitch());
        stmt.bindLong(27, entity.getLastlineswitch());
        stmt.bindLong(28, entity.getPrelinesjog());
        stmt.bindLong(29, entity.getLastlinesjog());
        stmt.bindLong(30, entity.getOnline() ? 1L: 0L);
        stmt.bindLong(31, entity.getPlMemory());
        stmt.bindDouble(32, entity.getLineJog());
        stmt.bindDouble(33, entity.getLine());
        stmt.bindDouble(34, entity.getLine2());
        stmt.bindDouble(35, entity.getLine3());
        stmt.bindDouble(36, entity.getLine4());
        stmt.bindDouble(37, entity.getLine5());
        stmt.bindDouble(38, entity.getLine6());
        stmt.bindDouble(39, entity.getLine7());
        stmt.bindDouble(40, entity.getLine8());
        stmt.bindDouble(41, entity.getLine9());
        stmt.bindDouble(42, entity.getLine10());
        stmt.bindDouble(43, entity.getLine11());
        stmt.bindDouble(44, entity.getLine12());
        stmt.bindDouble(45, entity.getLine13());
        stmt.bindDouble(46, entity.getLine14());
        stmt.bindDouble(47, entity.getLine15());
        stmt.bindDouble(48, entity.getLine16());
        stmt.bindDouble(49, entity.getTemp());
        stmt.bindDouble(50, entity.getHum());
        stmt.bindDouble(51, entity.getCurrent());
        stmt.bindDouble(52, entity.getVotage());
 
        String re485 = entity.getRe485();
        if (re485 != null) {
            stmt.bindString(53, re485);
        }
        stmt.bindLong(54, entity.getVlice2());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Device entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getDeviceId());
 
        String deviceName = entity.getDeviceName();
        if (deviceName != null) {
            stmt.bindString(3, deviceName);
        }
 
        String deviceOnlyMac = entity.getDeviceOnlyMac();
        if (deviceOnlyMac != null) {
            stmt.bindString(4, deviceOnlyMac);
        }
 
        String devicePassword = entity.getDevicePassword();
        if (devicePassword != null) {
            stmt.bindString(5, devicePassword);
        }
        stmt.bindLong(6, entity.getDeviceSellerId());
        stmt.bindLong(7, entity.getDeviceCreatorId());
        stmt.bindLong(8, entity.getDeviceModel());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(9, name);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(10, imei);
        }
 
        String share = entity.getShare();
        if (share != null) {
            stmt.bindString(11, share);
        }
        stmt.bindLong(12, entity.getDeviceAuthority_Alarm());
        stmt.bindLong(13, entity.getDeviceAuthority_Map());
        stmt.bindLong(14, entity.getDeviceAuthority_LineSwitch());
        stmt.bindLong(15, entity.getDeviceAuthority_Analog());
        stmt.bindLong(16, entity.getDeviceAuthority_Switch());
        stmt.bindLong(17, entity.getDeviceAuthority_Poweroff());
        stmt.bindLong(18, entity.getDeviceAuthority_Inching());
        stmt.bindLong(19, entity.getDeviceAuthority_Timer());
        stmt.bindLong(20, entity.getDeviceAuthority_Lock());
        stmt.bindLong(21, entity.getDeviceAuthority_Linked());
        stmt.bindLong(22, entity.getMcuVersion());
        stmt.bindLong(23, entity.getDeviceState());
        stmt.bindLong(24, entity.getPrelines());
        stmt.bindLong(25, entity.getLastlines());
        stmt.bindLong(26, entity.getPrelineswitch());
        stmt.bindLong(27, entity.getLastlineswitch());
        stmt.bindLong(28, entity.getPrelinesjog());
        stmt.bindLong(29, entity.getLastlinesjog());
        stmt.bindLong(30, entity.getOnline() ? 1L: 0L);
        stmt.bindLong(31, entity.getPlMemory());
        stmt.bindDouble(32, entity.getLineJog());
        stmt.bindDouble(33, entity.getLine());
        stmt.bindDouble(34, entity.getLine2());
        stmt.bindDouble(35, entity.getLine3());
        stmt.bindDouble(36, entity.getLine4());
        stmt.bindDouble(37, entity.getLine5());
        stmt.bindDouble(38, entity.getLine6());
        stmt.bindDouble(39, entity.getLine7());
        stmt.bindDouble(40, entity.getLine8());
        stmt.bindDouble(41, entity.getLine9());
        stmt.bindDouble(42, entity.getLine10());
        stmt.bindDouble(43, entity.getLine11());
        stmt.bindDouble(44, entity.getLine12());
        stmt.bindDouble(45, entity.getLine13());
        stmt.bindDouble(46, entity.getLine14());
        stmt.bindDouble(47, entity.getLine15());
        stmt.bindDouble(48, entity.getLine16());
        stmt.bindDouble(49, entity.getTemp());
        stmt.bindDouble(50, entity.getHum());
        stmt.bindDouble(51, entity.getCurrent());
        stmt.bindDouble(52, entity.getVotage());
 
        String re485 = entity.getRe485();
        if (re485 != null) {
            stmt.bindString(53, re485);
        }
        stmt.bindLong(54, entity.getVlice2());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Device readEntity(Cursor cursor, int offset) {
        Device entity = new Device( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // deviceId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // deviceOnlyMac
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // devicePassword
            cursor.getInt(offset + 5), // deviceSellerId
            cursor.getInt(offset + 6), // deviceCreatorId
            cursor.getInt(offset + 7), // deviceModel
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // name
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // imei
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // share
            cursor.getInt(offset + 11), // deviceAuthority_Alarm
            cursor.getInt(offset + 12), // deviceAuthority_Map
            cursor.getInt(offset + 13), // deviceAuthority_LineSwitch
            cursor.getInt(offset + 14), // deviceAuthority_Analog
            cursor.getInt(offset + 15), // deviceAuthority_Switch
            cursor.getInt(offset + 16), // deviceAuthority_Poweroff
            cursor.getInt(offset + 17), // deviceAuthority_Inching
            cursor.getInt(offset + 18), // deviceAuthority_Timer
            cursor.getInt(offset + 19), // deviceAuthority_Lock
            cursor.getInt(offset + 20), // deviceAuthority_Linked
            cursor.getInt(offset + 21), // mcuVersion
            cursor.getInt(offset + 22), // deviceState
            cursor.getInt(offset + 23), // prelines
            cursor.getInt(offset + 24), // lastlines
            cursor.getInt(offset + 25), // prelineswitch
            cursor.getInt(offset + 26), // lastlineswitch
            cursor.getInt(offset + 27), // prelinesjog
            cursor.getInt(offset + 28), // lastlinesjog
            cursor.getShort(offset + 29) != 0, // online
            cursor.getInt(offset + 30), // plMemory
            cursor.getDouble(offset + 31), // lineJog
            cursor.getDouble(offset + 32), // line
            cursor.getDouble(offset + 33), // line2
            cursor.getDouble(offset + 34), // line3
            cursor.getDouble(offset + 35), // line4
            cursor.getDouble(offset + 36), // line5
            cursor.getDouble(offset + 37), // line6
            cursor.getDouble(offset + 38), // line7
            cursor.getDouble(offset + 39), // line8
            cursor.getDouble(offset + 40), // line9
            cursor.getDouble(offset + 41), // line10
            cursor.getDouble(offset + 42), // line11
            cursor.getDouble(offset + 43), // line12
            cursor.getDouble(offset + 44), // line13
            cursor.getDouble(offset + 45), // line14
            cursor.getDouble(offset + 46), // line15
            cursor.getDouble(offset + 47), // line16
            cursor.getDouble(offset + 48), // temp
            cursor.getDouble(offset + 49), // hum
            cursor.getDouble(offset + 50), // current
            cursor.getDouble(offset + 51), // votage
            cursor.isNull(offset + 52) ? null : cursor.getString(offset + 52), // re485
            cursor.getInt(offset + 53) // vlice2
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Device entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDeviceId(cursor.getLong(offset + 1));
        entity.setDeviceName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDeviceOnlyMac(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDevicePassword(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDeviceSellerId(cursor.getInt(offset + 5));
        entity.setDeviceCreatorId(cursor.getInt(offset + 6));
        entity.setDeviceModel(cursor.getInt(offset + 7));
        entity.setName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setImei(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setShare(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDeviceAuthority_Alarm(cursor.getInt(offset + 11));
        entity.setDeviceAuthority_Map(cursor.getInt(offset + 12));
        entity.setDeviceAuthority_LineSwitch(cursor.getInt(offset + 13));
        entity.setDeviceAuthority_Analog(cursor.getInt(offset + 14));
        entity.setDeviceAuthority_Switch(cursor.getInt(offset + 15));
        entity.setDeviceAuthority_Poweroff(cursor.getInt(offset + 16));
        entity.setDeviceAuthority_Inching(cursor.getInt(offset + 17));
        entity.setDeviceAuthority_Timer(cursor.getInt(offset + 18));
        entity.setDeviceAuthority_Lock(cursor.getInt(offset + 19));
        entity.setDeviceAuthority_Linked(cursor.getInt(offset + 20));
        entity.setMcuVersion(cursor.getInt(offset + 21));
        entity.setDeviceState(cursor.getInt(offset + 22));
        entity.setPrelines(cursor.getInt(offset + 23));
        entity.setLastlines(cursor.getInt(offset + 24));
        entity.setPrelineswitch(cursor.getInt(offset + 25));
        entity.setLastlineswitch(cursor.getInt(offset + 26));
        entity.setPrelinesjog(cursor.getInt(offset + 27));
        entity.setLastlinesjog(cursor.getInt(offset + 28));
        entity.setOnline(cursor.getShort(offset + 29) != 0);
        entity.setPlMemory(cursor.getInt(offset + 30));
        entity.setLineJog(cursor.getDouble(offset + 31));
        entity.setLine(cursor.getDouble(offset + 32));
        entity.setLine2(cursor.getDouble(offset + 33));
        entity.setLine3(cursor.getDouble(offset + 34));
        entity.setLine4(cursor.getDouble(offset + 35));
        entity.setLine5(cursor.getDouble(offset + 36));
        entity.setLine6(cursor.getDouble(offset + 37));
        entity.setLine7(cursor.getDouble(offset + 38));
        entity.setLine8(cursor.getDouble(offset + 39));
        entity.setLine9(cursor.getDouble(offset + 40));
        entity.setLine10(cursor.getDouble(offset + 41));
        entity.setLine11(cursor.getDouble(offset + 42));
        entity.setLine12(cursor.getDouble(offset + 43));
        entity.setLine13(cursor.getDouble(offset + 44));
        entity.setLine14(cursor.getDouble(offset + 45));
        entity.setLine15(cursor.getDouble(offset + 46));
        entity.setLine16(cursor.getDouble(offset + 47));
        entity.setTemp(cursor.getDouble(offset + 48));
        entity.setHum(cursor.getDouble(offset + 49));
        entity.setCurrent(cursor.getDouble(offset + 50));
        entity.setVotage(cursor.getDouble(offset + 51));
        entity.setRe485(cursor.isNull(offset + 52) ? null : cursor.getString(offset + 52));
        entity.setVlice2(cursor.getInt(offset + 53));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Device entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Device entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Device entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
