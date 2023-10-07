package com.jetems.responsewrapper

import java.io.Serializable

/**
 * 业务异常
 * @param errorCode 错误码
 * @param errorMessage 错误信息
 */
class BizException : RuntimeException, Serializable {
    var errorCode: Int
    var errorMessage: String

    constructor(errorCode: Int, errorMessage: String) : super(errorMessage) {
        this.errorCode = errorCode
        this.errorMessage = errorMessage
    }

}