package com.justinmobile.tsm.cms2ac;

public interface Constants {

	static final int CMD_TYPE_PERSONAL = 0x00;

	static final int CMD_TYPE_OTHER = 0x01;
	
	static final int CMD_TYPE_APP_SD = 0x02;
	
	static final int CMD_TYPE_SD_APP = 0x03;

	static final int LOCK_FLAG_UNLOCK = 0x00;

	static final int LOCK_FLAG_LOCK = 0x01;

	static final int COMM_TYPE_SMS = 0x01;

	static final int COMM_TYPE_BIP = 0x02;

	static final int COMM_TYPE_CARD_DRIVER = 0x03;

	static final int COMM_TYPE_MOCAM = 0x04;

	static final int COMM_TYPE_UNKNOWN = 0x0F;

	static final String COTA_INTERFACE_TYPE_LOTA = "COTA_INTERFACE_TYPE_LOTA";

	static final String COTA_INTERFACE_TYPE_PROVDER = "COTA_INTERFACE_TYPE_PROVDER";

	static final String LOTA_INTERFACE_TYPE_COTA = "LOTA_INTERFACE_TYPE_COTA";

	static final String LOTA_INTERFACE_TYPE_PROVDER = "LOTA_INTERFACE_TYPE_PROVDER";

	static final String LOTA_INTERFACE_TYPE_OTA3 = "LOTA_INTERFACE_TYPE_OTA3";

	static final String LOTA_INTERFACE_TYPE_SMS = "LOTA_INTERFACE_TYPE_SMS";

	static final String LOTA_INTERFACE_TYPE_BIP = "LOTA_INTERFACE_TYPE_BIP";

	static final String LOTA_INTERFACE_TYPE_CARD_DRIVER = "LOTA_INTERFACE_TYPE_CARD_DRIVER";

	static final String CMS2AC_SCP_01 = "01";

	static final String CMS2AC_SCP_02 = "02";

	static final String CMS2AC_SCP_80 = "80";

	public static final int CMS2AC_DEFAULT_SCP02_I = 0x15;

	public static final int CMS2AC_DEFAULT_SCP80_I = 0x090A;

	public static final int CMS2AC_SEC_LEVEL_NO_SECURITY = 0x00;

	public static final int CMS2AC_SEC_LEVEL_C_MAC = 0x01;

	public static final int CMS2AC_SEC_LEVEL_C_DECRYPTION = 0x02;

	public static final int CMS2AC_SEC_LEVEL_R_MAC = 0x10;

	public static final int SCP80_KI_ALG_DES = 0x01;

	public static final int SCP80_KI_DES_CBC = 0x00;

	public static final int SCP80_KI_DES_3DES_2KEY = 0x01;

	public static final int SCP80_KI_DES_3DES_3KEY = 0x02;

	public static final int SCP80_KI_DES_ECB = 0x03;

	public static final int CARD_SD_KEY_VERSION = 0x01;

	public static final int BIP_DATA_MAX_LENGTH = 780;

	public static final int CARD_DRIVER_DATA_MAX_LENGTH = 5200;

	public static final int CARD_DRIVER_PERSO_LENGTH = 100;

	public static final int MOCAM_DATA_MAX_LENGTH = Integer.MAX_VALUE;

	public static final int MOCAM_PERSO_LENGTH = Integer.MAX_VALUE;

	public static final String CMS2AC_SP_ADN = "10658465";

	public static final String PRESET_SIGN = "FFFFFFFFFFFFFFFF";

	public static final int UNBIND_AID = 1;

	public static final int BIND_AID = 0;
	
	public static final String OTA3_ENGINEE_APP_AID = "D15600010180016000FFFFFFB0001000";
	
	public static final String ECARD_AID = "D1560001018000000000000100000000";
}
