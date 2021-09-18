//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2021.05.24 时间 06:36:27 PM CST 
//


package com.gemini.toolkit.devops.xml.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}desc"/>
 *         &lt;element ref="{}paramsOfWhere"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{}paramsOfSet"/>
 *         &lt;/sequence>
 *         &lt;element ref="{}execSqls"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{}backupSqls"/>
 *           &lt;element ref="{}rollbackSqls"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="type" type="{}stepType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "desc",
    "paramsOfWhere",
    "paramsOfSet",
    "execSqls",
    "backupSqls",
    "rollbackSqls"
})
@XmlRootElement(name = "step")
public class Step {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlElement(required = true)
    protected String desc;
    @XmlElement(required = true)
    protected ParamsOfWhere paramsOfWhere;
    protected ParamsOfSet paramsOfSet;
    @XmlElement(required = true)
    protected ExecSqls execSqls;
    protected BackupSqls backupSqls;
    protected RollbackSqls rollbackSqls;
    @XmlAttribute(name = "id", required = true)
    protected Integer id;
    @XmlAttribute(name = "type")
    protected StepType type;

    /**
     * 获取name属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * 获取desc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置desc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * 获取paramsOfWhere属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ParamsOfWhere }
     *     
     */
    public ParamsOfWhere getParamsOfWhere() {
        return paramsOfWhere;
    }

    /**
     * 设置paramsOfWhere属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ParamsOfWhere }
     *     
     */
    public void setParamsOfWhere(ParamsOfWhere value) {
        this.paramsOfWhere = value;
    }

    /**
     * 获取paramsOfSet属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ParamsOfSet }
     *     
     */
    public ParamsOfSet getParamsOfSet() {
        return paramsOfSet;
    }

    /**
     * 设置paramsOfSet属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ParamsOfSet }
     *     
     */
    public void setParamsOfSet(ParamsOfSet value) {
        this.paramsOfSet = value;
    }

    /**
     * 获取execSqls属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ExecSqls }
     *     
     */
    public ExecSqls getExecSqls() {
        return execSqls;
    }

    /**
     * 设置execSqls属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ExecSqls }
     *     
     */
    public void setExecSqls(ExecSqls value) {
        this.execSqls = value;
    }

    /**
     * 获取backupSqls属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BackupSqls }
     *     
     */
    public BackupSqls getBackupSqls() {
        return backupSqls;
    }

    /**
     * 设置backupSqls属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BackupSqls }
     *     
     */
    public void setBackupSqls(BackupSqls value) {
        this.backupSqls = value;
    }

    /**
     * 获取rollbackSqls属性的值。
     * 
     * @return
     *     possible object is
     *     {@link RollbackSqls }
     *     
     */
    public RollbackSqls getRollbackSqls() {
        return rollbackSqls;
    }

    /**
     * 设置rollbackSqls属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link RollbackSqls }
     *     
     */
    public void setRollbackSqls(RollbackSqls value) {
        this.rollbackSqls = value;
    }

    /**
     * 获取id属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置id属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * 获取type属性的值。
     * 
     * @return
     *     possible object is
     *     {@link StepType }
     *     
     */
    public StepType getType() {
        return type;
    }

    /**
     * 设置type属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link StepType }
     *     
     */
    public void setType(StepType value) {
        this.type = value;
    }

}
