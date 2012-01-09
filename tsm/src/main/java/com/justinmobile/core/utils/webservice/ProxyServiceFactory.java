package com.justinmobile.core.utils.webservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

/**
 * 根据wsdl文件解析，获取实现方法
 * 
 * @author peak
 * 
 * @param <T>
 */
public class ProxyServiceFactory {

	protected Service service;

	protected QName serviceName;

	protected QName httpPortName;

	protected String storeFileUrl;

	protected String trustStorePass;

	protected String keyStorePass;

	protected static final String HTTP_PORT_DEFAULT_SUFFIX = "HttpPort";

	public ProxyServiceFactory(String url, String serviceName, String... nameSpace) {
		try {
			if (!StringUtils.contains(url, "?wsdl")) {
				url += "?wsdl";
			}
			URL wsdlDocumentLocation = new URL(url);
			QName qName = null;
			if (ArrayUtils.isEmpty(nameSpace)) {
				qName = new QName(NameSpace.CM, serviceName, "simota");
			} else {
				qName = new QName(nameSpace[0], serviceName, "simota");
			}
			this.service = Service.create(wsdlDocumentLocation, qName);
			this.serviceName = qName;
			this.httpPortName = new QName(this.serviceName.getNamespaceURI(), this.serviceName.getLocalPart() + HTTP_PORT_DEFAULT_SUFFIX);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new WebServiceException("url is error");
		}
	}

	/**
	 * 不带SSL的webservice服务创建
	 * 
	 * @param wsdlDocumentLocation
	 * @param serviceName
	 */
	public ProxyServiceFactory(URL wsdlDocumentLocation, QName serviceName) {
		this.service = Service.create(wsdlDocumentLocation, serviceName);
		this.serviceName = serviceName;
		this.httpPortName = new QName(this.serviceName.getNamespaceURI(), this.serviceName.getLocalPart() + HTTP_PORT_DEFAULT_SUFFIX);
	}

	/**
	 * 带SSL的webservice服务创建
	 * 
	 * @param wsdlDocumentLocation
	 * @param serviceName
	 * @param storeFileUrl
	 *            密钥存放的路径
	 */
	public ProxyServiceFactory(URL wsdlDocumentLocation, QName serviceName, String storeFileUrl) {
		if (StringUtils.isEmpty(storeFileUrl)) {
			throw new WebServiceException("unable to find valid certification path to requested target");
		}
		System.setProperty("javax.net.ssl.trustStore", storeFileUrl);
		this.service = Service.create(wsdlDocumentLocation, serviceName);
		this.serviceName = serviceName;
		this.httpPortName = new QName(this.serviceName.getNamespaceURI(), this.serviceName.getLocalPart() + HTTP_PORT_DEFAULT_SUFFIX);
		this.storeFileUrl = storeFileUrl;
	}

	/**
	 * 带SSL的webservice服务创建
	 * 
	 * @param wsdlDocumentLocation
	 * @param serviceName
	 * @param storeFileUrl
	 * @param trustStorePass
	 * @param keyStorePass
	 */
	public ProxyServiceFactory(URL wsdlDocumentLocation, QName serviceName, String storeFileUrl, String trustStorePass, String keyStorePass) {
		this(wsdlDocumentLocation, serviceName, storeFileUrl);
		if (StringUtils.isEmpty(trustStorePass)) {
			throw new WebServiceException("unable to find valid trustStorePass");
		}
		if (StringUtils.isEmpty(keyStorePass)) {
			throw new WebServiceException("unable to find valid keyStorePass");
		}
		this.trustStorePass = trustStorePass;
		this.keyStorePass = keyStorePass;
	}

	public QName getServiceName() {
		return serviceName;
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}

	public QName getHttpPortName() {
		return httpPortName;
	}

	public void setHttpPortName(QName httpPortName) {
		this.httpPortName = httpPortName;
	}

	/**
	 * 获取Web服务的代理对象<br/>
	 * 如要管理名字空间前缀，请查看T getHttpPort(Class<T> serviceClass, Map<String, String>
	 * namespaceMap)
	 * 
	 * @param serviceClass
	 *            Web服务的服务类
	 * @return
	 */
	public <T> T getHttpPort(Class<T> serviceClass) {
		return getHttpPort(serviceClass, null);
	}

	/**
	 * 获取Web服务的代理对象，并进行名字空间前缀管理
	 * 
	 * @param serviceClass
	 *            Web服务的服务类
	 * @param namespaceMap
	 *            名字空空间及前缀的对应关系
	 * @return
	 */
	public <T> T getHttpPort(Class<T> serviceClass, Map<String, String> namespaceMap) {
		T port = service.getPort(httpPortName, serviceClass);

		org.apache.cxf.service.Service s = ((JaxWsClientProxy) Proxy.getInvocationHandler(port)).getClient().getEndpoint().getService();
		if (s.getDataBinding() instanceof JAXBDataBinding && null != namespaceMap) {
			((JAXBDataBinding) s.getDataBinding()).setNamespaceMap(namespaceMap);
		}

		return port;
	}

	public <T> void setSSLProperties(Class<T> serviceClass) {
		if (StringUtils.isEmpty(this.storeFileUrl)) {
			throw new WebServiceException("unable to find valid certification path to requested target");
		}
		Client client = ClientProxy.getClient(serviceClass);
		HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
		TLSClientParameters tlsParams = httpConduit.getTlsClientParameters();
		if (tlsParams == null) {
			tlsParams = new TLSClientParameters();
		}
		tlsParams.setSecureSocketProtocol("SSL");
		tlsParams.setKeyManagers(getKeyManagers());
		tlsParams.setTrustManagers(getTrustManagers());
		httpConduit.setTlsClientParameters(tlsParams);
	}

	private TrustManager[] getTrustManagers() {
		try {
			String alg = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory factory = TrustManagerFactory.getInstance(alg);
			InputStream fp = new FileInputStream(this.storeFileUrl);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(fp, this.trustStorePass.toCharArray());
			fp.close();
			factory.init(ks);
			TrustManager[] tms = factory.getTrustManagers();
			return tms;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		}
	}

	private KeyManager[] getKeyManagers() {
		try {
			String alg = KeyManagerFactory.getDefaultAlgorithm();
			KeyManagerFactory factory = KeyManagerFactory.getInstance(alg);
			InputStream fp = new FileInputStream(this.storeFileUrl);
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(fp, this.keyStorePass.toCharArray());
			fp.close();
			factory.init(ks, this.keyStorePass.toCharArray());
			KeyManager[] keyms = factory.getKeyManagers();
			return keyms;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (KeyStoreException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebServiceException(e);
		}
	}

	public String getStoreFileUrl() {
		return storeFileUrl;
	}

	public void setStoreFileUrl(String storeFileUrl) {
		System.setProperty("javax.net.ssl.trustStore", storeFileUrl);
		this.storeFileUrl = storeFileUrl;
	}

	public String getTrustStorePass() {
		return trustStorePass;
	}

	public void setTrustStorePass(String trustStorePass) {
		this.trustStorePass = trustStorePass;
	}

	public String getKeyStorePass() {
		return keyStorePass;
	}

	public void setKeyStorePass(String keyStorePass) {
		this.keyStorePass = keyStorePass;
	}
}