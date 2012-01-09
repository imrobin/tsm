package com.justinmobile.tsm.cms2ac.engine;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service("transLockContainer")
public class TransLockContainer {

	private static final int LOCK_TIME_OUT = 100000;
	
	private Map<String, TransLock> transLockMap = new ConcurrentHashMap<String, TransLock>();

	public TransLock getTransLock(String providerSessionId) {
		return transLockMap.get(providerSessionId);
	}

	public void addTransLock(String providerSessionId, TransLock transLock) {
		this.transLockMap.put(providerSessionId, transLock);
	}

	public void waitTransLock(String providerSessionId) throws InterruptedException {
		TransLock transLock = getTransLock(providerSessionId);
		synchronized (transLock) {
			transLock.wait(LOCK_TIME_OUT);
		}
		System.out.println("waitTransLock:" + new Date());
	}

	public void releaseTransLock(String providerSessionId) {
		TransLock transLock = getTransLock(providerSessionId);
		synchronized (transLock) {
			transLock.notify();
		}
	}
}
