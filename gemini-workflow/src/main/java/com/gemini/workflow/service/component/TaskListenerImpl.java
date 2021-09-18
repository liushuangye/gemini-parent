package com.gemini.workflow.service.component;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * activiti 任务监听：可以配置到模板的任务节点中进行使用
 */
@Component
public class TaskListenerImpl  implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("我是一个监听");
    }
}
