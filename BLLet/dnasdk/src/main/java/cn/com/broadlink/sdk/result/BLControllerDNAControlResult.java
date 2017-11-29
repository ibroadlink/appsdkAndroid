package cn.com.broadlink.sdk.result;

import org.json.JSONObject;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLControllerDNAControlResult extends BLBaseResult {
    private JSONObject data;
    private String cookie;

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

}
