package cn.com.broadlink.sdk.data.controller;

public class BLRMCloudAcConstants {
	/** 开 **/
	public static final int ACT_PWR_ON = 0;
	/** 关 **/
	public static final int ACT_PWR_OFF = 1;
	/** 温度加 **/
	public static final int ACT_TEMP_ADD = 2;
	/** 温度减 **/
	public static final int ACT_TEMP_SUB = 3;
	
	/** 开关 **/
	public static final int BTN_SWITCH = 0;
	/** 模式 **/
	public static final int BTN_MODE = 1;
	/** 温度加 **/
	public static final int BTN_TEM_ADD = 2;
	/** 温度减 **/
	public static final int BTN_TEM_RED = 3;
	/** 风速加 **/
	public static final int BTN_WIND_ADD = 4;
	/** 风减 **/
	public static final int BTN_WIND_RED = 4;
	
    /** 红外频率 **/
    public static final int IR_HZ = 38;
    
	/** 自动模式 **/
	public static final int IRDA_MODE_AUTO = 0; // 自动模式
	/** 制冷模式 **/
	public static final int IRDA_MODE_COOL = 1; // 制冷模式
	/** 除湿模式 **/
	public static final int IRDA_MODE_HUMIDITY = 2; // 除湿模式
	/** 通风模式 **/
	public static final int IRDA_MODE_WIND = 3; // 通风模式
	/** 制热模式 **/
	public static final int IRDA_MODE_HEAT = 4; // 制热模式

	/** 空调处于关闭状态 **/
	public static final int IRDA_STATUS_CLOSE = 0; // 空调处于关闭状态
	/** 空调处于开启状态 **/
	public static final int IRDA_STATUS_OPEN = 1; // 空调处于开启状态

	/** 风速自动 **/
	public static final int IRDA_WINDSPEED_AUTO = 0; // 风速自动
	/** 风速低 **/
	public static final int IRDA_WINDSPEED_LOW = 1; // 风速低
	/** 风速中 **/
	public static final int IRDA_WINDSPEED_MIDDLE = 2; // 风速中
	/** 风速高 **/
	public static final int IRDA_WINDSPEED_HIGH = 3; // 风速高

	/** 风向固定 **/
	public static final int IRDA_WINDIRECT_FIXED = 0; // 风向固定
	/** 风向自动 **/
	public static final int IRDA_WINDIRECT_AUTO = 1; // 风向自动

	/** 空调开关按键 **/
	public static final int IRDA_KEY_SWITCH = 0; // 空调开关按键
	/** 空调模式按键**/
	public static final int IRDA_KEY_MODE = 1; // 空调模式按键
	/** 空调风向按键 **/
	public static final int IRDA_KEY_WIND_DIRECT = 2; // 空调风向按键
	/** 空调风速按键 **/
	public static final int IRDA_KEY_WIND_SPEED = 3; // 空调风速按键
	/** 空调温度加键 **/
	public static final int IRDA_KEY_TEMP_ADD = 4; // 空调温度加键
	/** 空调温度减键 **/
	public static final int IRDA_KEY_TEMP_SUB = 5; // 空调温度减键
}
