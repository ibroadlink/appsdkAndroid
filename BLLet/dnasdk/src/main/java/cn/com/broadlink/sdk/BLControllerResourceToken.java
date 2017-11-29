package cn.com.broadlink.sdk;

/**
 * Created by zhuxuyang on 16/4/20.
 */
public class BLControllerResourceToken {
    /** 请求到的url **/
    private String url;
    /** url的使用期限时间戳 **/
    private long deadLine;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(long deadLine) {
        this.deadLine = deadLine;
    }
}
