package com.gemini.toolkit.universal.calculate.groovy.calculate;


import com.gemini.toolkit.universal.calculate.entity.request.CalculateRequest;
import com.gemini.toolkit.universal.calculate.entity.response.CalculateResponse;

/**
 * 接入方都要实现这个接口
 */
public interface CalculateParser {

    CalculateResponse parse(CalculateRequest request);
}
