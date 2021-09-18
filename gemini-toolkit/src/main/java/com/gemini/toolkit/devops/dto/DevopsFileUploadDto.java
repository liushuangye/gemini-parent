package com.gemini.toolkit.devops.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


public class DevopsFileUploadDto  implements Serializable {

	
	private static final long serialVersionUID = -7238977303899035495L;

	private long id;
	/**
	 * 工具集名称
	 */
	private String devopsName;
	
	/**
	 * 功能描述
	 */
	private String deptdesc;
	
	/**
	 * xml文件
	 */
	// AOP拦截日志时，MultipartFile不能被序列化，所以此处需要设置MultipartFile不序列化
	@JSONField(serialize = false)
	private MultipartFile xml;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDevopsName() {
		return devopsName;
	}

	public void setDevopsName(String devopsName) {
		this.devopsName = devopsName;
	}

	public String getDeptdesc() {
		return deptdesc;
	}

	public void setDeptdesc(String deptdesc) {
		this.deptdesc = deptdesc;
	}

	public MultipartFile getXml() {
		return xml;
	}

	public void setXml(MultipartFile xml) {
		this.xml = xml;
	}

	@Override
	public String toString() {
		return "DevopsFileUploadDto{" +
				"id=" + id +
				", devopsName='" + devopsName + '\'' +
				", deptdesc='" + deptdesc + '\'' +
				", xml=" + xml +
				'}';
	}
}
