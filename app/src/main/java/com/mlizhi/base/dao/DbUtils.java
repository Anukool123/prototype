package com.mlizhi.base.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Hex;

public class DbUtils {
    public static void vacuum(SQLiteDatabase db) {
        db.execSQL("VACUUM");
    }

    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename) throws IOException {
        return executeSqlScript(context, db, assetFilename, true);
    }

    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename, boolean transactional) throws IOException {
        int count;
        String[] lines = new String(readAsset(context, assetFilename), Hex.DEFAULT_CHARSET_NAME).split(";(\\s)*[\n\r]");
        if (transactional) {
            count = executeSqlStatementsInTx(db, lines);
        } else {
            count = executeSqlStatements(db, lines);
        }
        DaoLog.m24i("Executed " + count + " statements from SQL script '" + assetFilename + "'");
        return count;
    }

    public static int executeSqlStatementsInTx(SQLiteDatabase db, String[] statements) {
        db.beginTransaction();
        try {
            int count = executeSqlStatements(db, statements);
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int executeSqlStatements(SQLiteDatabase db, String[] statements) {
        int count = 0;
        for (String line : statements) {
            String line2 = line2.trim();
            if (line2.length() > 0) {
                db.execSQL(line2);
                count++;
            }
        }
        return count;
    }

    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                return byteCount;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    public static byte[] readAsset(Context context, String filename) throws IOException {
        InputStream in = context.getResources().getAssets().open(filename);
        try {
            byte[] readAllBytes = readAllBytes(in);
            return readAllBytes;
        } finally {
            in.close();
        }
    }

    public static void logTableDump(SQLiteDatabase db, String tablename) {
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        try {
            DaoLog.m20d(DatabaseUtils.dumpCursorToString(cursor));
        } finally {
            cursor.close();
        }
    }
}
