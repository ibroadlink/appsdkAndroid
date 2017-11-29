package cn.com.broadlink.dnasdkdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.TrustManager;

import cn.com.broadlink.dnasdkdemo.R;
import cn.com.broadlink.sdk.BLFileStorageUtils;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.constants.controller.BLControllerErrCode;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;
import cn.com.broadlink.sdk.result.controller.BLQueryResoureVersionResult;

public class ControlActivity extends BaseActivity {
    private ProgressDialog mDialog;
    private BLDNADevice mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        TextView update = (TextView) findViewById(R.id.tv_update);
        TextView firmware = (TextView) findViewById(R.id.tv_fw);
        TextView time = (TextView) findViewById(R.id.tv_time);
        TextView profile = (TextView) findViewById(R.id.tv_profile);
        TextView ui = (TextView) findViewById(R.id.tv_ui);
        TextView script = (TextView) findViewById(R.id.tv_script);

        mDialog = new ProgressDialog(this);
        String did = getIntent().getStringExtra("DEVICE_ID");
        mDevice = mApplication.mMapDevice.get(did);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

                        BLLet.Controller.updateDeviceInfo(mDevice.getDid(), "S1", false);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        firmware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

                        BLLet.Controller.queryFirmwareVersion(mDevice.getDid());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

                        BLLet.Controller.queryDeviceTime(mDevice.getDid());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

                        BLLet.Controller.queryProfile(mDevice.getDid());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

                        BLQueryResoureVersionResult result = BLLet.Controller.queryUIVersion(mDevice.getPid());

                        if(result.succeed()){
                            BLLet.Controller.downloadUI(mDevice.getPid());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });

        script.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.show();
                            }
                        });

//                        Log.i("script path", BLLet.Controller.queryScriptPath(mDevice.getPid()));
//                        BLStdControlParam stdControlParam = new BLStdControlParam();
//                        stdControlParam.setAct("get");
//                        BLLet.Controller.dnaControl(mDevice.getDid(), null, stdControlParam, null);
                        downloadScript(mDevice.getPid(), 10 * 1000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    protected static BLDownloadScriptResult downloadScript(String pid, int httpTimeOut, TrustManager... trustManagers) {
        int status = -1;

        String tempPath;
        String savePath = null;

        try {
            URL url = new URL("http://10.10.2.219:18881/ec4/v1/system/downloadproductresource");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 请求类型
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            // 不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlConnection.setConnectTimeout(httpTimeOut);
            urlConnection.setReadTimeout(httpTimeOut);

            /** 安全认证 **/
//            if ("https".equals(url.getProtocol().toLowerCase())) {
//                SSLContext sc = SSLContext.getInstance("TLS");
//                if(trustManagers == null){
//                    trustManagers = new TrustManager[]{new BLTrustManager()};
//                }
//                sc.init(null, trustManagers, new java.security.SecureRandom());
//                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
//                ((HttpsURLConnection)urlConnection).setHostnameVerifier(new BLFreeHostnameVerifier());
//            }

//            /** http头 **/
//            if(mapHead != null){
//                for(String key : mapHead.keySet()){
//                    urlConnection.setRequestProperty(key, mapHead.get(key));
//                }
//            }

            // 组织参数
            JSONObject jParam = new JSONObject();
            try {
                jParam.put("resourcetype", "script");
                jParam.put("pid", pid);
            } catch (JSONException e) {
//                BLCommonTools.handleError(e);
            }

            /** 写入数据 **/
            // getOutputStream()会自动调用connect()方法
            OutputStream os = urlConnection.getOutputStream();
            os.write(jParam.toString().getBytes());

            os.flush();
            os.close();

            // 获取状态码
            status = urlConnection.getResponseCode();
//            BLCommonTools.debug("Http Status: " + status);

            if(status == 200){
                /** 请求服务器并返回结果 **/
                InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
                if(urlConnection.getHeaderField("resourcetype").equals("script")){
                    // 设置保存路径
                    savePath = BLFileStorageUtils.getDefaultLuaScriptPath(pid);
                    tempPath = BLFileStorageUtils.mTempPath + File.separator + pid + ".script";
                }else{
                    savePath = BLFileStorageUtils.getDefaultJSScriptPath(pid);
                    tempPath = BLFileStorageUtils.mTempPath + File.separator + pid + ".js";
                }

                File tempFile = new File(tempPath);
                if(tempFile.exists()){
                    tempFile.delete();
                }

                if(!tempFile.getParentFile().exists()){
                    tempFile.getParentFile().mkdirs();
                }
                tempFile.createNewFile();

                OutputStream outputStream = new FileOutputStream(tempFile);

                byte buffer[] = new byte[4*1024];
                //循环读取下载的文件到buffer对象数组中

                int size = 0;
                while((size = is.read(buffer)) != -1) {
                    //把文件写入到文件
                    outputStream.write(buffer, 0, size);
                }

                is.close();
                outputStream.close();

                if(savePath != null){
                    File saveFile = new File(savePath);

                    if(!saveFile.getParentFile().exists()){
                        saveFile.getParentFile().mkdirs();
                    }

                    tempFile.renameTo(saveFile);
                }

//                BLCommonTools.debug("Http Download complete");

            }
        } catch (Exception e) {
//            BLCommonTools.handleError(e);
        }

        BLDownloadScriptResult result = new BLDownloadScriptResult();
        switch (status) {
            case 200:
                result.setStatus(BLControllerErrCode.SUCCESS);
                result.setMsg("success");
                result.setSavePath(savePath);
                break;
            case 414:
                result.setStatus(BLControllerErrCode.ERR_NO_RESOURCE);
                result.setMsg("found resource error");
                break;
            case 415:
                result.setStatus(BLControllerErrCode.ERR_PARAM);
                result.setMsg("param fomat error");
                break;
            case 416:
                result.setStatus(BLControllerErrCode.ERR_LEAK_PARAM);
                result.setMsg("leak necessary param");
                break;
            case 417:
                result.setStatus(BLControllerErrCode.ERR_TOKEN_OUT_OF_DATE);
                result.setMsg("token out of date");
                break;
            case 418:
                result.setStatus(BLControllerErrCode.ERR_WRONG_METHOD);
                result.setMsg("wrong method");
                break;
            default:
                result.setStatus(BLControllerErrCode.ERR_UNKNOWN);
                result.setMsg("unknown error");
        }

        return result;
    }
}
