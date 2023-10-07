package com.jetems.responsewrapper

data class ResponseWrapper<T>(
    val code: Int,
    val msg: String,
    val data: T?
) {
    companion object {
        fun <T> success(code: Int, msg: String, data: T?): ResponseWrapper<T> {
            return ResponseWrapper(code, msg, data)
        }

        fun <T> error(code: Int, msg: String, data: T? = null): ResponseWrapper<T> {
            return ResponseWrapper(code, msg, data)
        }
    }
}
