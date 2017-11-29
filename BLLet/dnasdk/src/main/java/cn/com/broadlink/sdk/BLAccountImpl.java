package cn.com.broadlink.sdk;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.account.BLAccountConstants;
import cn.com.broadlink.sdk.param.account.BLGetUserInfoParam;
import cn.com.broadlink.sdk.param.account.BLLoginParam;
import cn.com.broadlink.sdk.param.account.BLModifyPasswordParam;
import cn.com.broadlink.sdk.param.account.BLModifyPhoneOrEmailParam;
import cn.com.broadlink.sdk.param.account.BLModifyUserIconParam;
import cn.com.broadlink.sdk.param.account.BLModifyUserNicknameParam;
import cn.com.broadlink.sdk.param.account.BLRegistParam;
import cn.com.broadlink.sdk.param.account.BLRetrievePasswordParam;
import cn.com.broadlink.sdk.param.account.BLSendVCodeParam;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.result.account.BLBaseResult;
import cn.com.broadlink.sdk.result.account.BLGetUserInfoResult;
import cn.com.broadlink.sdk.result.account.BLGetUserPhoneAndEmailResult;
import cn.com.broadlink.sdk.result.account.BLLoginResult;
import cn.com.broadlink.sdk.result.account.BLModifyUserIconResult;
import cn.com.broadlink.sdk.result.account.BLOauthResult;

/**
 * Created by zhuxuyang on 16/2/18.
 */
final class BLAccountImpl {
    /**
     * 未知异常提示语
     **/
    private static final String ERR_UNKNOWN = "unknown error";

    /**
     * 未登录异常提示语
     **/
    private static final String ERR_NOT_LOGIN = "not login";

    /**
     * 服务没有返回结果
     */
    private static final String ERR_SERVER_NO_RESULT = "Server has no return data";

    /**
     * 密码SHA1加密后缀
     **/
    private static final String PASSWORD_ENCRYPT = "4969fj#k23#";

    /**
     * 存储公司id
     */
    private String mCompanyId;

    /**
     * 存储已登录userid
     */
    private String mUserid = null;

    /**
     * 存储已登录usersession
     */
    private String mUserserssion = null;
    /**
     * 存储License ID
     */
    private String mLicenseid = null;

    private String mLicense;

    /**
     * http超时时间设置
     **/
    private int mHttpTimeOut = 30000;

    private ArrayList<BLAccountLoginListener> mListLoginListener;

    public void init(String license, String companyid, String lid, BLConfigParam configParam) {
        mListLoginListener = new ArrayList<>();
        mCompanyId = companyid;
        mLicenseid = lid;
        mLicense = license;

        if (configParam != null) {
            // 初始化参数
            String timeout = configParam.get(BLConfigParam.ACCOUNT_HTTP_TIMEOUT);
            if (timeout == null) {
                timeout = configParam.get(BLConfigParam.HTTP_TIMEOUT);
            }

            if (timeout != null) {
                try {
                    mHttpTimeOut = Integer.parseInt(timeout);
                } catch (Exception e) {
                }
            }

            String accountHost = configParam.get(BLConfigParam.ACCOUNT_HOST);
            if (!TextUtils.isEmpty(accountHost)) {
                BLApiUrls.Account.setUrlBase(accountHost);
                Log.e("Account_URL_BASE", BLApiUrls.Account.URL_TIMESTAMP());
            }

            String oauthHost = configParam.get(BLConfigParam.OAUTH_HOST);
            if (!TextUtils.isEmpty(oauthHost)) {
                BLApiUrls.Oauth.setUrlBase(oauthHost);
            }
        }
    }

    /**
     * 登陆用户
     *
     * @param loginParam 用户对象
     * @return
     */
    public BLLoginResult login(BLLoginParam loginParam) {
        BLLoginResult loginResult = new BLLoginResult();

        // 判断参数是否合格
        if (loginParam == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("param null");

            return loginResult;
        } else if (TextUtils.isEmpty(loginParam.getUsername())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("username is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(loginParam.getPassword())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("password is empty");

            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();
            if (BLCommonTools.isEmail(loginParam.getUsername())) {
                jParam.put("email", loginParam.getUsername());
            } else if (BLCommonTools.isPhone(loginParam.getUsername())) {
                jParam.put("phone", loginParam.getUsername());
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                loginResult.setMsg("username format error");
                return loginResult;
            }

            // 密码加密
            jParam.put("password", BLCommonTools.SHA1(loginParam.getPassword() + PASSWORD_ENCRYPT));

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_LOGIN(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
//                    loginResult.setFlag(jResult.optString("flag", null));
                    loginResult.setCompanyid(jResult.optString("companyid", null));
                    loginResult.setFname(jResult.optString("fname", null));
                    loginResult.setLname(jResult.optString("lname", null));
                    loginResult.setUsertype(jResult.optString("usertype", null));
                    loginResult.setCountrycode(jResult.optString("countrycode", null));
                    loginResult.setPhone(jResult.optString("phone", null));
                    loginResult.setEmail(jResult.optString("email", null));
                    loginResult.setBirthday(jResult.optString("birthday", null));
                    loginResult.setPwdflag(jResult.optInt("pwdflag"));
                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }

    public BLLoginResult localLogin(BLLoginResult loginResult) {
        // 登录成功调用
        onLoginSucc(loginResult);

        return loginResult;
    }

    /**
     * 第三方授权账户登录
     *
     * @param thirdID 第三方授权ID
     * @return
     */
    public BLLoginResult thirdAuth(String thirdID) {
        BLLoginResult loginResult = new BLLoginResult();

        if (thirdID == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("thirdID null");

            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("thirdid", thirdID);
            jParam.put("companyid", mCompanyId);
            jParam.put("lid", mLicenseid);
            jParam.put("license", BLCommonTools.SHA1(mLicense));

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_THIRD_AUTH(), jParam.toString(), mHttpTimeOut);
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
//                    loginResult.setFlag(jResult.optString("flag", null));
                    loginResult.setCompanyid(jResult.optString("companyid", null));
                    loginResult.setFname(jResult.optString("fname", null));
                    loginResult.setLname(jResult.optString("lname", null));
                    loginResult.setUsertype(jResult.optString("usertype", null));
                    loginResult.setCountrycode(jResult.optString("countrycode", null));
                    loginResult.setPhone(jResult.optString("phone", null));
                    loginResult.setEmail(jResult.optString("email", null));
                    loginResult.setBirthday(jResult.optString("birthday", null));
                    loginResult.setPwdflag(jResult.optInt("pwdflag"));
                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }


    public BLLoginResult fastLogin(String phoneOrEmail, String countrycode, String vcode) {
        BLLoginResult loginResult = new BLLoginResult();

        if (phoneOrEmail == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("phone or email is empty");
            return loginResult;
        } else if (vcode == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("vcode is empty");
            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isPhone(phoneOrEmail)) {
                jParam.put("phone", phoneOrEmail);
                if (countrycode == null) {
                    loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    loginResult.setMsg("countrycode is empty");

                    return loginResult;
                }

                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else if (BLCommonTools.isEmail(phoneOrEmail)) {
                jParam.put("email", phoneOrEmail);
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                loginResult.setMsg("username format error");
                return loginResult;
            }

            jParam.put("companyid", mCompanyId);
            jParam.put("code", vcode);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_FAST_LOGIN(), jParam.toString(), mHttpTimeOut);
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
//                    loginResult.setFlag(jResult.optString("flag", null));
                    loginResult.setCompanyid(jResult.optString("companyid", null));
                    loginResult.setFname(jResult.optString("fname", null));
                    loginResult.setLname(jResult.optString("lname", null));
                    loginResult.setUsertype(jResult.optString("usertype", null));
                    loginResult.setCountrycode(jResult.optString("countrycode", null));
                    loginResult.setPhone(jResult.optString("phone", null));
                    loginResult.setEmail(jResult.optString("email", null));
                    loginResult.setBirthday(jResult.optString("birthday", null));
                    loginResult.setPwdflag(jResult.optInt("pwdflag"));
                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }


    public BLLoginResult oauthLogin(String thirdType, String thirdID, String accesstoken, String topsign, String nickname, String iconUrl) {
        BLLoginResult loginResult = new BLLoginResult();

        if (thirdType == null || thirdID == null || accesstoken == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("Input params is empty");

            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("companyid", mCompanyId);
            jParam.put("thirdtype", thirdType);
            jParam.put("id", thirdID);
            jParam.put("accesstoken", accesstoken);
            if (topsign != null) {
                jParam.put("topsign", topsign);
            }
            if (nickname != null) {
                jParam.put("nickname", nickname);
            }
            if (iconUrl != null) {
                jParam.put("pic", iconUrl);
            }

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_OAUTH_LOGIN(), jParam.toString(), mHttpTimeOut);
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
                    loginResult.setCompanyid(jResult.optString("companyid", null));
                    loginResult.setFname(jResult.optString("fname", null));
                    loginResult.setLname(jResult.optString("lname", null));
                    loginResult.setUsertype(jResult.optString("usertype", null));
                    loginResult.setCountrycode(jResult.optString("countrycode", null));
                    loginResult.setPhone(jResult.optString("phone", null));
                    loginResult.setEmail(jResult.optString("email", null));
                    loginResult.setBirthday(jResult.optString("birthday", null));
                    loginResult.setPwdflag(jResult.optInt("pwdflag"));
                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }

    public BLOauthResult queryIhcAccessToken(String username, String password, String cliendId, String redirectUri) {
        BLOauthResult oauthResult = new BLOauthResult();

        // 判断参数是否合格
        if (username == null || password == null || cliendId == null || redirectUri == null) {
            oauthResult.setError(BLAppSdkErrCode.ERR_PARAM);
            oauthResult.setMsg("param null");

            return oauthResult;
        }

        try {
            String url = BLApiUrls.Oauth.URL_OAUTH_LOGIN_INFO() + "?response_type=token&client_id=" + cliendId + "&&redirect_uri=" + redirectUri;

            JSONObject jParam = new JSONObject();

            jParam.put("phone", username);
            jParam.put("password", password);

            String result = BLAccountHttpAccessor.get(url, jParam.toString(), null, mHttpTimeOut, new BLAccountTrustManager());
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                oauthResult.setError(jResult.optInt("status"));
                oauthResult.setMsg(jResult.optString("msg", null));

                if (oauthResult.succeed()) {
                    oauthResult.setAccessToken(jResult.optString("access_token", null));
                    oauthResult.setExpires_in(jResult.optInt("expires_in"));
                }

                return oauthResult;
            } else {
                oauthResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                oauthResult.setMsg(ERR_SERVER_NO_RESULT);

                return oauthResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            oauthResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            oauthResult.setMsg(e.toString());

            return oauthResult;
        }

    }


    public BLLoginResult loginWithIhc(String accessToken) {
        BLLoginResult loginResult = new BLLoginResult();

        // 判断参数是否合格
        if (accessToken == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("param null");

            return loginResult;
        }

        try {
            String url = BLApiUrls.Oauth.URL_OAUTH_LOGIN_DATA() + "?access_token=" + accessToken;

            String result = BLAccountHttpAccessor.get(url, null, null, mHttpTimeOut, new BLAccountTrustManager());
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setStatus(jResult.optInt("status"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
                    loginResult.setCompanyid(jResult.optString("companyid", null));
                    loginResult.setFname(jResult.optString("fname", null));
                    loginResult.setLname(jResult.optString("lname", null));
                    loginResult.setUsertype(jResult.optString("usertype", null));
                    loginResult.setCountrycode(jResult.optString("countrycode", null));
                    loginResult.setPhone(jResult.optString("phone", null));
                    loginResult.setEmail(jResult.optString("email", null));
                    loginResult.setBirthday(jResult.optString("birthday", null));
                    loginResult.setPwdflag(jResult.optInt("pwdflag"));
                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }

    /**
     * 获取手机验证码
     *
     * @param sendVCodeParam 获取验证码的参数
     * @return
     */
    public BLBaseResult sendRegVCode(BLSendVCodeParam sendVCodeParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (sendVCodeParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(sendVCodeParam.getPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("email", sendVCodeParam.getPhoneOrEmail());
            } else if (BLCommonTools.isPhone(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("phone", sendVCodeParam.getPhoneOrEmail());

                // 检查countrycode
                if (TextUtils.isEmpty(sendVCodeParam.getCountrycode())) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }

                String countrycode = sendVCodeParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("username format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_SEND_REG_VCODE(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }

    }

    /**
     * 获取快速登录验证码
     *
     * @param sendVCodeParam 获取验证码的参数
     * @return
     */
    public BLBaseResult sendFastVCode(BLSendVCodeParam sendVCodeParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (sendVCodeParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");
            return baseResult;
        } else if (TextUtils.isEmpty(sendVCodeParam.getPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");
            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isPhone(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("phone", sendVCodeParam.getPhoneOrEmail());
                if (sendVCodeParam.getCountrycode() == null) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }
                String countrycode = sendVCodeParam.getCountrycode();

                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else if (BLCommonTools.isEmail(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("email", sendVCodeParam.getPhoneOrEmail());
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("username format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_FAST_LOGIN_VCODE(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }

    }

    /**
     * 注册：邮箱注册或手机注册,根据username格式自动判断
     *
     * @param registParam
     * @param fileIcon
     * @return
     */
    public BLLoginResult regist(BLRegistParam registParam, File fileIcon) {
        BLLoginResult loginResult = new BLLoginResult();

        // 判断参数是否合格
        if (registParam == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("param null");

            return loginResult;
        } else if (TextUtils.isEmpty(registParam.getPhoneOrEmail())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("phone or email is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(registParam.getPassword())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("password is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(registParam.getNickname())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("nickname is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(registParam.getSex())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("sex is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(registParam.getCode())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("vcode is empty");

            return loginResult;
        } else if (fileIcon != null && !fileIcon.exists()) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("file not exists");

            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            // 校验账号格式
            if (BLCommonTools.isPhone(registParam.getPhoneOrEmail())) {
                jParam.put("phone", registParam.getPhoneOrEmail());
                jParam.put("type", "phone");

                if (TextUtils.isEmpty(registParam.getCountrycode())) {
                    loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    loginResult.setMsg("countrycode is empty");

                    return loginResult;
                }
                String countrycode = registParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else if (BLCommonTools.isEmail(registParam.getPhoneOrEmail())) {
                jParam.put("email", registParam.getPhoneOrEmail());
                jParam.put("type", "email");
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                loginResult.setMsg("phone format error");

                return loginResult;
            }

            String sex = registParam.getSex();
            if (!BLAccountConstants.Sex.MALE.equals(sex) && !BLAccountConstants.Sex.FEMALE.equals(sex)) {
                loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                loginResult.setMsg("unknown sex");

                return loginResult;
            }

            jParam.put("password", BLCommonTools.SHA1(registParam.getPassword() + PASSWORD_ENCRYPT));
            jParam.put("nickname", registParam.getNickname());
            jParam.put("sex", sex);

            jParam.put("preferlanguage", BLCommonTools.getLanguage());
            jParam.put("code", registParam.getCode());
            jParam.put("companyid", mCompanyId);

            if (!TextUtils.isEmpty(registParam.getCountry())) {
                jParam.put("country", registParam.getCountry());
            }

            if (!TextUtils.isEmpty(registParam.getIconpath())) {
                jParam.put("iconpath", registParam.getIconpath());
            }

            if (!TextUtils.isEmpty(registParam.getBirthday())) {
                JSONObject jTempObject = new JSONObject();
                jTempObject.put("birthday", registParam.getBirthday());
                jParam.put("completeinfo", jTempObject);
            }

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalMutipartPost(BLApiUrls.Account.URL_REGIST(), null, json, fileIcon, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
//                    loginResult.setFlag(jResult.optString("flag", null));

                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }

    }

    /**
     * 修改用户头像
     *
     * @param modifyUserIconParam
     * @param fileIcon
     * @return
     */
    public BLModifyUserIconResult modifyUserIcon(BLModifyUserIconParam modifyUserIconParam, File fileIcon) {
        BLModifyUserIconResult modifyUserIconResult = new BLModifyUserIconResult();

        // 判断参数是否合格
        if ((modifyUserIconParam == null || modifyUserIconParam.getIcon() == null) && fileIcon == null) {
            modifyUserIconResult.setError(BLAppSdkErrCode.ERR_PARAM);
            modifyUserIconResult.setMsg("no icon found");

            return modifyUserIconResult;
        } else if (modifyUserIconParam != null && modifyUserIconParam.getIcon() != null && fileIcon != null) {
            modifyUserIconResult.setError(BLAppSdkErrCode.ERR_PARAM);
            modifyUserIconResult.setMsg("multi icon found");

            return modifyUserIconResult;
        }

        if (fileIcon != null && !fileIcon.exists()) {
            modifyUserIconResult.setError(BLAppSdkErrCode.ERR_PARAM);
            modifyUserIconResult.setMsg("file not exists");

            return modifyUserIconResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            modifyUserIconResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            modifyUserIconResult.setMsg(ERR_NOT_LOGIN);

            return modifyUserIconResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (modifyUserIconParam != null && !TextUtils.isEmpty(modifyUserIconParam.getIcon())) {
                jParam.put("icon", modifyUserIconParam.getIcon());
            }

            jParam.put("userid", mUserid);

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalMutipartPost(BLApiUrls.Account.URL_MODIFY_ICON(), getLoginMap(), json, fileIcon, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                modifyUserIconResult.setError(jResult.optInt("error"));
                modifyUserIconResult.setMsg(jResult.optString("msg", null));

                if (modifyUserIconResult.succeed()) {
                    modifyUserIconResult.setIcon(jResult.optString("icon", null));
                }

                return modifyUserIconResult;
            } else {
                modifyUserIconResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                modifyUserIconResult.setMsg(ERR_SERVER_NO_RESULT);

                return modifyUserIconResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            modifyUserIconResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            modifyUserIconResult.setMsg(e.toString());

            return modifyUserIconResult;
        }

    }

    /**
     * 修改用户昵称
     *
     * @param modifyUserNicknameParam
     * @return
     */
    public BLBaseResult modifyUserNickname(BLModifyUserNicknameParam modifyUserNicknameParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (modifyUserNicknameParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        }

        // 判断参数
        if (modifyUserNicknameParam.getNickname() == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("nickname is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("userid", mUserid);
            jParam.put("nickname", modifyUserNicknameParam.getNickname());

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_MODIFY_NICKNAME(), getLoginMap(), json, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    public BLBaseResult modifyUserGenderBirthday(String gender, String birthday) {
        BLBaseResult baseResult = new BLBaseResult();

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("userid", mUserid);
            if (gender != null) {
                jParam.put("sex", gender);
            }
            if (birthday != null) {
                jParam.put("birthday", birthday);
            }

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_MODIFY_GENDER(), getLoginMap(), json, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 获取用户昵称头像
     *
     * @param getUserInfoParam
     * @return
     */
    public BLGetUserInfoResult getUserInfo(BLGetUserInfoParam getUserInfoParam) {
        BLGetUserInfoResult getUserInfoResult = new BLGetUserInfoResult();

        // 判断参数是否合格
        if (getUserInfoParam == null) {
            getUserInfoResult.setError(BLAppSdkErrCode.ERR_PARAM);
            getUserInfoResult.setMsg("param null");

            return getUserInfoResult;
        } else if (getUserInfoParam.getRequserid() == null || getUserInfoParam.getRequserid().isEmpty()) {
            getUserInfoResult.setError(BLAppSdkErrCode.ERR_PARAM);
            getUserInfoResult.setMsg("userid is empty");

            return getUserInfoResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            getUserInfoResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            getUserInfoResult.setMsg(ERR_NOT_LOGIN);

            return getUserInfoResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            JSONArray jArray = new JSONArray();
            for (String userid : getUserInfoParam.getRequserid()) {
                jArray.put(userid);
            }

            jParam.put("requserid", jArray);

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_GET_USERINFO(), getLoginMap(), json, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                getUserInfoResult.setError(jResult.optInt("error"));
                getUserInfoResult.setMsg(jResult.optString("msg", null));

                if (getUserInfoResult.succeed()) {
                    JSONArray jArrayResult = jResult.optJSONArray("info");
                    List<BLGetUserInfoResult.UserInfo> listUserInfo = new ArrayList<>();
                    for (int i = 0; i < jArrayResult.length(); i++) {
                        BLGetUserInfoResult.UserInfo userInfo = new BLGetUserInfoResult.UserInfo();
                        JSONObject jUserInfo = jArrayResult.optJSONObject(i);

                        userInfo.setUserid(jUserInfo.optString("userid", null));
                        userInfo.setNickname(jUserInfo.optString("nickname", null));
                        userInfo.setIcon(jUserInfo.optString("icon", null));

                        listUserInfo.add(userInfo);
                    }

                    getUserInfoResult.setInfo(listUserInfo);
                }

                return getUserInfoResult;
            } else {
                getUserInfoResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                getUserInfoResult.setMsg(ERR_SERVER_NO_RESULT);

                return getUserInfoResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            getUserInfoResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            getUserInfoResult.setMsg(e.toString());

            return getUserInfoResult;
        }
    }

    /**
     * 修改密码
     *
     * @param modifyPasswordParam
     * @return
     */
    public BLBaseResult modifyPassword(BLModifyPasswordParam modifyPasswordParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (modifyPasswordParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (modifyPasswordParam.getOldpassword() == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("old password is empty");

            return baseResult;
        } else if (modifyPasswordParam.getNewpassword() == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("new password is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("oldpassword", BLCommonTools.SHA1(modifyPasswordParam.getOldpassword() + PASSWORD_ENCRYPT));
            jParam.put("newpassword", BLCommonTools.SHA1(modifyPasswordParam.getNewpassword() + PASSWORD_ENCRYPT));

            String json = jParam.toString();

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_MODIFY_PASSWORD(), getLoginMap(), json, mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 设置快速登录用户密码
     *
     * @param modifyPhoneOrEmailParam 设置的相关参数
     * @return
     */
    public BLBaseResult setFastLoginPassword(BLModifyPhoneOrEmailParam modifyPhoneOrEmailParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (modifyPhoneOrEmailParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getCode())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("code is empty");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getPassword())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("password is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
                jParam.put("email", modifyPhoneOrEmailParam.getNewPhoneOrEmail());
            } else if (BLCommonTools.isPhone(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
                jParam.put("phone", modifyPhoneOrEmailParam.getNewPhoneOrEmail());
                if (modifyPhoneOrEmailParam.getCountrycode() == null) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }
                String countrycode = modifyPhoneOrEmailParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("phone or email format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);
            jParam.put("userid", mUserid);
            jParam.put("code", modifyPhoneOrEmailParam.getCode());
            jParam.put("password", BLCommonTools.SHA1(modifyPhoneOrEmailParam.getPassword() + PASSWORD_ENCRYPT));

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_FAST_LOGIN_MODIFY_PASSWORD(), getLoginMap(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }

    }

    /**
     * 发送快速登录用户设置密码验证码
     *
     * @param sendVCodeParam 获取验证码的参数
     * @return
     */
    public BLBaseResult sendFastLoginPasswordVCode(BLSendVCodeParam sendVCodeParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (sendVCodeParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(sendVCodeParam.getPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("email", sendVCodeParam.getPhoneOrEmail());
            } else if (BLCommonTools.isPhone(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("phone", sendVCodeParam.getPhoneOrEmail());
                if (sendVCodeParam.getCountrycode() == null) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }
                String countrycode = sendVCodeParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("phone or email format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_FAST_LOGIN_MODIFY_VCODE(), getLoginMap(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 发送修改手机邮箱验证码
     *
     * @param sendVCodeParam 获取验证码的参数
     * @return
     */
    public BLBaseResult sendModifyVCode(BLSendVCodeParam sendVCodeParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (sendVCodeParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(sendVCodeParam.getPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("email", sendVCodeParam.getPhoneOrEmail());
            } else if (BLCommonTools.isPhone(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("phone", sendVCodeParam.getPhoneOrEmail());
                if (sendVCodeParam.getCountrycode() == null) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }
                String countrycode = sendVCodeParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("phone or email format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_SEND_MODIFY_VCODE(), getLoginMap(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 修改手机或邮箱
     *
     * @param modifyPhoneOrEmailParam
     * @return
     */
    public BLBaseResult modifyPhoneOrEmail(BLModifyPhoneOrEmailParam modifyPhoneOrEmailParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (modifyPhoneOrEmailParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getCode())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("code is empty");

            return baseResult;
        } else if (TextUtils.isEmpty(modifyPhoneOrEmailParam.getPassword())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("password is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
                jParam.put("newemail", modifyPhoneOrEmailParam.getNewPhoneOrEmail());
            } else if (BLCommonTools.isPhone(modifyPhoneOrEmailParam.getNewPhoneOrEmail())) {
                jParam.put("newphone", modifyPhoneOrEmailParam.getNewPhoneOrEmail());

                // 检查countrycode
                if (modifyPhoneOrEmailParam.getCountrycode() == null) {
                    baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                    baseResult.setMsg("countrycode is empty");

                    return baseResult;
                }
                String countrycode = modifyPhoneOrEmailParam.getCountrycode();
                jParam.put("countrycode", countrycodeCorrecte(countrycode));
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("phone or email format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);
            jParam.put("userid", mUserid);
            jParam.put("code", modifyPhoneOrEmailParam.getCode());
            jParam.put("password", BLCommonTools.SHA1(modifyPhoneOrEmailParam.getPassword() + PASSWORD_ENCRYPT));

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_MODIFY_PHONE_OR_EMAIL(), getLoginMap(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 发送找回密码验证码
     *
     * @param sendVCodeParam 获取验证码的参数
     * @return
     */
    public BLBaseResult sendRetrieveVCode(BLSendVCodeParam sendVCodeParam) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (sendVCodeParam == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("param null");

            return baseResult;
        } else if (TextUtils.isEmpty(sendVCodeParam.getPhoneOrEmail())) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("phone or email is empty");

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("email", sendVCodeParam.getPhoneOrEmail());
            } else if (BLCommonTools.isPhone(sendVCodeParam.getPhoneOrEmail())) {
                jParam.put("phone", sendVCodeParam.getPhoneOrEmail());
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
                baseResult.setMsg("phone or email format error");
                return baseResult;
            }

            jParam.put("companyid", mCompanyId);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_SEND_RETRIEVE_PWD_VCODE(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 重置密码
     *
     * @param retrievePasswordParam
     * @return
     */
    public BLLoginResult retrievePassword(BLRetrievePasswordParam retrievePasswordParam) {
        BLLoginResult loginResult = new BLLoginResult();

        // 判断参数是否合格
        if (retrievePasswordParam == null) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("param null");

            return loginResult;
        } else if (TextUtils.isEmpty(retrievePasswordParam.getPhoneOrEmail())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("phone or email is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(retrievePasswordParam.getCode())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("code is empty");

            return loginResult;
        } else if (TextUtils.isEmpty(retrievePasswordParam.getNewpassword())) {
            loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
            loginResult.setMsg("newpassword is empty");

            return loginResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            if (BLCommonTools.isEmail(retrievePasswordParam.getPhoneOrEmail())) {
                jParam.put("email", retrievePasswordParam.getPhoneOrEmail());
            } else if (BLCommonTools.isPhone(retrievePasswordParam.getPhoneOrEmail())) {
                jParam.put("phone", retrievePasswordParam.getPhoneOrEmail());
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_PARAM);
                loginResult.setMsg("phone or email format error");
                return loginResult;
            }

            jParam.put("companyid", mCompanyId);
            jParam.put("code", retrievePasswordParam.getCode());
            jParam.put("newpassword", BLCommonTools.SHA1(retrievePasswordParam.getNewpassword() + PASSWORD_ENCRYPT));

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_RETRIEVE_PWD(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                loginResult.setError(jResult.optInt("error"));
                loginResult.setMsg(jResult.optString("msg", null));

                if (loginResult.succeed()) {
                    loginResult.setUserid(jResult.optString("userid", null));
                    loginResult.setNickname(jResult.optString("nickname", null));
                    loginResult.setIconpath(jResult.optString("iconpath", null));
                    loginResult.setLoginsession(jResult.optString("loginsession", null));
                    loginResult.setLoginip(jResult.optString("loginip", null));
                    loginResult.setLogintime(jResult.optString("logintime", null));
                    loginResult.setSex(jResult.optString("sex", null));
//                    loginResult.setFlag(jResult.optString("flag", null));

                    // 登录成功调用
                    onLoginSucc(loginResult);
                }

                return loginResult;
            } else {
                loginResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                loginResult.setMsg(ERR_SERVER_NO_RESULT);

                return loginResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            loginResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            loginResult.setMsg(e.toString());

            return loginResult;
        }
    }

    /**
     * 检查用户名密码是否正确
     *
     * @param password
     * @return
     */
    public BLBaseResult checkUserPassword(String password) {
        BLBaseResult baseResult = new BLBaseResult();

        // 判断参数是否合格
        if (TextUtils.isEmpty(password)) {
            baseResult.setError(BLAppSdkErrCode.ERR_PARAM);
            baseResult.setMsg("password is empty");

            return baseResult;
        }

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            baseResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            baseResult.setMsg(ERR_NOT_LOGIN);

            return baseResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("userid", mUserid);
            jParam.put("loginsession", mUserserssion);
            jParam.put("password", BLCommonTools.SHA1(password + PASSWORD_ENCRYPT));

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_CHECK_USER_PWD(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                baseResult.setError(jResult.optInt("error"));
                baseResult.setMsg(jResult.optString("msg", null));

                return baseResult;
            } else {
                baseResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                baseResult.setMsg(ERR_SERVER_NO_RESULT);

                return baseResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            baseResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            baseResult.setMsg(e.toString());

            return baseResult;
        }
    }

    /**
     * 获取用户手机和邮箱信息
     *
     * @return
     */
    public BLGetUserPhoneAndEmailResult getUserPhoneAndEmail() {
        BLGetUserPhoneAndEmailResult getUserPhoneAndEmailResult = new BLGetUserPhoneAndEmailResult();

        // 是否已登录
        if (mUserid == null || mUserserssion == null) {
            getUserPhoneAndEmailResult.setError(BLAppSdkErrCode.ERR_NOT_LOGIN);
            getUserPhoneAndEmailResult.setMsg(ERR_NOT_LOGIN);

            return getUserPhoneAndEmailResult;
        }

        try {
            JSONObject jParam = new JSONObject();

            jParam.put("userid", mUserid);
            jParam.put("loginsession", mUserserssion);

            String result = BLAccountHttpAccessor.generalPost(BLApiUrls.Account.URL_GET_PHONE_AND_EMAIL(), jParam.toString(), mHttpTimeOut);

            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                getUserPhoneAndEmailResult.setError(jResult.optInt("error"));
                getUserPhoneAndEmailResult.setMsg(jResult.optString("msg", null));

                if (getUserPhoneAndEmailResult.succeed()) {
                    getUserPhoneAndEmailResult.setUserid(jResult.optString("userid", null));
                    getUserPhoneAndEmailResult.setEmail(jResult.optString("email", null));
                    getUserPhoneAndEmailResult.setPhone(jResult.optString("phone", null));
                }

                return getUserPhoneAndEmailResult;
            } else {
                getUserPhoneAndEmailResult.setError(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                getUserPhoneAndEmailResult.setMsg(ERR_SERVER_NO_RESULT);

                return getUserPhoneAndEmailResult;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            getUserPhoneAndEmailResult.setError(BLAppSdkErrCode.ERR_UNKNOWN);
            getUserPhoneAndEmailResult.setMsg(e.toString());

            return getUserPhoneAndEmailResult;
        }

    }

    /**
     * 注册一个登陆回调函数
     *
     * @param listener
     */
    void addLoginListener(BLAccountLoginListener listener) {
        mListLoginListener.add(listener);
    }

    /**
     * 生成用户HEAD
     *
     * @return
     */
    private Map<String, String> getLoginMap() {
        if (mUserid != null && mUserserssion != null) {
            Map<String, String> mapSession = new HashMap<>(2);
            mapSession.put("USERID", mUserid);
            mapSession.put("LOGINSESSION", mUserserssion);

            return mapSession;
        } else {
            return null;
        }
    }

    /**
     * 登陆成功后调用
     *
     * @param loginResult
     */
    private void onLoginSucc(BLLoginResult loginResult) {
        // 缓存到本地
        mUserid = loginResult.getUserid();
        mUserserssion = loginResult.getLoginsession();

        // 通知其他模块
        if (mListLoginListener != null) {
            for (BLAccountLoginListener listener : mListLoginListener) {
                listener.onLogin(loginResult);
            }
        }
    }

    /**
     * 获取正确的contrycode字段
     * @param countrycode 输入的contrycode字段
     * @return 矫正过的countrycode
     */
    private String countrycodeCorrecte(String countrycode) {
        if (countrycode == null) {
            return null;
        }

        if (countrycode.startsWith("+")) {
            countrycode = countrycode.substring(1, countrycode.length());
        }

        String code = "0000" + countrycode;

        return code.substring(code.length() - 4, code.length());
    }
}
