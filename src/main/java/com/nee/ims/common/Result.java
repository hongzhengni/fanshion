package com.nee.ims.common;

import com.nee.ims.uitls.StringUtils;

/**
 * Created by heikki on 17/8/20.
 */
public class Result {

    /** 请求id */
    private String reqId;
    /** 返回结果编码 */
    private String code;
    /** 返回结果消息 */
    private String message;
    /** 返回数据对象 */
    private Object data;

    public static class Builder {
        /** 请求id */
        private String reqId;
        /** 返回结果编码 */
        private String code;
        /** 返回结果消息 */
        private String message;
        /** 返回数据对象 */
        private Object data;

        public Builder(){}

        public Builder(String reqId){
            this.reqId = reqId;
        }

        public Builder setReqId(String reqId) {
            this.reqId = reqId;
            return this;
        }

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setData(Object data) {
            this.data = data;
            return this;
        }

        public Result build() { return new Result(this);}
    }

    public String getReqId() {
        return reqId;
    }

    public String getCode() {
        return code == null ? "0" : code;
    }

    public String getMessage() {
        return StringUtils.isBlank(message)? "操作成功" : message;
    }

    public Object getData() {
        return data;
    }

    private Result(Builder builder) {
        this.reqId = builder.reqId;
        this.message = builder.message;
        this.data = builder.data;
        this.code = builder.code;
    }
}