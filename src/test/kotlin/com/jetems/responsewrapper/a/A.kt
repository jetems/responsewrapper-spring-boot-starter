package com.jetems.responsewrapper.a

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class A {
    @GetMapping("/testa")
    fun test(): String {
        return "testa"
    }
}