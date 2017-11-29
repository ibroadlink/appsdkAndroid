package cn.com.broadlink.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.networkapi.NetworkCallback;
import cn.com.broadlink.sdk.data.controller.BLCycleInfo;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLGetAPListResult;
import cn.com.broadlink.sdk.data.controller.BLPeriodInfo;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.data.controller.BLTimerInfo;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceScanListener;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceStateChangedListener;
import cn.com.broadlink.sdk.param.account.BLGetUserInfoParam;
import cn.com.broadlink.sdk.param.account.BLLoginParam;
import cn.com.broadlink.sdk.param.account.BLModifyPasswordParam;
import cn.com.broadlink.sdk.param.account.BLModifyPhoneOrEmailParam;
import cn.com.broadlink.sdk.param.account.BLModifyUserNicknameParam;
import cn.com.broadlink.sdk.param.account.BLRegistParam;
import cn.com.broadlink.sdk.param.account.BLRetrievePasswordParam;
import cn.com.broadlink.sdk.param.account.BLSendVCodeParam;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;
import cn.com.broadlink.sdk.param.controller.BLQueryIRCodeParams;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.param.family.BLFamilyDeviceInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyElectricityInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyModuleInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyRoomInfo;
import cn.com.broadlink.sdk.param.family.BLPrivateData;
import cn.com.broadlink.sdk.result.BLControllerDNAControlResult;
import cn.com.broadlink.sdk.result.account.BLBaseResult;
import cn.com.broadlink.sdk.result.account.BLGetUserInfoResult;
import cn.com.broadlink.sdk.result.account.BLGetUserPhoneAndEmailResult;
import cn.com.broadlink.sdk.result.account.BLLoginResult;
import cn.com.broadlink.sdk.result.account.BLModifyUserIconResult;
import cn.com.broadlink.sdk.result.account.BLOauthResult;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;
import cn.com.broadlink.sdk.result.controller.BLBindDeviceResult;
import cn.com.broadlink.sdk.result.controller.BLDeviceConfigResult;
import cn.com.broadlink.sdk.result.controller.BLDeviceTimeResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;
import cn.com.broadlink.sdk.result.controller.BLFirmwareVersionResult;
import cn.com.broadlink.sdk.result.controller.BLPairResult;
import cn.com.broadlink.sdk.result.controller.BLPassthroughResult;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLQueryResoureVersionResult;
import cn.com.broadlink.sdk.result.controller.BLQueryTaskResult;
import cn.com.broadlink.sdk.result.controller.BLIRCodeDataResult;
import cn.com.broadlink.sdk.result.controller.BLIRCodeInfoResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevAddResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;
import cn.com.broadlink.sdk.result.controller.BLTaskDataResult;
import cn.com.broadlink.sdk.result.controller.BLUpdateDeviceResult;
import cn.com.broadlink.sdk.result.family.BLAllFamilyInfoResult;
import cn.com.broadlink.sdk.result.family.BLDefineRoomTypeResult;
import cn.com.broadlink.sdk.result.family.BLFamilyBaseInfoListResult;
import cn.com.broadlink.sdk.result.family.BLFamilyConfigedDevicesResult;
import cn.com.broadlink.sdk.result.family.BLFamilyElectricityInfoResult;
import cn.com.broadlink.sdk.result.family.BLFamilyIdListGetResult;
import cn.com.broadlink.sdk.result.family.BLFamilyInfoResult;
import cn.com.broadlink.sdk.result.family.BLFamilyInviteQrcodeGetResult;
import cn.com.broadlink.sdk.result.family.BLFamilyInvitedQrcodePostResult;
import cn.com.broadlink.sdk.result.family.BLFamilyMemberInfoGetResult;
import cn.com.broadlink.sdk.result.family.BLManageRoomResult;
import cn.com.broadlink.sdk.result.family.BLModuleControlResult;
import cn.com.broadlink.sdk.result.family.BLPrivateDataIdResult;
import cn.com.broadlink.sdk.result.family.BLPrivateDataResult;

/**
 * APPSDK Core Class.
 * ALL sdk users should apply for license from BroadLink Co., Ltd.
 * License is associated with the package name. Different application should apply for different License.
 *
 */
public final class BLLet {
    // SDK Version
    private static final String SDK_VERSION = BuildConfig.VERSION_NAME;

    private static boolean mDebugLog = false;

    /** Account Instance object **/
    private static BLAccountImpl mAccountImpl;
    /** Data picker Instance object **/
    private static BLPyramidImpl mPyramidImpl;
    /** Device Instance object **/
    private static BLControllerImpl mControllerImpl;
    /** Family Instance object **/
    private static BLFamilyImpl mFamilyImpl;
    /** OAuth Instance object**/
    private static BLOAuthImpl mOAuthImpl;
    /** IRCode Instance object**/
    private static BLIRCodeImpl mIRCodeImpl;

    // 私有化构造函数，禁止用户生成实例
    private BLLet() {
    }

    /**
     * Init APPSDK with app context.
     * License and channel are setted in AndroidManifest.xml by meta mode.
     *
     * @param context APP Context
     */
    public static void init(Context context){
        init(context, null);
    }

    /**
     * Init APPSDK with app context and config params.
     * License and channel are setted in AndroidManifest.xml by meta mode.
     *
     * @param context APP Context
     * @param configParam Config params
     */
    public static void init(Context context, BLConfigParam configParam){
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData == null) {
                throw new NullPointerException("Cannot found necessary meta data");
            }
            String license = appInfo.metaData.getString(BLConstants.LET_LICENSE);
            if (license == null) {
                throw new NullPointerException("Cannot found meta '" + BLConstants.LET_LICENSE + "'");
            }
            String channel = appInfo.metaData.getString(BLConstants.LET_CHANNEL);
            if (channel == null) {
                throw new NullPointerException("Cannot found meta '" + BLConstants.LET_CHANNEL + "'");
            }
            init(context, license, channel, configParam);
        } catch (PackageManager.NameNotFoundException e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * Init APPSDK without AndroidManifest.xml
     *
     * @param context APP Context
     * @param license License from BroadLink Co., Ltd.
     * @param channel APP publish channel
     */
    public static void init(Context context, String license, String channel){
        init(context, license, channel, null);
    }

    /**
     * Init APPSDK without AndroidManifest.xml
     *
     * @param context APP Context
     * @param license License from BroadLink Co., Ltd.
     * @param channel APP publish channel
     * @param configParam Config params
     */
    public static void init(Context context, String license, String channel, BLConfigParam configParam){

        BLCommonTools.debug(SDK_VERSION);

        // 减少后续对null的判断
        if(configParam == null){
            configParam = new BLConfigParam();
        }

        /** 初始化文件夹 **/
        BLFileStorageUtils.initFilePath(context, configParam.get(BLConfigParam.SDK_FILE_PATH));

        /** 初始化控制模块 **/
        if(mControllerImpl == null){
            mControllerImpl = new BLControllerImpl();
        }
        mControllerImpl.init(context, license, configParam);

        /** 获取 uid & lid , 用于后续模块初始化 **/
        String licenseInfo = mControllerImpl.getLicenseInfo();
        String uid = "";
        String lid = "";
        try {
            JSONObject jInfo = new JSONObject(licenseInfo);

            uid = jInfo.optString("uid", null);
            lid = jInfo.optString("lid", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** 初始化各个域名*/
        BLApiUrls.setLicenseId(lid);

        /** 初始化红外码服务 */
        if (mIRCodeImpl == null) {
            mIRCodeImpl = new BLIRCodeImpl();
        }
        mIRCodeImpl.init(context, lid, configParam);

        /** 初始化家庭模块 **/
        if (mFamilyImpl == null) {
            mFamilyImpl = new BLFamilyImpl();
        }
        mFamilyImpl.init(lid, configParam);

        /** 初始化账户模块 **/
        if(mAccountImpl == null){
            mAccountImpl = new BLAccountImpl();
        }
        mAccountImpl.init(license, uid, lid, configParam);
        /** 将控制模块注册给账户系统 **/
        mAccountImpl.addLoginListener(mControllerImpl);
        mAccountImpl.addLoginListener(mFamilyImpl);
        mAccountImpl.addLoginListener(mIRCodeImpl);

        /** 初始化数据采集模块 **/
        if(mPyramidImpl == null){
            mPyramidImpl = new BLPyramidImpl();
        }
        mPyramidImpl.init(context, license, channel, lid, configParam);

        /** 初始化Oauth认证模块 **/
        if (mOAuthImpl == null) {
            mOAuthImpl = new BLOAuthImpl();
        }
        mOAuthImpl.init(context, configParam);
    }

    /**
     * Get LicenseId
     * @return LicenseId
     */
    public static String getLicenseId(){
        if(mControllerImpl == null) return null;
        /** 获取 uid & lid , 用于后续模块初始化 **/
        String licenseInfo = mControllerImpl.getLicenseInfo();
        String lid = "";
        try {
            JSONObject jInfo = new JSONObject(licenseInfo);
            lid = jInfo.optString("lid", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lid;
    }

    /**
     * Get CompanyId
     * @return CompanyId
     */
    public static String getCompanyid(){
        /** 获取 uid & lid , 用于后续模块初始化 **/
        String licenseInfo = mControllerImpl.getLicenseInfo();
        String uid = "";
        try {
            JSONObject jInfo = new JSONObject(licenseInfo);
            uid = jInfo.optString("uid", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uid;
    }

    /**
     * Get APP SDK Version
     * @return SDK version
     */
    public static String getSDKVersion(){
        return SDK_VERSION;
    }

    /**
     * Call this method when app finish to recycling resources
     */
    public static void finish(){
        mAccountImpl = null;

        mPyramidImpl.finish();
        mPyramidImpl = null;
		
        if(mControllerImpl != null){
            mControllerImpl.finish();
            mControllerImpl = null;
        }
    }

    /**
     * Debug log class
     */
    public static final class DebugLog {
        /**
         * Open debug log
         */
        public static void on() {
            mDebugLog = true;
        }

        /**
         * Close debug log
         */
        public static void off() {
            mDebugLog = false;
        }

        /**
         * Charge debug is on/off, used in package.
         * @return debug on / off
         */
        protected static boolean islog() {
            return mDebugLog;
        }
    }

    /**
     * Accnout class
     */
    public static final class Account {

        /**
         * Login with username and password
         *
         * @param username username
         * @param password password
         * @return login result
         */
        public static BLLoginResult login(String username, String password) {
            return mAccountImpl.login(new BLLoginParam(username, password));
        }

        /**
         * Local login with local stored login result
         * @param loginResult last stored login result
         * @return login result
         */
        public static BLLoginResult localLogin(BLLoginResult loginResult){
            return mAccountImpl.localLogin(loginResult);
        }

        /**
         * Login with Verification code
         * @param phoneOrEmail Verification code send to phone or email
         * @param countrycode Phone country code. if Verification code send to email, this can be null. (eg. 0086)
         * @param vcode Verification code
         * @return login result
         */
        public static  BLLoginResult fastLogin(String phoneOrEmail, String countrycode, String vcode) {
            return mAccountImpl.fastLogin(phoneOrEmail, countrycode, vcode);
        }

        /**
         * Login With third party id
         * @param thirdID Uniquely id in third party
         * @return login result
         */
        public static BLLoginResult thirdAuth(String thirdID) {
            return mAccountImpl.thirdAuth(thirdID);
        }

        /**
         * Query accessToken from ihc
         * @param username  username
         * @param password password
         * @param cliendId cliendId
         * @param redirectUri redirectUri
         * @return query result
         */
        public static BLOauthResult queryIhcAccessToken(String username, String password, String cliendId, String redirectUri) {
            return mAccountImpl.queryIhcAccessToken(username, password, cliendId, redirectUri);
        }

        /**
         * Login by ihc Oauth
         * @param accessToken Token by Oauth
         * @return login result
         */
        public static BLLoginResult oauthByIhc(String accessToken) {
            return mAccountImpl.loginWithIhc(accessToken);
        }

        /**
         * Login by third app Oauth
         * @param thirdType Third company name - like "facebook"
         * @param thirdID Open id by Oauth
         * @param accesstoken Token by Oauth
         * @param topsign topsign by Oauth, can be null
         * @param nickname User nickname, can be null
         * @param iconUrl User icon url, can be null
         * @return login result
         */
        public static BLLoginResult oauthLogin(String thirdType, String thirdID, String accesstoken, String topsign, String nickname, String iconUrl) {
            return  mAccountImpl.oauthLogin(thirdType, thirdID, accesstoken, topsign, nickname, iconUrl);
        }

        /**
         * Send verification code to phone or email for fastLogin
         *
         * @param phoneOrEmail Phone or email
         * @param countrycode Phone country code. if Verification code send to email, this can be null. (eg. 0086)
         * @return send result
         */
        public static BLBaseResult sendFastLoginVCode(String phoneOrEmail, String countrycode) {
            return mAccountImpl.sendFastVCode(new BLSendVCodeParam(phoneOrEmail, countrycode));
        }

        /**
         * Send verification code to phone for register
         *
         * @param phone Phone
         * @param countrycode Phone country code. (eg. 0086)
         * @return send result
         */
        public static BLBaseResult sendRegVCode(String phone, String countrycode) {
            return mAccountImpl.sendRegVCode(new BLSendVCodeParam(phone, countrycode));
        }

        /**
         * Send verification code to email for register
         *
         * @param email email
         * @return send result
         */
        public static BLBaseResult sendRegVCode(String email) {
            return mAccountImpl.sendRegVCode(new BLSendVCodeParam(email));
        }

        /**
         * Register Account by phone or email
         * @param registParam regist param include user info
         * @param fileIcon  User icon path, this can be null.
         * @return register result
         */
        public static BLLoginResult regist(BLRegistParam registParam, File fileIcon) {
            return mAccountImpl.regist(registParam, fileIcon);
        }

        /**
         * Modify user gender and birthday
         * @param gender "male" or "female"
         * @param birthday New user birthday
         * @return modify result
         */
        public static BLBaseResult modifyUserGenderBirthday(String gender, String birthday) {
            return mAccountImpl.modifyUserGenderBirthday(gender, birthday);
        }

        /**
         * Modify user icon
         *
         * @param picFile New user icon file
         * @return modify result
         */
        public static BLModifyUserIconResult modifyUserIcon(File picFile) {
            return mAccountImpl.modifyUserIcon(null, picFile);
        }

        /**
         * Modify user nick name
         * @param nickName New nickname
         * @return modify result
         */
        public static BLBaseResult modifyUserNickname(String nickName) {
            return mAccountImpl.modifyUserNickname(new BLModifyUserNicknameParam(nickName));
        }

        /**
         * Query user info list
         * @param useridList User ids
         * @return query result
         */
        public static BLGetUserInfoResult getUserInfo(List<String> useridList) {
            return mAccountImpl.getUserInfo(new BLGetUserInfoParam(useridList));
        }

        /**
         * Modify user password
         * @param oldPassword Old password
         * @param newPassword New password
         * @return modify result
         */
        public static BLBaseResult modifyPassword(String oldPassword, String newPassword) {
            return mAccountImpl.modifyPassword(new BLModifyPasswordParam(oldPassword, newPassword));
        }

        /**
         * Set password to fastlogin user
         * @param phoneOrEmail Phone or email
         * @param countrycode Phone country code. if Verification code send to email, this can be nil. (eg. 0086)
         * @param vcode Verification code
         * @param password Password
         * @return set result
         */
        public static BLBaseResult setFastLoginPassword(String phoneOrEmail, String countrycode, String vcode, String password) {
            return mAccountImpl.setFastLoginPassword(new BLModifyPhoneOrEmailParam(phoneOrEmail, countrycode, vcode, password));
        }

        /**
         * Send verification code to phone/email for set fastlogin user's password
         * @param phoneOrEmail  Phone or email
         * @param countrycode Phone country code. if Verification code send to email, this can be nil. (eg. 0086)
         * @return send result
         */
        public static BLBaseResult sendFastLoginPasswordVCode(String phoneOrEmail, String countrycode) {
            return mAccountImpl.sendFastLoginPasswordVCode(new BLSendVCodeParam(phoneOrEmail, countrycode));
        }

        /**
         * Send verification code to phone for modify password
         *
         * @param phone Phone
         * @param countrycode Phone country code. (eg. 0086)
         * @return send result
         */
        public static BLBaseResult sendModifyVCode(String phone, String countrycode) {
            return mAccountImpl.sendModifyVCode(new BLSendVCodeParam(phone, countrycode));
        }

        /**
         * Send verification code to email for modify password
         *
         * @param email Email
         * @return send result
         */
        public static BLBaseResult sendModifyVCode(String email) {
            return mAccountImpl.sendModifyVCode(new BLSendVCodeParam(email));
        }

        /**
         * Modify user register phone
         *
         * @param phone Phone
         * @param countrycode Phone country code.  (eg. 0086)
         * @param vcode Verification code
         * @param password Password
         * @return modify result
         */
        public static BLBaseResult modifyPhone(String phone, String countrycode, String vcode, String password) {
            return mAccountImpl.modifyPhoneOrEmail(new BLModifyPhoneOrEmailParam(phone, countrycode, vcode, password));
        }

        /**
         * Modify user register email
         *
         * @param email Email
         * @param vcode Verification code
         * @param password Password
         * @return modify result
         */
        public static BLBaseResult modifyEmail(String email, String vcode, String password) {
            return mAccountImpl.modifyPhoneOrEmail(new BLModifyPhoneOrEmailParam(email, vcode, password));
        }

        /**
         * Send verification code to phone/email for reset password
         *
         * @param phoneOrEmail Phone or email
         * @return send result
         */
        public static BLBaseResult sendRetrieveVCode(String phoneOrEmail) {
            return mAccountImpl.sendRetrieveVCode(new BLSendVCodeParam(phoneOrEmail));
        }

        /**
         * Reset password
         *
         * @param phoneOrEmail Phone or email
         * @param vcode Verification code
         * @param newPassword New password
         * @return reset password result
         */
        public static BLLoginResult retrievePassword(String phoneOrEmail, String vcode, String newPassword) {
            return mAccountImpl.retrievePassword(new BLRetrievePasswordParam(phoneOrEmail, vcode, newPassword));
        }

        /**
         * Check password is right or not
         *
         * @param password Password
         * @return check password result
         */
        public static BLBaseResult checkUserPassword(String password) {
            return mAccountImpl.checkUserPassword(password);
        }

        /**
         * Query user phone and email
         *
         * @return query result
         */
        public static BLGetUserPhoneAndEmailResult getUserPhoneAndEmail() {
            return mAccountImpl.getUserPhoneAndEmail();
        }
    }

    /**
     * OAuth class
     */
    public static final class OAuth {
        /**
         * Start oauth
         * @param client_id
         * @param redirectURI
         * @return
         */
        public static Boolean authorize(String client_id, String redirectURI) {
            return mOAuthImpl.authorize(client_id, redirectURI);
        }

        /**
         * Handle intent to get Oauth access token
         * @param intent
         * @return
         */
        public static BLOauthResult handleOpenURL(Intent intent) {
            return mOAuthImpl.handleOpenURL(intent);
        }
    }

    /**
     * Data picker class
     */
    public static final class Picker {

        /**
         * 开启数据统计上报模块
         */
        public static void startPick() {
            mPyramidImpl.startPick();
        }

        /**
         * 跟踪页面使用情况方法，在Activity的onCreate方法中调用
         *
         * @param activity 跟踪页面Activity
         */
        public static void onCreate(Activity activity) {
            mPyramidImpl.onCreate(activity);
        }

        /**
         * 跟踪页面使用情况方法，在Activity的onDestroy方法中调用
         *
         * @param activity 跟踪页面Activity
         */
        public static void onDestroy(Activity activity) {
            mPyramidImpl.onDestroy(activity);
        }

        /**
         * 跟踪页面使用情况方法，在Activity的onResume方法中调用
         *
         * @param activity 跟踪页面Activity
         */
        public static void onResume(Activity activity) {
            mPyramidImpl.onResume(activity);
        }

        /**
         * 跟踪页面使用情况方法，在Activity的onPause方法中调用
         *
         * @param activity 跟踪页面Activity
         */
        public static void onPause(Activity activity) {
            mPyramidImpl.onPause(activity);
        }

        /**
         * 需要追踪的事件
         *
         * @param eventId 事件ID
         */
        public static void onEvent(String eventId) {
            mPyramidImpl.onEvent(eventId, "", null);
        }

        /**
         * 需要追踪的事件，事件内不需要加入额外数据
         *
         * @param eventId 事件ID
         * @param eventTag 事件Tag
         */
        public static void onEvent(String eventId, String eventTag) {
            mPyramidImpl.onEvent(eventId, eventTag, null);
        }

        /**
         * 需要追踪的事件，如果事件内需要加入数据，可调用此方法
         *
         * @param eventId 事件ID
         * @param eventTag 事件Tag
         * @param data 加入数据
         */
        public static void onEvent(String eventId, String eventTag, Map<String, Object> data) {
            mPyramidImpl.onEvent(eventId, eventTag, data);
        }
    }

    /**
     * Device control class
     */
    public static final class Controller{
        /**
         * Device Scan Listener
         * @param scanListener Listener
         */
        public static void setOnDeviceScanListener(BLDeviceScanListener scanListener){
            mControllerImpl.setDeviceScanListener(scanListener);
        }

        /**
         * Device State changed Listener
         *
         * @param deviceStateChangedListener Listener
         */
        public static void setOnDeviceStateChangedListener(BLDeviceStateChangedListener deviceStateChangedListener){
            mControllerImpl.setOnDeviceStateChangedListener(deviceStateChangedListener);
        }

        /**
         * Network callback used for private data
         * @param networkCallback
         */
        public static void setOnNetworkCallback(NetworkCallback networkCallback) {
            mControllerImpl.setOnNetworkCallback(networkCallback);
        }

        /**
         * Start probe devices in lan. Default one probe is 3000ms.
         */
        public static void startProbe(){
            mControllerImpl.startProbe();
        }

        /**
         * Stop probe devices in lan.
         */
        public static void stopProbe(){
            if(mControllerImpl != null){
                mControllerImpl.stopProbe();
            }
        }

        /**
         * Finsh device control
         */
        public static void finish(){
            mControllerImpl.finish();
        }

        /**
         *  Config device to phone's WiFi.
         *  This interface is Obstructed.
         * @param deviceConfigParam WiFi config param
         * @param timeout Config timeout - Unit : second
         * @return Config result
         */
        public static BLDeviceConfigResult deviceConfig(BLDeviceConfigParam deviceConfigParam, int timeout){
            return mControllerImpl.deviceConfig(deviceConfigParam, timeout);
        }

        /**
         * Config device to phone's WiFi.
         * This interface is Obstructed. Use default timeout(75s).
         *
         * @param deviceConfigParam WiFi config param
         * @return Config result
         */
        public static BLDeviceConfigResult deviceConfig(BLDeviceConfigParam deviceConfigParam){
            return mControllerImpl.deviceConfig(deviceConfigParam, -1);
        }

        /**
         * Cancel config device.
         * @return Cancel result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult deviceConfigCancel(){
            return mControllerImpl.deviceConfigCancel();
        }

        /**
         * Pair device to get control id and key.
         * These id and key are used to control deivce in remote mode.
         *
         * @param device Device info
         * @param configParam config param
         * @return Pair result - Include control id and key.
         */
        public static BLPairResult pair(BLDNADevice device, BLConfigParam configParam){
            return mControllerImpl.pair(new BLProbeDevice(device), configParam);
        }

        /**
         * Pair device to get control id and key.
         * These id and key are used to control deivce in remote mode.
         *
         * @param device Device info
         * @return Pair result - Include control id and key.
         */
        public static BLPairResult pair(BLDNADevice device){
            return mControllerImpl.pair(new BLProbeDevice(device), null);
        }

        /**
         * Add device list to sdk.
         * Device must be added to sdk, if you want to control this deive.
         *
         * @param listDevice Device info List
         */
        public static void addDevice(ArrayList<BLDNADevice> listDevice){
            mControllerImpl.addDevice(listDevice);
        }

        /**
         * Add a device to sdk.
         * Device must be added to sdk, if you want to control this deive.
         *
         * @param device Device info
         */
        public static void addDevice(BLDNADevice device){
            ArrayList<BLDNADevice> listDevice = new ArrayList<>(1);
            listDevice.add(device);
            mControllerImpl.addDevice(listDevice);
        }

        /**
         * Remove device list from sdk.
         *
         * @param listDevice Device did List
         */
        public static void removeDevice(ArrayList<String> listDevice){
            mControllerImpl.removeDevice(listDevice);
        }

        /**
         * Remove a device from sdk.
         *
         * @param did Device did
         */
        public static void removeDevice(String did){
            ArrayList<String> listDevice = new ArrayList<>(1);
            listDevice.add(did);
            mControllerImpl.removeDevice(listDevice);
        }

        /**
         * Remove all device infos from sdk.
         */
        public static void removeAllDevice(){
            mControllerImpl.removeAllDevice();
        }

        /**
         * Query device network state.
         * @param did Device did
         * @return Query result
         */
        public static int queryDeviceState(String did){
            return mControllerImpl.queryDeviceState(did);
        }

        /**
         * Query device remote network state.
         * @param did Device did
         * @return Query result
         */
        public static int queryDeviceRemoteState(String did) {
            return mControllerImpl.queryDeviceRemoteState(did);
        }

        /**
         * Query device lan ip address.
         * @param did Device did
         * @return Query result - if device not in lan, retuen ""
         */
        public static String queryDeviceIp(String did){
            return mControllerImpl.queryDeviceIp(did);
        }

        /**
         * Bind device to server for control device in remote mode.
         * You need login first.
         *
         * @param listDevice Device info list
         * @return Bind result
         */
        public static BLBindDeviceResult bindWithServer(ArrayList<BLDNADevice> listDevice){
            return mControllerImpl.bindWithServer(listDevice);
        }

        /**
         * Bind device to server for control device in remote mode.
         * You need login first.
         *
         * @param device Device info
         * @return Bind result
         */
        public static BLBindDeviceResult bindWithServer(BLDNADevice device){
            ArrayList<BLDNADevice> listDevice = new ArrayList<>(1);
            listDevice.add(device);
            return mControllerImpl.bindWithServer(listDevice);
        }

        /**
         * Query device list bind to server status.
         *
         * @param listDevice Device info list
         * @return Query result
         */
        public static BLBindDeviceResult queryDeviceBinded(ArrayList<BLDNADevice> listDevice){
            return mControllerImpl.queryDeviceBinded(listDevice);
        }

        /**
         * Query device's product profile with specify the file.
         * After first query, sdk will store profile info in cache.
         *
         * @param did Device did
         * @param profilePath Specify profile path
         * @return Query result
         */
        public static BLProfileStringResult queryProfile(String did, String profilePath){
            return mControllerImpl.getProfileAsStringByDid(did, profilePath);
        }

        /**
         * Query device's product profile in default store path.
         * After first query, sdk will store profile info in cache.
         *
         * @param did Device did
         * @return Query result
         */
        public static BLProfileStringResult queryProfile(String did){
            return mControllerImpl.getProfileAsStringByDid(did, null);
        }

        /**
         * Query product profile with specify the file.
         * After first query, sdk will store profile info in cache.
         *
         * @param pid Device product pid
         * @param profilePath Specify profile path， can be null
         * @return Query result
         */
        public static BLProfileStringResult queryProfileByPid(String pid, String profilePath){
            return mControllerImpl.getProfileAsStringByPid(pid, profilePath);
        }

        /**
         * Query product profile in default store path.
         * After first query, sdk will store profile info in cache.
         *
         * @param pid Device product pid
         * @return Query result
         */
        public static BLProfileStringResult queryProfileByPid(String pid){
            return mControllerImpl.getProfileAsStringByPid(pid, null);
        }

        /**
         * Clean sdk store cache.
         * If profile has changed, you need clean cache first.
         *
         * @param pid Device product pid
         */
        public static void cleanProfileCahe(String pid){
            mControllerImpl.cleanProfileCahe(pid);
        }

        /**
         * Config device AP setting mode.
         *
         * @param ssid AP SSID
         * @param password AP password
         * @param type AP type
         * @param configParam Config param
         * @return Config result
         */
        public static BLAPConfigResult deviceAPConfig(String ssid, String password, int type, BLConfigParam configParam){
            return mControllerImpl.deviceAPConfig(ssid, password, type, configParam);
        }

        /**
         * Get AP List
         *
         * @param timeOut Get timeout
         * @return Get result
         */
        public static BLGetAPListResult deviceAPList(int timeOut){
            return mControllerImpl.deviceGetAPList(timeOut);
        }

        /**
         * Control device or sub device
         *
         * @param did Control wifi device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param stdControlParam Control data
         * @param configParam Config param
         * @return Control result
         */
        public static BLStdControlResult dnaControl(String did, String sDid, BLStdControlParam stdControlParam, BLConfigParam configParam){
            return mControllerImpl.dnaControl(did, sDid, stdControlParam, configParam);
        }

        /**
         * Control device with data string composed by yourself.
         *
         * @param did Control wifi device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param dataStr Control data string composed by yourself
         * @param cmd Control command composed by yourself
         * @param configParam Config param
         * @return Control result
         */
        public static String dnaControl(String did, String sDid, String dataStr, String cmd, BLConfigParam configParam){
            BLControllerDNAControlResult result = mControllerImpl.autoDNAControl(did, sDid, dataStr,cmd, configParam);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("status", result.getStatus());
                jsonObject.put("msg", result.getMsg());
                jsonObject.put("data", result.getData());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return  jsonObject.toString();
        }


        /**
         * Control device with data string composed by yourself.
         * command is "dev_ctrl"
         *
         * @param did Control wifi device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param dataStr Control data string composed by yourself
         * @param configParam Config param
         * @return Control result
         */
        public static BLStdControlResult dnaControl(String did, String sDid, String dataStr, BLConfigParam configParam){
            BLControllerDNAControlResult result = mControllerImpl.autoDNAControl(did, sDid, dataStr, BLDevCmdConstants.DEV_CTRL, configParam);
            BLStdControlResult controlResult = new BLStdControlResult();
            controlResult.setMsg(result.getMsg());
            controlResult.setStatus(result.getStatus());
            if (result.succeed()) {
                controlResult.setData(mControllerImpl.parseStdData(result.getData()));
            }

            return controlResult;
        }

        /**
         * Control device in default
         *
         * @param did Control wifi device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param stdControlParam Control data
         * @return Control result
         */
        public static BLStdControlResult dnaControl(String did, String sDid, BLStdControlParam stdControlParam){
            return mControllerImpl.dnaControl(did, sDid, stdControlParam, null);
        }

        /**
         * Control device with passthough raw data
         *
         * @param did  Control device did
         * @param data Control raw data
         * @param configParam Config param
         * @return Control result
         */
        public static BLPassthroughResult dnaPassthrough(String did, String sDid, byte[] data, BLConfigParam configParam){
            return mControllerImpl.dnaPassthrough(did, sDid, data, configParam);
        }

        /**
         * Control device or sub device with passthough raw data in default
         *
         * @param did  Control device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param data Control raw data
         * @return Control result
         */
        public static BLPassthroughResult dnaPassthrough(String did, String sDid, byte[] data){
            return mControllerImpl.dnaPassthrough(did, sDid, data, null);
        }

        /**
         * Get raw data from device script
         *
         * @param did  Control device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param data Control data
         * @return Get raw data
         */
        public static byte[] dnaControlData(String did, String sDid, BLStdControlParam data){
            return mControllerImpl.dnaControlData(did, sDid, data);
        }

        /**
         * Modify device info - name and lock status
         *
         * @param did Device did
         * @param name Device new name
         * @param lock Device lock status
         * @param configParam Config param
         * @return Modify result
         */
        public static BLUpdateDeviceResult updateDeviceInfo(String did, String name, boolean lock, BLConfigParam configParam){
            return mControllerImpl.updateDeviceInfo(did, name, lock, configParam);
        }

        /**
         * Modify device info - name and lock status in default
         *
         * @param did Device did
         * @param name Device new name
         * @param lock Device lock status
         * @return Modify result
         */
        public static BLUpdateDeviceResult updateDeviceInfo(String did, String name, boolean lock){
            return mControllerImpl.updateDeviceInfo(did, name, lock, null);
        }

        /**
         * Query device firmware version
         *
         * @param did Device did
         * @param configParam Config param
         * @return Query result
         */
        public static BLFirmwareVersionResult queryFirmwareVersion(String did, BLConfigParam configParam){
            return mControllerImpl.queryFirmwareVersion(did, configParam);
        }

        /**
         * Query device firmware version in default
         *
         * @param did  Device did
         * @return Query result
         */
        public static BLFirmwareVersionResult queryFirmwareVersion(String did){
            return mControllerImpl.queryFirmwareVersion(did, null);
        }

        /**
         * Query device or sub device firmware version
         *
         * @param did Device did
         * @param sDid Control sub device did. If sdid = nil, just control wifi device.
         * @param configParam Config param
         * @return Query result
         */
        public static BLFirmwareVersionResult devSubDevVersion(String did, String sDid, BLConfigParam configParam){
            return mControllerImpl.devSubDevVersion(did, sDid, configParam);
        }

        /**
         * Upgrade device firmware
         * Only send url to device, and device will upgrade after.
         *
         * @param did Device did
         * @param url New firmware download url
         * @param configParam Config param
         * @return Send result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult updateFirmware(String did, String url, BLConfigParam configParam){
            return mControllerImpl.updateFirmware(did, url, configParam);
        }

        /**
         * Upgrade device firmware in default.
         * Only send url to device, and device will upgrade after.
         *
         * @param did Device did
         * @param url New firmware download url
         * @return Send result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult updateFirmware(String did, String url){
            return mControllerImpl.updateFirmware(did, url, null);
        }

        /**
         * Query device server time
         *
         * @param did Device did
         * @param configParam Config param
         * @return Query result
         */
        public static BLDeviceTimeResult queryDeviceTime(String did, BLConfigParam configParam){
            return mControllerImpl.queryDeviceTime(did, configParam);
        }

        /**
         * Query device server time in default.
         *
         * @param did Device did
         * @return Query result
         */
        public static BLDeviceTimeResult queryDeviceTime(String did){
            return mControllerImpl.queryDeviceTime(did, null);
        }

        /**
         * Query deivce or sub device all tasks, include timer tasks and period tasks.
         *
         * @param did Query wifi device did
         * @param sDid Query sub device did. If sdid = nil, just control wifi device.
         * @param configParam Config param
         * @return Query result
         */
        public static BLQueryTaskResult queryTask(String did, String sDid, BLConfigParam configParam){
            return mControllerImpl.queryTask(did, sDid, configParam);
        }

        /**
         * Query deivce or sub device all tasks, include timer tasks and period tasks in default.
         *
         * @param did Query wifi device did
         * @param sDid Query sub device did. If sdid = nil, just control wifi device.
         * @return Query result
         */
        public static BLQueryTaskResult queryTask(String did, String sDid){
            return mControllerImpl.queryTask(did, sDid,  null);
        }

        /**
         * Add new common task or delay task to device or sub device
         *
         * @param did Add wifi device did
         * @param sDid Add sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 0, common task.
         *                  taskType = 1, delay task.
         * @param isNew if isNew = true, add new task. if false, update task which task index
         * @param task Add timer task info
         * @param stdData Add timer task do control data
         * @param configParam Config param
         * @return Add result and query now all tasks
         */
        public static BLQueryTaskResult updateTask(String did, String sDid, int taskType, boolean isNew, BLTimerInfo task, BLStdData stdData, BLConfigParam configParam) {
            return mControllerImpl.modifyTimerTask(did, sDid, taskType, isNew, task, stdData, configParam);
        }

        public static BLQueryTaskResult updateTask(String did, String sDid, int taskType, boolean isNew, BLTimerInfo task, BLStdData stdData) {
            return mControllerImpl.modifyTimerTask(did, sDid, taskType, isNew, task, stdData, null);
        }

        /**
         * Add new period task to device or sub device
         *
         * @param did Add wifi device did
         * @param sDid Add sub device did. If sdid = nil, just control wifi device.
         * @param isNew if isNew = true, add new task. if false, update task which task index
         * @param task Add period task info
         * @param stdData Add period task do control data
         * @param configParam Config param
         * @return Add result and query now all tasks
         */
        public static BLQueryTaskResult updateTask(String did, String sDid, boolean isNew, BLPeriodInfo task, BLStdData stdData, BLConfigParam configParam) {
            return mControllerImpl.modifyPeriodTask(did, sDid, isNew, task, stdData, configParam);
        }

        public static BLQueryTaskResult updateTask(String did, String sDid, boolean isNew, BLPeriodInfo task, BLStdData stdData) {
            return mControllerImpl.modifyPeriodTask(did, sDid, isNew, task, stdData, null);
        }
        /**
         * Add new cycle task or random task to device or sub device
         * @param did   Add wifi device did
         * @param sDid  Add sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 3, cycle task.
         *                  taskType = 4, random task.
         * @param isNew if isNew = true, add new task. if false, update task which task index
         * @param task  Add cycle task info
         * @param stdData1  Add cycle task do control data 1
         * @param stdData2  Add cycle task do control data 2
         * @param configParam   Config param
         * @return  Add result and query now all tasks
         */
        public static BLQueryTaskResult updateTask(String did, String sDid, int taskType, boolean isNew, BLCycleInfo task, BLStdData stdData1, BLStdData stdData2, BLConfigParam configParam) {
            return mControllerImpl.modifyCycleTask(did, sDid, taskType, isNew, task, stdData1, stdData2, configParam);
        }

        public static BLQueryTaskResult updateTask(String did, String sDid, int taskType, boolean isNew, BLCycleInfo task, BLStdData stdData1, BLStdData stdData2) {
            return mControllerImpl.modifyCycleTask(did, sDid, taskType, isNew, task, stdData1, stdData2, null);
        }
        /**
         * Delete task from device or sub device
         *
         * @param did Delete wifi device did
         * @param sDid Delete sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 0, common task.
         *                  taskType = 1, delay task.
         *                  taskType = 2, period task.
         *                  taskType = 3, cycle task.
         *                  taskType = 4, random task.
         * @param index task index
         * @param configParam Config param
         * @return Delete result and query now all tasks
         */
        public static BLQueryTaskResult delTask(String did, String sDid, int taskType, int index, BLConfigParam configParam){
            return mControllerImpl.delTask(did, sDid, taskType, index, configParam);
        }

        /**
         * Delete task from device or sub device
         *
         * @param did Delete wifi device did
         * @param sDid Delete sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 0, common task.
         *                  taskType = 1, delay task.
         *                  taskType = 2, period task.
         *                  taskType = 3, cycle task.
         *                  taskType = 4, random task.
         * @param index task index
         * @return Delete result and query now all tasks
         */
        public static BLQueryTaskResult delTask(String did, String sDid, int taskType, int index){
            return mControllerImpl.delTask(did, sDid, taskType, index, null);
        }

        /**
         * Query task control data to device
         *
         * @param did Query wifi device did
         * @param sDid Query sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 0, common task.
         *                  taskType = 1, delay task.
         *                  taskType = 2, period task.
         *                  taskType = 3, cycle task.
         *                  taskType = 4, random task.
         * @param index task index
         * @param configParam Config param
         * @return Query result
         */
        public static BLTaskDataResult queryTaskData(String did, String sDid, int taskType, int index, BLConfigParam configParam){
            return mControllerImpl.queryTaskData(did, sDid, taskType, index, configParam);
        }

        /**
         * Query task control data to device in default
         *
         * @param did Query wifi device did
         * @param sDid Query sub device did. If sdid = nil, just control wifi device.
         * @param taskType  taskType = 0, common task.
         *                  taskType = 1, delay task.
         *                  taskType = 2, period task.
         *                  taskType = 3, cycle task.
         *                  taskType = 4, random task.
         * @param index task index
         * @return Query result
         */
        public static BLTaskDataResult queryTaskData(String did, String sDid, int taskType, int index){
            return mControllerImpl.queryTaskData(did, sDid, taskType, index, null);
        }

        /**
         * Query one product ui version by pid
         *
         * @param pid Product pid
         * @return Query result
         */
        public static BLQueryResoureVersionResult queryUIVersion(String pid){
            List<String> list = new ArrayList<>();
            list.add(pid);
            return mControllerImpl.queryUI(list);
        }

        /**
         * Query products ui version by pids
         *
         * @param pidList Product pids
         * @return Query result
         */
        public static BLQueryResoureVersionResult queryUIVersion(List<String> pidList){
            return mControllerImpl.queryUI(pidList);
        }

        /**
         * Download device control ui.
         *
         * @param pid Product pid
         * @return Download result
         */
        public static BLDownloadUIResult downloadUI(String pid){
            return mControllerImpl.downloadUI(pid);
        }

        /**
         * Query one product script version by pid
         *
         * @param pid Product pid
         * @return Query result
         */
        public static BLQueryResoureVersionResult queryScriptVersion(String pid){
            List<String> list = new ArrayList<>();
            list.add(pid);
            return mControllerImpl.queryScript(list);
        }

        /**
         * Query products script version by pids
         *
         * @param pidList Product pids
         * @return Query result
         */
        public static BLQueryResoureVersionResult queryScriptVersion(List<String> pidList){
            return mControllerImpl.queryScript(pidList);
        }

        /**
         * Download device control script.
         *
         * @param pid Product pid
         * @return Download result
         */
        public static BLDownloadScriptResult downloadScript(String pid){
            return mControllerImpl.downloadScript(pid);
        }

        /**
         * Notice WiFi device to start scan sub devices
         *
         * @param did WiFi device did
         * @param subPid Scan sub device product pid. If subPid = nil, scan all sub devices.
         * @return Notice result
         */
        public static BLSubdevResult subDevScanStart(String did, String subPid){
            return mControllerImpl.devNewSubDevScanStart(did, subPid, null);
        }

        /***
         * Notice WiFi device to stop scan sub devices
         *
         * @param did WiFi device did
         * @return Notice result
         */
        public static BLSubdevResult subDevScanStop(String did){
            return mControllerImpl.devNewSubDevScanStop(did, null);
        }

        /**
         * Query WiFi device new scan sub devices
         *
         * @param did WiFi device did
         * @param subpid sub device pid
         * @param index Query page index
         * @param count Query page count
         * @return Query result
         */
        public static BLSubDevListResult devSubDevNewListQuery(String did, String subpid, int index, int count){
            return mControllerImpl.devGetNewSubDevList(did, subpid, index, count, null);
        }

        /**
         * Notice WiFi device to add a sub device
         *
         * @param did WiFi device did
         * @param subDevInfo Sub device info
         * @return Notice result
         */
        public static BLSubdevResult subDevAdd(String did, BLDNADevice subDevInfo){
            return mControllerImpl.devSubDevAdd(did, subDevInfo, null);
        }

        /**
         * Notice WiFi device to add a sub device with specify url.
         *
         * @param did WiFi device did
         * @param url Sub device info download url
         * @param param Sub device param
         * @param subDevInfo Sub device info
         * @return Notice result
         */
        public static BLSubdevResult subDevAdd(String did, String url, String param, BLDNADevice subDevInfo){
            return mControllerImpl.devSubDevAdd(did, url, param, subDevInfo, null);
        }

        /**
         * Query add new sub device to WiFi device result
         *
         * @param did WiFi device did
         * @param sDid Sub device did
         * @return Add result
         */
        public static BLSubDevAddResult devSubDevAddResultQuery(String did, String sDid){
            return mControllerImpl.devSubDevAddResultQuery(did, sDid, null);
        }

        /***
         * Delete sub deivce from WiFi device
         *
         * @param did WiFi device did
         * @param sDid Sub device did
         * @return Delete result
         */
        public static BLSubdevResult subDevDel(String did, String sDid){
            return mControllerImpl.devSubDevDel(did, sDid, null);
        }

        /**
         * Modify sub device info from WiFi device
         *
         * @param did WiFi device did
         * @param sDid Sub device did
         * @param name Sub device new name
         * @param lock Device is locked or not
         * @param type Sub device type
         * @return Modify result
         */
        public static BLSubdevResult subDevModify(String did, String sDid, String name, boolean lock, short type){
            return mControllerImpl.devSubDevModify(did, sDid, name, lock, type, null);
        }

        /***
         * Query sub devices have added in WiFi device
         *
         * @param did WiFi device did
         * @param index Query page index
         * @param count Query page count
         * @return Query result
         */
        public static BLSubDevListResult devSubDevListQuery(String did,int index, int count){
            return mControllerImpl.devGetSubDevList(did, index, count, null);
        }

        /**
         * Device do dnaproxy auth
         * Third party manufacturers can control device by cloud to our cloud.
         *
         * @param did Device did
         * @param accesskey Third party cloud get accesskey
         * @param tpid Third party cloud product type, can be nil
         * @param attr Device attributes
         * @return Dnaproxy auth result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult dnaProxyAuth(String did, String accesskey, String tpid, String attr) {
            return mControllerImpl.dnaProxyAuth(did, accesskey, tpid, attr);
        }

        /**
         * Device cancel dnaproxy auth
         *
         * @param did Device did
         * @param accesskey Third party cloud get accesskey
         * @return Cancel dnaproxy auth result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult dnaProxyDisauth(String did, String accesskey) {
            return mControllerImpl.dnaProxyDisauth(did, accesskey);
        }

        /**
         * Query device's ui store path by pid. Default is ../Let/ui/$(pid)/
         *
         * @param pid Product pid
         * @return UI path
         */
        public static String queryUIPath(String pid){
            return BLFileStorageUtils.getDefaultUIPath(pid);
        }

        /**
         * Query device's ui store path. Default is ../Let/ui/
         *
         * @return UI path
         */
        public static String queryUIPath(){
            return BLFileStorageUtils.mUIPath;
        }

        /**
         * Query ircode script store path. Default is ../Let/ircode/
         *
         * @return IRCode script path
         */
        public static String queryIRCodePath(){
            return BLFileStorageUtils.mIRCodeScriptPath;
        }

        /**
         * Query control script file name with full path by pid. Default is ../Let/script/$(pid).xx
         *
         * @param pid Product pid
         * @return Full script path
         */
        public static String queryScriptPath(String pid){
            String jsPath = BLFileStorageUtils.getDefaultJSScriptPath(pid);
            String luaPath = BLFileStorageUtils.getDefaultLuaScriptPath(pid);
            if(new File(jsPath).exists()){
                return jsPath;
            }else{
                return luaPath;
            }
        }

        /**
         * Query device data
         * @param did Device did
         * @param familyId familyId , can be null
         * @param startTime query start time
         * @param endTime query end time
         * @param type query type. default type = fw_spminielec_v1
         * @return
         */
        public static BLBaseBodyResult queryDeviceData(String did, String familyId, String startTime, String endTime, String type) {
            return mControllerImpl.queryDeviceData(did, familyId, startTime, endTime, type);
        }
    }

    /**
     * IRCode service class
     */
    public static final class IRCode {
        /**
         * Query all support ircode device types. Like AC, TV, STB ...
         *
         * @return Query result
         */
        public static BLBaseBodyResult requestIRCodeDeviceTypes() {
            return mIRCodeImpl.requestIRCodeDeviceTypes();
        }

        /**
         * Query all support ircode device brands with type.
         *
         * @param deviceType Device type ID
         * @return Query result
         */
        public static BLBaseBodyResult requestIRCodeDeviceBrands(int deviceType) {
            return mIRCodeImpl.requestIRCodeDeviceBrands(deviceType);
        }

        /**
         * Query ircode download url with type and brand and verison.
         *
         * @param deviceType Device type ID
         * @param deviceBrand Device brand ID
         * @return Query result with download url and randkey
         */
        public static BLBaseBodyResult requestIRCodeScriptDownloadUrl(int deviceType, int deviceBrand){
            return mIRCodeImpl.requestIRCodeScriptDownloadUrl(deviceType, deviceBrand);
        }

        /**
         *  Gets the list of regions under the specified region ID
         *  if ID = 0, get all countries‘ ids
         *  if isleaf = 0, you need call this interface again to get the list of regions.
         *  if isleaf = 1, don't need get again.
         *
         * @param locateid Region ID
         * @return the list of regions
         */
        public static BLBaseBodyResult requestSubAreas(int locateid) {
            return mIRCodeImpl.requestSubAreas(locateid);
        }

        /**
         * Get a list of set-top box providers
         * @param locateid Region ID
         * @return
         */
        public static BLBaseBodyResult requestSTBProvider(int locateid) {
            return mIRCodeImpl.requestSTBProvider(locateid);
        }

        /**
         * Query ircode download url with recognize ircode hex string.
         * Only support AC ircode.
         *
         * @param hexString ircode hex string
         * @return Query result with download url and randkey
         */
        public static BLBaseBodyResult recognizeIRCode(String hexString) {
            return mIRCodeImpl.recognizeIRCode(hexString);
        }

        /**
         * Get the set-top box ircode download URL
         * @param locateid Region ID
         * @param providerid Provider ID
         * @return
         */
        public static BLBaseBodyResult requestSTBSTBIRCodeScriptDownloadUrl(int locateid, int providerid) {
            return mIRCodeImpl.requestSTBSTBIRCodeScriptDownloadUrl(locateid, providerid);
        }

        /**
         * Download ircode script
         * If randkey != nil, script will decrypted after download. If you do this, other methods don't need randkey.
         * If randkey == nil, script wiil store with encrypted. You must store this randkey in db, and use this randkey to other methods.
         *
         * @param UrlString Download url
         * @param savePath Ircode script store path
         * @param randkey Ircode script decrypted key
         * @return Download result
         */
        public static BLDownloadScriptResult downloadIRCodeScript(String UrlString, String savePath, String randkey) {
            return mIRCodeImpl.downloadIRCodeScript(UrlString, savePath, randkey);
        }

        /**
         * Query ircode script infomation.
         * This infomation include ircode support device name, ircode support operate, ircode support data range...
         *
         * @param scriptPath Ircode script store path
         * @param deviceType Device type. TV = 1, STB = 2, AC = 3
         * @return Query result
         */
        public static BLIRCodeInfoResult queryIRCodeInfomation(String scriptPath, int deviceType) {
            return mIRCodeImpl.queryIRCodeInfomation(scriptPath, deviceType);
        }

        /**
         * Query AC ircode hex string
         *
         * @param scriptPath Ircode script store path
         * @param params AC status to change params
         * @return Query result with ircode hex string
         */
        public static BLIRCodeDataResult queryACIRCodeData(String scriptPath, BLQueryIRCodeParams params) {
            return mIRCodeImpl.queryACIRCodeData(scriptPath, params);
        }

        /**
         * Query TV/STB ircode hex string with specify funcname.
         *
         * @param scriptPath Ircode script store path
         * @param deviceType Device type. TV = 1, STB = 2
         * @param funcname Ircode support operate name
         * @return  Query result with ircode hex string
         */
        public static BLIRCodeDataResult queryTVIRCodeData(String scriptPath, int deviceType, String funcname) {
            return mIRCodeImpl.queryTVIRCodeData(scriptPath, deviceType, funcname);
        }
    }
    /**
     * Family control class
     */
    public static final class Family {

        /**
         * Set current family id
         * @param currentFamilyId
         */
        public static void setCurrentFamilyId(String currentFamilyId) {
            mFamilyImpl.setCurrentFamilyId(currentFamilyId);
        }

        /**
         * 家庭通用HTTP请求
         * @param urlPath URL路径 不包含domain
         * @param head 请求头
         * @param body 请求body
         * @return 返回数据
         */
        public static String familyHttpPost(String urlPath, Map<String, String> head, String body) {
            return mFamilyImpl.familyHttpPost(urlPath, head, body);
        }

        /**
         * Query all family id list by current login user.
         * @return Query result with family id list
         */
        public static BLFamilyIdListGetResult queryLoginUserFamilyIdList() {
            return mFamilyImpl.queryLoginUserFamilyIdList();
        }

        /**
         * Query all family baseinfo list by current login user.
         * @return Query result with family base info list
         */
        public static BLFamilyBaseInfoListResult queryLoginUserFamilyBaseInfoList() {
            return mFamilyImpl.queryLoginUserFamilyBaseInfoList();
        }

        /**
         * Create new family
         * @param familyInfo Family info
         * @param icon Family icon, can be null
         * @return create result
         */
        public static BLFamilyInfoResult createNewFamily(BLFamilyInfo familyInfo, File icon) {
            return mFamilyImpl.createNewFamily(familyInfo, icon);
        }

        /**
         * Query family all infos with specify ids
         * @param ids Family id list
         * @return query result
         */
        public static BLAllFamilyInfoResult queryAllFamilyInfos(String[] ids) {
            return mFamilyImpl.queryAllFamilyInfos(ids);
        }
        /**
         * Modify family base info
         * @param familyInfo Family base info
         * @return modify result
         */
        public static BLFamilyInfoResult modifyFamilyInfo(BLFamilyInfo familyInfo) {
            return mFamilyImpl.modifyFamilyInfo(familyInfo);
        }
        /**
         * Modify family base info and icon image
         * @param familyInfo Family base info
         * @param icon New icon image
         * @return modify result
         */
        public static BLFamilyInfoResult modifyFamilyIcon(BLFamilyInfo familyInfo, File icon) {
            return mFamilyImpl.modifyFamilyIcon(familyInfo, icon);
        }
        /**
         * Delete family with family id and version
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return delete result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult delFamily(String familyId, String familyVersion) {
            return mFamilyImpl.delFamily(familyId, familyVersion);
        }
        /**
         * Get System pre define room types
         * @return get result
         */
        public static BLDefineRoomTypeResult getSystemPreDefineRoomTypes() {
            return mFamilyImpl.getSystemPreDefineRoomTypes();
        }
        /**
         * Manage rooms with family id and version
         * @param familyId Family id
         * @param familyVersion Family current version
         * @param roomInfos Rooms in family
         * @return manage result
         */
        public static BLManageRoomResult manageFamilyRooms(String familyId, String familyVersion, List<BLFamilyRoomInfo> roomInfos) {
            return mFamilyImpl.manageFamilyRooms(familyId, familyVersion, roomInfos);
        }
        /**
         * Get family invite qrcode from server with family id
         * @param familyId Family id
         * @return get result
         */
        public static BLFamilyInviteQrcodeGetResult getFamilyInviteQrCode(String familyId) {
            return mFamilyImpl.getFamilyInviteQrCode(familyId);
        }
        /**
         * Post scan family invite qrcode string to server
         * @param qrcode Qrcode string
         * @return post result
         */
        public static BLFamilyInvitedQrcodePostResult postScanFamilyInviteQrcode(String qrcode) {
            return mFamilyImpl.postScanFamilyInviteQrcode(qrcode);
        }
        /**
         * Join Family with scan qrcode string
         * @param qrcode Qrcode string
         * @return join result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult joinFamilyWithQrcode(String qrcode) {
            return mFamilyImpl.joinFamilyWithQrcode(qrcode);
        }
        /**
         * Join family with family id
         * @param familyId Family ID
         * @return join result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult joinFamily(String familyId) {
            return mFamilyImpl.joinFamily(familyId);
        }
        /**
         * Quite family with family id
         * @param familyId Family ID
         * @return quite result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult quiteFamily(String familyId) {
            return mFamilyImpl.quiteFamily(familyId);
        }
        /**
         * Delete family members with family id
         * @param familyId Family ID
         * @param members Delete members
         * @return delete result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult deleteFamilyMembers(String familyId, List<String> members) {
            return mFamilyImpl.deleteFamilyMembers(familyId, members);
        }
        /**
         * Get all family member infos with family id
         *
         * @param familyId Family ID
         * @return get result
         */
        public static BLFamilyMemberInfoGetResult getFamilyMemberInfos(String familyId) {
            return mFamilyImpl.getFamilyMemberInfos(familyId);
        }
        /**
         * Charge devices have been family configed or not
         *
         * @param dids Deice did list
         * @return charge result
         */
        public static BLFamilyConfigedDevicesResult chargeDevicesHavaFamilyConfiged(List<String> dids) {
            return mFamilyImpl.chargeDevicesHavaFamilyConfiged(dids);
        }
        /**
         * Remove device from family
         *
         * @param did Delete device did
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return delete result
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult removeDeviceFromFamily(String did, String familyId, String familyVersion) {
            return mFamilyImpl.removeDeviceFromFamily(did, familyId, familyVersion);
        }
        /**
         * Add module to family
         *
         * @param moduleInfo Add module indo
         * @param familyInfo Family info
         * @param deviceInfo Module contains device info, can nil.
         * @param subDeviceInfo Module contains sub device info, can nil.
         * @return add result
         */
        public static BLModuleControlResult addModuleToFamily(BLFamilyModuleInfo moduleInfo, BLFamilyInfo familyInfo,
                                                              BLFamilyDeviceInfo deviceInfo, BLFamilyDeviceInfo subDeviceInfo) {
            return mFamilyImpl.addModuleToFamily(moduleInfo, familyInfo, deviceInfo, subDeviceInfo);
        }
        /**
         * Delete module from family
         *
         * @param moduleId Delete module ID
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return delete result
         */
        public static BLModuleControlResult delModuleFromFamily(String moduleId, String familyId, String familyVersion) {
            return mFamilyImpl.delModuleFromFamily(moduleId, familyId, familyVersion);
        }
        /**
         * Modify module info from family
         * @param moduleInfo Modify module info
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return modify result
         */
        public static BLModuleControlResult modifyModuleFromFamily(BLFamilyModuleInfo moduleInfo, String familyId, String familyVersion) {
            return mFamilyImpl.modifyModuleFromFamily(moduleInfo, familyId, familyVersion);
        }
        /**
         * Modify module flag from family
         * @param moduleId Modify module ID
         * @param flag New flag
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return modify result
         */
        public static BLModuleControlResult modifyModuleFlagFromFamily(String moduleId, int flag, String familyId, String familyVersion) {
            return mFamilyImpl.modifyModuleFlagFromFamily(moduleId, flag, familyId, familyVersion);
        }
        /**
         * Move module to specify room in family
         *
         * @param moduleId Modify module ID
         * @param roomId Specify room ID
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return modify result
         */
        public static BLModuleControlResult moveModuleRoomFromFamily(String moduleId, String roomId, String familyId, String familyVersion) {
            return mFamilyImpl.moveModuleRoomFromFamily(moduleId, roomId, familyId, familyVersion);
        }

        /**
         * Modify module info and move to specify room in family
         * @param moduleInfo Modify module ID
         * @param roomId Specify room ID
         * @param familyId Family id
         * @param familyVersion Family current version
         * @return modify result
         */
        public static BLModuleControlResult modifyAndMoveModuleFromFamily(BLFamilyModuleInfo moduleInfo, String roomId, String familyId, String familyVersion) {
            return mFamilyImpl.modifyAndMoveModuleFromFamily(moduleInfo, roomId, familyId, familyVersion);
        }

        /**
         * Query family peak valley electricity info
         * @return query result
         */
        public static BLFamilyElectricityInfoResult queryFamilyPeakValleyElectricityInfo() {
            return mFamilyImpl.queryFamilyPeakValleyElectricityInfo();
        }

        /**
         * Config family peak valley electricity info
         * @param electricityInfo New family peak valley electricity info
         * @return config result
         */
        public static BLFamilyElectricityInfoResult configFamilyPeakValleyElectricityInfo(BLFamilyElectricityInfo electricityInfo) {
            return mFamilyImpl.configFamilyPeakValleyElectricityInfo(electricityInfo);
        }

        /**
         * Get private data id
         * @return
         */
        public static BLPrivateDataIdResult getFamilyPrivateDataId() {
            return mFamilyImpl.getFamilyPrivateDataId(null);
        }

        /**
         * Update private data list
         * @param dataList
         * @return
         */
        public static BLPrivateDataResult updateFamilyPrivateData(List<BLPrivateData> dataList) {
            return mFamilyImpl.updateFamilyPrivateData(dataList, null, null, 0);
        }

        /**
         * Delete private data list
         * @param dataList
         * @return
         */
        public static cn.com.broadlink.sdk.result.BLBaseResult deleteFamilyPrivateData(List<BLPrivateData> dataList) {
            return mFamilyImpl.deleteFamilyPrivateData(dataList, null, null, 0);
        }

        /**
         * Query private data by key
         * @param mkeyid
         * @return
         */
        public static BLPrivateDataResult queryFamilyPrivateData(String mkeyid) {
            return mFamilyImpl.queryFamilyPrivateData(mkeyid, null);
        }
    }
}
