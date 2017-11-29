package cn.com.broadlink.sdk.result.controller;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zhujunjie on 2017/8/7.
 */
public class BLQueryDeviceStatusResult extends BLBaseResult {
    private List<BLQueryDeviceStatus> queryDeviceMap = new ArrayList<>();

    public List<BLQueryDeviceStatus> getQueryDeviceMap() {
        return queryDeviceMap;
    }

    public void setQueryDeviceMap(List<BLQueryDeviceStatus> queryDeviceMap) {
        this.queryDeviceMap = queryDeviceMap;
    }

}
