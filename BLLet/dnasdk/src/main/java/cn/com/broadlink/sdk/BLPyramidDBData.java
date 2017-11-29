package cn.com.broadlink.sdk;

/**
 * Created by zhuxuyang on 15/11/4.
 */
final class BLPyramidDBData {
    protected static final int TYPE_APP = 1;
    protected static final int TYPE_PAGE = 2;
    protected static final int TYPE_EVENT = 3;
    protected static final int TYPE_WIFI = 4;
    protected static final int TYPE_BLUETOOTH = 5;

    protected int id;
    protected int type;
    protected String data;
}
