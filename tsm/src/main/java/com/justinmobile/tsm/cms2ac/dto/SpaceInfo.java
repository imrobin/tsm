package com.justinmobile.tsm.cms2ac.dto;

public class SpaceInfo {
	
	private String sdAid;
	
	private long freeNoneVolatile;

	private int freeVolatile;

	public String getSdAid() {
		return sdAid;
	}

	public void setSdAid(String sdAid) {
		this.sdAid = sdAid;
	}

	public long getFreeNoneVolatile() {
		return freeNoneVolatile;
	}

	public void setFreeNoneVolatile(long freeNoneVolatile) {
		this.freeNoneVolatile = freeNoneVolatile;
	}

	public int getFreeVolatile() {
		return freeVolatile;
	}

	public void setFreeVolatile(int freeVolatile) {
		this.freeVolatile = freeVolatile;
	} 

}
