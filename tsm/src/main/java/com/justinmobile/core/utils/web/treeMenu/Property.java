package com.justinmobile.core.utils.web.treeMenu;

import java.io.Serializable;

public class Property implements Serializable {
	
	private static final long serialVersionUID = -1508251280862216832L;

	private String name;
	
	private Boolean hasCheckbox;

	public Property(String name, Boolean hasCheckbox) {
		super();
		this.name = name;
		this.hasCheckbox = hasCheckbox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getHasCheckbox() {
		return hasCheckbox;
	}

	public void setHasCheckbox(Boolean hasCheckbox) {
		this.hasCheckbox = hasCheckbox;
	}
	
}
