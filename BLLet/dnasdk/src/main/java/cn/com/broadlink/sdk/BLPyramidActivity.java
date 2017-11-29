package cn.com.broadlink.sdk;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by zhuxuyang on 15/11/3.
 */
public class BLPyramidActivity extends Activity {
    private boolean mEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mEnable){
            BLLet.Picker.onCreate(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEnable) {
            BLLet.Picker.onDestroy(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mEnable) {
            BLLet.Picker.onResume(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mEnable) {
            BLLet.Picker.onPause(this);
        }
    }

    /**
     * 设置是否跟踪页面状态，默认不跟踪
     */
    public void trace(){
        this.mEnable = true;
    }
}
