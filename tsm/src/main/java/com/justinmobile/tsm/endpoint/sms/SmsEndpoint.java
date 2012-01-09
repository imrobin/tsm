package com.justinmobile.tsm.endpoint.sms;


public interface SmsEndpoint {
	
	boolean sendMessage(String mobileNo, String smsContent);
	
	boolean pushMessage(String mobileNo, String smsContent);
	
}
