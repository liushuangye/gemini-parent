package com.gemini.toolkit.universal.calculate.groovy.calculate.impl;


import com.gemini.toolkit.universal.calculate.entity.request.CalculateRequest;
import com.gemini.toolkit.universal.calculate.entity.response.CalculateResponse;
import com.gemini.toolkit.universal.calculate.groovy.cache.BeanNameCache;
import com.gemini.toolkit.universal.calculate.groovy.calculate.GroovyParserEngine;
import com.gemini.toolkit.universal.calculate.groovy.core.CalculateEngineInstanceFactory;
import com.gemini.toolkit.universal.calculate.groovy.calculate.CalculateParser;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class GroovyParserEngineImpl implements GroovyParserEngine, ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Override
    public CalculateResponse parse(CalculateRequest request) {

        String beanName = BeanNameCache.getByInterfaceId(request.getInterfaceId());

        CalculateParser parser = (CalculateParser) applicationContext.getBean(beanName);

        return parser.parse(request);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CalculateEngineInstanceFactory.setGroovyParserEngine(this);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
