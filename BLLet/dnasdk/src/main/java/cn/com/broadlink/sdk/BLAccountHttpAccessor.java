package cn.com.broadlink.sdk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuxuyang on 16/1/21.
 */
final class BLAccountHttpAccessor extends BLBaseHttpAccessor {
    /**
     * MD5加密后缀
     **/
    public static final String ACCOUNT_BODY_ENCRYPT = "xgx3d*fe3478$ukx";
    public static final String ACCOUNT_TOKEN_ENCRYPT = "kdixkdqp54545^#*";

    /**
     * 普通post:先获取时间戳再整合数据发送
     *
     * @param addr
     * @param json
     * @return
     */
    public static String generalPost(String addr, String json, int httpTimeOut) {
        return generalPost(addr, null, json, httpTimeOut);
    }

    public static String generalPost(String addr, Map<String, String> head, String json, int httpTimeOut) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String key = BLCommonTools.md5(timestamp + ACCOUNT_TOKEN_ENCRYPT);

        // 加入http头
        Map<String, String> mapHead = new HashMap<>();
        mapHead.put("timestamp", timestamp);
        mapHead.put("token", BLCommonTools.md5(json + ACCOUNT_BODY_ENCRYPT));

        if (head != null && !head.isEmpty()) {
            for (String k : head.keySet()) {
                mapHead.put(k, head.get(k));
            }
        }

        BLCommonTools.debug("Json Param: " + json);
        // 返回父类post结果
        return post(addr, mapHead, BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(key), json), httpTimeOut, new BLAccountTrustManager());
    }

    /**
     * multiPost
     *
     * @param addr
     * @param head
     * @param text
     * @param fileIcon
     * @return
     */
    public static String generalMutipartPost(String addr, Map<String, String> head, String text, File fileIcon, int httpTimeOut) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String key = BLCommonTools.md5(timestamp + ACCOUNT_TOKEN_ENCRYPT);

        // 加入http头
        Map<String, String> mapHead = new HashMap<>();
        mapHead.put("timestamp", timestamp);
        mapHead.put("token", BLCommonTools.md5(text + ACCOUNT_BODY_ENCRYPT));

        if (head != null && !head.isEmpty()) {
            for (String k : head.keySet()) {
                mapHead.put(k, head.get(k));
            }
        }

        BLCommonTools.debug("text: " + text);

        Map<String, Object> mapData = new HashMap<>(2);
        mapData.put("text", BLCommonTools.aesNoPadding(BLCommonTools.parseStringToByte(key), text));
        if (fileIcon != null) {
            mapData.put("picdata", fileIcon);
        }

        // 返回父类post结果
        return multipartPost(addr, mapHead, mapData, httpTimeOut, new BLAccountTrustManager());
    }
}
