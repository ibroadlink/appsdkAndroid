package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by admin on 2017/10/20.
 */

public class BLSubdevResult extends BLBaseResult {
    private int subdevStatus;

    public int getSubdevStatus() {
        return subdevStatus;
    }

    public void setSubdevStatus(int subdevStatus) {
        this.subdevStatus = subdevStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.subdevStatus);
    }

    public BLSubdevResult() {
    }

    protected BLSubdevResult(Parcel in) {
        super(in);
        this.subdevStatus = in.readInt();
    }

    public static final Creator<BLSubdevResult> CREATOR = new Creator<BLSubdevResult>() {
        @Override
        public BLSubdevResult createFromParcel(Parcel source) {
            return new BLSubdevResult(source);
        }

        @Override
        public BLSubdevResult[] newArray(int size) {
            return new BLSubdevResult[size];
        }
    };

}
