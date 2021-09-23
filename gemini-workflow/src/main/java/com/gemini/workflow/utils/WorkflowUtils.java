package com.gemini.workflow.utils;

import com.github.wnameless.json.flattener.JsonFlattener;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;
import java.util.Properties;

public class WorkflowUtils {
    /**
     * 使用businessData中的信息替换str的占位符
     * @param variables
     * @param str
     * @return
     */
    public static String replacePlaceHolder(Map variables, String str){
        Object businessData = variables.get("businessData");
        Properties properties = new Properties();//json扁平化的结果
        if(businessData != null){
            String businessDataStr = businessData.toString();
            Map<String, Object> flatMap = JsonFlattener.flattenAsMap(businessDataStr);
            for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
                System.out.println("businessData." + entry.getKey() + ":" + entry.getValue());
                properties.put("businessData." + entry.getKey(), String.valueOf(entry.getValue()));//所有value转成String，否则后续替换占位符会报错
            }
        }
        //替换掉url中的占位符
        PropertyPlaceholderHelper propertyHelper= new PropertyPlaceholderHelper("${", "}", ":", false);//false
        str = propertyHelper.replacePlaceholders(str, properties);
        return str;
    }
}
