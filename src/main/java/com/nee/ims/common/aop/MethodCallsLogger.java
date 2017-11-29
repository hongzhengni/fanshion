package com.nee.ims.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

//@Aspect
//@Component
public class MethodCallsLogger {

    @Pointcut("execution(static * io.vertx.core.json..*(..))")
    private void jacksonDecodeValue() {
    }


    @Around("jacksonDecodeValue()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        System.out.println(
                "Called--> " + className + "." + methodName + "-- Arguments =" +
                        Arrays.toString(joinPoint.getArgs()));
    }
}