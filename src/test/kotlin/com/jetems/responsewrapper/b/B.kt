package com.jetems.responsewrapper.b

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class B {
    @GetMapping("/testb")
    fun test(): String {
        return "testb"
    }
}