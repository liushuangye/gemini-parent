
package com.gemini.workflow.exception;

import com.gemini.workflow.utils.RestMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理器
 *
 */
@RestControllerAdvice
public class WorkflowExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

//    @ExceptionHandler(ProcessInstanceNotFoundException.class)
//    public RestMsg handleException(Exception e) {
//        logger.error(e.getMessage(), e);
//        return RestMsg.response(ProcessInstanceNotFoundException.getCode(),ProcessInstanceNotFoundException.getMsg(),null);
//    }
}
