package com.justinmobile.tsm.application.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.annotations.Parameter;

import com.justinmobile.core.domain.AbstractEntity;
import com.justinmobile.core.domain.ResourcesFormat;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

@Entity
@Table(name = "APPLICATION_SERVICE")
public class ApplicationService extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4045608064766138936L;
	/** 主键 */
	private Long id;
	/** 类型：1为应用 2 为安全域 */
	@ResourcesFormat(key = "applicationService.type")
	private Integer type;
	/** 应用或安全域名称 */
	private String appName;
	/** 应用或安全域AID */
	private String aid;
	/** 业务接口名称 */
	private String serviceName;
	/** 所属SP */
	private SpBaseInfo sp;

	public enum BusinessPlatformInterface {
		/** 应用下载     */
		APP_DOWNLOAD("应用下载"),
		/** 应用删除    */
		APP_DELETE("应用删除"),
		/** 应用指令请求   */
		APP_COMMAND("应用指令"),
		/** 应用锁定*/
		APP_LOCK("应用锁定"),
		/** 应用解锁 */
		APP_UNLOCK("应用解锁"),
		/**获取 TOKEN*/
		GET_TOKEN("获取TOKEN"),
		/**安全域创建*/
		SD_CREATE("安全域创建"),
		/** 安全域删除    */
		SD_DELETE("安全域删除"),
		/** 安全域密钥更新  */
	    SD_KEY_UPDATE("安全域密钥更新"),
		/** 预处理 */
		PRE_OPERTION("预处理"),
		/** 结果通知 */
		RESULT_NOTIFY("结果通知"),
		/** 业务事件通知 */
		BUSINESS_EVENT_NOTITY("业务事件通知"),
		/** 未知 */
		UNKNOWN("未知");

		private String value;

		BusinessPlatformInterface(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public BusinessPlatformInterface getByValue(String value) {
			BusinessPlatformInterface[] businessPlatformInterfaces = BusinessPlatformInterface.values();
			for (BusinessPlatformInterface businessPlatformInterface : businessPlatformInterfaces) {
				if (businessPlatformInterface.getValue().equals(value)) {
					return businessPlatformInterface;
				}
			}
			return UNKNOWN;
		}
	}

	public static final Integer TYPE_APP = 1;

	public static final Integer TYPE_SD = 2;

	@Id
	@GeneratedValue(generator = "sequence")
	@GenericGenerator(name = "sequence", strategy = "sequence", parameters = { @Parameter(name = "sequence", value = "SEQ_APPLICATION_SERVICE") })
	public Long getId() {
		return id;
	}

	@ManyToOne
	@JoinColumn(name = "SP_ID")
	@Cascade({ CascadeType.PERSIST, CascadeType.MERGE, CascadeType.SAVE_UPDATE })
	@LazyToOne(LazyToOneOption.PROXY)
	public SpBaseInfo getSp() {
		return sp;
	}

	public void setSp(SpBaseInfo sp) {
		this.sp = sp;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
