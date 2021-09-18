package com.gemini.toolkit.login.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
@KeySequence("t_user_seq")
public class TUserEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 人员ID
     */
    private String staffId;

    /**
     * 登录密码
     */
    private String passwd;

    /**
     * 启用标志
     */
    private String enableFlg;

    /**
     * 短信验证标识（1：不需要短信验证 0/null：需要短信验证）
     */
    private String smscheckFlg;

    private String userSource;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEnableFlg() {
        return enableFlg;
    }

    public void setEnableFlg(String enableFlg) {
        this.enableFlg = enableFlg;
    }

    public String getSmscheckFlg() {
        return smscheckFlg;
    }

    public void setSmscheckFlg(String smscheckFlg) {
        this.smscheckFlg = smscheckFlg;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    @Override
    public String toString() {
        return "TUserEntity{" +
                "userId='" + userId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", passwd='" + passwd + '\'' +
                ", enableFlg='" + enableFlg + '\'' +
                ", smscheckFlg='" + smscheckFlg + '\'' +
                ", userSource='" + userSource + '\'' +
                '}';
    }
}
