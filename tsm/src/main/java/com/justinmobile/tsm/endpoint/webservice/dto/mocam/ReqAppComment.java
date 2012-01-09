package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PutInformationRequest", namespace = NameSpace.CM)
public class ReqAppComment extends BasicRequest {
	
	@XmlElement(namespace = NameSpace.CM, name="SEID")
	private String cardNo;
	
	@XmlElement(namespace = NameSpace.CM, name="AppAID")
	private String appAID;
	
	@XmlElement(namespace = NameSpace.CM, name="StarGrade")
	private Integer starGrade;
	
	@XmlElement(namespace = NameSpace.CM, name="Comment")
	private AppComment comment;

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getAppAID() {
		return appAID;
	}

	public void setAppAID(String appAID) {
		this.appAID = appAID;
	}

	public Integer getStarGrade() {
		return starGrade;
	}

	public void setStarGrade(Integer starGrade) {
		this.starGrade = starGrade;
	}

	public AppComment getComment() {
		return comment;
	}

	public void setComment(AppComment comment) {
		this.comment = comment;
	}
}
