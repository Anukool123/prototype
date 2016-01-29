package com.mlizhi.modules.spec.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import com.mlizhi.base.dao.AbstractDao;
import com.mlizhi.base.dao.Property;
import com.mlizhi.base.dao.internal.DaoConfig;
import com.mlizhi.modules.spec.dao.model.DetectModel;
import com.umeng.socialize.common.SocializeConstants;
import java.util.Date;
import p016u.aly.bq;

public class DetectDao extends AbstractDao<DetectModel, Long> {
    public static final String TABLENAME = "DETECT_MODEL";

    public static class Properties {
        public static final Property DetectType;
        public static final Property DetectValue;
        public static final Property Id;
        public static final Property InsertTime;
        public static final Property NurserType;
        public static final Property PartType;
        public static final Property UserId;

        static {
            Id = new Property(0, Long.class, SocializeConstants.WEIBO_ID, true, "_id");
            PartType = new Property(1, Integer.class, "partType", true, "PART_TYPE");
            DetectType = new Property(2, Integer.class, "detectType", true, "DETECT_TYPE");
            NurserType = new Property(3, Integer.class, "nurserType", true, "NURSER_TYPE");
            DetectValue = new Property(4, Double.class, "detectValue", false, "DETECT_VALUE");
            UserId = new Property(5, String.class, "userId", false, "USER_ID");
            InsertTime = new Property(6, Date.class, "insertTime", false, "INSERT_TIME");
        }
    }

    public DetectDao(DaoConfig config) {
        super(config);
    }

    public DetectDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : bq.f888b) + "'DETECT_MODEL' (" + "'_id' INTEGER PRIMARY KEY ," + "'PART_TYPE' INTEGER ," + "'DETECT_TYPE' INTEGER ," + "'NURSER_TYPE' INTEGER ," + "'DETECT_VALUE' DOUBLE ," + "'USER_ID' TEXT NOT NULL ," + "'INSERT_TIME' INTEGER);");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : bq.f888b) + "'DETECT_MODEL'");
    }

    protected void bindValues(SQLiteStatement stmt, DetectModel entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindLong(2, (long) entity.getPartType());
        stmt.bindLong(3, (long) entity.getDetectType());
        stmt.bindLong(4, (long) entity.getNurserType());
        stmt.bindDouble(5, entity.getDetectValue());
        stmt.bindString(6, entity.getUserId());
        Date date = entity.getInsertTime();
        if (date != null) {
            stmt.bindLong(7, date.getTime());
        }
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
    }

    public DetectModel readEntity(Cursor cursor, int offset) {
        Date date = null;
        int i = -1;
        Long valueOf = Long.valueOf(cursor.isNull(offset + 0) ? -1 : cursor.getLong(offset + 0));
        int i2 = cursor.isNull(offset + 1) ? -1 : cursor.getInt(offset + 1);
        int i3 = cursor.isNull(offset + 2) ? -1 : cursor.getInt(offset + 2);
        if (!cursor.isNull(offset + 3)) {
            i = cursor.getInt(offset + 3);
        }
        double d = cursor.isNull(offset + 4) ? -1.0d : cursor.getDouble(offset + 4);
        String string = cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5);
        if (!cursor.isNull(offset + 6)) {
            date = new Date(cursor.getLong(offset + 6));
        }
        return new DetectModel(valueOf, i2, i3, i, d, string, date);
    }

    public void readEntity(Cursor cursor, DetectModel entity, int offset) {
        Date date = null;
        int i = -1;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setPartType(cursor.isNull(offset + 1) ? -1 : cursor.getInt(offset + 1));
        entity.setDetectType(cursor.isNull(offset + 2) ? -1 : cursor.getInt(offset + 2));
        if (!cursor.isNull(offset + 3)) {
            i = cursor.getInt(offset + 3);
        }
        entity.setNurserType(i);
        entity.setDetectValue(cursor.isNull(offset + 4) ? -1.0d : cursor.getDouble(offset + 4));
        entity.setUserId(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        if (!cursor.isNull(offset + 6)) {
            date = new Date(cursor.getLong(offset + 6));
        }
        entity.setInsertTime(date);
    }

    protected Long updateKeyAfterInsert(DetectModel entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(DetectModel entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }
}
