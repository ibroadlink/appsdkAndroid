package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLPassthroughResult extends BLBaseResult {
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByteArray(this.data);
    }

    public BLPassthroughResult() {
    }

    protected BLPassthroughResult(Parcel in) {
        super(in);
        this.data = in.createByteArray();
    }

    public static final Creator<BLPassthroughResult> CREATOR = new Creator<BLPassthroughResult>() {
        @Override
        public BLPassthroughResult createFromParcel(Parcel source) {
            return new BLPassthroughResult(source);
        }

        @Override
        public BLPassthroughResult[] newArray(int size) {
            return new BLPassthroughResult[size];
        }
    };
}
