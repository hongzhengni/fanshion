/**
 * Title: ProductPermission.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 *
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nee.ims.common.DBTableDataBean;


/**
 * 以下为数据库对应的JavaBean代码，统一由自动工具生成，请不要人工修改
 * product_permission表
 *
 * @author
 * @version
 */
@Entity
@Table(name = "product_permission")
@JsonIgnoreProperties(value = {"tableName", "tableColumnNames", "tableKeyColumnNames"})
public class ProductPermission extends DBTableDataBean
        implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    protected static String tableName = "product_permission";// 表名
    protected static String tableColumnNames = "perission_id,product_id,customer_id,is_delete,create_time,update_time";// 字段名

    protected static String tableKeyColumnNames = "perission_id";// 主键字段

    @Id
    private String permissionId;

    private String userId;
    private Integer isDelete;
    private Date validateTime;
    private Date createTime;
    private Date updateTime;

    public ProductPermission() {
        super();
        clear();
    }

    public void clear() {
        permissionId = null;
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

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
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

    public Date getValidateTime() {
        return validateTime;
    }

    public void setValidateTime(Date validateTime) {
        this.validateTime = validateTime;
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


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    public Product getProduct() {
        if (product == null) {
            product = new Product();
        }
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Transient
    private String category;
    @Transient
    private String storeId;
    @Transient
    private Integer businessLineId;
    @Transient
    private String startDate;
    @Transient
    private String endDate;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
