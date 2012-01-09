package com.justinmobile.tsm.cms2ac.exception;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

public class ApduResponseException extends PlatformException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3138068104927325755L;
	
	private String apduName;
	
	private String errorCode;

	public ApduResponseException(String apduName, String errorCode) {
		super(PlatformErrorCode.INVALID_APDU_RESP_COMMAND);
		this.apduName = apduName;
		this.errorCode = errorCode;
	}

	@Override
	public String getMessage() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.getMessage()).append("[");
		buf.append("ApduException.").append(this.apduName).append(".").append(this.errorCode);
		buf.append("]");
		return buf.toString();
	}
}
