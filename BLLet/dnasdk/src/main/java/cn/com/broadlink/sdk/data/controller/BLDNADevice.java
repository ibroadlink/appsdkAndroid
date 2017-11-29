package cn.com.broadlink.sdk.data.controller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhuxuyang on 16/4/15.
 */
public class BLDNADevice implements Cloneable, Parcelable {
    /** 子设备父类ID **/
    private String pDid;

    /** 设备的 id,用于区分每个不同的设备 **/
    private String did;
    /** 设备 mac 地址 **/
    private String mac;
    /** 设备的产品 id,用于归类同一产品的设备 **/
    private String pid;
    /** 设备名称 **/
    private String name;
    /** 设备类型 **/
    private int type;
    /** 设备是否锁定 **/
    private boolean lock;
    /** 是否新配置的设备 **/
    private boolean newconfig;
    /** 一代设备控制密码 **/
    private long password;
    /** 设备控制 id **/
    private int id;
    /** 设备控制密钥 **/
    private String key;
    /** 设备所在的房间 **/
    private int roomtype = -1;

    /** 设备状态 **/
    private int state;

    private String extend;

    private String lanaddr;

    private long freshStateTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isNewconfig() {
        return newconfig;
    }

    public void setNewconfig(boolean newconfig) {
        this.newconfig = newconfig;
    }

    public long getPassword() {
        return password;
    }

    public void setPassword(long password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getpDid() {
        return pDid;
    }

    public void setpDid(String pDid) {
        this.pDid = pDid;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getLanaddr() {
        return lanaddr;
    }

    public void setLanaddr(String lanaddr) {
        this.lanaddr = lanaddr;
    }

    public long getFreshStateTime() {
        return freshStateTime;
    }

    public void setFreshStateTime(long freshStateTime) {
        this.freshStateTime = freshStateTime;
    }

    public BLDNADevice copy(){
        try {
            return (BLDNADevice) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pDid);
        dest.writeString(this.did);
        dest.writeString(this.mac);
        dest.writeString(this.pid);
        dest.writeString(this.name);
        dest.writeInt(this.type);
        dest.writeByte(this.lock ? (byte) 1 : (byte) 0);
        dest.writeByte(this.newconfig ? (byte) 1 : (byte) 0);
        dest.writeLong(this.password);
        dest.writeInt(this.id);
        dest.writeString(this.key);
        dest.writeInt(this.state);
        dest.writeInt(this.roomtype);
        dest.writeString(this.extend);
        dest.writeString(this.lanaddr);
        dest.writeLong(this.freshStateTime);
    }

    public BLDNADevice() {
    }

    protected BLDNADevice(Parcel in) {
        this.pDid = in.readString();
        this.did = in.readString();
        this.mac = in.readString();
        this.pid = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.lock = in.readByte() != 0;
        this.newconfig = in.readByte() != 0;
        this.password = in.readLong();
        this.id = in.readInt();
        this.key = in.readString();
        this.state = in.readInt();
        this.roomtype = in.readInt();
        this.extend = in.readString();
        this.lanaddr = in.readString();
        this.freshStateTime = in.readLong();
    }

    public static final Creator<BLDNADevice> CREATOR = new Creator<BLDNADevice>() {
        @Override
        public BLDNADevice createFromParcel(Parcel source) {
            return new BLDNADevice(source);
        }

        @Override
        public BLDNADevice[] newArray(int size) {
            return new BLDNADevice[size];
        }
    };

    public int getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(int roomtype) {
        this.roomtype = roomtype;
    }
}
