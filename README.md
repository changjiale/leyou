# 全品类电商微服务项目

 基于springboot和spring cloud实现的分布式全品类电商项目

项目分为3个项目 

1.[leyou-portal](https://github.com/changjiale/leyou/tree/master/leyou-portal)  --客户端门户

2.[leyou-manage-web](https://github.com/changjiale/leyou/tree/master/leyou-manage-web)   --后台管理系统（前端）

3.[leyou-cloud](https://github.com/changjiale/leyou/tree/master/leyou-cloud)   --后台微服务(11个服务)



## 1、门户网站

门户系统面向的是用户，安全性很重要，而且搜索引擎对于单页应用并不友好。因此门户系统不再采用与后台系统类似的SPA（单页应用）。依然是前后端分离，不过前端的页面会使用独立的html，在每个页面中使用vue来做页面渲染。采用live-server热部署方式

部署方式  :

-  **live-server  --port=9002**



## 2、后台管理系统（前端）

### Build Setup

```
# install dependencies
npm install

# serve with hot reload at localhost:8080
npm run dev

# build for production with minification
npm run build

# build for production and view the bundle analyzer report
npm run build --report
```

### 

## 3、后台微服务

### 一、架构图

![img](https://img-blog.csdnimg.cn/20181212215151153.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

### 二、包含的微服务

#### 2.1、 网关微服务

架构图

![img](https://img-blog.csdnimg.cn/20181214102311483.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

不管是来自于客户端（PC或移动端）的请求，还是服务内部调用。一切对服务的请求都会经过Zuul这个网关，然后再由网关来实现 鉴权、动态路由等等操作。Zuul就是我们服务的统一入口。

#### 2.2、授权中心微服务

> 结合RSA的鉴权

![img](https://img-blog.csdnimg.cn/20181214104033784.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

- 首先利用RSA生成公钥和私钥。私钥保存在授权中心，公钥保存在Zuul和各个微服务
- 用户请求登录
- 授权中心校验，通过后用私钥对JWT进行签名加密
- 返回jwt给用户
- 用户携带JWT访问
- Zuul直接通过公钥解密JWT，进行验证，验证通过则放行
- 请求到达微服务，微服务直接用公钥解析JWT，获取用户信息，无需访问授权中心

> 授权中心的主要职责

1. 用户鉴权：接收用户的登录请求，通过用户中心的接口进行校验，通过后生成JWT。使用私钥生成JWT并返回
2. 服务鉴权：微服务间的调用不经过Zuul，会有风险，需要鉴权中心进行认证。原理与用户鉴权类似，但逻辑稍微复杂一些（未实现）。





#### 2.3、购物车微服务



> 功能需求

- 用户可以在登录状态下将商品添加到购物车

放入数据库

放入redis（采用）

- 用户可以在未登录状态下将商品添加到购物车
- 放入localstorage
- 用户可以使用购物车一起结算下单
- 用户可以查询自己的购物车
- 用户可以在购物车中修改购买商品的数量。
- 用户可以在购物车中删除商品。
- 在购物车中展示商品优惠信息
- 提示购物车商品价格变化

> 流程图

![img](https://img-blog.csdnimg.cn/20181214104500266.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

这幅图主要描述了两个功能：新增商品到购物车、查询购物车。

- 新增商品：
  - 判断是否登录
    - 是：则添加商品到后台Redis中
    - 否：则添加商品到本地的Localstorage
- 无论哪种新增，完成后都需要查询购物车列表：
  - 判断是否登录
    - 否：直接查询localstorage中数据并展示
    - 是：已登录，则需要先看本地是否有数据，
      - 有：需要提交到后台添加到redis，合并数据，而后查询
      - 否：直接去后台查询redis，而后返回

#### 2.4、页面静态化微服务

商品详情浏览量比较大，并发高，所以单独开启一个微服务用来展示商品详情，并且对其进行静态化处理，保存为静态html文件。在用户访问商品详情页面时，让nginx对商品请求进行监听，指向本地静态页面，如果本地没找到，才反向代理到页面详情微服务端口。 

#### 2.5、商品微服务

主要是对商品分类、品牌、商品的规格参数以及商品的CRUD，为后台管理提供各种接口。

#### 2.6、订单微服务

主要接口有：

- 创建订单
- 查询订单
- 更新订单状态
- 根据订单号生成微信付款链接
- 根据订单号查询支付状态



#### 2.7、注册中心

> 基本架构

![img](https://img-blog.csdnimg.cn/2018121514422783.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

- Eureka：就是服务注册中心（可以是一个集群），对外暴露自己的地址
- 提供者：启动后向Eureka注册自己信息（地址，提供什么服务）
- 消费者：向Eureka订阅服务，Eureka会将对应服务的所有提供者地址列表发送给消费者，并且定期更新
- 心跳(续约)：提供者定期通过http方式向Eureka刷新自己的状态

主要功能就是对各种服务进行管理。

#### 2.8、搜索微服务

主要是对Elasticsearch的应用，将所有商品数据封装好后添加到Elasticsearch的索引库中，然后进行搜索过滤，查询相应的商品信息。 

#### 2.9、短信微服务

因为系统中不止注册一个地方需要短信发送，因此将短信发送抽取为微服务：`leyou-sms-service`，凡是需要的地方都可以使用。

另外，因为短信发送API调用时长的不确定性，为了提高程序的响应速度，短信发送我们都将采用异步发送方式，即：

- 短信服务监听MQ消息，收到消息后发送短信。
- 其它服务要发送短信时，通过MQ通知短信微服务。
- 

#### 2.10、文件上传微服务

使用分布式文件系统FastDFS实现图片上传。

> FastDFS架构

![img](https://img-blog.csdnimg.cn/20181215150632856.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

FastDFS两个主要的角色：Tracker Server 和 Storage Server 。

- Tracker Server：跟踪服务器，主要负责调度storage节点与client通信，在访问上起负载均衡的作用，和记录storage节点的运行状态，是连接client和storage节点的枢纽。
- Storage Server：存储服务器，保存文件和文件的meta data（元数据），每个storage server会启动一个单独的线程主动向Tracker cluster中每个tracker server报告其状态信息，包括磁盘使用情况，文件同步情况及文件上传下载次数统计等信息
- Group：文件组，多台Storage Server的集群。上传一个文件到同组内的一台机器上后，FastDFS会将该文件即时同步到同组内的其它所有机器上，起到备份的作用。不同组的服务器，保存的数据不同，而且相互独立，不进行通信。
- Tracker Cluster：跟踪服务器的集群，有一组Tracker Server（跟踪服务器）组成。
- Storage Cluster ：存储集群，有多个Group组成。

> 上传流程

![img](https://img-blog.csdnimg.cn/2018121515081720.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

1. Client通过Tracker server查找可用的Storage server。
2. Tracker server向Client返回一台可用的Storage server的IP地址和端口号。
3. Client直接通过Tracker server返回的IP地址和端口与其中一台Storage server建立连接并进行文件上传。
4. 上传完成，Storage server返回Client一个文件ID，文件上传结束。

> 下载流程

![img](https://img-blog.csdnimg.cn/20181215150912300.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5ajIwMThneXE=,size_16,color_FFFFFF,t_70)

1. Client通过Tracker server查找要下载文件所在的的Storage server。
2. Tracker server向Client返回包含指定文件的某个Storage server的IP地址和端口号。
3. Client直接通过Tracker server返回的IP地址和端口与其中一台Storage server建立连接并指定要下载文件。
4. 下载文件成功。

#### 2.11、用户中心微服务

- 提供的接口：
  - 检查用户名和手机号是否可用
  - 发送短信验证码
  - 用户注册
  - 用户查询
  - 修改用户个人资料



4.启动项目

在虚拟机中进行以下中间件的配置：

- ES：搜索
- FDFS：文件上传
- nginx：代理FDFS中的图片及静态图片
- Rabbitmq：数据同步
- Redis：缓存

并将配置文件中所有和虚拟机相关的ip进行修改

本机中需要的配置：

- nginx：前端所有请求统一代理到网关，域名的反向代理

  ```
  server {
          listen       80;
          server_name  www.leyou.com;
  
          proxy_set_header X-Forwarded-Host $host;
          proxy_set_header X-Forwarded-Server $host;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  		proxy_set_header Host $host;
  		
  		
  		location /item {
          # 先找本地
          root html;
          if (!-f $request_filename) { #请求的文件不存在，就反向代理
              proxy_pass http://127.0.0.1:8084;
              break;
          }
      }
          location / {
  			proxy_pass http://127.0.0.1:9002;
  			proxy_connect_timeout 600;
  			proxy_read_timeout 600;
          }
      }
  	server {
          listen       9001;
          server_name  manage.leyou.com;
  		
  		location / {
  			proxy_pass http://127.0.0.1:9001;
          }
  	}
  	server {
          listen       80;
          server_name  manage.leyou.com;
  
          proxy_set_header X-Forwarded-Host $host;
          proxy_set_header X-Forwarded-Server $host;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  
          location / {
  			proxy_pass http://127.0.0.1:9001;
  			proxy_connect_timeout 600;
  			proxy_read_timeout 600;
          }
      }
  	
  	server {
          listen       80;
          server_name  api.leyou.com;
  		
  		proxy_set_header Host $host;
          proxy_set_header X-Forwarded-Host $host;
          proxy_set_header X-Forwarded-Server $host;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  		
  		
  		location /api/upload {	
  		
  			rewrite "^/(.*)$" /zuul/$1; 
          }
  		
          location / {
  			proxy_pass http://127.0.0.1:10010;
  			proxy_connect_timeout 600;
  			proxy_read_timeout 600;
          }
      }
  ```

  

- host：实现域名访问

  ```
  #leyou-demo
  127.0.0.1 www.leyou.com
  127.0.0.1 manage.leyou.com
  127.0.0.1 api.leyou.com
  192.168.10.128 image.leyou.com
  ```

  

