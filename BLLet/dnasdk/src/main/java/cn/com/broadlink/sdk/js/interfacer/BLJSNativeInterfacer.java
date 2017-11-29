package cn.com.broadlink.sdk.js.interfacer;

/**
 * Created by YeJin on 2016/8/26.
 */
public interface BLJSNativeInterfacer {

    String appVersion();

    String nativeControl(String did, String sdid, String cmd);

    String httpRequest(String method, String url, String headerJson, Byte[] bodys);

    void BLLogDebug(String tag, String msg);
}
