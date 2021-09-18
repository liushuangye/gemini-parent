package com.gemini.toolkit.universal.calculate.groovy.core;


import com.gemini.toolkit.universal.calculate.groovy.calculate.GroovyParserEngine;

public class CalculateEngineInstanceFactory {

    private static GroovyParserEngine groovyParserEngine;

    public static GroovyParserEngine getGroovyParserEngine() {
        return groovyParserEngine;
    }

    public static void setGroovyParserEngine(GroovyParserEngine groovyParserEngine) {
        CalculateEngineInstanceFactory.groovyParserEngine = groovyParserEngine;
    }
}
