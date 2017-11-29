package cn.com.broadlink.sdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * 保存和读取Pyramid的配置项
 *
 * Created by zhuxuyang on 15/11/3.
 */
final class BLPyramidConfig {
    /** 常量区 **/
    private static final String PUBLIC_PREFERENCE = "PyramidPublic";
    private static final String PRIVATE_PREFERENCE = "PyramidPrivate";

    private static final String LASTUPDATETIME = "LASTUPDATETIME";

    /** 可配置项 **/
    private static final String UPDATE_PERIOD = "UPDATE_PERIOD";
    private static final String UPLOAD_PERIOD = "UPLOAD_PERIOD";
    private static final String MAX_DATA_COUNT = "MAX_DATA_COUNT";

    /** 变量区 **/
    private SharedPreferences mPreferencePublic;
    private SharedPreferences mPreferencePrivate;

    // 上传周期(单位秒)
    private int mUploadPeriod;
    // 上传最大数量
    private int mMaxDataCount;
    // 配置更新最短周期(单位分)
    private int mUpdatePeriod;

    /**
     * 构造方法
     * 初始化配置变量
     * @param context
     */
    BLPyramidConfig(Context context){
        mPreferencePublic = context.getSharedPreferences(PUBLIC_PREFERENCE, Context.MODE_PRIVATE);
        mPreferencePrivate  = context.getSharedPreferences(PRIVATE_PREFERENCE, Context.MODE_PRIVATE);

        init();
    }

    private void init(){
        mUploadPeriod = mPreferencePublic.getInt(UPLOAD_PERIOD, 120);
        mMaxDataCount = mPreferencePublic.getInt(MAX_DATA_COUNT, 100);
        mUpdatePeriod = mPreferencePublic.getInt(UPDATE_PERIOD, 720);
    }

    /**
     * 更新配置信息
     * @param cfg
     */
    void updateConfig(Map<String, String> cfg){
        SharedPreferences.Editor pub = mPreferencePublic.edit();
        SharedPreferences.Editor pri = mPreferencePrivate.edit();
        for (String key: cfg.keySet()) {
            pub.putString(key, cfg.get(key));
        }
        pub.commit();

        // 更新时间
        pri.putLong(LASTUPDATETIME, System.currentTimeMillis());
        pri.commit();
    }

    /**
     * 是否需要更新配置信息
     * @return
     */
    protected boolean shouldUpdate(){
        long lastTime = mPreferencePrivate.getLong(LASTUPDATETIME, 0);
        long time = System.currentTimeMillis() - lastTime;
        if(time / 60000 > getUpdatePeriod()){
            return true;
        }

        return false;
    }

    /**
     * 上传频率
     * 单位:s
     * @return
     */
    protected int getUploadPeriod(){
        return mUploadPeriod;
    }

    /**
     * 单次上传条数
     * @return
     */
    protected int getMaxDataCount(){
        return mMaxDataCount;
    }

    /**
     * 配置更新周期
     * 单位:m
     * @return
     */
    private int getUpdatePeriod(){
        return mUpdatePeriod;
    }
}
