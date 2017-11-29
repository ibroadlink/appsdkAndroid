package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLDeviceTimeResult extends BLBaseResult {
    private String time;
    private int difftime;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.time);
        dest.writeInt(this.difftime);
    }

    public BLDeviceTimeResult() {
    }

    protected BLDeviceTimeResult(Parcel in) {
        super(in);
        this.time = in.readString();
    }

    public int getDifftime() {
        return difftime;
    }

    public void setDifftime(int difftime) {
        this.difftime = difftime;
    }

    public static final Creator<BLDeviceTimeResult> CREATOR = new Creator<BLDeviceTimeResult>() {
        @Override
        public BLDeviceTimeResult createFromParcel(Parcel source) {
            return new BLDeviceTimeResult(source);
        }

        @Override
        public BLDeviceTimeResult[] newArray(int size) {
            return new BLDeviceTimeResult[size];
        }
    };

}
