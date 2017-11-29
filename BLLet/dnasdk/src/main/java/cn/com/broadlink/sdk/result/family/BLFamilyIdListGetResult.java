package cn.com.broadlink.sdk.result.family;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLFamilyIdInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyIdListGetResult extends BLBaseResult {
    private List<BLFamilyIdInfo> idInfoList = new ArrayList<BLFamilyIdInfo>();

    public List<BLFamilyIdInfo> getIdInfoList() {
        return idInfoList;
    }

    public void setIdInfoList(List<BLFamilyIdInfo> idInfoList) {
        this.idInfoList = idInfoList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.idInfoList);
    }

    public BLFamilyIdListGetResult() {

    }


}
