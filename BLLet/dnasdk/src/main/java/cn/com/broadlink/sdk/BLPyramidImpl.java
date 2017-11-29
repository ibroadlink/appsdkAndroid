package cn.com.broadlink.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.sdk.param.controller.BLConfigParam;

/**
 * Created by zhuxuyang on 15/11/3.
 */
final class BLPyramidImpl {
    private Context mContext;
    protected String mLicense;
    protected String mChannel;
    protected String mLid;
    protected BLPyramidConfig mPyramidConfig;
    private BLPyramidTask mPyramidTask;
    protected BLPhoneInfo mPhoneInfo;
    private Timer mTimerUpload;
    private BLDataManager mDataManager;
    private boolean isStartPick;

    /** http超时时间设置 **/
    private int mHttpTimeOut = 30000;

    /**
     * 参数区
     */
    protected String mSign;
    protected String mBundle;
    /**
     * APP信息记录区
     **/
    protected String mAppStartTime;
    /**
     * PAGE信息记录区
     **/
    private static Map<String, BLPageData> mMapPage;

    private SimpleDateFormat mFormat;
    /**
     * 初始化方法，在App main Activity 启动时调用，如果appId未定义在AndroidManifest中，可调用此方法传入
     *
     * @param context
     * @param license
     */
    public void init(Context context, String license, String channel, String lid, BLConfigParam configParam) {
        mContext = context;
        mLicense = license;
        mChannel = channel;
        mLid = lid;

        // 初始化参数
        String timeout = configParam.get(BLConfigParam.PICKER_HTTP_TIMEOUT);
        if(timeout == null){
            timeout = configParam.get(BLConfigParam.HTTP_TIMEOUT);
        }

        if(timeout != null){
            try {
                mHttpTimeOut = Integer.parseInt(timeout);
            }catch (Exception e){}
        }

        String pickMode = configParam.get(BLConfigParam.PICKER_SYSTEM_MODE);
        if (!TextUtils.isEmpty(pickMode)) {
            int mode = Integer.parseInt(pickMode);
            if (mode == 1) {
                String host = String.format("https://%sapplog.ibroadlink.com", mLid);
                String param = "?source=app&datatype=app_user_v2";

                BLApiUrls.Pyramid.setUrlBase(host);
                BLApiUrls.Pyramid.setUrlParams(param);
            }
        }

        String pickerHost = configParam.get(BLConfigParam.PICKER_HOST);
        if (!TextUtils.isEmpty(pickerHost)) {
            BLApiUrls.Pyramid.setUrlBase(pickerHost);
        }

        mPyramidConfig = new BLPyramidConfig(mContext);
        mPyramidTask = new BLPyramidTask(mContext, this, mHttpTimeOut);
        mPhoneInfo = new BLPhoneInfo();
        mDataManager = BLDataManager.getInstance(mContext);
        mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mAppStartTime = mFormat.format(new Date());
        isStartPick = false;

        // 计算sign
        generateSign();
        // 读取手机状态
        getPhoneState();
    }

    public void startPick() {
        //Crash日志收集
        BLCatchCrash catchCrash = BLCatchCrash.getInstance();
        catchCrash.init(mContext);

        // 更新配置
        //updateConfig();
        // 开始周期上传
        startUpload();
    }

    /**
     * 在App 结束时调用
     */
    public void finish() {
        // 停止周期上传
        stopUpload();

        // 保存数据
        try {
            JSONObject jPhone = new JSONObject();
            jPhone.put("appVerCode", mPhoneInfo.appVerCode);
            jPhone.put("appVerName", mPhoneInfo.appVerName);
            jPhone.put("sdkVerName", mPhoneInfo.sdkVerName);
            jPhone.put("language", mPhoneInfo.language);
            jPhone.put("coordinate", mPhoneInfo.coordinate);
            jPhone.put("net", mPhoneInfo.net);
            jPhone.put("operator", mPhoneInfo.operator);
            jPhone.put("start", mAppStartTime);
            jPhone.put("finish", mFormat.format(new Date()));

            BLPyramidDBData pyramidDbData = new BLPyramidDBData();
            pyramidDbData.type = BLPyramidDBData.TYPE_APP;
            pyramidDbData.data = jPhone.toString();

            mDataManager.putData(pyramidDbData);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        mDataManager.close();

        mPyramidTask.sendMsg(BLPyramidTask.UPLOAD_DATA);
    }

    /**
     * 跟踪页面使用情况方法，在Activity的onCreate方法中调用
     *
     * @param activity
     */
    public void onCreate(Activity activity) {
        try {
            if (mMapPage == null) {
                mMapPage = new HashMap<String, BLPageData>();
            }
            String key = activity.toString();

            BLPageData pageData = new BLPageData();
            pageData.start = System.currentTimeMillis();
            pageData.pageName = key.split("@")[0];
            pageData.resumeTime = 0;
            pageData.useTime = 0;

            mMapPage.put(key, pageData);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * 跟踪页面使用情况方法，在Activity的onDestroy方法中调用
     *
     * @param activity
     */
    public void onDestroy(Activity activity) {
        try {
            if (mMapPage != null) {
                String key = activity.toString();
                BLPageData pageData = mMapPage.get(key);
                if (pageData != null) {
                    JSONObject jPage = new JSONObject();
                    jPage.put("start", pageData.start);
                    jPage.put("finish", System.currentTimeMillis());
                    jPage.put("pageName", pageData.pageName);
                    jPage.put("useTime", String.valueOf(pageData.useTime / 1000));

                    BLPyramidDBData pyramidDbData = new BLPyramidDBData();
                    pyramidDbData.type = BLPyramidDBData.TYPE_PAGE;
                    pyramidDbData.data = jPage.toString();

                    mDataManager.putData(pyramidDbData);

                    mMapPage.remove(key);
                }
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * 跟踪页面使用情况方法，在Activity的onResume方法中调用
     *
     * @param activity
     */
    public void onResume(Activity activity) {
        try {
            if (mMapPage != null) {
                String key = activity.toString();
                BLPageData pageData = mMapPage.get(key);
                if (pageData != null) {
                    pageData.resumeTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * 跟踪页面使用情况方法，在Activity的onPause方法中调用
     *
     * @param activity
     */
    public void onPause(Activity activity) {
        try {
            if (mMapPage != null) {
                String key = activity.toString();
                BLPageData pageData = mMapPage.get(key);
                if (pageData != null) {
                    pageData.useTime += System.currentTimeMillis() - pageData.resumeTime;
                }
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * 需要追踪的事件，如果事件内需要加入数据，可调用此方法
     *
     * @param eventId
     * @param eventTag
     * @param data
     */
    public void onEvent(String eventId, String eventTag, Map<String, Object> data) {
        try {
            if (mDataManager != null && isStartPick) {
                JSONObject jEvent = new JSONObject();
                jEvent.put("type", eventId);
                jEvent.put("time", mFormat.format(new Date()));
                jEvent.put("tag", eventTag);
                if (data != null && data.size() != 0) {
                    JSONObject jData = new JSONObject();
                    for (String key : data.keySet()) {
                        jData.put(key, data.get(key));
                    }
                    jEvent.put("data", jData);
                }

                BLPyramidDBData pyramidDbData = new BLPyramidDBData();
                pyramidDbData.type = BLPyramidDBData.TYPE_EVENT;
                pyramidDbData.data = jEvent.toString();

                mDataManager.putData(pyramidDbData);
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }
    }

    private void generateSign() {
        mBundle = mContext.getPackageName();
        mSign = calDataSign(mLicense, mBundle);
    }

    /**
     * 读取手机的信息
     */
    private void getPhoneState() {
        // 获取手机信息
        mPhoneInfo.operator = "";
        mPhoneInfo.brand = Build.BRAND;
        mPhoneInfo.model = Build.MODEL;
        mPhoneInfo.os = Build.VERSION.SDK;

        // 部分操作放入线程减少阻塞时间
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取app信息
                try {
                    PackageManager packageManager = mContext.getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                    mPhoneInfo.appVerCode = String.valueOf(packageInfo.versionCode);
                    mPhoneInfo.appVerName = packageInfo.versionName;
                    mPhoneInfo.sdkVerName = BLLet.getSDKVersion();
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }

                // 获取网络信息
                ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        mPhoneInfo.net = "WIFI";
                    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
                            mPhoneInfo.net = "CDMA";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                            mPhoneInfo.net = "EDGE";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                            mPhoneInfo.net = "EVDO0";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                            mPhoneInfo.net = "EVDOA";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                            mPhoneInfo.net = "GPRS";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA) {
                            mPhoneInfo.net = "HSDPA";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA) {
                            mPhoneInfo.net = "HSPA";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA) {
                            mPhoneInfo.net = "HSUPA";
                        } else if (networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS) {
                            mPhoneInfo.net = "UMTS";
                        }
                    }
                }

                // 获取地理位置
                try {
                    LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                    List<String> providers = locationManager.getProviders(true);
                    String locationProvider = null;
                    if (providers.contains(LocationManager.GPS_PROVIDER)) {
                        //如果是GPS
                        locationProvider = LocationManager.GPS_PROVIDER;
                    } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                        //如果是Network
                        locationProvider = LocationManager.NETWORK_PROVIDER;
                    }
                    //获取Location
                    if (locationProvider != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            mPhoneInfo.coordinate = String.format("%.02f,%.02f", longitude, latitude);
                        }
                    }
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }
        }).start();
    }

    /**
     * 计算uuid
     *
     * @param imei
     * @param mac
     * @return
     */
    private String calUuid(String imei, String mac) {
        return BLCommonTools.md5(imei + mac);
    }

    /**
     * 开始上传
     */
    private void startUpload() {
        isStartPick = true;
        if (mTimerUpload == null) {
            mTimerUpload = new Timer();
            mTimerUpload.schedule(new TimerTask() {
                @Override
                public void run() {
                    mPyramidTask.sendMsg(BLPyramidTask.UPLOAD_DATA);
                }
            }, 5, mPyramidConfig.getUploadPeriod() * 1000);
        }
    }

    /**
     * 结束上传
     */
    private void stopUpload() {
        isStartPick = false;
        if (mTimerUpload != null) {
            mTimerUpload.cancel();
            mTimerUpload = null;
        }
    }

    /**
     * 计算app的sign
     * @param license License
     * @param bundle 包名
     * @return
     */
    protected static String calDataSign(String license, String bundle){
        return BLCommonTools.md5(bundle + license + "9#$*05");
    }

}
