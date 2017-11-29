package cn.com.broadlink.sdk.result.family;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyConfigedDevicesResult extends BLBaseResult {
    private List<String> didList = new ArrayList<String>();

    public List<String> getDidList() {
        return didList;
    }

    public void setDidList(List<String> didList) {
        this.didList = didList;
    }
}
