package cn.com.broadlink.sdk.result.controller;

/**
 * Created by zhujunjie on 2017/8/11.
 */

public class BLQueryDeviceStatus {
    private String did;
    private int status;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
