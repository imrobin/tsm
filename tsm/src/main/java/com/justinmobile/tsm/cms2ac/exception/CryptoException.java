package com.justinmobile.tsm.cms2ac.exception;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

public class CryptoException extends PlatformException {

	private static final long serialVersionUID = -4245512259207811325L;
	
	private String message;

	public CryptoException(String message) {
		super(PlatformErrorCode.CRYPTO_FAILURE);
	}

	@Override
	public String getMessage() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.getMessage()).append("[");
		buf.append("ApduException.").append(this.message);
		buf.append("]");
		return buf.toString();
	}
	
	
}
