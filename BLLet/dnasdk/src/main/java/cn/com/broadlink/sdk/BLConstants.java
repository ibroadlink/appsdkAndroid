package cn.com.broadlink.sdk;

/**
 * SDK常量类
 * Created by zhuxuyang on 15/12/25.
 */
final class BLConstants {
    /** 打印日志Tag **/
    protected static final String LOG_TAG = "BROADLINK_LET_SDK_LOG";

    protected static final String LET_LICENSE = "LET_LICENSE";
    protected static final String LET_CHANNEL = "LET_CHANNEL";
    protected static final String LET_CAMPANYID = "LET_CAMPANYID";

    /**
     * 控制系统相关的内部常量
     */
    static class Controller{
        /** 脚本文件夹 **/
        public static final String LET_PATH = "let";
        /** 临时文件夹 **/
        public static final String TEMP_PATH = "temp";
        /** 脚本文件夹 **/
        public static final String SCRIPT_PATH = "script";
        /** ui文件夹 **/
        public static final String UI_PATH = "ui";
        /** 红码脚本文件夹 **/
        public static final String IRCODE_PATH = "ircode";
    }

    static class Picker {
        /** 错误日志上报的EventID **/
        public static final String ERROR_Event_ID = "10001";
        /** 错误日志上报的EventLabel **/
        public static final String ERROR_Event_LABEL = "app error";
        /** 崩溃日志上报的EventID **/
        public static final String CRASH_Event_ID = "10002";
        /** 崩溃日志上报的EventLabel **/
        public static final String CRASH_Event_LABEL = "app crash";
        /** APP信息上报的EventID **/
        public static final String DATA_Event_ID = "10003";
        /** APP信息上报的EventLabel **/
        public static final String DATA_Event_LABEL = "app data";
    }
}
