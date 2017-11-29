package cn.com.broadlink.dnasdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.com.broadlink.dnasdkdemo.R;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceScanListener;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceStateChangedListener;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;

public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initSDK();

        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void initSDK(){
        long start = System.currentTimeMillis();
        Log.d("_broadlink", "sdk init start: " + start);

        BLLet.DebugLog.on();

        BLConfigParam configParam = new BLConfigParam();
//        configParam.put(BLConfigParam.SDK_FILE_PATH, "/sdf/adsf");
        BLLet.init(LoadingActivity.this, "bO0svbHFxQnB6F1k2H1PTVEZcyIuInjYNmmEqHd2TZ2+c+Z5WqmuSUK3T3rUN2g+658xVwAAAADTGxz36w3DzAQdABAhGASJdUNFZ/NDpuqoJFduSypi1hJajXYYyUTSWHETF7nakKVdHPVjXPMmKKlZZiOH8ksJsEkbxXTfoUSQjDzWcfVjcAAAAAA=", "");

        BLLet.Controller.setOnDeviceScanListener(new BLDeviceScanListener() {
            @Override
            public void onDeviceUpdate(BLDNADevice device, boolean isNewDevice) {
                if(isNewDevice){
                    mApplication.mMapDevice.put(device.getDid(), device);
                }else{
                    Log.d("_broadinl", "update device");
                }
            }
        });

        BLLet.Controller.setOnDeviceStateChangedListener(new BLDeviceStateChangedListener() {
            @Override
            public void onChanged(String did, int state) {
                BLDNADevice device = mApplication.mMapDevice.get(did);
                if(device != null){
                    device.setState(state);
                }
            }
        });

        BLLet.Controller.startProbe();

        long end = System.currentTimeMillis();
        Log.d("_broadlink", "sdk init end: " + end);
        Log.d("_broadlink", "sdk init use: " + (end - start));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("_broadlink", "onDestroy");
        BLLet.finish();
    }
}
