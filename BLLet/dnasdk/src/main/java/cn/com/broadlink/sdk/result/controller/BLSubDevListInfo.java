package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Created by YeJin on 2016/6/25.
 */
public class BLSubDevListInfo implements Serializable, Parcelable {

    private static final long serialVersionUID = 5598723815720514152L;

    private int total; // 当前网关设备管理的所有子设备的数量
    private int index; // 该index由应用层设定，这里仅作返回

    private List<BLDNADevice> list = new ArrayList<BLDNADevice>();

    public List<BLDNADevice> getList() {
        return list;
    }

    public void setList(List<BLDNADevice> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeInt(this.index);
        dest.writeTypedList(this.list);
    }

    public BLSubDevListInfo() {
    }

    protected BLSubDevListInfo(Parcel in) {
        this.total = in.readInt();
        this.index = in.readInt();
        this.list = in.createTypedArrayList(BLDNADevice.CREATOR);
    }

    public static final Creator<BLSubDevListInfo> CREATOR = new Creator<BLSubDevListInfo>() {
        @Override
        public BLSubDevListInfo createFromParcel(Parcel source) {
            return new BLSubDevListInfo(source);
        }

        @Override
        public BLSubDevListInfo[] newArray(int size) {
            return new BLSubDevListInfo[size];
        }
    };
}
