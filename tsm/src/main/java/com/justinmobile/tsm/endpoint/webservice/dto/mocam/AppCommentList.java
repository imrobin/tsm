package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.justinmobile.core.utils.CalendarUtils;
import com.justinmobile.tsm.application.domain.ApplicationComment;
import com.justinmobile.tsm.endpoint.webservice.NameSpace;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppCommentList", namespace = NameSpace.CM)
public class AppCommentList {
	
	@XmlElement(namespace = NameSpace.CM)
	private List<AppComment> Comment = Lists.newArrayList();

	public List<AppComment> getAppComment() {
		return Comment;
	}

	public void setAppComment(List<AppComment> Comment) {
		this.Comment = Comment;
	}

	public void addAll(List<ApplicationComment> result) {
		if (CollectionUtils.isNotEmpty(result)) {
			for (ApplicationComment comment : result) {
				AppComment appComment = new AppComment();
				appComment.setCommentTime(CalendarUtils.parsefomatCalendar(comment.getCommentTime(), CalendarUtils.LONG_FORMAT));
				appComment.setCommentContent(comment.getContent());
				appComment.setStarGrade(comment.getGrade() == null ? 5 : comment.getGrade());
				appComment.setUserName(comment.getCustomer().getSysUser().getUserName());
				appComment.setAppAID(comment.getApplication().getAid());
				this.Comment.add(appComment);
			}
		}
		
	}

}
