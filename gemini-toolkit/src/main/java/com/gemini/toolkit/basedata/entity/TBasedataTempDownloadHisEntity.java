package com.gemini.toolkit.basedata.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *基础数据模板下载履历
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_tk_basedata_template_ver")
@KeySequence("t_tk_basedata_template_ver_seq")
public class TBasedataTempDownloadHisEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 模板类型
	 */
	private String templateType;

	/**
	 * 模板名称
	 */
	private String tempalteName;

	/**
	 * 静态版本
	 */
	private String staticVersion;

	/**
	 * 动态版本
	 */
	private String dynamicVersion;

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getTempalteName() {
		return tempalteName;
	}

	public void setTempalteName(String tempalteName) {
		this.tempalteName = tempalteName;
	}

	public String getStaticVersion() {
		return staticVersion;
	}

	public void setStaticVersion(String staticVersion) {
		this.staticVersion = staticVersion;
	}

	public String getDynamicVersion() {
		return dynamicVersion;
	}

	public void setDynamicVersion(String dynamicVersion) {
		this.dynamicVersion = dynamicVersion;
	}

	@Override
	public String toString() {
		return "TBasedataTempDownloadHisEntity{" +
				"templateType='" + templateType + '\'' +
				", tempalteName='" + tempalteName + '\'' +
				", staticVersion='" + staticVersion + '\'' +
				", dynamicVersion='" + dynamicVersion + '\'' +
				'}';
	}
}
