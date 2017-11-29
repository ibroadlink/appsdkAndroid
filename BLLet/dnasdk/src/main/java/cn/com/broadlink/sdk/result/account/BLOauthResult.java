package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;

import java.util.Date;

/**
 * Created by zhujunjie on 2017/8/2.
 */

public class BLOauthResult extends BLBaseResult {
    private String accessToken;
    private int expires_in;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.accessToken);
        dest.writeInt(this.expires_in);
    }

    public BLOauthResult() {
    }

    protected BLOauthResult(Parcel in) {
        super(in);
        this.accessToken = in.readString();
        this.expires_in = in.readInt();
    }

    public static final Creator<BLOauthResult> CREATOR = new Creator<BLOauthResult>() {
        @Override
        public BLOauthResult createFromParcel(Parcel source) {
            return new BLOauthResult(source);
        }

        @Override
        public BLOauthResult[] newArray(int size) {
            return new BLOauthResult[size];
        }
    };

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}
