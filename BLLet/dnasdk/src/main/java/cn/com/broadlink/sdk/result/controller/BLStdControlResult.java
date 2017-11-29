package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.data.controller.BLStdData;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLStdControlResult extends BLBaseResult {
    private BLStdData data;

    public BLStdData getData() {
        return data;
    }

    public void setData(BLStdData data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
    }

    public BLStdControlResult() {
    }

    protected BLStdControlResult(Parcel in) {
        super(in);
        this.data = in.readParcelable(BLStdData.class.getClassLoader());
    }

    public static final Creator<BLStdControlResult> CREATOR = new Creator<BLStdControlResult>() {
        @Override
        public BLStdControlResult createFromParcel(Parcel source) {
            return new BLStdControlResult(source);
        }

        @Override
        public BLStdControlResult[] newArray(int size) {
            return new BLStdControlResult[size];
        }
    };
}
