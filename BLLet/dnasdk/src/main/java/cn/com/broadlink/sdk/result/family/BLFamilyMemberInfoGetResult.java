package cn.com.broadlink.sdk.result.family;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.param.family.BLFamilyMemberInfo;
import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * Created by zjjllj on 2017/2/15.
 */

public class BLFamilyMemberInfoGetResult extends BLBaseResult {
    private List<BLFamilyMemberInfo> memberInfos = new ArrayList<BLFamilyMemberInfo>();

    public List<BLFamilyMemberInfo> getMemberInfos() {
        return memberInfos;
    }

    public void setMemberInfos(List<BLFamilyMemberInfo> memberInfos) {
        this.memberInfos = memberInfos;
    }
}
