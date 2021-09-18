package com.gemini.toolkit.universal.calculate.controller;


import com.gemini.toolkit.universal.calculate.entity.request.CalculateRequest;
import com.gemini.toolkit.universal.calculate.groovy.calculate.GroovyParserEngine;
import com.gemini.toolkit.universal.calculate.groovy.core.GroovyDynamicLoader;
import groovy.lang.GroovyShell;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CalculateController {

    @Resource
    private GroovyParserEngine groovyParserEngine;

    @Resource
    private GroovyDynamicLoader groovyDynamicLoader;

    @RequestMapping("/calculate")
    public Object calculate(String name) {

        String interfaceId = "ctms.test";

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        CalculateRequest request = new CalculateRequest();
        request.setInterfaceId(interfaceId);
        request.setExtendInfo(map);

        return groovyParserEngine.parse(request);
    }

    @RequestMapping("/refresh")
    public void refresh() {
        groovyDynamicLoader.refresh();
    }

    public static void main(String[] args) throws IOException {
        GroovyShell groovyShell = new GroovyShell();
        Object evaluate = groovyShell.evaluate(new File("src/main/java/com/example/demo/groovy/Test.groovy"));
        System.out.println(evaluate.toString());
    }
}
