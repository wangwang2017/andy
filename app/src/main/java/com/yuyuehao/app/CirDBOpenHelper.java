package com.yuyuehao.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.yuyuehao.app.ContentData.TableData.TABLE_NAME;

/**
 * Created by Wang
 * on 2018-08-14
 */

public class CirDBOpenHelper extends SQLiteOpenHelper {


    public CirDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CirDBOpenHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("1","Create");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "(" + ContentData.TableData.ID
                + " integer primary key autoincrement , "
                + ContentData.TableData.TIMESTAMP +" integer,"
                + ContentData.TableData.RECORD + " varchar(20),"
                + ContentData.TableData.SEND_REG_TIME + " timestamp,"
                + ContentData.TableData.LOG_TYPE + " varchar(20),"
                + ContentData.TableData.LOG_SEND_TYPE + " varchar(20),"
                + ContentData.TableData.DATA1 + " varchar(20),"
                + ContentData.TableData.DATA2 + " varchar(20),"
                + ContentData.TableData.DATA3 + " varchar(20),"
                + ContentData.TableData.DATA4 + " varchar(20),"
                + ContentData.TableData.DATA5 + " varchar(20),"
                + ContentData.TableData.DATA6 + " varchar(20),"
                + ContentData.TableData.DATA7 + " varchar(20),"
                + ContentData.TableData.LOG_UID + " varchar(20))"+";");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("1","up");
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
