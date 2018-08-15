package com.yuyuehao.app;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Wang
 * on 2018-08-14
 */

public class ContentData {


    public static final String AUTHORITY = "com.yuyuehao.app.circontentprovider";
    public static final String DATABASE_NAME = "log.db";
    public static final int DATABASE_VERSION = 7;
    public static final String CIRC_TABLE = "circulation";

    public static final class TableData implements BaseColumns {
        public static final String TABLE_NAME = "circulation";

        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+CIRC_TABLE);

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/circulation";
        // 单一数据的MIME类型字符串应该以vnd.android.cursor.item/开头
        public static final String CONTENT_TYPE_ITME = "vnd.android.cursor.item/circulation";
        /* 自定义匹配码 */
        public static final int MATCHS = 1;
        /* 自定义匹配码 */
        public static final int MATCH = 2;

        public static final String ID = "_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String RECORD = "record";
        public static final String SEND_REG_TIME = "send_reg_time";
        public static final String LOG_TYPE = "log_type";
        public static final String LOG_SEND_TYPE = "log_send_type";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DATA6 = "data6";
        public static final String DATA7 = "data7";

        public static final String LOG_UID = "log_uid";

        public static final UriMatcher uriMatcher;

        static {
            // 常量UriMatcher.NO_MATCH表示不匹配任何路径的返回码
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            // 如果match()方法匹配content://hb.android.teacherProvider/teachern路径,返回匹配码为MATCHS
            uriMatcher.addURI(ContentData.AUTHORITY, "circulation", MATCHS);
            // 如果match()方法匹配content://hb.android.teacherProvider/teacher/230,路径，返回匹配码为MATCH
            uriMatcher.addURI(ContentData.AUTHORITY, "circulation/#",MATCH);
        }
    }
}
