package com.gemini.toolkit.sysparam.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * 获取系统配置的参数
 * @author BHH
 * @since 2021-05-08
 */

public class CodeForm {
	/**
	 * 类型
	 */
	@NotBlank( message="{sysparam.busId.notEmpty}")
	@Size(max=256,message="{sysparam.busId.lengthMaxError}")
	private String busId;
	
	/**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页显示条数
     */
    private Integer pagesize;

	public String getBusId() {
		return busId;
	}

	public void setBusId(String busId) {
		this.busId = busId;
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
		return "CodeForm{" +
				"busId='" + busId + '\'' +
				", currentPage=" + currentPage +
				", pagesize=" + pagesize +
				'}';
	}
}
