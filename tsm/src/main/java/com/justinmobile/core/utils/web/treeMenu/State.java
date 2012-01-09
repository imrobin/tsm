package com.justinmobile.core.utils.web.treeMenu;

import java.io.Serializable;

public class State implements Serializable {
	
	private static final long serialVersionUID = -7153882239220338726L;
	
	private String checked;
	
	private Boolean open = false;

	public State(boolean checked) {
		super();
		this.checked = checked ? "checked" : "unchecked";
	}

	public State(boolean checked, boolean open) {
		this(checked);
		this.open = open;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
	
	public Boolean getOpen() {
		return open;
	}

	public void setOpen(Boolean open) {
		this.open = open;
	}

}
