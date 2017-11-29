package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/18.
 */
public class BLProfileStringResult extends BLBaseResult {
    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.profile);
    }

    public BLProfileStringResult() {
    }

    protected BLProfileStringResult(Parcel in) {
        super(in);
        this.profile = in.readString();
    }

    public static final Creator<BLProfileStringResult> CREATOR = new Creator<BLProfileStringResult>() {
        @Override
        public BLProfileStringResult createFromParcel(Parcel source) {
            return new BLProfileStringResult(source);
        }

        @Override
        public BLProfileStringResult[] newArray(int size) {
            return new BLProfileStringResult[size];
        }
    };
}
