package cn.com.broadlink.sdk.constants.controller;

/**
 * 控制模块的ErrCode
 * 从 -3100 到 -3199
 */
public class BLControllerErrCode {
    /** 成功 **/
    public static final int SUCCESS = 0;

    /** 不支持的命令类型。 **/
    public static final int NOT_SUPPORT = -4;

    /** 设备不在线 **/
    public static final int NOT_ACCESS = -3;

    /*** 认证失败，设备已重置 */
    public static final int AUTH_FAIL = -7;

    /*** 脚本校验错误 */
    public static final int SCRIPT_CHECK_ERR = 14;

    /*** 脚本执行错误 */
    public static final int SCRIPT_EXECUTE_ERR = 16;

    /** 控制时未提供 account_id */
    public static final int NO_ACCOUNT_ID_ERR = -2017;

    /** License 不支持当前绑定到云端的 设备类型 */
    public static final int LICENSE_FORBIDDEN_BIND_ERR = -2016;

    /** 绑定到云端的设备数量超过限制 */
    public static final int TOO_MUCH_DEVICE_ERR = -2015;

    /** 远程认证数据解密失败 */
    public static final int REMOTE_AUTH_DECIPHERING_ERR = -2014;

    /** 账号无法通过校验 */
    public static final int ACCOUNT_VERIFY_ERR = -2013;

    /** UID 非法 */
    public static final int ILLEGALITY_UID_ERR = -2012;

    /** 认证码不匹配 */
    public static final int AUTH_CODE_ERR = -2011;

    /** 远程控制数据解密失败 */
    public static final int REMOTE_CONTROL_DECIPHERING_ERR = -2010;

    /** License 未在云端注册 */
    public static final int FORBIDDEN_LICENSE_ERR = -2009;

    /** 远程控制数据校验失败 */
    public static final int REMOTE_CONTROL_VERIFY_ERR = -2008;

    /** 远程控制数据加密类型错误 */
    public static final int REMOTE_CONTROL_ENCRYPTION_ERR = -2007;

    /** 网络故障 */
    public static final int NETWORK_ERR = -2006;

    /** 请求的命令不支持 */
    public static final int NO_SUCH_METROD_ERR = -2005;

    /** 绑定设备到云端请求的参数不合法 */
    public static final int BIND_PARAM_ERR = -2004;

    /** 控制的设备未绑定至云端 */
    public static final int NOT_BIND_ERR = -2003;

    /** 非法的认证码 */
    public static final int ILLEGALITY_AUTH_CODE_ERR = -2002;

    /** SDK 认证超时,请重新认证 */
    public static final int AUTH_TIMEOUT_ERR = -2001;

    /** SSL 读数据内容失败 */
    public static final int SSL_READ_BODY_ERR = -4042;

    /** SSL 读数据头失败 */
    public static final int SSL_READ_HEAD_ERR = -4041;

    /** SSL 写数据失败 */
    public static final int SSL_WRITE_ERR = -4040;

    /** SSL 证书无效 */
    public static final int SSL_CERTIFICATE_ERR = -4039;

    /** SSL 与服务器握手失败 */
    public static final int SSL_SHAKE_ERR = -4038;

    /** SSL 连接服务器失败 */
    public static final int SSL_CONNECT_ERR = -4037;

    /** 非法license */
    public static final int ILLEGALITY_LICENSE = -4036;

    /** SDK未认证 */
    public static final int NOT_AUTH = -4035;

    /** SDK认证失败 */
    public static final int AUTH_ERR = -4034;

    /** 系统调用出错 */
    public static final int SYS_INVOKE_FAIL = -4033;

    /** 电控指令到标准指令失败 */
    public static final int DEVICE_TO_ORDER_FAIL = -4032;

    /** 标准指令到电控指令失败 */
    public static final int ORDER_TO_DEVICE_FAIL = -4031;

    /** 数据缓存过小 */
    public static final int DATA_CACHE_ERR = -4030;

    /** 不存在对应的通信地址 */
    public static final int SERV_NOT_EXIT = -4029;

    /** 电控板与模块之间通信超时,电控板未及时响应 */
    public static final int ELE_BOARD_TIMEOUT = -4028;

    /** 传输数据过长 */
    public static final int DATA_ERR = -4026;

    /** SDK版本不支持远程操作 */
    public static final int NOT_SUPPORT_REMOTE = -4025;

    /** 操作过于频繁 */
    public static final int OPERATING_FAST = -4024;

    /** 无效license */
    public static final int LICENSE_REJECT = -4023;

    /** 不支持的command */
    public static final int FUNCTION_FAIL = -4022;

    /** script文件有误 */
    public static final int FILE_FAIL = -4020;

    /** 分配内存失败 */
    public static final int MALLOC_FAIL = -4018;

    /** 输入的设备信息有误 */
    public static final int INFO_ERR = -4017;

    /** JSON字符串的数据类型有误 */
    public static final int JSON_TYPE_ERR = -4016;

    /** 传入的JSON字符串有误 */
    public static final int JSON_ERR = -4015;

    /** 网络库初始化失败 */
    public static final int INIT_FAIL = -4014;

    /** 域名解析失败 */
    public static final int DNS_PARSE_FAIL = -4013;

    /** 设备控制ID错误，设备已经复位且控制终端未在局域网与设备配对配对 */
    public static final int CONTROL_ID_FAIL = -4012;

    /** 接收数据解密校验失败 */
    public static final int AES_CHECK_FAIL = -4011;

    /** 接收数据解密长度失败 */
    public static final int AES_LEN_FAIL = -4010;

    /** 网络消息类型错误 */
    public static final int HEAD_MSG_FAIL = -4009;

    /** 接收数据包校验失败 */
    public static final int HEAD_CHECK_FAIL = -4008;

    /** 接收数据包长度有误 */
    public static final int RECV_LEN_FAIL = -4007;

    /** socket发包失败 */
    public static final int SOCKET_SEND_FAIL = -4005;

    /** 设置socket属性失败 */
    public static final int SET_SOCKET_OPT_FAIL = -4004;

    /** 创建socket失败 */
    public static final int CREATE_SOCKET_FAIL = -4003;

    /** 取消easyconfig */
    public static final int EASYCONFIG_CANCEL = -4002;

    /** 设备不在局域网 */
    public static final int NOT_LAN = -4001;

    /** 超时 */
    public static final int TIMEOUT = -4000;
}
