/*
 * (c) Hitachi, Ltd. 2020. All rights reserved.
 */
//## AutomaticGeneration

package com.gemini.toolkit.common.exception;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author zhulan
 *
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class PgErrorMessage implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** エラーコード */
    private String code;

    /** エラーメッセージ*/
    private String message;

    /** エラー項目*/
    private String property;
}

