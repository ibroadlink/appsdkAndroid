package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.data.controller.BLStdData;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLTaskDataResult extends BLBaseResult {
    private BLStdData data;
    private BLStdData data2;

    public BLStdData getData() {
        return data;
    }

    public void setData(BLStdData data) {
        this.data = data;
    }

    public BLStdData getData2() {
        return data2;
    }

    public void setData2(BLStdData data2) {
        this.data2 = data2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.data, flags);
        dest.writeParcelable(this.data2, flags);
    }

    public BLTaskDataResult() {
    }

    protected BLTaskDataResult(Parcel in) {
        super(in);
        this.data = in.readParcelable(BLStdData.class.getClassLoader());
        this.data2 = in.readParcelable(BLStdData.class.getClassLoader());
    }

    public static final Creator<BLTaskDataResult> CREATOR = new Creator<BLTaskDataResult>() {
        @Override
        public BLTaskDataResult createFromParcel(Parcel source) {
            return new BLTaskDataResult(source);
        }

        @Override
        public BLTaskDataResult[] newArray(int size) {
            return new BLTaskDataResult[size];
        }
    };

}
