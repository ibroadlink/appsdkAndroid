package cn.com.broadlink.sdk.result.family;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLModuleControlResult extends BLBaseResult {
    private String moduleId;
    private String version;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
