package com.gemini.toolkit.basedata.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;

/**
 * <p>
 * 基础数据导入履历
 * </p>
 *
 * @author BHH
 * @since 2021-05-13
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_tk_basedata_import_his")
@KeySequence("t_tk_basedata_import_his_seq")
public class TBasedataImportHisEntity extends BaseEntity {

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
	 * 导入状态
	 */
	private String state;

	/**
	 * 导入文件
	 */
	private byte[] fileContent;

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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@Override
	public String toString() {
		return "TBasedataImportHisEntity{" +
				"templateType='" + templateType + '\'' +
				", tempalteName='" + tempalteName + '\'' +
				", state='" + state + '\'' +
				", fileContent=" + Arrays.toString(fileContent) +
				'}';
	}
}
