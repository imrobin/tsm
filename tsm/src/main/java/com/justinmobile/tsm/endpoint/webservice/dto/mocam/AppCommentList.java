package com.justinmobile.tsm.endpoint.webservice.dto.mocam;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd HH:mm:ss");
				appComment.setCommentTime(sdf.format(comment.getCommentTime().getTime()));
				appComment.setCommentContent(comment.getContent());
				appComment.setStarGrade(comment.getGrade());
				appComment.setUserName(comment.getCustomer().getSysUser().getUserName());
				appComment.setAppAID(comment.getApplication().getAid());
				appComment.setCommentId(comment.getId());
				appComment.setUp(comment.getUp());
				appComment.setDown(comment.getDown());
				this.Comment.add(appComment);
			}
		}
		
	}

}
