package com.nee.ims.common;

/**
 * Created by heikki on 17/8/24.
 */
public class Request<T> {

    /** 请求id，随机数，用于生成签名以及返回时校验是否对应同一个请求 */
    private String reqId;
    /** 签名 */
    private String sign;
    /** 时间戳 */
    private String timestamp;
    /** 请求方法 */
    private String method;
    /** 版本号 */
    private String version;
    /** 设备Id */
    private String devId;
    /** 请求业务参数，请参看具体api说明 */
    private T params;

    public String getReqId() {
        return reqId;
    }

    public Request setReqId(String reqId) {
        this.reqId = reqId;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public Request setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Request setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public T getParams() {
        return params;
    }

    public Request setParams(T params) {
        this.params = params;
        return this;
    }
}



