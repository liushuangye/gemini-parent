package com.gemini.workflow.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "返回响应数据")
public class RestMsg {

    @ApiModelProperty(value = "错误信息")
    private String msg;
    @ApiModelProperty(value = "状态码")
    private int code;
    @ApiModelProperty(value = "返回的数据")
    private Object data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static RestMsg success(String msg, Object data) {
        RestMsg restMsg = new RestMsg();
        restMsg.setCode(200);
        restMsg.setMsg(msg);
        restMsg.setData(data);
        return restMsg;
    }

    public static RestMsg fail(String msg, Object data) {
        RestMsg restMsg = new RestMsg();
        restMsg.setCode(500);
        restMsg.setMsg(msg);
        restMsg.setData(data);
        return restMsg;
    }
    public static RestMsg response(int code,String msg, Object data) {
        RestMsg restMsg = new RestMsg();
        restMsg.setCode(code);
        restMsg.setMsg(msg);
        restMsg.setData(data);
        return restMsg;
    }
}