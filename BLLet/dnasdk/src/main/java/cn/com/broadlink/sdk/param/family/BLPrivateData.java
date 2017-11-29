package cn.com.broadlink.sdk.param.family;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhujunjie on 2017/8/24.
 */

public class BLPrivateData implements Parcelable {
    private String mkeyid;
    private String content;
    private String idversion = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mkeyid);
        dest.writeString(this.content);
        dest.writeString(this.idversion);
    }

    public BLPrivateData() {
    }

    protected BLPrivateData(Parcel in) {
        this.mkeyid = in.readString();
        this.content = in.readString();
        this.idversion = in.readString();
    }

    public static final Parcelable.Creator<BLPrivateData> CREATOR = new Parcelable.Creator<BLPrivateData>() {
        @Override
        public BLPrivateData createFromParcel(Parcel source) {
            return new BLPrivateData(source);
        }

        @Override
        public BLPrivateData[] newArray(int size) {
            return new BLPrivateData[size];
        }
    };

    public String getMkeyid() {
        return mkeyid;
    }

    public void setMkeyid(String mkeyid) {
        this.mkeyid = mkeyid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdversion() {
        return idversion;
    }

    public void setIdversion(String idversion) {
        this.idversion = idversion;
    }

}
