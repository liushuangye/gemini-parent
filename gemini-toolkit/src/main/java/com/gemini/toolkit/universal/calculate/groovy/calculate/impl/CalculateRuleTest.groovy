package com.gemini.toolkit.universal.calculate.groovy.calculate.impl


import com.gemini.toolkit.universal.calculate.groovy.calculate.CalculateParser

/**
 * 需要把此代码放进DB
 * 此文件用于研发调试，可以从项目中删掉
 * groovy基于JVM完全兼容java语法，可以无障碍过渡
 * groovy特有语法更加灵活简洁
 */
class CalculateRuleTest implements CalculateParser {

    private com.gemini.toolkit.universal.calculate.service.GroovyTestService groovyTestService = com.gemini.toolkit.universal.utils.SpringUtils.getBean(com.gemini.toolkit.universal.calculate.service.GroovyTestService.class);

    @Override
    com.gemini.toolkit.universal.calculate.entity.response.CalculateResponse parse(com.gemini.toolkit.universal.calculate.entity.request.CalculateRequest request) {
        if(groovyTestService != null)System.out.println("-----Spring Bean 注入到了groovy-----");
        groovyTestService.fun1();
        def name = request.getExtendInfo().get("name");
        System.out.println(groovyTestService.fun2("我是项目中的脚本！"));
        return null;
    }
}
