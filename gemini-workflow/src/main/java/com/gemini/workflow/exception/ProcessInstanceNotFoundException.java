package com.gemini.workflow.exception;

/**
 * 自定义异常:流程实例未找到
 */
public class ProcessInstanceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public static final int code = 1001;
	public static final String msg = "流程实例不存在";

	public ProcessInstanceNotFoundException(String message) {
		super(message);
	}
}
