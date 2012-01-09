package com.justinmobile.tsm.application.domain;

import org.apache.commons.lang.StringUtils;

/**
 * 非持久化领域对象<br/>
 * 表示实例和安全域的权限<br/>
 * 包括权限字段的组装和解析功能<br/>
 * 
 * @author JazGung
 * 
 */
public class Privilege {

	public static final Integer SD = 0x80;
	public static final Integer DAP = 0xC0;
	public static final Integer DAP_FORCE = 0xC1;
	public static final Integer TOKEN = 0xA0;
	public static final Integer LOCK_CARD = 0x10;
	public static final Integer ABANDON_CARD = 0x08;
	public static final Integer DEFAULT_SELETE = 0x04;
	public static final Integer CVM = 0x02;

	private static final Integer DAP_MARK = 0xC0;
	private static final Integer DAP_TYPE_MARK = 0x01;

	/** 是否是安全域 */
	private boolean sd = false;

	/** 是否具有验证DAP能力，必须是安全域 */
	private boolean dap = false;

	/** 是否具有强制验证DAP的能力，必须是安全域 */
	private boolean dapForce = false;

	/** 是否具有委托管理能力，必须是安全域 */
	private boolean token = false;

	/** 是否具有锁卡权限 */
	private boolean lockCard = false;

	/** 是否具有废卡权限 */
	private boolean abandonCard = false;

	/** 是否具有缺省权限 */
	private boolean defaultSelect = false;

	/** 是否具有CVM管理权限 */
	private boolean cvm = false;

	public boolean isSd() {
		return sd;
	}

	public void setSd(boolean sd) {
		this.sd = sd;
	}

	public boolean isDap() {
		return dap;
	}

	public void setDap(boolean dap) {
		this.dap = dap;
	}

	public boolean isDapForce() {
		return dapForce;
	}

	public void setDapForce(boolean dapForce) {
		this.dapForce = dapForce;
	}

	public boolean isToken() {
		return token;
	}

	public void setToken(boolean token) {
		this.token = token;
	}

	public boolean isLockCard() {
		return lockCard;
	}

	public void setLockCard(boolean lockCard) {
		this.lockCard = lockCard;
	}

	public boolean isAbandonCard() {
		return abandonCard;
	}

	public void setAbandonCard(boolean abandonCard) {
		this.abandonCard = abandonCard;
	}

	public boolean isDefaultSelect() {
		return defaultSelect;
	}

	public void setDefaultSelect(boolean defaultSelect) {
		this.defaultSelect = defaultSelect;
	}

	public boolean isCvm() {
		return cvm;
	}

	public void setCvm(boolean cvm) {
		this.cvm = cvm;
	}

	/**
	 * 组建权限字段<br/>
	 * 如果是安全域，只有一个安全域权限字段会生效，优先级是 委托管理安全域->DAP安全域->强制安全域->普通安全域
	 * 
	 * @return 权限，用int表示
	 */
	public Integer biuld() {
		int privilege = 0;

		// 处理安全域能力
		if (sd) {
			if (token) {
				privilege = privilege | TOKEN;
			} else if (dap) {
				privilege = privilege | DAP;
			} else if (dapForce) {
				privilege = privilege | DAP_FORCE;
			} else {
				privilege = privilege | SD;
			}
		}

		// 处理其他能力
		if (lockCard) {
			privilege = privilege | LOCK_CARD;
		}

		if (abandonCard) {
			privilege = privilege | ABANDON_CARD;
		}

		if (defaultSelect) {
			privilege = privilege | DEFAULT_SELETE;
		}

		if (cvm) {
			privilege = privilege | CVM;
		}

		return privilege;
	}

	/**
	 * 解析int表示的权限
	 * 
	 * @param privilegeInt
	 *            int表示的权限
	 * @return 对象
	 */
	public static Privilege parse(final int privilegeInt) {
		Privilege p = new Privilege();

		if (SD == (privilegeInt & SD)) {// 判断是否是安全域
			p.sd = true;

			if (TOKEN == (privilegeInt & TOKEN)) {// 判断是否是委托管理安全域
				p.token = true;
			} else if (DAP_MARK == (privilegeInt & DAP_MARK)) {// 判断是否是DAP安全域或强制DAP安全域
				if (DAP_TYPE_MARK == (privilegeInt & DAP_TYPE_MARK)) {// 根据最低bit判定是DAP安全域还是强制DAP安全域
					p.dapForce = true;// 最低bit为1，强制DAP安全域
				} else {
					p.dap = true;// 最低bit为0，DAP安全域
				}
			}
		}

		if (LOCK_CARD == (privilegeInt & LOCK_CARD)) {
			p.lockCard = true;
		}

		if (ABANDON_CARD == (privilegeInt & ABANDON_CARD)) {
			p.abandonCard = true;
		}

		if (DEFAULT_SELETE == (privilegeInt & DEFAULT_SELETE)) {
			p.defaultSelect = true;
		}

		if (CVM == (privilegeInt & CVM)) {
			p.cvm = true;
		}

		return p; 

	}
	
	public int getModel() {
		if(!this.dap && !this.dapForce && !this.token) {
			return SecurityDomain.MODEL_COMMON;
		} else if(this.dap) {
			return SecurityDomain.MODEL_DAP;
		} else if(this.dapForce) {
			return SecurityDomain.MODEL_DAP;
		} else if(this.token) {
			return SecurityDomain.MODEL_TOKEN;
		}
		
		return SecurityDomain.MODEL_COMMON;
	}
	
	public String translateToZH() {
		String chinese = "";
		
		if(this.dap) {
			chinese += ",DAP验证";
		} 
		if(this.dapForce) {
			chinese += ",强制要求验证DAP";
		} 
		if(this.token) {
			chinese += ",委托管理";
		} 
		if(this.lockCard) {
			chinese += ",锁定卡";
		} 
		if(this.abandonCard) {
			chinese += ",废止卡";
		} 
		if(this.defaultSelect) {
			chinese += ",缺省应用";
		} 
		if(this.cvm) {
			chinese += ",管理卡CVM";
		}
		
		if(!StringUtils.isBlank(chinese)) {
			chinese = chinese.replaceFirst(",", "");
		}
		
		return chinese;
	}
}
