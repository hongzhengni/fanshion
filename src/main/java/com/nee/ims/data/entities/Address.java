/**
 * Title: Address.java
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
 * address表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "address")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class Address extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "address";// 表名
	protected static String tableColumnNames = "address_id,user_id,province_id,city_id,area_id,zone_id,address,full_address,contact_name,contact_mobile,is_default,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "address_id";// 主键字段

	@Id
	private String addressId;
	private String userId;
	private String provinceId;
	private String cityId;
	private String areaId;
	private String zoneId;
	private String address;
	private String fullAddress;
	private String contactName;
	private String contactMobile;
	private Integer isDefault;
	private Integer isDelete;
	private String createTime;
	private String updateTime;

	public Address() {
		super();
		clear();
	}

	public void clear() {
		addressId = null;
		userId = null;
		provinceId = null;
		cityId = null;
		areaId = null;
		zoneId = null;
		address = null;
		fullAddress = null;
		contactName = null;
		contactMobile = null;
		isDefault = null;
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

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}
	public Integer getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Integer isDefault) {
		this.isDefault = isDefault;
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
