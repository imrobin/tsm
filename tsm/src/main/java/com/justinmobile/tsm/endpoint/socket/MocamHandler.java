package com.justinmobile.tsm.endpoint.socket;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.XmlUtils;
import com.justinmobile.core.utils.reflection.ConvertUtils;
import com.justinmobile.core.utils.reflection.ReflectionUtils;
import com.justinmobile.tsm.endpoint.webservice.MobileWebService;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.BasicResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.GetInformation;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoadClientResponse;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.LoginOrRegisterRequest;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqAppComment;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqGetApplicationInfo;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ReqSdList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResApplicationList;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResExecAPDU;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResLoginOrRegister;
import com.justinmobile.tsm.endpoint.webservice.dto.mocam.ResSdList;

@Service("mocamHandler")
public class MocamHandler {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MobileWebService mobileWebService;

	public String messageReceived(String message) throws Exception {
		log.info(XmlUtils.formatXml(message));
		if (StringUtils.isBlank(message)) {
			return "message not found";
		}
		Document document = DocumentHelper.parseText(message);
		Element root = document.getRootElement();
		String result = "";
		try {
			MocamMethodName rootName = MocamMethodName.valueOf(root.getName());
			switch (rootName) {
			case ApplicationListRequest:
				ReqApplicationList reqApplicationList = new ReqApplicationList();
				parseXml(reqApplicationList, root);
				ResApplicationList resApplicationList = mobileWebService.listApplication(reqApplicationList);
				result = toXml(resApplicationList, MocamMethodName.ApplicationListRequest.getReturnName());
				break;
			case PutInformationRequest:
				ReqAppComment reqAppComment = new ReqAppComment();
				parseXml(reqAppComment, root);
				BasicResponse basicResponse = mobileWebService.postAppComment(reqAppComment);
				result = toXml(basicResponse, MocamMethodName.PutInformationRequest.getReturnName());
				break;
			case GetInformationRequest:
				ReqGetApplicationInfo reqGetApplicationInfo = new ReqGetApplicationInfo();
				parseXml(reqGetApplicationInfo, root);
				GetInformation getInformation = mobileWebService.getInfo(reqGetApplicationInfo);
				result = toXml(getInformation, MocamMethodName.GetInformationRequest.getReturnName());
				break;
			case ExecAPDUsRequest:
				ReqExecAPDU reqExecAPDU = unmarshal(ReqExecAPDU.class, message);
				// parseXml(reqExecAPDU, root);
				ResExecAPDU resExecAPDU = mobileWebService.execAPDU(reqExecAPDU);
				result = toXml(resExecAPDU, MocamMethodName.ExecAPDUsRequest.getReturnName());
				break;
			case LoadClientRequest:
				LoadClientRequest loadClientRequest = new LoadClientRequest();
				parseXml(loadClientRequest, root);
				LoadClientResponse loadClientResponse = mobileWebService.loadClient(loadClientRequest);
				result = toXml(loadClientResponse, MocamMethodName.LoadClientRequest.getReturnName());
				break;
			case loginOrRegister:
				LoginOrRegisterRequest loginOrRegisterRequest = new LoginOrRegisterRequest();
				parseXml(loginOrRegisterRequest, root);
				ResLoginOrRegister resLoginOrRegister = mobileWebService.loginOrRegiseter(loginOrRegisterRequest);
				result = toXml(resLoginOrRegister, MocamMethodName.loginOrRegister.getReturnName());
				break;
			case SDListRequest:
				ReqSdList reqSdList = new ReqSdList();
				parseXml(reqSdList, root);
				ResSdList resSdList = mobileWebService.listSd(reqSdList);
				result = toXml(resSdList, MocamMethodName.SDListRequest.getReturnName());
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 非法请求
		}
		log.info(XmlUtils.formatXml(result));
		return result;
	}

	@SuppressWarnings("unchecked")
	private <T> T unmarshal(Class<T> clazz, String xmlText) {
		try {
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(clazz);
			Unmarshaller um = jaxbContext.createUnmarshaller();

			InputStream is = new ByteArrayInputStream(xmlText.getBytes());
			return (T) um.unmarshal(is);
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR);
		}
	}

	private void parseXml(Object bean, Element root) {
		@SuppressWarnings("unchecked")
		List<Element> chilren = root.elements();
		Map<String, Field> map = getBeanMap(bean);
		for (Element element : chilren) {
			Field field = map.get(element.getName());
			if (field != null) {
				boolean b = field.isAccessible();
				field.setAccessible(true);
				try {
					if (element.isTextOnly()) {// 如果只是文本
						String text = element.getTextTrim();
						if (text != null) {
							field.set(bean, ConvertUtils.convertStringToObject(text, field.getType()));
						}
					} else {
						try {
							if ("List".equals(field.getType().getSimpleName())) {
								ArrayList<Object> newList = new ArrayList<Object>();
								parseXml(newList, element);
								field.set(bean, newList);
							} else {
								Object newInstance = field.getType().newInstance();
								parseXml(newInstance, element);
								if (bean instanceof ArrayList) {
									@SuppressWarnings("unchecked")
									ArrayList<Object> newList = (ArrayList<Object>) bean;
									newList.add(newInstance);
								} else {
									field.set(bean, newInstance);
								}
							}

						} catch (InstantiationException e) {
							e.printStackTrace();
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				field.setAccessible(b);
			}
		}
	}

	private Map<String, Field> getBeanMap(Object bean) {
		Set<Field> fields = ReflectionUtils.getAllDeclareFields(bean);
		Map<String, Field> map = new LinkedHashMap<String, Field>();
		for (Field field : fields) {// 遍历所有field
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			if (xmlElement == null || "##default".equals(xmlElement.name())) {// 如果制定命名使用指定的名称，否则使用field的name
				map.put(field.getName(), field);
			} else {
				map.put(xmlElement.name(), field);
			}
		}
		return map;
	}

	public String toXml(Object bean, String rootName) {
		Document document = DocumentHelper.createDocument();
		Namespace namespace = new Namespace("simota", "http://www.chinamobile.com");
		Element root = document.addElement(new QName(rootName, namespace));
		root.addAttribute(new QName("schemaLocation", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")),
				"http://www.chinamobile.com ..\\xsds\\online-resp.xsd");
		setFieldValue(bean, namespace, root);
		return document.asXML();
	}

	private void setFieldValue(Object bean, Namespace namespace, Element parentElement) {
		Map<String, Field> map = getBeanMap(bean);
		for (Map.Entry<String, Field> entry : map.entrySet()) {
			Field field = entry.getValue();
			boolean b = field.isAccessible();
			field.setAccessible(true);
			try {
				Object value = field.get(bean);
				if (value != null) {
					Element e = parentElement.addElement(new QName(entry.getKey(), namespace));
					String[] basicTypeName = new String[] { "String", "Long", "Integer", "int", "long", "Float", "float", "Double",
							"double", "BigDecimal", "Boolean", "boolean" };
					if (ArrayUtils.contains(basicTypeName, field.getType().getSimpleName())) {
						e.addText(String.valueOf(value));
					} else if (value instanceof Collection) {
						@SuppressWarnings("rawtypes")
						Collection c = (Collection) value;
						parentElement.remove(e);
						for (Object childObject : c) {
							Element childElement = parentElement.addElement(new QName(entry.getKey(), namespace));
							if (ArrayUtils.contains(basicTypeName, childObject.getClass().getSimpleName())) {
								childElement.addText(String.valueOf(childObject));
							} else {
								setFieldValue(childObject, namespace, childElement);
							}
						}
					} else {
						setFieldValue(value, namespace, e);
					}
				}
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			field.setAccessible(b);
		}
	}

}
