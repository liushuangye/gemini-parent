package com.gemini.toolkit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;

@Component
public class KeyGenerator {

	@ConditionalOnProperty(value = "db.type" , havingValue = "postgresql") 
	@Bean
	public PostgreKeyGenerator postgreKeyGenerator() {
		
		return new PostgreKeyGenerator();
	}
	
	@ConditionalOnProperty(value = "db.type" , havingValue = "oracle") 
	@Bean
	public OracleKeyGenerator  oracleKeyGenerator () {
		
		return new OracleKeyGenerator();
	}
	
}
