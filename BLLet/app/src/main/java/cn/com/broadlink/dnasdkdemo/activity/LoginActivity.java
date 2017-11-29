package cn.com.broadlink.dnasdkdemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.result.account.BLLoginResult;

/**
 * Created by zhuxuyang on 16/1/20.
 */
public class LoginActivity extends Activity {
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.show();
                    }
                });

                final BLLoginResult result = BLLet.Account.login("13067914781", "123456");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        if(result.succeed()){
                            Intent intent = new Intent(LoginActivity.this, DeviceActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
//                /** 发送注册验证码 **/
//                BLLet.Account.sendRegVCode("18957132096", "+86");
//
//                /** 注册 **/
//                BLRegistParam registParam = new BLRegistParam();
//                registParam.setPhoneOrEmail("18957132096");
//                registParam.setPassword("12345678");
//                registParam.setNickname("xjf");
//                registParam.setCountrycode("+86");
//                registParam.setSex("female");
//                registParam.setIconpath("http://img5.imgtn.bdimg.com/it/u=894758686,1295828113&fm=11&gp=0.jpg");
//                registParam.setCode("1243");
//                BLLet.Account.regist(registParam, null);
//
//                /** 登陆 **/
//                BLLoginResult loginResult = BLLet.Account.login("18657107261", "zztc1247");
//
//                /** 修改昵称 **/
//                BLLet.Account.modifyUserNickname("newNickName");
//
//                /** 查询用户昵称和头像 **/
//                BLLet.Account.getUserInfo(new BLGetUserInfoParam().add(loginResult.getUserid()));
//
//                /** 修改用户头像 **/
//                File icon = new File(Environment.getExternalStorageDirectory().getPath() + "/broadlink/icon.png");
//                BLLet.Account.modifyUserIcon(icon);
//
//                /** 登陆用户修改密码 **/
//                BLLet.Account.modifyPassword("zztc1247", "12345678");
//
//                /** 登陆用户修改手机发送验证码 **/
//                BLLet.Account.sendModifyVCode("18657107261", "+86");
//
//                /** 登陆用户修改手机 **/
//                BLLet.Account.modifyPhoneOrEmail("18657107261", "+86", "3382", "12345678");
//
//                /** 登陆用户修改邮箱发送验证码 **/
//                BLLet.Account.sendModifyVCode("zztc1248@163.com");
//
//                /** 登陆用户修改邮箱 **/
//                BLLet.Account.modifyPhoneOrEmail("zztc1248@163.com", "8027", "12345678");
//
//                /** 通过手机找回密码发送验证码 **/
//                BLLet.Account.sendRetrieveVCode("18657107261");
//
//                /** 通过手机找回密码 **/
//                BLLet.Account.retrievePassword("18657107261", "4008", "12345678");
//
//                /** 通过邮箱找回密码发送验证码 **/
//                BLLet.Account.sendRetrieveVCode("zztc1248@163.com");
//
//                /** 通过邮箱找回密码 **/
//                BLLet.Account.retrievePassword("zztc1248@163.com", "2798", "zztc1247");
//
//                /** 检查用户名密码是否正确 **/
//                BLLet.Account.checkUserPassword("12345678");
//
//                /** 获取手机和邮箱 **/
//                BLLet.Account.getUserPhoneAndEmail();
            }
        }).start();
    }
}
