package com.justinmobile.tsm.cms2ac.command;

import java.nio.ByteBuffer;
import java.util.List;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.ByteUtils;
import com.justinmobile.core.utils.ConvertUtils;

/**
 * <p>
 * 这个类定义了符合CMS2AC规范的APDU指令的格式
 * </p>
 * <p>
 * APDU的格式为<br />
 * cla|ins|p1|p2|lc(|data)(|le)<br />
 * </p>
 * <p>
 * 所有指令使用IOS/IEC 7816-4中的短消息格式，即lc字段的长度为1 byte
 * </p>
 * <p>
 * 在CMS2AC规范中，
 * <ul>
 * <li>所有指令使用IOS/IEC 7816-4中的短消息格式，即lc字段的长度为1 byte</li>
 * <li>所有指令使用长度（包括报文头）不超过255 byte</li>
 * </ul>
 * </p>
 * <p>
 * 所有继承这个类的子类对应具体的APDU指令，定义了各个字段的取值
 * </p>
 * 
 * @author JazGung
 */
abstract public class Command<T extends Command<T>> {
	public static int INDEX_CLA = 0;
	public static int INDEX_INS = 1;
	public static int INDEX_P1 = 2;
	public static int INDEX_P2 = 3;
	public static int INDEX_LC = 4;

	public static int LENGTH_COMMAND_MAX = 255;
	public static int LNEGTH_COMMADN_MIN = 5;

	/**
	 * class，指令的类型， 1 byte<br />
	 * <br />
	 * b<sub>8</sub>=0，ISO/IEC 7816-4中定义的指令<br />
	 * b<sub>8</sub>=1，CMS2AC中定义的指令<br />
	 * <br />
	 * b<sub>4</sub>=0，普通报文<br />
	 * b<sub>4</sub>=1，安全报文<br />
	 */
	private byte cla;

	/**
	 * instruction，指令，这个字段的取值决定了具体的APDU指令<br />
	 */
	private byte ins;

	/**
	 * parameter1<br />
	 * 取值具体由指令决定<br />
	 */
	private byte p1;

	/**
	 * parameter2<br />
	 * 取值具体由指令决定<br />
	 */
	private byte p2;

	/**
	 * 数据域的长度<br />
	 */
	private int lc = 0;

	/**
	 * 数据域
	 */
	private byte[] data = null;

	/**
	 * 响应数据域的最大长度<br />
	 * 如果le=null，响应不允许存在数据域<br />
	 * 如果0&ltle&lt=255，响应数据域最大长度是le <br />
	 * 如果le=0，响应数据域最大长度是le<br />
	 */
	private Byte le = null;

	public static enum InsType {
		INSTALL(InstallCommand.VALUE_INS) {
			@Override
			public Command<?> parse(byte[] command) {
				return InstallCommand.parse(command);
			}
		};

		byte ins;

		InsType(byte ins) {
			this.ins = ins;
		}

		public byte getIns() {
			return ins;
		}

		public abstract Command<?> parse(byte[] command);

		public static InsType dispatch(byte ins) {
			InsType[] insTypes = InsType.values();
			for (InsType insType : insTypes) {
				if (ins == insType.getIns()) {
					return insType;
				}
			}

			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}
	}

	protected Command(byte cla, byte ins, byte p1, byte p2, Byte le) {
		super();
		this.cla = cla;
		this.ins = ins;
		this.p1 = p1;
		this.p2 = p2;
		this.le = le;
	}

	public byte getCla() {
		return cla;
	}

	public byte getIns() {
		return ins;
	}

	public byte getP1() {
		return p1;
	}

	public byte getP2() {
		return p2;
	}

	public int getLc() {
		return lc;
	}

	protected void setData(byte[] data) {
		this.lc = data.length;
		this.data = data;

	}

	public byte[] getData() {
		// 如果data字段还没有组装，调用buildData()方法
		if (null == this.data) {
			setData(buildData());
		}
		return data;
	}

	public Byte getLe() {
		return le;
	}

	/**
	 * 将命令向下转型为指令
	 * 
	 * @return command with correct instruction type
	 */
	@SuppressWarnings("unchecked")
	public T downcast() {
		return (T) this;
	}

	/**
	 * 组装非安全报文
	 * 
	 * @return 非安全报文，byte[]表示
	 */
	public byte[] buildNativeCommand() {

		return buildCommand(cla, getData());
	}

	/**
	 * 组装安全报文
	 * 
	 * @return 安全报文，byte[]表示
	 */
	public byte[] buildSecuredCommand(byte[] securedData) {
		byte cla = (byte) (this.cla | (byte) 0x04);

		return buildCommand(cla, securedData);
	}

	private byte[] buildCommand(byte cla, byte[] data) {
		byte[] header = { cla, ins, p1, p2 };

		data = ByteUtils.prepend((byte) data.length, data);

		if (null != le) {
			data = ByteUtils.append(data, le);
		}

		return ByteUtils.contactArray(header, data);
	}

	/**
	 * 组装命令的数据域
	 * 
	 * @return 数据域，用byte[]表示
	 */
	abstract public byte[] buildData();

	/**
	 * 将List中的byte[]组装为一个byte[]<br />
	 * List中各个byte[]在组装后的byte[]中的顺序与其在List中的次序一致
	 * 
	 * @param parts
	 *            待组装的byte[]
	 * @return 组装后的byte[]
	 */
	protected byte[] buildData(List<byte[]> parts) {
		ByteBuffer bb = ByteBuffer.allocate(LENGTH_COMMAND_MAX);

		for (byte[] part : parts) {
			bb.put(part);
		}

		int pos = bb.position();
		return ByteUtils.subArray(bb.array(), 0, pos);
	}

	/**
	 * 验证数据域的各个段是否符合规范
	 * 
	 * @return 验证结果<br />
	 *         true：符合规范<br />
	 *         false：不符合规范<br />
	 */
	abstract public boolean validateDate();

	public static Command<?> parse(byte[] command) {
		validateCommandByteArray(command);
		byte ins = command[INDEX_INS];
		InsType insType = InsType.dispatch(ins);
		return insType.parse(command);
	}

	private static void validateCommandByteArray(byte[] command) {
		int lc = ConvertUtils.byte2Int(command[INDEX_LC]);
		int expectCommondLength = lc + 5;
		int actualCommondLength = command.length;
		if ((actualCommondLength != expectCommondLength) || (actualCommondLength != (expectCommondLength + 1))) {
			throw new PlatformException(PlatformErrorCode.DEFAULT);
		}
	}
}
