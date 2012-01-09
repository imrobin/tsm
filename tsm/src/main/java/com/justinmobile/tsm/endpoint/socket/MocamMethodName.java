package com.justinmobile.tsm.endpoint.socket;

public enum MocamMethodName {
	
	ApplicationListRequest("ApplicationListACK"),
	PutInformationRequest("PutInformationACK"),
	GetInformationRequest("GetInformationACK"),
	ExecAPDUsRequest("ExecAPDUsCmd"),
	LoadClientRequest("LoadClientACK"),
	loginOrRegister("loginOrRegister"),
	SDListRequest("SDListACK"),
	;
	
	private String returnName;
	
	public String getReturnName() {
		return returnName;
	}

	private MocamMethodName(String returnName) {
		this.returnName = returnName;
	}
}
