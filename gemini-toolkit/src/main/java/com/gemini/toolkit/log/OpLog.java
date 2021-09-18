package com.gemini.toolkit.log;

import java.lang.annotation.*;
/**
 * @author zcj
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface OpLog {

    /**
     * 该注解作用于方法上时需要备注信息
     */
    String remarks() default "";

    String moduleName() default "";

    String opName() default "";

}
