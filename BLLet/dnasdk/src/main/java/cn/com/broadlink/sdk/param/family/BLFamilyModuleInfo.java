package cn.com.broadlink.sdk.param.family;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyModuleInfo {
    private String familyId;                        //模块所在家庭ID
    private String roomId;                          //模块所在房间ID
    private String moduleId;                        //模块自身ID
    private String name;                            //模块名称
    private String iconPath;                        //模块icon路径
    private List<ModuleDeviceInfo> moduleDevs;      //模块下挂设备列表
    private int followDev;                          //模块移动时，设备是否跟随  0-NO 1-YES
    private int order;                              //模块序号
    private int flag;                               //模块Flag
    private int moduleType;                         //模块类型
    private String scenceType;                      //场景类型
    private String extend;                          //模块扩展信息，由用户自定义

    public BLFamilyModuleInfo() {

    }

    public static class ModuleDeviceInfo{
        private String did;     //设备Did
        private String sdid;    //若为子设备，则包含该子设备did
        private int order;      //序号
        private String content; //设备相关扩展内容，由用户自定义

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getSdid() {
            return sdid;
        }

        public void setSdid(String sdid) {
            this.sdid = sdid;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public BLFamilyModuleInfo(JSONObject object) {
        if (object != null) {
            this.familyId = object.optString("familyid", null);
            this.roomId = object.optString("roomid", null);
            this.moduleId = object.optString("moduleid", null);
            this.name = object.optString("name", null);
            this.iconPath = object.optString("icon", null);
            this.scenceType = object.optString("scenetype", null);
            this.extend = object.optString("extend", null);

            this.moduleType = object.optInt("moduletype");
            this.order = object.optInt("order");
            this.flag = object.optInt("flag");
            this.followDev = object.optInt("followdev");

            JSONArray moduledev = object.optJSONArray("moduledev");
            if (moduledev != null) {
                this.moduleDevs = new ArrayList<ModuleDeviceInfo>();
                for (int i = 0; i < moduledev.length(); i++) {
                    JSONObject jsonObject = moduledev.optJSONObject(i);
                    ModuleDeviceInfo deviceInfo = new ModuleDeviceInfo();
                    deviceInfo.setDid(jsonObject.optString("did"));
                    deviceInfo.setSdid(jsonObject.optString("sdid"));
                    deviceInfo.setOrder(jsonObject.optInt("order"));
                    deviceInfo.setContent(jsonObject.optString("content"));
                    this.moduleDevs.add(deviceInfo);
                }
            }
        }
    }

    public JSONObject toDictionary() {

        try {
            JSONObject object = new JSONObject();
            if (this.familyId != null) {
                object.put("familyid", this.familyId);
            }
            if (this.roomId != null) {
                object.put("roomid", this.roomId);
            }
            if (this.moduleId != null) {
                object.put("moduleid", this.moduleId);
            }
            if (this.name != null) {
                object.put("name", this.name);
            }
            if (this.iconPath != null) {
                object.put("icon", this.iconPath);
            }
            if (this.scenceType != null) {
                object.put("scenetype", this.scenceType);
            }
            if (this.extend != null) {
                object.put("extend", this.extend);
            }
            object.put("moduletype", this.moduleType);
            object.put("flag", this.flag);
            object.put("followdev", this.followDev);
            object.put("order", this.order);

            if (this.moduleDevs != null) {
                JSONArray jModuleDevs = new JSONArray();
                for (ModuleDeviceInfo devInfo : this.moduleDevs) {
                    JSONObject devObject = new JSONObject();
                    devObject.put("did", devInfo.getDid());
                    devObject.put("sdid", devInfo.getSdid());
                    devObject.put("order", devInfo.getOrder());
                    devObject.put("content", devInfo.getContent());

                    jModuleDevs.put(devObject);
                }

                object.put("moduledev", jModuleDevs);
            }

            return object;

        } catch (Exception e) {
            return null;
        }
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getFollowDev() {
        return followDev;
    }

    public void setFollowDev(int followDev) {
        this.followDev = followDev;
    }

    public List<ModuleDeviceInfo> getModuleDevs() {
        return moduleDevs;
    }

    public void setModuleDevs(List<ModuleDeviceInfo> moduleDevs) {
        this.moduleDevs = moduleDevs;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getModuleType() {
        return moduleType;
    }

    public void setModuleType(int moduleType) {
        this.moduleType = moduleType;
    }

    public String getScenceType() {
        return scenceType;
    }

    public void setScenceType(String scenceType) {
        this.scenceType = scenceType;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }
}
