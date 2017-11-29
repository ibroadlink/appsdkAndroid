package cn.com.broadlink.sdk.data.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLPeriodInfo {
    private int index;
    private boolean enable;
    private List<Integer> repeat = new ArrayList<>();
    private int hour, min, sec;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public List<Integer> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<Integer> repeat) {
        this.repeat = repeat;
    }
}
