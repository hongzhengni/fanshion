package com.nee.ims.common.constant;

public interface CommonConstant {

	/**
	 * 是否删除
	 */
	interface DELETE {

		int YES = 1;

		int NO = 0;
	}

	/**
	 * 是否默认
	 */
	interface DEFAULT {

		int YES = 1;

		int NO = 0;
	}

	/**
	 * 状态：1-启用，2-停用
	 */
	interface SWITCH_STATUS {

		/**
		 * 启用
		 */
		int ON = 1;
		/**
		 * 停用
		 */
		int OFF = 2;
	}

	/**
	 * 是否默认：0-否，1-是
	 */
	interface IS_DEFAULT {
		/** 0-否 */
		int NO = 0;
		/** 1-是 */
		int YES = 1;

	}

	/**
	 * 性别
	 */
	interface GENDER {
		/** 男 */
		int MALE = 1;
		/** 女 */
		int FEMALE = 2;
	}

	/**
	 * 证件类型
	 */
	interface CARD_TYPE {
		/** 身份证 */
		int ID_CARD = 1;
		/** 护照 */
		int PASSPORT = 2;
		/** 港澳通行证 */
		int HK_MO_PASSPORT = 3;
		/** 台湾通行证 */
		int TAIWAN_PASSPORT = 4;
	}

	/**
	 * 极光推送状态
	 */
	interface JPUSH_STATUS {
		/** 未推送 */
		int NO_PUSH = 1;
		/** 已推送 */
		int PUSH_SUCCESS = 2;
		/** 推送失败 */
		int PUSH_FAIL = 3;
	}

	/**
	 * 地区等级：1-国家，2-省，3-市，4-区/县/镇
	 *
	 */
	interface AREA_LEVEL {
		/** 国家 */
		int COUNTRY = 1;
		/** 省 */
		int PROVINCE = 2;
		/** 市 */
		int CITY = 3;
		/** 区/县/镇 */
		int AREA = 4;
	}

	interface ORDER_STATUS {
		/** 下单中 */
		int ORDERED = 1;
		/** 待支付 */
		int UN_PAY = 2;
		/** 待发货 */
		int UN_DELIVER = 3;
		/** 待收货 */
		int UN_RECEIVING = 4;
		/** 完成 */
		int FINISH = 5;
		/** 取消 */
		int CANCEL = 6;
	}
}
