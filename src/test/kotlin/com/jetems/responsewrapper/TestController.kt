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

    @GetMapping("/test3")
    fun test3(): Map<String, List<User>> {
        val userList = listOf(User("张三", 18), User("李四", 19))
        return mapOf("users" to userList)
    }

    @GetMapping("/bizerror")
    fun bizerror(): ResponseWrapper<*> {
        throw BizException(1001, "业务异常,参数错误")
    }

    @GetMapping("/syserror")
    fun syserror(): ResponseWrapper<*> {
        throw Exception("系统异常")
    }

    data class User(val name: String, val age: Int)
}