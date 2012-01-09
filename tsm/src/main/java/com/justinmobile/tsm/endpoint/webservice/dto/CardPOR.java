package com.justinmobile.tsm.endpoint.webservice.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardPOR", namespace = NameSpace.CM)
public class CardPOR {

	public static String CARD_SUCCESS_CODE = "9000";

	@XmlElement(name = "APDUSum", namespace = NameSpace.CM)
	private String apduSum;

	@XmlElement(name = "LastApduSW", namespace = NameSpace.CM)
	private String lastAPDUSW;

	@XmlElement(name = "LastData", namespace = NameSpace.CM)
	private String lastData;

	@XmlElement(name = "LastApdu", namespace = NameSpace.CM)
	private String lastApdu;

	public String getApduSum() {
		return apduSum;
	}

	public void setApduSum(String apduSum) {
		this.apduSum = apduSum;
	}

	public String getLastAPDUSW() {
		return lastAPDUSW;
	}

	public void setLastAPDUSW(String lastAPDUSW) {
		this.lastAPDUSW = lastAPDUSW;
	}

	public String getLastData() {
		return lastData;
	}

	public void setLastData(String lastData) {
		this.lastData = lastData;
	}

	public String getLastApdu() {
		return lastApdu;
	}

	public void setLastApdu(String lastApdu) {
		this.lastApdu = lastApdu;
	}

	public boolean isSuccess() {
		return CARD_SUCCESS_CODE.equals(lastAPDUSW);
	}

}
