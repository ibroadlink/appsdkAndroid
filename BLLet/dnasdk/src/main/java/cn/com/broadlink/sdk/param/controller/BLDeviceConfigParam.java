package cn.com.broadlink.sdk.param.controller;

/**
 * Created by zhuxuyang on 16/4/18.
 */
public class BLDeviceConfigParam {
    private String ssid;
    private String password;
    private String gatewayaddr;
    private int version = 2;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGatewayaddr() {
        return gatewayaddr;
    }

    public void setGatewayaddr(String gatewayaddr) {
        this.gatewayaddr = gatewayaddr;
    }

    /**
     * Config Version. 1 / 2 / 3, rely to device profile. Default is 2.
     * @return config version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set config version. 1 / 2 / 3, rely to device profile. Default is 2.
     * @param version config version
     */
    public void setVersion(int version) {
        this.version = version;
    }
}
