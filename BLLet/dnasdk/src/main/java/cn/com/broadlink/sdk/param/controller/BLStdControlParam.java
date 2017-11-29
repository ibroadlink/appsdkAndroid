package cn.com.broadlink.sdk.param.controller;

import cn.com.broadlink.sdk.data.controller.BLStdData;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLStdControlParam extends BLStdData {
    private String prop = "stdctrl";

    private String did;

    private String srv;

    private String password;

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getSrv() {
        return srv;
    }

    public void setSrv(String srv) {
        this.srv = srv;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
