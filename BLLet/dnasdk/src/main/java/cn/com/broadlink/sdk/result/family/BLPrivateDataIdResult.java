package cn.com.broadlink.sdk.result.family;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/8/23.
 */

public class BLPrivateDataIdResult extends BLBaseResult {
    private String dataId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.dataId);
    }

    public BLPrivateDataIdResult() {
    }

    protected BLPrivateDataIdResult(Parcel in) {
        super(in);
        this.dataId = in.readString();
    }

    public static final Creator<BLPrivateDataIdResult> CREATOR = new Creator<BLPrivateDataIdResult>() {
        @Override
        public BLPrivateDataIdResult createFromParcel(Parcel source) {
            return new BLPrivateDataIdResult(source);
        }

        @Override
        public BLPrivateDataIdResult[] newArray(int size) {
            return new BLPrivateDataIdResult[size];
        }
    };

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }
}
