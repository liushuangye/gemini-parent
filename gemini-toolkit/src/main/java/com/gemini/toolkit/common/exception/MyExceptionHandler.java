
package com.gemini.toolkit.common.exception;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.gemini.toolkit.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 *
 * @author BHH
 * @date 2018-05-29
 */
@RestControllerAdvice
public class MyExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private MessageSource messageSource;

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(MyException.class)
    public R handleMyException(MyException e) {
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());

        return r;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R handlerNoFoundException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error(404, messageSource.getMessage("url.notFound", null, LocaleContextHolder.getLocale()));
    }


    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e) {
        logger.error(e.getMessage(), e);
        return R.error(messageSource.getMessage("data.duplicate", null, LocaleContextHolder.getLocale()));
    }


    /**
     * 用来处理bean validation异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public R resolveConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        String message = "";
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            for (ConstraintViolation constraintViolation : constraintViolations) {
                message = constraintViolation.getMessage();
            }
        }
        logger.warn(message);
        return R.error(400, message);

    }

    /**
     * 用来处理bean validation异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public R resolveMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ObjectError> objectErrors = ex.getBindingResult().getAllErrors();
        String message = "";
        if (!CollectionUtils.isEmpty(objectErrors)) {
            for (ObjectError error : objectErrors) {
                message = error.getDefaultMessage();
            }
        }
        logger.warn(message);
        return R.error(400, message);
    }

    /**
     * 用来处理PgInputCheckException异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(PgInputCheckException.class)
    @ResponseBody
    public R resolvePgInputCheckException(PgInputCheckException ex) {
        String message = ex.getMessage();
        logger.warn(message);
        return R.error(400, message);
    }

    /**
     * 用来处理PgApplicationException异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(PgApplicationException.class)
    @ResponseBody
    public R resolvePgApplicationException(PgApplicationException ex) {
        String message = ex.getMessage();
        logger.warn(message);
        return R.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return R.error(messageSource.getMessage("systemError", null, LocaleContextHolder.getLocale()));
    }
}
