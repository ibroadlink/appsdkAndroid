package cn.com.broadlink.sdk.param.account;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLModifyPhoneOrEmailParam {
    private String newPhoneOrEmail;
    private String countrycode;
    private String code;
    private String password;

    public BLModifyPhoneOrEmailParam(){}

    public BLModifyPhoneOrEmailParam(String newPhoneOrEmail, String code, String password){
        this(newPhoneOrEmail, null, code, password);
    }

    public BLModifyPhoneOrEmailParam(String newPhoneOrEmail, String countrycode, String code, String password){
        this.newPhoneOrEmail = newPhoneOrEmail;
        this.countrycode = countrycode;
        this.code = code;
        this.password = password;
    }

    public String getNewPhoneOrEmail() {
        return newPhoneOrEmail;
    }

    public void setNewPhoneOrEmail(String newPhoneOrEmail) {
        this.newPhoneOrEmail = newPhoneOrEmail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}
