package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class BLModifyUserIconResult extends BLBaseResult {
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.icon);
    }

    public BLModifyUserIconResult() {
    }

    protected BLModifyUserIconResult(Parcel in) {
        super(in);
        this.icon = in.readString();
    }

    public static final Creator<BLModifyUserIconResult> CREATOR = new Creator<BLModifyUserIconResult>() {
        @Override
        public BLModifyUserIconResult createFromParcel(Parcel source) {
            return new BLModifyUserIconResult(source);
        }

        @Override
        public BLModifyUserIconResult[] newArray(int size) {
            return new BLModifyUserIconResult[size];
        }
    };
}
