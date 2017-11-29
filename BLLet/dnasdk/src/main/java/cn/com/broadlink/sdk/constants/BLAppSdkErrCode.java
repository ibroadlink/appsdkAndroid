package cn.com.broadlink.sdk.constants;

/**
 * Created by zhujunjie on 2017/7/4.
 */

public class BLAppSdkErrCode {
    /** APPSDK自定义错误码 **/
    /** 成功 **/
    public static final int SUCCESS = 0;

    /** 未知异常 **/
    public static final int ERR_UNKNOWN = -3001;

    /** 参数错误 **/
    public static final int ERR_PARAM = -3002;

    /** 未登录异常 **/
    public static final int ERR_NOT_LOGIN = -3003;

    /** 网络异常 **/
    public static final int ERR_HTTP = -3004;

    /** 网络请求过快 **/
    public static final int ERR_HTTP_TOO_FAST = -3005;

    /** 服务器无返回 **/
    public static final int ERR_SERVER_NO_RESULT = -3006;

    /** Action 不存在  不支持**/
    public static final int ERR_ACTION_NOT_SUPPORT = -3007;

    /** 配置线程已经存在**/
    public static final int ERR_CONFIG_THREAD_EXIST = -3008;

    /** 无SDK访问 读写权限 **/
    public static final int ERR_NO_PERMISSION = -3009;

    /** 设备不存在异常 **/
    public static final int ERR_DEVICE_NOT_FOUND = -3103;

    /** 获取Token异常 **/
    public static final int ERR_GET_TOKEN = -3104;

    /** 查询资源异常 **/
    public static final int ERR_QUERY_RESOURCE = -3105;

    /** 无法找到请求的资源 **/
    public static final int ERR_NO_RESOURCE = -3106;

    /** Body 格式错误 **/
    public static final int ERR_BODY_PARAM = -3107;

    /** 数据缺少必要字段**/
    public static final int ERR_LEAK_PARAM = -3108;

    /** URL,token 过期 **/
    public static final int ERR_TOKEN_OUT_OF_DATE = -3109;

    /** 请求的方式错误 **/
    public static final int ERR_WRONG_METHOD = -3110;

    /** 用户主动停止 **/
    public static final int ERR_USER_DENIED = -3111;

    /** 解压失败 **/
    public static final int ERR_UNZIP = -3112;

    /** 文件不存在 */
    public static final int ERR_FILE_NOT_FOUND = -3113;

    /** DNS解析失败 */
    public static final int ERR_GET_HOST_IP = -3114;

    /** Access key 为空 **/
    public static final int ERR_ACCESS_KEY_NULL = -3115;

    /** User cancel oauth**/
    public static final int ERR_OAUTH_CANCEL = -3116;

    /** timout **/
    public static final int ERR_TIMEOUT = -4000;

}
