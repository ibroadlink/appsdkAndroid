package cn.com.broadlink.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.net.URLEncoder;
import java.util.List;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.account.BLAccountErrCode;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.result.account.BLOauthResult;

/**
 * Created by zhujunjie on 2017/8/2.
 */

final class BLOAuthImpl {
    private static final String response_type = "token";
    private static final String oauthAgentApp = "ihc://oauth/authenticate";

    /** Access Token凭证，用于后续访问各开放接口 */
    private String accessToken;
    /** 第三方应用在互联开放平台申请的clientID */
    private String clientId;

    /** Access Token的失效期 */
    private int expires_in;

    private String mRedirectURI;

    private Context mContext;

    public void init(Context context, BLConfigParam configParam) {
        mContext = context;
    }

    public Boolean authorize(String client_id, String redirectURI) {
        try {
            clientId = client_id;

            mRedirectURI = redirectURI;

            String encodeRedirectURI = new String(mRedirectURI.getBytes(), "UTF-8");
            encodeRedirectURI = URLEncoder.encode(encodeRedirectURI, "UTF-8");

            String schemes = "?response_type=" + response_type + "&client_id=" + clientId + "&redirect_uri=" + encodeRedirectURI;

            BLCommonTools.debug("schemes=" + schemes);
            if (hasApplication()) {
                String ihcUrl = oauthAgentApp + schemes;
                BLCommonTools.debug("ihcUrl=" + ihcUrl);
                Intent action = new Intent();
                action.setData(Uri.parse(ihcUrl));
                action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(action);
            } else {
                String httpUrl = BLApiUrls.Oauth.URL_OAUTH_LOGIN_WEB() + schemes;
                BLCommonTools.debug("httpUrl=" + httpUrl);
                Intent action = new Intent(Intent.ACTION_VIEW);
                action.setData(Uri.parse(httpUrl));
                action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(action);
            }

            return true;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return false;
    }

    public BLOauthResult handleOpenURL(Intent intent) {
        if (intent != null) {
            String intentAction = intent.getAction();
            if (Intent.ACTION_VIEW.equals(intentAction)) {
                //处理字符串 bl123456://?access_token=xxxxxxxx&expires_in=7776000
                Uri uri = intent.getData();
                accessToken = uri.getQueryParameter("access_token");
                String expires = uri.getQueryParameter("expires_in");

                BLOauthResult result = new BLOauthResult();
                if (accessToken != null && expires != null) {
                    expires_in = Integer.parseInt(expires);
                    result.setError(BLAppSdkErrCode.SUCCESS);
                    result.setMsg("success");
                    result.setAccessToken(accessToken);
                    result.setExpires_in(expires_in);
                } else {
                    String cancel = uri.getQueryParameter("cancel");
                    if (cancel != null) {
                        result.setError(BLAppSdkErrCode.ERR_OAUTH_CANCEL);
                        result.setMsg("The user is deauthorized");
                    }
                }
                return result;
            }
        }

        return null;
    }

    /**
     * 判断是否安装了应用
     * @return true 为已经安装
     */
    private boolean hasApplication() {
        PackageManager manager = mContext.getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse("ihc://"));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getExpires_in() {
        return expires_in;
    }

}
