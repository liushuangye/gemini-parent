package com.gemini.toolkit.base;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author BHH
 */

public class BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	   /**
     * 备用字段1
     */
    private String preparation1;

    /**
     * 备用字段2
     */
    private String preparation2;

    /**
     * 备用字段3
     */
    private String preparation3;

    /**
     * 备用字段4
     */
    private String preparation4;

    /**
     * 备用字段5
     */
    private String preparation5;

    /**
     * 备用字段6
     */
    private String preparation6;

    /**
     * 备用字段7
     */
    private String preparation7;

    /**
     * 备用字段8
     */
    private String preparation8;

    /**
     * 备用字段9
     */
    private String preparation9;

    /**
     * 备用字段10
     */
    private String preparation10;

    /**
     * 排他键
     */
    private Integer updateKey;

    /**
     * 删除标志
     */
    private String deleteFlg;

    /**
     * 作成时间
     */
    private Date createDateTime;

    /**
     * 作成用户ID
     */
    private String createUserId;

    /**
     * 更新时间
     */
    private Date updateDateTime;

    /**
     * 更新用户ID
     */
    private String updateUserId;

    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * UUID
     */
    //@TableId(type = IdType.ASSIGN_UUID)
    private String uuid;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPreparation1() {
        return preparation1;
    }

    public void setPreparation1(String preparation1) {
        this.preparation1 = preparation1;
    }

    public String getPreparation2() {
        return preparation2;
    }

    public void setPreparation2(String preparation2) {
        this.preparation2 = preparation2;
    }

    public String getPreparation3() {
        return preparation3;
    }

    public void setPreparation3(String preparation3) {
        this.preparation3 = preparation3;
    }

    public String getPreparation4() {
        return preparation4;
    }

    public void setPreparation4(String preparation4) {
        this.preparation4 = preparation4;
    }

    public String getPreparation5() {
        return preparation5;
    }

    public void setPreparation5(String preparation5) {
        this.preparation5 = preparation5;
    }

    public String getPreparation6() {
        return preparation6;
    }

    public void setPreparation6(String preparation6) {
        this.preparation6 = preparation6;
    }

    public String getPreparation7() {
        return preparation7;
    }

    public void setPreparation7(String preparation7) {
        this.preparation7 = preparation7;
    }

    public String getPreparation8() {
        return preparation8;
    }

    public void setPreparation8(String preparation8) {
        this.preparation8 = preparation8;
    }

    public String getPreparation9() {
        return preparation9;
    }

    public void setPreparation9(String preparation9) {
        this.preparation9 = preparation9;
    }

    public String getPreparation10() {
        return preparation10;
    }

    public void setPreparation10(String preparation10) {
        this.preparation10 = preparation10;
    }

    public Integer getUpdateKey() {
        return updateKey;
    }

    public void setUpdateKey(Integer updateKey) {
        this.updateKey = updateKey;
    }

    public String getDeleteFlg() {
        return deleteFlg;
    }

    public void setDeleteFlg(String deleteFlg) {
        this.deleteFlg = deleteFlg;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "preparation1='" + preparation1 + '\'' +
                ", preparation2='" + preparation2 + '\'' +
                ", preparation3='" + preparation3 + '\'' +
                ", preparation4='" + preparation4 + '\'' +
                ", preparation5='" + preparation5 + '\'' +
                ", preparation6='" + preparation6 + '\'' +
                ", preparation7='" + preparation7 + '\'' +
                ", preparation8='" + preparation8 + '\'' +
                ", preparation9='" + preparation9 + '\'' +
                ", preparation10='" + preparation10 + '\'' +
                ", updateKey=" + updateKey +
                ", deleteFlg='" + deleteFlg + '\'' +
                ", createDateTime=" + createDateTime +
                ", createUserId='" + createUserId + '\'' +
                ", updateDateTime=" + updateDateTime +
                ", updateUserId='" + updateUserId + '\'' +
                ", id=" + id +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
