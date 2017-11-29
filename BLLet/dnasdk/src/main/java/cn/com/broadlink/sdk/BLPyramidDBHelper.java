package cn.com.broadlink.sdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuxuyang on 15/11/4.
 */
final class BLPyramidDBHelper extends SQLiteOpenHelper{
    private static String DB_NAME = "pyramid.db";
    protected static String TABLE_NAME = "pyramid";
    protected static String COLUMN_id = "did";
    protected static String COLUMN_TYPE = "type";
    protected static String COLUMN_DATA = "data";

    BLPyramidDBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS pyramid (did INTEGER PRIMARY KEY AUTOINCREMENT, type INTEGER, data TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pyramid");
    }
}
