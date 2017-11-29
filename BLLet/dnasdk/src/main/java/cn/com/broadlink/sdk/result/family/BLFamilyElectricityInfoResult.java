package cn.com.broadlink.sdk.result.family;

import java.util.List;

import cn.com.broadlink.sdk.param.family.BLFamilyElectricityInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyElectricityInfoResult extends BLBaseResult {
    private BLFamilyElectricityInfo electricityInfo;

    public BLFamilyElectricityInfo getElectricityInfo() {
        return electricityInfo;
    }

    public void setElectricityInfo(BLFamilyElectricityInfo electricityInfo) {
        this.electricityInfo = electricityInfo;
    }
}
