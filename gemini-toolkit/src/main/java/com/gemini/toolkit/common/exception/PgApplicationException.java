/*
 * (c) Hitachi, Ltd. 2020. All rights reserved.
 */
//## AutomaticGeneration

package com.gemini.toolkit.common.exception;

import lombok.ToString;

/**
 *
 * @author zhulan
 *
 */
@ToString(callSuper = true)
public class PgApplicationException extends PgException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * デフォルトコンストラクタ。<br>
     */
    public PgApplicationException() {
        super();
    }

    /**
     * 指定された詳細メッセージで初期化する。<br>
     * エラーコードは空。<br>
     *
     * @param errorMessage 詳細メッセージ
     */
    public PgApplicationException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 指定されたエラーコードと詳細メッセージで初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     */
    public PgApplicationException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 指定されたと詳細メッセージ、原因例外で初期化する。<br>
     *
     * @param errorMessage 詳細メッセージ
     * @param cause 原因例外
     */
    public PgApplicationException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    /**
     * 指定されたエラーコードと詳細メッセージ、原因例外で初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     * @param cause 原因例外
     */
    public PgApplicationException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    /**
     * 原因例外で初期化する。<br>
     * @param cause 原因例外
     */
    public PgApplicationException(Throwable cause) {
        super(cause);
    }
}
