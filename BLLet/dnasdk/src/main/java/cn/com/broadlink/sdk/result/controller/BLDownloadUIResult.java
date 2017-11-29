package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/21.
 */
public class BLDownloadUIResult extends BLBaseResult {
    private String savePath;

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.savePath);
    }

    public BLDownloadUIResult() {
    }

    protected BLDownloadUIResult(Parcel in) {
        super(in);
        this.savePath = in.readString();
    }

    public static final Creator<BLDownloadUIResult> CREATOR = new Creator<BLDownloadUIResult>() {
        @Override
        public BLDownloadUIResult createFromParcel(Parcel source) {
            return new BLDownloadUIResult(source);
        }

        @Override
        public BLDownloadUIResult[] newArray(int size) {
            return new BLDownloadUIResult[size];
        }
    };
}
