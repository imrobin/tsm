package com.justinmobile.tsm.endpoint.sms;


public interface SmsEndpoint {
	
	boolean sendMessage(String mobileNo, String smsContent);
	
	boolean pushMessage(String mobileNo, String smsContent);
	
	boolean pushMessage(String mobileNo, Integer messageFormat, String daPort, String srcPort, String clientId, String seId,
			String pushSerial);
	
}
