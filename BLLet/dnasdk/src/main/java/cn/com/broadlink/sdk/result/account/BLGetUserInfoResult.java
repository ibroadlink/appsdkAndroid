package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class BLGetUserInfoResult extends BLBaseResult implements Parcelable {
    private List<UserInfo> info;

    public List<UserInfo> getInfo() {
        return info;
    }

    public void setInfo(List<UserInfo> info) {
        this.info = info;
    }

    public static class UserInfo implements Parcelable {
        private String userid;
        private String nickname;
        private String icon;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

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
            dest.writeString(this.userid);
            dest.writeString(this.nickname);
            dest.writeString(this.icon);
        }

        public UserInfo() {
        }

        protected UserInfo(Parcel in) {
            this.userid = in.readString();
            this.nickname = in.readString();
            this.icon = in.readString();
        }

        public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
            @Override
            public UserInfo createFromParcel(Parcel source) {
                return new UserInfo(source);
            }

            @Override
            public UserInfo[] newArray(int size) {
                return new UserInfo[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(info);
    }

    public BLGetUserInfoResult() {
    }

    protected BLGetUserInfoResult(Parcel in) {
        this.info = in.createTypedArrayList(UserInfo.CREATOR);
    }

    public static final Parcelable.Creator<BLGetUserInfoResult> CREATOR = new Parcelable.Creator<BLGetUserInfoResult>() {
        @Override
        public BLGetUserInfoResult createFromParcel(Parcel source) {
            return new BLGetUserInfoResult(source);
        }

        @Override
        public BLGetUserInfoResult[] newArray(int size) {
            return new BLGetUserInfoResult[size];
        }
    };
}
