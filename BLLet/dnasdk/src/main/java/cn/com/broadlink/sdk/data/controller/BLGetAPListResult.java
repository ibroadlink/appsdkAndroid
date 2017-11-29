package cn.com.broadlink.sdk.data.controller;

import java.util.ArrayList;
import java.util.List;

import cn.com.broadlink.sdk.result.BLBaseResult;

/**
 * 获取AP列表结果
 * @author YeJin
 *
 */
public class BLGetAPListResult extends BLBaseResult {

	private List<BLAPInfo> list = new ArrayList<BLAPInfo>();

	public List<BLAPInfo> getList() {
		return list;
	}

	public void setList(List<BLAPInfo> list) {
		this.list = list;
	}
	
	
}
