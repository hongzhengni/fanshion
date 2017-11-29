/**
 * Title: ContactGroupRelation.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nee.ims.common.DBTableDataBean;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * contact_group_relation表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "contact_group_relation")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class ContactGroupRelation extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "contact_group_relation";// 表名
	protected static String tableColumnNames = "relation_id,group_id,user_id,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "relation_id";// 主键字段

	@Id
	private String relationId;
	private String groupId;
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	private Integer isDelete;
	private Date createTime;
	private Date updateTime;

	public ContactGroupRelation() {
		super();
		clear();
	}

	public void clear() {
		relationId = null;
		groupId = null;
		user = null;
		isDelete = null;
		createTime = null;
		updateTime = null;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getTableKeyColumnNames() {
		return tableKeyColumnNames;
	}

	public String getRelationId() {
		return relationId;
	}

	public void setRelationId(String relationId) {
		this.relationId = relationId;
	}
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
