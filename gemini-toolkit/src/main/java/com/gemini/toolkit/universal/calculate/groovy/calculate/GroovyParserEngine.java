package com.gemini.toolkit.universal.calculate.groovy.calculate;

import com.gemini.toolkit.universal.calculate.entity.request.CalculateRequest;
import com.gemini.toolkit.universal.calculate.entity.response.CalculateResponse;

public interface GroovyParserEngine {

    CalculateResponse parse(CalculateRequest request);
}
