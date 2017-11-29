package cn.com.broadlink.sdk.constants.family;

/**
 * Created by zjjllj on 2017/2/14.
 */

public class BLModuleType {
    public static final int Scene = 0;
    public static final int Sp = 1;
    public static final int PowerConsumption = 2;
    public static final int Common = 3;
    public static final int ControlCenter = 4;
    public static final int Customize_Scene = 5;
    public static final int HK_SP = 6;
    public static final int RM = 9;
    public static final int RM_AC = 10;
    public static final int RM_TV = 11;
    public static final int RM_TOPBOX = 12;
    public static final int RM_NETWORK_BOX = 13;
    public static final int RM_TOPBOX_CHANNEL = 14;
    public static final int RM_TC_1 = 15;
    public static final int RM_TC_2 = 16;
    public static final int RM_TC_3 = 17;
    public static final int RM_CURTAIN = 18;
    public static final int RM_LAMP = 19;
    public static final int RM_COMMON = 20;
    public static final int RM_LAMP_RADIO = 21;
    public static final int RM_CURTAIN_RADIO = 22;
    public static final int RM_CUSTOM_AC = 23;
    public static final int RM_NEW_TV = 24;
    public static final int RM_NEW_TOPBOX = 25;

    public static final int SENSOR_GAS = 100;
    public static final int SENSOR_SMOKE_DETECTOR = 101;
    public static final int SENSOR_SECURITY = 102;
    public static final int SENSOR_IR = 103;
    public static final int SENSOR_DOOR = 104;

    /**设备对应的模块**/
    public static boolean isDeviceModule(int type){
        return type == Sp || type == Common  || type == RM_AC || type == RM_TV || type == RM_TOPBOX
                || type == RM_TOPBOX_CHANNEL || type == RM_TC_1 || type == RM_TC_2 || type == RM_TC_3
                || type == RM_CURTAIN || type == RM_LAMP || type == RM_COMMON || type == RM_LAMP_RADIO
                || type == RM_CURTAIN_RADIO || type == RM_CUSTOM_AC || type == RM_NEW_TV || type == RM_NEW_TOPBOX;
    }

    /**设备对应的模块**/
    public static boolean isRMDeviceModule(int type){
        return type == RM_AC || type == RM_TV || type == RM_TOPBOX
                || type == RM_TOPBOX_CHANNEL || type == RM_TC_1 || type == RM_TC_2 || type == RM_TC_3
                || type == RM_CURTAIN || type == RM_LAMP || type == RM_COMMON || type == RM_LAMP_RADIO
                || type == RM_CURTAIN_RADIO || type == RM_CUSTOM_AC || type == RM_NEW_TV || type == RM_NEW_TOPBOX;
    }
}
