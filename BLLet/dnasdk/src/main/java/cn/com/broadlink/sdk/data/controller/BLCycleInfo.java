package cn.com.broadlink.sdk.data.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujunjie on 2017/11/22.
 */

public class BLCycleInfo {
    private int index;
    private boolean enable;
    private List<Integer> repeat = new ArrayList<>();
    private int start_hour, start_min, start_sec;
    private int end_hour, end_min, end_sec;
    private int cmd1duration, cmd2duration;

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

    public List<Integer> getRepeat() {
        return repeat;
    }

    public void setRepeat(List<Integer> repeat) {
        this.repeat = repeat;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public void setStart_hour(int start_hour) {
        this.start_hour = start_hour;
    }

    public int getStart_min() {
        return start_min;
    }

    public void setStart_min(int start_min) {
        this.start_min = start_min;
    }

    public int getStart_sec() {
        return start_sec;
    }

    public void setStart_sec(int start_sec) {
        this.start_sec = start_sec;
    }

    public int getEnd_sec() {
        return end_sec;
    }

    public void setEnd_sec(int end_sec) {
        this.end_sec = end_sec;
    }

    public int getEnd_min() {
        return end_min;
    }

    public void setEnd_min(int end_min) {
        this.end_min = end_min;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public void setEnd_hour(int end_hour) {
        this.end_hour = end_hour;
    }

    public int getCmd1duration() {
        return cmd1duration;
    }

    public void setCmd1duration(int cmd1duration) {
        this.cmd1duration = cmd1duration;
    }

    public int getCmd2duration() {
        return cmd2duration;
    }

    public void setCmd2duration(int cmd2duration) {
        this.cmd2duration = cmd2duration;
    }
}
