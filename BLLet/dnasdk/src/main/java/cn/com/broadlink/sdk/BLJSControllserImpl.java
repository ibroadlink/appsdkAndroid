package cn.com.broadlink.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;
import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.io.File;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.js.controller.BLJsNativeMethods;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.result.BLControllerDNAControlResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;

/**
 * JS脚本控制类
 * Created by YeJin on 2016/8/26.
 */
public class BLJSControllserImpl {
    private BLLruCacheV4<String, String> mJSFileCache = new BLLruCacheV4<>(10 * 1024);
    private Context mContext;

    public BLJSControllserImpl(Context context){
        mContext = context;
    }

    private JSContext getJSContext(){
        return new JSContext();
    }

    /**
     * 注入native 方法给JS 使用
     * @param jsContext
     * @return
     */
    public JSContext initNativeToJS(JSContext jsContext){
        jsContext.property("appVersion", new BLJsNativeMethods(jsContext, "appVersion"));
        jsContext.property("nativeControl", new BLJsNativeMethods(jsContext, "nativeControl"));
        jsContext.property("httpRequest", new BLJsNativeMethods(jsContext, "httpRequest"));
        jsContext.property("BLLogDebug", new BLJsNativeMethods(jsContext, "BLLogDebug"));
        return jsContext;
    }

    public BLProfileStringResult getProfileAsStringByPid(String pid, String profilePath){
        BLProfileStringResult result = new BLProfileStringResult();

        String jsFilseStr = mJSFileCache.get(pid);

        if(TextUtils.isEmpty(jsFilseStr)){
            if(TextUtils.isEmpty(profilePath)){
                profilePath = BLFileStorageUtils.getDefaultJSScriptPath(pid);
            }

            //判断脚本文件是否存在
            File file = new File(profilePath);
            if(!file.exists()){
                result.setMsg("js script file not exists");
                result.setStatus(BLControllerErrCode.FILE_FAIL);
                return result;
            }

            jsFilseStr = BLFileUtils.readTextFileContent(profilePath);

            if(jsFilseStr != null){
                mJSFileCache.put(pid, jsFilseStr);
            }
        }

        //获取JS 文件内容失败
        if(TextUtils.isEmpty(jsFilseStr)){
            result.setMsg("js script file not exists");
            result.setStatus(BLControllerErrCode.FILE_FAIL);
            return result;
        }

        try {
            JSContext jsContext = getJSContext();

            //注入native 方法
            initNativeToJS(jsContext);

            //加载脚本
            jsContext.evaluateScript(jsFilseStr);

            JSValue jscontrolFunction = jsContext.property("profile");

            JSValue jsResult = jscontrolFunction.toFunction().call(null);

            if(!TextUtils.isEmpty(jsResult.toString())){
                result.setStatus(BLAppSdkErrCode.SUCCESS);
                result.setMsg("success");
                result.setProfile(jsResult.toString());
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("Error Unknown");
        }

        return result;
    }

    public BLControllerDNAControlResult dnaControl(BLProbeDevice device, BLProbeDevice subdevInfo, String dataStr, BLControllerDescParam desc, BLConfigParam configParam){
        String jDescStr = "{}";
        if(desc != null){
            jDescStr = desc.toJSONStr();
        }

        BLControllerDNAControlResult result = new BLControllerDNAControlResult();
        if(device == null){
            result.setMsg("cannot find device");
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            return result;
        }

        String pid = subdevInfo != null ? subdevInfo.getPid() : device.getPid();

        String jsFilseStr = mJSFileCache.get(pid);

        if(TextUtils.isEmpty(jsFilseStr)){
            String scriptPath = (configParam != null) ? configParam.get(BLConfigParam.CONTROLLER_SCRIPT_PATH) : null;
            if(TextUtils.isEmpty(scriptPath)){
                scriptPath = BLFileStorageUtils.getDefaultJSScriptPath(pid);
            }

            //判断脚本文件是否存在
            File file = new File(scriptPath);
            if(!file.exists()){
                //如果脚本不存在尝试重新下载脚本
                BLDownloadScriptResult downloadScriptResult = BLLet.Controller.downloadScript(pid);
                if(!downloadScriptResult.succeed()){
                    result.setMsg("js script file not exists");
                    result.setStatus(BLControllerErrCode.FILE_FAIL);

                    return result;
                }
            }

            jsFilseStr = BLFileUtils.readTextFileContent(scriptPath);

            if(jsFilseStr != null){
                mJSFileCache.put(pid, jsFilseStr);
            }
        }

        //获取JS 文件内容失败
        if(TextUtils.isEmpty(jsFilseStr)){
            result.setMsg("js script file not exists");
            result.setStatus(BLControllerErrCode.FILE_FAIL);
            return result;
        }

        try {
            JSContext jsContext = getJSContext();

            //注入native 方法
            initNativeToJS(jsContext);

            //加载脚本
            jsContext.evaluateScript(jsFilseStr);

            JSValue jscontrolFunction = jsContext.property("jscontrol");

            String subDevStr = (subdevInfo != null) ? subdevInfo.toJSONString() : "";

            Log.e("js control", "dataStr" + dataStr);
            JSValue jsResult = jscontrolFunction.toFunction().call(null, device.toJSONString(), subDevStr, dataStr, jDescStr);
            Log.e("js control", "result:" + jsResult.toString());

            JSONObject jResult = new JSONObject(jsResult.toString());
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
            if (result.getStatus() == 0) {
                result.setData(jResult.optJSONObject("data"));
                result.setCookie(jResult.optString("cookie", null));
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("Error Unknown");
        }

        return result;
    }

}
