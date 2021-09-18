package com.gemini.toolkit.devops.dto;

import java.util.List;

import lombok.Data;


public class DevopsLogDetailsDto {

	/**
	 * 工具名称
	 */
	private String devopsToolName;
	
	/**
	 * 功能描述
	 */
	private String devopsDesc;
	
	/**
	 * 执行日志List
	 */
	private List<DevopsLogStepDto> stepList;

	public String getDevopsToolName() {
		return devopsToolName;
	}

	public void setDevopsToolName(String devopsToolName) {
		this.devopsToolName = devopsToolName;
	}

	public String getDevopsDesc() {
		return devopsDesc;
	}

	public void setDevopsDesc(String devopsDesc) {
		this.devopsDesc = devopsDesc;
	}

	public List<DevopsLogStepDto> getStepList() {
		return stepList;
	}

	public void setStepList(List<DevopsLogStepDto> stepList) {
		this.stepList = stepList;
	}

	@Override
	public String toString() {
		return "DevopsLogDetailsDto{" +
				"devopsToolName='" + devopsToolName + '\'' +
				", devopsDesc='" + devopsDesc + '\'' +
				", stepList=" + stepList +
				'}';
	}
}
