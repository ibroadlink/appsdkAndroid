package cn.com.broadlink.sdk.param.account;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLRetrievePasswordParam {
    private String phoneOrEmail;
    private String code;
    private String newpassword;

    public BLRetrievePasswordParam(){}

    public BLRetrievePasswordParam(String phoneOrEmail, String code, String newpassword){
        this.phoneOrEmail = phoneOrEmail;
        this.code = code;
        this.newpassword = newpassword;
    }

    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

    public void setPhoneOrEmail(String phoneOrEmail) {
        this.phoneOrEmail = phoneOrEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
