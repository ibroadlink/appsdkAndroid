package cn.com.broadlink.dnasdkdemo.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import cn.com.broadlink.dnasdkdemo.SDKApplication;

/**
 * Created by zhuxuyang on 16/4/20.
 */
public class BaseActivity extends Activity {
    protected SDKApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (SDKApplication) getApplication();
    }
}
