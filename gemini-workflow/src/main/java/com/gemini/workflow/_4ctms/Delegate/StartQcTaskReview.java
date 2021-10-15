package com.gemini.workflow._4ctms.Delegate;

import com.alibaba.fastjson.JSONObject;
import com.gemini.workflow.service.BaseService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用于质控预约流程结束后启动质控任务流程
 */
public class StartQcTaskReview implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        String qcOrderVwStr = (String) variables.get("qcOrderVw");
        JSONObject qcOrderVw = JSONObject.parseObject(qcOrderVwStr);


        System.out.println("123");
        int a = 1/0;
    }
}
