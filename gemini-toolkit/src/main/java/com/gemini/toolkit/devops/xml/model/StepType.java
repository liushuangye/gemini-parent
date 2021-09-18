//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2021.05.24 时间 06:36:27 PM CST 
//


package com.gemini.toolkit.devops.xml.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>stepType的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * <p>
 * <pre>
 * &lt;simpleType name="stepType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="query"/>
 *     &lt;enumeration value="update"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "stepType")
@XmlEnum
public enum StepType {

    @XmlEnumValue("query")
    QUERY("query"),
    @XmlEnumValue("update")
    UPDATE("update");
    private final String value;

    StepType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StepType fromValue(String v) {
        for (StepType c: StepType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
