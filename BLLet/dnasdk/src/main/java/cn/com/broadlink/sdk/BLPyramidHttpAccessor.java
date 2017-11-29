package cn.com.broadlink.sdk;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * Created by zhuxuyang on 15/11/10.
 */
final class BLPyramidHttpAccessor {

    protected String post(String addr, String param, int httpTimeout) {
        URL url = null;
        try {
            BLCommonTools.debug("Http Url: " + addr);
            BLCommonTools.debug("Http Method: POST");

            url = new URL(addr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 请求类型
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            // post请求不使用缓存
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Content-type", "application/x-java-serialized-object");
            urlConnection.setConnectTimeout(httpTimeout);
            urlConnection.setReadTimeout(httpTimeout);

            BLCommonTools.debug("Send Param: " + param);

            /** 安全认证 **/
            if ("https".equals(url.getProtocol().toLowerCase())) {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, new TrustManager[]{new BLTrustManager()}, new java.security.SecureRandom());
                ((HttpsURLConnection)urlConnection).setSSLSocketFactory(sc.getSocketFactory());
                ((HttpsURLConnection)urlConnection).setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            }

            /** http头 **/
            long time = System.currentTimeMillis();
            String signatrue = calSignature(param, time);
            urlConnection.setRequestProperty("Signature", signatrue);
            urlConnection.setRequestProperty("Timestamp", String.valueOf(time));

            /** 写入数据 **/
            // getOutputStream()会自动调用connect()方法
            OutputStream os = urlConnection.getOutputStream();
            // 使用gzip进行压缩
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(param.getBytes());

            gos.flush();
            gos.close();

            /** 请求服务器并返回结果 **/
            InputStream is = urlConnection.getInputStream();   //获取输入流，此时才真正建立链接
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine = "";
            String resultData = "";
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
            is.close();

            BLCommonTools.debug("Server Return: " + resultData);

            return resultData;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
        }

        return null;
    }

    /**
     * 通过传入参数计算signatrue
     * @param data 数据内容
     * @param time 时间
     * @return
     */
    private static String calSignature(String data, long time){
        return BLCommonTools.md5(data + "^&*%Y$#" + time);
    }
}
