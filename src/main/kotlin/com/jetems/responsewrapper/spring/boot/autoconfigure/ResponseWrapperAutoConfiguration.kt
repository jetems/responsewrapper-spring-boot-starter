package com.jetems.responsewrapper.spring.boot.autoconfigure

import com.jetems.responsewrapper.spring.boot.autoconfigure.properties.ResponseWrapperProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(ResponseWrapperProperties::class)
@ConditionalOnExpression("\${response.wrapper.enable:true}")
class ResponseWrapperAutoConfiguration {
}