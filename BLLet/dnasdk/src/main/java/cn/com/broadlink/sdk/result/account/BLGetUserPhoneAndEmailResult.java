package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class BLGetUserPhoneAndEmailResult extends BLBaseResult {
    private String userid;
    private String email;
    private String phone;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.userid);
        dest.writeString(this.email);
        dest.writeString(this.phone);
    }

    public BLGetUserPhoneAndEmailResult() {
    }

    protected BLGetUserPhoneAndEmailResult(Parcel in) {
        super(in);
        this.userid = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
    }

    public static final Creator<BLGetUserPhoneAndEmailResult> CREATOR = new Creator<BLGetUserPhoneAndEmailResult>() {
        @Override
        public BLGetUserPhoneAndEmailResult createFromParcel(Parcel source) {
            return new BLGetUserPhoneAndEmailResult(source);
        }

        @Override
        public BLGetUserPhoneAndEmailResult[] newArray(int size) {
            return new BLGetUserPhoneAndEmailResult[size];
        }
    };
}
