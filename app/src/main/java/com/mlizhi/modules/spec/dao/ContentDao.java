package com.mlizhi.modules.spec.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.mlizhi.base.dao.AbstractDao;
import com.mlizhi.base.dao.Property;
import com.mlizhi.base.dao.internal.DaoConfig;
import com.mlizhi.modules.spec.dao.model.ContentModel;

import java.util.Date;

import p016u.aly.bq;

public class ContentDao extends AbstractDao<ContentModel, Long> {
    public static final String TABLENAME = "CONTENT_MODEL";

    public static class Properties {
        public static final Property ContentComment;
        public static final Property ContentId;
        public static final Property ContentImageUrl;
        public static final Property ContentInsertTime;
        public static final Property ContentPraise;
        public static final Property ContentSummarize;
        public static final Property ContentTitle;
        public static final Property ContentType;
        public static final Property ContentView;
        public static final Property Id;

        static {
            Id = new Property(0, Long.class, "id", true, "_id");
            ContentTitle = new Property(1, String.class, "contentTitle", true, "CONTENT_TITLE");
            ContentSummarize = new Property(2, String.class, "contentSummarize", true, "CONTENT_SUMMARIZE");
            ContentImageUrl = new Property(3, String.class, "contentImageUrl", true, "CONTENT_IMAGE_URL");
            ContentPraise = new Property(4, String.class, "contentPraise", false, "CONTENT_PRAISE");
            ContentView = new Property(5, String.class, "contentView", false, "CONTENT_VIEW");
            ContentComment = new Property(6, String.class, "contentComment", false, "CONTENT_COMMENT");
            ContentInsertTime = new Property(7, Date.class, "contentInsertTime", false, "CONTENT_INSERT_TIME");
            ContentType = new Property(8, Integer.class, "contentType", false, "CONTENT_TYPE");
            ContentId = new Property(9, String.class, "contentId", false, "CONTENT_ID");
        }
    }

    public ContentDao(DaoConfig config) {
        super(config);
    }

    public ContentDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        db.execSQL("CREATE TABLE " + (ifNotExists ? "IF NOT EXISTS " : bq.f888b) + "'CONTENT_MODEL' (" + "'_id' INTEGER PRIMARY KEY ," + "'CONTENT_TITLE' TEXT ," + "'CONTENT_SUMMARIZE' TEXT ," + "'CONTENT_IMAGE_URL' TEXT ," + "'CONTENT_PRAISE' TEXT ," + "'CONTENT_VIEW' TEXT ," + "'CONTENT_COMMENT' TEXT ," + "'CONTENT_INSERT_TIME' INTEGER," + "'CONTENT_TYPE' INTEGER," + "'CONTENT_ID' TEXT);");
    }

    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        db.execSQL("DROP TABLE " + (ifExists ? "IF EXISTS " : bq.f888b) + "'CONTENT_MODEL'");
    }

    protected void bindValues(SQLiteStatement stmt, ContentModel entity) {
        stmt.clearBindings();
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id.longValue());
        }
        stmt.bindString(2, entity.getContentTitle() == null ? bq.f888b : entity.getContentTitle());
        stmt.bindString(3, entity.getContentSummarize() == null ? bq.f888b : entity.getContentSummarize());
        stmt.bindString(4, entity.getContentImageUrl() == null ? bq.f888b : entity.getContentImageUrl());
        stmt.bindString(5, entity.getContentPraise() == null ? bq.f888b : entity.getContentPraise());
        stmt.bindString(6, entity.getContentView() == null ? bq.f888b : entity.getContentView());
        stmt.bindString(7, entity.getContentComment() == null ? bq.f888b : entity.getContentComment());
        Date date = entity.getContentInsertTime();
        if (date != null) {
            stmt.bindLong(8, date.getTime());
        }
        stmt.bindLong(9, entity.getContentType() == null ? -1 : (long) entity.getContentType().intValue());
        stmt.bindString(10, entity.getContentId() == null ? bq.f888b : entity.getContentId());
    }

    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
    }

    public ContentModel readEntity(Cursor cursor, int offset) {
        String str = null;
        Long valueOf = cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0));
        String string = cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
        String string2 = cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2);
        String string3 = cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
        String string4 = cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4);
        String string5 = cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5);
        String string6 = cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6);
        Date date = cursor.isNull(offset + 7) ? null : new Date(cursor.getLong(offset + 7));
        Integer valueOf2 = Integer.valueOf(cursor.isNull(offset + 8) ? -1 : cursor.getInt(offset + 8));
        if (!cursor.isNull(offset + 9)) {
            str = cursor.getString(offset + 9);
        }
        return new ContentModel(valueOf, string, string2, string3, string4, string5, string6, date, valueOf2, str);
    }

    public void readEntity(Cursor cursor, ContentModel entity, int offset) {
        String str = null;
        entity.setId(cursor.isNull(offset + 0) ? null : Long.valueOf(cursor.getLong(offset + 0)));
        entity.setContentTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContentSummarize(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContentImageUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setContentPraise(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContentView(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setContentComment(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setContentInsertTime(cursor.isNull(offset + 7) ? null : new Date(cursor.getLong(offset + 7)));
        entity.setContentType(Integer.valueOf(cursor.isNull(offset + 8) ? -1 : cursor.getInt(offset + 8)));
        if (!cursor.isNull(offset + 9)) {
            str = cursor.getString(offset + 9);
        }
        entity.setContentId(str);
    }

    protected Long updateKeyAfterInsert(ContentModel entity, long rowId) {
        entity.setId(Long.valueOf(rowId));
        return Long.valueOf(rowId);
    }

    public Long getKey(ContentModel entity) {
        if (entity != null) {
            return entity.getId();
        }
        return null;
    }

    protected boolean isEntityUpdateable() {
        return true;
    }
}
