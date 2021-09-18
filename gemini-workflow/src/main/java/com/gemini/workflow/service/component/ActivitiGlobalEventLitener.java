package com.gemini.workflow.service.component;

import cn.hutool.core.date.DateUtil;
import com.gemini.workflow.entity.User;
import com.gemini.workflow.mapper.UserMapper;
import com.gemini.workflow.service.BaseService;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.impl.ActivitiEntityEventImpl;
import org.activiti.engine.delegate.event.impl.ActivitiEventImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntityImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * Activiti的全局事件监听器，即所有事件均需要在这里统一分发处理
 *
 */
@Component
public class ActivitiGlobalEventLitener extends BaseService implements ActivitiEventListener {
    @Resource
    UserMapper userMapper;


    @Override
    public void onEvent(ActivitiEvent event) {
        //事件类型
        String eventType=event.getType().name();
        //任务完成事件
        if("TASK_COMPLETED".equals(eventType)){
            ActivitiEntityEventImpl entityEvent = (ActivitiEntityEventImpl) event;
            TaskEntityImpl entity = (TaskEntityImpl) entityEvent.getEntity();
            String executionId = entity.getExecutionId();
            String preUserId = entity.getAssignee();//任务处理人
            String preOptTime = DateUtil.format(DateUtil.date(),"yyyy/MM/dd HH:mm:ss");//任务处理时间
            User user = userMapper.loadUserByUserId(preUserId);
            String preUserName = user.getStaffName();
            //每当一个任务结束的时候记录处理人和处理时间
            Map variables = new HashMap();
            variables.put("preUserId",preUserId);
            variables.put("preUserName",preUserName);
            variables.put("preOptTime",preOptTime);
            runtimeService.setVariables(executionId,variables);
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}