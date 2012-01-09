package com.justinmobile.tsm.endpoint.webservice;

public enum ListOrder {

	noOrder(0),
	appDownloadCount_desc(1),
	appNvm_desc(2),
	appName_asc(3),
	appName_desc(4),
	appIssuingDate_desc(5);
	
	private int type;
	
	public int getType() {
		return type;
	}

	ListOrder(int type) {
		this.type = type;
	}
	
	public static ListOrder typeOf(Integer type) {
		if (type == null) {
			return noOrder;
		}
		ListOrder[] ids = ListOrder.values();
		ListOrder result = null;
		for (ListOrder listOrder : ids) {
			if (listOrder.getType() == type) {
				result = listOrder;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("not exist type");
		}
		return result;
	}
}
