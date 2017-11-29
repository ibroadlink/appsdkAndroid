package cn.com.broadlink.sdk.interfaces.controller;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Device Scan Listener
 * You must call "startProbe" to get these infomations.
 */
public abstract class BLDeviceScanListener {
    /**
     * Add device to sdk when find new device.
     *
     * @param device    Add device info
     * @return true / false. Default is false.
     */
    public boolean shouldAdd(BLDNADevice device){
        return false;
    }

    /**
     * Update device info when find new device or device info change.
     *
     * @param device Update device info
     * @param isNewDevice Device is added to sdk or not
     */
    public abstract void onDeviceUpdate(BLDNADevice device, boolean isNewDevice);
}
