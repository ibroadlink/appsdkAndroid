package cn.com.broadlink.sdk;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by YeJin on 2016/8/26.
 */
public class BLFileStorageUtils {
    /**
     * 文件存储路径相关
     **/
    public static String mScriptPath;

    public static String mUIPath;

    public static String mTempPath;

    public static String mIRCodeScriptPath;

    private BLFileStorageUtils(){}

    /**
     * 初始化SDK所需的文件夹
     *
     * @param context
     * @param customPath
     */
    public static void initFilePath(Context context, String customPath) {
        String basePath;
        if (customPath != null && !customPath.equals("")) {
            // 用户有自定义目录则使用
            basePath = customPath;
        } else {
            // 默认路径
            // 存在SDCARD的时候，路径设置到SDCARD
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                if (context.getExternalCacheDir() != null) {
                    basePath = context.getExternalCacheDir().toString();
                } else {
                    basePath = context.getCacheDir().toString();
                }
            } else {
                basePath = context.getCacheDir().toString();
            }

            // 加一层let文件夹
            basePath = basePath + File.separator + BLConstants.Controller.LET_PATH;
        }

        mScriptPath = basePath + File.separator + BLConstants.Controller.SCRIPT_PATH;
        mUIPath = basePath + File.separator + BLConstants.Controller.UI_PATH;
        mTempPath = basePath + File.separator + BLConstants.Controller.TEMP_PATH;
        mIRCodeScriptPath = basePath + File.separator + BLConstants.Controller.IRCODE_PATH;

        new File(mScriptPath).mkdirs();
        new File(mUIPath).mkdirs();
        new File(mTempPath).mkdirs();
        new File(mIRCodeScriptPath).mkdirs();
    }

    /**
     * 获取默认UI包保存路径
     *
     * @param pid
     * @return
     */
    public static String getDefaultUIPath(String pid) {
        return mUIPath + File.separator + pid;
    }

    /**
     * 获取默认脚本保存路径
     *
     * @param pid
     * @return
     */
    public static String getDefaultLuaScriptPath(String pid) {
        return mScriptPath + File.separator + pid + ".script";
    }


    /**
     * 获取默认JS脚本保存路径
     *
     * @param pid
     * @return
     */
    public static String getDefaultJSScriptPath(String pid) {
        return mScriptPath + File.separator + pid + ".js";
    }

}
