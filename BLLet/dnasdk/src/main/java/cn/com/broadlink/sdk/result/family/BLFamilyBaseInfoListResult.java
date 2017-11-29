package cn.com.broadlink.sdk.result.family;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLFamilyBaseInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/11/2.
 */

public class BLFamilyBaseInfoListResult extends BLBaseResult {
    private List<BLFamilyBaseInfo> infoList = new ArrayList<BLFamilyBaseInfo>();

    public List<BLFamilyBaseInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<BLFamilyBaseInfo> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.infoList);
    }

    public BLFamilyBaseInfoListResult() {

    }
}
