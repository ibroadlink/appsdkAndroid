package cn.com.broadlink.sdk.result.family;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLFamilyAllInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLAllFamilyInfoResult extends BLBaseResult {
    private List<BLFamilyAllInfo> allInfos = new ArrayList<BLFamilyAllInfo>();

    public List<BLFamilyAllInfo> getAllInfos() {
        return allInfos;
    }

    public void setAllInfos(List<BLFamilyAllInfo> allInfos) {
        this.allInfos = allInfos;
    }
}
