package cn.com.broadlink.sdk.result.controller;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/21.
 */

public class BLBaseBodyResult extends BLBaseResult {
    private String responseBody;

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
