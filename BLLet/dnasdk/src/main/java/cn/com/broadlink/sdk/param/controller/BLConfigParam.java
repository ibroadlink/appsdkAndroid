package cn.com.broadlink.sdk.param.controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于SDK的各项配置，统一采用字符串的键值对
 */
public class BLConfigParam {
    /** 全局的http超时时间 **/
    public static final String HTTP_TIMEOUT = "HTTP_TIMEOUT";
    /** 账号系统的http超时时间 **/
    public static final String ACCOUNT_HTTP_TIMEOUT = "ACCOUNT_HTTP_TIMEOUT";
    /** 账号域名配置 **/
    public static final String ACCOUNT_HOST = "ACCOUNT_HOST";
    /** 家庭域名配置**/
    public static final String FAMILY_HOST = "FAMILY_HOST";
    /** 数据采集系统的域名配置 **/
    public static final String PICKER_HOST = "PICKER_HOST";
    /** OAUTH系统的域名配置 **/
    public static final String OAUTH_HOST = "OAUTH_HOST";

    /** 数据采集系统的http超时时间 **/
    public static final String PICKER_HTTP_TIMEOUT = "PICKER_HTTP_TIMEOUT";
    /** 数据采集系统模式 0-自定义 1-日志采集 **/
    public static final String PICKER_SYSTEM_MODE = "PICKER_SYSTEM_MODE";
    /** SDK文件存储根目录 **/
    public static final String SDK_FILE_PATH = "SDK_FILE_PATH";
    /** 控制系统sdk jni日志等级
     * 0不打印错误信息
     * 1打印error
     * 2打印warn
     * 3打印debug
     * 4打印info
     * **/
    public static final String CONTROLLER_JNI_LOG_LEVEL = "CONTROLLER_JNI_LOG_LEVEL";
    /** 本地超时时间 **/
    public static final String CONTROLLER_LOCAL_TIMEOUT = "CONTROLLER_LOCAL_TIMEOUT";
    /** 远程超时时间 **/
    public static final String CONTROLLER_REMOTE_TIMEOUT = "CONTROLLER_REMOTE_TIMEOUT";
    /** 网络模式 **/
    public static final String CONTROLLER_NETMODE = "CONTROLLER_NETMODE";
    /** 连续发包次数 **/
    public static final String CONTROLLER_SEND_COUNT = "CONTROLLER_SEND_COUNT";
    /** 设备批量查询**/
    public static final String CONTROLLER_QUERY_COUNT = "CONTROLLER_QUERY_COUNT";
    /** 设备配置超时时间 **/
    public static final String CONTROLLER_DEVICE_CONFIG_TIMEOUT = "CONTROLLER_DEVICE_CONFIG_TIMEOUT";
    /** 脚本路径 **/
    public static final String CONTROLLER_SCRIPT_PATH = "CONTROLLER_SCRIPT_PATH";
    /** 子设备脚本路径 **/
    public static final String CONTROLLER_SUB_SCRIPT_PATH = "CONTROLLER_SUB_SCRIPT_PATH";
//    /** 控制cookie **/
//    public static final String CONTROLLER_COOKIE = "CONTROLLER_COOKIE";
//    /** 保存路径 **/
//    public static final String CONTROLLER_SAVE_PATH = "CONTROLLER_SAVE_PATH";

    private Map<String, String> mParam;

    public BLConfigParam(){
        mParam = new HashMap<>();
    }

    public void put(String key, String value){
        mParam.put(key, value);
    }

    public String get(String key){
        return mParam.get(key);
    }
}
