package com.nee.ims.common.constant;

/**
 * Created by heikki on 17/5/8.
 */
public enum ErrorCodeEnum {

    /** 操作成功 */
    SUCCESS (0, "操作成功"),
    /** 系统错误，稍后再试 */
    SYSTEM_ERROR (1001, "系统错误，稍后再试 "),
    /** 参数为空 */
    NO_PARAM (1002, "参数为空"),
    /** 新增的参数已经存在(唯一性约束) */
    DUPLICATE_DATA (1003, "新增的参数已经存在(唯一性约束)"),
    /** 参数不正确 */
    ERROR_PARAM (1004, "参数不正确"),
    /** 逻辑错误 */
    ERROR_LOGIC (1005, "逻辑错误"),
    /** 依赖外部接口失败 */
    OUT_API_ERROR (1006, "依赖外部接口失败"),
    /** 用户认证失败 */
    AUTH_ERROR (1007, "用户认证失败"),
    /** 会话连接超时 */
    CONNECT_TIMEOUT (1008, "会话连接超时"),
    /** 校验失败 */
    VERIFY_ERROR (1009, "校验失败"),
    /** 重复提交错误 */
    SUBMIT_DUPLICATE (1010, "重复提交错误"),
    /** 数据不存在 */
    DATA_NOT_EXIST (1011, "数据不存在"),
    /** 用户未激活 */
    USER_NOT_ACTIVE (1012, "用户未激活"),
    /** 用户已注销 */
    LOGOUT_USER (1013, "用户已注销"),
    /** 签名错误 */
    ERROR_SIGN (1014, "签名错误"),
    /** 用户不存在或密码错误 */
    LOGIN_ERROR (1015, "用户不存在或密码错误"),
    /** session已过期 */
    SESSION_EXPIRED (1016, "登录状态已过期"),
    /** 没有相应的方法 */
    REQUEST_ERROR (1017, "没有相应的方法"),
    /** 版本号不正确 */
    VERSION_ERROR (1018, "版本号不正确"),


    /** 手机号不合法 */
     MOBILE_FORMAT_ERROR (2001, "手机号不合法"),
    /** 密码长度不合法 */
    PASSWORD_LENGTH_ERRORR (2002, "密码长度不合法"),
    /** 验证码错误 */
     V_CODE_ERROR (2003, "验证码错误"),
    /** 验证码过期 */
     V_CODE_EXPIRED (2004, "验证码过期"),
    /** 手机号已注册 */
     DUPLICATE_MOBILE (2005, "手机号已注册"),
    /** 客户不存在 */
     CUSTOMER_NOT_EXIST (2006, "客户不存在"),
    /** 密码错误 */
     PASSWORD_ERROR (2007, "密码错误"),
    /** 微信号已绑定 */
     WECHAT_HAS_BOUND (2008, "微信号已绑定"),
    /** 微信号未绑定 */
     WECHAT_NOT_BOUND (2009, "微信号未绑定"),
    /** 用户名已注册 */
     DUPLICATE_CUSTOMER_NAME (2010, "用户名已注册"),
    /** 账号已禁用 */
     STOPPED (2011, "账号已禁用"),
    /** 已绑定过银行卡号 */
     HAS_BOUND_BANK_CARD_NO (2012, "已绑定过银行卡号"),
    /** 账号已禁用 */
     OTHER_BOUND_BANK_CARD_NO (2013, "账号已禁用"),
    /** 身份证已经被绑定过 */
     OTHER_BOUND_ID_CARD_NO (2014, "身份证已经被绑定过"),
    /** 未通过实名认证 */
    NOT_PASS_AUTH (2015, "未通过实名认证"),
    /** 验证次数超过3次 */
    MORE_THAN_THREE (2016, "您已验证失败3次，无法再次验证，请联系签约人再次发起邀请"),
    /** 已经超过24小时 */
    MORE_THAN_HOUR (2017, "您未在24小时内完成验证，请联系签约人再次发起邀请"),
    /** 邀请人已经取消 */
    HAS_CANCEL (2018, "对方已取消对您的邀请"),
    /** 邀请人已经完成 */
    HAS_FINISH (2019, "已经验证成功，无需重复验证"),
    /** 认证失败 */
    AUTH_FAIL (2020, "验证失败，您还有%d次机会"),


    NO_PRODUCT_PERMISSION(3001, "需要申请访问"),

    /** 账单状态错误 */
     ERROR_BILL_STATUS (4001, "账单状态错误"),
    /** 新增增配错误 */
     ADD_FACILITY_ERROR (4002, "新增增配错误"),
    /** 非本人账单 */
     NOT_BILL_OWNER (2003, "非本人账单");

    private int code;
    private String message;

    private ErrorCodeEnum (int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
