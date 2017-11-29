package cn.com.broadlink.sdk.result.controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.blirdaconlib.BLIrdaConProduct;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/4.
 */

public class BLIRCodeInfoResult extends BLBaseResult {
    private String infomation;
    private BLIrdaConProduct irdaInfo;

    public String getInfomation() {
        return infomation;
    }

    public void setInfomation(String infomation) {
        this.infomation = infomation;
    }

    public BLIrdaConProduct getIrdaInfo() {
        return irdaInfo;
    }

    public void setIrdaInfo(BLIrdaConProduct irdaInfo) {
        this.irdaInfo = irdaInfo;
    }

    public void setIrdaInfo(String infomation) {
        try {
            JSONObject jInfo = new JSONObject(infomation);
            this.irdaInfo.name = jInfo.optString("name", null);
            this.irdaInfo.min_temperature= jInfo.optInt("temp_min");
            this.irdaInfo.max_temperature = jInfo.optInt("temp_max");

            JSONArray statusArray = jInfo.getJSONArray("status");
            this.irdaInfo.status_count = statusArray.length();
            for (int i = 0; i < statusArray.length(); i++) {
                this.irdaInfo.status[i] = statusArray.getInt(i);
            }

            JSONArray modeArray = jInfo.getJSONArray("mode");
            this.irdaInfo.mode_count = modeArray.length();
            for (int i = 0; i < modeArray.length(); i++) {
                this.irdaInfo.mode[i] = modeArray.getInt(i);
            }

            JSONArray speedArray = jInfo.getJSONArray("speed");
            this.irdaInfo.windspeed_count = speedArray.length();
            for (int i = 0; i < speedArray.length(); i++) {
                this.irdaInfo.windspeed[i] = speedArray.getInt(i);
            }

            JSONArray directArray = jInfo.getJSONArray("direct");
            this.irdaInfo.windirect_count = directArray.length();
            for (int i = 0; i < directArray.length(); i++) {
                this.irdaInfo.windirect[i] = directArray.getInt(i);
            }

        } catch (Exception e) {

        }
    }
}
