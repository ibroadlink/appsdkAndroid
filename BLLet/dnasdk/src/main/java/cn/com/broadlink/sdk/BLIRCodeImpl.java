package cn.com.broadlink.sdk;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.com.broadlink.blirdaconlib.BLIrdaConLib;
import cn.com.broadlink.blirdaconlib.BLIrdaConProduct;
import cn.com.broadlink.blirdaconlib.BLIrdaConState;
import cn.com.broadlink.networkapi.NetworkAPI;
import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.param.controller.BLConfigParam;
import cn.com.broadlink.sdk.param.controller.BLQueryIRCodeParams;
import cn.com.broadlink.sdk.result.account.BLLoginResult;
import cn.com.broadlink.sdk.result.controller.BLBaseBodyResult;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLIRCodeDataResult;
import cn.com.broadlink.sdk.result.controller.BLIRCodeInfoResult;

/**
 * Created by zhujunjie on 2017/10/10.
 */

final class BLIRCodeImpl implements BLAccountLoginListener{
    private NetworkAPI mNetworkAPI = null;
    private BLIrdaConLib mBLIrdaConLibPareser = null;

    private String mUserid = null;
    private String mUserserssion = null;
    private String mLid = null;

    // http超时时间设置
    private static final int mHttpTimeOut = 30 * 1000;
    // HTTP 错误返回信息
    private static final String ERR_SERVER_NO_RESULT = "Server has no return data";
    // AES Key
    private static final String STR_RM_KEY_PF = "aas45^#*";

    @Override
    public void onLogin(BLLoginResult loginResult) {
        mUserid = loginResult.getUserid();
        mUserserssion = loginResult.getLoginsession();
    }

    public void init(Context context, String lid, BLConfigParam configParam) {
        // 红外码解析库
        mBLIrdaConLibPareser = new BLIrdaConLib();
        // DNASDK底层库
        mNetworkAPI = NetworkAPI.getInstanceBLNetwork(context);

        mLid = lid;
    }

    /**
     * 查询所有产品的类型列表
     * @return 产品列表
     */
    public BLBaseBodyResult requestIRCodeDeviceTypes() {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("brandid", 0);
            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_QUERY_TYPE(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {
                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * 获取指定产品的品牌列表
     * @param deviceType 产品类型ID
     * @return
     */
    public BLBaseBodyResult requestIRCodeDeviceBrands(int deviceType) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {

            JSONObject jParam = new JSONObject();
            jParam.put("devtypeid", deviceType);
            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_QUERY_BRAND(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * 根据指定产品、品牌、型号获取该设备的红外码脚本下载URL和randkey
     * @param deviceType 指定产品ID
     * @param deviceBrand 指定品牌ID
     * @return
     */
    public BLBaseBodyResult requestIRCodeScriptDownloadUrl(int deviceType, int deviceBrand) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {

            JSONObject jParam = new JSONObject();
            jParam.put("devtypeid", deviceType);
            jParam.put("brandid", deviceBrand);

            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_SCRIPT_GET_URL(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     *  Gets the list of regions under the specified region ID
     *  if ID = 0, get all countries‘ ids
     *  if isleaf = 0, you need call this interface again to get the list of regions.
     *  if isleaf = 1, don't need get again.
     *
     * @param locateid Region ID
     * @return the list of regions
     */
    public BLBaseBodyResult requestSubAreas(int locateid) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("locateid", locateid);
            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_SUBAREAGET(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * Get a list of set-top box providers
     * @param locateid Region ID
     * @return
     */
    public BLBaseBodyResult requestSTBProvider(int locateid) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("locateid", locateid);
            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_STBGet(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * Get the set-top box ircode download URL
     * @param locateid Region ID
     * @param providerid Provider ID
     * @return
     */
    public BLBaseBodyResult requestSTBSTBIRCodeScriptDownloadUrl(int locateid, int providerid) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {
            JSONObject jParam = new JSONObject();
            jParam.put("locateid", locateid);
            jParam.put("providerid", providerid);
            jParam.put("devtypeid", 2);
            String json = jParam.toString();

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_STB_SCRIPT_GET_URL(), head, json.getBytes(), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * 根据获取到的红外码，获取红外码脚本下载URL和randkey
     * @param hexString 红外码十六进制字符串
     * @return
     */
    public BLBaseBodyResult recognizeIRCode(String hexString) {
        BLBaseBodyResult result = new BLBaseBodyResult();

        try {

            Map<String, String > head = new HashMap<>(2);
            head.put("UserId", mUserid);
            head.put("LoginSession", mUserserssion);

            String httpResult = BLBaseHttpAccessor.post(BLApiUrls.KitResource.URL_IRCODE_RECOGNIZE(), head, BLCommonTools.parseStringToByte(hexString), mHttpTimeOut, new BLAccountTrustManager());
            if (httpResult != null) {

                JSONObject jResult = new JSONObject(httpResult);
                result.setStatus(jResult.optInt("error"));
                result.setMsg(jResult.optString("msg", null));
                if (result.succeed()) {
                    result.setResponseBody(jResult.optString("respbody", null));
                }
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
     * 下载红外码脚本
     * @param UrlString 获取到的下载临时URL
     * @param savePath 保存地址
     * @return
     */
    public BLDownloadScriptResult downloadIRCodeScript(String UrlString, String savePath, String randkey) {
        BLDownloadScriptResult result = new BLDownloadScriptResult();

        String url;
        if (UrlString.startsWith("http")) {
            url = UrlString;
        } else {
            url = BLApiUrls.KitResource.URL_IRCOCE_BASE_() + UrlString;
        }

        long nowtime = System.currentTimeMillis() / 1000;

        String baseUrl;
        if (url.indexOf("?") > 0) {
            baseUrl = url.substring(0, url.indexOf("?"));
        } else  {
            baseUrl = url;
        }
        String verfiedString = baseUrl + String.valueOf(nowtime) + "broadlinkappmanage@" + mLid;

        Map<String, String> mapSession = new HashMap<>(6);
        mapSession.put("language", BLCommonTools.getLanguage());
        mapSession.put("licenseid", mLid);
        mapSession.put("timestamp", String.valueOf(nowtime));
        mapSession.put("sign", BLCommonTools.SHA1(verfiedString));
        mapSession.put("userid", mUserid);
        mapSession.put("loginsession", mUserserssion);

        // 设置保存路径
        int status = BLBaseHttpAccessor.download(url, mapSession, null, savePath, null, mHttpTimeOut, new BLAccountTrustManager());

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

        try {
            result.setStatus(BLControllerErrCode.SUCCESS);
            result.setMsg("success");

            if (randkey != null) {   //若存在解密密钥，则解密下载文件
                savePath = decryptIRCodeScript(savePath, randkey);
            }

            result.setSavePath(savePath);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            result.setStatus(BLAppSdkErrCode.ERR_UNZIP);
            result.setMsg("unzip error");
        }
        return result;
    }

    /**
     * 获取红码脚本信息
     * @param scriptPath 脚本全路径名
     * @param deviceType 设备类型，本接口值为 1 / 2 / 3
     * @return  红码信息
     */
    public BLIRCodeInfoResult queryIRCodeInfomation(String scriptPath, int deviceType) {
        BLIRCodeInfoResult result = new BLIRCodeInfoResult();

        try {
            if (scriptPath.endsWith(".gz")) {   //如果是gz文件则，调用之前红码库SDK
                BLIrdaConProduct irdaConProduct = mBLIrdaConLibPareser.irda_con_get_info(scriptPath);

                if (irdaConProduct != null) {
                    result.setStatus(0);
                    result.setMsg("Success");
                    result.setIrdaInfo(irdaConProduct);
                } else {
                    result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
                    result.setMsg("parese irda failed");
                }

                return result;
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("luacode", scriptPath);
                jsonObject.put("devtypeid", deviceType);

                BLCommonTools.debug("queryRedcodeInfomation");
                String resultStr = mNetworkAPI.deviceRedCodeInfomation(jsonObject.toString());
                BLCommonTools.debug("Controller queryRedcodeInfomation result: " + resultStr);

                JSONObject jResult = new JSONObject(resultStr);
                result.setStatus(jResult.optInt("status"));
                result.setMsg(jResult.optString("msg", null));

                if(result.succeed()) {
                    String info = jResult.optString("information", null);
                    result.setInfomation(info);
                    result.setIrdaInfo(info);
                }
            }

        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg("Error Unknown");
        }

        return result;
    }

    /**
     * 获取红码发送命令
     * @param scriptPath 脚本
     * @param params 命令详情
     * @return  红码命令
     */
    public BLIRCodeDataResult queryACIRCodeData(String scriptPath, BLQueryIRCodeParams params) {
        BLIRCodeDataResult result = new BLIRCodeDataResult();

        try {
            if (scriptPath.endsWith(".gz")) {    //如果是gz文件则，调用之前红码库SDK
                BLIrdaConState blIrdaConState = new BLIrdaConState();
                blIrdaConState.status = params.getState();
                blIrdaConState.temperature = params.getTemperature();
                blIrdaConState.mode = params.getMode();
                blIrdaConState.wind_speed = params.getSpeed();
                blIrdaConState.wind_direct = params.getDirect();
                blIrdaConState.hour = BLDateUtils.getCurrrentHour();
                blIrdaConState.minute = BLDateUtils.getCurrrentMin();

                byte[] irData = mBLIrdaConLibPareser.irda_low_data_output(scriptPath, params.getKey(), params.getFreq(), blIrdaConState);
                if (irData != null) {
                    result.setStatus(0);
                    result.setMsg("Success");

                    result.setFreq(params.getFreq());
                    result.setIrcode(BLCommonTools.bytes2HexString(irData));
                } else {
                    result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
                    result.setMsg("parese irda failed");
                }
            } else {
                int deviceType = 3;
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("luacode", scriptPath);
                jsonObject.put("devtypeid", deviceType);
                jsonObject.put("state", params.getState());
                jsonObject.put("mode", params.getMode());
                jsonObject.put("speed", params.getSpeed());
                jsonObject.put("direct", params.getDirect());
                jsonObject.put("temperature", params.getTemperature());
                jsonObject.put("key", params.getKey());
                jsonObject.put("freq", params.getFreq());

                BLCommonTools.debug("queryRedcodeDataWithScript");
                String resultStr = mNetworkAPI.deviceRedCodeData(jsonObject.toString());
                BLCommonTools.debug("Controller queryRedcodeDataWithScript result: " + resultStr);

                JSONObject jResult = new JSONObject(resultStr);
                result.setStatus(jResult.optInt("status"));
                result.setMsg(jResult.optString("msg", null));

                if(result.succeed()) {
                    JSONArray redCodeArray = jResult.optJSONArray("red_code");

                    int freq = redCodeArray.getInt(0);
                    int dataLen = redCodeArray.getInt(1);

                    byte[] redData = new byte[2 * dataLen];

                    int i = 4;
                    for (int j = 2; j < redCodeArray.length(); j++) {
                        int redInt = (redCodeArray.getInt(j) * 4 + 61) / 122;
                        if (redInt > 255) {
                            redData[i++] = 0x00;
                            redData[i++] = (byte) ((redInt & 0xff00) >> 8);
                            redData[i++] = (byte) (redInt & 0xff);
                            dataLen += 4;
                        } else {
                            redData[i++] = (byte) (redInt & 0xff);
                        }
                    }

                    redData[0] = (byte) (freq & 0xff);
                    redData[1] = (byte) ((freq & 0xff00) >> 8);
                    redData[2] = (byte) (dataLen & 0xff);
                    redData[3] = (byte) ((dataLen & 0xff00) >> 8);

                    byte[] irData = new byte[i];
                    System.arraycopy(redData, 0, irData, 0, i);

                    result.setFreq(freq);
                    result.setIrcode(BLCommonTools.bytes2HexString(irData));
                }
            }
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 获取电视机/机顶盒发送命令
     * @param scriptPath 脚本
     * @param deviceType 设备类型，本接口值为 1 / 2
     * @param funcname 指定操作名称
     * @return
     */
    public BLIRCodeDataResult queryTVIRCodeData(String scriptPath, int deviceType, String funcname) {
        BLIRCodeDataResult result = new BLIRCodeDataResult();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("luacode", scriptPath);
            jsonObject.put("devtypeid", deviceType);
            jsonObject.put("funcName", funcname);

            BLCommonTools.debug("queryRedcodeDataWithScript");
            String resultStr = mNetworkAPI.deviceRedCodeData(jsonObject.toString());
            BLCommonTools.debug("Controller queryRedcodeDataWithScript result: " + resultStr);

            JSONObject jResult = new JSONObject(resultStr);
            result.setStatus(jResult.optInt("status"));
            result.setMsg(jResult.optString("msg", null));
            if (result.succeed()) {
                JSONArray redCodeArray = jResult.optJSONArray("red_code");
                if (redCodeArray != null) {
                    int size = redCodeArray.length();
                    byte[] irData = new byte[size];
                    for (int i = 0; i < size; i++) {
                        irData[i] = (byte)redCodeArray.getInt(i);
                    }
                    result.setIrcode(BLCommonTools.bytes2HexString(irData));
                }
            }

        } catch (Exception e) {
            BLCommonTools.handleError(e);

            result.setStatus(BLAppSdkErrCode.ERR_UNKNOWN);
            result.setMsg(e.toString());
        }

        return result;
    }

    /**
     * 解析脚本
     * @param filePath 脚本路径
     * @param randkey 密钥
     * @return
     */
    private String decryptIRCodeScript(String filePath, String randkey) {
        if (filePath == null) {
            return null;
        }

        try {
            File file = new File(filePath);
            if(!file.exists()){
                BLCommonTools.debug("ircode script not exit");
                return null;
            }

            String tmpFilePath = filePath + ".bak";
            File tmpFile = new File(tmpFilePath);
            if(tmpFile.exists()){
                tmpFile.delete();
            }
            file.renameTo(tmpFile);

            byte[] encryptData = BLFileUtils.readFileBytes(tmpFile);
            if (encryptData != null) {
                String aesKey = STR_RM_KEY_PF + randkey;

                byte[] IV = new byte[]{(byte)0xea, (byte)0xa4, 0x7a, 0x3a, (byte)0xeb, 0x08, 0x22,
                        (byte)0xa2, 0x19, 0x18,(byte) 0xc5, (byte)0xd7, 0x1d, 0x36, 0x15,(byte) 0xaa};

                byte[] decryptData = BLCommonTools.aesPKCS7PaddingDecryptToByte(BLCommonTools.aeskeyDecrypt(aesKey), IV, encryptData);
                if (decryptData != null) {
                    File destFile = new File(filePath);
                    if(destFile.exists()){
                        destFile.delete();
                    }
                    destFile.createNewFile();
                    boolean isWrite = BLFileUtils.saveBytesToFile(decryptData, destFile);
                    if (isWrite) {
                        return filePath;
                    }
                }
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return null;
    }
}
