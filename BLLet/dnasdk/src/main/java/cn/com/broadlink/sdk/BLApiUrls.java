package cn.com.broadlink.sdk;

/**
 * Created by zhuxuyang on 16/1/20.
 */
final class BLApiUrls {
    
    public static void setLicenseId(String licenseId) {
        URL_ACCOUNT_BASE = String.format("https://%sbizaccount.ibroadlink.com", licenseId);
        APP_MANAGE_BASE = String.format("https://%sbizappmanage.ibroadlink.com", licenseId);
        URL_PROXY_BASE = String.format("https://%scloudauthorize.ibroadlink.com", licenseId);

        URL_FAMILY_BASE = String.format("https://%sbizihcv0.ibroadlink.com", licenseId);
        URL_FAMILY_PRIVATE_BASE = String.format("https://%sbizpd.ibroadlink.com", licenseId);

        URL_IRCODE_BASE = String.format("https://%srccode.ibroadlink.com", licenseId);
        URL_PYRAMID_BASE = String.format("https://%sbizappdata.ibroadlink.com", licenseId);
        URL_OAUTH_BASE = String.format("https://%soauth.ibroadlink.com", licenseId);

        URL_DEVICE_DATA_BASE = "rtasquery.ibroadlink.com";
    }

    /** 正式服务器 **/
    private static String URL_ACCOUNT_BASE;
    /** APP后台服务器 **/
    private static String APP_MANAGE_BASE;
    /** 第三方设备授权 **/
    private static String URL_PROXY_BASE;
    /** 家庭相关API*/
    private static String URL_FAMILY_BASE;
    /** 家庭私有相关API*/
    private static String URL_FAMILY_PRIVATE_BASE;
    /** 红码相关API**/
    private static String URL_IRCODE_BASE;
    /** 数据统计系统API */
    private static String URL_PYRAMID_BASE;
    /** Oauth授权系统API*/
    private static String URL_OAUTH_BASE;
    /** 设备数据上班相关API**/
    private static String URL_DEVICE_DATA_BASE;

    public static final class Account {

        public static void setUrlBase(String urlBase){
            URL_ACCOUNT_BASE = urlBase;
        }

        /**
         * 请求时间
         **/
        public static final String URL_TIMESTAMP() {return URL_ACCOUNT_BASE + "/account/api";}
        /**
         * 账户登录
         **/
        public static final String URL_LOGIN () {return URL_ACCOUNT_BASE + "/account/login";}
        /**
         * 获取手机验证码
         **/
        public static final String URL_SEND_REG_VCODE () {return URL_ACCOUNT_BASE + "/account/newregcode";}

        /**
         * 账户手机号快速登录
         **/
        public static final String URL_FAST_LOGIN () {return URL_ACCOUNT_BASE + "/account/fastlogin";}
        /**
         * 账户手机号快速登录，获取验证码
         **/
        public static final String URL_FAST_LOGIN_VCODE () {return URL_ACCOUNT_BASE + "/account/fastlogincode";}
        /**
         * 注册
         **/
        public static final String URL_REGIST () {return URL_ACCOUNT_BASE + "/account/register";}
        /**
         * 修改用户性别和生日
         */
        public static final String URL_MODIFY_GENDER () {return URL_ACCOUNT_BASE + "/account/modifysexandbirth";}
        /**
         * 修改用户头像
         **/
        public static final String URL_MODIFY_ICON () {return URL_ACCOUNT_BASE + "/account/modifyicon";}
        /**
         * 修改用户昵称
         **/
        public static final String URL_MODIFY_NICKNAME () {return URL_ACCOUNT_BASE + "/account/modifynickname";}
        /**
         * 获取用户信息
         **/
        public static final String URL_GET_USERINFO () {return URL_ACCOUNT_BASE + "/account/usersketchyinfo";}

        /**
         * 快速登录用户获取修改密码验证码
         **/
        public static final String URL_FAST_LOGIN_MODIFY_VCODE () {return URL_ACCOUNT_BASE + "/account/modifypwdcode";}
        /**
         * 快速登录用户修改密码
         **/
        public static final String URL_FAST_LOGIN_MODIFY_PASSWORD () {return URL_ACCOUNT_BASE + "/account/modifypwdbycode";}

        /**
         * 修改用户密码
         **/
        public static final String URL_MODIFY_PASSWORD () {return URL_ACCOUNT_BASE + "/account/modifypwd";}
        /**
         * 发送修改手机邮箱验证码
         **/
        public static final String URL_SEND_MODIFY_VCODE () {return URL_ACCOUNT_BASE + "/account/modifycode";}
        /**
         * 修改手机邮箱
         **/
        public static final String URL_MODIFY_PHONE_OR_EMAIL () {return URL_ACCOUNT_BASE + "/account/modifyinfo";}
        /**
         * 发送找回密码验证码
         **/
        public static final String URL_SEND_RETRIEVE_PWD_VCODE () {return URL_ACCOUNT_BASE + "/account/retrivecode";}
        /**
         * 重置密码
         **/
        public static final String URL_RETRIEVE_PWD () {return URL_ACCOUNT_BASE + "/account/retrivepwd";}
        /**
         * 检查用户名密码是否正确
         **/
        public static final String URL_CHECK_USER_PWD () {return URL_ACCOUNT_BASE + "/account/checkinfo";}
        /**
         * 获取用户手机和邮箱
         **/
        public static final String URL_GET_PHONE_AND_EMAIL () {return URL_ACCOUNT_BASE + "/account/getuserinfo";}
        /**
         * 获取第三方授权URL
         */
        public static final String URL_THIRD_AUTH() {return URL_ACCOUNT_BASE + "/account/thirdauth";}

        /**
         * Get Oauth login URL
         */
        public static final String URL_OAUTH_LOGIN() {return  URL_ACCOUNT_BASE + "/account/thirdoauth/login";}
    }

    public static final class Pyramid {
        public static String urlParams = "?source=app&datatype=app_user_v1";

        public static void setUrlBase(String urlBase){
            URL_PYRAMID_BASE = urlBase;
        }

        public static void setUrlParams(String params) {
            urlParams = params;
        }

        public static final String URL_UPLOAD_CONFIG() {
            return URL_PYRAMID_BASE + "/data/v1/appdata/configure" + urlParams;
        }

        public static final String URL_UPLOAD_DATA() {
            return URL_PYRAMID_BASE + "/data/v1/appdata/upload" + urlParams;
        }
    }

    public static final class Proxy  {

        public static void setUrlBase(String urlBase){
            URL_PROXY_BASE = urlBase;
        }

        /**
         * 获取DNA Proxy 认证授权URL
         **/
        public static final String URL_AUTH_DNA_PROXY () {return URL_PROXY_BASE + "/dnaproxy/v1/app/auth";}
        /**
         * 获取取消DNA Proxy 认证授权URL
         */
        public static final String URL_DISAUTH_DNA_PROXY () {return URL_PROXY_BASE + "/dnaproxy/v1/app/disauth";}
    }

    public static final class APPManager{
        public static final String QUERY_PRODUCT_DIRECTORY() {return APP_MANAGE_BASE + "/ec4/v1/system/language/category/list";}

        public static final String QUERY_BRADN_LIST() {return APP_MANAGE_BASE + "/ec4/v1/system/language/productbrand/list";}

        public static final String QUERY_PRODUCT_LIST_BY_BRADN() {return APP_MANAGE_BASE + "/ec4/v1/system/language/productbrandfilter/list";}

        public static final String URL_VERSION_QUERY() {return APP_MANAGE_BASE + "/ec4/v1/system/getresourceversionbypid";}

        public static final String URL_DOWNLOAD()  { return APP_MANAGE_BASE + "/ec4/v1/system/downloadproductresource";}
    }

    public static final class KitResource {
        public static final String URL_IRCOCE_BASE_() { return  URL_IRCODE_BASE;}

        public static final String URL_IRCODE_QUERY_TYPE() { return  URL_IRCODE_BASE + "/publicircode/v2/app/getdevtype";}

        public static final String URL_IRCODE_QUERY_BRAND() { return  URL_IRCODE_BASE + "/publicircode/v2/app/getbrand";}

        public static final String URL_IRCODE_QUERY_STB_BRAND() { return  URL_IRCODE_BASE + "/publicircode/v2/stb/getproviderinfobylocatename";}

        public static final String URL_IRCODE_QUERY_VERSION() { return  URL_IRCODE_BASE + "/publicircode/v2/app/getversion";}

        public static final String URL_IRCODE_SCRIPT_GET_URL() { return  URL_IRCODE_BASE + "/publicircode/v2/app/geturlbybrandversion";}

        public static final String CLOUD_BRAND_CLASS_LIST() { return  URL_IRCODE_BASE + "/publicircode/v2/app/geturlbybrandversion?mtag=app";}

        public static final String URL_IRCODE_RECOGNIZE() { return  URL_IRCODE_BASE + "/publicircode/v2/cloudac/recognizeirdata";}

        public static final String URL_IRCODE_SUBAREAGET() { return  URL_IRCODE_BASE + "/publicircode/v2/app/getsubarea";}

        public static final String URL_IRCODE_AREAINFOGET() { return  URL_IRCODE_BASE + "/publicircode/v2/app/getareainfobyid";}

        public static final String URL_IRCODE_STBGet() { return  URL_IRCODE_BASE + "/publicircode/v2/stb/getprovider";}

        public static final String URL_IRCODE_STB_SCRIPT_GET_URL() { return  URL_IRCODE_BASE + "/publicircode/v2/stb/geturlbyarea";}

        public static final String CLOUD_STB_CHANNEL_LIST() { return  URL_IRCODE_BASE + "/publicircode/v2/stb/getchannel";}
    }

    public static final class Family {
        public static void setUrlBase(String urlBase){
            URL_FAMILY_BASE = urlBase;
        }
        /** 根据传入path返回URL**/
        public static String getFamilyCommonUrl(String path) {
            return URL_FAMILY_BASE + path;
        }

        /** 创建默认家庭 自带创建房间*/
        public static final String URL_CREATE_DELFAULT_FAMILY () {return URL_FAMILY_BASE + "/ec4/v1/family/default";}
        /** 查询家庭基本信息列表*/
        public static final String URL_QUERY_BASE_FAMILYINFO_LIST() {return URL_FAMILY_BASE + "/ec4/v1/user/getbasefamilylist";}
        /** 查询家庭信息*/
        public static final String URL_GET_FAMILY_DETAIL_INFO() {return URL_FAMILY_BASE + "/ec4/v1/family/getfamilyversioninfo";}
        /** 删除设备*/
        public static final String URL_DELETE() {return URL_FAMILY_BASE + "/ec4/v1/family/getfamilyversioninfo";}
        /** 修改模块基本信息*/
        public static final String URL_UPDATE_MODULE_BASEINFO() {return URL_FAMILY_BASE +  "/ec4/v1/module/modifybasicinfo";}
        /** 批量删除模块*/
        public static final String URL_DELETE_MODULE_LIST() {return URL_FAMILY_BASE + "/ec4/v1/module/dellist";}
        /** 获取家庭数据接口加密KEY*/
        public static final String URL_KEY_ADN_TIMESTRATRAMP () {return URL_FAMILY_BASE + "/ec4/v1/common/api";}
        /** 获取家庭ID列表*/
        public static final String URL_FAMILY_ID_LIST () {return URL_FAMILY_BASE + "/ec4/v1/user/getfamilyid";}
        /** 获取家庭基本信息列表*/
        public static final String URL_FAMILY_BASE_INFO_LIST () {return URL_FAMILY_BASE + "/ec4/v1/user/getbasefamilylist";}
        /** 获取家庭版本Y*/
        public static final String URL_FAMILY_VERSION () {return URL_FAMILY_BASE + "/ec4/v1/family/getversion";}
        /** 获取家庭所有信息*/
        public static final String URL_FAMILY_ALL_INFO () {return URL_FAMILY_BASE + "/ec4/v1/family/getallinfo";}
        /** 添加家庭*/
        public static final String URL_FAMILY_ADD () {return URL_FAMILY_BASE + "/ec4/v1/family/add";}
        /** 修改家庭*/
        public static final String URL_FAMILY_MODIFY () {return URL_FAMILY_BASE + "/ec4/v1/family/modifyinfo";}
        /** 修改家庭icon**/
        public static final String URL_FAMILY_MODIFY_ICON () {return URL_FAMILY_BASE + "/ec4/v1/family/modifyicon";}
        /** 删除家庭*/
        public static final String URL_FAMILY_DEL () {return URL_FAMILY_BASE + "/ec4/v1/family/del";}
        /** 获取家庭子目录*/
        public static final String URL_FAMILY_CHILD_DIR () {return URL_FAMILY_BASE + "/ec4/v1/system/getchilddir";}
        /** 获取家庭场景详情*/
        public static final String URL_FAMILY_SCENCE_DETAIL () {return URL_FAMILY_BASE + "/ec4/v1/system/scenedetail";}
        /** 获取家庭云端预定义房间*/
        public static final String URL_FAMILY_ROOM_DEFINE () {return URL_FAMILY_BASE + "/ec4/v1/system/defineroom";}
        /** 获取家庭房间列表*/
        public static final String URL_FAMILY_ROOM_LIST_MANAGE () {return URL_FAMILY_BASE + "/ec4/v1/room/manage";}
        /** 获取家庭成员邀请二维码请求*/
        public static final String URL_FAMILY_MEMBER_INVITE_QRCODE_REQUEST () {return URL_FAMILY_BASE + "/ec4/v1/member/invited/reqqrcode";}
        /** 获取家庭成员邀请二维码扫描信息*/
        public static final String URL_FAMILY_MEMBER_INVITE_QRCODE_SCAN_INFO () {return URL_FAMILY_BASE + "/ec4/v1/member/invited/scanqrcode";}
        /** 家庭成员邀请二维码加入家庭*/
        public static final String URL_FAMILY_MEMBER_INVITE_QRCODE_JOIN () {return URL_FAMILY_BASE + "/ec4/v1/member/invited/joinfamily";}
        /** 加入家庭*/
        public static final String URL_FAMILY_MEMBER_JOIN () {return URL_FAMILY_BASE + "/ec4/v1/member/joinpublicfamily";}
        /** 退出家庭*/
        public static final String URL_FAMILY_MEMBER_QUIT () {return URL_FAMILY_BASE + "/ec4/v1/member/quitfamily";}
        /** 删除家庭成员*/
        public static final String URL_FAMILY_MEMBER_DEL () {return URL_FAMILY_BASE + "/ec4/v1/member/delfamilymember";}
        /** 获取家庭成员信息*/
        public static final String URL_FAMILY_MEMBER_INFO_GET () {return URL_FAMILY_BASE + "/ec4/v1/member/getfamilymember";}
        /** 获取家庭已经配置的设备类别**/
        public static final String URL_FAMILY_DEVICE_CONFIGED_LIST () {return URL_FAMILY_BASE + "/ec4/v1/dev/getconfigdev";}
        /** 删除家庭设备**/
        public static final String URL_FAMILY_DEVICE_DEL () {return URL_FAMILY_BASE + "/ec4/v1/dev/deldev";}
        /** 家庭模块单个添加*/
        public static final String URL_FAMILY_MODULE_ADD () {return URL_FAMILY_BASE + "/ec4/v1/module/add";}
        /** 家庭模块列表添加*/
        public static final String URL_FAMILY_MODULE_ADD_LIST () {return URL_FAMILY_BASE + "/ec4/v1/module/addlist";}
        /** 家庭模块删除*/
        public static final String URL_FAMILY_MODULE_DEL () {return URL_FAMILY_BASE + "/ec4/v1/module/del";}
        /** 家庭模块修改*/
        public static final String URL_FAMILY_MODULE_MODIFY () {return URL_FAMILY_BASE + "/ec4/v1/module/modify";}
        /** 家庭模块FLAG修改*/
        public static final String URL_FAMILY_MODULE_MODIFY_FLAG () {return URL_FAMILY_BASE + "/ec4/v1/module/modifyflag";}
        /** 家庭模块房间移动*/
        public static final String URL_FAMILY_MODULE_MODIFY_ROOM () {return URL_FAMILY_BASE + "/ec4/v1/module/movemodule";}
        /** 家庭模块信息和房间修改*/
        public static final String URL_FAMILY_MODULE_MODIFY_INFO_AND_ROOM () {return URL_FAMILY_BASE + "/ec4/v1/module/modifyandmovemodule";}
        /** 家庭峰谷电配置*/
        public static final String URL_FAMILY_ELECTRIC_INFO_CONFIG () {return URL_FAMILY_BASE + "/ec4/v1/electricinfo/config";}
        /** 家庭峰谷电查询*/
        public static final String URL_FAMILY_ELECTRIC_INFO_QUERY () {return URL_FAMILY_BASE + "/ec4/v1/electricinfo/query";}

        /** 获取家庭私有数据唯一ID 用于虚拟子设备Did**/
        public static final String URL_FAMILY_PRIVATE_DATA_ID () {
            return URL_FAMILY_PRIVATE_BASE + "/ec4/v1/family/privatedata/getid";
        }

        /** 获取家庭私有数据更新 **/
        public static final String URL_FAMILY_PRIVATE_DATA_UPDATE () {
            return URL_FAMILY_PRIVATE_BASE + "/ec4/v1/family/privatedata/update";
        }

        /** 获取家庭私有数据删除 **/
        public static final String URL_FAMILY_PRIVATE_DATA_DELETE () {
            return URL_FAMILY_PRIVATE_BASE + "/ec4/v1/family/privatedata/del";
        }

        /** 获取家庭私有数据查询 **/
        public static final String URL_FAMILY_PRIVATE_DATA_QUERY () {
            return URL_FAMILY_PRIVATE_BASE + "/ec4/v1/family/privatedata/query";
        }

        /** 获取家庭私有数据所有数据查询 **/
        public static final String URL_FAMILY_PRIVATE_DATA_QUERY_ALL () {
            return URL_FAMILY_PRIVATE_BASE + "/ec4/v1/family/privatedata/queryall";
        }
    }

    public static final class Oauth {
        public static void setUrlBase(String urlBase){
            URL_OAUTH_BASE = urlBase;
        }

        /**
         *  OAUTH 登录web页面
         **/
        public static final String URL_OAUTH_LOGIN_WEB () {return URL_OAUTH_BASE;}

        public static final String URL_OAUTH_LOGIN_DATA () {return URL_OAUTH_BASE + "/oauth/v2/server/getlogindata";}

        public static final String URL_OAUTH_LOGIN_INFO () {
            return URL_OAUTH_BASE + "/oauth/v2/login/info";
        }
    }

    public static final class DeviceData {
        public static void setUrlBase(String urlBase){
            URL_DEVICE_DATA_BASE = urlBase;
        }

        public static final String URL_DEVICE_DATA_QUERY (String pid) {
            return String.format("https://%s%s/dataservice/v1/device/stats", pid, URL_DEVICE_DATA_BASE);
        }
    }
}
