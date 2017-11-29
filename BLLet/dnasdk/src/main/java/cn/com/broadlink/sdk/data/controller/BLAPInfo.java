package cn.com.broadlink.sdk.data.controller;

/**
 * AP Info
 * @author YeJin
 *
 */
public class BLAPInfo {

	private String ssid;
	
	private int rssi;
	
	private int type;

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
