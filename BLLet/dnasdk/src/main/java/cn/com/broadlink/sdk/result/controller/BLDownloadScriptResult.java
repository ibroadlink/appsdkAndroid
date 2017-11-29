package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/21.
 */
public class BLDownloadScriptResult extends BLBaseResult {
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

    public BLDownloadScriptResult() {
    }

    protected BLDownloadScriptResult(Parcel in) {
        super(in);
        this.savePath = in.readString();
    }

    public static final Creator<BLDownloadScriptResult> CREATOR = new Creator<BLDownloadScriptResult>() {
        @Override
        public BLDownloadScriptResult createFromParcel(Parcel source) {
            return new BLDownloadScriptResult(source);
        }

        @Override
        public BLDownloadScriptResult[] newArray(int size) {
            return new BLDownloadScriptResult[size];
        }
    };
}
