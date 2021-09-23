package com.gemini.workflow.extension;

import org.activiti.bpmn.model.UserTask;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringExpressionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExtensionConfiguration {
    @Bean
    public CustomBpmnJsonConverter customerBpmnJsonConverter(){
        /**
         * 1.创建CustomBpmnJsonConverter，触发父类的静态方法完成相关map的初始化
         * 2.初始化之后添加自定义属性
         */
        CustomBpmnJsonConverter customBpmnJsonConverter = new CustomBpmnJsonConverter();
        CustomBpmnJsonConverter.getConvertersToBpmnMap().put("UserTask", CustomUserTaskJsonConverter.class);/** 添加自定义的属性到BpmnJsonConverter的map中 */
        CustomBpmnJsonConverter.getConvertersToJsonMap().put(UserTask.class, CustomUserTaskJsonConverter.class);
        return customBpmnJsonConverter;
    }

}
