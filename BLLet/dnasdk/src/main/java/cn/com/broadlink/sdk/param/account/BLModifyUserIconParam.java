package cn.com.broadlink.sdk.param.account;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLModifyUserIconParam {
    private String icon;

    public BLModifyUserIconParam(){}

    public BLModifyUserIconParam(String icon){
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
