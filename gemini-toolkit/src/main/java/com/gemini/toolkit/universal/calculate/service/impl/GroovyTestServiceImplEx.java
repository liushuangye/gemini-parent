package com.gemini.toolkit.universal.calculate.service.impl;

import org.springframework.stereotype.Service;

@Service
public class GroovyTestServiceImplEx extends GroovyTestServiceImpl {
    @Override
    public String fun2(String name) {
        return "我覆盖了父类方法";
    }
}
