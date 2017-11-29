package cn.com.broadlink.sdk.result.family;

import android.os.Parcel;

import cn.com.broadlink.sdk.param.family.BLFamilyInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyInfoResult extends BLBaseResult {
    private BLFamilyInfo familyInfo;

    public BLFamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(BLFamilyInfo familyInfo) {
        this.familyInfo = familyInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.familyInfo);
    }

    public BLFamilyInfoResult() {

    }
}
