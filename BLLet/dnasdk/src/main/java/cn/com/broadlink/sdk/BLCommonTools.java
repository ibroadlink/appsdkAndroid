package cn.com.broadlink.sdk;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SDK通用方法封装
 * Created by zhuxuyang on 15/11/13.
 */
final class BLCommonTools {
    /**
     * 数组转换为16进制字符串
     * @param bytes
     * @return
     */
    public static String bytes2HexString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 计算MD5
     * @param data
     * @return
     */
    protected static String md5(String data){
        return md5(data.getBytes());
    }

    /**
     * 计算MD5
     * @param data
     * @return
     */
    protected static String md5(byte[] data){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte[] m = md5.digest();//加密

            return bytes2HexString(m);
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            return "";
        }
    }

    /**
     * 计算MD5
     * @param data
     * @return
     */
    protected static byte[] md5Byte(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte[] m = md5.digest();//加密

            return m;
        } catch (Exception e) {
            BLCommonTools.handleError(e);
            return null;
        }
    }

    /**
     * SHA1
     * @param value
     * @return
     */
    public static String SHA1(String value) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];

            md.update(value.getBytes());
            sha1hash = md.digest();
            return bytes2HexString(sha1hash);
        } catch (Exception e) {
        }

        return "";
    }

    public static byte[] aeskeyDecrypt(String srcKey) {
        byte[] newKeys = new byte[16];
        int[] swap = new int[]{7, 12, 3, 0, 11, 15, 2, 4, 5, 9, 14, 1, 13,
                10, 8, 6,};
        byte[] md5key = md5Byte(srcKey.getBytes());
        for (int i = 0; i < 16; i++) {
            newKeys[i] = md5key[swap[i]];
        }

        return newKeys;
    }

    /***
     * AES/CBC/ZeroBytePadding加密
     * @param key
     * @param data 加密数据
     * @return 加密之后返回的数据
     */
    public static byte[] aesNoPadding(byte[] key, String data) {
        byte[] iv = new byte[] { (byte) 0xEA, (byte) 0xAA, (byte) 0xAA, 0x3A,
                (byte) 0xBB, 0x58, 0x62, (byte) 0Xa2, 0x19, 0x18, (byte) 0xb5,
                0x77, 0x1D, 0x16, 0x15, (byte) 0xaa };

        return aesNoPadding(iv, key, data);
    }

    /***
     * AES/CBC/ZeroBytePadding加密
     * @param iv 偏移量
     * @param key
     * @param data 加密数据
     * @return 加密之后返回的数据
     */
    public static byte[] aesNoPadding(byte[] iv, byte[] key, String data) {
        try {
            byte[] dataBytes = data.getBytes();
            Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            int blockSize = cipher.getBlockSize();

            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * AES/CBC/ZeroBytePadding加密
     * @param iv 偏移量
     * @param key
     * @param data 加密数据
     * @return 加密之后返回的数据
     */
    public static byte[] aesPKCS7PaddingDecryptToByte(byte[] key, byte[] iv, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(data);
            return original;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转为 btye 数组
     *
     * @param dataString 转化的16进制字符串
     * @return byte数组
     */
    public static byte[] parseStringToByte(String dataString) {
        int subPosition = 0;
        int byteLenght = dataString.length() / 2;

        byte[] result = new byte[byteLenght];

        for (int i = 0; i < byteLenght; i++) {
            String s = dataString.substring(subPosition, subPosition + 2);
            result[i] = (byte) Integer.parseInt(s, 16);
            subPosition = subPosition + 2;
        }

        return result;
    }

    /**
     * 统一处理异常
     * @param e
     */
    protected static void handleError(Exception e){
        if(BLLet.DebugLog.islog()){
            Log.e(BLConstants.LOG_TAG, "Exception: " + e.toString(), e);
        }
    }

    /**
     * 打印debug信息
     * @param msg
     */
    protected static void verbose(String msg){
        if(BLLet.DebugLog.islog()){
            Log.v(BLConstants.LOG_TAG, msg);
        }
    }

    /**
     * 打印debug信息
     * @param msg
     */
    protected static void debug(String msg){
        if(BLLet.DebugLog.islog()){
            Log.d(BLConstants.LOG_TAG, msg);
        }
    }

    /**
     * 验证邮箱格式
     * @param email
     * @return
     */
    protected static boolean isEmail(String email) {
        String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return match(regex, email);
    }


    /**
     * 验证手机格式
     * @param phone
     * @return
     */
    protected static boolean isPhone(String phone) {
        String regex = "[0-9]+";
        return match(regex, phone);
    }

    /**
     * @param regex
     *            正则表达式字符串
     * @param str
     *            要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 获取手机语言
     * @return
     * <br>zh_Hant 中文繁体
     * <br>zh_Hans 中文简体
     * <br>en 英文
     */
    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();

        StringBuffer language = new StringBuffer(locale.getLanguage());
        language.append("-");
        language.append(country);
        return language.toString().toLowerCase();
    }

    /**
     * 获取Url的域名
     *
     * @param urlString
     *            Url地址
     *
     * @return String host 域名
     *
     */
    public static String urlHost(String urlString) {
        String host = null;
        try {
            URL url = new URL(urlString);
            host = url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return host;
    }

    /**
     * 域名转Ip地址
     *
     * @param host
     *            域名
     *
     * @return ip地址
     *
     */
    public static String hostInetAddress(String host) {
        String IPAddress = null;
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = java.net.InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return IPAddress;
    }

    public static void upZipFile(File zipFile, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                }
                inZip.closeEntry();
                out.close();
            }
        }
        inZip.close();
    }

//    public static void upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
//        //public static void upZipFile() throws Exception{
//        ZipFile zfile=new ZipFile(zipFile);
//        Enumeration zList=zfile.entries();
//        ZipEntry ze=null;
//        byte[] buf=new byte[1024];
//        while(zList.hasMoreElements()){
//            ze=(ZipEntry)zList.nextElement();
//            if(ze.isDirectory()){
//                String dirstr = folderPath + ze.getName();
//                //dirstr.trim();
//                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
//                File f=new File(dirstr);
//                f.mkdir();
//                continue;
//            }
//            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
//            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
//            int readLen=0;
//            while ((readLen=is.read(buf, 0, 1024))!=-1) {
//                os.write(buf, 0, readLen);
//            }
//            is.close();
//            os.close();
//        }
//        zfile.close();
//    }

    public static File getRealFileName(String baseDir, String absFileName)
    {
        String[] dirs=absFileName.split("/");
        String lastDir=baseDir;
        if(dirs.length>1)
        {
            for (int i = 0; i < dirs.length-1;i++)
            {
                lastDir +=(dirs[i]+"/");
                File dir =new File(lastDir);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }
            }
            File ret = new File(lastDir,dirs[dirs.length-1]);
            return ret;
        }
        else
        {

            return new File(baseDir,absFileName);

        }

    }

    /**
     * aesCBCEncryptByte(AES/CBC/PKCS5Padding)解密
     */
    public static byte[] aesZeroBytePaddingDecode(byte[] iv, byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
