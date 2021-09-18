/*
 * (c) Hitachi, Ltd. 2020. All rights reserved.
 */
//## AutomaticGeneration

package com.gemini.toolkit.common.exception;

import java.util.List;
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
@ToString(callSuper = true)
public class PgInputCheckException extends PgApplicationException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** チェック編集用エラー情報 */
    private List<PgErrorMessage> errorList = null;

    /**
     * デフォルトコンストラクタ。<br>
     */
    public PgInputCheckException() {
        super();
    }

    /**
     * 指定された詳細メッセージで初期化する。<br>
     * エラーコードは空。<br>
     *
     * @param errorMessage 詳細メッセージ
     */
    public PgInputCheckException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * 指定されたエラーコードと詳細メッセージで初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     */
    public PgInputCheckException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 指定されたと詳細メッセージ、エラー情報で初期化する。<br>
     *
     * @param errorMessage 詳細メッセージ
     * @param errorList チェック・編集用エラー情報
     */
    public PgInputCheckException(String errorMessage, List<PgErrorMessage> errorList) {
        super(errorMessage);
        this.errorList = errorList;
    }

    /**
     * 指定されたエラーコードと詳細メッセージ、エラー情報で初期化する。<br>
     *
     * @param errorCode エラーコード
     * @param errorMessage 詳細メッセージ
     * @param errorList チェック・編集用エラー情報
     */
    public PgInputCheckException(String errorCode, String errorMessage, List<PgErrorMessage> errorList) {
        super(errorCode, errorMessage);
        this.errorList = errorList;
    }
}
