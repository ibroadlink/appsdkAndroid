package cn.com.broadlink.dnasdkdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.broadlink.dnasdkdemo.R;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class DeviceActivity extends BaseActivity {
    private DeviceAdapter mAdapter;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        ListView lv = (ListView) findViewById(R.id.lv_device);

        mAdapter = new DeviceAdapter();
        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeviceActivity.this, ControlActivity.class);
                intent.putExtra("DEVICE_ID", ((BLDNADevice)mAdapter.getItem(position)).getDid());

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mTimer == null){
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final ArrayList<BLDNADevice> listDevice = new ArrayList<BLDNADevice>();
                    for(String key : mApplication.mMapDevice.keySet()){
                        listDevice.add(mApplication.mMapDevice.get(key));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged(listDevice);
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    public class DeviceAdapter extends BaseAdapter{
        private ArrayList<BLDNADevice> mListDevice = new ArrayList<BLDNADevice>();

        @Override
        public int getCount() {
            return mListDevice.size();
        }

        @Override
        public Object getItem(int position) {
            return mListDevice.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = DeviceActivity.this.getLayoutInflater().inflate(R.layout.adapter_device, null);
            }

            BLDNADevice device = mListDevice.get(position);

            TextView name = (TextView) convertView.findViewById(R.id.tv_name);
            TextView mac = (TextView) convertView.findViewById(R.id.tv_mac);
            TextView state = (TextView) convertView.findViewById(R.id.tv_state);
            TextView ip = (TextView) convertView.findViewById(R.id.tv_ip);

            name.setText(device.getName());
            mac.setText(device.getMac());
            state.setText("" + device.getState());
            ip.setText("" + BLLet.Controller.queryDeviceIp(device.getDid()));

            return convertView;
        }

        public void notifyDataSetChanged(ArrayList<BLDNADevice> listDevice) {
            mListDevice.clear();
            mListDevice.addAll(listDevice);

            super.notifyDataSetChanged();
        }
    }
}
