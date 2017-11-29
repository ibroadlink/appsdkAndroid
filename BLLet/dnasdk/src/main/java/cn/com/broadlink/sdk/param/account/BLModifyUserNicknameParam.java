package cn.com.broadlink.sdk.param.account;

/**
 * Created by zhuxuyang on 16/1/22.
 */
public class BLModifyUserNicknameParam {
    private String nickname;

    public BLModifyUserNicknameParam(){}

    public BLModifyUserNicknameParam(String nickname){
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
