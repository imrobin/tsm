package com.justinmobile.tsm.cms2ac.response;

public class Response {

	private byte sw1;

	private byte sw2;

	private byte[] data;

	public byte getSw1() {
		return sw1;
	}

	public void setSw1(byte sw1) {
		this.sw1 = sw1;
	}

	public byte getSw2() {
		return sw2;
	}

	public void setSw2(byte sw2) {
		this.sw2 = sw2;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return (getSw1() == (byte) 0x90 && getSw2() == (byte) 0x00);
	}
}
