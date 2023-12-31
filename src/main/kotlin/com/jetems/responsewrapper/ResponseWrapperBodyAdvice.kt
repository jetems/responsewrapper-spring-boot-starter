package com.jetems.responsewrapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jetems.responsewrapper.annotation.NotResponseWrapper
import com.jetems.responsewrapper.spring.boot.autoconfigure.properties.ResponseWrapperProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.Method

@RestControllerAdvice
@ConditionalOnExpression("\${response.wrapper.enable:true}")
class ResponseWrapperBodyAdvice(private val resp: ResponseWrapperProperties) :
    ResponseBodyAdvice<Any> {
    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        val method: Method? = returnType.method
        // 如果方法上有NotResponseWrapper注解，则不包装
        if (method != null) {
            val annotation = method.getAnnotation(NotResponseWrapper::class.java)
            if (annotation != null) {
                return false
            }
        }
        // 如果设置了excludePackages，则不包装
        if (resp.excludePackages.isNotEmpty()) {
            val packageName = returnType.declaringClass.`package`.name
            if (resp.excludePackages.contains(packageName)) {
                return false
            }
        }
        return resp.enable
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        return when (body) {
            is String -> jacksonObjectMapper().writeValueAsString(
                ResponseWrapper.success(
                    resp.successCode,
                    resp.successMessage,
                    body
                )
            )

            is ResponseWrapper<*> -> body
            else -> ResponseWrapper.success(resp.successCode, resp.successMessage, body)
        }
    }


}


