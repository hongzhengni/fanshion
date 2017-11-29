/**
 * Title: StockOrder.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nee.ims.common.DBTableDataBean;
import io.vertx.core.json.JsonObject;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * stock_order表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "stock_order")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class StockOrder extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	protected static String tableName = "stock_order";// 表名
	protected static String tableColumnNames = "order_id,order_no,order_type,store_id,store_name,freight,status,user_id,user_name,customer_id,customer_name,address_id,pay_way,logistics_way,logistics_no,is_delete,create_time,update_time,order_time,pay_time,deliver_time";// 字段名
	
	protected static String tableKeyColumnNames = "order_id";// 主键字段

	@Id
	private String orderId;
	private String orderNo;
	private Integer orderType;
	private String storeId;
	private String storeName;
	private String freight;
	private Integer status;
	private String userId;
	private String userName;
	private String customerId;
	private String customerName;
	private String addressId;
	private Integer payWay;
	private Integer logisticsWay;
	private String logisticsCompany;
	private String logisticsNo;
	private Integer isDelete;
	private String createTime;
	private String updateTime;
	private Date orderTime;
	private Date payTime;
	private Date deliverTime;
	private Date confirmTime;


	public StockOrder() {
		super();
		clear();
	}

	public void clear() {
		orderId = null;
		orderNo = null;
		orderType = null;
		storeId = null;
		storeName = null;
		freight = null;
		status = null;
		userId = null;
		userName = null;
		customerId = null;
		customerName = null;
		addressId = null;
		payWay = null;
		logisticsWay = null;
		logisticsNo = null;
		isDelete = null;
		createTime = null;
		updateTime = null;
		orderTime = null;
		payTime = null;
		deliverTime = null;
		confirmTime = null;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	public String getTableKeyColumnNames() {
		return tableKeyColumnNames;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public Integer getPayWay() {
		return payWay;
	}

	public void setPayWay(Integer payWay) {
		this.payWay = payWay;
	}
	public Integer getLogisticsWay() {
		return logisticsWay;
	}

	public void setLogisticsWay(Integer logisticsWay) {
		this.logisticsWay = logisticsWay;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsNo() {
		return logisticsNo;
	}

	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
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

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getDeliverTime() {
		return deliverTime;
	}

	public void setDeliverTime(Date deliverTime) {
		this.deliverTime = deliverTime;
	}

	public Date getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(Date confirmTime) {
		this.confirmTime = confirmTime;
	}

	@Transient
	private Address address;
	@Transient
	private List<JsonObject> items;

	public List<JsonObject> getItems() {
		return items;
	}

	public void setItems(List<JsonObject> items) {
		this.items = items;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
