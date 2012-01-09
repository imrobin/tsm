package com.justinmobile.tsm.endpoint.sms;

import java.util.ResourceBundle;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.webservice.ProxyServiceFactory;

@Service("smsEndpoint")
public class Cmpp3SmsEndpoint implements SmsEndpoint {

	private ResourceBundle rb = ResourceBundle.getBundle("config/sms");

	public boolean sendMessage(String mobileNo, String smsContent) {
		try {
			String connect = rb.getString("sms.connect");
			if ("local".equals(connect)) {
				String url = rb.getString("sms.url");
				String serviceName = rb.getString("sms.service.name");
				String qName = rb.getString("sms.service.qname");
				ProxyServiceFactory factory = new ProxyServiceFactory(url, serviceName, qName);
				SmsWsClient client = factory.getHttpPort(SmsWsClient.class);
				Client clientProxy = ClientProxy.getClient(client);
				setTimeOut(clientProxy);
				return client.sendMessage(mobileNo, smsContent);
			} else if ("remote".equals(connect)) {
				String url = rb.getString("sms.remote.url");
				String serviceName = rb.getString("sms.remote.service.name");
				String qName = rb.getString("sms.remote.service.qname");
				ProxyServiceFactory factory = new ProxyServiceFactory(url, serviceName, qName);
				OuterWebService client = factory.getHttpPort(OuterWebService.class);
				Client clientProxy = ClientProxy.getClient(client);
				setTimeOut(clientProxy);
				return client.smsNotifyUser(mobileNo, smsContent);
			} else {
				return false;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean pushMessage(String mobileNo, String smsContent) {
		try {
			String connect = rb.getString("sms.connect");
			if ("local".equals(connect)) {
				String url = rb.getString("sms.url");
				String serviceName = rb.getString("sms.service.name");
				String qName = rb.getString("sms.service.qname");
				ProxyServiceFactory factory = new ProxyServiceFactory(url, serviceName, qName);
				SmsWsClient client = factory.getHttpPort(SmsWsClient.class);
				Client clientProxy = ClientProxy.getClient(client);
				setTimeOut(clientProxy);
				return client.sendApdu(mobileNo, 0x01, 0x00, 0x0F, smsContent);
			} else if ("remote".equals(connect)) {
				String url = rb.getString("sms.remote.url");
				String serviceName = rb.getString("sms.remote.service.name");
				String qName = rb.getString("sms.remote.service.qname");
				ProxyServiceFactory factory = new ProxyServiceFactory(url, serviceName, qName);
				OuterWebService client = factory.getHttpPort(OuterWebService.class);
				Client clientProxy = ClientProxy.getClient(client);
				setTimeOut(clientProxy);
				return client.smsPushUser(mobileNo, smsContent);
			} else {
				return false;
			}
		} catch (PlatformException e) {
			throw e;
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	private void setTimeOut(Client clientProxy) {
		HTTPConduit httpConduit = (HTTPConduit) clientProxy.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(5000);
		httpConduit.setClient(policy);
	}

}
