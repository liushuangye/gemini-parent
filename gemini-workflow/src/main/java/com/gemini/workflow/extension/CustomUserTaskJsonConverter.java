package com.gemini.workflow.extension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.xml.internal.ws.util.xml.CDATA;
import org.activiti.bpmn.model.*;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class CustomUserTaskJsonConverter extends UserTaskJsonConverter{
    //UserTask扩展属性
    public static final String HANDLE_URL = "handleurl";
    public static final String SHOW_URL = "reviewurl";

    private void addUserTaskExtensionElement(String name, String elementText, UserTask task) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setNamespace(NAMESPACE);
        extensionElement.setNamespacePrefix("activiti");
        extensionElement.setName(name);
        extensionElement.setElementText(elementText);
        task.addExtensionElement(extensionElement);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode,modelNode,shapeMap);
        UserTask userTask = (UserTask)flowElement;

        /**
         * 第一种方式：不能出现url特殊字符，url需要转码解码
         * 添加扩展属性的名称对应stencilset.json里定义的id
         */
//        CustomProperty reviewurl = new CustomProperty();
//        reviewurl.setName("reviewurl");
//        reviewurl.setSimpleValue(this.getPropertyValueAsString("reviewurl",elementNode));
//        userTask.getCustomProperties().add(reviewurl);

        /**
         * 第二种方式：这么设置数据会被<![CDATA[  ]]>包裹，可避免特殊字符。
         * 两种只能选一种
         */
        addUserTaskExtensionElement(HANDLE_URL,this.getPropertyValueAsString(HANDLE_URL,elementNode),userTask);
        addUserTaskExtensionElement(SHOW_URL,this.getPropertyValueAsString(SHOW_URL,elementNode),userTask);
        return userTask;
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        UserTask userTask = (UserTask) baseElement;

        //解析
        Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
        if(!extensionElements.isEmpty() ){
            if(extensionElements.containsKey(HANDLE_URL)){
                ExtensionElement e = extensionElements.get(HANDLE_URL).get(0);
                setPropertyValue(HANDLE_URL, e.getElementText(), propertiesNode);
            }
            if(extensionElements.containsKey(SHOW_URL)){
                ExtensionElement e = extensionElements.get(SHOW_URL).get(0);
                setPropertyValue(SHOW_URL, e.getElementText(), propertiesNode);
            }
        }
    }
}
