package com.justinmobile.tsm.application.domain;

import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;
import com.justinmobile.core.utils.TlvObject;

public class SecurityDomainInstallParams {

	/** 安装参数字符串表现形式 */
	private String installParams;

	/** 安全通道协议 */
	private String securityChannel;

	/** 安全 通道协议选项 */
	private String securityChannelOption;

	/** 安全通道基本属性 */
	private int baseProp;

	/** 安全域是否接受迁移 */
	private int transfer;

	public static final int ACCEPT_TRANSFER = 1;

	public static final int NOT_ACCEPT_TRANSFER = 0;

	/** 是否接受主安全域发起的应用删除 */
	private int deleteApp;

	public static final int ACCEPT_DELETEAPP = 0;

	public static final int NOT_ACCEPT_DELETEAPP = 1;

	/** 是否允许从主安全域发起的应用安装 */
	private int installApp;

	/** 是否允许从其他安全域发起的应用下载 */
	private int downloadApp;

	/** 是否允许从主安全域发起的应用锁定或解锁 */
	private int lockedApp;

	public static final int ACCEPT = 0;

	public static final int NOT_ACCEPT = 1;

	/** 安全域是否允许删除 */
	private int deleteSelf;

	public static final int ACCPET_DELETESELF = 0;

	public static final int NOT_ACCEPT_DELETSELF = 1;

	/** 安全域支持的最大对称密钥个数 */
	private int maxKeyNumber;

	/** 密钥版本号 */
	private int keyVersion;

	/** 安全通道最大连续鉴权失败次数 */
	private int maxFailCount;

	/** 管理的可变空间 */
	private int managedVolatileSpace;

	/** 管理的非可变空间 */
	private long managedNoneVolatileSpace;

	/** 是否为固定空间 */
	private boolean isSpaceFixed;

	public String getSecurityChannel() {
		return securityChannel;
	}

	public void setSecurityChannel(String securityChannel) {
		this.securityChannel = securityChannel;
	}

	public String getSecurityChannelOption() {
		return securityChannelOption;
	}

	public void setSecurityChannelOption(String securityChannelOption) {
		this.securityChannelOption = securityChannelOption;
	}

	public int getBaseProp() {
		return baseProp;
	}

	public void setBaseProp(int baseProp) {
		this.baseProp = baseProp;
	}

	public int getManagedVolatileSpace() {
		return managedVolatileSpace;
	}

	public void setManagedVolatileSpace(int managedVolatileSpace) {
		this.managedVolatileSpace = managedVolatileSpace;
	}

	public long getManagedNoneVolatileSpace() {
		return managedNoneVolatileSpace;
	}

	public void setManagedNoneVolatileSpace(long managedNoneVolatileSpace) {
		this.managedNoneVolatileSpace = managedNoneVolatileSpace;
	}

	public int getMaxKeyNumber() {
		return maxKeyNumber;
	}

	public void setMaxKeyNumber(int maxKeyNumber) {
		this.maxKeyNumber = maxKeyNumber;
	}

	public int getKeyVersion() {
		return keyVersion;
	}

	public void setKeyVersion(int keyVersion) {
		this.keyVersion = keyVersion;
	}

	public int getMaxFailCount() {
		return maxFailCount;
	}

	public void setMaxFailCount(int maxFailCount) {
		this.maxFailCount = maxFailCount;
	}

	public void setInstallParams(String installParams) {
		this.installParams = installParams;
	}

	public String getInstallParams() {
		return installParams;
	}

	public int getTransfer() {
		return transfer;
	}

	public void setTransfer(int transfer) {
		this.transfer = transfer;
	}

	public int getDeleteApp() {
		return deleteApp;
	}

	public void setDeleteApp(int deleteApp) {
		this.deleteApp = deleteApp;
	}

	public int getDeleteSelf() {
		return deleteSelf;
	}

	public void setDeleteSelf(int deleteSelf) {
		this.deleteSelf = deleteSelf;
	}

	public int getInstallApp() {
		return installApp;
	}

	public void setInstallApp(int installApp) {
		this.installApp = installApp;
	}

	public int getDownloadApp() {
		return downloadApp;
	}

	public void setDownloadApp(int downloadApp) {
		this.downloadApp = downloadApp;
	}

	public int getLockedApp() {
		return lockedApp;
	}

	public void setLockedApp(int lockedApp) {
		this.lockedApp = lockedApp;
	}

	public String build() {
		TlvObject tag_45 = new TlvObject();
		TlvObject tag_46 = new TlvObject();
		TlvObject tag_47 = new TlvObject();
		TlvObject tag_48 = new TlvObject();
		TlvObject tag_49 = new TlvObject();
		TlvObject tag_4a = new TlvObject();
		TlvObject tlv = new TlvObject();
		String hexBaseProp = ConvertUtils.int2HexString(baseProp);
		tag_45.add("45", hexBaseProp);
		tlv.add("C9", tag_45);
		if (keyVersion != 0) {
			String hexKeyVersion = ConvertUtils.int2HexString(keyVersion);
			tag_46.add("46", hexKeyVersion);
			tlv.add("C9", tag_46);
		}
		if (!(securityChannel + securityChannelOption).equals("")) {
			tag_47.add("47", securityChannel + securityChannelOption);
			tlv.add("C9", tag_47);
		}

		if (maxFailCount != 0) {
			String hexMaxFailCount = ConvertUtils.int2HexString(maxFailCount);
			tag_48.add("48", hexMaxFailCount);
			tlv.add("C9", tag_48);
		}
		if (managedVolatileSpace != 0 && managedNoneVolatileSpace != 0) {
			String hexManagedVolatileSpace = ConvertUtils.int2HexString(managedVolatileSpace, 4);
			String hexManagedNoneVolatileSpace = ConvertUtils.long2HexString(managedNoneVolatileSpace, 8);
			tag_49.add("49", hexManagedVolatileSpace + hexManagedNoneVolatileSpace);
			tlv.add("C9", tag_49);
		}
		if (maxKeyNumber != 0) {
			String hexMaxKeyNumber = ConvertUtils.int2HexString(maxKeyNumber);
			tag_4a.add("4a", hexMaxKeyNumber);
			tlv.add("C9", tag_4a);
		}

		return tlv.build();
	}

	public Space getManagedSpace() {
		Space space = new Space();

		space.setNvm(managedNoneVolatileSpace);
		space.setRam(managedVolatileSpace);

		return space;
	}

	public static SecurityDomainInstallParams parse(String hexParams) {
		SecurityDomainInstallParams sdip = new SecurityDomainInstallParams();
		TlvObject source = TlvObject.parse(hexParams);
		TlvObject c9 = TlvObject.parse(source.getByTag("c9"));

		int tag_45 = ConvertUtils.byteArray2Int(c9.getByTag("45"));
		String baseProp = ByteUtils.intToBinaryString(tag_45, 8);
		sdip.setTransfer(new Integer(baseProp.substring(7)));
		sdip.setDeleteApp(new Integer(baseProp.substring(5, 6)));
		sdip.setDeleteSelf(new Integer(baseProp.substring(6, 7)));
		sdip.setBaseProp(ConvertUtils.byteArray2Int(c9.getByTag("45")));
		if (c9.getByTag("46").length == 0) {
			sdip.setKeyVersion(115);
		} else {
			int tag_46 = ConvertUtils.byteArray2Int(c9.getByTag("46"));
			sdip.setKeyVersion(tag_46);
		}
		if (c9.getByTag("47").length == 0) {
			sdip.setSecurityChannel("02");
			sdip.setSecurityChannelOption("15");
		} else {
			String tag_47 = ConvertUtils.byteArray2HexString(c9.getByTag("47"));
			sdip.setSecurityChannel(tag_47.substring(0, 2));
			sdip.setSecurityChannelOption(tag_47.substring(2, 4));
		}
		if (c9.getByTag("48").length == 0) {
			sdip.setMaxFailCount(255);
		} else {
			int tag_48 = ConvertUtils.byteArray2Int(c9.getByTag("48"));
			sdip.setMaxFailCount(tag_48);
		}
		if (c9.getByTag("49").length == 0) {
			sdip.setSpaceFixed(false);
			sdip.setManagedNoneVolatileSpace(0);
			sdip.setManagedVolatileSpace(0);
		} else {
			sdip.setSpaceFixed(true);
			String tag_49 = ConvertUtils.byteArray2HexString(c9.getByTag("49"));
			sdip.setManagedVolatileSpace(ConvertUtils.hexString2Int(tag_49.substring(0, 4)));
			sdip.setManagedNoneVolatileSpace(ConvertUtils.hexString2Int(tag_49.substring(4, 12)));
		}
		if (c9.getByTag("4a").length == 0) {
			sdip.setMaxKeyNumber(16);
		} else {
			int tag_4a = ConvertUtils.byteArray2Int(c9.getByTag("4a"));
			sdip.setMaxKeyNumber(tag_4a);
		}

		return sdip;
	}

	public boolean isExtraditable() {
		return ACCEPT_TRANSFER == transfer;
	}

	public static void main(String[] args) throws Exception {
		String installParams = "C90D450107470202154801FF4A0110";
		SecurityDomainInstallParams sdip = parse(installParams);
		System.out.println(sdip.getKeyVersion());

	}

	public boolean isSpaceFixed() {
		return isSpaceFixed;
	}

	public void setSpaceFixed(boolean isSpaceFixed) {
		this.isSpaceFixed = isSpaceFixed;
	}
}
