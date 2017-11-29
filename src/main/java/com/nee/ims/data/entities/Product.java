/**
 * Title: Product.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nee.ims.common.DBTableDataBean;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * product表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "product")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class Product extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "product";// 表名
	protected static String tableColumnNames = "product_id,store_id,user_id,product_code,product_name,color,size,category,reference_price,price_a,price_b,price_c,price_d,wholesale_count,material,season,introduction,import_function,status,is_delete,create_time,update_time";// 字段名
	
	protected static String tableKeyColumnNames = "product_id";// 主键字段

	@Id
	private String productId;
	private String storeId;
	private Integer businessLineId;
	private String userId;
	private String productCode;
	private String productName;
	private String color;
	private String size;
	private String category;
	private String referencePrice;
	@Column(name = "price_a")
	private String priceA;
	@Column(name = "price_b")
	private String priceB;
	@Column(name = "price_c")
	private String priceC;
	@Column(name = "price_d")
	private String priceD;
	private String wholesaleCount;
	private String material;
	private String season;
	private String introduction;
	private String importFunction;
	private Integer status;
	private Integer isDelete;
	private Date createTime;
	private Date updateTime;

	public Product() {
		super();
		clear();
	}

	public void clear() {
		productId = null;
		storeId = null;
		userId = null;
		productCode = null;
		productName = null;
		color = null;
		size = null;
		category = null;
		referencePrice = null;
		priceA = null;
		priceB = null;
		priceC = null;
		priceD = null;
		wholesaleCount = null;
		material = null;
		season = null;
		introduction = null;
		importFunction = null;
		status = null;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public Integer getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(Integer businessLineId) {
		this.businessLineId = businessLineId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	public String getReferencePrice() {
		return referencePrice;
	}

	public void setReferencePrice(String referencePrice) {
		this.referencePrice = referencePrice;
	}
	public String getPriceA() {
		return priceA;
	}

	public void setPriceA(String priceA) {
		this.priceA = priceA;
	}
	public String getPriceB() {
		return priceB;
	}

	public void setPriceB(String priceB) {
		this.priceB = priceB;
	}
	public String getPriceC() {
		return priceC;
	}

	public void setPriceC(String priceC) {
		this.priceC = priceC;
	}
	public String getPriceD() {
		return priceD;
	}

	public void setPriceD(String priceD) {
		this.priceD = priceD;
	}
	public String getWholesaleCount() {
		return wholesaleCount;
	}

	public void setWholesaleCount(String wholesaleCount) {
		this.wholesaleCount = wholesaleCount;
	}
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}
	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getImportFunction() {
		return importFunction;
	}

	public void setImportFunction(String importFunction) {
		this.importFunction = importFunction;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	@Transient
	private String startDate;
	@Transient
	private String endDate;
	@Transient
	private List<String> productIds;
	@Transient
	private Boolean collect = false;
	@Transient
	private String pictureUrl;
	@Transient
	private List<String> pictureUrls;
	@Transient
	private List<Map<String, String>> sizes;
	@Transient
	private List<Map<String, String>> colors;
	@Transient
	private Map<String, Map<String, Object>> remember;
	@Transient
	private List<String> userIds;
	@Transient
	private Map<String, Object> storeInfo;
	@Transient
	private Integer days;


	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<String> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<String> productIds) {
		this.productIds = productIds;
	}

	public List<String> getPictureUrls() {
		return pictureUrls;
	}

	public void setPictureUrls(List<String> pictureUrls) {
		this.pictureUrls = pictureUrls;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public List<Map<String, String>> getSizes() {
		return sizes;
	}

	public void setSizes(List<Map<String, String>> sizes) {
		this.sizes = sizes;
	}

	public List<Map<String, String>> getColors() {
		return colors;
	}

	public void setColors(List<Map<String, String>> colors) {
		this.colors = colors;
	}

	public Map<String, Map<String, Object>> getRemember() {
		return remember;
	}

	public void setRemember(Map<String, Map<String, Object>> remember) {
		this.remember = remember;
	}

	public void setCollect(Boolean collect) {
		this.collect = collect;
	}

	public Boolean getCollect() {
		return collect;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public Map<String, Object> getStoreInfo() {
		return storeInfo;
	}

	public void setStoreInfo(Map<String, Object> storeInfo) {
		this.storeInfo = storeInfo;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}
}
