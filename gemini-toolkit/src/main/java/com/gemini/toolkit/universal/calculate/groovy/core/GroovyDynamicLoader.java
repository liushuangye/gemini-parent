/**
 * 官网groovy bean示例如下：
 <beans>

 <!-- this bean is now 'refreshable' due to the presence of the 'refresh-check-delay' attribute -->
 <lang:groovy id="messenger"
 refresh-check-delay="5000" <!-- switches refreshing on with 5 seconds between checks -->
 script-source="classpath:Messenger.groovy">
 <lang:property name="message" value="I Can Do The Frug" />
 </lang:groovy>

 <bean id="bookingService" class="x.y.DefaultBookingService">
 <property name="messenger" ref="messenger" />
 </bean>

 </beans>
 */
package com.gemini.toolkit.universal.calculate.groovy.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.gemini.toolkit.universal.calculate.entity.CalculateRule;
import com.gemini.toolkit.universal.calculate.groovy.cache.BeanName;
import com.gemini.toolkit.universal.calculate.groovy.cache.BeanNameCache;
import com.gemini.toolkit.universal.calculate.groovy.cache.GroovyInfo;
import com.gemini.toolkit.universal.calculate.groovy.cache.GroovyInnerCache;
import com.gemini.toolkit.universal.calculate.groovy.core.*;
import com.gemini.toolkit.universal.calculate.service.CalculateRuleService;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Configuration
public class GroovyDynamicLoader implements ApplicationContextAware, InitializingBean {

    private ConfigurableApplicationContext applicationContext;

    private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader(GroovyDynamicLoader.class.getClassLoader());

    @Resource
    private CalculateRuleService calculateRuleService;

    /** Invoked by the containing BeanFactory after it has set all bean properties and satisfied BeanFactoryAware, ApplicationContextAware etc. */
    @Override
    public void afterPropertiesSet() throws Exception {

        long start = System.currentTimeMillis();
        System.out.println("开始解析groovy脚本...");

        init();

        long cost = System.currentTimeMillis() - start;
        System.out.println("结束解析groovy脚本...，耗时：" + cost);
    }

    private void init() {

        List<CalculateRule> CalculateRuleS = calculateRuleService.list(new QueryWrapper<CalculateRule>()
                .eq("status", "ENABLE"));

        List<BeanName> beanNameList = new ArrayList<>();

        List<GroovyInfo> groovyInfos = convert(CalculateRuleS, beanNameList);

        init(groovyInfos, beanNameList);
    }

    private void init(List<GroovyInfo> groovyInfos, List<BeanName> beanNameList) {

        if (CollectionUtils.isEmpty(groovyInfos)) {
            return;
        }

        ConfigurationXMLWriter config = new ConfigurationXMLWriter();

        addConfiguration(config, groovyInfos);

        put2map(groovyInfos, beanNameList);

        loadBeanDefinitions(config);
    }


    public void refresh() {

        List<CalculateRule> CalculateRuleS = calculateRuleService.list(new QueryWrapper<CalculateRule>()
                .eq("status", "ENABLE"));

        List<BeanName> beanNameList = new ArrayList<>();

        List<GroovyInfo> groovyInfos = convert(CalculateRuleS, beanNameList);

        if (CollectionUtils.isEmpty(groovyInfos)) {
            return;
        }

        // loadBeanDefinitions 之后才会生效
        destroyBeanDefinition(groovyInfos);

        destroyScriptBeanFactory();

        ConfigurationXMLWriter config = new ConfigurationXMLWriter();

        addConfiguration(config, groovyInfos);

        put2map(groovyInfos, beanNameList);

        loadBeanDefinitions(config);
    }

    private List<GroovyInfo> convert(List<CalculateRule> CalculateRuleS, List<BeanName> beanNameList) {

        List<GroovyInfo> groovyInfos = new LinkedList<>();

        if (CollectionUtils.isEmpty(CalculateRuleS)) {
            return groovyInfos;
        }

        for (CalculateRule CalculateRule : CalculateRuleS) {
            GroovyInfo groovyInfo = new GroovyInfo();
            groovyInfo.setClassName(CalculateRule.getBeanName());
            groovyInfo.setGroovyContent(CalculateRule.getCalculateRule());
            groovyInfos.add(groovyInfo);

            BeanName beanName = new BeanName();
            beanName.setInterfaceId(CalculateRule.getInterfaceId());
            beanName.setBeanName(CalculateRule.getBeanName());
            beanNameList.add(beanName);
        }

        return groovyInfos;
    }


    private void addConfiguration(ConfigurationXMLWriter config, List<GroovyInfo> groovyInfos) {
        for (GroovyInfo groovyInfo : groovyInfos) {
            writeBean(config, groovyInfo);
        }
    }

    private void loadBeanDefinitions(ConfigurationXMLWriter config) {

        String contextString = config.getContent();

        if (StringUtils.isBlank(contextString)) {
            return;
        }

        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) this.applicationContext.getBeanFactory());
        beanDefinitionReader.setResourceLoader(this.applicationContext);
        beanDefinitionReader.setBeanClassLoader(applicationContext.getClassLoader());
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this.applicationContext));

        beanDefinitionReader.loadBeanDefinitions(new InMemoryResource(contextString));

        String[] postProcessorNames = applicationContext.getBeanFactory().getBeanNamesForType(CustomScriptFactoryPostProcessor.class, true, false);

        for (String postProcessorName : postProcessorNames) {
            applicationContext.getBeanFactory().addBeanPostProcessor((BeanPostProcessor) applicationContext.getBean(postProcessorName));
        }
    }

    private void destroyBeanDefinition(List<GroovyInfo> groovyInfos) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        for (GroovyInfo groovyInfo : groovyInfos) {
            try {
                beanFactory.removeBeanDefinition(groovyInfo.getClassName());
            } catch (Exception e) {
                System.out.println("【Groovy】delete groovy bean definition exception. skip:" + groovyInfo.getClassName());
            }
        }
    }

    private void destroyScriptBeanFactory() {
        String[] postProcessorNames = applicationContext.getBeanFactory().getBeanNamesForType(CustomScriptFactoryPostProcessor.class, true, false);
        for (String postProcessorName : postProcessorNames) {
            CustomScriptFactoryPostProcessor processor = (CustomScriptFactoryPostProcessor) applicationContext.getBean(postProcessorName);
            processor.destroy();
        }
    }

    private void writeBean(ConfigurationXMLWriter config, GroovyInfo groovyInfo) {
        if (checkSyntax(groovyInfo)) {
            DynamicBean bean = composeDynamicBean(groovyInfo);
            config.write(GroovyConstant.SPRING_TAG, bean);
        }
    }

    private boolean checkSyntax(GroovyInfo groovyInfo) {
        try {
            groovyClassLoader.parseClass(groovyInfo.getGroovyContent());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private DynamicBean composeDynamicBean(GroovyInfo groovyInfo) {
        DynamicBean bean = new DynamicBean();
        String scriptName = groovyInfo.getClassName();

        Assert.notNull(scriptName, "parser className cannot be empty!");

        //设置bean的属性，这里只有id和script-source。
        bean.put("id", scriptName);
        bean.put("script-source", GroovyConstant.SCRIPT_SOURCE_PREFIX + scriptName);

        return bean;
    }

    private void put2map(List<GroovyInfo> groovyInfos, List<BeanName> beanNameList) {

        GroovyInnerCache.put2map(groovyInfos);

        BeanNameCache.put2map(beanNameList);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }
}
