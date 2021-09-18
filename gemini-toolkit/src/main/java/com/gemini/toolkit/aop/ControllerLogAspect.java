package com.gemini.toolkit.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * controller method log
 * @author liuwei2017
 *
 */
@Aspect
@Component
public class ControllerLogAspect {
    
    private static Logger logger = LoggerFactory.getLogger(ControllerLogAspect.class);
    
    @Pointcut("execution(public * com.gemini.toolkit.*.controller..*.*(..))")
    public void logPointCut() { 
    }
    
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        Signature signature = point.getSignature();

        try {
            logger.info(signature + " start");
            result = point.proceed();
        } catch (Throwable e) {
            logger.error(signature + " error", e);
            throw e;
        } finally {
            logger.info(signature + " end");
        }

        return result;
    }

}
