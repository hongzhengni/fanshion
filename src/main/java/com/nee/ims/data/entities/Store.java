/**
 * Title: Store.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;



import com.nee.ims.common.DBTableDataBean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * store表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "store")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class Store extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "store";// 表名
	protected static String tableColumnNames = "store_id,user_id,store_name,store_sign,logo,provice_id,city_id,area_id,zone_id,address,business_line_id,business_line_name,desc,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "store_id";// 主键字段

	@Id
	private String storeId;
	private String userId;
	private String storeName;
	private String storeSign;
	private String logo;
	private String provinceId;
	private String cityId;
	private String areaId;
	private String zoneId;
	private String address;
	private String fullAddress;
	private Integer businessLineId;
	private String businessLineName;
	private Integer description;
	private Integer isDelete;
	private String createTime;
	private Date updateTime;

	public Store() {
		super();
		clear();
	}

	public void clear() {
		storeId = null;
		userId = null;
		storeName = null;
		storeSign = null;
		logo = null;
		provinceId = null;
		cityId = null;
		areaId = null;
		zoneId = null;
		address = null;
		fullAddress = null;
		businessLineId = null;
		businessLineName = null;
		description = null;
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
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreSign() {
		return storeSign;
	}

	public void setStoreSign(String storeSign) {
		this.storeSign = storeSign;
	}
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
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

	public Integer getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(Integer businessLineId) {
		this.businessLineId = businessLineId;
	}
	public String getBusinessLineName() {
		return businessLineName;
	}

	public void setBusinessLineName(String businessLineName) {
		this.businessLineName = businessLineName;
	}
	public Integer getDescription() {
		return description;
	}

	public void setDescription(Integer desc) {
		this.description = desc;
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
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Transient
	private Long fansNum;
	@Transient
	private Boolean hasFollow = false;
	@Transient
	private Double earnings;
	@Transient
	private Double todayEarnings;
	@Transient
	private Long todayOrderNum;
	@Transient
	private Long todayFanNum;
	@Transient
	private Long yesterdayFanNum;
	@Transient
	private String userName;
	@Transient
	private String avatarUrl;



	public Long getFansNum() {
		return fansNum;
	}

	public void setFansNum(Long fansNum) {
		this.fansNum = fansNum;
	}

	public Boolean getHasFollow() {
		return hasFollow;
	}

	public void setHasFollow(Boolean hasFollow) {
		this.hasFollow = hasFollow;
	}

	public Double getEarnings() {
		return earnings;
	}

	public void setEarnings(Double earnings) {
		this.earnings = earnings;
	}

	public Double getTodayEarnings() {
		return todayEarnings;
	}

	public void setTodayEarnings(Double todayEarnings) {
		this.todayEarnings = todayEarnings;
	}

	public Long getTodayOrderNum() {
		return todayOrderNum;
	}

	public void setTodayOrderNum(Long todayOrderNum) {
		this.todayOrderNum = todayOrderNum;
	}


	public Long getTodayFanNum() {
		return todayFanNum;
	}

	public void setTodayFanNum(Long todayFanNum) {
		this.todayFanNum = todayFanNum;
	}

	public Long getYesterdayFanNum() {
		return yesterdayFanNum;
	}

	public void setYesterdayFanNum(Long yesterdayFanNum) {
		this.yesterdayFanNum = yesterdayFanNum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
