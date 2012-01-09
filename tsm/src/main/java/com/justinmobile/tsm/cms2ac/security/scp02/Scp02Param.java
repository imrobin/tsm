package com.justinmobile.tsm.cms2ac.security.scp02;

public class Scp02Param {

	public static final int SCP02_I_14 = 0x14;

	public static final int SCP02_I_15 = 0x15;

	public static final int NO_SECURITY = 0x00;

	public static final int C_MAC = 0x01;

	public static final int C_DECRYPTION = 0x02;

	public static final int R_MAC = 0x10;

	private int securityLevel;
	
	private int indication;

	public Scp02Param(int indication, int securityLevel) {
		this.indication = indication;
		this.securityLevel = securityLevel;
	}

	public Scp02Param(int indication, boolean cMac, boolean cDecryption, boolean rMac) {
		this.indication = indication;		
		this.securityLevel = NO_SECURITY;
		if(cMac) {
			this.securityLevel = this.securityLevel|C_MAC;
		}
		if(cDecryption) {
			this.securityLevel = this.securityLevel|C_DECRYPTION;
		}
		if(rMac) {
			this.securityLevel = this.securityLevel|R_MAC;
		}
	}

	public int getIndication() {
		return indication;
	}

	public void setIndication(int indication) {
		this.indication = indication;
	}
	
	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isCMac() {
		return (securityLevel & C_MAC) == C_MAC;
	}
	
	public boolean isCDecryption() {
		return (securityLevel & C_DECRYPTION) == C_DECRYPTION;
	}
	
	public boolean isRMac() {
		return (securityLevel & R_MAC) == R_MAC;
	}
	
	public boolean isNoSecurity() {
		return securityLevel == NO_SECURITY;
	}
}
