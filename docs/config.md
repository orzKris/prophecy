### prophecy.dev配置变更记录  


---
* 增加prophecy数据源配置  
```yaml
com:
  kris:
    datasource:
      prophecy:
        url: jdbc:mysql://localhost:3306/prophecy?useUnicode=true&characterEncoding=utf8&autoReconnect=true
        username: root
        password: root
```
---
* 增加日志配置  
```yaml
logging:
  level:
    com:
      kris:
        prophecy: debug
```

* 增加spring cloud配置
```yaml
cloud:
    consul:
      discovery:
        enabled: true
      host: 192.168.3.45
      enabled: true
```
---
* 增加语言识别请求路径配置
```yaml
#百度翻译语言识别
baidu:
  url: https://fanyi.baidu.com/langdetect
```
---
* 增加经纬度地址解析请求配置
```yaml
# 经纬度地址解析
address:
  url: http://apis.juhe.cn/geo
  key: 2bc57d818e18d1cbe33748124b12dd10
```
---
* 增加人脸检测请求配置
```yaml
# 旷视人脸检测
face:
  url: https://api-cn.faceplusplus.com/facepp/v3/detect
  api_key: fPd1ep8kXcdKvcAXUPswDpTGxwkPNb3O
  api_secret: Ris2AFcKzjmqgUXXbi99bmPio0_lamBS
  return_attributes: gender,skinstatus,beauty,age,emotion
```
---
* 增加影讯合集请求配置
```yaml
# 影讯集合
movie:
  url: http://v.juhe.cn/movie/index
  key: 0d8f7a7d1cfb1c3e1275ec931b154fa2
```

---
* 增加kafka配置
```yaml
kafka:
  consumer:
    zookeeperConnect: 172.16.101.103:2181
    servers: 172.16.101.103:9092
    enableAutoCommit: true
    sessionTimeout: 6000
    autoCommitInterval: 100
    autoOffsetReset: latest
    topic: test
    groupId: test
    concurrency: 10

  producer:
    servers: 172.16.101.103:9092
    retries: 0
    batchSize: 4096
    linger: 1
    bufferMemory: 40960
```
---
* 增加redis,mongo配置
```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    password: kris0424
    blockWhenExhausted: true
    jedis:
      pool:
        maxActive: 1024
        maxIdle: 200
        minIdle: 0
        timeout: 10000
  data:
      mongodb:
        uri: mongodb://Kris:kris0424@localhost:27017/prophecy
  application:
    name: prophecy
```