package cn.com.broadlink.sdk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuxuyang on 15/11/3.
 */
final class BLPyramidTask {
    protected static final int CHECK_CONFIG = 1;
    protected static final int UPLOAD_DATA = 2;

    private BLDataManager mDataManager;
    private BLPyramidImpl mPyramidImpl;

    private int mHttpTimeout;

    public BLPyramidTask(Context context, BLPyramidImpl pyramidImpl, int httpTimeout){
        mDataManager = BLDataManager.getInstance(context);
        mPyramidImpl = pyramidImpl;

        mHttpTimeout = httpTimeout;
    }

    void sendMsg(int msg){
        if(msg == CHECK_CONFIG){
            updateConfig();
        }else if(msg == UPLOAD_DATA){
            uploadData();
        }
    }
    /**
     * 异步更新配置
     */
    private void updateConfig(){
        if(mPyramidImpl.mPyramidConfig.shouldUpdate()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jParam = new JSONObject();
                    try {
                        jParam.put("sign", mPyramidImpl.mSign);
                        jParam.put("bundle", mPyramidImpl.mBundle);
                        BLPyramidHttpAccessor accessor = new BLPyramidHttpAccessor();
                        String result = accessor.post(BLApiUrls.Pyramid.URL_UPLOAD_CONFIG(), jParam.toString(), mHttpTimeout);
                        Map<String, String> mapConfig = getConfigMap(result);
                        if(mapConfig != null) {
                            mPyramidImpl.mPyramidConfig.updateConfig(mapConfig);
                        }
                    } catch (JSONException e) {
                        BLCommonTools.handleError(e);
                    }
                }
            }).start();
        }
    }

    private void uploadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mDataManager){
                    List<BLPyramidDBData> listData = mDataManager.queryData(mPyramidImpl.mPyramidConfig.getMaxDataCount());
                    while(listData.size() != 0){
                        String json = wrapData(listData);
                        if(upload(json)){
                            deleteData(listData);
                        }else{
                            // 上传失败则终止上传
                            return;
                        }

                        // 不满最大值则说明已经读取完毕
                        if(listData.size() < mPyramidImpl.mPyramidConfig.getMaxDataCount()){
                            break;
                        }

                        // 继续读取
                        listData = mDataManager.queryData(mPyramidImpl.mPyramidConfig.getMaxDataCount());
                    }
                }
            }
        }).start();
    }

    /**
     * 构建数据内容
     * @param listData
     * @return
     */
    private String wrapData(List<BLPyramidDBData> listData){
        JSONObject jData = new JSONObject();
        JSONObject jPhone = new JSONObject();
        try {
            jData.put("sign", mPyramidImpl.mSign);
            jData.put("bundle", mPyramidImpl.mBundle);
            jData.put("channel", mPyramidImpl.mChannel);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            jData.put("uploadtime", format.format(new Date()));

            // Phone信息
            jPhone.put("brand", mPyramidImpl.mPhoneInfo.brand);
            jPhone.put("model", mPyramidImpl.mPhoneInfo.model);
            jPhone.put("os", mPyramidImpl.mPhoneInfo.os);
            jPhone.put("type", mPyramidImpl.mPhoneInfo.type);

            jData.put("phone", jPhone);

            // 组织数据
            JSONArray jArrayApp = new JSONArray();
            JSONArray jArrayEvent = new JSONArray();
            JSONArray jArrayPage = new JSONArray();
            JSONArray jArrayWifi = new JSONArray();
            JSONArray jArrayBluetooth = new JSONArray();
            for(int i = 0; i < listData.size(); i++){
                BLPyramidDBData data = listData.get(i);
                if(data.type == BLPyramidDBData.TYPE_APP){
                    jArrayApp.put(new JSONObject(data.data));
                }else if(data.type == BLPyramidDBData.TYPE_EVENT){
                    jArrayEvent.put(new JSONObject(data.data));
                }else if(data.type == BLPyramidDBData.TYPE_PAGE){
                    jArrayPage.put(new JSONObject(data.data));
                }else if(data.type == BLPyramidDBData.TYPE_WIFI){
                    jArrayWifi.put(new JSONObject(data.data));
                }else if(data.type == BLPyramidDBData.TYPE_BLUETOOTH){
                    jArrayBluetooth.put(new JSONObject(data.data));
                }
            }

            // 不为空则加入
            if(jArrayApp.length() != 0){
                jData.put("app", jArrayApp);
            } else {
                try {
                    JSONObject jApp = new JSONObject();
                    jApp.put("appVerCode", mPyramidImpl.mPhoneInfo.appVerCode);
                    jApp.put("appVerName", mPyramidImpl.mPhoneInfo.appVerName);
                    jApp.put("sdkVerName", mPyramidImpl.mPhoneInfo.sdkVerName);
                    jApp.put("language", mPyramidImpl.mPhoneInfo.language);
                    jApp.put("coordinate", mPyramidImpl.mPhoneInfo.coordinate);
                    jApp.put("net", mPyramidImpl.mPhoneInfo.net);
                    jApp.put("operator", mPyramidImpl.mPhoneInfo.operator);
                    jApp.put("start", mPyramidImpl.mAppStartTime);

                    jArrayApp.put(jApp);
                    jData.put("app", jArrayApp);
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }
            if(jArrayEvent.length() != 0){
                jData.put("event", jArrayEvent);
            }
            if(jArrayPage.length() != 0){
                jData.put("page", jArrayPage);
            }
            if(jArrayWifi.length() != 0){
                jData.put("wifi", jArrayWifi);
            }
            if(jArrayBluetooth.length() != 0){
                jData.put("bluetooth", jArrayBluetooth);
            }

            return jData.toString();
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            return null;
        }
    }

    /**
     * 上传数据
     * @param json
     * @return
     */
    private boolean upload(String json){
        BLPyramidHttpAccessor accessor = new BLPyramidHttpAccessor();
        String result = accessor.post(BLApiUrls.Pyramid.URL_UPLOAD_DATA(), json, mHttpTimeout);
        if(result != null){
            try {
                JSONObject jResult = new JSONObject(result);
                if(jResult.optInt("code") == 0){
                    return true;
                }
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        }
        return false;
    }

    /**
     * 从本地数据库删除已上传数据
     * @param listData
     */
    private void deleteData(List<BLPyramidDBData> listData){
        mDataManager.deleteData(listData);
    }

    /**
     * 把配置结果解析为map
     * @param result
     * @return
     */
    private Map<String, String> getConfigMap(String result){
        try {
            if(result != null){
                JSONObject jConfig = new JSONObject(result);
                if(jConfig.optInt("code") == 0){
                    JSONObject jData = jConfig.optJSONObject("data");
                    Map<String, String> mapConfig = new HashMap<String, String>();
                    Iterator<String> iter = jData.keys();
                    while(iter.hasNext()){
                        String key = iter.next();
                        mapConfig.put(key, jData.getString(key));
                    }
                    return mapConfig;
                }
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }
        return null;
    }
}
