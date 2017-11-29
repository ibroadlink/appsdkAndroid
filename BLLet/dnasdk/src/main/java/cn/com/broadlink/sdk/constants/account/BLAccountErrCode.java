package cn.com.broadlink.sdk.constants.account;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public final class BLAccountErrCode {

    /** 成功 **/
    public static final int SUCCESSS = 0;

    /** session失效 **/
    public static final int SESSION_INVALID = -1000;

    /** 服务器繁忙 **/
    public static final int SERVER_BUSY = -1001;

    /** 验证码错误 **/
    public static final int ERROR_VERIFICATION_CODE = -1002;

    /** 帐号已被注册 **/
    public static final int ACCOUNT_HAS_REGISTERED = -1003;

    /** 请求时间过期 **/
    public static final int REQUERST_TIME_EXPIRED = -1004;

    /** 数据错误 **/
    public static final int ERROR_DATA = -1005;

    /** 帐号或密码不正确 **/
    public static final int ACCOUNT_PASSWORD_ERR = -1006;

    /** 未登陆 **/
    public static final int UNLOGIN_IN = -1009;

    /** 请重新登陆 **/
    public static final int LOGIN_AGAIN = -1012;

    /** 未绑定第三方帐号 **/
    public static final int THRID_ACCOUNT_UNBIND = -1018;

    /** 账号不存在 **/
    public static final int ACCOUNT_NOT_EXIST = -1035;

    /** 用户未登录 **/
    public static final int USER_UNLOGIN = -2006;

    /*** 没有权限 **/
    public static final int UN_PERMISSION = -2007;

    /** 不存在该家庭 **/
    public static final int FAMILY_NOT_EXIST = -2008;

    /** 家庭版本已过期 **/
    public static final int FAMILY_VISION_INVALID = -2014;
}
