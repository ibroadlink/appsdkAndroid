package cn.com.broadlink.sdk.param.account;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLGetUserInfoParam {
    private List<String> requserid;

    public BLGetUserInfoParam(){
        this.requserid = new ArrayList<>();
    }

    public BLGetUserInfoParam(List<String> requserid){
        this.requserid = requserid;
    }

    /**
     * 放入一个userid
     * @param userid
     */
    public BLGetUserInfoParam add(String userid){
        this.requserid.add(userid);
        return this;
    }

    public List<String> getRequserid() {
        return requserid;
    }

    public void setRequserid(List<String> requserid) {
        this.requserid = requserid;
    }
}
