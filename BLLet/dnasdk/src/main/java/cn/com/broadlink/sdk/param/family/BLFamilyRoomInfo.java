package cn.com.broadlink.sdk.param.family;

import org.json.JSONObject;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLFamilyRoomInfo {
    private String familyId;        //房间所在家庭的ID
    private String roomId;          //房间自身ID
    private String name;            //房间名称
    private int type;               //房间类型
    private int order;              //房间序号
    private String action;          //房间执行操作 add,modify,del

    public BLFamilyRoomInfo() {

    }

    public BLFamilyRoomInfo(JSONObject object) {
        if (object != null) {
            this.familyId = object.optString("familyid", null);
            this.roomId = object.optString("roomid", null);
            this.name = object.optString("name", null);
            this.type = object.optInt("type");
            this.order = object.optInt("order");
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
