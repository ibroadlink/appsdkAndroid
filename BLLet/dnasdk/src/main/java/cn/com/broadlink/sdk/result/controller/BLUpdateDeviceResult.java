package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLUpdateDeviceResult extends BLBaseResult {
    private String name;
    private boolean lock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeByte(lock ? (byte) 1 : (byte) 0);
    }

    public BLUpdateDeviceResult() {
    }

    protected BLUpdateDeviceResult(Parcel in) {
        super(in);
        this.name = in.readString();
        this.lock = in.readByte() != 0;
    }

    public static final Creator<BLUpdateDeviceResult> CREATOR = new Creator<BLUpdateDeviceResult>() {
        @Override
        public BLUpdateDeviceResult createFromParcel(Parcel source) {
            return new BLUpdateDeviceResult(source);
        }

        @Override
        public BLUpdateDeviceResult[] newArray(int size) {
            return new BLUpdateDeviceResult[size];
        }
    };
}
