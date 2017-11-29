package cn.com.broadlink.sdk.result.controller;


import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/4.
 */

public class BLIRCodeDataResult extends BLBaseResult {
    private int freq;
    private String ircode;

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getIrcode() {
        return ircode;
    }

    public void setIrcode(String ircode) {
        this.ircode = ircode;
    }
}
