package cn.com.broadlink.sdk;

/**
 * Created by YeJin on 2016/5/5.
 */
final class BLDevCmdConstants {

    /**设备控制**/
    protected static final String DEV_CTRL = "dev_ctrl";

    /**设备透传控制**/
    protected static final String DEV_PASSTHROUGH = "dev_passthrough";

    /** 通知网关设备进入扫描子设备的模式  **/
    public static final String DEV_NEWSUBDEV_SCAN_START = "dev_newsubdev_scan_start";

    /** 通知网关设备退出扫描子设备的模式  **/
    public static final String DEV_NEWSUBDEV_SCAN_STOP = "dev_newsubdev_scan_stop";

    /** 获取扫描到的新子设备的列表  **/
    public static final String DEV_NEWSUBDEV_LIST = "dev_newsubdevlist";

    /** 获取网关设备中已经添加的子设备列表**/
    public static final String DEV_SUBDEV_LIST = "dev_subdevlist";

    /** 添加新子设备到网关设备**/
    public static final String DEV_SUBDEV_ADD = "dev_subdevadd";

    /** 子设备添加结果查询**/
    public static final String DEV_SUBDEV_ADD_RESULT = "dev_subdev_add_result";

    /**从网关设备中删除指定的子设备**/
    public static final String DEV_SUBDEV_DEL = "dev_subdevdel";

    /** 修改网关设备中指定的子设备信息**/
    public static final String DEV_SUBDEV_MODIFY = "dev_subdevmodify";

    /** 查询子设备版本**/
    public static final String DEV_SUBDEV_VERSION = "dev_subdev_query_version";

    /**获取设备发送的二进制指令**/
    public static final String DEV_DATA = "dev_data";

}
