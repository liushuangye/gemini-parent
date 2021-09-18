package com.gemini.toolkit.devops.dto;

import java.io.Serializable;

import lombok.Data;


public class DevopsTaskDto  implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private String taskName;
	
	
	private Long devopsId;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Long getDevopsId() {
		return devopsId;
	}

	public void setDevopsId(Long devopsId) {
		this.devopsId = devopsId;
	}

	@Override
	public String toString() {
		return "DevopsTaskDto{" +
				"taskName='" + taskName + '\'' +
				", devopsId=" + devopsId +
				'}';
	}
}
