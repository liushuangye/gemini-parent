//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2021.05.24 时间 06:36:27 PM CST 
//


package com.gemini.toolkit.devops.xml.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.gemini.toolkit.devops.xml.model package.
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Name_QNAME = new QName("", "name");
    private final static QName _Type_QNAME = new QName("", "type");
    private final static QName _Desc_QNAME = new QName("", "desc");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.gemini.toolkit.devops.xml.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExecSqls }
     * 
     */
    public ExecSqls createExecSqls() {
        return new ExecSqls();
    }

    /**
     * Create an instance of {@link SqlScript }
     * 
     */
    public SqlScript createSqlScript() {
        return new SqlScript();
    }

    /**
     * Create an instance of {@link Sql }
     * 
     */
    public Sql createSql() {
        return new Sql();
    }

    /**
     * Create an instance of {@link RollbackSqls }
     * 
     */
    public RollbackSqls createRollbackSqls() {
        return new RollbackSqls();
    }

    /**
     * Create an instance of {@link ParamsOfWhere }
     * 
     */
    public ParamsOfWhere createParamsOfWhere() {
        return new ParamsOfWhere();
    }

    /**
     * Create an instance of {@link Param }
     * 
     */
    public Param createParam() {
        return new Param();
    }

    /**
     * Create an instance of {@link Steps }
     * 
     */
    public Steps createSteps() {
        return new Steps();
    }

    /**
     * Create an instance of {@link Step }
     * 
     */
    public Step createStep() {
        return new Step();
    }

    /**
     * Create an instance of {@link ParamsOfSet }
     * 
     */
    public ParamsOfSet createParamsOfSet() {
        return new ParamsOfSet();
    }

    /**
     * Create an instance of {@link BackupSqls }
     * 
     */
    public BackupSqls createBackupSqls() {
        return new BackupSqls();
    }

    /**
     * Create an instance of {@link Devops }
     * 
     */
    public Devops createDevops() {
        return new Devops();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createType(String value) {
        return new JAXBElement<String>(_Type_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "desc")
    public JAXBElement<String> createDesc(String value) {
        return new JAXBElement<String>(_Desc_QNAME, String.class, null, value);
    }

}
