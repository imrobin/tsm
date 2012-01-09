package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.justinmobile.tsm.application.domain.ApplicationClientInfo;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;
import com.justinmobile.tsm.utils.SystemConfigUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClientInfoList", namespace = NameSpace.CM)
public class ClientInfoList {
	
	@XmlElement(namespace = NameSpace.CM, name="ClientInformation")
	private List<ClientInfo> clientInfo = Lists.newArrayList();

	public List<ClientInfo> getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(List<ClientInfo> clientInfo) {
		this.clientInfo = clientInfo;
	}
	
	public void addAll(List<ApplicationClientInfo> result,String aid) {
		if (CollectionUtils.isNotEmpty(result)) {
			for (ApplicationClientInfo aci : result) {
				ClientInfo ci  = new ClientInfo();
				ci.setAppAID(aid);
				ci.setClientClassName(aci.getClientClassName());
				ci.setClientID(String.valueOf(aci.getId()));
				ci.setClientLoadURL(SystemConfigUtils.getServiceUrl()+aci.getFileUrl());
				ci.setClientName(aci.getName());
				ci.setClientPackageName(aci.getClientPackageName());
				ci.setClientSize(aci.getSize());
				ci.setClientVersion(new Long(aci.getVersion().replace(".","")));
				ci.setIsUpdatable(0x00);
				this.clientInfo.add(ci);
			}
		}
		
	}

}
