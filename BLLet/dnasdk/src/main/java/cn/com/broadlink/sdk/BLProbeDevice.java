package cn.com.broadlink.sdk;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

/**
 * Created by zhuxuyang on 16/4/17.
 */
class BLProbeDevice extends BLDNADevice {

    private JSONObject extendObject;

    public BLProbeDevice(){
    }

    public BLProbeDevice(BLDNADevice device){
        this.setDid(device.getDid());
        this.setMac(device.getMac());
        this.setPid(device.getPid());
        this.setName(device.getName());
        this.setLock(device.isLock());
        this.setPassword(device.getPassword());
        this.setId(device.getId());
        this.setType(device.getType());
        this.setKey(device.getKey());
        this.setpDid(device.getpDid());
        this.setLanaddr(device.getLanaddr());
        this.setState(device.getState());
        this.setExtend(device.getExtend());
        if(!TextUtils.isEmpty(device.getExtend())){
            try {
                this.setExtendObject(new JSONObject(device.getExtend()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 转为Json字符串
     * @return
     */
    public String toJSONString(){
        try {
            JSONObject jDevice = new JSONObject();
            jDevice.put("did", this.getDid());
            jDevice.put("mac", this.getMac());
            jDevice.put("pid", this.getPid());
            jDevice.put("name", this.getName());
            jDevice.put("type", this.getType());
            jDevice.put("lock", this.isLock());
            jDevice.put("password", this.getPassword());
            jDevice.put("id", this.getId());
            jDevice.put("key", this.getKey());
            jDevice.put("pDid", this.getpDid());

            if(this.getLanaddr() != null){
                jDevice.put("lanaddr", this.getLanaddr());
            }

            if(this.getExtendObject() != null) {
                jDevice.put("extend", this.getExtendObject());
            }

            return jDevice.toString();
        } catch (JSONException e) {
            BLCommonTools.handleError(e);

            return "";
        }
    }

    public JSONObject getExtendObject() {
        return extendObject;
    }

    public void setExtendObject(JSONObject extendObject) {
        this.extendObject = extendObject;
    }
}
