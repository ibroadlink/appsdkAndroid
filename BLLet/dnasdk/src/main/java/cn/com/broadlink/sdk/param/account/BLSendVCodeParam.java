package cn.com.broadlink.sdk.param.account;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLSendVCodeParam {
    private String countrycode;
    private String phoneOrEmail;

    public BLSendVCodeParam(){}

    public BLSendVCodeParam(String phoneOrEmail){
        this(phoneOrEmail, null);
    }

    public BLSendVCodeParam(String phoneOrEmail, String countrycode){
        this.phoneOrEmail = phoneOrEmail;
        this.countrycode = countrycode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

    public void setPhoneOrEmail(String phoneOrEmail) {
        this.phoneOrEmail = phoneOrEmail;
    }
}
