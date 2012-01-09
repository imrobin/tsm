package com.justinmobile.tsm.cms2ac.command;

import static com.justinmobile.core.utils.ConvertUtils.int2Byte;

import java.util.ArrayList;
import java.util.List;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;

/**
 * Install for Install指令<br />
 * p1: 0x04
 * 
 * @author JazGung
 */
public class InstallForInstallCommand extends InstallCommand<InstallForInstallCommand> {

	public static final byte VALUE_P1 = (byte) 0x04;

	public static enum InsType {
		INSTALL_FOR_INSTALL(InstallForInstallCommand.VALUE_P1);

		byte p1;

		InsType(byte p1) {
			this.p1 = p1;
		}

		private byte getP1() {
			return p1;
		}

		public static InsType getByIns(byte p1) {
			InsType[] insTypes = InsType.values();
			for (InsType insType : insTypes) {
				if (p1 == insType.getP1()) {
					return insType;
				}
			}

			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}
	}

	/**
	 * load file的aid<br />
	 * CMS2AC中的load file与JavaCard中的package同义<br />
	 */
	byte[] loadFileAid;

	/**
	 * model的aid<br />
	 * CMS2AC中的model与JavaCard中的applet同义<br />
	 * 在JavaCard中applet是继承于javacard.framework.Applet的类<br />
	 */
	byte[] modelAid;

	/**
	 * applet的aid<br />
	 * CMS2AC中的applet与JavaCard中的instance同义<br />
	 */
	byte[] appletAid;

	/**
	 * 应用的权限<br />
	 * 参考CMS2AC规范第13章<br />
	 */
	byte authority = (byte) 0x00;

	/**
	 * 安装参数<br />
	 * 参考CMS2AC规范第13.6.2.4.6节<br />
	 */
	byte[] param;

	/**
	 * token
	 */
	byte[] token = new byte[0];

	public InstallForInstallCommand() {
		super((byte) 0x04);
	}

	public byte[] getLoadFileAid() {
		return loadFileAid;
	}

	public void setLoadFileAid(byte[] loadFileAid) {
		this.loadFileAid = loadFileAid;
	}

	public byte[] getModelAid() {
		return modelAid;
	}

	public void setModelAid(byte[] modelAid) {
		this.modelAid = modelAid;
	}

	public byte[] getAppletAid() {
		return appletAid;
	}

	public void setAppletAid(byte[] appletAid) {
		this.appletAid = appletAid;
	}

	public byte getAuthority() {
		return authority;
	}

	public void setAuthority(byte authority) {
		this.authority = authority;
	}

	public byte[] getParam() {
		return param;
	}

	public void setParam(byte[] param) {
		this.param = param;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	@Override
	public byte[] buildData() {
		List<byte[]> dataParts = new ArrayList<byte[]>();

		dataParts.add(ByteUtils.prepend(int2Byte(loadFileAid.length), loadFileAid));
		dataParts.add(ByteUtils.prepend(int2Byte(modelAid.length), modelAid));
		dataParts.add(ByteUtils.prepend(int2Byte(appletAid.length), appletAid));
		dataParts.add(new byte[] { (byte) 0x01, (byte) authority });
		dataParts.add(ByteUtils.prepend(int2Byte(param.length), param));
		dataParts.add(ByteUtils.prepend(int2Byte(token.length), token));

		return buildData(dataParts);
	}

	@Override
	public boolean validateDate() {
		// TODO Auto-generated method stub
		return false;
	};

	public static InstallForInstallCommand parse(byte[] commandByteArray) {
		InstallForInstallCommand command = new InstallForInstallCommand();
		int pos = INDEX_LC + 1;
		int length = 0;

		try {
			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte[] loadFileAid = ByteUtils.subArray(commandByteArray, pos, pos + length);
			command.setLoadFileAid(loadFileAid);

			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte[] modelAid = ByteUtils.subArray(commandByteArray, pos, pos + length);
			command.setModelAid(modelAid);

			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte[] appletAid = ByteUtils.subArray(commandByteArray, pos, pos + length);
			command.setAppletAid(appletAid);

			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte authority = ByteUtils.subArray(commandByteArray, pos, pos + length)[0];
			command.setAuthority(authority);

			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte[] param = ByteUtils.subArray(commandByteArray, pos, pos + length);
			command.setParam(param);

			length = ConvertUtils.byte2Int(commandByteArray[pos]);
			byte[] token = ByteUtils.subArray(commandByteArray, pos, pos + length);
			command.setToken(token);

			return command;
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

	}
}
