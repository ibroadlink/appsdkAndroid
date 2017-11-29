package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/7/25.
 */

public class BLAPConfigResult extends BLBaseResult {

    private String ssid;

    private String did;

    private String pid;

    private String devkey;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.ssid);
        dest.writeString(this.did);
        dest.writeString(this.pid);
        dest.writeString(this.devkey);
    }

    public BLAPConfigResult() {
    }

    protected BLAPConfigResult(Parcel in) {
        super(in);
        this.ssid = in.readString();
        this.did = in.readString();
        this.pid = in.readString();
        this.devkey = in.readString();
    }

    public static final Creator<BLAPConfigResult> CREATOR = new Creator<BLAPConfigResult>() {
        @Override
        public BLAPConfigResult createFromParcel(Parcel source) {
            return new BLAPConfigResult(source);
        }

        @Override
        public BLAPConfigResult[] newArray(int size) {
            return new BLAPConfigResult[size];
        }
    };

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDevkey() {
        return devkey;
    }

    public void setDevkey(String devkey) {
        this.devkey = devkey;
    }
}
