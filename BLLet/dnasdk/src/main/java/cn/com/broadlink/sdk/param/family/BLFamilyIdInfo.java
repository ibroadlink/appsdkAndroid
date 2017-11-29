package cn.com.broadlink.sdk.param.family;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyIdInfo {
    private int shareFlag = 0;
    private String familyId;
    private String familyVersion;
    private String familyName;

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyVersion() {
        return familyVersion;
    }

    public void setFamilyVersion(String familyVersion) {
        this.familyVersion = familyVersion;
    }

    public int getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(int shareFlag) {
        this.shareFlag = shareFlag;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
