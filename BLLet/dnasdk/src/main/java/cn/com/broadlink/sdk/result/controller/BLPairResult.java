package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhuxuyang on 16/4/18.
 */
public class BLPairResult extends BLBaseResult {
    /** 设备控制id **/
    private int id;
    /** 设备控制秘钥 **/
    private String key;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.id);
        dest.writeString(this.key);
    }

    public BLPairResult() {
    }

    protected BLPairResult(Parcel in) {
        super(in);
        this.id = in.readInt();
        this.key = in.readString();
    }

    public static final Creator<BLPairResult> CREATOR = new Creator<BLPairResult>() {
        @Override
        public BLPairResult createFromParcel(Parcel source) {
            return new BLPairResult(source);
        }

        @Override
        public BLPairResult[] newArray(int size) {
            return new BLPairResult[size];
        }
    };
}
