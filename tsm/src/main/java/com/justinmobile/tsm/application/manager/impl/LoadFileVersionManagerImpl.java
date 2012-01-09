package com.justinmobile.tsm.application.manager.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.exception.PlatformErrorCode;
import com.justinmobile.core.exception.PlatformException;
import com.justinmobile.core.manager.EntityManagerImpl;
import com.justinmobile.tsm.application.dao.LoadFileVersionDao;
import com.justinmobile.tsm.application.domain.ApplicationVersion;
import com.justinmobile.tsm.application.domain.LoadFile;
import com.justinmobile.tsm.application.domain.LoadFileVersion;
import com.justinmobile.tsm.application.manager.ApplicationLoadFileManager;
import com.justinmobile.tsm.application.manager.LoadFileManager;
import com.justinmobile.tsm.application.manager.LoadFileVersionManager;
import com.justinmobile.tsm.card.domain.CardBaseInfo;
import com.justinmobile.tsm.card.manager.CardBaseInfoManager;

@Service("loadFileVersionManager")
public class LoadFileVersionManagerImpl extends EntityManagerImpl<LoadFileVersion, LoadFileVersionDao> implements LoadFileVersionManager {

	@Autowired
	private LoadFileVersionDao loadFileVersionDao;

	@Autowired
	private LoadFileManager loadFileManager;

	@Autowired
	private ApplicationLoadFileManager applicationLoadFileManager;

	@Autowired
	private CardBaseInfoManager cardBaseInfoManager;

	@Override
	public List<LoadFileVersion> getByApplicationVersion(Long applicationVersionId) {
		try {
			return loadFileVersionDao.getByApplicationVersion(applicationVersionId);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersion(Long applicationVersionId, Integer shareFlag) {
		return loadFileVersionDao.getWhichImportedByApplicationVersion(applicationVersionId, shareFlag);
	}

	@Override
	public void createNewLoadFileVersionForApplicaitonVersion(LoadFileVersion loadFileVersion, Map<String, String> params, String username) {
		try {
			createNewLoadFileVersion(loadFileVersion, params, username);

			// 将加载文件版本与应用版本关联
			Long applicationVersionId = Long.parseLong(params.get("applicationVersionId"));
			applicationLoadFileManager.buildImportBetweenLoadFileVersionAndApplicationVersion(loadFileVersion.getId(),
					applicationVersionId, username);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void createNewLoadFileVersion(LoadFileVersion loadFileVersion, Map<String, String> params, String username) {
		try {
			String loadFileIdStr = params.get("loadFileId");
			LoadFile loadFile = loadFileManager.load(Long.parseLong(loadFileIdStr));

			// 验证用户是否有权创建加载文件版本
			if (!loadFileManager.isEditable(username, loadFile)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
			}

			// 判断版本号是存在
			if (StringUtils.isBlank(loadFileVersion.getVersionNo())) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_NO_BLANK);
			}

			// 判断版本号是否重复
			int count = loadFileVersionDao.getCountByLoadFileAndVersionNo(loadFile, loadFileVersion.getVersionNo());
			if (0 != count) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_NO_REDUPLICATE);
			}

			// 解析临时文件
			String tempFilePath = params.get("tempFileAbsPath");
			if (!(new File(tempFilePath)).isFile()) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_CAP_UNEXIST);
			}
			loadFileVersion.parseLoadFile(params.get("tempDir"), tempFilePath);

			// 保存
			loadFileVersionDao.saveOrUpdate(loadFileVersion);

			// 添加加载文件版本
			loadFile.addLoadFileVersion(loadFileVersion);

			loadFileVersionDao.saveOrUpdate(loadFileVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	/**
	 * 后续遍历指定加载文件版本的依赖
	 * 
	 * @param origin
	 *            起点，待遍历依赖的加载文件版本
	 * @param visted
	 *            已访问的加载文件版本集合
	 * @param result
	 *            存储遍历结果的列表
	 */
	private void postorderTraversalDependence(LoadFileVersion origin, Set<LoadFileVersion> visted, List<LoadFileVersion> result) {
		try {
			for (LoadFileVersion parent : origin.getParents()) {
				postorderTraversalDependence(parent, visted, result);
			}

			if (!visted.contains(origin)) {
				result.add(origin);
				visted.add(origin);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public boolean isEditable(String username, LoadFileVersion loadFileVersion) {
		try {
			return loadFileManager.isEditable(username, loadFileVersion.getLoadFile());
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LoadFileVersion> calcDependenceAsDownloadOrder(Collection<LoadFileVersion> origins) {
		try {
			List<LoadFileVersion> result = new ArrayList<LoadFileVersion>();
			Set<LoadFileVersion> visted = new HashSet<LoadFileVersion>();

			for (LoadFileVersion origin : origins) {
				postorderTraversalDependence(origin, visted, result);
			}

			return result;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LoadFileVersion> calcDependenceAsDeleteOrder(Collection<LoadFileVersion> origins) {
		try {
			List<LoadFileVersion> order = calcDependenceAsDownloadOrder(origins);// 获取下载顺序
			Collections.reverse(order);// 将下载顺序倒置成为删除顺序
			return order;
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void addDependence(LoadFileVersion parent, LoadFileVersion child, String username) {
		try {
			if (!isEditable(username, child)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
			}

			child.addDenepency(parent);

			validateCircularDependence(child);

			loadFileVersionDao.saveOrUpdate(child);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void validateCircularDependence(LoadFileVersion loadFileVersion) {
		try {
			Set<LoadFile> traversalToChildren = null;
			{
				// 确定依赖当前文件版本的文件版本中没有循环依赖
				Set<LoadFile> preorderToChildren = new HashSet<LoadFile>();
				Set<LoadFile> postorderToChildren = new HashSet<LoadFile>();
				validateCircularDependenceToChildren(loadFileVersion, preorderToChildren, postorderToChildren);
				traversalToChildren = postorderToChildren;
			}

			Set<LoadFile> traversalToParents = null;
			{
				// 确定被当前文件版本依赖的文件版本中没有循环依赖
				Set<LoadFile> preorderToParents = new HashSet<LoadFile>();
				Set<LoadFile> postorderToParents = new HashSet<LoadFile>();
				validateCircularDependenceToParents(loadFileVersion, preorderToParents, postorderToParents);
				traversalToParents = postorderToParents;
			}

			// 在对依赖文件版本的遍历结果和对被依赖版本的遍历结果中去掉当前节点后，判断是否还有将在文件版本同时在两个集合中，如果有，说明存在循环依赖
			traversalToChildren.remove(loadFileVersion.getLoadFile());
			traversalToParents.remove(loadFileVersion.getLoadFile());
			if (CollectionUtils.containsAny(traversalToChildren, traversalToParents)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE);
			}
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}

	}

	/**
	 * 同时先序遍历和后序遍历被依赖关系，根据当前加载文件是否在先序集合及后序集合中有以下4中排列情况：
	 * <ul>
	 * <li>情况1：不在先序集合中；不在后序集合中——第一次遍历该加载文件节点</li>
	 * <li>情况2：在先序集合中；不在后序集合中——已经遍历该加载文件节点，正在遍历其加载文件节点时再次遍历到该加载文件，说明有循环依赖存在</li>
	 * <li>情况3：在先序集合中；在后序集合中——已经遍历该加载文件节点，且已经完成对其后代文件节点的遍历</li>
	 * <li>情况4：不在先序集合中；在后序集合中——不可能出现</li>
	 * </ul>
	 * 当且仅当情况2出现时，抛出异常，终止遍历
	 * 
	 * @param loadFileVersion
	 *            当前加载文件版本
	 * @param preorder
	 *            先序遍历集合
	 * @param postorder
	 *            后序遍历集合
	 * 
	 * @throws LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE
	 *             如果出现循环依赖
	 */
	private void validateCircularDependenceToParents(LoadFileVersion loadFileVersion, Set<LoadFile> preorder, Set<LoadFile> postorder) {
		LoadFile loadFile = loadFileVersion.getLoadFile();

		if (!preorder.contains(loadFile)) {
			preorder.add(loadFile);
			for (LoadFileVersion child : loadFileVersion.getChildren()) {
				validateCircularDependenceToParents(child, preorder, postorder);
			}
			postorder.add(loadFile);
		} else if (!postorder.contains(loadFile)) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE);
		} else {
			return;
		}

	}

	/**
	 * 同时先序遍历和后序遍历依赖关系，根据当前加载文件是否在先序集合及后序集合中有以下4中排列情况：
	 * <ul>
	 * <li>情况1：不在先序集合中；不在后序集合中——第一次遍历该加载文件节点</li>
	 * <li>情况2：在先序集合中；不在后序集合中——已经遍历该加载文件节点，正在遍历其加载文件节点时再次遍历到该加载文件，说明有循环依赖存在</li>
	 * <li>情况3：在先序集合中；在后序集合中——已经遍历该加载文件节点，且已经完成对其后代文件节点的遍历</li>
	 * <li>情况4：不在先序集合中；在后序集合中——不可能出现</li>
	 * </ul>
	 * 当且仅当情况2出现时，抛出异常，终止遍历
	 * 
	 * @param loadFileVersion
	 *            当前加载文件版本
	 * @param preorder
	 *            先序遍历集合
	 * @param postorder
	 *            后序遍历集合
	 * 
	 * @throws LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE
	 *             如果出现循环依赖
	 */
	private void validateCircularDependenceToChildren(LoadFileVersion loadFileVersion, Set<LoadFile> preorder, Set<LoadFile> postorder) {
		LoadFile loadFile = loadFileVersion.getLoadFile();

		if (!preorder.contains(loadFile)) {
			preorder.add(loadFile);
			for (LoadFileVersion parent : loadFileVersion.getParents()) {
				validateCircularDependenceToChildren(parent, preorder, postorder);
			}
			postorder.add(loadFile);
		} else if (!postorder.contains(loadFile)) {
			throw new PlatformException(PlatformErrorCode.LOAD_FILE_VERSION_CIRCULAR_DEPENDENCE);
		} else {
			return;
		}
	}

	@Override
	public void removeDependence(LoadFileVersion parent, LoadFileVersion child, String username) {
		try {
			if (!isEditable(username, child)) {
				throw new PlatformException(PlatformErrorCode.LOAD_FILE_SP_DISCARD);
			}

			child.removeDenepency(parent);

			loadFileVersionDao.saveOrUpdate(parent);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public Page<LoadFileVersion> findUnLinkPage(Page<LoadFileVersion> page, String cardBaseId) {
		try {
			CardBaseInfo cbi = cardBaseInfoManager.load(Long.valueOf(cardBaseId));
			String hql = "from LoadFileVersion as lf where  lf not in (select cblf.loadFileVersion from CardBaseLoadFile as cblf where cblf.cardBaseInfo = ?)";
			return loadFileVersionDao.findPage(page, hql, cbi);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersionAndType(ApplicationVersion applicationVersion, int type) {
		try {
			return loadFileVersionDao.getWhichImportedByApplicationVersionAndType(applicationVersion, type);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

	@Override
	public void removeIfNotImport(LoadFileVersion loadFileVersion) {
		if (0 == loadFileVersion.getApplicationLoadFiles().size()) {
			LoadFile loadFile = loadFileVersion.getLoadFile();

			loadFileVersionDao.remove(loadFileVersion);

			loadFileManager.removeIfNotHasVersion(loadFile);
		}

	}

	@Override
	public List<LoadFileVersion> getWhichImportedByApplicationVersion(ApplicationVersion applicationVersion) {
		try {
			return loadFileVersionDao.getWhichImportedByApplicationVersionOrderByAidAsc(applicationVersion);
		} catch (PlatformException e) {
			throw e;
		} catch (HibernateException e) {
			throw new PlatformException(PlatformErrorCode.DB_ERROR, e);
		} catch (Exception e) {
			throw new PlatformException(PlatformErrorCode.UNKNOWN_ERROR, e);
		}
	}

}