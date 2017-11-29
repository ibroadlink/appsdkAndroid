package cn.com.broadlink.sdk;

import cn.com.broadlink.sdk.result.account.BLLoginResult;

/**
 * 该接口用于其他模块监听账号模块的登陆动作
 * 需要在初始化时注册给账号模块
 */
interface BLAccountLoginListener {
    public void onLogin(BLLoginResult loginResult);
}
