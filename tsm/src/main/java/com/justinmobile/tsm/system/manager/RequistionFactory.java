package com.justinmobile.tsm.system.manager;

import java.util.Calendar;

import com.justinmobile.tsm.system.domain.Requistion;

public class RequistionFactory {

	/**
	 * setType<br/>
	 * setStatus : STATUS_INIT<br/>
	 * setSubmitDate<br/>
	 * @param type
	 * @return
	 */
	public static Requistion getRequistion(Integer type) {
		Requistion apply = new Requistion();
		
		apply.setType(type);
		apply.setStatus(Requistion.STATUS_INIT);
		apply.setSubmitDate(Calendar.getInstance());
		
		return apply;
	}
	
	public static Requistion getPublishForSD() {
		Requistion apply = getRequistion(Requistion.TYPE_SD_PUBLISH);
		apply.setReason(Requistion.REASON_DEFAULT_SD_APPLY);
		return apply;
	}
	
	public static Requistion getRegisterForSP() {
		Requistion apply = new Requistion();
		
		apply.setType(Requistion.TYPE_SP_REGISTER);
		apply.setStatus(Requistion.STATUS_INIT);
		apply.setReason(Requistion.REASON_DEFAULT_SP_APPLY);
		apply.setSubmitDate(Calendar.getInstance());
		
		return apply;
	}
}
