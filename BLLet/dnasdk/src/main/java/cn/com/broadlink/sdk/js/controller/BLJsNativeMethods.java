package cn.com.broadlink.sdk.js.controller;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSFunction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.com.broadlink.sdk.BLAccountTrustManager;
import cn.com.broadlink.sdk.BLBaseHttpAccessor;
import cn.com.broadlink.sdk.js.interfacer.BLJSNativeInterfacer;

/**
 * Created by YeJin on 2016/8/26.
 */
public class BLJsNativeMethods extends JSFunction implements BLJSNativeInterfacer {

    public BLJsNativeMethods(JSContext ctx, final String methodName){
        super(ctx, methodName);
    }

    @Override
    public String appVersion() {
        return "1.2.3.4.5";
    }

    @Override
    public String nativeControl(String did, String sdid, String cmd) {
        return "nativeControl";
    }

    @Override
    public void BLLogDebug(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    public String httpRequest(String method, String url, String headerJson, Byte[] bodys) {
        Map<String, String> mapHead = null;
        if(!TextUtils.isEmpty(headerJson)){
            try {
                JSONObject jsonObject = new JSONObject(headerJson);
                mapHead = new HashMap<>();

                Iterator<String> jsonKeys = jsonObject.keys();
                while (jsonKeys.hasNext()){
                    String key = jsonKeys.next();
                    mapHead.put(key, String.valueOf(jsonObject.get(key)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        byte[] dataBytes = null;
        if(bodys != null){
            dataBytes = new byte[bodys.length];
            for(int i = 0; i < bodys.length; i ++){
                dataBytes[i] = bodys[i];
            }
        }

        if(method.equals(BLBaseHttpAccessor.HTTP_GET)){
            return BLBaseHttpAccessor.get(url, null, mapHead, BLBaseHttpAccessor.HTTP_TIMEOUT, new BLAccountTrustManager());
        }else if(method.equals(BLBaseHttpAccessor.HTTP_POST)){
            return BLBaseHttpAccessor.post(url, mapHead, dataBytes, BLBaseHttpAccessor.HTTP_TIMEOUT, new BLAccountTrustManager());
        }

        return null;
    }
}
