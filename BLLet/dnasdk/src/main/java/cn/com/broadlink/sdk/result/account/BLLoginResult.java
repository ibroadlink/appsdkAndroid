package cn.com.broadlink.sdk.result.account;

import android.os.Parcel;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class BLLoginResult extends BLBaseResult {
    private String loginsession;

    private String userid;

    private String nickname;

    private String iconpath;

    private String loginip;

    private String logintime;

    private String sex;

    private String companyid;

    private String fname;

    private String lname;

    private String usertype;

    private String countrycode;

    private String phone;

    private String email;

    private String birthday;

    //    private String flag;

    private int pwdflag;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIconpath() {
        return iconpath;
    }

    public void setIconpath(String iconpath) {
        this.iconpath = iconpath;
    }

    public String getLoginsession() {
        return loginsession;
    }

    public void setLoginsession(String loginsession) {
        this.loginsession = loginsession;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLoginip() {
        return loginip;
    }

    public void setLoginip(String loginip) {
        this.loginip = loginip;
    }

    public String getLogintime() {
        return logintime;
    }

    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

//    public String getFlag() {
//        return flag;
//    }
//
//    public void setFlag(String flag) {
//        this.flag = flag;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.loginsession);
        dest.writeString(this.userid);
        dest.writeString(this.nickname);
        dest.writeString(this.iconpath);
        dest.writeString(this.loginip);
        dest.writeString(this.logintime);
        dest.writeString(this.sex);
//        dest.writeString(this.flag);
        dest.writeString(this.companyid);
        dest.writeString(this.fname);
        dest.writeString(this.lname);
        dest.writeString(this.usertype);
        dest.writeString(this.countrycode);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.birthday);
        dest.writeInt(this.pwdflag);
    }

    public BLLoginResult() {
    }

    protected BLLoginResult(Parcel in) {
        super(in);
        this.loginsession = in.readString();
        this.userid = in.readString();
        this.nickname = in.readString();
        this.iconpath = in.readString();
        this.loginip = in.readString();
        this.logintime = in.readString();
        this.sex = in.readString();
//        this.flag = in.readString();
        this.companyid = in.readString();
        this.fname = in.readString();
        this.lname = in.readString();
        this.usertype = in.readString();
        this.countrycode = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.birthday = in.readString();
        this.pwdflag = in.readInt();
    }

    public static final Creator<BLLoginResult> CREATOR = new Creator<BLLoginResult>() {
        @Override
        public BLLoginResult createFromParcel(Parcel source) {
            return new BLLoginResult(source);
        }

        @Override
        public BLLoginResult[] newArray(int size) {
            return new BLLoginResult[size];
        }
    };

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getPwdflag() {
        return pwdflag;
    }

    public void setPwdflag(int pwdflag) {
        this.pwdflag = pwdflag;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }
}
