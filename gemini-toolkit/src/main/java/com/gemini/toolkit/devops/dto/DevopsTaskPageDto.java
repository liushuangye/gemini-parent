package com.gemini.toolkit.devops.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;


public class DevopsTaskPageDto  implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 人物与名称
	 */
	private String taskName;
	
	/**
	 * 工具名称
	 */
	private String devopsName;
	

	/**
	 * 创建人
	 */
	private  String createUser;
	
	/**
	 * 创建时间
	 */
	private  Date createTime;
	
	/**
	 * 任务ID
	 */
	private Long id;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDevopsName() {
		return devopsName;
	}

	public void setDevopsName(String devopsName) {
		this.devopsName = devopsName;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DevopsTaskPageDto{" +
				"taskName='" + taskName + '\'' +
				", devopsName='" + devopsName + '\'' +
				", createUser='" + createUser + '\'' +
				", createTime=" + createTime +
				", id=" + id +
				'}';
	}
}
