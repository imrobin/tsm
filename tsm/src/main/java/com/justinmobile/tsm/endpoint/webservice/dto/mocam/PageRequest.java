package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PageRequest", namespace = NameSpace.CM)
public class PageRequest extends BasicRequest {
	
	@XmlElement(namespace = NameSpace.CM, name="QueryCondition")
	private String queryCondition;
	
	@XmlElement(namespace = NameSpace.CM, name="ListOrder")
	private Integer listOrder;
	
	@XmlElement(namespace = NameSpace.CM, name="PageSize")
	private Integer pageSize;
	
	@XmlElement(namespace = NameSpace.CM, name="PageNumber")
	private Integer pageNumber;

	public String getQueryCondition() {
		return queryCondition;
	}

	public void setQueryCondition(String queryCondition) {
		this.queryCondition = queryCondition;
	}

	public Integer getListOrder() {
		return listOrder;
	}

	public void setListOrder(Integer listOrder) {
		this.listOrder = listOrder;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
}
