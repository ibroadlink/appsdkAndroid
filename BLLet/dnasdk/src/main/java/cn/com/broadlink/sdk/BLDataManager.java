package cn.com.broadlink.sdk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLPrivateData;

/**
 * Created by zhuxuyang on 15/11/4.
 */
final class BLDataManager {
    private static volatile BLDataManager mDataManager;

    private BLPyramidDBHelper mHelper;
    private SQLiteDatabase mDB;

    private List<BLPyramidDBData> dataList = new ArrayList<>();

    private BLDataManager(Context context) {
        mHelper = new BLPyramidDBHelper(context);
        mDB = mHelper.getReadableDatabase();
    }

    static BLDataManager getInstance(Context context) {
        if (mDataManager == null) {
            synchronized (BLDataManager.class) {
                if (mDataManager == null) {
                    mDataManager = new BLDataManager(context.getApplicationContext());
                }
            }
        }

        return mDataManager;
    }

    /**
     * 开启事务批量插入
     * @return
     */
    private boolean insertData() {
        boolean result = true;
        if (dataList.isEmpty()) {
            return true;
        }

        synchronized (mDB) {
            if (mDB.isOpen()) {
                mDB.beginTransaction();

                List<BLPyramidDBData> cacheData = new ArrayList<BLPyramidDBData>(dataList);

                for (BLPyramidDBData data: cacheData) {
                    ContentValues cv = new ContentValues();
                    cv.put(BLPyramidDBHelper.COLUMN_TYPE, data.type);
                    cv.put(BLPyramidDBHelper.COLUMN_DATA, data.data);

                    if (mDB.insert(BLPyramidDBHelper.TABLE_NAME, null, cv) < 0) {
                        result = false;
                        break;
                    }
                }

                if (result) {
                    mDB.setTransactionSuccessful();
                    dataList.clear();
                }

                mDB.endTransaction();
            }
        }

        return result;
    }

    /**
     * 保存数据
     *
     * @param data
     */
    void putData(BLPyramidDBData data) {
        synchronized (dataList) {
            dataList.add(data);
            if (dataList.size() >= 20) {
                synchronized (mDataManager) {
                    insertData();
                }
            }
        }
    }

    /**
     * 查询数据
     *
     * @return
     */
     List<BLPyramidDBData> queryData(int limit) {
        List<BLPyramidDBData> listData = new ArrayList<BLPyramidDBData>();

        if (mDB.isOpen()) {
            synchronized (mDataManager) {
                insertData();

                Cursor cursor = mDB.query(BLPyramidDBHelper.TABLE_NAME, new String[]{BLPyramidDBHelper.COLUMN_id, BLPyramidDBHelper.COLUMN_TYPE, BLPyramidDBHelper.COLUMN_DATA}, null, null, null, null, null, limit <= 0 ? null : String.valueOf(limit));

                while (cursor.moveToNext()) {
                    BLPyramidDBData data = new BLPyramidDBData();
                    data.id = cursor.getInt(0);
                    data.type = cursor.getInt(1);
                    data.data = cursor.getString(2);
                    listData.add(data);
                }
            }
        }

        return listData;
    }

    /**
     * 删除数据
     *
     * @param id
     */
    void deleteData(int id) {
        if (mDB.isOpen()) {
            synchronized (mDataManager) {
                mDB.delete(BLPyramidDBHelper.TABLE_NAME, "did = ?", new String[]{String.valueOf(id)});
            }
        }
    }

    /**
     * 删除多组数据
     *
     * @param listData
     */
    void deleteData(List<BLPyramidDBData> listData) {
        for (BLPyramidDBData data : listData) {
            deleteData(data.id);
        }
    }

    /**
     * 关闭数据库
     */
    void close() {
        mDB.close();
    }
}
