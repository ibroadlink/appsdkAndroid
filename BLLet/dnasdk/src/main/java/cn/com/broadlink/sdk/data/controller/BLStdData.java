package cn.com.broadlink.sdk.data.controller;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhuxuyang on 16/4/19.
 */
public class BLStdData implements Parcelable {
    private String act = "set";

    private ArrayList<String> params = new ArrayList<>();

    private ArrayList<ArrayList<Value>> vals = new ArrayList<>();

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public ArrayList<ArrayList<Value>> getVals() {
        return vals;
    }

    public void setVals(ArrayList<ArrayList<Value>> vals) {
        this.vals = vals;
    }


    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    /**
     * Created by YeJin on 2016/5/5.
     */
    public static class Value<T> implements Parcelable{

        private T val;
        private int idx;

        public T getVal() {
            return val;
        }

        public void setVal(T val) {
            this.val = val;
        }

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("key_val", (Serializable) val);
            dest.writeBundle(bundle);
            dest.writeInt(this.idx);
        }

        public Value() {
        }

        protected Value(Parcel in) {
            Bundle bundle = in.readBundle();
            Serializable value = bundle.getSerializable("key_val");
            this.val = (T) value;
            this.idx = in.readInt();
        }

        public static final Creator<Value> CREATOR = new Creator<Value>() {
            @Override
            public Value createFromParcel(Parcel source) {
                return new Value(source);
            }

            @Override
            public Value[] newArray(int size) {
                return new Value[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.act);
        dest.writeStringList(this.params);
        if(vals != null){
            dest.writeInt(vals.size()); // write list size
            for(ArrayList<Value> arrayList: vals){
                dest.writeList(arrayList);
            }
        }
    }

    public BLStdData() {
    }

    protected BLStdData(Parcel in) {
        this.act = in.readString();
        this.params = in.createStringArrayList();
        this.vals = new ArrayList<>();
        int size = in.readInt();
        for(int i = 0; i < size; i ++){
            vals.add(in.readArrayList(Value.class.getClassLoader()));
        }
    }

    public static final Creator<BLStdData> CREATOR = new Creator<BLStdData>() {
        @Override
        public BLStdData createFromParcel(Parcel source) {
            return new BLStdData(source);
        }

        @Override
        public BLStdData[] newArray(int size) {
            return new BLStdData[size];
        }
    };
}