package cn.com.broadlink.sdk.result.family;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyInvitedQrcodePostResult extends BLBaseResult {
    private String familyId;
    private String familyName;
    private String familyIconPath;
    private String familyCreatorId;

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyIconPath() {
        return familyIconPath;
    }

    public void setFamilyIconPath(String familyIconPath) {
        this.familyIconPath = familyIconPath;
    }

    public String getFamilyCreatorId() {
        return familyCreatorId;
    }

    public void setFamilyCreatorId(String familyCreatorId) {
        this.familyCreatorId = familyCreatorId;
    }
}
