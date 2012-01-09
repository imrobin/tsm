package com.justinmobile.core.dao;

import static org.apache.commons.lang.StringUtils.leftPad;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.utils.CalendarUtils;

@SuppressWarnings("rawtypes")
@Repository("oralceSequenceDao")
public class OracleSequenceDao extends EntityDaoHibernate {

	private static final int DEFAUTH_SIZE = 6;

	/**
	 * 直接得到sequence
	 * 
	 * @param seqName
	 * @return
	 * @throws PlatformException
	 */
	public String getNextSerialNo(String seqName) throws PlatformException {
		return getNextSerialNo(seqName, DEFAUTH_SIZE);
	}

	/**
	 * 得到sequence后，前加时间
	 * 
	 * @param seqName
	 * @return
	 * @throws PlatformException
	 */
	public String getNextSerialNoWithTime(String seqName) throws PlatformException {
		return CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmss") + getNextSerialNo(seqName, DEFAUTH_SIZE);
	}

	/**
	 * 
	 * @param seqName
	 *            sequence的名称
	 * @param size
	 *            要多长的字符串，前自动
	 * @return
	 * @throws PlatformException
	 */
	public String getNextSerialNo(String seqName, int size) throws PlatformException {
		try {
			if (StringUtils.isBlank(seqName)) {
				throw new PlatformException(PlatformErrorCode.PARAM_ERROR);
			}
			String seqNum = null;
			if (!existSequence(seqName)) {
				if (size > 28) {
					createSequence(seqName, 28);
				} else {
					createSequence(seqName, size);
				}
			}
			seqNum = getSequence(seqName);
			return leftPad(seqNum, size, "0");
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	public String getSequence(String seqName) throws HibernateException {
		SQLQuery query = getSession().createSQLQuery("select " + seqName + ".nextval from DUAL");
		String seqNum = String.valueOf(query.uniqueResult());
		return seqNum;
	}

	public void deleteSequence(String seqName) throws HibernateException {
		if (existSequence(seqName)) {
			SQLQuery query = getSession().createSQLQuery("DROP SEQUENCE " + seqName);
			query.executeUpdate();
		}
	}

	public boolean existSequence(String seqName) throws HibernateException {
		SQLQuery countQuery = getSession().createSQLQuery("SELECT COUNT(*) FROM USER_SEQUENCES T WHERE T.SEQUENCE_NAME = '" + seqName + "'");
		Object uniqueResult = countQuery.uniqueResult();
		Integer seq = Integer.parseInt(String.valueOf(uniqueResult));
		return !(seq == null || seq == 0);
	}

	public void createSequence(String seqName, int size) throws HibernateException {
		if (size > 28) {
			throw new HibernateException("sequence MAXVALUE length max is 28");
		}
		SQLQuery query = getSession().createSQLQuery(
				"CREATE SEQUENCE " + seqName + " INCREMENT BY 1 START WITH 1 MAXVALUE " + StringUtils.leftPad("", size, "9")
						+ " CYCLE NOCACHE");
		query.executeUpdate();
	}
}
