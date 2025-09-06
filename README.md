# 古力商城项目 (GuliMall)

一个基于SpringBoot + Spring Cloud + Vue的分布式电商项目。

## 项目结构

- `gulimall-auth` - 认证服务
- `gulimall-cart` - 购物车服务
- `gulimall-common` - 公共模块
- `gulimall-coupon` - 优惠券服务
- `gulimall-gateway` - 网关服务
- `gulimall-member` - 会员服务
- `gulimall-order` - 订单服务
- `gulimall-product` - 商品服务
- `gulimall-search` - 搜索服务
- `gulimall-seckill` - 秒杀服务
- `gulimall-third-party` - 第三方服务
- `gulimall-ware` - 仓储服务
- `renren-fast` - 后台管理系统
- `renren-fast-vue` - 后台管理前端
- `renren-generator` - 代码生成器

## 配置说明

### 第三方服务配置

在 `gulimall-third-party/src/main/resources/` 目录下：

1. 复制 `application.yml.example` 为 `application.yml`
2. 配置你的阿里云AccessKey等信息：
   - `ALIYUN_ACCESS_KEY`: 阿里云AccessKey ID
   - `ALIYUN_SECRET_KEY`: 阿里云AccessKey Secret
   - `ALIYUN_SMS_ACCESS_KEY`: 短信服务AccessKey ID
   - `ALIYUN_SMS_ACCESS_SECRET`: 短信服务AccessKey Secret

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 7.x
- RabbitMQ 3.8+

## 启动说明

1. 启动注册中心 Nacos
2. 启动各个微服务
3. 启动前端项目

## 注意事项

- 请不要将真实的AccessKey等敏感信息提交到版本控制系统
- 生产环境请使用环境变量或配置中心管理敏感配置