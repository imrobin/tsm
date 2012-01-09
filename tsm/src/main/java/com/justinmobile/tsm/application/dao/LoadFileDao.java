package com.justinmobile.tsm.application.dao;

import java.util.List;

import com.justinmobile.core.dao.EntityDao;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.sp.domain.SpBaseInfo;

public interface LoadFileDao extends EntityDao<LoadFile, Long> {

	/**
	 * 返回满足以下条件的加载文件
	 * <ol>
	 * <li>属于指定的sp</li>
	 * <li>私有的</li>
	 * <li>未被指定的应用版本引用，即
	 * <ul>
	 * <li>未被任何应用版本引用</li>
	 * <li>被除指定应用版本之外的其他应用版本引用</li>
	 * <ul></li>
	 * <ol>
	 * 
	 * @param sp
	 *            加载文件所属sp
	 * @param applicationVersionId
	 *            指定的应用版本ID
	 * @return 满足条件的加载文件
	 */
	List<LoadFile> getLoadFilesWhichExclusivAndBelongSpAndUnassociateWithApplicationVersion(SpBaseInfo sp, Long applicationVersionId);

	/**
	 * 返回满足以下条件的加载文件
	 * <ol>
	 * <li>共享的的</li>
	 * <li>未被指定的应用版本引用，即
	 * <ul>
	 * <li>未被任何应用版本引用</li>
	 * <li>被除指定应用版本之外的其他应用版本引用</li>
	 * <ul></li>
	 * <ol>
	 * 
	 * @param applicationVersionId
	 *            指定的应用版本ID
	 * @return 满足条件的加载文件
	 */
	List<LoadFile> getSharedLoadFilesWhichUnassociateWithApplicationVersion(Long applicationVersionId);

	/**
	 * 返回满足以下条件的加载文件
	 * <ol>
	 * <li>共享的的</li>
	 * <li>所有版本都未被指定的加载文件版本依赖
	 * <li>不是加载文件版本属于加载文件</li>
	 * <ol>
	 * 
	 * @param loadFileVersion
	 *            加载文件版本
	 * @return 满足条件的加载文件
	 */
	List<LoadFile> getThatIsSharedAndIsnotSelfAndIsnotDependent(LoadFileVersion loadFileVersion);

	Page<LoadFile> loadByIds(Page<LoadFile> page, String loadFileIds);

	/**
	 * 查找未被指定应用版本使用但被指定应用版本所属应用的其他版本使用的、类型为指定类型的加载文件
	 * 
	 * @param applicationVersion
	 *            指定的加载文件
	 * @param fileType
	 *            加载文件的类型
	 * @return 查找结果
	 */
	List<LoadFile> getUnusedByApplicationVersionAndType(ApplicationVersion applicationVersion, int fileType);

}