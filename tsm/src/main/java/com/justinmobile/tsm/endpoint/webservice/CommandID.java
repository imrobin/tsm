package com.justinmobile.tsm.endpoint.webservice;

/**
 *  100001，用户登录
	100002，用户注册
	100003，用户注销
	100004，卡空间信息
	100005，应用列表
	100006，安全域列表
	100007，应用详情
    100008，新IMSI通知
	100101，业务订购
	100102，业务退订
	100103，创建安全域
	100104，删除安全域
	100105，更新安全域的密钥
	100106，锁定应用
	100107，解锁应用
	100108，更新应用
	100109，锁定安全域
	100110，解锁安全域
	100111，业务迁出
	100112，业务迁入
	100113，PUSH更新
    100114，同步卡片安全域
    100115，个人化数据管理
    100116，重写手机号
	100201，升级客户端
	100202，升级UE
	100203，客户端详情
	100301，业务评论
	100302，浏览评论
 */
public enum CommandID {
	
	/** 100001，用户登录 */
	UserLogin("100001"),

	/** 100002，用户注册 */
	UserRegist("100002"),

	/** 100003，用户注销 */
	UserCancel("100003"),
	
	/** 100004，卡空间信息*/
	CardSpaceInfo("100004"),
	
	/** 100005，应用列表 */
	AppList("100005"),
	
	/** 100006，安全域列表 */
	SdList("100006"),
	
	/** 100007，应用详情 */
	AppInfo("100007"),
	
	/**100008，更新Token*/
	ChangeToken("100008"),
	
	/** 100101，业务订购 */
	BusinessOrder("100101"),
	
	/** 100102，业务退订 */
	BusinessCancelOrder("100102"),
	
	/** 100103，创建安全域 */
	SdCreate("100103"),
	
	/** 100104，删除安全域 */
	SdDelete("100104"),
	
	/** 100105，更新安全域的密钥 */
	SdKeyUpdate("100105"),
	
	/** 100106，锁定应用 */
	AppLock("100106"),
	
	/** 100107，解锁应用 */
	AppUnlock("100107"),
	
	/** 100108，更新应用 */
	AppUpdate("100108"),
	
	/** 100109，锁定安全域 */
	SdLock("100109"),
	
	/** 100110，解锁安全域 */
	SdUnlock("100110"),
	
	/** 100111，业务迁出 */
	BusinessEmigrate("100111"),
	
	/** 100112，业务迁入 */
	BusinessImmigrate("100112"),
	
	/** 100113，PUSH更新 */
	PushUpdate("100113"),

	/** 100114，同步卡片安全域 */
	SynCardSd("100114"),

	/** 100115，应用个人化更新 */
	AppPersonal("100115"),
	
	/** 100116，重写手机号 */
	ImsiRewrite("100116"),

	/** 100201，升级客户端 */
	AppClientUpgrade("100201"),
	
	/** 100202，升级UE */
	UeUprage("100202"),
	
	/** 100203，客户端详情 */
	AppClientInfo("100203"),
	
	/** 100301，业务评论 */
	CommentPost("100301"),
	
	/** 100302，浏览评论 */
	CommentView("100302"),
	
	/** 100401， 评论顶踩*/
	CommentUpDown("100401"),
	;
	
	private String  code;
	
	public String getCode() {
		return code;
	}

	CommandID(String code) {
		this.code = code;
	}
	
	public boolean equals(String code) {
		return this.code == code;
	}
	
	public static CommandID codeOf(String code) {
		CommandID[] ids = CommandID.values();
		CommandID result = null;
		for (CommandID commandID : ids) {
			if (commandID.getCode().equals(code)) {
				result = commandID;
			}
		}
		if (result == null) {
			throw new IllegalArgumentException("not exist commandid");
		}
		return result;
	}
}
