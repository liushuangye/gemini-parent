package com.gemini.toolkit.universal.calculate.service.impl;

import com.gemini.toolkit.universal.calculate.service.ExCondition;
import com.gemini.toolkit.universal.calculate.service.GroovyTestService;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

@Service
@Conditional(ExCondition.class)
public class GroovyTestServiceImpl implements GroovyTestService {
    @Override
    public String fun2(String name) {
        return "hello "+name;
    }
}
