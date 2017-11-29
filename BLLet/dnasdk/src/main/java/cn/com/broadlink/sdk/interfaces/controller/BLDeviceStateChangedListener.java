package cn.com.broadlink.sdk.interfaces.controller;

/**
 * Device State changed Listener
 */
public interface BLDeviceStateChangedListener {
    /**
     * Device status changed callback
     * @param did Device did
     * @param state Device status
     */
    public void onChanged(String did, int state);
}
