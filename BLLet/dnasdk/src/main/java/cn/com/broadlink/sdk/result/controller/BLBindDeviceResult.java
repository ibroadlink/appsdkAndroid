package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/18.
 */
public class BLBindDeviceResult extends BLBaseResult {
    private int[] bindmap;

    public int[] getBindmap() {
        return bindmap;
    }

    public void setBindmap(int[] bindmap) {
        this.bindmap = bindmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeIntArray(this.bindmap);
    }

    public BLBindDeviceResult() {
    }

    protected BLBindDeviceResult(Parcel in) {
        super(in);
        this.bindmap = in.createIntArray();
    }

    public static final Creator<BLBindDeviceResult> CREATOR = new Creator<BLBindDeviceResult>() {
        @Override
        public BLBindDeviceResult createFromParcel(Parcel source) {
            return new BLBindDeviceResult(source);
        }

        @Override
        public BLBindDeviceResult[] newArray(int size) {
            return new BLBindDeviceResult[size];
        }
    };
}
