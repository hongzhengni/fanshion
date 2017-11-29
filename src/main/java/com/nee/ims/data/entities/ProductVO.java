/**
 * Title: Product.java
 * Copyright: Copyright (C) 2015 - 2018 HangZhou Nee Techonlogy Co. Ltd
 * Company: 杭州xxxxx有限公司
 * @author: auto-tools
 */
package com.nee.ims.data.entities;


import com.nee.ims.common.DBTableDataBean;

import java.util.List;
import java.util.Map;


/**

 */

public class ProductVO extends DBTableDataBean
		implements java.io.Serializable , Cloneable {
	private static final long serialVersionUID = 1L;

	private String productId;
	private String productCode;
	private Map productNameInfo;
	private Map colorInfo;
	private Map sizeInfo;
	private Map categoryInfo;
	private Map referencePriceInfo;
	private Map priceAInfo;
	private Map priceBInfo;
	private Map priceCInfo;
	private Map priceDInfo;
	private Map wholesaleCountInfo;
	private Map materialInfo;
	private Map seasonInfo;
	private Map introductionInfo;
	private Map importFunctionInfo;


	public ProductVO() {
		super();
		clear();
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Map getProductNameInfo() {
		return productNameInfo;
	}

	public void setProductNameInfo(Map productNameInfo) {
		this.productNameInfo = productNameInfo;
	}

	public Map getColorInfo() {
		return colorInfo;
	}

	public void setColorInfo(Map colorInfo) {
		this.colorInfo = colorInfo;
	}

	public Map getSizeInfo() {
		return sizeInfo;
	}

	public void setSizeInfo(Map sizeInfo) {
		this.sizeInfo = sizeInfo;
	}

	public Map getCategoryInfo() {
		return categoryInfo;
	}

	public void setCategoryInfo(Map categoryInfo) {
		this.categoryInfo = categoryInfo;
	}

	public Map getReferencePriceInfo() {
		return referencePriceInfo;
	}

	public void setReferencePriceInfo(Map referencePriceInfo) {
		this.referencePriceInfo = referencePriceInfo;
	}

	public Map getPriceAInfo() {
		return priceAInfo;
	}

	public void setPriceAInfo(Map priceAInfo) {
		this.priceAInfo = priceAInfo;
	}

	public Map getPriceBInfo() {
		return priceBInfo;
	}

	public void setPriceBInfo(Map priceBInfo) {
		this.priceBInfo = priceBInfo;
	}

	public Map getPriceCInfo() {
		return priceCInfo;
	}

	public void setPriceCInfo(Map priceCInfo) {
		this.priceCInfo = priceCInfo;
	}

	public Map getPriceDInfo() {
		return priceDInfo;
	}

	public void setPriceDInfo(Map priceDInfo) {
		this.priceDInfo = priceDInfo;
	}

	public Map getWholesaleCountInfo() {
		return wholesaleCountInfo;
	}

	public void setWholesaleCountInfo(Map wholesaleCountInfo) {
		this.wholesaleCountInfo = wholesaleCountInfo;
	}

	public Map getMaterialInfo() {
		return materialInfo;
	}

	public void setMaterialInfo(Map materialInfo) {
		this.materialInfo = materialInfo;
	}

	public Map getSeasonInfo() {
		return seasonInfo;
	}

	public void setSeasonInfo(Map seasonInfo) {
		this.seasonInfo = seasonInfo;
	}

	public Map getIntroductionInfo() {
		return introductionInfo;
	}

	public void setIntroductionInfo(Map introductionInfo) {
		this.introductionInfo = introductionInfo;
	}

	public Map getImportFunctionInfo() {
		return importFunctionInfo;
	}

	public void setImportFunctionInfo(Map importFunctionInfo) {
		this.importFunctionInfo = importFunctionInfo;
	}

	private List<String> pictureUrls;

	public List<String> getPictureUrls() {
		return pictureUrls;
	}

	public void setPictureUrls(List<String> pictureUrls) {
		this.pictureUrls = pictureUrls;
	}
}
