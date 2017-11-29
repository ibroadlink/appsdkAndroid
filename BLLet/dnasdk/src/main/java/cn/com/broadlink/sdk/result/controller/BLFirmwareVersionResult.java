package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLFirmwareVersionResult extends BLSubdevResult implements Parcelable {
    private String version;

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
        dest.writeString(this.version);
    }

    public BLFirmwareVersionResult() {
    }

    protected BLFirmwareVersionResult(Parcel in) {
        this.version = in.readString();
    }

    public static final Parcelable.Creator<BLFirmwareVersionResult> CREATOR = new Parcelable.Creator<BLFirmwareVersionResult>() {
        @Override
        public BLFirmwareVersionResult createFromParcel(Parcel source) {
            return new BLFirmwareVersionResult(source);
        }

        @Override
        public BLFirmwareVersionResult[] newArray(int size) {
            return new BLFirmwareVersionResult[size];
        }
    };
}
