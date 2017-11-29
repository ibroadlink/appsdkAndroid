package cn.com.broadlink.sdk.result;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhuxuyang on 16/4/17.
 */
public class BLBaseResult implements Parcelable {
    public static final int SUCCESS = 0;
    private int status = -1;
    private String msg;

    public int getStatus() {
        return status;
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

    /**
     * 是否成功
     * @return 返回结果是否成功
     */
    public boolean succeed(){
        return status == SUCCESS;
    }

    public BLBaseResult() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeString(this.msg);
    }

    protected BLBaseResult(Parcel in) {
        this.status = in.readInt();
        this.msg = in.readString();
    }

    public static final Creator<BLBaseResult> CREATOR = new Creator<BLBaseResult>() {
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
