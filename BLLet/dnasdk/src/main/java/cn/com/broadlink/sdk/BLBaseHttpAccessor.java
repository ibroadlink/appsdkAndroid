package cn.com.broadlink.sdk;

import android.text.TextUtils;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import cn.com.broadlink.sdk.constants.BLAppSdkErrCode;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;

public class BLBaseHttpAccessor {
    public static int HTTP_TIMEOUT = 15000;

    public static String HTTP_GET = "get";

    public static String HTTP_POST = "post";

    private static String HTTP_ERROR_MSG = "Http error: ";

    private static String HTTP_ERROR_TOO_FAST = "HTTP request too fast!";

    private static void addCommondToRequest(HttpURLConnection urlConnection) {
        long nowTime = System.currentTimeMillis() / 1000;
        String language = BLCommonTools.getLanguage();
        String lid = BLLet.getLicenseId();

        urlConnection.setRequestProperty("system", "android");
        urlConnection.setRequestProperty("appPlatform", "android");
        urlConnection.setRequestProperty("language", language);
        urlConnection.setRequestProperty("licenseid", lid);
        urlConnection.setRequestProperty("timestamp", String.valueOf(nowTime));
    }

    public static String get(String addr, String param,  Map<String, String> mapHead, int httpTimeOut, TrustManager... trustManagers){
        BLCommonTools.debug("Http Url: " + addr);
        BLCommonTools.debug("Http Method: GET");

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(addr)) {
            BLCommonTools.debug("Request too fast");
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP_TOO_FAST);
                jobect.put("msg", HTTP_ERROR_TOO_FAST);

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
        }

        URL url = null;
        try {
            /** 如果有参数，添加到url后 **/
            BLCommonTools.debug("Http Param: " + param);
            if(!TextUtils.isEmpty(param)){
                addr += param;
            }
            url = new URL(addr);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 请求类型
            urlConnection.setRequestMethod("GET");
            // 不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(httpTimeOut);
            urlConnection.setReadTimeout(httpTimeOut);

            /** 安全认证 **/
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");

                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());

                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            addCommondToRequest(urlConnection);
            if(mapHead != null){
                for(String key : mapHead.keySet()){
                    urlConnection.setRequestProperty(key, mapHead.get(key));
                }
            }

            BLCommonTools.debug("Http connect start");
            /** 连接 **/
            urlConnection.connect();

            BLCommonTools.debug("Http connect end");

            int respCode = urlConnection.getResponseCode();

            BLCommonTools.debug("Http ResponseCode: " + respCode);

            /** 请求服务器并返回结果 **/
            InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine;
            String resultData = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
            is.close();

            BLCommonTools.debug("Http Return: " + resultData);

            return resultData;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP);
                jobect.put("msg", HTTP_ERROR_MSG + e.toString());

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
            return null;
        }

    }


    public static byte[] getResultBytes(String addr, String param,  Map<String, String> mapHead, int httpTimeOut, TrustManager... trustManagers){
        BLCommonTools.debug("Http Url: " + addr);
        BLCommonTools.debug("Http Method: GET");

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(addr)) {
            BLCommonTools.debug("Request too fast");
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP_TOO_FAST);
                jobect.put("msg", HTTP_ERROR_TOO_FAST);

                return jobect.toString().getBytes();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
        }

        URL url = null;
        try {
            /** 如果有参数，添加到url后 **/
            BLCommonTools.debug("Http Param: " + param);
            if(!TextUtils.isEmpty(param)){
                addr += param;
            }
            url = new URL(addr);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 请求类型
            urlConnection.setRequestMethod("GET");
            // 不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(httpTimeOut);
            urlConnection.setReadTimeout(httpTimeOut);

            /** 安全认证 **/
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");

                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());

                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            addCommondToRequest(urlConnection);
            if(mapHead != null){
                for(String key : mapHead.keySet()){
                    urlConnection.setRequestProperty(key, mapHead.get(key));
                }
            }

            BLCommonTools.debug("Http connect start");
            /** 连接 **/
            urlConnection.connect();

            BLCommonTools.debug("Http connect end");

            int respCode = urlConnection.getResponseCode();

            BLCommonTools.debug("Http ResponseCode: " + respCode);

            /** 请求服务器并返回结果 **/
            InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
            BufferedInputStream isr = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int size;
            byte[] temp = new byte[8192];
            while ((size = isr.read(temp, 0, temp.length)) != -1) {
                baos.write(temp, 0, size);
            }

            is.close();
            baos.close();

            byte[] result = baos.toByteArray();
            BLCommonTools.debug("Http Return: " + new String(result));

            return result;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP);
                jobect.put("msg", HTTP_ERROR_MSG + e.toString());

                return jobect.toString().getBytes();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
            return null;
        }

    }
    /**
     * 普通post
     * @param addr
     * @param mapHead
     * @param dataBytes
     * @return
     */
    public static String post(String addr, Map<String, String> mapHead, byte[] dataBytes, int httpTimeOut, TrustManager... trustManagers) {
        BLCommonTools.debug("Http Url: " + addr);
        BLCommonTools.debug("Http Method: POST");

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(addr)) {
            BLCommonTools.debug("Request too fast");
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP_TOO_FAST);
                jobect.put("msg", HTTP_ERROR_TOO_FAST);

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
        }

        URL url = null;
        try {
            url = new URL(addr);
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
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");
                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            addCommondToRequest(urlConnection);
            if(mapHead != null){
                for(String key : mapHead.keySet()){
                    urlConnection.setRequestProperty(key, mapHead.get(key));
                }
            }

            /** 写入数据 **/
            // getOutputStream()会自动调用connect()方法
            OutputStream os = urlConnection.getOutputStream();
            os.write(dataBytes);

            os.flush();
            os.close();

            BLCommonTools.debug("post: " + urlConnection.getResponseCode());

            /** 请求服务器并返回结果 **/
            InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine;
            String resultData = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
            is.close();

            BLCommonTools.debug("Http Return: " + resultData);

            return resultData;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP);
                jobect.put("msg", HTTP_ERROR_MSG + e.toString());

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
            return null;
        }
    }

    /**
     * 文件上传post
     * @param addr
     * @param mapHead
     * @param data
     * @return
     */
    protected static String multipartPost(String addr, Map<String, String> mapHead, Map<String, Object> data, int httpTimeOut, TrustManager... trustManagers) {
        BLCommonTools.debug("Http Url: " + addr);
        BLCommonTools.debug("Http Method: POST");

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(addr)) {
            BLCommonTools.debug("Request too fast");
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP_TOO_FAST);
                jobect.put("msg", HTTP_ERROR_TOO_FAST);

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
        }

        String boundary = UUID.randomUUID().toString();

        URL url = null;
        try {
            url = new URL(addr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 请求类型
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            // 不使用缓存
            urlConnection.setUseCaches(false);
            // 设置维持长连接
            urlConnection.setRequestProperty("connection", "Keep-Alive");
            // 设置文件字符集
            urlConnection.setRequestProperty("Charset", "utf-8");
            // 设置文件类型
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            urlConnection.setConnectTimeout(httpTimeOut);
            urlConnection.setReadTimeout(httpTimeOut);

            /** 安全认证 **/
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");
                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            addCommondToRequest(urlConnection);
            if(mapHead != null){
                for(String key : mapHead.keySet()){
                    urlConnection.setRequestProperty(key, mapHead.get(key));
                }
            }

            /** 写入数据 **/
            // getOutputStream()会自动调用connect()方法
            DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

            for(String key : data.keySet()){
                Object value = data.get(key);

                dos.writeBytes("--" + boundary);
                dos.writeBytes("\r\n");

                if(value instanceof String){
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Content-Type: text/plain");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("\r\n");
                    dos.writeBytes(URLEncoder.encode((String) value, "UTF-8"));
                    dos.writeBytes("\r\n");
                }else if(value instanceof byte[]){
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"UTF-8\"");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Content-Type: application/octet-stream");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Content-Transfer-Encoding: binary");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("\r\n");
                    dos.write((byte[])value);
                    dos.writeBytes("\r\n");
                }else if(value instanceof File){
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + URLEncoder.encode(((File) value).getName(), "UTF-8") + "\"");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Content-Type: application/octet-stream");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("Content-Transfer-Encoding: binary");
                    dos.writeBytes("\r\n");
                    dos.writeBytes("\r\n");

                    // 文件转为byte[]
                    FileInputStream in = new FileInputStream((File) value);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = in.read(b)) != -1) {
                        out.write(b, 0, n);
                    }
                    in.close();
                    dos.write(out.toByteArray());
                    out.close();

                    dos.writeBytes("\r\n");
                }
            }

            dos.writeBytes("--" + boundary + "--" + "\r\n");
            dos.writeBytes("\r\n");

            dos.flush();
            dos.close();

            /** 请求服务器并返回结果 **/
            InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine;
            String resultData = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
            is.close();

            BLCommonTools.debug("Http Return: " + resultData);

            return resultData;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            try {
                JSONObject jobect = new JSONObject();
                jobect.put("error", BLAppSdkErrCode.ERR_HTTP);
                jobect.put("msg", HTTP_ERROR_MSG + e.toString());

                return jobect.toString();
            } catch (Exception err) {
                BLCommonTools.handleError(err);
            }
            return null;
        }

    }

    /**
     * 文件下载post
     * @param addr
     * @param mapHead
     * @param dataBytes
     * @return
     */
    protected static int download(String addr, Map<String, String> mapHead, byte[] dataBytes,String tempPath, String savePath, int httpTimeOut, TrustManager... trustManagers) {
        BLCommonTools.debug("Http Url: " + addr);
        BLCommonTools.debug("Http Method: Download");

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(addr)) {
            BLCommonTools.debug("Request too fast");
            return -1;
        }

        try {
            URL url =new URL(addr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            // 不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlConnection.setConnectTimeout(httpTimeOut);
            urlConnection.setReadTimeout(httpTimeOut);

            /** 安全认证 **/
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");
                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            addCommondToRequest(urlConnection);
            if(mapHead != null){
                for(String key : mapHead.keySet()){
                    urlConnection.setRequestProperty(key, mapHead.get(key));
                }
            }

            /** 写入数据 **/
            if(dataBytes != null){
                OutputStream os = urlConnection.getOutputStream();
                os.write(dataBytes);
                os.flush();
                os.close();
            }

            // 获取状态码
            int status = urlConnection.getResponseCode();
            BLCommonTools.debug("Http Status: " + status);

            if(status == HttpURLConnection.HTTP_OK){
                /** 请求服务器并返回结果 **/
                InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接

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

                BLCommonTools.debug("Http Download complete " + savePath);

                return HttpURLConnection.HTTP_OK;
            } else {
                return status;
            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return -1;
    }

    protected static BLDownloadScriptResult downloadScript(String pid, int httpTimeOut, TrustManager... trustManagers) {
        String urlAddress = BLApiUrls.APPManager.URL_DOWNLOAD() + "?resourcetype=enscript&pid=" + pid;
        BLCommonTools.debug("Http Url: " + urlAddress);
        BLCommonTools.debug("Http Method: POST");
        int status = -1;

        BLTokenBurst blTokenBurst = BLTokenBurst.getInstance();
        if (!blTokenBurst.queryTokenBurst(urlAddress)) {
            BLCommonTools.debug("Request too fast");
            return null;
        }

        String tempPath;
        String savePath = null;

        try {
            URL url = new URL(urlAddress);
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
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");
                if(trustManagers == null){
                    trustManagers = new TrustManager[]{new BLTrustManager()};
                }
                sc.init(null, trustManagers, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }
            
            // 获取状态码
            status = urlConnection.getResponseCode();
            BLCommonTools.debug("Http Status: " + status);
            String value = urlConnection.getHeaderField("Resourcetype");

            BLCommonTools.debug("http down script file type:" + value);

            if(status == 200 && value != null){
                /** 请求服务器并返回结果 **/
                InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
                if(value.equals("lua")){
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

                BLCommonTools.debug("Http Download complete");

            }
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        BLDownloadScriptResult result = new BLDownloadScriptResult();
        switch (status) {
            case 200:
                result.setStatus(BLAppSdkErrCode.SUCCESS);
                result.setMsg("success");
                result.setSavePath(savePath);
                break;
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
}
