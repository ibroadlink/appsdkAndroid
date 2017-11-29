package cn.com.broadlink.dnasdkdemo;

import android.app.Application;

import java.util.HashMap;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Created by zhuxuyang on 16/4/20.
 */
public class SDKApplication extends Application {
    public HashMap<String ,BLDNADevice> mMapDevice = new HashMap<>();
}
