package cn.com.broadlink.sdk.result.family;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyInviteQrcodeGetResult extends BLBaseResult {
    private String qrcode;

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
