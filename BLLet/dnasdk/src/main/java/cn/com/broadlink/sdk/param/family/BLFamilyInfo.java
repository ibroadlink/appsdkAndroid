package cn.com.broadlink.sdk.param.family;

import org.json.JSONObject;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyInfo {
    private String familyId;            //家庭ID
    private String familyVersion;       //家庭版本信息
    private String familyIconPath;      //家庭Icon路径
    private String familyName;          //家庭名称
    private String familyDescription;   //家庭描述
    private String familyPostcode;      //家庭的邮编
    private String familyMailaddress;   //家庭的邮寄地址
    private String familyCountry;       //家庭所在国家
    private String familyProvince;      //家庭所在省份
    private String familyCity;          //家庭所在城市
    private String familyArea;          //家庭所在城区
    private int familyLimit;            //家庭的访问权限
    private int familyLongitude;        //家庭所在维度
    private int familyLatitude;         //家庭所在经度
    private int familyOrder;            //家庭序号

    public BLFamilyInfo() {

    }

    public BLFamilyInfo(JSONObject object) {
        if (object != null) {
            this.familyId = object.optString("familyid", null);
            this.familyVersion = object.optString("version", null);
            this.familyIconPath = object.optString("icon", null);
            this.familyName = object.optString("name", null);
            this.familyDescription = object.optString("description", null);
            this.familyPostcode = object.optString("postcode", null);
            this.familyMailaddress = object.optString("mailaddress", null);
            this.familyCountry = object.optString("country", null);
            this.familyProvince = object.optString("province", null);
            this.familyCity = object.optString("city", null);
            this.familyArea = object.optString("area", null);
            this.familyLimit = object.optInt("familylimit");
            this.familyLongitude = object.optInt("longitude");
            this.familyLatitude = object.optInt("latitude");
            this.familyOrder = object.optInt("order");
        }
    }

    public JSONObject toDictionary() {

        try {
            JSONObject object = new JSONObject();
            if (this.familyId != null) {
                object.put("familyid", this.familyId);
            }
            if (this.familyVersion != null) {
                object.put("version", this.familyVersion);
            }
            if (this.familyName != null) {
                object.put("name", this.familyName);
            }
            if (this.familyDescription != null) {
                object.put("description", this.familyDescription);
            }
            if (this.familyPostcode != null) {
                object.put("postcode", this.familyPostcode);
            }
            if (this.familyMailaddress != null) {
                object.put("mailaddress", this.familyMailaddress);
            }
            if (this.familyCountry != null) {
                object.put("country", this.familyCountry);
            }
            if (this.familyProvince != null) {
                object.put("province", this.familyProvince);
            }
            if (this.familyCity != null) {
                object.put("city", this.familyCity);
            }
            if (this.familyArea != null) {
                object.put("area", this.familyArea);
            }
            object.put("familylimit", this.familyLimit);
            object.put("longitude", this.familyLongitude);
            object.put("latitude", this.familyLatitude);
            object.put("order", this.familyOrder);

            return object;
        } catch (Exception e) {
            return null;
        }
    }

    public String getFamilyVersion() {
        return familyVersion;
    }

    public void setFamilyVersion(String familyVersion) {
        this.familyVersion = familyVersion;
    }

    public String getFamilyIconPath() {
        return familyIconPath;
    }

    public void setFamilyIconPath(String familyIconPath) {
        this.familyIconPath = familyIconPath;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyDescription() {
        return familyDescription;
    }

    public void setFamilyDescription(String familyDescription) {
        this.familyDescription = familyDescription;
    }

    public String getFamilyPostcode() {
        return familyPostcode;
    }

    public void setFamilyPostcode(String familyPostcode) {
        this.familyPostcode = familyPostcode;
    }

    public String getFamilyMailaddress() {
        return familyMailaddress;
    }

    public void setFamilyMailaddress(String familyMailaddress) {
        this.familyMailaddress = familyMailaddress;
    }

    public String getFamilyCountry() {
        return familyCountry;
    }

    public void setFamilyCountry(String familyCountry) {
        this.familyCountry = familyCountry;
    }

    public String getFamilyProvince() {
        return familyProvince;
    }

    public void setFamilyProvince(String familyProvince) {
        this.familyProvince = familyProvince;
    }

    public String getFamilyCity() {
        return familyCity;
    }

    public void setFamilyCity(String familyCity) {
        this.familyCity = familyCity;
    }

    public String getFamilyArea() {
        return familyArea;
    }

    public void setFamilyArea(String familyArea) {
        this.familyArea = familyArea;
    }

    public int getFamilyLimit() {
        return familyLimit;
    }

    public void setFamilyLimit(int familyLimit) {
        this.familyLimit = familyLimit;
    }

    public int getFamilyLongitude() {
        return familyLongitude;
    }

    public void setFamilyLongitude(int familyLongitude) {
        this.familyLongitude = familyLongitude;
    }

    public int getFamilyLatitude() {
        return familyLatitude;
    }

    public void setFamilyLatitude(int familyLatitude) {
        this.familyLatitude = familyLatitude;
    }

    public int getFamilyOrder() {
        return familyOrder;
    }

    public void setFamilyOrder(int familyOrder) {
        this.familyOrder = familyOrder;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
