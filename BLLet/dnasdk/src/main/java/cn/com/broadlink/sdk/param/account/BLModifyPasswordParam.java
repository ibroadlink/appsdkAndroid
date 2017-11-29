package cn.com.broadlink.sdk.param.account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLModifyPasswordParam {
    private String oldpassword;
    private String newpassword;

    public BLModifyPasswordParam(){}

    public BLModifyPasswordParam(String oldpassword, String newpassword){
        this.oldpassword = oldpassword;
        this.newpassword = newpassword;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
