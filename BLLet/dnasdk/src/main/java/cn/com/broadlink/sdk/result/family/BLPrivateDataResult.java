package cn.com.broadlink.sdk.result.family;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLPrivateData;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/8/23.
 */

public class BLPrivateDataResult extends BLBaseResult {

    private List<BLPrivateData> dataList = new ArrayList<>();

    private String version;

    public List<BLPrivateData> getDataList() {
        return dataList;
    }

    public void setDataList(List<BLPrivateData> dataList) {
        this.dataList = dataList;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dataList);
        dest.writeString(version);
    }

    public BLPrivateDataResult() {
    }

    protected BLPrivateDataResult(Parcel in) {
        this.dataList = in.createTypedArrayList(BLPrivateData.CREATOR);
        this.version = in.readString();
    }

    public static final Parcelable.Creator<BLPrivateDataResult> CREATOR = new Parcelable.Creator<BLPrivateDataResult>() {
        @Override
        public BLPrivateDataResult createFromParcel(Parcel source) {
            return new BLPrivateDataResult(source);
        }

        @Override
        public BLPrivateDataResult[] newArray(int size) {
            return new BLPrivateDataResult[size];
        }
    };
}
