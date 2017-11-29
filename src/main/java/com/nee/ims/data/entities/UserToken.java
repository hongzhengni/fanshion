/**
 * Title: UserToken.java
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


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * user_token表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "user_token")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class UserToken extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "user_token";// 表名
	protected static String tableColumnNames = "id,user_id,token,status,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "user_id";// 主键字段

	@Id
	private String id;
	private String userId;
	private String token;
	private Integer status;
	private String createTime;
	private String updateTime;

	public UserToken() {
		super();
		clear();
	}

	public void clear() {
		id = null;
		userId = null;
		token = null;
		status = null;
		createTime = null;
		updateTime = null;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getTableKeyColumnNames() {
		return tableKeyColumnNames;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
}
