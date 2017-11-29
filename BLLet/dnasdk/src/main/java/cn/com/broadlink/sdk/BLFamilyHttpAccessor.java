package cn.com.broadlink.sdk;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zjjllj on 2017/2/13.
 */

public class BLFamilyHttpAccessor extends BLBaseHttpAccessor {
    /**
     * MD5加密后缀
     **/
    private static final String FAMILY_TOKEN_KEY = "xgx3d*fe3478$ukx";
    /*
    * 上报数据限制
    * */
    private static final int UPDATE_ICON_LIMIT = 2014 * 512;
    private static final int UPDATE_KEY_TIMEINTERVAL = 2 * 60 * 60;

    private long mLastRequestTime;
    private String mKey;
    private String mUserid;
    private String mLoginsession;
    private String mLicenseId;

    private static BLFamilyHttpAccessor instance = null;
    private BLFamilyHttpAccessor() {

    }
    public static BLFamilyHttpAccessor getInstance() {
        synchronized (BLFamilyHttpAccessor.class) {
            if (instance == null) {
                instance = new BLFamilyHttpAccessor();
            }
        }
        return instance;
    }

    private boolean checkLocalKeyIsVaild() {

        long nowTimeInvterval = System.currentTimeMillis() / 1000;
        if (mKey != null && (nowTimeInvterval - mLastRequestTime < UPDATE_KEY_TIMEINTERVAL)) {
            return true;
        } else {
            int ret = updateKeyAndTimeIntervate();
            return (ret==0);
        }
    }

    private int updateKeyAndTimeIntervate() {
        String familyKeyUrl = BLApiUrls.Family.URL_KEY_ADN_TIMESTRATRAMP();

        try {
            String result = BLBaseHttpAccessor.get(familyKeyUrl, null, null, BLBaseHttpAccessor.HTTP_TIMEOUT, new BLAccountTrustManager());
            if (result != null) {
                JSONObject jResult = new JSONObject(result);
                int statue = jResult.optInt("error");
                if (statue == 0) {
                    mLastRequestTime = jResult.optLong("timestamp");
                    mKey = jResult.optString("key");
                }
                return statue;
            }
        } catch  (Exception e) {
            BLCommonTools.handleError(e);
        }
        return -1;
    }

    private Map<String, String>generateHead(String body, Map<String, String> head) {
        String token = body + FAMILY_TOKEN_KEY + String.valueOf(mLastRequestTime) + mUserid;

        Map<String, String> mapHead = new HashMap<>();
        mapHead.put("timestamp", String.valueOf(mLastRequestTime));
        mapHead.put("token", BLCommonTools.md5(token));
        mapHead.put("userid", mUserid);
        mapHead.put("loginsession", mLoginsession);
        mapHead.put("lid", mLicenseId);

        if (head != null && !head.isEmpty()) {
            for (String k : head.keySet()) {
                mapHead.put(k, head.get(k));
            }
        }

        return mapHead;
    }

    public String generalPost(String addr, Map<String, String> head, String json, int httpTimeOut) {

        if (!checkLocalKeyIsVaild()) {
            return null;
        }

        // 加入http头
        Map<String, String> mapHead = generateHead(json, head);

        BLCommonTools.debug("Json Param: " + json);
        // 返回父类post结果
        return post(addr, mapHead, BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(mKey), json), httpTimeOut, new BLAccountTrustManager());
    }

    public String generalMutipartPost(String addr, Map<String, String> head, String text, File fileIcon, int httpTimeOut) {

        if (!checkLocalKeyIsVaild()) {
            return null;
        }

        // 加入http头
        Map<String, String> mapHead = generateHead(text, head);

        BLCommonTools.debug("text: " + text);

        Map<String, Object> mapData = new HashMap<>(2);
        mapData.put("text", BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(mKey), text));
        if (fileIcon != null) {
            mapData.put("picdata", fileIcon);
        }

        // 返回父类post结果
        return multipartPost(addr, mapHead, mapData, httpTimeOut, new BLAccountTrustManager());
    }

    public void setmUserid(String mUserid) {
        this.mUserid = mUserid;
    }

    public void setmLoginsession(String mLoginsession) {
        this.mLoginsession = mLoginsession;
    }

    public void setmLicenseId(String mLicenseId) {
        this.mLicenseId = mLicenseId;
    }

}
