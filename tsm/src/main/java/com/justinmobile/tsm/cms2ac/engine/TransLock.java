package com.justinmobile.tsm.cms2ac.engine;

import com.justinmobile.tsm.transaction.domain.LocalTransaction;

public class TransLock {

	private int sessionStatus;
	
	private LocalTransaction localTransaction;

	public TransLock(int sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

	public int getSessionStatus() {
		return sessionStatus;
	}

	public LocalTransaction getLocalTransaction() {
		return localTransaction;
	}

	public void setLocalTransaction(LocalTransaction localTransaction) {
		this.localTransaction = localTransaction;
	}
}
