package cn.com.broadlink.sdk.result.controller;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/21.
 */
public class BLQueryResoureVersionResult extends BLBaseResult {

    private List<ResourceVersion> versions =  new ArrayList<>();

    public List<ResourceVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<ResourceVersion> versions) {
        this.versions = versions;
    }
}
