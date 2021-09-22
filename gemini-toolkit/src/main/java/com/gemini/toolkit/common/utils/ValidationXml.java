package com.gemini.toolkit.common.utils;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * xsd方式验证xml
 * @author jintg
 *
 */


public class ValidationXml {
    private static final Logger log = LoggerFactory.getLogger(ValidationXml.class);
	
    @SuppressWarnings("unchecked")
	public static String validateXMLByXSD(String xmlFilename,InputStream xml,String xsd) {

        // 从类路径下读取文件
        String xsdFileName = xsd;
        StringBuilder errorMsg = new StringBuilder();
        
        try {
            //创建默认的XML错误处理器
            XMLErrorHandler errorHandler = new XMLErrorHandler();
            //获取基于 SAX 的解析器的实例
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //解析器在解析时验证 XML 内容。
            factory.setValidating(true);
            //指定由此代码生成的解析器将提供对 XML 名称空间的支持。
            factory.setNamespaceAware(true);
            //使用当前配置的工厂参数创建 SAXParser 的一个新实例。
            SAXParser parser = factory.newSAXParser();
            //创建一个读取工具
            SAXReader xmlReader = new SAXReader();
            //获取要校验xml文档实例
            Document xmlDocument = (Document) xmlReader.read(xml);
            //设置 XMLReader 的基础实现中的特定属性。核心功能和属性列表可以在 [url]http://sax.sourceforge.net/?selected=get-set[/url] 中找到。
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource",xsdFileName);
            //创建一个SAXValidator校验工具，并设置校验工具的属性
            SAXValidator validator = new SAXValidator(parser.getXMLReader());
            //设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
            validator.setErrorHandler(errorHandler);
            //校验
            validator.validate(xmlDocument);

            // 错误输出方式1
            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
            //如果错误信息不为空，说明校验失败，打印错误信息
			if (errorHandler.getErrors().hasContent()) {
				// writer.write(errorHandler.getErrors());
				List<Element> elements = errorHandler.getErrors().elements();
				for (Element element : elements) {
					String line = String.valueOf(Integer.parseInt(element.attributeValue("line")));
					String text = element.getText();
					errorMsg.append("第 " + line + "行 :" + text + "\r\n");
				}
				log.error("XML文件通过XSD文件校验失败！详细信息：{}",errorMsg.toString());
			} else {
            	log.info("XML文件通过XSD文件校验成功！");
            }
            
        } catch (Exception ex) {
        	log.error("xml模板:{},通过xsd文件:{}校验失败。",xmlFilename,xsdFileName);
        	log.error("失败原因:{}",ex.getMessage());
            ex.printStackTrace();
        }
        return errorMsg.toString();
    }

}
