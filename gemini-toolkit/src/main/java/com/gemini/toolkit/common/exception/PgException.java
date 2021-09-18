/*
 * (c) Hitachi, Ltd. 2020. All rights reserved.
 */
//## AutomaticGeneration

package com.gemini.toolkit.common.exception;

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
@ToString(exclude = "doneOutput")
public class PgException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** エラーコード */
    private String errorCode = null;

    /** エラーメッセージ */
    private String errorMessage = null;

    /** 出力済みフラグ */
    private boolean doneOutput = false;

    /**
     * デフォルトコンストラクタ。<br>
     */
    public PgException() {
        super();
    }

    /**
     * 指定された詳細メッセージで初期化する。<br>
     * エラーコードは空。<br>
     *
     * @param errorMessage 詳細メッセージ
     */
    public PgException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    /**
     * 指定されたエラーコードと詳細メッセージで初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     */
    public PgException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 指定されたと詳細メッセージ、原因例外で初期化する。<br>
     *
     * @param errorMessage 詳細メッセージ
     * @param cause 原因例外
     */
    public PgException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }

    /**
     * 指定されたエラーコードと詳細メッセージ、原因例外で初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     * @param cause 原因例外
     */
    public PgException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 原因例外で初期化する。<br>
     * @param cause 原因例外
     */
    public PgException(Throwable cause) {
        super(cause);
    }
}
