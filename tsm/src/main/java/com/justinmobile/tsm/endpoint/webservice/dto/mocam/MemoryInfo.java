package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MemoryInfo", namespace = NameSpace.CM)
public class MemoryInfo {
	
	@XmlElement(namespace = NameSpace.CM, name="NonVolatileMemory")
	private Long nonVolatileMemory;
	
	@XmlElement(namespace = NameSpace.CM, name="VolatileMemory")
	private Integer volatileMemory;
	
	@XmlElement(namespace = NameSpace.CM, name="TotalMemory")
	private Long totalMemory;

	public Long getNonVolatileMemory() {
		return nonVolatileMemory;
	}

	public void setNonVolatileMemory(Long nonVolatileMemory) {
		this.nonVolatileMemory = nonVolatileMemory;
	}

	public Integer getVolatileMemory() {
		return volatileMemory;
	}

	public void setVolatileMemory(Integer volatileMemory) {
		this.volatileMemory = volatileMemory;
	}

	public Long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(Long totalMemory) {
		this.totalMemory = totalMemory;
	}

}
