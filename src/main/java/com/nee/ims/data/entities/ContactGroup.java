/**
 * Title: ContactGroup.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;



import com.nee.ims.common.DBTableDataBean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * contact_group表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "contact_group")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class ContactGroup extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "contact_group";// 表名
	protected static String tableColumnNames = "group_id,store_id,group_name,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "group_id";// 主键字段

	@Id
	private String groupId;
	private String storeId;
	private String groupName;
	private Integer isDelete;
	private String createTime;
	private String updateTime;

	public ContactGroup() {
		super();
		clear();
	}

	public void clear() {
		groupId = null;
		storeId = null;
		groupName = null;
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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Transient
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
