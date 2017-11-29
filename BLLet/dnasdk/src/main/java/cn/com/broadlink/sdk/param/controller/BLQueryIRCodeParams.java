package cn.com.broadlink.sdk.param.controller;

import cn.com.broadlink.sdk.data.controller.BLRMCloudAcConstants;

/**
 * Created by zjjllj on 2017/2/4.
 */

public class BLQueryIRCodeParams {
    private int state;
    private int mode;
    private int speed;
    private int direct;
    private int temperature;
    private int key;
    private int freq = BLRMCloudAcConstants.IR_HZ;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDirect() {
        return direct;
    }

    public void setDirect(int direct) {
        this.direct = direct;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }
}
