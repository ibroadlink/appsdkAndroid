package cn.com.broadlink.sdk.param.family;


/**
 * Created by zhujunjie on 2017/11/2.
 */

public class BLFamilyBaseInfo {
    private int shareFlag = 0;
    private String createUser;
    private BLFamilyInfo familyInfo;

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

    public BLFamilyInfo getFamilyInfo() {
        return familyInfo;
    }

    public void setFamilyInfo(BLFamilyInfo familyInfo) {
        this.familyInfo = familyInfo;
    }

}
