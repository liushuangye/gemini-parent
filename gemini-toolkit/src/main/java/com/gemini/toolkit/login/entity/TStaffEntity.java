package com.gemini.toolkit.login.entity;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gemini.toolkit.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 人员
 * </p>
 *
 * @author BHH
 * @since 2021-05-06
 */

@EqualsAndHashCode(callSuper = true)
@TableName("t_staff")
@KeySequence("t_staff_seq")
public class TStaffEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 人员ID
     */
    private String staffId;

    /**
     * 人员姓名
     */
    private String staffName;

    /**
     * 性别
     */
    private String sexType;

    /**
     * 出生年月日
     */
    private String birthday;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 固话
     */
    private String telephone;

    /**
     * GCP资格
     */
    private String gcpFlg;

    /**
     * GCP资格级别
     */
    private String gcpLevel;

    /**
     * GCP资格证书号
     */
    private String gcpCertificateNo;

    /**
     * GCP资格取得日
     */
    private String gcpGetDate;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 专业
     */
    private String organizeId;

    /**
     * 职务
     */
    private String positionType;

    /**
     * 学历
     */
    private String educationType;

    /**
     * 学位
     */
    private String degreeType;

    /**
     * 职称
     */
    private String titleType;

    /**
     * 人员所属单位
     */
    private String staffAddress;


    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getSexType() {
        return sexType;
    }

    public void setSexType(String sexType) {
        this.sexType = sexType;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGcpFlg() {
        return gcpFlg;
    }

    public void setGcpFlg(String gcpFlg) {
        this.gcpFlg = gcpFlg;
    }

    public String getGcpLevel() {
        return gcpLevel;
    }

    public void setGcpLevel(String gcpLevel) {
        this.gcpLevel = gcpLevel;
    }

    public String getGcpCertificateNo() {
        return gcpCertificateNo;
    }

    public void setGcpCertificateNo(String gcpCertificateNo) {
        this.gcpCertificateNo = gcpCertificateNo;
    }

    public String getGcpGetDate() {
        return gcpGetDate;
    }

    public void setGcpGetDate(String gcpGetDate) {
        this.gcpGetDate = gcpGetDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizeId() {
        return organizeId;
    }

    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    public String getEducationType() {
        return educationType;
    }

    public void setEducationType(String educationType) {
        this.educationType = educationType;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getTitleType() {
        return titleType;
    }

    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    @Override
    public String toString() {
        return "TStaffEntity{" +
                "staffId='" + staffId + '\'' +
                ", staffName='" + staffName + '\'' +
                ", sexType='" + sexType + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", telephone='" + telephone + '\'' +
                ", gcpFlg='" + gcpFlg + '\'' +
                ", gcpLevel='" + gcpLevel + '\'' +
                ", gcpCertificateNo='" + gcpCertificateNo + '\'' +
                ", gcpGetDate='" + gcpGetDate + '\'' +
                ", email='" + email + '\'' +
                ", organizeId='" + organizeId + '\'' +
                ", positionType='" + positionType + '\'' +
                ", educationType='" + educationType + '\'' +
                ", degreeType='" + degreeType + '\'' +
                ", titleType='" + titleType + '\'' +
                ", staffAddress='" + staffAddress + '\'' +
                '}';
    }
}
