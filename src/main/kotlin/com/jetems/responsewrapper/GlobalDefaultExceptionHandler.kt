package com.jetems.responsewrapper

import com.jetems.responsewrapper.spring.boot.autoconfigure.properties.ResponseWrapperProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

data class ExceptionResponseWrapper(
    val code: Int?,
    val message: String?,
    val data: Any?
)

@RestControllerAdvice
@ConditionalOnExpression("\${response.wrapper.enable:true}")
class GlobalDefaultExceptionHandler(private val resp: ResponseWrapperProperties) {
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFoundException(e: NoHandlerFoundException): ExceptionResponseWrapper {
        val errorCode = 404
        return if (resp.errorCodes[errorCode] != null) ExceptionResponseWrapper(
            errorCode,
            resp.errorCodes[errorCode],
            e.message
        )
        else ExceptionResponseWrapper(errorCode, "此错误码未定义", e.message)
    }
}