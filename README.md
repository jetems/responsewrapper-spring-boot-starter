# responsewrapper-spring-boot-starter

### 文档

application.yml配置

```yaml
response:
  wrapper:
    enable: false
    #    排除不需要包装的包
    exclude-packages:
      - com.jetems.responsewrapper.a
    #   错误码定义
    error-codes:
      404: Resource not found
      500: Internal Server Error

# 对于springboot项目，如果需要捕获404错误，需要在application.yml中添加如下配置：
spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
```

