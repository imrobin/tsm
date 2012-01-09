package com.justinmobile.tsm.endpoint.webservice.log.manager.impl;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.cms2ac.domain.ProviderProcess;
import com.justinmobile.tsm.endpoint.webservice.log.dao.ProviderProcessDao;
import com.justinmobile.tsm.endpoint.webservice.log.manager.ProviderProcessManager;

@Service("providerProcessManager")
public class ProviderProcessManagerImpl extends EntityManagerImpl<ProviderProcess, ProviderProcessDao> implements ProviderProcessManager {

	@Autowired
	private ProviderProcessDao providerProcessDao;

	@Override
	public ProviderProcess getBySessionIdThatNotVisited(String sessionId) {
		try {
			ProviderProcess providerProcess = null;

			int index = 0;
			Integer pollingInterval = null;
			if (null == pollingInterval) {// 如果没有配置的轮询间隔时间，使用100ms为默认值
				pollingInterval = 100;
			}

			do {// 轮询数据库获取业务平台放送的异步数据
				providerProcess = providerProcessDao.getBySessionIdThatNotVisited(sessionId);
				if (null == providerProcess) {// 如果本次轮询时没有收到业务平台放送的异步数据，休眠后继续轮询
					index++;
					try {
						Thread.sleep(pollingInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			} while (100 < index);

			return providerProcess;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
}
