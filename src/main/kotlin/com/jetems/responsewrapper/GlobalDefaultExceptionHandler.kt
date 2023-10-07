package com.jetems.responsewrapper

import com.jetems.responsewrapper.spring.boot.autoconfigure.properties.ResponseWrapperProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException



@RestControllerAdvice
@ConditionalOnExpression("\${response.wrapper.enable:true}")
class GlobalDefaultExceptionHandler(private val resp: ResponseWrapperProperties) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun noHandlerFoundException(e: NoHandlerFoundException): ResponseWrapper<*> {
        val errorCode = 404
        return responseWrapper(errorCode)
    }

    @ExceptionHandler(BizException::class)
    @ResponseStatus(HttpStatus.OK)
    fun bizException(e: BizException): ResponseWrapper<*> {
        log.error(e.printStackTrace().toString())
        val errorCode = e.errorCode
        return responseWrapper(errorCode)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun exception(e: Exception): ResponseWrapper<*> {
        log.error("系统异常", e)
        val errorCode = 500
        return responseWrapper(errorCode)
    }

    // 根据 application.yml 中配置的错误码获取错误信息，如果没有配置，则提示“此错误码未定义”
    private fun responseWrapper(
        errorCode: Int,
    ): ResponseWrapper<*> {
        return if (resp.errorCodes[errorCode] != null) ResponseWrapper.error(
            errorCode,
            resp.errorCodes[errorCode].toString(), null
        )
        else ResponseWrapper.error(errorCode, "此错误码未定义", null)
    }

}