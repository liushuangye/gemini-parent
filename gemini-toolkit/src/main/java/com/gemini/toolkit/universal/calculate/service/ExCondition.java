package com.gemini.toolkit.universal.calculate.service;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ExCondition implements Condition {

    /**
     * @param conditionContext:判断条件能使用的上下文环境
     * @param annotatedTypeMetadata:注解所在位置的注释信息
     * */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        try{
            Class.forName("com.gemini.toolkit.universal.calculate.service.impl.GroovyTestServiceImplEx");
            return false;
        }catch (Exception e){
            return true;
        }


    }
}