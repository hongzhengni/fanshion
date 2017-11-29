/**
 * Title: StoreFollow.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nee.ims.common.DBTableDataBean;

import javax.persistence.*;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * store_follow表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "store_follow")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class StoreFollow extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "store_follow";// 表名
	protected static String tableColumnNames = "follow_id,store_id,user_id,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "follow_id";// 主键字段

	@Id
	private String followId;

	private String storeId;
	private String userId;
	private Integer isDelete;
	private String createTime;
	private String updateTime;

	public StoreFollow() {
		super();
		clear();
	}

	public void clear() {
		followId = null;
		storeId = null;
		userId = null;
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

	public String getFollowId() {
		return followId;
	}

	public void setFollowId(String followId) {
		this.followId = followId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
	private Store store;

	public Store getStore() {
		return store;
	}

	public Store setStore(Store store) {
		this.store = store;
		return this.store;
	}
}
