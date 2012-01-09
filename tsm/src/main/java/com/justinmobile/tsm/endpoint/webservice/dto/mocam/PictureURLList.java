

package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.justinmobile.tsm.endpoint.webservice.NameSpace;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PictureURLList", namespace = NameSpace.CM)
public class PictureURLList {
	@XmlElement(namespace = NameSpace.CM,name="Picture")
	private List<String> pictureUrlList = new ArrayList<String>();

	public List<String> getPictureURLList() {
		return pictureUrlList;
	}

	public void setPictureUrlList(List<String> pictureUrlList) {
		this.pictureUrlList = pictureUrlList;
	}

	public void add(String pictureUrl) {
		this.pictureUrlList.add(pictureUrl);
	}

}



