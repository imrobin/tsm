package com.justinmobile.core.utils.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.justinmobile.core.dao.support.Page;
import com.justinmobile.core.domain.AbstractEntity;

public class JsonResult {
	
	private Boolean success;
	
	private String message;
	
	private Integer totalCount;
	
	private Integer totalPage;
	
	private List<Map<String, Object>> result = Lists.newArrayList();
	
	public JsonResult() {
		this.success = Boolean.TRUE;
		this.message = "操作成功";
	}

	public Boolean getSuccess() {
		return success;
	}


	public void setSuccess(Boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Map<String, Object>> getResult() {
		return result;
	}

	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	
	public void setPage(Page<Map<String, Object>> page) {
		this.result = page.getResult();
		this.totalCount = page.getTotalCount();
		this.totalPage = page.getTotalPages();
	}
	
	public <T extends AbstractEntity> void setPage(Page<T> page, String excludeField, String includeCascadeField) {
		setResult(page.getResult(), excludeField, includeCascadeField);
		this.totalCount = page.getTotalCount();
		this.totalPage = page.getTotalPages();
	}
	
	public <T extends AbstractEntity> void setResult(Collection<T> result, String excludeField, String includeCascadeField) {
		if (CollectionUtils.isNotEmpty(result)) {
			for (T t : result) {
				Map<String, Object> map = t.toMap(excludeField, includeCascadeField);
				this.result.add(map);
			}
		}
	}
	
}
