package cn.com.broadlink.sdk;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.broadlink.sdk.constants.controller.BLDeviceState;
import cn.com.broadlink.sdk.result.BLBaseResult;
import cn.com.broadlink.sdk.result.BLControllerDNAControlResult;
import cn.com.broadlink.sdk.result.controller.BLQueryDeviceStatus;
import cn.com.broadlink.sdk.result.controller.BLQueryDeviceStatusResult;

/**
 * 用于查询设备状态
 */
final class BLControllerDeviceQueryTask {
    private BLControllerImpl mControllerImpl;
    private QueryDeviceStateListener mQueryDeviceStateListener;
    private ExecutorService mFixedThreadPool;
    private HashSet<String> mSetInQuery;

    public BLControllerDeviceQueryTask(BLControllerImpl controllerImpl, QueryDeviceStateListener queryDeviceStateListener) {
        mControllerImpl = controllerImpl;
        mQueryDeviceStateListener = queryDeviceStateListener;

        // 限制线程池大小为10
        mFixedThreadPool = Executors.newFixedThreadPool(10);
        mSetInQuery = new HashSet<>();
    }

    /**
     * 查询设备状态
     * 判断设备是否在在查询中，是则不重复查询
     * 通过线程池查询
     * 查询完毕回调通知
     *
     * @param listDevice
     */
    public void query(ArrayList<BLProbeDevice> listDevice) {
        for (BLProbeDevice device : listDevice) {
            if (!mSetInQuery.contains(device.getDid())) {
                mSetInQuery.add(device.getDid());

                mFixedThreadPool.execute(new QueryStateRunnable(device));
            }
        }
    }

    /**
     * 批量查询设备状态
     * @param listDevice
     */
    public void batchQuery(ArrayList<BLProbeDevice> listDevice) {
        mFixedThreadPool.execute(new BatchQueryStateRunnable(listDevice));
    }

    /**
     * 查询状态Runnable
     */
    private class QueryStateRunnable implements Runnable {
        private BLProbeDevice mDevice;

        public QueryStateRunnable(BLProbeDevice device) {
            mDevice = device;
        }

        @Override
        public void run() {
            BLControllerDNAControlResult result = mControllerImpl.autoDNAControl(mDevice.getDid(), null, null, "dev_online", null);

            if (result.getStatus() == BLBaseResult.SUCCESS) {
                JSONObject jData = result.getData();
                if (jData != null) {
                    int state = jData.optBoolean("online") ? BLDeviceState.REMOTE : BLDeviceState.OFFLINE;
                    mQueryDeviceStateListener.onQuery(mDevice.getDid(), state);
                }
            }

            // 在查队列中取出
            mSetInQuery.remove(mDevice.getDid());
        }
    }

    private class BatchQueryStateRunnable implements Runnable {

        private ArrayList<BLProbeDevice> mDevices = new ArrayList<>();
        private HashMap<String, BLProbeDevice> mDeviceSet = new HashMap<>();

        public BatchQueryStateRunnable(ArrayList<BLProbeDevice> listDevice) {
            mDevices = listDevice;
            synchronized (mDeviceSet) {
                for (BLProbeDevice device : mDevices) {
                    mDeviceSet.put(device.getDid(), device);
                }
            }
        }

        @Override
        public void run() {
            BLQueryDeviceStatusResult result = mControllerImpl.queryDeviceOnServer(mDevices);
            if (result.getStatus() == BLBaseResult.SUCCESS) {

                ArrayList<BLProbeDevice> backDevices = new ArrayList<>();
                List<BLQueryDeviceStatus> blQueryDeviceStatusList = result.getQueryDeviceMap();
                if (blQueryDeviceStatusList != null) {
                    for (BLQueryDeviceStatus devStatus : blQueryDeviceStatusList) {
                        int state = devStatus.getStatus() > 0 ? BLDeviceState.REMOTE : BLDeviceState.OFFLINE;
                        String did = devStatus.getDid();

                        BLProbeDevice device = mDeviceSet.get(did);
                        device.setState(state);
                        backDevices.add(device);
                    }
                }

                mQueryDeviceStateListener.onQueryDevices(backDevices);
            }
        }
    }

    /**
     * 用于查询到设备状态之后的回调
     */
    public interface QueryDeviceStateListener {
        public void onQuery(String did, int state);

        public void onQueryDevices(ArrayList<BLProbeDevice> listDevice);
    }
}
