package cn.com.broadlink.sdk;

import org.json.JSONObject;

/**
 * Created by zhuxuyang on 16/4/20.
 */
public class BLControllerTokenResult {
    private int status;
    private String msg;
    private JSONObject data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
