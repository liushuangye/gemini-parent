package com.gemini.toolkit.basedata.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 获取基础数据导入履历的参数
 * @author BHH
 * @since 2021-05-13
 */

public class ImportHisForm {
	/**
	 * 类型
	 */
	@NotBlank( message="{basedata.templateType.notEmpty}")
	@Size(max=256,message="{basedata.templateType.lengthMaxError}")
	private String templateType;

	/**
	 * 当前页
	 */
	private Integer currentPage;

	/**
	 * 每页显示条数
	 */
	private Integer pagesize;


	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	@Override
	public String toString() {
		return "ImportHisForm{" +
				"templateType='" + templateType + '\'' +
				", currentPage=" + currentPage +
				", pagesize=" + pagesize +
				'}';
	}
}
