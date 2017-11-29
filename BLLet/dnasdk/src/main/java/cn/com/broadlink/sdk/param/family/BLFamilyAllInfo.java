package cn.com.broadlink.sdk.param.family;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyAllInfo {
    private int shareFlag = 0;
    private String createUser;
    private BLFamilyInfo familyInfo;
    private List<BLFamilyRoomInfo> roomInfos = new ArrayList<BLFamilyRoomInfo>();
    private List<BLFamilyModuleInfo> moduleInfos = new ArrayList<BLFamilyModuleInfo>();
    private List<BLFamilyDeviceInfo> deviceInfos = new ArrayList<BLFamilyDeviceInfo>();
    private List<BLFamilyDeviceInfo> subDeviceInfos = new ArrayList<BLFamilyDeviceInfo>();

    public BLFamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(BLFamilyInfo familyInfo) {
        this.familyInfo = familyInfo;
    }

    public List<BLFamilyRoomInfo> getRoomInfos() {
        return roomInfos;
    }

    public void setRoomInfos(List<BLFamilyRoomInfo> roomInfos) {
        this.roomInfos = roomInfos;
    }

    public List<BLFamilyModuleInfo> getModuleInfos() {
        return moduleInfos;
    }

    public void setModuleInfos(List<BLFamilyModuleInfo> moduleInfos) {
        this.moduleInfos = moduleInfos;
    }

    public List<BLFamilyDeviceInfo> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(List<BLFamilyDeviceInfo> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public List<BLFamilyDeviceInfo> getSubDeviceInfos() {
        return subDeviceInfos;
    }

    public void setSubDeviceInfos(List<BLFamilyDeviceInfo> subDeviceInfos) {
        this.subDeviceInfos = subDeviceInfos;
    }

    public int getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(int shareFlag) {
        this.shareFlag = shareFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
