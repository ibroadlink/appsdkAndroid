package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/18.
 */
public class BLDeviceConfigResult extends BLBaseResult {
    private String devaddr;
    private String mac;
    private String did;

    public String getDevaddr() {
        return devaddr;
    }

    public void setDevaddr(String devaddr) {
        this.devaddr = devaddr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.devaddr);
        dest.writeString(this.mac);
        dest.writeString(this.did);
    }

    public BLDeviceConfigResult() {
    }

    protected BLDeviceConfigResult(Parcel in) {
        super(in);
        this.devaddr = in.readString();
        this.mac = in.readString();
        this.did = in.readString();
    }

    public static final Creator<BLDeviceConfigResult> CREATOR = new Creator<BLDeviceConfigResult>() {
        @Override
        public BLDeviceConfigResult createFromParcel(Parcel source) {
            return new BLDeviceConfigResult(source);
        }

        @Override
        public BLDeviceConfigResult[] newArray(int size) {
            return new BLDeviceConfigResult[size];
        }
    };

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
