package cn.com.broadlink.sdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by zjjllj on 2016/9/28.
 */

public class BLProxyHttpAccessor extends BLBaseHttpAccessor {
    public static String generalPost(String addr, Map<String, String> head, String json, int httpTimeOut) {
        String license = null;

        try {
            JSONObject jResult = new JSONObject(json);
            license = jResult.optString("license", null);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String signature = BLCommonTools.SHA1(json + timestamp + license);
        // 加入http头
        Map<String, String> mapHead = new HashMap<>();
        mapHead.put("timestamp", timestamp);
        mapHead.put("signature", signature);

        if (head != null && !head.isEmpty()) {
            for (String k : head.keySet()) {
                mapHead.put(k, head.get(k));
            }
        }

        BLCommonTools.debug("Json Param: " + json);
        // 返回父类post结果
        return post(addr, mapHead, (json == null) ? null : json.getBytes(), httpTimeOut, new BLAccountTrustManager());
    }
}
