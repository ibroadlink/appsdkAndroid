package cn.com.broadlink.sdk.result.controller;

import android.os.Parcel;

import java.util.ArrayList;

import cn.com.broadlink.sdk.data.controller.BLCycleInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.data.controller.BLPeriodInfo;
import cn.com.broadlink.sdk.data.controller.BLTimerInfo;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLQueryTaskResult extends BLBaseResult {
    private ArrayList<BLTimerInfo> timer = new ArrayList<>();
    private ArrayList<BLTimerInfo> delay = new ArrayList<>();
    private ArrayList<BLPeriodInfo> period = new ArrayList<>();
    private ArrayList<BLCycleInfo> cycle = new ArrayList<>();
    private ArrayList<BLCycleInfo> random = new ArrayList<>();

    public ArrayList<BLTimerInfo> getTimer() {
        return timer;
    }

    public void setTimer(ArrayList<BLTimerInfo> timer) {
        this.timer = timer;
    }

    public ArrayList<BLPeriodInfo> getPeriod() {
        return period;
    }

    public void setPeriod(ArrayList<BLPeriodInfo> period) {
        this.period = period;
    }

    public ArrayList<BLTimerInfo> getDelay() {
        return delay;
    }

    public void setDelay(ArrayList<BLTimerInfo> delay) {
        this.delay = delay;
    }

    public ArrayList<BLCycleInfo> getCycle() {
        return cycle;
    }

    public void setCycle(ArrayList<BLCycleInfo> cycle) {
        this.cycle = cycle;
    }

    public ArrayList<BLCycleInfo> getRandom() {
        return random;
    }

    public void setRandom(ArrayList<BLCycleInfo> random) {
        this.random = random;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.timer);
        dest.writeList(this.delay);
        dest.writeList(this.period);
        dest.writeList(this.cycle);
        dest.writeList(this.random);
    }

    public BLQueryTaskResult() {
    }

    protected BLQueryTaskResult(Parcel in) {
        super(in);
        this.timer = new ArrayList<BLTimerInfo>();
        in.readList(this.timer, BLTimerInfo.class.getClassLoader());
        this.delay = new ArrayList<BLTimerInfo>();
        in.readList(this.delay, BLTimerInfo.class.getClassLoader());
        this.period = new ArrayList<BLPeriodInfo>();
        in.readList(this.period, BLPeriodInfo.class.getClassLoader());
        this.cycle = new ArrayList<BLCycleInfo>();
        in.readList(this.cycle, BLCycleInfo.class.getClassLoader());
        this.random = new ArrayList<BLCycleInfo>();
        in.readList(this.random, BLCycleInfo.class.getClassLoader());
    }

    public static final Creator<BLQueryTaskResult> CREATOR = new Creator<BLQueryTaskResult>() {
        @Override
        public BLQueryTaskResult createFromParcel(Parcel source) {
            return new BLQueryTaskResult(source);
        }

        @Override
        public BLQueryTaskResult[] newArray(int size) {
            return new BLQueryTaskResult[size];
        }
    };


}
