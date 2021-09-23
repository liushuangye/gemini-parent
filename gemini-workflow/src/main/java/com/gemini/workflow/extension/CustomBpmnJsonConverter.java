package com.gemini.workflow.extension;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

import java.util.Map;

public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    //获取convertersToBpmnMap，后续会添加自定义的属性
    public static Map<String,Class<? extends BaseBpmnJsonConverter>> getConvertersToBpmnMap(){
        return convertersToBpmnMap;
    }
    //获取convertersToJsonMap，后续会添加自定义的属性
    public static Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> getConvertersToJsonMap(){
        return convertersToJsonMap;
    }
}
