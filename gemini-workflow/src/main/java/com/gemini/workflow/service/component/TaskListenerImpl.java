package com.gemini.workflow.service.component;

import com.alibaba.fastjson.JSONObject;
import com.gemini.workflow.mapper.CommonMapper;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * activiti 任务监听：可以配置到模板的任务节点中进行使用
 */
public class TaskListenerImpl  implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("我是一个监听");
    }
}
