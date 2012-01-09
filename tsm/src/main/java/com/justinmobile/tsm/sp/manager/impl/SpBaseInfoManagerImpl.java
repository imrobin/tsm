package com.justinmobile.tsm.sp.manager.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.security.dao.SysUserDao;
import com.justinmobile.security.domain.SysUser;
import com.justinmobile.security.domain.SysRole.SpecialRoleType;
import com.justinmobile.security.manager.SysUserManager;
import com.justinmobile.security.utils.SpringSecurityUtils;
import com.justinmobile.tsm.sp.dao.SpBaseInfoApplyDao;
import com.justinmobile.tsm.sp.dao.SpBaseInfoDao;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;
import com.justinmobile.tsm.sp.domain.SpBaseInfoApply;
import com.justinmobile.tsm.sp.manager.SpBaseInfoManager;
import com.justinmobile.tsm.system.dao.RequistionDao;
import com.justinmobile.tsm.system.domain.Requistion;
import com.justinmobile.tsm.system.manager.RequistionFactory;
import com.justinmobile.tsm.system.manager.RequistionManager;

@Service("spBaseInfoManager")
public class SpBaseInfoManagerImpl extends EntityManagerImpl<SpBaseInfo, SpBaseInfoDao> implements SpBaseInfoManager {

	@Autowired
	private SpBaseInfoDao spBaseInfoDao;

	@Autowired
	private SpBaseInfoApplyDao spBaseInfoApplyDao;
	
	@Autowired
	private SysUserDao sysUserDao;
	
	@Autowired
	private SysUserManager sysUserManager;
	
	@Autowired
	private RequistionDao requistionDao;
	
	@Autowired
	private RequistionManager requistionManager;
	
	@Autowired
	private SysUserManager userManager;
	
	@Override
	public String generateNumber(String prefix) {
		String number = spBaseInfoDao.generateServiceProviderNumber().toString();
		number = StringUtils.leftPad(number, 7, '0');
		return prefix + number;
	}
	
	@Override
	public SpBaseInfo getSpByNameOrMobileOrEmail(String proof) {
		SysUser user = sysUserDao.getUserByNameOrMobileOrEmail(proof);

		if (null == user) {
			return null;
		}

		return spBaseInfoDao.load(user.getId());
	}

	@Override
	public boolean validateSpFullName(String name) throws PlatformException {
		return spBaseInfoDao.isPropertyUnique("name", name, null);
	}

	@Override
	public boolean validateSpShortName(String name) throws PlatformException {
		return spBaseInfoDao.isPropertyUnique("shortName", name, null);
	}
	
	@Override
	public boolean validateSpProperty(String property, String value) throws PlatformException {
		return spBaseInfoDao.isPropertyUnique(property, value, null);
	}
	
	@Override
	public boolean validateSpProperty(String property, String newValue, String orgValue) throws PlatformException {
		boolean bln = false;
		try {
			boolean formal = spBaseInfoDao.isPropertyUnique(property, newValue, orgValue);
			boolean temp = spBaseInfoApplyDao.isPropertyUniqueForAvaliable(property, newValue, orgValue);
			bln = formal && temp;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return bln;
	}
	
	@Override
	public void validateSpAvaliable(SpBaseInfo sp) {
		if (null == sp) {
			throw new PlatformException(PlatformErrorCode.SP_NOT_EXIST);
		} else if (SpBaseInfo.STATUS_AVALIABLE != sp.getStatus()) {
			throw new PlatformException(PlatformErrorCode.SP_UNAVALIABLE);
		}

	}

	@Override
	public Boolean register(SpBaseInfo spBaseInfo) throws PlatformException {
		Boolean bln = false;
		try {
			final String prefix = "SP";
			final String spNo = generateNumber(prefix);
			SysUser sysUser = spBaseInfo.getSysUser();
			sysUser.setUserName(spNo);
			sysUserManager.addUser(sysUser, SpecialRoleType.SERVICE_PROVIDER);
			spBaseInfo.setSysUser(sysUser);
			spBaseInfo.setNo(spNo);
			spBaseInfo.setStatus(SpBaseInfo.STATUS_INIT);
			spBaseInfo.setInBlack(SpBaseInfo.NOT_INBLACK);
			spBaseInfo.setHasLock(SpBaseInfo.LOCK);
			spBaseInfoDao.saveOrUpdate(spBaseInfo);
			
			SpBaseInfoApply apply = new SpBaseInfoApply();
			BeanUtils.copyProperties(spBaseInfo, apply, new String[]{"id"});
			apply.setApplyDate(Calendar.getInstance());
			apply.setApplyType(SpBaseInfoApply.APPLY_TYPE_REGISTER);
			apply.setEmail(sysUser.getEmail());
			
			//创建SP注册申请
			Requistion requistion = RequistionFactory.getRegisterForSP();
			requistion.setOriginalId(spBaseInfo.getId());
			requistionDao.saveOrUpdate(requistion);

			apply.setRequistion(requistion);
			spBaseInfoApplyDao.saveOrUpdate(apply);
			
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		return bln;
	}
	
	@Override
	public boolean modifyApply(SpBaseInfoApply apply) throws PlatformException {
		boolean bln = false;
		
		try {
			Requistion requistion = apply.getRequistion();
			requistionDao.saveOrUpdate(requistion);
			spBaseInfoApplyDao.saveOrUpdate(apply);
			
			SpBaseInfo sp = spBaseInfoDao.load(requistion.getOriginalId());
			sp.setHasLock(SpBaseInfo.LOCK);
			//sp.getSysUser().setProvince(apply.getLocationNo());
			spBaseInfoDao.saveOrUpdate(sp);
			bln = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}
	
	@Override
	public boolean modifyApply(SpBaseInfoApply apply, SpBaseInfo sp) throws PlatformException {
		boolean bln = false;
		
		try {

			//SP不可用状态，即待审核状态下的修改
			//现在改成都要处理完毕后才可以修改
			sp.setHasLock(SpBaseInfo.LOCK);
			
			//SP原表可以直接改
			/*
			sp.setAddress(apply.getAddress());
			sp.setCertificateNo(apply.getCertificateNo());
			sp.setContactPersonMobileNo(apply.getContactPersonMobileNo());
			sp.setContactPersonName(apply.getContactPersonName());
			if(ArrayUtils.isNotEmpty(apply.getFirmLogo())) {
				sp.setFirmLogo(apply.getFirmLogo());
			}
			sp.setFirmNature(apply.getFirmNature());
			sp.setFirmScale(apply.getFirmScale());
			sp.setLegalPersonIdNo(apply.getLegalPersonIdNo());
			sp.setLegalPersonIdType(apply.getLegalPersonIdType());
			sp.setLegalPersonName(apply.getLegalPersonName());
			sp.setLocationNo(apply.getLocationNo());
			sp.setName(apply.getName());
			sp.setRegistrationNo(apply.getRegistrationNo());
			sp.setShortName(apply.getShortName());
			sp.setType(apply.getType());
			sp.setRid(apply.getRid());
			 */
			
			//申请记录的处理
			//若该申请已经处理过了，则生成一条新的申请
			//若还未处理，则直接修改该申请的数据
			Requistion requistion = requistionManager.getRequistionByTypeAndId(Requistion.TYPE_SP_REGISTER, sp.getId());
			if(requistion.getReviewDate() != null) {
				//new a requistion
				Requistion _requistion = RequistionFactory.getRegisterForSP();
				_requistion.setOriginalId(sp.getId());
				requistionDao.saveOrUpdate(_requistion);
				
				SpBaseInfoApply _apply = new SpBaseInfoApply();
				BeanUtils.copyProperties(apply, _apply, new String[]{"id"});
				_apply.setNo(sp.getNo());
				//_apply.setName(sp.getName());
				_apply.setRequistion(_requistion);
				_apply.setApplyDate(Calendar.getInstance());
				_apply.setApplyType(Requistion.REASON_DEFAULT_SP_APPLY);
				_apply.setStatus(SpBaseInfo.STATUS_INIT);
				spBaseInfoApplyDao.saveOrUpdate(_apply);
			} else {
				//update a requistion
				Long id = requistion.getId();
				SpBaseInfoApply _apply = spBaseInfoApplyDao.load(id);
				
				_apply.setAddress(apply.getAddress());
				_apply.setCertificateNo(apply.getCertificateNo());
				_apply.setContactPersonMobileNo(apply.getContactPersonMobileNo());
				_apply.setContactPersonName(apply.getContactPersonName());
				if(ArrayUtils.isNotEmpty(apply.getFirmLogo())) {
					_apply.setFirmLogo(apply.getFirmLogo());
				}
				_apply.setFirmNature(apply.getFirmNature());
				_apply.setFirmScale(apply.getFirmScale());
				_apply.setLegalPersonIdNo(apply.getLegalPersonIdNo());
				_apply.setLegalPersonIdType(apply.getLegalPersonIdType());
				_apply.setLegalPersonName(apply.getLegalPersonName());
				_apply.setLocationNo(apply.getLocationNo());
				_apply.setName(apply.getName());
				_apply.setRegistrationNo(apply.getRegistrationNo());
				_apply.setShortName(apply.getShortName());
				_apply.setType(apply.getType());
				_apply.setRid(apply.getRid());
				_apply.setInBlack(apply.getInBlack());
				spBaseInfoApplyDao.saveOrUpdate(_apply);
			}
			
			SysUser user = sysUserDao.load(sp.getId());
			if(!StringUtils.isBlank(apply.getEmail())) {
				if(!user.getEmail().equals(apply.getEmail())) {
					user.setEmail(apply.getEmail());
				}
			}
			
			user.setProvince(sp.getLocationNo());
			sysUserDao.saveOrUpdate(user);
			
			spBaseInfoDao.saveOrUpdate(sp);
			
			bln = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}
	
	@Override
	public Page<Requistion> getRequistionPageForSp(Page<Requistion> page, Long spId) {
		try {
			String hql = "from Requistion a where a.type in (21,22,31) and a.originalId="+spId;
			return requistionDao.findPage(page, hql);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	
	public SpBaseInfo getOnlyBySpno(String spno) throws PlatformException {
		StringBuilder sb = new StringBuilder();
		sb.append(" from SpBaseInfo sp where sp.spNo=?");
		try {
			return spBaseInfoDao.findUniqueEntity(sb.toString(), spno);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		}
	}

	
	/* ********************************************
	 * method name   : getUnAuditSp 
	 * modified      : Administrator ,  2011-6-10
	 * @see          : @see com.justinmobile.tsm.sp.manager.SpBaseInfoManager#getUnAuditSp(com.justinmobile.core.dao.support.Page)
	 * ********************************************/     
	@Override
	public Page<SpBaseInfo> getUnAuditSp(Page<SpBaseInfo> page,final Object... values) {
		try {
			return spBaseInfoDao.getUnAuditSp(page,values);
		} catch (HibernateException e) {
			e.printStackTrace();
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		}
	}

	@Override
	public Page<SpBaseInfo> advanceSearch(Page<SpBaseInfo> page, Map<String, String> paramMap) {
		try {
			return spBaseInfoDao.advanceSearch(page, paramMap);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}
	
	@Override
	public Page<SpBaseInfo> recommendSpList(Page<SpBaseInfo> page) {
		try {
			SysUser currentUser = userManager.getUserByName(SpringSecurityUtils.getCurrentUserName());
			return spBaseInfoDao.recommendSpList(page, currentUser);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean handleRegisterApply(SpBaseInfoApply apply, boolean result) throws PlatformException {
		boolean bln = false;
		
		try {
			
			SpBaseInfo sp = spBaseInfoDao.load(apply.getRequistion().getOriginalId());
			if(result) {
				sp.setStatus(SpBaseInfo.STATUS_AVALIABLE);
				apply.setStatus(SpBaseInfo.STATUS_AVALIABLE);
				
				sp.setAttachment(apply.getAttachment());
				sp.setAttachmentName(apply.getAttachmentName());
				BeanUtils.copyProperties(apply, sp, new String[]{"id", "status"});
			}
			sp.setHasLock(SpBaseInfo.UNLOCK);
			spBaseInfoDao.saveOrUpdate(sp);
			spBaseInfoApplyDao.saveOrUpdate(apply);
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}

	@Override
	public boolean handleModifyApply(SpBaseInfoApply apply, boolean result) throws PlatformException {
		boolean bln = false;
		
		try {
			
			SpBaseInfo sp = spBaseInfoDao.load(apply.getRequistion().getOriginalId());
			if(result) {
				
				sp.setName(apply.getName());
				sp.setShortName(apply.getShortName());
				sp.setRegistrationNo(apply.getRegistrationNo());
				
				sp.setAddress(apply.getAddress());
				sp.setCertificateNo(apply.getCertificateNo());
				sp.setContactPersonMobileNo(apply.getContactPersonMobileNo());
				sp.setContactPersonName(apply.getContactPersonName());
				if(ArrayUtils.isNotEmpty(apply.getFirmLogo())) {
					sp.setFirmLogo(apply.getFirmLogo());
				}
				sp.setFirmNature(apply.getFirmNature());
				sp.setFirmScale(apply.getFirmScale());
				sp.setLegalPersonIdNo(apply.getLegalPersonIdNo());
				sp.setLegalPersonIdType(apply.getLegalPersonIdType());
				sp.setLegalPersonName(apply.getLegalPersonName());
				sp.setLocationNo(apply.getLocationNo());
				sp.setType(apply.getType());
				
				SysUser user = sysUserDao.load(apply.getRequistion().getOriginalId());
				if(user != null) {
					if(!user.getEmail().equals(apply.getEmail())) {
						user.setEmail(apply.getEmail());
					}
					user.setProvince(apply.getLocationNo());
					user.setEmail(apply.getEmail());
					sysUserDao.saveOrUpdate(user);
				}
			} else {
				apply.setApplyResult(Requistion.RESULT_REJECT_CH);
			}

			sp.setHasLock(SpBaseInfo.UNLOCK);
			spBaseInfoDao.saveOrUpdate(sp);
			spBaseInfoApplyDao.saveOrUpdate(apply);
			
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}

	@Override
	public boolean cancelApply(Long id) throws PlatformException {
		boolean bln = false;
		
		try {
			
			//SpBaseInfoApply apply = spBaseInfoApplyDao.findUniqueByProperty("id", id);
			Requistion requistion = requistionDao.load(id);
			
			SpBaseInfo sp = spBaseInfoDao.load(requistion.getOriginalId());
			sp.setHasLock(SpBaseInfo.UNLOCK);
			spBaseInfoDao.saveOrUpdate(sp);
			
			spBaseInfoApplyDao.delete(id);
			requistionDao.remove(id);
			
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}
	
	@Override
	public boolean cancelApply(SpBaseInfo sp) throws PlatformException {
		boolean bln = false;
		
		try {
			if(sp.getStatus().equals(SpBaseInfo.STATUS_INIT)) {
				//待审核状态的SP记录，无论申请是否处理，全部删除
				Long id = sp.getId();
//				Requistion requistion = requistionDao.findRequistionByOriginalIdAndType(id, Requistion.TYPE_SP_REGISTER);
//				if(requistion != null) {
//					spBaseInfoApplyDao.remove(requistion.getId());
//					requistionDao.remove(requistion.getId());
//				}
				spBaseInfoDao.deleteSpForUnavaliable(id);//.remove(id);
				sysUserDao.remove(id);
			} else if(sp.getStatus().equals(SpBaseInfo.STATUS_AVALIABLE)) {
				//可用状态的SP记录，只删未处理状态的申请
				String hql = "from Requistion a where a.originalId = ? and a.type = ? and a.reviewDate is null order by a.id desc";
				List<Requistion> list = requistionDao.find(hql, sp.getId(), Requistion.TYPE_SP_MODIFY);
				if(list == null) {
					throw new PlatformException(PlatformErrorCode.REQUISTION_NOT_EXIST);
				}
				if(list.isEmpty()) {
					throw new PlatformException(PlatformErrorCode.REQUISTION_NOT_EXIST);
				}
				
				Requistion requistion = list.get(0);
				requistionDao.remove(requistion.getId());
				spBaseInfoApplyDao.remove(requistion.getId());
				sp.setHasLock(SpBaseInfo.UNLOCK);
				spBaseInfoDao.saveOrUpdate(sp);
			}
			bln = true;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return bln;
	}
	
	@Override
	public Map<String, Object> getSpNameAndId(Map<String, Object> parameters) throws PlatformException {
		Map<String, Object> select = new HashMap<String, Object>();
		
		try {
			String hql = "select a.id, a.name from SpBaseInfo a where 1 = ?";
			
			if(parameters.containsKey("status")) {
				hql += " and a.status = " + parameters.get("status");
			}
			
			if(parameters.containsKey("province")) {
				String province = (String)parameters.get("province");
				if(!StringUtils.isBlank(province)) {
					hql += " and a.locationNo = '" + province + "' ";
				}
			}
			
			if(parameters.containsKey("type")) {
				String type = (String)parameters.get("type");
				if(!StringUtils.isBlank(type)) {
					hql += " and a.type in ("+SpBaseInfo.TYPE_GLOBAL_YD+","+SpBaseInfo.TYPE_LOCAL_YD+")";
				}
			}
			
			hql += " order by a.name";
			@SuppressWarnings("rawtypes")
			List list = spBaseInfoDao.find(hql, 1);
			
			if(list != null && !list.isEmpty()) {
				for(int index = 0; index < list.size(); index++) {
					Object[] objects = (Object[])list.get(index);
					select.put(objects[0].toString(), objects[1]);
				}
			}
			
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
		
		return select;
	}
	
	@Override
	public Map<String, Object> getSpNameAndId(Integer status, String province) throws PlatformException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", status);
		parameters.put("province", province);
		
		return getSpNameAndId(parameters);
	}
	
	@Override
	public Page<SpBaseInfo> getList(Page<SpBaseInfo> page, Map<String, String> params) throws PlatformException {
		return this.spBaseInfoDao.getSpForAvailableApplication(page, params);
	}
}