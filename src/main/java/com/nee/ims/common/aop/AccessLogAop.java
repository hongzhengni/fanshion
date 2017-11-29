package com.nee.ims.common.aop;

import com.nee.ims.common.A0Json;
import com.nee.ims.common.Result;
import com.nee.ims.common.exception.BusinessException;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by heikki on 17/8/23.
 */
 @Aspect
 @Component
public class AccessLogAop {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(public * com.nee.ims.service..execute(*))")
    private void execute() {
    }

    @Around("execute()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        return proceed(pjp);
    }

    /**
     * 执行切面方法
     *
     * @param pjp
     * @return
     */
    private Object proceed(ProceedingJoinPoint pjp) {

        String requestUrl = null;
        String methodName = null;
        Object[] args = null;
        try {
            args = pjp.getArgs();
            if (args[0] instanceof RoutingContext) {
                RoutingContext routingContext = (RoutingContext) args[0];
                logger.info("request param -> " + routingContext.getBodyAsString());
            }

            methodName = pjp.getSignature().getName();
            return pjp.proceed();
        } catch (Throwable e) {

            String msgCode = "1001";
            String msg = "系统异常, 稍后再试";

            if (logger.isErrorEnabled()) {
                logger.error("位置信息: {} ---- {}", methodName, pjp.toString());
                logger.error("请求参数: {} ---- {}", methodName, args);
                logger.error("异常信息: {} ---- {}", methodName, e.getMessage());
                logger.error("详细信息: ", e);
            }
            if (args[0] instanceof RoutingContext) {
                RoutingContext routingContext = (RoutingContext) args[0];
                if (e instanceof BusinessException) {
                    BusinessException exception = (BusinessException) e;
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(A0Json.encode(new Result.Builder().setCode(exception.getErrorCode())
                                    .setMessage(exception.getMessage()).build()));
                } else if (e instanceof DecodeException) {
                    DecodeException exception = (DecodeException) e;
                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(A0Json.encode(new Result.Builder().setCode("1004")
                                    .setMessage(exception.getMessage()).build()));
                } else {

                    routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
                            .end(A0Json.encode(new Result.Builder().setCode(msgCode).setMessage(msg).build()));
                }
            }
        }
        return null;
    }

    /**
     * 根据不同的返回类型 创建返回值
     *
     * @param msgCode
     * @param msg
     * @param returnType
     * @return
     */
    private Object createReturnObject(String msgCode, String msg, Class returnType) {

        if (returnType == null) {
            return returnType;
        }
        if (returnType.getSimpleName().equals("ApiResult")) {
            return new Result.Builder().setCode(msgCode);
        }

        return null;
    }
}
