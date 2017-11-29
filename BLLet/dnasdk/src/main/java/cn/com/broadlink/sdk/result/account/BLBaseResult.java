package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;
import android.os.Parcelable;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class BLBaseResult implements Parcelable {
    private int error = -1;
    private int status = -1;

    private String msg;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
        this.status = error;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    // 判断是否成功
    public boolean succeed(){
        return error == BLAppSdkErrCode.SUCCESS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.error);
        dest.writeInt(this.status);
        dest.writeString(this.msg);
    }

    public BLBaseResult() {
    }

    protected BLBaseResult(Parcel in) {
        this.error = in.readInt();
        this.status = in.readInt();
        this.msg = in.readString();
    }

    public static final Parcelable.Creator<BLBaseResult> CREATOR = new Parcelable.Creator<BLBaseResult>() {
        @Override
        public BLBaseResult createFromParcel(Parcel source) {
            return new BLBaseResult(source);
        }

        @Override
        public BLBaseResult[] newArray(int size) {
            return new BLBaseResult[size];
        }
    };
}
