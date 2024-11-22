# ChipMatrix API Gateway

API网关服务，系统统一入口。基于Spring Cloud Gateway实现。

## 主要功能

### 1. 路由管理
- 统一入口，动态路由配置
- 基于服务发现的自动路由
- 路由匹配规则配置

### 2. 安全控制
- JWT Token统一验证
- 白名单路径配置
- 请求头信息清洗
- 用户信息传递

### 3. 流量控制
- 请求限流（基于Redis）
- 熔断降级
- 黑名单控制
- 防重放攻击

### 4. 跨域处理
- CORS配置
- 预检请求处理
- 允许域名配置

### 5. 日志管理
- 请求响应日志记录
- 调用链路追踪集成
- 异常信息采集

## 配置说明

~~~yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://chipmatrix-auth-center
          predicates:
            - Path=/auth/**
~~~


## 使用指南
1. 路由配置方式
2. 限流规则设置
3. 跨域配置说明
4. 日志级别调整

## 部署说明
- 推荐JDK版本：17
- 内存配置：建议2G以上
- 依赖服务：
  - Redis（限流）
  - Nacos（服务发现）