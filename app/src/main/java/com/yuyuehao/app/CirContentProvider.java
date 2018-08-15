package com.yuyuehao.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static android.content.ContentUris.withAppendedId;
import static com.yuyuehao.app.ContentData.TableData.CONTENT_TYPE;
import static com.yuyuehao.app.ContentData.TableData.CONTENT_TYPE_ITME;
import static com.yuyuehao.app.ContentData.TableData.MATCH;
import static com.yuyuehao.app.ContentData.TableData.MATCHS;
import static com.yuyuehao.app.ContentData.TableData.uriMatcher;

/**
 * Created by Wang
 * on 2018-08-14
 */

public class CirContentProvider extends ContentProvider {

    private CirDBOpenHelper mCirDBOpenHelper = null;



    /**
     * onCreate()
     * 是一个回调函数，在ContentProvider创建的时候，就会运行,第二个参数为指定数据库名称，
     * 如果不指定，就会找不到数据库；
     * 如果数据库存在的情况下是不会再创建一个数据库的。
     * （当然首次调用 在这里也不会生成数据库必须调用SQLiteDatabase的
     * getWritableDatabase,getReadableDatabase两个方法中的一个才会创建数据库）
     */
    @Override
    public boolean onCreate() {
        //这里会调用 DBOpenHelper的构造函数创建一个数据库；
        mCirDBOpenHelper = new CirDBOpenHelper(this.getContext(), ContentData.DATABASE_NAME, ContentData.DATABASE_VERSION);
        return true;
    }




    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mCirDBOpenHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MATCHS:
                return db.query("circulation", projection, selection, selectionArgs, null, null, sortOrder);
            case MATCH:
                // 进行解析，返回值为10
                long id = ContentUris.parseId(uri);
                String where = "_ID=" + id;// 获取指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "";// 把其它条件附加上
                return db.query("circulation", projection, where, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MATCHS:
                return CONTENT_TYPE;
            case MATCH:
                return CONTENT_TYPE_ITME;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * 当执行这个方法的时候，如果没有数据库，他会创建，
     * 同时也会创建表，但是如果没有表，下面在执行insert的时候就会出错
     * 这里的插入数据也完全可以用sql语句书写，然后调用 db.execSQL(sql)执行。
     */



    public static ContentValues insertLogData(LogData logData){
        ContentValues cv = new ContentValues();
        cv.put("timestamp",logData.getTimestamp());
        cv.put("record",logData.getRecord());
        cv.put("send_reg_time",logData.getSend_reg_time());
        cv.put("log_type",logData.getLog_type());
        cv.put("log_send_type",logData.getLog_send_type());
        cv.put("data1",logData.getData1());
        cv.put("data2",logData.getData2());
        cv.put("data3",logData.getData3());
        cv.put("data4",logData.getData4());
        cv.put("data5",logData.getData5());
        cv.put("data6",logData.getData6());
        cv.put("data7",logData.getData7());
        cv.put("log_uid",logData.getLog_uid().toString());
        return cv;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //获得一个可写的数据库引用，如果数据库不存在，则根据onCreate的方法里创建；
        SQLiteDatabase db = mCirDBOpenHelper.getWritableDatabase();
        long id = 0;
        switch (uriMatcher.match(uri)) {
            case MATCHS:
                id = db.insert("circulation", null, values);    // 返回的是记录的行号，主键为int，实际上就是主键值
                return withAppendedId(uri, id);
            case MATCH:
                id = db.insert("circulation", null, values);
                String path = uri.toString();
                return Uri.parse(path.substring(0, path.lastIndexOf("/"))+id); // 替换掉id
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mCirDBOpenHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MATCHS:
                count = db.delete("circulation", selection, selectionArgs);
                break;
            case MATCH:
                // 下面的方法用于从URI中解析出id，对这样的路径content://hb.android.teacherProvider/teacher/10
                // 进行解析，返回值为10
                long id = ContentUris.parseId(uri);
                String where = "_ID=" + id;   // 删除指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "";   // 把其它条件附加上
                count = db.delete("circulation", where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        db.close();
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mCirDBOpenHelper.getWritableDatabase();
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MATCHS:
                count = db.update("circulation", values, selection, selectionArgs);
                break;
            case MATCH:
                // 下面的方法用于从URI中解析出id，对这样的路径content://com.ljq.provider.personprovider/person/10
                // 进行解析，返回值为10
                long id = ContentUris.parseId(uri);
                String where = "_ID=" + id;// 获取指定id的记录
                where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "";// 把其它条件附加上
                count = db.update("circulation", values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        db.close();
        return count;
    }
}
