package cn.com.broadlink.sdk.result.family;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLManageRoomResult extends BLBaseResult {
    private String familyId;
    private String familyVersion;

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
}
