package com.jetems.responsewrapper.spring.boot.autoconfigure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "response.wrapper")
data class ResponseWrapperProperties(
    var enable: Boolean = true,
    var excludePackages: List<String> = listOf(),
    var errorCodes: Map<Int, String> = mapOf()
)