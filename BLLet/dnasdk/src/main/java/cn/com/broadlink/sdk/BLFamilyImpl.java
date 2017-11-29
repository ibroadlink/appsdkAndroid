package cn.com.broadlink.sdk;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.family.BLModuleType;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.param.family.BLFamilyBaseInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyElectricityInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyMemberInfo;
import cn.com.broadlink.sdk.param.family.BLPrivateData;
import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.result.account.BLLoginResult;
import cn.com.broadlink.sdk.result.family.BLAllFamilyInfoResult;
import cn.com.broadlink.sdk.result.family.BLDefineRoomTypeResult;
import cn.com.broadlink.sdk.param.family.BLFamilyAllInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyDeviceInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyIdInfo;
import cn.com.broadlink.sdk.result.family.BLFamilyBaseInfoListResult;
import cn.com.broadlink.sdk.result.family.BLFamilyConfigedDevicesResult;
import cn.com.broadlink.sdk.result.family.BLFamilyElectricityInfoResult;
import cn.com.broadlink.sdk.result.family.BLFamilyIdListGetResult;
import cn.com.broadlink.sdk.param.family.BLFamilyInfo;
import cn.com.broadlink.sdk.result.family.BLFamilyInfoResult;
import cn.com.broadlink.sdk.result.family.BLFamilyInviteQrcodeGetResult;
import cn.com.broadlink.sdk.result.family.BLFamilyInvitedQrcodePostResult;
import cn.com.broadlink.sdk.param.family.BLFamilyModuleInfo;
import cn.com.broadlink.sdk.param.family.BLFamilyRoomInfo;
import cn.com.broadlink.sdk.result.family.BLFamilyMemberInfoGetResult;
import cn.com.broadlink.sdk.result.family.BLManageRoomResult;
import cn.com.broadlink.sdk.result.family.BLModuleControlResult;
import cn.com.broadlink.sdk.result.family.BLPrivateDataIdResult;
import cn.com.broadlink.sdk.result.family.BLPrivateDataResult;

/**
 * Created by zjjllj on 2017/2/13.
 */

final class BLFamilyImpl implements BLAccountLoginListener {
    /**
     * 未知异常提示语
     **/
    private static final String ERR_UNKNOWN = "unknown error";
    /**
     * 未登录异常提示语
     **/
    private static final String ERR_NOT_LOGIN = "not login";
    /**
     * 未登录异常提示语
     **/
    private static final String ERR_PARAMS_INUPT_ERROR = "params input error";

    /**
     * 服务没有返回结果
     */
    private static final String ERR_SERVER_NO_RESULT = "Server has no return data";

    private BLFamilyHttpAccessor httpAccessor;

    /**
     * 存储已登录userid
     **/
    private String mUserid = null;

    /**
     * 存储已登录usersession
     **/
    private String mUserserssion = null;

    /** http超时时间设置 **/
    private int mHttpTimeOut = 30000;
    /** 存储License ID */
    private String mLicenseid = null;

    /**
     * 当前家庭FamilyId
     */
    private String mCurrentFamilyId = null;
    /**
     * 账户模块登陆后的回调
     *
     * @param loginResult
     */
    @Override
    public void onLogin(BLLoginResult loginResult) {
        mUserid = loginResult.getUserid();
        mUserserssion = loginResult.getLoginsession();

        httpAccessor.setmUserid(mUserid);
        httpAccessor.setmLoginsession(mUserserssion);
    }

    public void init(String lid, BLConfigParam configParam) {
        mLicenseid = lid;

        if(configParam != null){
            // 初始化参数
            String timeout = configParam.get(BLConfigParam.HTTP_TIMEOUT);

            if(timeout != null){
                try {
                    mHttpTimeOut = Integer.parseInt(timeout);
                }catch (Exception e){}
            }

            String familyHost = configParam.get(BLConfigParam.FAMILY_HOST);
            if(!TextUtils.isEmpty(familyHost)){
                BLApiUrls.Family.setUrlBase(familyHost);
            }
        }

        httpAccessor = BLFamilyHttpAccessor.getInstance();
        httpAccessor.setmLicenseId(mLicenseid);
    }

    /**
     * 设置当前家庭的家庭ID
     * @param currentFamilyId
     */
    public void setCurrentFamilyId(String currentFamilyId) {
        this.mCurrentFamilyId = currentFamilyId;
    }

    /**
     * 家庭通用HTTP请求
     * @param urlPath URL路径 不包含domain
     * @param head 请求头
     * @param body 请求body
     * @return 返回数据
     */
    public String familyHttpPost(String urlPath, Map<String, String> head, String body) {

        String host;
        if (urlPath.startsWith("http://") || urlPath.startsWith("https://")) {
            host = urlPath;
        } else {
            host = BLApiUrls.Family.getFamilyCommonUrl(urlPath);
        }

        return httpAccessor.generalPost(host, head, body, mHttpTimeOut);
    }

    /**
     * 查询登录用户的所有的家庭ID列表
     * @return
     */
    public BLFamilyIdListGetResult queryLoginUserFamilyIdList() {
        BLFamilyIdListGetResult result = new BLFamilyIdListGetResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ID_LIST(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("familyinfo");
                    if (jExtra != null) {
                        for(int i = 0 ; i < jExtra.length(); i ++){
                            JSONObject infoObject = jExtra.getJSONObject(i);

                            BLFamilyIdInfo familyIdInfo = new BLFamilyIdInfo();
                            familyIdInfo.setShareFlag(infoObject.optInt("shareflag"));
                            familyIdInfo.setFamilyId(infoObject.optString("id", null));
                            familyIdInfo.setFamilyVersion(infoObject.optString("version", null));
                            familyIdInfo.setFamilyName(infoObject.optString("familyname", null));

                            result.getIdInfoList().add(familyIdInfo);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 查询登录用户的所有的家庭ID列表
     * @return
     */
    public BLFamilyBaseInfoListResult queryLoginUserFamilyBaseInfoList() {
        BLFamilyBaseInfoListResult result = new BLFamilyBaseInfoListResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_BASE_INFO_LIST(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("families");
                    if (jExtra != null) {
                        for(int i = 0 ; i < jExtra.length(); i ++){
                            JSONObject infoObject = jExtra.getJSONObject(i);

                            BLFamilyBaseInfo info = new BLFamilyBaseInfo();
                            info.setShareFlag(infoObject.optInt("shareflag"));
                            info.setCreateUser(infoObject.optString("createuser", null));

                            JSONObject familyObject = infoObject.optJSONObject("familyinfo");
                            if (familyObject != null) {
                                BLFamilyInfo familyInfo = new BLFamilyInfo(familyObject);
                                info.setFamilyInfo(familyInfo);
                            }

                            result.getInfoList().add(info);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 创建家庭
     * @param familyInfo 家庭基本信息
     * @param icon 家庭icon
     * @return
     */
    public BLFamilyInfoResult createNewFamily(BLFamilyInfo familyInfo, File icon) {
        BLFamilyInfoResult result = new BLFamilyInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familylimit", familyInfo.getFamilyLimit());
            jParam.put("longitude", familyInfo.getFamilyLongitude());
            jParam.put("latitude", familyInfo.getFamilyLatitude());

            if (familyInfo.getFamilyName() != null) {
                jParam.put("name", familyInfo.getFamilyName());
            }

            if (familyInfo.getFamilyDescription() != null) {
                jParam.put("description", familyInfo.getFamilyDescription());
            }

            if (familyInfo.getFamilyPostcode() != null) {
                jParam.put("postcode", familyInfo.getFamilyPostcode());
            }

            if (familyInfo.getFamilyMailaddress()!= null) {
                jParam.put("mailaddress", familyInfo.getFamilyMailaddress());
            }

            if (familyInfo.getFamilyCountry() != null) {
                jParam.put("country", familyInfo.getFamilyCountry());
            }

            if (familyInfo.getFamilyProvince() != null) {
                jParam.put("province", familyInfo.getFamilyProvince());
            }

            if (familyInfo.getFamilyCity() != null) {
                jParam.put("city", familyInfo.getFamilyCity());
            }

            if (familyInfo.getFamilyArea()!= null) {
                jParam.put("area", familyInfo.getFamilyArea());
            }

            String ret = httpAccessor.generalMutipartPost(BLApiUrls.Family.URL_FAMILY_ADD(), null, jParam.toString(), icon, mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    familyInfo.setFamilyId(jResult.optString("familyid", null));
                    familyInfo.setFamilyIconPath(jResult.optString("iconpath", null));
                    familyInfo.setFamilyVersion(jResult.optString("version", null));
                    result.setFamilyInfo(familyInfo);
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 查询所有家庭的所有信息
     * @param ids 家庭ID列表
     * @return
     */
    public BLAllFamilyInfoResult queryAllFamilyInfos(String[] ids) {
        BLAllFamilyInfoResult result = new BLAllFamilyInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (ids == null || ids.length == 0) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);

            List<String> idList = Arrays.asList(ids);
            JSONArray jIds = new JSONArray(idList);

            jParam.put("familyid", jIds);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ALL_INFO(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("familyallinfo");
                    if (jExtra != null) {
                        for(int i = 0 ; i < jExtra.length(); i ++){
                            JSONObject infoObject = jExtra.getJSONObject(i);
                            BLFamilyAllInfo familyAllInfo = new BLFamilyAllInfo();

                            familyAllInfo.setShareFlag(infoObject.optInt("shareflag"));
                            familyAllInfo.setCreateUser(infoObject.optString("createuser", null));

                            JSONObject familyObject = infoObject.optJSONObject("familyinfo");
                            if (familyObject != null) {
                                BLFamilyInfo familyInfo = new BLFamilyInfo(familyObject);
                                familyAllInfo.setFamilyInfo(familyInfo);
                            }

                            JSONArray jRoomArray = infoObject.optJSONArray("roominfo");
                            if (jRoomArray != null) {
                                for (int j = 0; j < jRoomArray.length(); j++) {
                                    JSONObject roomObject = jRoomArray.getJSONObject(j);
                                    BLFamilyRoomInfo roomInfo = new BLFamilyRoomInfo(roomObject);
                                    familyAllInfo.getRoomInfos().add(roomInfo);
                                }
                            }

                            JSONArray jModuleArray = infoObject.optJSONArray("moduleinfo");
                            if (jModuleArray != null) {
                                for (int j = 0; j < jModuleArray.length(); j++) {
                                    JSONObject moduleObject = jModuleArray.getJSONObject(j);
                                    BLFamilyModuleInfo moduleInfo = new BLFamilyModuleInfo(moduleObject);
                                    familyAllInfo.getModuleInfos().add(moduleInfo);
                                }
                            }

                            JSONArray jDeviceArray = infoObject.optJSONArray("devinfo");
                            if (jDeviceArray != null) {
                                for (int j = 0; j < jDeviceArray.length(); j++) {
                                    JSONObject deviceObject = jDeviceArray.getJSONObject(j);
                                    BLFamilyDeviceInfo deviceInfo = new BLFamilyDeviceInfo(deviceObject);
                                    familyAllInfo.getDeviceInfos().add(deviceInfo);
                                }
                            }

                            JSONArray jSubDeviceArray = infoObject.optJSONArray("subdevinfo");
                            if (jSubDeviceArray != null) {
                                for (int j = 0; j < jSubDeviceArray.length(); j++) {
                                    JSONObject subDeviceObject = jSubDeviceArray.getJSONObject(j);
                                    BLFamilyDeviceInfo deviceInfo = new BLFamilyDeviceInfo(subDeviceObject);
                                    familyAllInfo.getSubDeviceInfos().add(deviceInfo);
                                }
                            }

                            result.getAllInfos().add(familyAllInfo);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 修改家庭基本信息
     * @param familyInfo 家庭基本信息
     * @return
     */
    public BLFamilyInfoResult modifyFamilyInfo(BLFamilyInfo familyInfo) {
        BLFamilyInfoResult result = new BLFamilyInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyInfo == null || familyInfo.getFamilyId() == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = familyInfo.toDictionary();
            if (jParam == null) {
                result.setStatus(BLAppSdkErrCode.ERR_PARAM);
                result.setMsg("family to json failed");
                return result;
            }
            jParam.put("userid", mUserid);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyInfo.getFamilyId());

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODIFY(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    familyInfo.setFamilyVersion(jResult.optString("version", null));
                    result.setFamilyInfo(familyInfo);
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 修改家庭icon
     * @param familyInfo 家庭基本信息
     * @param icon 修改的icon
     * @return
     */
    public BLFamilyInfoResult modifyFamilyIcon(BLFamilyInfo familyInfo, File icon) {
        BLFamilyInfoResult result = new BLFamilyInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyInfo == null || familyInfo.getFamilyId() == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        if (icon == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg("modify icon data not exit");
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyInfo.getFamilyId());
            jParam.put("version", familyInfo.getFamilyVersion());

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyInfo.getFamilyId());

            String ret = httpAccessor.generalMutipartPost(BLApiUrls.Family.URL_FAMILY_MODIFY_ICON(), head, jParam.toString(), icon, mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    familyInfo.setFamilyVersion(jResult.optString("version", null));
                    familyInfo.setFamilyIconPath(jResult.optString("iconpath", null));
                    result.setFamilyInfo(familyInfo);
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 删除家庭
     * @param familyId 删除家庭的ID
     * @param familyVersion 删除家庭的版本号
     * @return
     */
    public BLBaseResult delFamily(String familyId, String familyVersion) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null || familyVersion == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_DEL(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 获取云端预定义的房间类型
     * @return
     */
    public BLDefineRoomTypeResult getSystemPreDefineRoomTypes() {
        String lang = BLCommonTools.getLanguage();
        BLDefineRoomTypeResult result = new BLDefineRoomTypeResult();

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("language", lang);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ROOM_DEFINE(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("defineroom");
                    if (jExtra != null) {
                        for (int i = 0; i < jExtra.length(); i++) {
                            JSONObject roomObject = jExtra.getJSONObject(i);
                            int roomType = roomObject.optInt("type");
                            result.getDefineRoomTypes().add(roomType);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 家庭房间管理
     * @param familyId 家庭ID
     * @param familyVersion 当前家庭版本
     * @param roomInfos 房间信息列表
     * @return
     */
    public BLManageRoomResult manageFamilyRooms(String familyId, String familyVersion, List<BLFamilyRoomInfo> roomInfos) {
        BLManageRoomResult result = new BLManageRoomResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null || familyVersion == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        if (roomInfos == null || roomInfos.isEmpty()) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg("Manage rooms is empty");
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);

            JSONArray jRoomArray = new JSONArray();
            for (BLFamilyRoomInfo info: roomInfos ) {
                JSONObject jRoomObject = new JSONObject();
                jRoomObject.put("roomid", info.getRoomId());
                jRoomObject.put("name", info.getName());
                jRoomObject.put("action", info.getAction());
                jRoomObject.put("type", info.getType());
                jRoomObject.put("order", info.getOrder());
                jRoomArray.put(jRoomObject);
            }

            jParam.put("manageinfo", jRoomArray);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ROOM_LIST_MANAGE(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setFamilyId(familyId);
                    result.setFamilyVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }

        return result;
    }

    /**
     * 获取家庭邀请二维码字符串
     * @param familyId 邀请家庭的ID
     * @return
     */
    public BLFamilyInviteQrcodeGetResult getFamilyInviteQrCode(String familyId) {
        BLFamilyInviteQrcodeGetResult result = new BLFamilyInviteQrcodeGetResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_INVITE_QRCODE_REQUEST(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setQrcode(jResult.optString("qrcode", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 提交扫描到的二维码信息，获取家庭信息
     * @param qrcode 二维码字符串
     * @return
     */
    public BLFamilyInvitedQrcodePostResult postScanFamilyInviteQrcode(String qrcode) {
        BLFamilyInvitedQrcodePostResult result = new BLFamilyInvitedQrcodePostResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (qrcode == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("qrcode", qrcode);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_INVITE_QRCODE_SCAN_INFO(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setFamilyId(jResult.optString("familyid", null));
                    result.setFamilyName(jResult.optString("familyname", null));
                    result.setFamilyIconPath(jResult.optString("icon", null));
                    result.setFamilyCreatorId(jResult.optString("createuser", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 根据扫描到的二维码加入到家庭中
     * @param qrcode 扫描到的二维码
     * @return
     */
    public BLBaseResult joinFamilyWithQrcode(String qrcode) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (qrcode == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("qrcode", qrcode);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_INVITE_QRCODE_JOIN(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 加入到指定家庭中
     * @param familyId 家庭ID
     * @return
     */
    public BLBaseResult joinFamily(String familyId) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_JOIN(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 退出家庭
     * @param familyId 家庭ID
     * @return
     */
    public BLBaseResult quiteFamily(String familyId) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_QUIT(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 删除指定家庭下家庭成员
     * @param familyId 家庭ID
     * @param members 家庭成员
     * @return
     */
    public BLBaseResult deleteFamilyMembers(String familyId, List<String> members) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        if (members == null || members.isEmpty()) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg("Delete members is empty");
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);

            JSONArray jMemberArray = new JSONArray(members);

            jParam.put("familymember", jMemberArray);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_DEL(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 获取家庭成员所有信息
     * @param familyId 指定家庭ID
     * @return
     */
    public BLFamilyMemberInfoGetResult getFamilyMemberInfos(String familyId) {
        BLFamilyMemberInfoGetResult result = new BLFamilyMemberInfoGetResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MEMBER_INFO_GET(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("familymember");
                    if (jExtra != null) {
                        for (int i = 0; i < jExtra.length(); i++) {
                            JSONObject memberObject = jExtra.getJSONObject(i);
                            BLFamilyMemberInfo info = new BLFamilyMemberInfo();
                            info.setFamilyId(familyId);
                            info.setUserId(memberObject.optString("userid", null));
                            info.setType(memberObject.optInt("type"));
                            info.setNickName(memberObject.optString("nickname", null));
                            info.setIconPath(memberObject.optString("icon", null));
                            result.getMemberInfos().add(info);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 判断设备是否已经被家庭配置
     * @param dids 判断的设备列表
     * @return
     */
    public BLFamilyConfigedDevicesResult chargeDevicesHavaFamilyConfiged(List<String> dids) {
        BLFamilyConfigedDevicesResult result = new BLFamilyConfigedDevicesResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (dids == null || dids.isEmpty()) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            
            JSONArray jDidArray = new JSONArray(dids);
            jParam.put("dids", jDidArray);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_DEVICE_CONFIGED_LIST(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jExtra = jResult.optJSONArray("configdev");
                    if (jExtra != null) {
                        for (int i = 0; i < jExtra.length(); i++) {
                            String configDev = jExtra.getString(i);
                            result.getDidList().add(configDev);
                        }
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 从家庭中删除设备
     * @param did 删除设备的Did
     * @param familyId 家庭Id
     * @param familyVersion 家庭版本
     * @return
     */
    public BLBaseResult removeDeviceFromFamily(String did, String familyId, String familyVersion) {
        BLBaseResult result = new BLBaseResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (did == null || familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);
            jParam.put("did", did);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_DEVICE_DEL(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 添加模块到家庭中
     * @param moduleInfo 模块的信息
     * @param familyInfo 家庭信息
     * @param deviceInfo 模块下设备信息
     * @param subDeviceInfo 模块下子设备信息
     * @return
     */
    public BLModuleControlResult addModuleToFamily(BLFamilyModuleInfo moduleInfo, BLFamilyInfo familyInfo,
                                            BLFamilyDeviceInfo deviceInfo, BLFamilyDeviceInfo subDeviceInfo) {

        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleInfo == null || familyInfo == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("version", familyInfo.getFamilyVersion());
            jParam.put("moduleinfo", moduleInfo.toDictionary());

            if (deviceInfo != null) {
                jParam.put("devinfo", deviceInfo.toDictionary());
            }

            if (subDeviceInfo != null) {
                jParam.put("subdevinfo", subDeviceInfo.toDictionary());
            }

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyInfo.getFamilyId());

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_ADD(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setModuleId(jResult.optString("moduleid", null));
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 从家庭中删除模块
     * @param moduleId 删除模块的ID
     * @param familyId 家庭ID
     * @param familyVersion 家庭版本
     * @return
     */
    public BLModuleControlResult delModuleFromFamily(String moduleId, String familyId, String familyVersion) {
        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleId == null || familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);
            jParam.put("moduleid", moduleId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_DEL(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 修改模块信息
     * @param moduleInfo 修改后的模块信息
     * @param familyId 家庭ID
     * @param familyVersion 家庭版本
     * @return
     */
    public BLModuleControlResult modifyModuleFromFamily(BLFamilyModuleInfo moduleInfo, String familyId, String familyVersion) {
        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleInfo == null || familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("version", familyVersion);
            jParam.put("moduleinfo", moduleInfo.toDictionary());

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_MODIFY(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setModuleId(moduleInfo.getModuleId());
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;

    }

    /**
     * 修改模块Flag
     * @param moduleId 模块ID
     * @param flag 模块Flag
     * @param familyId 家庭ID
     * @param familyVersion 家庭版本
     * @return
     */
    public BLModuleControlResult modifyModuleFlagFromFamily(String moduleId, int flag, String familyId, String familyVersion) {
        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleId == null || familyId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);
            jParam.put("moduleid", moduleId);
            jParam.put("newflag", flag);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_MODIFY_FLAG(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setModuleId(moduleId);
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 移动模块房间
     * @param moduleId 模块ID
     * @param roomId 房间ID
     * @param familyId 家庭ID
     * @param familyVersion 家庭版本
     * @return
     */
    public BLModuleControlResult moveModuleRoomFromFamily(String moduleId, String roomId, String familyId, String familyVersion) {
        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleId == null || familyId == null || roomId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);
            jParam.put("moduleid", moduleId);
            jParam.put("roomid", roomId);

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_MODIFY_ROOM(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setModuleId(moduleId);
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 修改模块基本信息和移动模块房间
     * @param moduleInfo 模块信息
     * @param roomId 房间ID
     * @param familyId 家庭ID
     * @param familyVersion 家庭版本
     * @return
     */
    public BLModuleControlResult modifyAndMoveModuleFromFamily(BLFamilyModuleInfo moduleInfo, String roomId, String familyId, String familyVersion) {
        BLModuleControlResult result = new BLModuleControlResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        if (moduleInfo == null || familyId == null || roomId == null) {
            result.setStatus(BLAppSdkErrCode.ERR_PARAM);
            result.setMsg(ERR_PARAMS_INUPT_ERROR);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("familyid", familyId);
            jParam.put("version", familyVersion);
            jParam.put("moduleinfo", moduleInfo.toDictionary());
            jParam.put("roomid", roomId);
            jParam.put("movetype", moduleInfo.getModuleType());

            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyId);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_MODULE_MODIFY_INFO_AND_ROOM(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 查询家庭峰谷电信息
     * @return
     */
    public BLFamilyElectricityInfoResult queryFamilyPeakValleyElectricityInfo() {
        BLFamilyElectricityInfoResult result = new BLFamilyElectricityInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ELECTRIC_INFO_QUERY(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONObject jElectricityInfo = jResult.optJSONObject("electricpriinfo");
                    if (jElectricityInfo != null) {
                        BLFamilyElectricityInfo info = new BLFamilyElectricityInfo();
                        info.setBillingaddr(jElectricityInfo.optString("billingaddr", null));
                        info.setPvetime(jElectricityInfo.optString("pvetime", null));
                        info.setPeakprice(jElectricityInfo.optDouble("peakprice"));
                        info.setValleyprice(jElectricityInfo.optDouble("valleyprice"));
                        result.setElectricityInfo(info);
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    /**
     * 配置家庭峰谷电信息
     * @param electricityInfo 峰谷电信息
     * @return
     */
    public BLFamilyElectricityInfoResult configFamilyPeakValleyElectricityInfo(BLFamilyElectricityInfo electricityInfo) {

        BLFamilyElectricityInfoResult result = new BLFamilyElectricityInfoResult();
        if (mUserid == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg(ERR_NOT_LOGIN);
            return result;
        }

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("userid", mUserid);
            jParam.put("billingaddr", electricityInfo.getBillingaddr());
            jParam.put("pvetime", electricityInfo.getPvetime());
            jParam.put("peakprice", electricityInfo.getPeakprice());
            jParam.put("valleyprice", electricityInfo.getValleyprice());

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_ELECTRIC_INFO_CONFIG(), null, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONObject jElectricityInfo = jResult.optJSONObject("electricpriinfo");
                    if (jElectricityInfo != null) {
                        BLFamilyElectricityInfo info = new BLFamilyElectricityInfo();
                        info.setBillingaddr(jElectricityInfo.optString("billingaddr", null));
                        info.setPvetime(jElectricityInfo.optString("pvetime", null));
                        info.setPeakprice(jElectricityInfo.optDouble("peakprice"));
                        info.setValleyprice(jElectricityInfo.optDouble("valleyprice"));
                        result.setElectricityInfo(info);
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());

            return result;
        }
        return result;
    }

    public BLPrivateDataIdResult getFamilyPrivateDataId(String mtag) {
        BLPrivateDataIdResult result = new BLPrivateDataIdResult();

        if (mtag == null) {
            mtag = "subdevinfo";
        }

        String familyid;
        if (mCurrentFamilyId != null) {
            familyid = mCurrentFamilyId;
        } else {
            familyid = mUserid;
        }

        try {
            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyid);

            JSONObject jParam = new JSONObject();
            jParam.put("mtag", mtag);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_PRIVATE_DATA_ID(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONObject jData = jResult.optJSONObject("data");
                    if (jData != null) {
                        result.setDataId(jData.optString("id", null));
                    }
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    public BLPrivateDataResult updateFamilyPrivateData(List<BLPrivateData> dataList, String mtag, String familyVersion, int isUpdataFamilyVersion) {
        BLPrivateDataResult result = new BLPrivateDataResult();
        if (mtag == null) {
            mtag = "subdevinfo";
        }
        if (familyVersion == null) {
            familyVersion = "";
        }
        String familyid;
        if (mCurrentFamilyId != null) {
            familyid = mCurrentFamilyId;
        } else {
            familyid = mUserid;
        }

        try {
            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyid);

            JSONObject jParam = new JSONObject();
            jParam.put("familyid", familyid);
            jParam.put("version", familyVersion);
            jParam.put("updatefamily", isUpdataFamilyVersion);

            JSONArray jDataArray = new JSONArray();
            for (BLPrivateData data : dataList) {
                JSONObject jData = new JSONObject();
                jData.put("mtag", mtag);
                jData.put("mkeyid", data.getMkeyid());
                jData.put("content", data.getContent());
                jData.put("idversion", data.getIdversion());

                jDataArray.put(jData);
            }
            jParam.put("datalist", jDataArray);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_PRIVATE_DATA_UPDATE(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jDataList = jResult.optJSONArray("datalist");
                    if (jDataList != null) {
                        for(int i = 0 ; i < jDataList.length(); i ++){
                            BLPrivateData data = new BLPrivateData();
                            JSONObject infoObject = jDataList.getJSONObject(i);
                            data.setMkeyid(infoObject.optString("mkeyid", null));
                            data.setIdversion(infoObject.optString("idversion", null));
                            result.getDataList().add(data);
                        }
                    }
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }


        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }


    public BLBaseResult deleteFamilyPrivateData(List<BLPrivateData> dataList, String mtag, String familyVersion, int isUpdataFamilyVersion) {
        BLBaseResult result = new BLBaseResult();

        if (mtag == null) {
            mtag = "subdevinfo";
        }
        if (familyVersion == null) {
            familyVersion = "";
        }
        String familyid;
        if (mCurrentFamilyId != null) {
            familyid = mCurrentFamilyId;
        } else {
            familyid = mUserid;
        }

        try {
            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyid);

            JSONObject jParam = new JSONObject();
            jParam.put("familyid", familyid);
            jParam.put("version", familyVersion);
            jParam.put("updatefamily", isUpdataFamilyVersion);

            JSONArray jDataArray = new JSONArray();
            for (BLPrivateData data : dataList) {
                JSONObject jData = new JSONObject();
                jData.put("mtag", mtag);
                jData.put("mkeyid", data.getMkeyid());
                jData.put("idversion", data.getIdversion());

                jDataArray.put(jData);
            }
            jParam.put("datalist", jDataArray);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_PRIVATE_DATA_DELETE(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    public BLPrivateDataResult queryFamilyPrivateData(String mkeyid, String mtag) {
        BLPrivateDataResult result = new BLPrivateDataResult();
        if (mtag == null) {
            mtag = "subdevinfo";
        }

        String familyid;
        if (mCurrentFamilyId != null) {
            familyid = mCurrentFamilyId;
        } else {
            familyid = mUserid;
        }
        try {
            Map<String, String> head = new HashMap<>(1);
            head.put("familyid", familyid);

            JSONObject jParam = new JSONObject();
            jParam.put("familyid", familyid);
            jParam.put("mtag", mtag);
            jParam.put("mkeyid", mkeyid);

            String ret = httpAccessor.generalPost(BLApiUrls.Family.URL_FAMILY_PRIVATE_DATA_QUERY(), head, jParam.toString(), mHttpTimeOut);
            if (ret != null) {
                JSONObject jResult = new JSONObject(ret);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    JSONArray jDataList = jResult.optJSONArray("data");
                    if (jDataList != null) {
                        for(int i = 0 ; i < jDataList.length(); i ++){
                            JSONObject infoObject = jDataList.getJSONObject(i);

                            BLPrivateData data = new BLPrivateData();
                            data.setMkeyid(infoObject.optString("mkeyid", null));
                            data.setContent(infoObject.optString("content", null));
                            data.setIdversion(infoObject.optString("idversion", null));

                            result.getDataList().add(data);
                        }
                    }
                    result.setVersion(jResult.optString("version", null));
                }
            } else {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
            }


        } catch (Exception e) {

            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

}
