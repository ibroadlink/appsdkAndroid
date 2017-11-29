package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/8/21.
 */

public class BLGatewayTranslateResult extends BLBaseResult {
    private String data;
    private int eventcode;
    private String[] keys;
    private String[] privatedatas;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.data);
        dest.writeInt(this.eventcode);
        dest.writeStringArray(this.keys);
        dest.writeStringArray(this.privatedatas);
    }

    public BLGatewayTranslateResult() {
    }

    protected BLGatewayTranslateResult(Parcel in) {
        super(in);
        this.data = in.readString();
        this.eventcode = in.readInt();
        this.keys = in.createStringArray();
        this.privatedatas = in.createStringArray();
    }

    public static final Creator<BLGatewayTranslateResult> CREATOR = new Creator<BLGatewayTranslateResult>() {
        @Override
        public BLGatewayTranslateResult createFromParcel(Parcel source) {
            return new BLGatewayTranslateResult(source);
        }

        @Override
        public BLGatewayTranslateResult[] newArray(int size) {
            return new BLGatewayTranslateResult[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getEventcode() {
        return eventcode;
    }

    public void setEventcode(int eventcode) {
        this.eventcode = eventcode;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String[] getPrivatedatas() {
        return privatedatas;
    }

    public void setPrivatedatas(String[] privatedatas) {
        this.privatedatas = privatedatas;
    }
}
