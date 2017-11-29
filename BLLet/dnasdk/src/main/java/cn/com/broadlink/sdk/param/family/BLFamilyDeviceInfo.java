package cn.com.broadlink.sdk.param.family;

import org.json.JSONObject;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyDeviceInfo {
    private String familyId;
    private String roomId;
    private String did;
    private String sDid;
    private String mac;
    private String pid;
    private String name;
    private int password;
    private int type;
    private boolean lock;
    private String aeskey;
    private int terminalId;
    private int subdeviceNum;
    private String longitude;
    private String latitude;
    private String wifimac;
    private String extend;

    public BLFamilyDeviceInfo() {

    }

    public BLFamilyDeviceInfo(JSONObject object) {
        if (object != null) {
            this.familyId = object.optString("familyid", null);
            this.roomId = object.optString("roomId", null);
            this.did = object.optString("did", null);
            this.sDid = object.optString("sdid", null);
            this.mac = object.optString("mac", null);
            this.pid = object.optString("pid", null);
            this.name = object.optString("name", null);
            this.aeskey = object.optString("aeskey", null);
            this.password = object.optInt("password");
            this.type = object.optInt("devtype");
            this.terminalId = object.optInt("terminalid");
            this.subdeviceNum = object.optInt("subdevicenum");
            this.lock = object.optBoolean("lock");
        }
    }

    public JSONObject toDictionary() {

        try {
            JSONObject object = new JSONObject();
            if (this.familyId != null) {
                object.put("familyid", this.familyId);
            }
            if (this.roomId != null) {
                object.put("roomid", this.roomId);
            }
            if (this.did != null) {
                object.put("did", this.did);
            }
            if (this.sDid != null) {
                object.put("sdid", this.sDid);
            }
            if (this.mac != null) {
                object.put("mac", this.mac);
            }
            if (this.pid != null) {
                object.put("pid", this.pid);
            }
            if (this.name != null) {
                object.put("name", this.name);
            }
            if (this.aeskey != null) {
                object.put("aeskey", this.aeskey);
            }

            object.put("password", this.password);
            object.put("devtype", this.type);
            object.put("terminalid", this.terminalId);
            object.put("subdevicenum", this.subdeviceNum);

            return object;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getsDid() {
        return sDid;
    }

    public void setsDid(String sDid) {
        this.sDid = sDid;
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

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
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

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }

    public int getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(int terminalId) {
        this.terminalId = terminalId;
    }

    public int getSubdeviceNum() {
        return subdeviceNum;
    }

    public void setSubdeviceNum(int subdeviceNum) {
        this.subdeviceNum = subdeviceNum;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getWifimac() {
        return wifimac;
    }

    public void setWifimac(String wifimac) {
        this.wifimac = wifimac;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
