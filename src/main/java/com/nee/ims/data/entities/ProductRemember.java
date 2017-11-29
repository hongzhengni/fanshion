/**
 * Title: ProductRemember.java
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
 * product_remember表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "product_remember")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class ProductRemember extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "product_remember";// 表名
	protected static String tableColumnNames = "remember_id,store_id,product_id,attribute_code,attribute_name,default_show,remember,value,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "remember_id";// 主键字段

	@Id
	private String rememberId;
	private String storeId;
	private String productId;
	private String attributeCode;
	private String attributeName;
	private Boolean defaultShow;
	private Boolean remember;
	private String value;
	private Integer isDelete;
	private String createTime;
	private String updateTime;

	public ProductRemember() {
		super();
		clear();
	}

	public void clear() {
		rememberId = null;
		storeId = null;
		productId = null;
		attributeCode = null;
		attributeName = null;
		defaultShow = null;
		remember = null;
		value = null;
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

	public String getRememberId() {
		return rememberId;
	}

	public void setRememberId(String rememberId) {
		this.rememberId = rememberId;
	}
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getAttributeCode() {
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Boolean getDefaultShow() {
		return defaultShow;
	}

	public void setDefaultShow(Boolean defaultShow) {
		this.defaultShow = defaultShow;
	}

	public Boolean getRemember() {
		return remember;
	}

	public void setRemember(Boolean remember) {
		this.remember = remember;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
}
