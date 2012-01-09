package com.justinmobile.tsm.cms2ac.command;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;

/**
 * Install instruction<br />
 * cla: 0x80<br />
 * ins: 0xE6<br />
 * p1: defined by sub instruction<br />
 * p2: 0x00<br />
 * le: 0x00
 * 
 * @author JazGung
 */
abstract public class InstallCommand<T extends InstallCommand<T>> extends Command<T> {

	public static final byte VALUE_CLA = (byte) 0x80;
	public static final byte VALUE_INS = (byte) 0xE6;
	public static final byte VALUE_P2 = (byte) 0x00;
	public static final byte VALUE_LE = (byte) 0x00;

	public static enum SubInsType {
		INSTALL_FOR_INSTALL(InstallForInstallCommand.VALUE_P1) {
			@Override
			public InstallCommand<?> pares(byte[] command) {
				return InstallForInstallCommand.parse(command);
			}
		};

		private final byte p1;

		SubInsType(byte p1) {
			this.p1 = p1;
		}

		public static SubInsType valueOf(byte p1) {
			SubInsType[] sbuInsTypes = SubInsType.values();
			for (SubInsType subInsType : sbuInsTypes) {
				if (p1 == subInsType.p1) {
					return subInsType;
				}
			}
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}

		public abstract InstallCommand<?> pares(byte[] command);
	}

	protected InstallCommand(byte p1) {
		super(VALUE_CLA, VALUE_INS, p1, VALUE_P2, VALUE_LE);
	}

	public static InstallCommand<?> parse(byte[] command) {
		byte p1 = command[INDEX_P1];
		SubInsType subInsType = SubInsType.valueOf(p1);
		return subInsType.pares(command);
	}
}
