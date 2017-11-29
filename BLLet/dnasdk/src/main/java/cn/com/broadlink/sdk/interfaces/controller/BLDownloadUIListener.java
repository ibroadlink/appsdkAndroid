package cn.com.broadlink.sdk.interfaces.controller;

/**
 * Created by zhuxuyang on 16/4/21.
 */
public abstract class BLDownloadUIListener {
    /**
     * 用于控制是否下载该脚本
     * @param version
     * @param uiid
     * @return
     */
    public abstract boolean shouldDownload(String version, String uiid);

    /**
     * 用于更改保存路径
     * 非必须实现
     * @param path
     * @return
     */
    public String assignPath(String path){
        return path;
    }
}
