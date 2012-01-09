package com.justinmobile.core.domain;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

public class DomainToMapTest {
	
	@Test
	public void testToMap() throws Exception {
		User user = new User();
		user.setId(12345l);
		user.setName("testUser");
		user.setDate(Calendar.getInstance());
		user.setTimes(1111.123456);
		user.setStatus(0);
		Group group = new Group();
		group.setGroupName("groupNameAAAAAA");
		Org org = new Org();
		org.setId(111l);
		org.setOrgDate(new Date());
		group.setOrg(org);
		user.setGroup(group);
		
		Map<String, Object> map = user.toMap("name id", "group.groupName group.org.id group.org.orgDate");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
	public class User extends AbstractEntity {

		private static final long serialVersionUID = 2761815653070207105L;

		private Long id;
		
		private String name;
		
		@DateFormat(format = "yyyy年MM月dd日 HH时mm分ss秒")
		private Calendar date;
		
		@DecimalNumberFormat
		private Double times;
		
		@ResourcesFormat(key = "user.status")
		private Integer status;
		
		private Group group;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Calendar getDate() {
			return date;
		}

		public void setDate(Calendar date) {
			this.date = date;
		}

		public Double getTimes() {
			return times;
		}

		public void setTimes(Double times) {
			this.times = times;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public Group getGroup() {
			return group;
		}

		public void setGroup(Group group) {
			this.group = group;
		}
	}
	public class Org {
		
		private Long id;
		
		@DateFormat
		private Date orgDate;

		public Date getOrgDate() {
			return orgDate;
		}

		public void setOrgDate(Date orgDate) {
			this.orgDate = orgDate;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}
	public class Group {
		
		private String groupName;
		
		private Org org;

		public Org getOrg() {
			return org;
		}

		public void setOrg(Org org) {
			this.org = org;
		}

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

	}

}
