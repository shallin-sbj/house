package com.insu.house.common.constants;

public enum HouseUserType {

	/**
	 * 出售
	 */
	SALE(1),
	/**
	 * 租用
	 */
	BOOKMARK(2);
	
	public final Integer value;
	
	private HouseUserType(Integer value){
		this.value = value;
	}
}
