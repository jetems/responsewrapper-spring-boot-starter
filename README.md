# responsewrapper-spring-boot-starter

[![maven](https://img.shields.io/maven-central/v/com.jetems/responsewrapper-spring-boot-starter.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.jetems/responsewrapper-spring-boot-starter)
[![GitHub release](https://img.shields.io/github/v/release/jetems/responsewrapper-spring-boot-starter.svg)](https://github.com/jetems/responsewrapper-spring-boot-starter/releases)
[![JDK](https://img.shields.io/badge/JDK-17+-green.svg)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Apache 2.0](https://img.shields.io/github/license/jetems/responsewrapper-spring-boot-starter)](http://www.apache.org/licenses/LICENSE-2.0)

### 使用说明

1. 将“responsewrapper-spring-boot-starter”添加到 Spring Boot 项目中。

```Maven```

```xml

<dependency>
    <groupId>com.jetems</groupId>
    <artifactId>responsewrapper-spring-boot-starter</artifactId>
    <version>version</version>
</dependency>
```

```Gradle``` ```groovy```

```groovy
implementation 'com.jetems:responsewrapper-spring-boot-starter:version'
```

```Gradle``` ```kotlin```

```kotlin
implementation("com.jetems:responsewrapper-spring-boot-starter:version")
```

2. ```application.yml``` 配置

```yaml
response:
  wrapper:
    enable: true
    #    排除不需要统一返回的包，例如可以排除swagger的包、graphql的包
    exclude-packages:
      - com.jetems.responsewrapper.a
    #   错误码定义,将业务异常码和消息定义在这里
    error-codes:
      404: Resource not found
      500: Internal Server Error
      1001: 业务异常，参数错误
    #   定义成功码和成功信息
    success-code: 0
    success-message: ok

# 如果需要捕获404错误，需要在application.yml中添加如下配置：
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
```

3. 业务代码中使用 ```@NotResponseWrapper``` 注解，可以不使用统一返回

```kotlin
@GetMapping("/test2")
@NotResponseWrapper
fun test2(): String {
    return "responseWrapperProperties.enable.toString()"
}
```

4. 业务异常处理

```kotlin
@GetMapping("/bizerror")
fun bizerror(): ResponseWrapper<*> {
    throw BizException(1001, "业务异常,参数错误")
}
```

5. 建议返回的数据使用 ```map``` 包装，给数据一个有意义的key，这样可以让 data 里面的数据更有意义

```kotlin
@GetMapping("/test3")
fun test3(): Map<String, List<User>> {
    val userList = listOf(User("张三", 18), User("李四", 19))
    return mapOf("users" to userList)
}
```