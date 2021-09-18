package com.gemini.toolkit.universal.calculate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gemini.toolkit.universal.calculate.entity.CalculateRule;
import com.gemini.toolkit.universal.calculate.mapper.CalculateRuleMapper;
import com.gemini.toolkit.universal.calculate.service.CalculateRuleService;
import org.springframework.stereotype.Service;

@Service
public class CalculateRuleServiceImpl extends ServiceImpl<CalculateRuleMapper, CalculateRule> implements CalculateRuleService {
}
