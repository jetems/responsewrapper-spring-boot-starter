package com.jetems.responsewrapper

import com.jetems.responsewrapper.annotation.NotResponseWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController() {

    @GetMapping("/test")
    fun test(): String {
        return "responseWrapperProperties.enable.toString()"
    }

    @GetMapping("/test2")
    @NotResponseWrapper
    fun test2(): String {
        return "responseWrapperProperties.enable.toString()"
    }
}