package cn.com.broadlink.sdk.result.family;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLDefineRoomTypeResult extends BLBaseResult {
    private List<Integer> defineRoomTypes = new ArrayList<Integer>();

    public List<Integer> getDefineRoomTypes() {
        return defineRoomTypes;
    }

    public void setDefineRoomTypes(List<Integer> defineRoomTypes) {
        this.defineRoomTypes = defineRoomTypes;
    }
}
