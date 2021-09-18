package com.gemini.toolkit.config;

import javax.sql.DataSource;

import com.alibaba.druid.filter.config.ConfigTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;

import java.util.Properties;

@Configuration
public class DruidConfig {
    //数据库密码公钥
    public static final String PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCPoC4YvTrosfLCnLOrOBBExlAtHXzPhNRQKPwo7Kl7SpDCSbkQBJVbNMEKUdO4uOWJrLZddq2i53LOHQUCYIn1zkcr9kHaIWYHXJfkUm/BapbbyF6ekqKk+cURXEXTSCl6dth/Ird/FHocNV5NYRYzMQ7fZgBpc1qYZtbH7UL76wIDAQAB";

    @Value("${db.type}")
    private String dbType;

	@Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.druid.initial-size}")
    private int initialSize;

    @Value("${spring.datasource.druid.min-idle}")
    private int minIdle;

    @Value("${spring.datasource.druid.max-active}")
    private int maxActive;

    @Value("${spring.datasource.druid.max-wait}")
    private int maxWait;
    
    @Value("${spring.datasource.druid.validation-query}")
    private String validationQuery;
    
    @Value("${spring.datasource.druid.time-between-eviction-runs-millis}")
    private int timeBetweenEvictionRunsMillis;

    @Value("${spring.datasource.druid.min-evictable-idle-time-millis}")
    private int minEvictableIdleTimeMillis;

    @Value("${spring.datasource.druid.test-while-idle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.test-on-borrow}")
    private boolean testOnBorrow;

    @Value("${spring.datasource.druid.test-on-return}")
    private boolean testOnReturn;

    @Value("${spring.datasource.druid.pool-prepared-statements}")
    private boolean poolPreparedStatements;
	
	@ConfigurationProperties(prefix = "spring.datasource.druid")
    @Bean
    public DataSource dataSource() throws Exception {
		
		DruidDataSource datasource = new DruidDataSource();
		
		datasource.setUrl(this.dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(ConfigTools.decrypt(PUBLIC_KEY, password));//公钥解密
        datasource.setDriverClassName(driverClassName);
        datasource.setDbType(dbType);
        //configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);


        return datasource;
    }

}
