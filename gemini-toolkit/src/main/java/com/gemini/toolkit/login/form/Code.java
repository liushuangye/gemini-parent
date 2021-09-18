package com.gemini.toolkit.login.form;


import com.gemini.toolkit.base.BaseEntity;

import lombok.Data;


public class Code extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	//l类型
	private String type;
	//代码
	private String code;
	//备注
	private String comment;
}
