package cn.com.broadlink.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.networkapi.NetworkAPI;
import cn.com.broadlink.networkapi.NetworkCallback;
import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.constants.controller.BLDevProtocol;
import cn.com.broadlink.sdk.constants.controller.BLDeviceState;
import cn.com.broadlink.sdk.data.controller.BLAPInfo;
import cn.com.broadlink.sdk.data.controller.BLCycleInfo;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.data.controller.BLGetAPListResult;
import cn.com.broadlink.sdk.data.controller.BLPeriodInfo;
import cn.com.broadlink.sdk.data.controller.BLStdData;
import cn.com.broadlink.sdk.data.controller.BLTimerInfo;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceScanListener;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceStateChangedListener;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.param.controller.BLDeviceConfigParam;
import cn.com.broadlink.sdk.param.controller.BLStdControlParam;
import cn.com.broadlink.sdk.param.family.BLPrivateData;
import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.result.BLControllerDNAControlResult;
import cn.com.broadlink.sdk.result.account.BLLoginResult;
import cn.com.broadlink.sdk.result.controller.BLAPConfigResult;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;
import cn.com.broadlink.sdk.result.controller.BLBindDeviceResult;
import cn.com.broadlink.sdk.result.controller.BLDeviceConfigResult;
import cn.com.broadlink.sdk.result.controller.BLDeviceTimeResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadUIResult;
import cn.com.broadlink.sdk.result.controller.BLFirmwareVersionResult;
import cn.com.broadlink.sdk.result.controller.BLGatewayTranslateResult;

import cn.com.broadlink.sdk.result.controller.BLPairResult;
import cn.com.broadlink.sdk.result.controller.BLPassthroughResult;
import cn.com.broadlink.sdk.result.controller.BLProfileStringResult;
import cn.com.broadlink.sdk.result.controller.BLQueryDeviceStatus;
import cn.com.broadlink.sdk.result.controller.BLQueryDeviceStatusResult;
import cn.com.broadlink.sdk.result.controller.BLQueryResoureVersionResult;
import cn.com.broadlink.sdk.result.controller.BLQueryTaskResult;
import cn.com.broadlink.sdk.result.controller.BLStdControlResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevAddResult;
import cn.com.broadlink.sdk.result.controller.BLSubDevListInfo;
import cn.com.broadlink.sdk.result.controller.BLSubDevListResult;
import cn.com.broadlink.sdk.result.controller.BLSubdevResult;
import cn.com.broadlink.sdk.result.controller.BLTaskDataResult;
import cn.com.broadlink.sdk.result.controller.BLUpdateDeviceResult;
import cn.com.broadlink.sdk.result.controller.ResourceVersion;
import cn.com.broadlink.sdk.result.family.BLPrivateDataIdResult;
import cn.com.broadlink.sdk.result.family.BLPrivateDataResult;

/**
 * Created by zhuxuyang on 16/4/14.
 */
final class BLControllerImpl implements BLAccountLoginListener {
    private NetworkAPI mNetworkAPI = null;
    private BLJSControllserImpl mJSControllserImpl = null;

    // 存储SDK认证License
    private String mLicense = null;
    // 存储已登录userid
    private String mUserid = null;
    // 存储已登录usersession
    private String mUserserssion = null;

    /** 变量区 **/
    // 刷新设备Timer
    private Timer mTimerProbe = null;
    // 黑名单
    private ArrayList<String> mListBlack = new ArrayList<>();
    // 可扫描设备
    private ArrayList<String> mListPids = new ArrayList<>();
    // 设备列表
    private HashMap<String, BLProbeDevice> mMapDevice = new HashMap<>();
    // 设备信息改变监听
    private BLDeviceScanListener mDeviceScanListener = null;
    // 设备状态改变监听
    private BLDeviceStateChangedListener mDeviceStateChangedListener = null;
    // SDK运行标志位
    private boolean mSDKRunning = false;
    // 刷新设备状态线程
    private FreshDeviceStateThread mFreshDeviceStateThread;
    // 查询设备在线状态任务
    private BLControllerDeviceQueryTask mControllerDeviceQueryTask;
    // 缓存Profile size:10k
    private BLLruCacheV4<String, String> mCacheProfile = new BLLruCacheV4<>(10 * 1024);
    // 缓存cookie
    private HashMap<String, String> mMapCookie = new HashMap<>();

    /** 配置区 **/
    // 扫描时间
    private int mScanTime = 3000;
    // 扫描期间间隔
    private int mScanInterval = 950;
    // 本地超时时间
    private int mLtimeout = 1500;
    // 远程超时时间
    private int mRtimeout = 3000;
    // 连续发包次数
    private int mSendcount = 1;
    // 8个设备开启批量查询
    private int mQueryCount = 8;
    // 设备配置超时时间 单位：秒
    private int mConfigTimeout = 75;
    // http超时时间设置
    private int mHttpTimeOut = 30 * 1000;
    // 网络模式 0:本地控制, 1:远程控制 ,默认(-1)不传入
    private int mNetMode = -1;

    /** 常量区 **/
    // 刷新间隔
    private static final int PROBE_INTERVAL = 5000;
    // 设备状态更新周期
    private static final int FRESH_INTERVAL = 3000;
    // 设备从本地到远程超时时间
    private static final int DEV_STATE_FRESH_TIMEOUT = 30 * 1000;
    // HTTP 错误返回信息
    private static final String ERR_SERVER_NO_RESULT = "Server has no return data";

    /**
     * 初始化方法
     *
     * @param context
     */
    public void init(Context context, String license, BLConfigParam configParam) {
        mJSControllserImpl = new BLJSControllserImpl(context);

        // 初始化参数
        String ltimeout = configParam.get(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT);
        if (ltimeout != null) {
            try {
                mLtimeout = Integer.parseInt(ltimeout);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        String rtimeout = configParam.get(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT);
        if (rtimeout != null) {
            try {
                mRtimeout = Integer.parseInt(rtimeout);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        String sendCount = configParam.get(BLConfigParam.CONTROLLER_SEND_COUNT);
        if (sendCount != null) {
            try {
                mSendcount = Integer.parseInt(sendCount);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        // 批量查询设备状态最小个数
        String query = configParam.get(BLConfigParam.CONTROLLER_QUERY_COUNT);
        if (query != null) {
            try {
                mQueryCount = Integer.parseInt(query);
            }  catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        // 配置超时时间
        String configTimeOut = configParam.get(BLConfigParam.CONTROLLER_DEVICE_CONFIG_TIMEOUT);
        if (configTimeOut != null) {
            try {
                mConfigTimeout = Integer.parseInt(configTimeOut);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        // 设备控制网络模式
        String netMode = configParam.get(BLConfigParam.CONTROLLER_NETMODE);
        if (netMode != null) {
            try {
                mNetMode = Integer.parseInt(netMode);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        // DNASDK日志打印级别
        int logLevel = 4;
        String sLogLevel = configParam.get(BLConfigParam.CONTROLLER_JNI_LOG_LEVEL);
        if (sLogLevel != null) {
            try {
                logLevel = Integer.parseInt(sLogLevel);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        /** license初始化 **/
        mLicense = license;

        /** 获取sdk实例 **/
        mNetworkAPI = NetworkAPI.getInstanceBLNetwork(context);

        /** 初始化sdk **/
        initSDK(logLevel);

        // 初始化设备查询任务
        initQueryTask();

        // 开始查询设备状态线程
        mSDKRunning = true;
        mFreshDeviceStateThread = new FreshDeviceStateThread();
        mFreshDeviceStateThread.start();
    }

    /**
     * 初始化SDK
     */
    private void initSDK(int logLevel) {
        JSONObject jInit = new JSONObject();
        try {
            jInit.put("filepath", BLFileStorageUtils.mScriptPath);
            jInit.put("loglevel", logLevel);

            if (mNetMode == 0) {    //仅支持本地控制
                jInit.put("localctrl", true);
            } else {
                jInit.put("localctrl", false);
            }

            String sInit = jInit.toString();

            BLCommonTools.debug("Controller init param: " + sInit);
            String initResult = mNetworkAPI.SDKInit(sInit);
            BLCommonTools.debug("Controller init result: " + initResult);

        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }
    }

    /**
     * SDK结束时调用，清空缓存，结束任务
     */
    public void finish() {
        stopProbe();

        mSDKRunning = false;
        mFreshDeviceStateThread.interrupt();
        mFreshDeviceStateThread = null;
    }

    /**
     * SDK认证
     * 会在每次更变登陆信息之后自动调用
     * 一般不需要开发者手动调用
     */
    private BLBaseResult auth() {
        BLBaseResult result = new BLBaseResult();

        if (mUserid == null || mUserserssion == null) {
            // 未登录则返回异常
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("need login");
        } else {
            JSONObject jAuth = new JSONObject();
            try {
                jAuth.put("license", mLicense);
                jAuth.put("account_id", mUserid);
                jAuth.put("account_session", mUserserssion);

                String sAuth = jAuth.toString();

                BLCommonTools.debug("Controller auth param: " + sAuth);
                String authResult = mNetworkAPI.SDKAuth(sAuth);
                BLCommonTools.debug("Controller auth result: " + authResult);

                JSONObject jResult = new JSONObject(authResult);
                result.setStatus(jResult.optInt("status"));
                result.setMsg(jResult.optString("msg", null));

                // 记录auth结果
                recordAuth(result.getStatus(), result.getMsg());
            } catch (JSONException e) {
                BLCommonTools.handleError(e);

                result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
                result.setMsg(e.toString());
            }
        }

        return result;
    }

    /**
     * 设备扫描回调
     */
    public void setDeviceScanListener(BLDeviceScanListener scanListener) {
        mDeviceScanListener = scanListener;
    }

    /**
     * 设置设备状态监听事件
     * 全局只有一个即可
     * 不需要设置成事件组
     */
    public void setOnDeviceStateChangedListener(BLDeviceStateChangedListener deviceStateChangedListener) {
        mDeviceStateChangedListener = deviceStateChangedListener;
    }

    /**
     * 网络库回调设置
     */
    public void setOnNetworkCallback(NetworkCallback networkCallback) {
        mNetworkAPI.setNetworkCallback(networkCallback);
    }

    /**
     * 开始扫描设备进程
     */
    public synchronized void startProbe() {
        if (mTimerProbe == null) {
            mTimerProbe = new Timer();

            mTimerProbe.schedule(new TimerTask() {
                @Override
                public void run() {
                    JSONObject jProbe = new JSONObject();
                    try {
                        /** 设置参数 **/
                        jProbe.put("scantime", mScanTime);
                        jProbe.put("scaninterval", mScanInterval);

                        // 自动设置白名单
                        JSONArray jWhite = new JSONArray();
                        HashMap<String, BLProbeDevice> mapDevice = new HashMap<String, BLProbeDevice>(mMapDevice.size());
                        synchronized (mMapDevice) {
                            mapDevice.putAll(mMapDevice);
                        }
                        for (String key : mapDevice.keySet()) {
                            BLProbeDevice device = mapDevice.get(key);
                            String did = device.getDid();
                            if(!TextUtils.isEmpty(did) && TextUtils.isEmpty(device.getpDid())){
                                jWhite.put(did);
                            }
                        }

                        JSONArray jBlack;
                        synchronized (mListBlack) {
                            jBlack = new JSONArray(mListBlack);
                        }

                        JSONArray jPids;
                        synchronized (mListPids) {
                            jPids = new JSONArray(mListPids);
                        }

                        jProbe.put("white", jWhite);
                        jProbe.put("black", jBlack);
                        jProbe.put("pids", jPids);

                        String sProbe = jProbe.toString();

                        if(mNetworkAPI == null){
                           return;
                        }

                        /** 开始广播 **/
                        BLCommonTools.verbose("Controller probe param: " + sProbe);
                        String probeResult = mNetworkAPI.deviceProbe(sProbe);
                        BLCommonTools.verbose("Controller probe result: " + probeResult);

                        /** 结果处理 **/
                        JSONObject jResult = new JSONObject(probeResult);
                        if (jResult.optInt("status") == 0) {
                            JSONArray jArrayDevice = jResult.optJSONArray("list");

                            for (int i = 0; i < jArrayDevice.length(); i++) {
                                JSONObject jDevice = jArrayDevice.optJSONObject(i);

                                // 生成对象
                                BLProbeDevice probeDevice = new BLProbeDevice();
                                probeDevice.setDid(jDevice.optString("did", null));
                                probeDevice.setMac(jDevice.optString("mac", null));
                                probeDevice.setPid(jDevice.optString("pid", null));
                                probeDevice.setName(jDevice.optString("name", null));
                                probeDevice.setType(jDevice.optInt("type"));
                                probeDevice.setLock(jDevice.optBoolean("lock"));
                                probeDevice.setNewconfig(jDevice.optBoolean("newconfig"));
                                probeDevice.setPassword(jDevice.optInt("password"));
                                probeDevice.setLanaddr(jDevice.optString("lanaddr", null));
                                probeDevice.setState(BLDeviceState.LOCAL);
                                probeDevice.setFreshStateTime(System.currentTimeMillis());

                                //extend 添加WIFI协议
                                JSONObject jsonObject = jDevice.optJSONObject("extend");
                                if(jsonObject == null){
                                    jsonObject = new JSONObject();
                                }
                                jsonObject.put("protocol", BLDevProtocol.WIFI);
                                probeDevice.setExtend(jsonObject.toString());
                                probeDevice.setExtendObject(jsonObject);

                                BLProbeDevice existDevice = mMapDevice.get(probeDevice.getDid());
                                if (existDevice == null) {
                                    /** 新设备 **/
                                    BLPairResult pairResult = pair(probeDevice, null);
                                    if (pairResult.succeed()) {
                                        probeDevice.setId(pairResult.getId());
                                        probeDevice.setKey(pairResult.getKey());

                                        // 由App判断设备是否需要更新
                                        boolean shouldAdd = false;
                                        if (mDeviceScanListener != null) {
                                            shouldAdd = mDeviceScanListener.shouldAdd(probeDevice.copy());
                                            mDeviceScanListener.onDeviceUpdate(probeDevice.copy(), true);
                                        }

                                        if (shouldAdd) {
                                            synchronized (mMapDevice) {
                                                if (mMapDevice.get(probeDevice.getDid()) == null) {
                                                    mMapDevice.put(probeDevice.getDid(), probeDevice);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    /** 已存设备 **/
                                    boolean notify = true;

                                    // 设备名称改变
                                    if (!probeDevice.getName().equals(existDevice.getName())) {
                                        existDevice.setName(probeDevice.getName());
                                    }

                                    // 设备类型改变(一般不会)
                                    if (probeDevice.getType() != existDevice.getType()) {
                                        existDevice.setType(probeDevice.getType());
                                    }

                                    // 设备锁改变
                                    if (probeDevice.isLock() != existDevice.isLock()) {
                                        existDevice.setLock(probeDevice.isLock());
                                    }

                                    // 局域网地址变化
                                    if (!probeDevice.getLanaddr().equals(existDevice.getLanaddr())) {
                                        existDevice.setLanaddr(probeDevice.getLanaddr());
                                    }

                                    // Extend是否变化
                                    if (!probeDevice.getExtend().equals(existDevice.getExtend())) {
                                        existDevice.setExtend(probeDevice.getExtend());
                                    }

                                    // 更新本地扫描时间,判断是否需要通知App
                                    existDevice.setFreshStateTime(probeDevice.getFreshStateTime());
                                    probeDevice.setState(BLDeviceState.LOCAL);
                                    if (existDevice.getState() != BLDeviceState.LOCAL) {
                                        existDevice.setState(BLDeviceState.LOCAL);

                                        // 通知App设备状态发生改变
                                        if (mDeviceStateChangedListener != null) {
                                            mDeviceStateChangedListener.onChanged(existDevice.getDid(), BLDeviceState.LOCAL);
                                        }
                                    }

                                    // 如果设备复位过，则需要重新配对
                                    existDevice.setNewconfig(probeDevice.isNewconfig());
                                    if (probeDevice.isNewconfig()) {
                                        BLPairResult pairResult = pair(probeDevice, null);

                                        if (pairResult.succeed()) {
                                            existDevice.setId(pairResult.getId());
                                            existDevice.setKey(pairResult.getKey());
                                        } else {
                                            // 如果设备配对失败，则不上报
                                            BLCommonTools.debug("pair exit device failed: " + pairResult.getMsg());
                                            notify = false;
                                        }
                                    }

                                    // 需要通知App设备信息发生更变
                                    if (notify && mDeviceScanListener != null) {
                                        mDeviceScanListener.onDeviceUpdate(existDevice.copy(), false);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        BLCommonTools.handleError(e);
                    }
                }
            }, 0, PROBE_INTERVAL);
        }
    }

    /**
     * 停止广播新设备
     */
    public void stopProbe() {
        if (mTimerProbe != null) {
            mTimerProbe.cancel();
            mTimerProbe.purge();
            mTimerProbe = null;
        }
    }

    /**
     * 设备配置
     *
     * @param deviceConfigParam
     * @param timeout 配置超时时间
     * @return 设备配置结果
     */
    public BLDeviceConfigResult deviceConfig(BLDeviceConfigParam deviceConfigParam, int timeout) {
        int configTimeout = mConfigTimeout;
        if (timeout >= 0) {
            configTimeout = timeout;
        }

        JSONObject jConfig = new JSONObject();
        BLDeviceConfigResult result = new BLDeviceConfigResult();
        try {
            jConfig.put("ssid", deviceConfigParam.getSsid());
            jConfig.put("password", deviceConfigParam.getPassword());
            jConfig.put("gatewayaddr", deviceConfigParam.getGatewayaddr());
            jConfig.put("cfgversion", deviceConfigParam.getVersion());
            jConfig.put("timeout", configTimeout);

            String sConfig = jConfig.toString();

            BLCommonTools.debug("Controller deviceConfig param: " + sConfig);
            String configResult = mNetworkAPI.deviceEasyConfig(sConfig);
            BLCommonTools.debug("Controller deviceConfig result: " + configResult);

            JSONObject jResult = new JSONObject(configResult);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.succeed()) {
                result.setDevaddr(jResult.optString("devaddr", null));
                result.setMac(jResult.optString("mac", null));
                result.setDid(jResult.optString("did", null));
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 取消设备配置
     *
     * @return 取消配置结果
     */
    public BLBaseResult deviceConfigCancel() {
        BLBaseResult result = new BLBaseResult();
        try {
            BLCommonTools.debug("Controller deviceConfigCancel");
            String cancelResult = mNetworkAPI.deviceEasyConfigCancel();
            BLCommonTools.debug("Controller deviceConfigCancel result: " + cancelResult);

            JSONObject jResult = new JSONObject(cancelResult);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 设备配对
     *
     * @param device 设备信息
     * @param configParam 基本配置
     * @return 配对结果
     */
    public BLPairResult pair(BLProbeDevice device, BLConfigParam configParam) {
        BLPairResult result = new BLPairResult();

        try {
            String sPair = device.toJSONString();

            /** 参数配置 **/
            int ltimeout = mLtimeout;

            if (configParam != null) {
                try {
                    String ts = configParam.get(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT);
                    if (ts != null) {
                        ltimeout = Integer.parseInt(ts);
                    }

                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }

            JSONObject jDesc = new JSONObject();
            jDesc.put("ltimeout", ltimeout);

            if(mNetworkAPI == null){
                return result;
            }
            /** 指令发送 **/
            BLCommonTools.verbose("Controller pair param: " + sPair);
            String pairResult = mNetworkAPI.devicePair(sPair, jDesc.toString());
            BLCommonTools.verbose("Controller pair result: " + pairResult);

            JSONObject jResult = new JSONObject(pairResult);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
            if (result.succeed()) {
                result.setId(jResult.optInt("id"));
                result.setKey(jResult.optString("key", null));
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 添加设备到SDK中
     *
     * @param listDevice
     */
    public void addDevice(ArrayList<BLDNADevice> listDevice) {
        ArrayList<BLProbeDevice> listProbeDevice = new ArrayList<>(listDevice.size());

        synchronized (mMapDevice) {
            for (BLDNADevice device : listDevice) {
                BLProbeDevice probeDevice = new BLProbeDevice(device);
                BLCommonTools.debug("Controller add device did: " + probeDevice.getDid());

                mMapDevice.put(device.getDid(), probeDevice);
                if(probeDevice.getExtendObject() != null && probeDevice.getExtendObject().optInt("protocol") == BLDevProtocol.VIRTUAL){
                    probeDevice.setState(BLDeviceState.LOCAL);
                }

                // 判断是否需要检查设备在线状态
                if(probeDevice.getState() <= BLDeviceState.UNKNOWN || TextUtils.isEmpty(device.getpDid())){
                    BLCommonTools.debug("Controller add probe device did: " + probeDevice.getDid());
                    listProbeDevice.add(probeDevice);
                }
            }
        }

        // 查询设备状态
        queryDevicesStatusOnServer(listProbeDevice);
    }

    /**
     * 用于从SDK中移除不需要的设备
     *
     * @param listDevice
     */
    public void removeDevice(ArrayList<String> listDevice) {
        // 从SDK中删除
        synchronized (mMapDevice) {
            for (String deviceid : listDevice) {
                BLCommonTools.debug("Controller remove device did: " + deviceid);
                mMapDevice.remove(deviceid);
            }
        }
    }

    /**
     * 用于从SDK中移除所有的设备
     */
    public void removeAllDevice() {
        synchronized (mMapDevice) {
            mMapDevice.clear();
        }
    }

    /**
     * 查询设备状态
     *
     * @param did 查询设备的Did
     * @return 设备状态
     */
    public int queryDeviceState(String did) {
        BLProbeDevice device = mMapDevice.get(did);
        if (device != null) {
            return device.getState();
        } else {
            return BLDeviceState.UNKNOWN;
        }
    }

    /**
     * 查询设备远程状态
     *
     * @param did 查询设备的Did
     * @return 远程状态
     */
    public int queryDeviceRemoteState(String did) {

        BLCommonTools.debug("Controller queryDeviceRemoteState start");
        BLControllerDNAControlResult result = autoDNAControl(did, null, null, "dev_online", null);
        BLCommonTools.debug("Controller queryDeviceRemoteState end");

        if (result.getStatus() == BLBaseResult.SUCCESS) {
            JSONObject jData = result.getData();
            if (jData != null) {
                int state = jData.optBoolean("online") ? BLDeviceState.REMOTE : BLDeviceState.OFFLINE;
                return state;
            }
        }

        return BLDeviceState.OFFLINE;
    }

    /**
     * 返回设备局域网Ip
     *
     * @param did 查询设备的Did
     * @return 查询结果
     */
    public String queryDeviceIp(String did) {
        BLProbeDevice device = mMapDevice.get(did);
        if (device != null) {
            return device.getLanaddr();
        } else {
            return null;
        }
    }

    /**
     * 设备绑定到用户
     *
     * @param listDevice
     * @return
     */
    public BLBindDeviceResult bindWithServer(ArrayList<BLDNADevice> listDevice) {
        BLBindDeviceResult result = new BLBindDeviceResult();

        String accountId = mUserid;
        String accountSessiont = mUserserssion;

        if (accountId == null || accountSessiont == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("not login");
            return result;
        }

        try {
            JSONObject jBindDevice = new JSONObject();
            JSONArray jArrayDev = new JSONArray();

            for (BLDNADevice device : listDevice) {
                JSONObject jDev = new JSONObject();
                jDev.put("did", device.getDid());
                jDev.put("pid", device.getPid());

                jArrayDev.put(jDev);
            }
            jBindDevice.put("dev_list", jArrayDev);
            jBindDevice.put("account_id", accountId);
            jBindDevice.put("account_session", accountSessiont);
            jBindDevice.put("check_status", false);
            jBindDevice.put("name", "");
            jBindDevice.put("phone", "");
            jBindDevice.put("email", "");

            String sBindDevice = jBindDevice.toString();

            BLCommonTools.debug("Controller bindDevice param: " + sBindDevice);
            String bindDeviceResult = mNetworkAPI.deviceBindWithServer(sBindDevice, null);
            BLCommonTools.debug("Controller bindDevice result: " + bindDeviceResult);

            JSONObject jResult = new JSONObject(bindDeviceResult);

            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.succeed()) {
                JSONArray jBindMap = jResult.optJSONArray("bindmap");
                int[] bindmap = new int[jBindMap.length()];
                for (int i = 0; i < jBindMap.length(); i++) {
                    bindmap[i] = jBindMap.optInt(i);
                }

                result.setBindmap(bindmap);
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 用于查询设备的绑定状态
     *
     * @param listDevice
     * @return
     */
    public BLBindDeviceResult queryDeviceBinded(ArrayList<BLDNADevice> listDevice) {
        BLBindDeviceResult result = new BLBindDeviceResult();

        String accountId = mUserid;
        String accountSessiont = mUserserssion;

        if (accountId == null || accountSessiont == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("not login");
            return result;
        }

        try {
            JSONObject jBindDevice = new JSONObject();
            JSONArray jArrayDev = new JSONArray();

            for (BLDNADevice device : listDevice) {
                JSONObject jDev = new JSONObject();
                jDev.put("did", device.getDid());
                jDev.put("pid", device.getPid());

                jArrayDev.put(jDev);
            }
            jBindDevice.put("dev_list", jArrayDev);
            jBindDevice.put("account_id", accountId);
            jBindDevice.put("account_session", accountSessiont);
            jBindDevice.put("check_status", true);
            jBindDevice.put("name", "");
            jBindDevice.put("phone", "");
            jBindDevice.put("email", "");

            String sBindDevice = jBindDevice.toString();

            BLCommonTools.debug("Controller bindDevice param: " + sBindDevice);
            String bindDeviceResult = mNetworkAPI.deviceBindWithServer(sBindDevice, null);
            BLCommonTools.debug("Controller bindDevice result: " + bindDeviceResult);

            JSONObject jResult = new JSONObject(bindDeviceResult);

            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.succeed()) {
                JSONArray jBindMap = jResult.optJSONArray("bindmap");
                int[] bindmap = new int[jBindMap.length()];
                for (int i = 0; i < jBindMap.length(); i++) {
                    bindmap[i] = jBindMap.optInt(i);
                }

                result.setBindmap(bindmap);
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    public BLProfileStringResult getProfileAsStringByDid(String did, String profilePath){
        BLProfileStringResult result = new BLProfileStringResult();
        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");

            return result;
        }else{
            return getProfileAsStringByPid(device.getPid(), profilePath);
        }
    }

    public BLProfileStringResult getProfileAsStringByPid(String pid, String profilePath) {
        if(isVirtualDevice(profilePath, pid, null)){
            return mJSControllserImpl.getProfileAsStringByPid(pid, profilePath);
        } else {
            return queryDevProfile(pid, profilePath);
        }
    }

    private BLProfileStringResult queryDevProfile(String pid, String profilePath){
        BLProfileStringResult result = new BLProfileStringResult();
        String cachedProfile = mCacheProfile.get(pid);

        /** 返回缓存中的Profile **/
        if (cachedProfile != null) {
            result.setStatus(BLBaseResult.SUCCESS);
            result.setMsg("success");
            result.setProfile(cachedProfile);

            return result;
        }

        JSONObject jDesc = new JSONObject();
        if (profilePath != null) {
            try {
                jDesc.put("scriptfile", profilePath);
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        }

        BLCommonTools.debug("Controller profile param: " + pid);
        String sResult = mNetworkAPI.deviceProfile2(pid, jDesc.toString());
        BLCommonTools.debug("Controller profile result: " + sResult);

        // 结果转换
        try {
            JSONObject jResult = new JSONObject(sResult);

            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
            if (result.succeed()) {
                String profile = jResult.optString("profile", null);
                String scriptVersion = jResult.optString("scriptVersion", null);
                JSONObject jProObject = new JSONObject(profile);

                if (scriptVersion != null) {
                    List<String> mRmPidList = Arrays.asList("00000000000000000000000037270000", "000000000000000000000000a2270000");
                    List<String> mRmProPidList = Arrays.asList("00000000000000000000000012270000", "00000000000000000000000083270000",
                            "0000000000000000000000008b270000", "000000000000000000000000a1270000", "00000000000000000000000087270000",
                            "0000000000000000000000009d270000", "000000000000000000000000a9270000", "0000000000000000000000002a270000"
                    );

                    if (mRmPidList.contains(pid)) {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put("10");
                        jProObject.put("protocol", jsonArray);
                    }

                    if (mRmProPidList.contains(pid)) {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put("10");
                        jsonArray.put("11");
                        jsonArray.put("12");
                        jProObject.put("protocol", jsonArray);
                    }
                }

                result.setProfile(jProObject.toString());
                // 加入缓存
                mCacheProfile.put(pid, result.getProfile());
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    public void cleanProfileCahe(String pid){
        mCacheProfile.remove(pid);
    }

    /**
     * 用于标准控制
     *
     * @param did
     *          设备DID
     * @param sDid
     *          子设ID
     * @param stdControlParam
     * @param configParam
     * @return
     */
    public BLStdControlResult dnaControl(String did, String sDid, BLStdControlParam stdControlParam, BLConfigParam configParam) {
        BLStdControlResult result = new BLStdControlResult();

        String dataStr = ctrlParamObjectToStr(stdControlParam);

        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");
            return result;
        }

        BLProbeDevice subDevInfo = null;
        if(!TextUtils.isEmpty(sDid)){
            subDevInfo = mMapDevice.get(sDid);
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, dataStr, BLDevCmdConstants.DEV_CTRL, configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());

        if (result.succeed()) {
            // Json转为对象
            BLStdData stdData = parseStdData(controlResult.getData());

            result.setData(stdData);
        }

        return result;
    }

    /**
     * 透传控制
     *
     * @param did
     * @param data
     * @param configParam
     * @return
     */
    public BLPassthroughResult dnaPassthrough(String did, String sDid, byte[] data, BLConfigParam configParam) {
        BLPassthroughResult result = new BLPassthroughResult();

        JSONObject jParam = new JSONObject();
        try {
            jParam.put("data", Base64.encodeToString(data, Base64.NO_WRAP));
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("Error Unknown");
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, jParam.toString(), BLDevCmdConstants.DEV_PASSTHROUGH, configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            String resp = controlResult.getData().optString("resp", null);
            if (resp != null) {
                result.setData(Base64.decode(resp, Base64.NO_WRAP));
            }
        }

        return result;
    }

    /**
     * 获取发送的二进制
     * @param did
     * @param sDid
     * @param stdControlParam
     * @return
     */
    public byte[] dnaControlData(String did, String sDid, BLStdControlParam stdControlParam){
        String dataStr = ctrlParamObjectToStr(stdControlParam);

        BLControllerDNAControlResult result = autoDNAControl(did, sDid, dataStr, BLDevCmdConstants.DEV_DATA, null);
        if(result != null && result.succeed() && result.getData() != null){
            JSONObject jsonObject = result.getData();
            String ctrldataStr = jsonObject.optString("ctrldata");
            if(ctrldataStr != null){
                return Base64.decode(ctrldataStr, Base64.NO_WRAP);
            }
        }
        return null;
    }
    /**
     * 更新设备信息
     *
     * @param did
     * @param name
     * @param lock
     * @param configParam
     * @return
     */
    public BLUpdateDeviceResult updateDeviceInfo(String did, String name, boolean lock, BLConfigParam configParam) {
        BLUpdateDeviceResult result = new BLUpdateDeviceResult();

        JSONObject jParam = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jData.put("name", name);
            jData.put("lock", lock);
            jParam.put("data", jData);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, null, jParam.toString(), "dev_info", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            JSONObject jDataResult = controlResult.getData();
            if (jDataResult != null) {
                result.setName(jDataResult.optString("name", null));
                result.setLock(jDataResult.optBoolean("lock"));
            }
        }

        return result;
    }

    /**
     * 查询固件版本
     *
     * @param did
     * @param configParam
     * @return
     */
    public BLFirmwareVersionResult queryFirmwareVersion(String did, BLConfigParam configParam) {
        BLFirmwareVersionResult result = new BLFirmwareVersionResult();

        BLControllerDNAControlResult controlResult = autoDNAControl(did, null, null, "fw_version", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            try {
                String version = controlResult.getData().optJSONObject("version").optString("hw", null);
                result.setVersion(version);
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        return result;
    }

    /**
     * 固件升级
     *
     * @param did
     * @param url
     * @param configParam
     * @return
     */
    public BLBaseResult updateFirmware(String did, String url, BLConfigParam configParam) {
        BLBaseResult result = new BLBaseResult();

        JSONObject jData = new JSONObject();
        try {
            URL urls = new URL(url);
            String host = urls.getHost();
            InetAddress inetAddress = InetAddress.getByName(host);
            if (!host.equalsIgnoreCase(inetAddress.getHostAddress())) {
                String ipaddress = inetAddress.getHostAddress();
                url = url.replace(host, ipaddress);
            }

            JSONObject urlObject = new JSONObject();
            urlObject.put("hw_url", url);

            jData.put("data", urlObject);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, null, jData.toString(), "fw_upgrade", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());

        return result;
    }

    /**
     * 查询设备服务器时间
     *
     * @param did
     * @param configParam
     * @return
     */
    public BLDeviceTimeResult queryDeviceTime(String did, BLConfigParam configParam) {
        BLDeviceTimeResult result = new BLDeviceTimeResult();

        BLControllerDNAControlResult controlResult = autoDNAControl(did, null, null, "serv_time", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            try {
                result.setTime(controlResult.getData().optString("serv_time", null));
                result.setDifftime(controlResult.getData().optInt("diff_time"));
            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }

        return result;
    }

    /**
     * 查询任务
     */
    public BLQueryTaskResult queryTask(String did, String sDid, BLConfigParam configParam) {
        BLQueryTaskResult result = new BLQueryTaskResult();

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, null, "dev_tasklist", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            ArrayList<BLTimerInfo> listTimer = new ArrayList<>();
            ArrayList<BLTimerInfo> listDelay = new ArrayList<>();
            ArrayList<BLPeriodInfo> listPeriod = new ArrayList<>();
            ArrayList<BLCycleInfo> listCycle = new ArrayList<>();
            ArrayList<BLCycleInfo> listRandom = new ArrayList<>();

            parseTask(controlResult.getData(), listTimer, listDelay, listPeriod, listCycle, listRandom);

            result.setTimer(listTimer);
            result.setDelay(listDelay);
            result.setPeriod(listPeriod);
            result.setCycle(listCycle);
            result.setRandom(listRandom);
        }

        return result;
    }

    /**
     * 新增或修改定时任务
     */
    public BLQueryTaskResult modifyTimerTask(String did, String sDid, int taskType, boolean add, BLTimerInfo task, BLStdData stdData, BLConfigParam configParam) {
        JSONObject jTask = new JSONObject();
        try {
            jTask.put("type", taskType);
            if (!add) {
                jTask.put("index", task.getIndex());
            }
            jTask.put("enable", task.isEnable());
            jTask.put("time", String.format("%d-%d-%d %d:%d:%d", task.getYear(), task.getMonth(), task.getDay(), task.getHour(), task.getMin(), task.getSec()));
            jTask.put("data", parseStdDataJsonObject(stdData));
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return modifyTask(did, sDid, jTask.toString(), configParam);
    }

    /**
     * 新增或修改周期任务
     */
    public BLQueryTaskResult modifyPeriodTask(String did, String sDid, boolean add, BLPeriodInfo task, BLStdData stdData, BLConfigParam configParam) {
        JSONObject jTask = new JSONObject();
        try {
            jTask.put("type", 2);
            if (!add) {
                jTask.put("index", task.getIndex());
            }
            jTask.put("enable", task.isEnable());
            JSONArray jRepeat = new JSONArray();
            if (task.getRepeat() != null) {
                for (int i : task.getRepeat()) {
                    jRepeat.put(i);
                }
            }
            jTask.put("repeat", jRepeat);
            jTask.put("time", String.format("%d:%d:%d", task.getHour(), task.getMin(), task.getSec()));
            jTask.put("data", parseStdDataJsonObject(stdData));
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return modifyTask(did, sDid, jTask.toString(), configParam);
    }

    /**
     * 新增或修改循环任务
     */
    public BLQueryTaskResult modifyCycleTask(String did, String sDid, int taskType, boolean add,
                                             BLCycleInfo task, BLStdData stdData1, BLStdData stdData2, BLConfigParam configParam) {

        JSONObject jTask = new JSONObject();
        try {
            jTask.put("type", taskType);
            if (!add) {
                jTask.put("index", task.getIndex());
            }
            jTask.put("enable", task.isEnable());
            jTask.put("cmd1duration", task.getCmd1duration());
            jTask.put("cmd2duration", task.getCmd2duration());

            JSONArray jRepeat = new JSONArray();
            if (task.getRepeat() != null) {
                for (int i : task.getRepeat()) {
                    jRepeat.put(i);
                }
            }
            jTask.put("repeat", jRepeat);

            jTask.put("time", String.format("%d:%d:%d", task.getStart_hour(), task.getStart_min(), task.getStart_sec()));
            jTask.put("endtime", String.format("%d:%d:%d", task.getEnd_hour(), task.getEnd_min(), task.getEnd_sec()));
            jTask.put("data", parseStdDataJsonObject(stdData1));
            jTask.put("data2", parseStdDataJsonObject(stdData2));
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
        return modifyTask(did, sDid, jTask.toString(), configParam);
    }

    /**
     * 增加任务
     */
    private BLQueryTaskResult modifyTask(String did, String sDid, String task, BLConfigParam configParam) {
        BLQueryTaskResult result = new BLQueryTaskResult();

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, task, "dev_taskadd", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            ArrayList<BLTimerInfo> listTimer = new ArrayList<>();
            ArrayList<BLTimerInfo> listDelay = new ArrayList<>();
            ArrayList<BLPeriodInfo> listPeriod = new ArrayList<>();
            ArrayList<BLCycleInfo> listCycle = new ArrayList<>();
            ArrayList<BLCycleInfo> listRandom = new ArrayList<>();

            parseTask(controlResult.getData(), listTimer, listDelay, listPeriod, listCycle, listRandom);

            result.setTimer(listTimer);
            result.setDelay(listDelay);
            result.setPeriod(listPeriod);
            result.setCycle(listCycle);
            result.setRandom(listRandom);
        }

        return result;
    }

    /**
     * 删除任务
     */
    public BLQueryTaskResult delTask(String did, String sDid, int taskType, int index, BLConfigParam configParam) {
        BLQueryTaskResult result = new BLQueryTaskResult();

        JSONObject jDel = new JSONObject();
        try {
            jDel.put("type", taskType);
            jDel.put("index", index);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, jDel.toString(), "dev_taskdel", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            ArrayList<BLTimerInfo> listTimer = new ArrayList<>();
            ArrayList<BLTimerInfo> listDelay = new ArrayList<>();
            ArrayList<BLPeriodInfo> listPeriod = new ArrayList<>();
            ArrayList<BLCycleInfo> listCycle = new ArrayList<>();
            ArrayList<BLCycleInfo> listRandom = new ArrayList<>();

            parseTask(controlResult.getData(), listTimer, listDelay, listPeriod, listCycle, listRandom);

            result.setTimer(listTimer);
            result.setDelay(listDelay);
            result.setPeriod(listPeriod);
            result.setCycle(listCycle);
            result.setRandom(listRandom);
        }

        return result;
    }

    /**
     * 查询任务指令
     */
    public BLTaskDataResult queryTaskData(String did, String sDid, int taskType, int index, BLConfigParam configParam) {
        BLTaskDataResult result = new BLTaskDataResult();

        JSONObject jQuery = new JSONObject();
        try {
            jQuery.put("type", taskType);
            jQuery.put("index", index);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        BLControllerDNAControlResult controlResult = autoDNAControl(did, sDid, jQuery.toString(), "dev_taskdata", configParam);

        result.setStatus(controlResult.getStatus());
        result.setMsg(controlResult.getMsg());
        if (result.succeed()) {
            if (taskType == 0 || taskType == 1 || taskType == 2) {
                result.setData(parseStdData(controlResult.getData()));
            } else {

                try {
                    JSONObject dataObj = controlResult.getData();
                    JSONObject data1Obj = dataObj.getJSONObject("cmd1");
                    JSONObject data2Obj = dataObj.getJSONObject("cmd2");
                    if (data1Obj != null) {
                        result.setData(parseStdData(data1Obj));
                    }
                    if (data2Obj != null) {
                        result.setData2(parseStdData(data2Obj));
                    }

                } catch (JSONException e) {
                    BLCommonTools.handleError(e);
                }

            }
        }

        return result;
    }

    /**
     * Desc参数自动构造的dnaControl
     *
     * @param did
     * @param sDid
     * @param data
     * @param configParam
     * @return
     */
    public BLControllerDNAControlResult autoDNAControl(String did, String sDid, String data, String command, BLConfigParam configParam) {

        BLControllerDNAControlResult result = new BLControllerDNAControlResult();
        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");

            return result;
        }

        BLProbeDevice subDevInfo = null;
        if(!TextUtils.isEmpty(sDid)){
            subDevInfo = mMapDevice.get(sDid);
        }

        BLControllerDescParam descParam = new BLControllerDescParam();

        descParam.setCommand(command);
        descParam.setLtimeout(mLtimeout);
        descParam.setRtimeout(mRtimeout);
        descParam.setAccount_id(mUserid);
        descParam.setCookie("");
        descParam.setNetmode(mNetMode);
        descParam.setSendcount(mSendcount);

        // 有缓存cookie则用其代替
        String cacheCookie = mMapCookie.get(did);
        if (cacheCookie != null) {
            BLCommonTools.debug(cacheCookie);
            descParam.setCookie(cacheCookie);
        }

        String scriptpath = null;
        /** 配置参数 **/
        if (configParam != null) {
            // 本地超时
            String ltimeout = configParam.get(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT);
            if (ltimeout != null) {
                try {
                    descParam.setLtimeout(Integer.parseInt(ltimeout));
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }

            // 远程超时
            String rtimeout = configParam.get(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT);
            if (rtimeout != null) {
                try {
                    descParam.setRtimeout(Integer.parseInt(rtimeout));
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }

            // 脚本路径
            scriptpath = configParam.get(BLConfigParam.CONTROLLER_SCRIPT_PATH);
            if (scriptpath != null) {
                descParam.setScriptfile(scriptpath);
            }

            // 子设备脚本路径
            String sub_scriptpath = configParam.get(BLConfigParam.CONTROLLER_SUB_SCRIPT_PATH);
            if (sub_scriptpath != null) {
                descParam.setSub_scriptfile(sub_scriptpath);
            }

            String sendCount = configParam.get(BLConfigParam.CONTROLLER_SEND_COUNT);
            if (sendCount != null) {
                try {
                    descParam.setSendcount(Integer.parseInt(sendCount));
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }
        }

        BLControllerDNAControlResult controlResult;
        //判断是否是虚拟设备 ，如果是虚拟设备 则走JS脚本控制
        if(isVirtualDevice(scriptpath, device.getPid(), subDevInfo != null ? subDevInfo.getPid() : null) && isVirtualDevJsSupportCmd(command)){
            BLCommonTools.debug("Controller VirtualDevice stdControl start");
            controlResult =  mJSControllserImpl.dnaControl(device, subDevInfo, data, descParam, configParam);
        }else{
            /** 控制 **/
            controlResult = autoDNAControl(device, subDevInfo, data, descParam);
        }

        return controlResult;
    }

    /**
     * 会根据错误码自动重试一次的控制接口
     * 密码错误则重新配对
     * sdk未认证则认证
     * 设备未绑定则进行绑定
     *
     * @param device
     * @param subdevInfo
     * @param data
     * @param desc
     * @return
     */
    protected BLControllerDNAControlResult autoDNAControl(BLProbeDevice device, BLProbeDevice subdevInfo, String data, BLControllerDescParam desc) {
        String controlDeviceDid = device.getDid();
        String jDescStr = "{}";
        if(desc != null){
            jDescStr = desc.toJSONStr();
        }

        String subdevInfoStr = null;
        if(subdevInfo != null){
            controlDeviceDid = subdevInfo.getDid();
            subdevInfoStr = subdevInfo.toJSONString();
        }

        /** 开始控制 **/
        BLControllerDNAControlResult result = baseDNAControl(device.toJSONString(), subdevInfoStr, data, jDescStr);

        if (result.getStatus() == BLControllerErrCode.AUTH_FAIL && device.getState() == BLDeviceState.LOCAL) {
            /** 授权失败 重新配对 **/
            BLPairResult pairResult = pair(device, null);
            if (pairResult.succeed()) {
                // 配对成功更新设备
                device.setId(pairResult.getId());
                device.setKey(pairResult.getKey());

                // 通知App
                if (mDeviceScanListener != null) {
                    mDeviceScanListener.onDeviceUpdate(device.copy(), false);
                }

                // 重新控制
                result = baseDNAControl(device.toJSONString(), subdevInfoStr, data, jDescStr);
            }
        } else if (result.getStatus() == BLControllerErrCode.NOT_AUTH ||
                result.getStatus() == BLControllerErrCode.AUTH_TIMEOUT_ERR ||
                result.getStatus() == BLControllerErrCode.AUTH_CODE_ERR ||
                result.getStatus() == BLControllerErrCode.ILLEGALITY_AUTH_CODE_ERR) {
            /** 认证失败重新认证 **/
            if (auth().succeed()) {
                // 重新控制
                result = baseDNAControl(device.toJSONString(), subdevInfoStr, data, jDescStr);
            }
        } else if (result.getStatus() == BLControllerErrCode.NOT_BIND_ERR) {
            /** 设备未绑定则进行绑定 **/
            ArrayList<BLDNADevice> listBind = new ArrayList<>(1);
            listBind.add(device);
            if (bindWithServer(listBind).succeed()) {
                // 重新控制
                result = baseDNAControl(device.toJSONString(), subdevInfoStr, data, jDescStr);
            }
        } else if(result.getStatus() == BLControllerErrCode.FILE_FAIL) { //保存错误或者脚本不存在,重新下载脚本控制设备
            String pid = subdevInfo == null ? device.getPid() : subdevInfo.getPid();
            File file = new File(BLLet.Controller.queryScriptPath(pid));
            if(!file.exists()){
                BLDownloadScriptResult downloadScriptResult = downloadScript(pid);
                if(downloadScriptResult.succeed()){
                    // 重新控制
                    result = baseDNAControl(device.toJSONString(), subdevInfoStr, data, jDescStr);
                }
            }
        }

        // 设置cookie
        if(result != null && result.succeed()){
            String cookieResult = result.getCookie();
            if (cookieResult != null) {
                // 加入缓存
                mMapCookie.put(device.getDid(), cookieResult);
            }
        }

        String pid = device.getPid();
        String sDid = null;
        if (subdevInfo != null) {
            sDid = subdevInfo.getDid();
            pid = subdevInfo.getPid();
        }

        // 记录控制结果
        recordDeviceControl(device.getDid(), sDid, pid, data, result.getStatus(), result.getMsg());

        return result;
    }

    /**
     * 基控制接口
     *
     * @param devInfo
     * @param subdevInfo
     * @param data
     * @param desc
     */
    protected BLControllerDNAControlResult baseDNAControl(String devInfo, String subdevInfo, String data, String desc) {
        BLControllerDNAControlResult result = new BLControllerDNAControlResult();

        BLCommonTools.debug("Controller dnaControl devInfo: " + devInfo);
        BLCommonTools.debug("Controller dnaControl subdevInfo: " + subdevInfo);
        BLCommonTools.debug("Controller dnaControl data: " + data);
        BLCommonTools.debug("Controller dnaControl desc: " + desc);
        String sResult = mNetworkAPI.dnaControl(devInfo, subdevInfo, data, desc);
        BLCommonTools.debug("Controller dnaControl result: " + sResult);

        try {
            JSONObject jResult = new JSONObject(sResult);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.getStatus() == 0) {
                result.setData(jResult.optJSONObject("data"));
                result.setCookie(jResult.optString("cookie", null));
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 查询UI包
     *
     * @param pidList
     * @return
     */
    public BLQueryResoureVersionResult queryUI(List<String> pidList) {
        return queryResoureVersion("ui", pidList);
    }

    public BLQueryResoureVersionResult queryScript(List<String> pidList) {
        return queryResoureVersion("script", pidList);
    }

    private BLQueryResoureVersionResult queryResoureVersion(String type, List<String> pidList){
        BLQueryResoureVersionResult result = new BLQueryResoureVersionResult();
        JSONObject jParam = new JSONObject();

        try {
            jParam.put("type", type);

            JSONArray pidListArray = new JSONArray();
            for(String pid : pidList){
                pidListArray.put(pid);
            }

            jParam.put("pids", pidListArray);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        String queryUIResult = BLBaseHttpAccessor.post(BLApiUrls.APPManager.URL_VERSION_QUERY(), null, jParam.toString().getBytes(), mHttpTimeOut, new BLAccountTrustManager());
        if (queryUIResult != null) {
            try {
                JSONObject jQueryUI = new JSONObject(queryUIResult);
                int code = jQueryUI.optInt("status", BLAppSdkErrCode.ERR_UNKNOWN);

                result.setStatus(code);
                if (result.succeed()) {
                    JSONArray jExtra = jQueryUI.optJSONArray("versions");
                    if (jExtra != null) {
                        for(int i = 0 ; i < jExtra.length(); i ++){
                            ResourceVersion resourceVersion = new ResourceVersion();

                            JSONObject versionObject = jExtra.getJSONObject(i);
                            resourceVersion.setPid(versionObject.optString("pid"));
                            resourceVersion.setVersion(versionObject.optString("version"));
                            result.getVersions().add(resourceVersion);
                        }
                    }
                }
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        }else{
            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("query ui error");
        }

        return result;
    }

    /**
     * 下载UI包
     *
     * @param pid
     * @return
     */
    public BLDownloadUIResult downloadUI(String pid) {
        BLDownloadUIResult result = new BLDownloadUIResult();

        // 设置保存路径
        String savePath = BLFileStorageUtils.getDefaultUIPath(pid);

        String tempPath = BLFileStorageUtils.mTempPath + File.separator + pid + ".zip";

        String url = BLApiUrls.APPManager.URL_DOWNLOAD() + "?resourcetype=ui&pid=" + pid;
        int status = BLBaseHttpAccessor.download(url, null, null, tempPath, null, mHttpTimeOut, new BLAccountTrustManager());

        if (status != 200) {
            switch (status) {
                case 414:
                    result.setStatus(BLAppSdkErrCode.ERR_NO_RESOURCE);
                    result.setMsg("found resource error");
                    break;
                case 415:
                    result.setStatus(BLAppSdkErrCode.ERR_PARAM);
                    result.setMsg("param fomat error");
                    break;
                case 416:
                    result.setStatus(BLAppSdkErrCode.ERR_LEAK_PARAM);
                    result.setMsg("leak necessary param");
                    break;
                case 417:
                    result.setStatus(BLAppSdkErrCode.ERR_TOKEN_OUT_OF_DATE);
                    result.setMsg("token out of date");
                    break;
                case 418:
                    result.setStatus(BLAppSdkErrCode.ERR_WRONG_METHOD);
                    result.setMsg("wrong method");
                    break;
                default:
                    result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
                    result.setMsg("unknown error");
            }

            return result;
        }

        // 解压UI包
        try {
            File tempFile = new File(tempPath);

            File saveFile = new File(savePath);
            if (!saveFile.exists()) {
                saveFile.mkdirs();
            }
            BLCommonTools.upZipFile(tempFile, savePath);

            //删除旧文件
            if (tempFile.exists()) {
                tempFile.delete();
            }

            result.setStatus(BLControllerErrCode.SUCCESS);
            result.setMsg("success");
            result.setSavePath(savePath);
        } catch (Exception e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNZIP);
            result.setMsg("unzip error");
        }

        return result;
    }

    /**
     * 下载脚本包
     *
     * @param pid
     * @return
     */
    public BLDownloadScriptResult downloadScript(String pid) {
        return BLBaseHttpAccessor.downloadScript(pid, mHttpTimeOut, new BLAccountTrustManager());
    }

    /**
     * 获取license对应的 uid & lid
     *
     * @return
     */
    public String getLicenseInfo() {
        return mNetworkAPI.LicenseInfo(mLicense);
    }

    /**
     * 账户模块登陆后的回调
     *
     * @param loginResult
     */
    @Override
    public void onLogin(BLLoginResult loginResult) {
        mUserid = loginResult.getUserid();
        mUserserssion = loginResult.getLoginsession();

        // 重新登陆需要重新认证
        // 开启线程执行
        new Thread() {
            @Override
            public void run() {
                super.run();

                auth();
            }
        }.start();
    }

    public BLQueryDeviceStatusResult queryDeviceOnServer(ArrayList<BLProbeDevice> listDevice) {
        BLQueryDeviceStatusResult result = new BLQueryDeviceStatusResult();

        String accountId = mUserid;
        String accountSessiont = mUserserssion;

        if (accountId == null || accountSessiont == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("not login");
            return result;
        }

        try {
            JSONObject jQueryDevice = new JSONObject();
            JSONArray jArrayDev = new JSONArray();

            for (BLDNADevice device : listDevice) {
                JSONObject jDev = new JSONObject();
                jDev.put("did", device.getDid());
                jDev.put("pid", device.getPid());

                jArrayDev.put(jDev);
            }
            jQueryDevice.put("dev_list", jArrayDev);
            jQueryDevice.put("account_id", accountId);
            jQueryDevice.put("account_session", accountSessiont);

            String sQueryDevice = jQueryDevice.toString();

            BLCommonTools.debug("Controller queryDeviceOnServer param: " + sQueryDevice);
            String queryDeviceResult = mNetworkAPI.deviceStatusOnServer(sQueryDevice, null);
            BLCommonTools.debug("Controller queryDeviceOnServer result: " + queryDeviceResult);

            JSONObject jResult = new JSONObject(queryDeviceResult);

            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.succeed()) {
                JSONArray jStatusMap = jResult.optJSONArray("status_map");
                if (jStatusMap != null) {
                    int count = jStatusMap.length();
                    for (int i = 0; i < count; i++) {
                        BLQueryDeviceStatus blQueryDeviceStatus = new BLQueryDeviceStatus();
                        JSONObject jDevObject = jStatusMap.getJSONObject(i);

                        blQueryDeviceStatus.setDid(jDevObject.optString("did", null));
                        blQueryDeviceStatus.setStatus(jDevObject.optInt("status"));

                        result.getQueryDeviceMap().add(blQueryDeviceStatus);
                    }
                }

            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;

    }

    private void queryDevicesStatusOnServer(ArrayList<BLProbeDevice> devices) {
        // 单独查询设备状态数量限制
        int singleQueryNumber = mQueryCount;

        // 交给查询线程异步完成任务
        if (devices.size() < singleQueryNumber) {
            mControllerDeviceQueryTask.query(devices);
        } else {
            mControllerDeviceQueryTask.batchQuery(devices);
        }
    }

    /**
     * 用于初始化设备状态查询任务
     */
    private void initQueryTask() {
        mControllerDeviceQueryTask = new BLControllerDeviceQueryTask(this, new BLControllerDeviceQueryTask.QueryDeviceStateListener() {
            @Override
            public void onQuery(String did, int state) {
                if (state == BLDeviceState.REMOTE) {
                    BLProbeDevice device = mMapDevice.get(did);
                    if (device != null) {
                        // 非局域网状态则需要更新状态
                        if (device.getState() != BLDeviceState.LOCAL) {
                            // 刷新时间
                            device.setFreshStateTime(System.currentTimeMillis());

                            // 非远程状态才改变并通知用户
                            if (device.getState() != BLDeviceState.REMOTE) {
                                device.setState(BLDeviceState.REMOTE);
                                if (mDeviceStateChangedListener != null) {
                                    mDeviceStateChangedListener.onChanged(did, state);
                                }
                            }
                        }
                    }
                } else if (state == BLDeviceState.OFFLINE) {
                    BLProbeDevice device = mMapDevice.get(did);
                    if (device != null) {
                        // 非局域网状态则需要更新状态
                        if (device.getState() != BLDeviceState.LOCAL) {
                            // 刷新时间
                            device.setFreshStateTime(System.currentTimeMillis());

                            // 非离线状态才改变并通知用户
                            if (device.getState() != BLDeviceState.OFFLINE) {
                                device.setState(BLDeviceState.OFFLINE);
                                if (mDeviceStateChangedListener != null) {
                                    mDeviceStateChangedListener.onChanged(did, state);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onQueryDevices(ArrayList<BLProbeDevice> listDevice) {
                for (BLProbeDevice device : listDevice) {
                    String did = device.getDid();
                    BLProbeDevice localDevice = mMapDevice.get(did);
                    //设备存在，且是非局域网状态则需要更新状态
                    if (localDevice != null && localDevice.getState() != BLDeviceState.LOCAL) {
                        // 刷新时间
                        localDevice.setFreshStateTime(System.currentTimeMillis());

                        //状态发生改变才通知用户
                        if (localDevice.getState() != device.getState()) {
                            localDevice.setState(device.getState());
                            if (mDeviceStateChangedListener != null) {
                                mDeviceStateChangedListener.onChanged(did, localDevice.getState());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 用于刷新设备状态
     * 在SDK开始运行时启动
     * 在SDK结束时销毁
     */
    private class FreshDeviceStateThread extends Thread {
        @Override
        public void run() {
            super.run();

            // SDK运行时期一直刷新
            while (mSDKRunning) {
                ArrayList<BLProbeDevice> listDevice = new ArrayList<>(mMapDevice.size());
                ArrayList<BLProbeDevice> listNeedQuery = new ArrayList<>(mMapDevice.size());

                // 获取所有的设备
                synchronized (mMapDevice) {
                    for (String key : mMapDevice.keySet()) {
                        listDevice.add(mMapDevice.get(key));
                    }
                }

                for (BLProbeDevice device : listDevice) {
                    //如果是虚拟设备则不用刷新状态，直接默认本地
                    try {
                        if(device.getExtendObject() != null && device.getExtendObject().optInt("protocol") == BLDevProtocol.VIRTUAL){
                            device.setState(BLDeviceState.LOCAL);
                            continue;
                        }
                    }catch (Exception e){}

                    //子设备不查询状态
                    if(!TextUtils.isEmpty(device.getpDid()))
                        continue;

                    //没有Mac地址设备也不查询状态
                    if(TextUtils.isEmpty(device.getMac()))
                        continue;

                    long curTime = System.currentTimeMillis();

                    if (device.getState() == BLDeviceState.UNKNOWN) {
                        // 未知设备需要查询远程状态
                        listNeedQuery.add(device);
                    } else if (device.getState() == BLDeviceState.LOCAL) {
                        if (curTime - device.getFreshStateTime() > DEV_STATE_FRESH_TIMEOUT) {
                            // 局域网设备超过30秒未刷新出来则视为远程并查询远程状态
                            device.setState(BLDeviceState.REMOTE);
                            device.setLanaddr(null);
                            listNeedQuery.add(device);

                            // 状态改变，通知App
                            if (mDeviceStateChangedListener != null) {
                                mDeviceStateChangedListener.onChanged(device.getDid(), BLDeviceState.REMOTE);
                            }
                        }
                    } else if (device.getState() == BLDeviceState.REMOTE) {
                        if (curTime - device.getFreshStateTime() > DEV_STATE_FRESH_TIMEOUT) {
                            // 远程设备超过30秒未刷新则重新获取状态
                            listNeedQuery.add(device);
                        }
                    } else if (device.getState() == BLDeviceState.OFFLINE) {
                        if (curTime - device.getFreshStateTime() > DEV_STATE_FRESH_TIMEOUT) {
                            // 离线设备超过30秒未刷新则重新获取状态
                            listNeedQuery.add(device);
                        }
                    }
                }

                // 交给查询线程异步完成任务
                queryDevicesStatusOnServer(listNeedQuery);

                try {
                    Thread.sleep(FRESH_INTERVAL);
                } catch (InterruptedException e) {
                    BLCommonTools.handleError(e);
                }
            }
        }
    }

    /**
     * 工具方法，把Json对象转为Java对象
     */
    private void parseTask(JSONObject jData, ArrayList<BLTimerInfo> listTimer, ArrayList<BLTimerInfo> listDelay,
                           ArrayList<BLPeriodInfo> listPeriod, ArrayList<BLCycleInfo> listCycle, ArrayList<BLCycleInfo> listRandom) {
        if (jData != null) {
            try {
                JSONArray jArrayTimer = jData.optJSONArray("timerlist");
                if (jArrayTimer != null && jArrayTimer.length() > 0) {
                    for (int i = 0; i < jArrayTimer.length(); i++) {
                        JSONObject jTimer = jArrayTimer.optJSONObject(i);
                        BLTimerInfo timer = new BLTimerInfo();
                        timer.setIndex(jTimer.optInt("index"));
                        timer.setEnable(jTimer.optBoolean("enable"));
                        String[] s = jTimer.optString("time", null).split(" ");
                        String[] day = s[0].split("-");
                        String[] time = s[1].split(":");

                        timer.setYear(Integer.parseInt(day[0]));
                        timer.setMonth(Integer.parseInt(day[1]));
                        timer.setDay(Integer.parseInt(day[2]));

                        timer.setHour(Integer.parseInt(time[0]));
                        timer.setMin(Integer.parseInt(time[1]));
                        timer.setSec(Integer.parseInt(time[2]));

                        listTimer.add(timer);
                    }
                }

                JSONArray jArrayDelay = jData.optJSONArray("delaylist");
                if (jArrayDelay != null && jArrayDelay.length() > 0) {
                    for (int i = 0; i < jArrayDelay.length(); i++) {
                        JSONObject jTimer = jArrayTimer.optJSONObject(i);
                        BLTimerInfo timer = new BLTimerInfo();
                        timer.setIndex(jTimer.optInt("index"));
                        timer.setEnable(jTimer.optBoolean("enable"));
                        String[] s = jTimer.optString("time", null).split(" ");
                        String[] day = s[0].split("-");
                        String[] time = s[1].split(":");

                        timer.setYear(Integer.parseInt(day[0]));
                        timer.setMonth(Integer.parseInt(day[1]));
                        timer.setDay(Integer.parseInt(day[2]));

                        timer.setHour(Integer.parseInt(time[0]));
                        timer.setMin(Integer.parseInt(time[1]));
                        timer.setSec(Integer.parseInt(time[2]));

                        listDelay.add(timer);
                    }
                }

                JSONArray jArrayPeriod = jData.optJSONArray("periodlist");
                if (jArrayPeriod != null && jArrayPeriod.length() > 0) {
                    for (int i = 0; i < jArrayPeriod.length(); i++) {
                        JSONObject jPeriod = jArrayPeriod.optJSONObject(i);
                        BLPeriodInfo period = new BLPeriodInfo();
                        period.setIndex(jPeriod.optInt("index"));
                        period.setEnable(jPeriod.optBoolean("enable"));
                        String[] time = jPeriod.optString("time", null).split(":");

                        JSONArray jArrayRepeat = jPeriod.optJSONArray("repeat");
                        int j = 0;
                        for (; j < jArrayRepeat.length(); j++) {
                            period.getRepeat().add(jArrayRepeat.optInt(j));
                        }

                        period.setHour(Integer.parseInt(time[0]));
                        period.setMin(Integer.parseInt(time[1]));
                        period.setSec(Integer.parseInt(time[2]));

                        listPeriod.add(period);
                    }
                }

                JSONArray jArrayCycle = jData.optJSONArray("cyclelist");
                if (jArrayCycle != null && jArrayCycle.length() > 0) {
                    for (int i = 0; i < jArrayCycle.length(); i++) {
                        JSONObject jCycle = jArrayCycle.optJSONObject(i);
                        BLCycleInfo cycleInfo = new BLCycleInfo();
                        cycleInfo.setIndex(jCycle.optInt("index"));
                        cycleInfo.setEnable(jCycle.optBoolean("enable"));
                        cycleInfo.setCmd1duration(jCycle.optInt("cmd1duration"));
                        cycleInfo.setCmd2duration(jCycle.optInt("cmd2duration"));

                        JSONArray jArrayRepeat = jCycle.optJSONArray("repeat");
                        int j = 0;
                        for (; j < jArrayRepeat.length(); j++) {
                            cycleInfo.getRepeat().add(jArrayRepeat.optInt(j));
                        }

                        String[] time = jCycle.optString("time", null).split(":");
                        cycleInfo.setStart_hour(Integer.parseInt(time[0]));
                        cycleInfo.setStart_min(Integer.parseInt(time[1]));
                        cycleInfo.setStart_sec(Integer.parseInt(time[2]));

                        String[] endTime = jCycle.optString("endtime", null).split(":");
                        cycleInfo.setEnd_hour(Integer.parseInt(endTime[0]));
                        cycleInfo.setEnd_min(Integer.parseInt(endTime[1]));
                        cycleInfo.setEnd_sec(Integer.parseInt(endTime[2]));

                        listCycle.add(cycleInfo);
                    }
                }


                JSONArray jArrayRandom = jData.optJSONArray("randomlist");
                if (jArrayRandom != null && jArrayRandom.length() > 0) {
                    for (int i = 0; i < jArrayRandom.length(); i++) {
                        JSONObject jCycle = jArrayRandom.optJSONObject(i);
                        BLCycleInfo cycleInfo = new BLCycleInfo();
                        cycleInfo.setIndex(jCycle.optInt("index"));
                        cycleInfo.setEnable(jCycle.optBoolean("enable"));
                        cycleInfo.setCmd1duration(jCycle.optInt("cmd1duration"));
                        cycleInfo.setCmd2duration(jCycle.optInt("cmd2duration"));

                        JSONArray jArrayRepeat = jCycle.optJSONArray("repeat");
                        int j = 0;
                        for (; j < jArrayRepeat.length(); j++) {
                            cycleInfo.getRepeat().add(jArrayRepeat.optInt(j));
                        }

                        String[] time = jCycle.optString("time", null).split(":");
                        cycleInfo.setStart_hour(Integer.parseInt(time[0]));
                        cycleInfo.setStart_min(Integer.parseInt(time[1]));
                        cycleInfo.setStart_sec(Integer.parseInt(time[2]));

                        String[] endTime = jCycle.optString("endtime", null).split(":");
                        cycleInfo.setEnd_hour(Integer.parseInt(endTime[0]));
                        cycleInfo.setEnd_min(Integer.parseInt(endTime[1]));
                        cycleInfo.setEnd_sec(Integer.parseInt(endTime[2]));

                        listRandom.add(cycleInfo);
                    }
                }

            } catch (Exception e) {
                BLCommonTools.handleError(e);
            }
        }
    }

    private BLGatewayTranslateResult gatewayTranslate(String did, String paramStr, int dir, String action, String data, BLConfigParam configParam) {
        BLGatewayTranslateResult result = new BLGatewayTranslateResult();

        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");

            return result;
        }

        JSONObject jDesc = new JSONObject();
        try {
            jDesc.put("action", action);
            jDesc.put("dir", dir);

            if (data != null) {
                jDesc.put("resp", data);
            }

            if (configParam != null) { // 脚本路径
                String scriptpath = configParam.get(BLConfigParam.CONTROLLER_SCRIPT_PATH);
                if (scriptpath != null) {
                    jDesc.put("scriptfile", scriptpath);
                }
            }

            BLCommonTools.debug("gatewayTranslate paramStr: " + paramStr);
            BLCommonTools.debug("gatewayTranslate descStr: " + jDesc.toString());
            String sResult = mNetworkAPI.deviceSubControlTranslate(device.toJSONString(), paramStr, jDesc.toString());
            BLCommonTools.debug("gatewayTranslate result: " + sResult);

            JSONObject jResult = new JSONObject(sResult);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if (result.succeed()) {
                result.setData(jResult.optString("data", null));
                result.setEventcode(jResult.optInt("eventcode"));

                JSONArray jKeyMap = jResult.optJSONArray("keys");
                if (jKeyMap != null) {
                    BLCommonTools.debug("Key Length :" + String.valueOf(jKeyMap.length()));
                    String[] keymap = new String[jKeyMap.length()];
                    for (int i = 0; i < jKeyMap.length(); i++) {
                        keymap[i] = jKeyMap.optString(i, null);
                        BLCommonTools.debug("Key :" + keymap[i]);
                    }
                    result.setKeys(keymap);
                } else {
                    BLCommonTools.debug("gatewayTranslate result: jKeyMap is null");
                }

                JSONArray jDataMap = jResult.optJSONArray("privatedatas");
                if (jDataMap != null) {
                    BLCommonTools.debug("jDataMap Length :" + String.valueOf(jDataMap.length()));
                    String[] datamap = new String[jDataMap.length()];
                    for (int i = 0; i < jDataMap.length(); i++) {

                        datamap[i] = jDataMap.optString(i, null);
                        BLCommonTools.debug("privatedatas :" + datamap[i]);
                    }
                    result.setPrivatedatas(datamap);
                }else {
                    BLCommonTools.debug("gatewayTranslate result: jDataMap is null");
                }
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return result;
    }

    private BLControllerDNAControlResult handleGatewayResult(BLGatewayTranslateResult result, String did, String paramStr, String action, BLConfigParam configParam) {
        BLControllerDNAControlResult controllerDNAControlResult = new BLControllerDNAControlResult();

        if (result.succeed()) {
            BLGatewayTranslateResult blGatewayTranslateResult = new BLGatewayTranslateResult();

            int eventcode = result.getEventcode();
            BLCommonTools.debug("handleGatewayResult eventcode = " + String.valueOf(eventcode));
            String data = result.getData();

            switch (eventcode) {
                case 0: {   //该指令需要发送给网关设备
                    JSONObject jPassData = new JSONObject();
                    try {
                        jPassData.put("data", data);
                    } catch (JSONException e) {
                        BLCommonTools.handleError(e);
                    }

                    BLCommonTools.debug("handleGatewayResult passthough start: " + jPassData.toString());
                    BLControllerDNAControlResult controlResult = autoDNAControl(did, null, jPassData.toString(), BLDevCmdConstants.DEV_PASSTHROUGH, configParam);
                    BLCommonTools.debug("handleGatewayResult passthough result: " + controlResult.getMsg());

                    if (controlResult.succeed()) {
                        String respData = controlResult.getData().optString("resp", null);
                        blGatewayTranslateResult = gatewayTranslate(did, paramStr, 2, action, respData, configParam);
                    } else {
                        blGatewayTranslateResult.setStatus(controlResult.getStatus());
                        blGatewayTranslateResult.setMsg(controlResult.getMsg());
                    }
                }
                    break;
                case 1:{    //该指令直接传回到脚本
                    blGatewayTranslateResult = gatewayTranslate(did, paramStr, 2, action, data, configParam);
                }
                    break;
                case 2: {   //该指令需要从云端获取信息
                    JSONObject jRespData = new JSONObject();

                    if (action.equalsIgnoreCase(BLDevCmdConstants.DEV_NEWSUBDEV_LIST)) {
                        // 从云端获取到did
                        BLCommonTools.debug("Query private data id from server");

                        BLPrivateDataIdResult privateDataIdResult = BLLet.Family.getFamilyPrivateDataId();
                        if (!privateDataIdResult.succeed()) {
                            controllerDNAControlResult.setMsg(privateDataIdResult.getMsg());
                            controllerDNAControlResult.setStatus(privateDataIdResult.getStatus());
                            return controllerDNAControlResult;
                        }
                        String privateDataId = privateDataIdResult.getDataId();

                        try {
                            jRespData.put("did", privateDataId);
                        } catch (JSONException e) {
                            BLCommonTools.handleError(e);
                        }

                    } else {
                        //从云端获取key对应的数据
                        String[] keys = result.getKeys();
                        if (keys != null && keys.length > 0) {
                            //存在key列表，则从云端获取数据
                            BLCommonTools.debug("Query keys and privateDatas");
                            String mkeyid = keys[0];

                            BLPrivateDataResult privateDataIdResult = BLLet.Family.queryFamilyPrivateData(mkeyid);
                            if (!privateDataIdResult.succeed()) {
                                controllerDNAControlResult.setMsg(privateDataIdResult.getMsg());
                                controllerDNAControlResult.setStatus(privateDataIdResult.getStatus());
                                return controllerDNAControlResult;
                            }

                            try {
                                JSONArray jDataList = new JSONArray();
                                for (BLPrivateData pdata: privateDataIdResult.getDataList()) {
                                    String content = pdata.getContent();
                                    jDataList.put(content);
                                }
                                jRespData.put("list", jDataList);
                            } catch (JSONException e) {
                                BLCommonTools.handleError(e);
                            }
                        }
                    }

                    String respData = Base64.encodeToString(jRespData.toString().getBytes(), Base64.NO_WRAP);
                    blGatewayTranslateResult = gatewayTranslate(did, paramStr, 2, action, respData, configParam);
                }
                    break;
                case 3: {   //该指令需要发送给云端
                    String[] keys = result.getKeys();
                    String[] privateDatas = result.getPrivatedatas();

                    if (keys != null && keys.length > 0) { //存在key列表，则向云端上报数据

                            if (privateDatas !=null && privateDatas.length > 0) {
                                //云端数据保存
                                BLCommonTools.debug("Update keys and privateDatas");

                                List<BLPrivateData> updateDataList = new ArrayList<>();
                                for (int i = 0; i < keys.length; i++) {
                                    BLCommonTools.debug("key = " + keys[i] + " privateData = " + privateDatas[i]);

                                    BLPrivateData privateData = new BLPrivateData();
                                    privateData.setMkeyid(keys[i]);
                                    privateData.setContent(privateDatas[i]);

                                    updateDataList.add(privateData);
                                }

                                BLPrivateDataResult privateDataResult = BLLet.Family.updateFamilyPrivateData(updateDataList);
                                if (!privateDataResult.succeed()) {
                                    controllerDNAControlResult.setMsg(privateDataResult.getMsg());
                                    controllerDNAControlResult.setStatus(privateDataResult.getStatus());
                                    return controllerDNAControlResult;
                                }

                        } else {
                            //云端数据删除
                            BLCommonTools.debug("Delete keys");
                            List<BLPrivateData> deleteDataList = new ArrayList<>();
                            for (int i = 0; i < keys.length; i++) {
                                BLCommonTools.debug("Delete key = " + keys[i]);
                                BLPrivateData privateData = new BLPrivateData();
                                privateData.setMkeyid(keys[i]);
                                deleteDataList.add(privateData);
                            }

                            BLBaseResult deleteDataResult = BLLet.Family.deleteFamilyPrivateData(deleteDataList);
                            if (!deleteDataResult.succeed()) {
                                controllerDNAControlResult.setMsg(deleteDataResult.getMsg());
                                controllerDNAControlResult.setStatus(deleteDataResult.getStatus());
                                return controllerDNAControlResult;
                            }
                        }
                    }

                    //上报成功之后，该指令直接传回到脚本
                    blGatewayTranslateResult = gatewayTranslate(did, paramStr, 2, action, data, configParam);
                }
                    break;
                default: {
                    blGatewayTranslateResult.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
                    blGatewayTranslateResult.setMsg("unknown error");
                }
            }

            if (blGatewayTranslateResult.succeed()) {
                try {
                    JSONObject jData = new JSONObject(blGatewayTranslateResult.getData());
                    controllerDNAControlResult.setMsg(blGatewayTranslateResult.getMsg());
                    controllerDNAControlResult.setStatus(blGatewayTranslateResult.getStatus());
                    controllerDNAControlResult.setData(jData);
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            } else {
                controllerDNAControlResult.setMsg(blGatewayTranslateResult.getMsg());
                controllerDNAControlResult.setStatus(blGatewayTranslateResult.getStatus());
            }

        } else if (result.getStatus() == BLControllerErrCode.ORDER_TO_DEVICE_FAIL || result.getStatus() == BLControllerErrCode.FILE_FAIL) {
            //脚本解析错误 走命令直接发送模式
            controllerDNAControlResult = autoDNAControl(did, null, paramStr, action, configParam);
        } else {
            controllerDNAControlResult.setMsg(result.getMsg());
            controllerDNAControlResult.setStatus(result.getStatus());
        }

        return controllerDNAControlResult;
    }

    /**
     * 通知网关设备进入扫描子设备的模式
     */
    public BLSubdevResult devNewSubDevScanStart(String did, String pid, BLConfigParam configParam){
        BLSubdevResult result = new BLSubdevResult();

        String paramStr = "{}";
        if (pid != null) {
            try {
                JSONObject paramJSONObject = new JSONObject();
                paramJSONObject.put("pid", pid);
                paramStr = paramJSONObject.toString();
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        }

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, paramStr, 1, BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_START, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, paramStr, BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_START, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));
        }

        return result;
    }

    /**
     * 通知网关设备退出扫描子设备的模式
     */
    public BLSubdevResult devNewSubDevScanStop(String did, BLConfigParam configParam){
        BLSubdevResult result = new BLSubdevResult();

        String paramStr = "{}";

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, paramStr, 1, BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_STOP, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, paramStr, BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_STOP, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));
        }

        return result;
    }

    /**
     * 获取扫描到的新子设备的列表
     */
    public BLSubDevListResult devGetNewSubDevList(String did, String subpid, int index, int count, BLConfigParam configParam){
        BLSubDevListResult result = new BLSubDevListResult();
        String paramStr = "{}";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count", count);
            jsonObject.put("index", index);
            if (subpid != null) {
                jsonObject.put("pid", subpid);
            }

            paramStr = jsonObject.toString();
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, paramStr, 1, BLDevCmdConstants.DEV_NEWSUBDEV_LIST, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, paramStr, BLDevCmdConstants.DEV_NEWSUBDEV_LIST, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));

            //判断获取子设备是否成功成功的话，解析出子设备数据
            if (result.succeed()) {
                result.setData(parseSubDevInfoData(did, subData));
            }
        }

        return result;
    }



    /**
     * 添加新子设备到网关设备
     */
    public BLSubdevResult devSubDevAdd(String did, BLDNADevice subDevInfo, BLConfigParam configParam){
        String paramStr = null;
        String url = null;
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("action", "download");
            paramObject.put("type", "manuf");
            paramObject.put("pid", subDevInfo.getPid());
            JSONObject extrainfoObject = new JSONObject();
            extrainfoObject.put("platform", "bl");
            paramObject.put("extrainfo", extrainfoObject);

            paramStr = paramObject.toString();

            url = BLApiUrls.APPManager.URL_DOWNLOAD() + "?resourcetype=manuf&pid=" + subDevInfo.getPid();
            // 固件不支持https
            url = url.replace("https://", "http://");

            String host = BLCommonTools.urlHost(url);
            String ipaddress = BLCommonTools.hostInetAddress(host);

            if (!TextUtils.isEmpty(ipaddress)) {
                url = url.replace(host, ipaddress);
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return devSubDevAdd(did, url, paramStr, subDevInfo, configParam);
    }

    /**
     * 添加新子设备到网关设备
     */
    public BLSubdevResult devSubDevAdd(String did, String url, String param, BLDNADevice subDevInfo, BLConfigParam configParam){
        String subDevInfoStr = null;
        try {
            JSONObject jDevice = new JSONObject();
            jDevice.put("did", subDevInfo.getDid());
            jDevice.put("mac", subDevInfo.getMac());
            jDevice.put("pid", subDevInfo.getPid());
            jDevice.put("name", subDevInfo.getName());
            jDevice.put("type", subDevInfo.getType());
            jDevice.put("lock", subDevInfo.isLock());
            jDevice.put("password", subDevInfo.getPassword());
            jDevice.put("id", subDevInfo.getId());
            jDevice.put("key", subDevInfo.getKey());
            jDevice.put("url", url);
            jDevice.put("param", param);
            subDevInfoStr = jDevice.toString();
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        BLSubdevResult result = new BLSubdevResult();
        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, subDevInfoStr, 1, BLDevCmdConstants.DEV_SUBDEV_ADD, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, subDevInfoStr, BLDevCmdConstants.DEV_SUBDEV_ADD, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));
        }

        return result;
    }

    /**
     * 添加新子设备进度查询
     */
    public BLSubDevAddResult devSubDevAddResultQuery(String did, String sDid, BLConfigParam configParam){
        BLSubDevAddResult result = new BLSubDevAddResult();
        String dataString = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("did", sDid);
            dataString = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, dataString, 1, BLDevCmdConstants.DEV_SUBDEV_ADD_RESULT, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, dataString, BLDevCmdConstants.DEV_SUBDEV_ADD_RESULT, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));

            if(result.succeed()){
                result.setDownload_status(handleResult.getData().optInt("download_status"));
            }

        }

        return result;
    }

    /**
     * 从网关设备中删除指定的子设备
     */
    public BLSubdevResult devSubDevDel(String did, String sDid, BLConfigParam configParam){
        BLSubdevResult result = new BLSubdevResult();
        String dataString = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("did", sDid);
            dataString = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, dataString, 1, BLDevCmdConstants.DEV_SUBDEV_DEL, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, dataString, BLDevCmdConstants.DEV_SUBDEV_DEL, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));
        }

        return result;
    }


    /**
     * 修改网关设备中指定的子设备信息
     */
    public BLSubdevResult devSubDevModify(String did, String sDid, String name, boolean lock, short type, BLConfigParam configParam){
        BLSubdevResult result = new BLSubdevResult();

        BLProbeDevice subDevInfo = null;

        if(sDid != null){
            subDevInfo  = mMapDevice.get(sDid);
        }

        if (subDevInfo == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find sub device");

            return result;
        }

        String dataStr = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("lock", lock);
            jsonObject.put("type", type);

            jsonObject.put("did", subDevInfo.getDid());
            jsonObject.put("pid", subDevInfo.getPid());
            dataStr = jsonObject.toString();
        } catch (Exception e) {}

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, dataStr, 1, BLDevCmdConstants.DEV_SUBDEV_MODIFY, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, dataStr, BLDevCmdConstants.DEV_SUBDEV_MODIFY, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));

            if(result.succeed()){
                subDevInfo.setName(name);
                subDevInfo.setLock(lock);
                subDevInfo.setType(type);
            }
        }

        return null;
    }

    /**
     * 查询子设备版本
     */
    public BLFirmwareVersionResult devSubDevVersion(String did, String sDid, BLConfigParam configParam){
        BLFirmwareVersionResult result = new BLFirmwareVersionResult();

        BLProbeDevice subDevInfo = null;

        if(sDid != null){
            subDevInfo  = mMapDevice.get(sDid);
        }

        if (subDevInfo == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find sub device");

            return result;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("did", sDid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String dataStr = jsonObject.toString();

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, dataStr, 1, BLDevCmdConstants.DEV_SUBDEV_VERSION, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, dataStr, BLDevCmdConstants.DEV_SUBDEV_VERSION, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));

            if(result.succeed()){
                int fwversion = handleResult.getData().optInt("fwversion");
                int hwversion = handleResult.getData().optInt("hwversion");
                result.setVersion(fwversion + "-" + hwversion);
            }
        }

        return result;
    }

    /**
     * 获取网关设备中已经添加的子设备列表
     */
    public BLSubDevListResult devGetSubDevList(String did,int index, int count, BLConfigParam configParam){
        BLSubDevListResult result = new BLSubDevListResult();
        String dataStr = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count", count);
            jsonObject.put("index", index);
            dataStr = jsonObject.toString();
        } catch (Exception e) {}

        // 1. 根据脚本，获取发送数据
        BLGatewayTranslateResult gatewayTranslateResult = gatewayTranslate(did, dataStr, 1, BLDevCmdConstants.DEV_SUBDEV_LIST, null, configParam);

        // 2. 根据脚本返回数据处理
        BLControllerDNAControlResult handleResult = handleGatewayResult(gatewayTranslateResult, did, dataStr, BLDevCmdConstants.DEV_SUBDEV_LIST, configParam);

        // 3. 结果输出
        result.setStatus(handleResult.getStatus());
        result.setMsg(handleResult.getMsg());

        JSONObject subData = handleResult.getData();
        if (subData != null) {
            result.setSubdevStatus(subData.optInt("status"));

            //判断获取子设备是否成功成功的话，解析出子设备数据
            if (result.succeed()) {
                result.setData(parseSubDevInfoData(did, subData));
            }
        }

        return result;
    }

    public BLAPConfigResult deviceAPConfig(String ssid, String password, int type, BLConfigParam configParam) {
        JSONObject jsonObject = new JSONObject();

        int ltimeout = 10 * 1000;
        if(configParam != null){
            // 本地超时
            String ltimeoutStr= configParam.get(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT);
            if (ltimeoutStr != null) {
                try {
                    ltimeout = Integer.parseInt(ltimeoutStr);
                } catch (Exception e) {
                    BLCommonTools.handleError(e);
                }
            }
        }

        try {
            jsonObject.put("ssid", ssid);
            jsonObject.put("password", password);
            jsonObject.put("type", type);
            jsonObject.put("timeout", ltimeout);
            jsonObject.put("sendcount", 1);
        } catch (Exception e) {}

        BLCommonTools.debug("deviceAPConfig");
        String resultStr = mNetworkAPI.deviceAPConfig(jsonObject.toString());
        BLCommonTools.debug("Controller dnaControl result: " + resultStr);

        BLAPConfigResult result = new BLAPConfigResult();
        try {
            JSONObject jResult = new JSONObject(resultStr);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
            if (result.succeed()) {
                JSONObject jDeviceInfo = jResult.optJSONObject("devinfo");
                if (jDeviceInfo != null) {
                    result.setDid(jDeviceInfo.optString("did", null));
                    result.setPid(jDeviceInfo.optString("pid", null));
                    result.setSsid(jDeviceInfo.optString("ssid", null));
                    result.setDevkey(jDeviceInfo.optString("devkey", null));
                }
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("Error Unknown");
        }

        return result;
    }

    public BLGetAPListResult deviceGetAPList(int timeout) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timeout", timeout);
            jsonObject.put("sendcount", 1);
        } catch (Exception e) {}

        BLCommonTools.debug("deviceGetAPList");
        String resultStr = mNetworkAPI.deviceGetAPList(jsonObject.toString());
        BLCommonTools.debug("Controller dnaControl result: " + resultStr);

        BLGetAPListResult result = new BLGetAPListResult();
        try {
            JSONObject jResult = new JSONObject(resultStr);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));

            if(result.succeed()){
                JSONArray jsonArray = jResult.optJSONArray("list");
                if(jsonArray != null && jsonArray.length() > 0){
                    for(int i = 0; i < jsonArray.length(); i ++){
                        JSONObject apJsonObject = jsonArray.getJSONObject(i);
                        BLAPInfo blapInfo = new BLAPInfo();
                        blapInfo.setSsid(apJsonObject.optString("ssid"));
                        blapInfo.setRssi(apJsonObject.optInt("rssi"));
                        blapInfo.setType(apJsonObject.optInt("type"));
                        result.getList().add(blapInfo);
                    }
                }
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /***
     * 是否是虚拟设备
     */
    private boolean isVirtualDevice(String filePath, String pid, String sPid){
        if(filePath != null && !filePath.equals("")){
            return filePath.endsWith(".js");
        }else if(filePath == null || filePath.equals("")){
            String fileName = TextUtils.isEmpty(sPid) ? pid : sPid;
            filePath = BLFileStorageUtils.getDefaultJSScriptPath(fileName);
        }
        return new File(filePath).exists();
    }

    private boolean isVirtualDevJsSupportCmd(String command){
        return command.equals(BLDevCmdConstants.DEV_CTRL) || command.equals(BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_START) || command.equals(BLDevCmdConstants.DEV_NEWSUBDEV_SCAN_STOP)
                || command.equals(BLDevCmdConstants.DEV_NEWSUBDEV_LIST) || command.equals(BLDevCmdConstants.DEV_SUBDEV_LIST) || command.equals(BLDevCmdConstants.DEV_SUBDEV_ADD)
                || command.equals(BLDevCmdConstants.DEV_SUBDEV_ADD_RESULT) || command.equals(BLDevCmdConstants.DEV_SUBDEV_DEL) || command.equals(BLDevCmdConstants.DEV_SUBDEV_MODIFY);
    }

    /**
     * 设备授权
     */
    public BLBaseResult dnaProxyAuth(String did, String accesskey, String tpid, String attr) {
        BLBaseResult result = new BLBaseResult();

        if (accesskey == null) {
            result.setStatus(BLAppSdkErrCode.ERR_ACCESS_KEY_NULL);
            result.setMsg("access key null");
            return result;
        }

        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");
            return result;
        }

        String accountId = mUserid;
        String accountSessiont = mUserserssion;

        if (accountId == null || accountSessiont == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("not login");
            return result;
        }
        try {
            Map<String, String> mapSession = new HashMap<>(2);
            mapSession.put("userid", accountId);
            mapSession.put("accesskey", accesskey);

            JSONObject jParam = new JSONObject();
            jParam.put("license", mLicense);
            jParam.put("did", device.getDid());
            jParam.put("pid", device.getPid());
            jParam.put("devkeyid", String.valueOf(device.getId()));
            jParam.put("devkey", device.getKey());
            jParam.put("tpid", tpid);
            jParam.put("attr", attr);
            String json = jParam.toString();

            String httpResult = BLProxyHttpAccessor.generalPost(BLApiUrls.Proxy.URL_AUTH_DNA_PROXY(), mapSession, json, mHttpTimeOut);
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));

                return  result;
            } else  {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
                return result;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return null;
    }

    /**
     * 取消设备授权
     */
    public BLBaseResult dnaProxyDisauth(String did, String accesskey) {
        BLBaseResult result = new BLBaseResult();

        if (accesskey == null) {
            result.setStatus(BLAppSdkErrCode.ERR_ACCESS_KEY_NULL);
            result.setMsg("access key null");
            return result;
        }

        BLProbeDevice device = mMapDevice.get(did);
        if (device == null) {
            result.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            result.setMsg("cannot find device");
            return result;
        }

        String accountId = mUserid;
        String accountSessiont = mUserserssion;

        if (accountId == null || accountSessiont == null) {
            result.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            result.setMsg("not login");
            return result;
        }

        try {
            Map<String, String> mapSession = new HashMap<>(2);
            mapSession.put("userid", accountId);
            mapSession.put("accesskey", accesskey);

            JSONObject jParam = new JSONObject();
            jParam.put("license", mLicense);
            jParam.put("did", device.getDid());
            jParam.put("pid", device.getPid());
            String json = jParam.toString();

            String httpResult = BLProxyHttpAccessor.generalPost(BLApiUrls.Proxy.URL_DISAUTH_DNA_PROXY(), mapSession, json, mHttpTimeOut);
            if (httpResult != null) {
                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                return  result;
            } else  {
                result.setStatus(BLAppSdkErrCode.ERR_SERVER_NO_RESULT);
                result.setMsg(ERR_SERVER_NO_RESULT);
                return result;
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return null;
    }


    /**
     * 控制指令转为Json对象
     */
    private JSONObject parseStdDataJsonObject(BLStdData stdData) {
        JSONObject jData = new JSONObject();

        try {
            JSONArray jArrayParam = new JSONArray();
            for (String param : stdData.getParams()) {
                jArrayParam.put(param);
            }

            jData.put("params", jArrayParam);

            JSONArray jVals = new JSONArray();
            for (ArrayList<BLStdData.Value> listVal : stdData.getVals()) {
                JSONArray jArrayVal = new JSONArray();

                for (BLStdData.Value val : listVal) {
                    JSONObject jVal = new JSONObject();
                    jVal.put("val", val.getVal());
                    jVal.put("idx", val.getIdx());

                    jArrayVal.put(jVal);
                }

                jVals.put(jArrayVal);
            }

            jData.put("vals", jVals);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return jData;
    }

    /**
     * Json对象转为控制指令
     */
    public BLStdData parseStdData(JSONObject jData) {
        BLStdData stdData = new BLStdData();
        try {
            if (jData != null) {

                // 设置Params
                ArrayList<String> params = new ArrayList<>();
                JSONArray jArrayParams = jData.optJSONArray("params");
                for (int i = 0; i < jArrayParams.length(); i++) {
                    params.add(jArrayParams.optString(i, null));
                }

                stdData.setParams(params);

                // 设置vals
                ArrayList<ArrayList<BLStdData.Value>> vals = new ArrayList<>();
                JSONArray jArrayVals = jData.optJSONArray("vals");
                for (int i = 0; i < jArrayVals.length(); i++) {
                    JSONArray jArrayVal = jArrayVals.optJSONArray(i);
                    ArrayList<BLStdData.Value> listVal = new ArrayList<>();
                    for (int j = 0; j < jArrayVal.length(); j++) {
                        BLStdData.Value val = new BLStdData.Value();
                        JSONObject jVal = jArrayVal.optJSONObject(j);
                        val.setIdx(jVal.optInt("idx"));
                        val.setVal(jVal.opt("val"));
                        listVal.add(val);
                    }
                    vals.add(listVal);
                }

                stdData.setVals(vals);
            }
        }catch (Exception e){
        }
        return stdData;
    }

    /**
     * Json对象转为控制指令
     */
    private BLSubDevListInfo parseSubDevInfoData(String pDid, JSONObject jData) {
        BLSubDevListInfo subDevListInfo = new BLSubDevListInfo();
        if (jData != null) {
            subDevListInfo.setTotal(jData.optInt("total"));
            subDevListInfo.setIndex(jData.optInt("index"));

            // 设置Params
            JSONArray jArrayParams = jData.optJSONArray("list");

            List<BLDNADevice> subList = new ArrayList<>();

            if(jArrayParams != null){
                for (int i = 0; i < jArrayParams.length(); i++) {
                    BLDNADevice dnaDevice = new BLDNADevice();
                    JSONObject subDevJSONObject = jArrayParams.optJSONObject(i);

                    // 生成对象
                    dnaDevice.setDid(subDevJSONObject.optString("did", null));
                    dnaDevice.setMac(subDevJSONObject.optString("mac", null));
                    dnaDevice.setPid(subDevJSONObject.optString("pid", null));
                    dnaDevice.setName(subDevJSONObject.optString("name", null));
                    dnaDevice.setType(subDevJSONObject.optInt("type"));
                    dnaDevice.setLock(subDevJSONObject.optBoolean("lock"));
                    dnaDevice.setRoomtype(subDevJSONObject.optInt("defroomtype"));
                    dnaDevice.setpDid(pDid);
                    subList.add(dnaDevice);
                }
            }

            subDevListInfo.setList(subList);
        }

        return subDevListInfo;
    }

    private String ctrlParamObjectToStr(BLStdControlParam stdControlParam){
        /** 解析控制 **/
        JSONObject jData = new JSONObject();
        try {
            jData.put("prop", stdControlParam.getProp());
            jData.put("act", stdControlParam.getAct());
            jData.put("did", stdControlParam.getDid());

            if (stdControlParam.getSrv() != null) {
                jData.put("srv", stdControlParam.getSrv());
            }

            if (stdControlParam.getPassword() != null) {
                jData.put("password", stdControlParam.getPassword());
            }

            JSONArray jParams = new JSONArray();
            for (String param : stdControlParam.getParams()) {
                jParams.put(param);
            }
            jData.put("params", jParams);

            JSONArray jVals = new JSONArray();
            for (ArrayList<BLStdControlParam.Value> listVal : stdControlParam.getVals()) {
                JSONArray jArrayVal = new JSONArray();

                for (BLStdControlParam.Value val : listVal) {
                    JSONObject jVal = new JSONObject();
                    jVal.put("val", val.getVal());
                    jVal.put("idx", val.getIdx());

                    jArrayVal.put(jVal);
                }

                jVals.put(jArrayVal);
            }

            jData.put("vals", jVals);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        return jData.toString();
    }

    private void recordDeviceControl(String did, String sDid, String pid, String dataStr, int status, String msg) {
        if (did == null) {
            return;
        }

        try {
            Map<String, Object> dataObject = new HashMap<String, Object>();
            dataObject.put("cat", 10);
            dataObject.put("eventId", 7000);

            List<Integer> featureArray = new ArrayList<>();
            featureArray.add(2);
            dataObject.put("feature", featureArray);

            dataObject.put("did", did);

            if (sDid != null) {
                dataObject.put("sDid", sDid);
            } else {
                dataObject.put("sDid", "");
            }

            dataObject.put("pid", pid);

            if (dataStr != null) {
                dataObject.put("param", dataStr);
            } else {
                dataObject.put("param", "");
            }

            dataObject.put("status", String.valueOf(status));
            if (msg != null) {
                dataObject.put("msg", msg);
            } else {
                dataObject.put("msg", "");
            }

            BLLet.Picker.onEvent(BLConstants.Picker.DATA_Event_ID, BLConstants.Picker.DATA_Event_LABEL, dataObject);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    private void recordAuth(int status, String msg) {
        try {
            Map<String, Object> dataObject = new HashMap<String, Object>();
            dataObject.put("cat", 10);
            dataObject.put("eventId", 7001);

            List<Integer> featureArray = new ArrayList<>();
            dataObject.put("feature", featureArray);

            if (mLicense != null) {
                dataObject.put("license", mLicense);
            } else {
                dataObject.put("license", "");
            }

            if (mUserid != null) {
                dataObject.put("account_id", mUserid);
            } else {
                dataObject.put("account_id", "");
            }

            if (mUserserssion != null) {
                dataObject.put("account_session", mUserserssion);
            } else {
                dataObject.put("account_session", "");
            }

            dataObject.put("status", String.valueOf(status));
            if (msg != null) {
                dataObject.put("msg", msg);
            } else {
                dataObject.put("msg", "");
            }

            BLLet.Picker.onEvent(BLConstants.Picker.DATA_Event_ID, BLConstants.Picker.DATA_Event_LABEL, dataObject);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }
    }

    public BLBaseBodyResult queryDeviceData(String did, String familyId,String startTime, String endTime, String type) {
        BLBaseBodyResult blBaseBodyResult = new BLBaseBodyResult();

        BLProbeDevice blProbeDevice = mMapDevice.get(did);
        if (blProbeDevice == null) {
            blBaseBodyResult.setStatus(BLAppSdkErrCode.ERR_DEVICE_NOT_FOUND);
            blBaseBodyResult.setMsg("cannot find device");

            return blBaseBodyResult;
        }

        if (mUserid == null || mUserserssion == null) {
            // 未登录则返回异常
            blBaseBodyResult.setStatus(BLAppSdkErrCode.ERR_NOT_LOGIN);
            blBaseBodyResult.setMsg("need login");

            return blBaseBodyResult;
        }

        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("userid", mUserid);
        headMap.put("loginsession", mUserserssion);
        String licenseId = BLLet.getLicenseId();
        headMap.put("licenseid", licenseId);
        if (familyId != null) {
            headMap.put("FamilyId", familyId);
        }

        JSONObject jParam = new JSONObject();
        try {
            if (type == null) {
                jParam.put("report", "fw_spminielec_v1");
            } else {
                jParam.put("report", type);
            }

            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("did", did);
            deviceInfo.put("offset", 0);
            deviceInfo.put("step", 0);
            ArrayList<Integer> param = new ArrayList<>();
            deviceInfo.put("param", param);
            deviceInfo.put("start", startTime);
            deviceInfo.put("end", endTime);

            JSONArray deviceArray = new JSONArray();
            deviceArray.put(deviceInfo);

            jParam.put("device", deviceArray);
        } catch (JSONException e) {
            BLCommonTools.handleError(e);
        }

        String url = BLApiUrls.DeviceData.URL_DEVICE_DATA_QUERY(blProbeDevice.getPid());
        String queryResult = BLBaseHttpAccessor.post(url, headMap, jParam.toString().getBytes(), mHttpTimeOut, new BLAccountTrustManager());
        if (queryResult != null) {
            try {
                JSONObject jQuery = new JSONObject(queryResult);
                int code = jQuery.optInt("status", BLAppSdkErrCode.ERR_UNKNOWN);

                blBaseBodyResult.setStatus(code);
                if (blBaseBodyResult.succeed()) {
                    JSONArray jTable = jQuery.optJSONArray("table");
                    if (jTable != null) {
                        blBaseBodyResult.setResponseBody(jTable.toString());
                    }
                    blBaseBodyResult.setMsg("Success");
                } else {
                    blBaseBodyResult.setMsg(jQuery.optString("msg", null));
                }
            } catch (JSONException e) {
                BLCommonTools.handleError(e);
            }
        } else {
            blBaseBodyResult.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            blBaseBodyResult.setMsg("query ui error");
        }

        return blBaseBodyResult;
    }

}
