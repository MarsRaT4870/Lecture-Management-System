<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">高校学术讲座与活动一体化管理系统</h1>
<h4 align="center">基于RuoYi框架 + MyBatis-Plus 的全流程活动管理平台</h4>
<h5 align="center">覆盖活动发布、报名、签到、反馈全流程，实现资源跨部门共享与数据可视化分析</h5>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Vue/stargazers"><img src="https://gitee.com/y_project/RuoYi-Vue/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue"><img src="https://img.shields.io/badge/RuoYi-v3.9.0-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/RuoYi-Vue/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 系统简介

本系统是基于**RuoYi v3.9.0**框架开发的高校学术讲座与活动一体化管理平台，旨在解决校园活动信息分散、师生参与渠道不畅、数据追踪困难等问题。

### 核心特性

* **全流程闭环管理**：从活动申报→审核→发布→报名→签到→评价→归档的完整业务流程
* **多主体申报体系**：支持校内部门、校外机构、教师个人三种主体发起活动申请
* **智能报名排队**：并发控制、候补队列、防重复报名、自动转正机制
* **智能签到核验**：动态二维码签到、人脸识别签到（接口预留）、迟到自动判定
* **数据可视化分析**：参与热力图、学科活跃度排名、反馈趋势统计、多维度数据大屏
* **自动化通知**：报名成功、候补转正、活动提醒（2小时前/1小时前）、变更预警
* **第二课堂学分**：自动发放学分、学分规则配置、学分排行榜、电子证书（待实现）
* **混合分页架构**：新模块使用MyBatis-Plus，旧模块保留PageHelper，平滑升级

### 技术架构

* **前端**：Vue 2.x + Element UI
* **后端**：Spring Boot 2.5.15 + Spring Security + MyBatis-Plus 3.5.3.1
* **数据库**：MySQL 5.7+
* **权限认证**：JWT + Redis，支持多终端认证
* **定时任务**：Quartz，支持活动状态自动更新、自动发放学分、自动通知
* **文件存储**：本地存储（可扩展OSS）

### 基础框架

本系统基于**RuoYi v3.9.0**开发，RuoYi是一套全部开源的快速开发平台。

* 提供了技术栈（[Vue3](https://v3.cn.vuejs.org) [Element Plus](https://element-plus.org/zh-CN) [Vite](https://cn.vitejs.dev)）版本[RuoYi-Vue3](https://gitcode.com/yangzongzhuan/RuoYi-Vue3)，保持同步更新。
* 提供了单应用版本[RuoYi-Vue-fast](https://gitcode.com/yangzongzhuan/RuoYi-Vue-fast)，Oracle版本[RuoYi-Vue-Oracle](https://gitcode.com/yangzongzhuan/RuoYi-Vue-Oracle)，保持同步更新。
* 不分离版本，请移步[RuoYi](https://gitee.com/y_project/RuoYi)，微服务版本，请移步[RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud)

## 系统功能模块

### 📢 活动发布与资源池模块
1. **多主体申报**：支持校内部门、校外机构、教师个人三种主体发起活动申请
2. **审核发布流程**：草稿→待审核→已发布/已驳回，支持审核意见
3. **资源池检索**：按活动类型、时间范围、关键字组合查询
4. **业务规则校验**：提前一周申报、报名截止时间校验、场地冲突检测

### 📝 智能报名与排队模块
5. **并发报名控制**：实时校验最大人数限制，防止超员
6. **候补队列机制**：名额已满时自动进入候补，支持自动转正
7. **防重复报名**：同一用户对同一活动仅能存在一条有效记录
8. **报名管理**：学生可查看所有报名记录，支持自行取消报名

### 🤳 现场签到与核验模块
9. **动态二维码签到**：管理员/教师端生成动态Token，支持刷新防转发
10. **人脸识别签到**：接口预留，支持接入AI服务（百度AI、腾讯云等）
11. **迟到判定**：系统自动计算，超过30分钟标记为迟到
12. **签到统计**：签到方式统计、准时率统计

### 📊 数据可视化驾驶舱
13. **学科活跃度排名**：按活动类型统计参与人数，柱状图展示
14. **活动类型占比**：学术讲座 vs 校园活动占比，环形图展示
15. **参与热力图**：按时间段统计参与人数，热力图展示
16. **反馈趋势统计**：按周统计评价数量和平均分，趋势图展示
17. **多维度统计**：活动状态分布、热门活动Top5、签到方式统计、迟到统计

### 🌟 评价与反馈模块
18. **学生评价**：已签到活动允许填写评价（星级打分+文字评论）
19. **评分报表**：教师/管理员可查看活动的平均评分统计
20. **敏感词过滤**：基础敏感词检测（可扩展专业词库）

### 🎓 第二课堂/学分认证模块
21. **学分规则配置**：按活动类型配置学分值（学术讲座0.5分、校园活动0.3分、竞赛2分）
22. **自动发放学分**：活动结束后自动为已签到用户发放学分
23. **学分查询**：我的总学分、学分记录列表、学分排行榜

### 🏟️ 场地资源管理模块
24. **场地管理**：场地列表、场地信息（名称、位置、容纳人数、设施描述）
25. **冲突检测**：发布活动时自动检测时间段冲突
26. **容量校验**：活动人数不能超过场地最大容量

### 📢 消息通知中心
27. **系统通知**：报名成功、候补转正、活动提醒、变更预警
28. **站内信**：消息列表、已读/未读状态、全部已读
29. **自动化通知**：定时任务自动发送活动提醒（2小时前/1小时前）

### 📂 讲座归档与回放模块
30. **资料上传**：PPT文件上传、视频链接设置、精彩瞬间照片上传
31. **资料下载**：PPT下载、下载次数统计、观看次数统计
32. **活动总结**：活动总结编辑、归档信息管理

### ⚙️ 若依基础功能（保留）
33. 用户管理、部门管理、岗位管理、菜单管理、角色管理
34. 字典管理、参数管理、通知公告
35. 操作日志、登录日志、在线用户
36. 定时任务、代码生成、系统接口
37. 服务监控、缓存监控、连接池监视

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 3.0+
- Node.js 14+

### 数据库初始化
1. 创建数据库（如：`lecture_management`）
2. 执行 `sql/ry_20250522.sql` 初始化若依基础数据
3. 执行 `sql/lecture.sql` 创建业务表结构

### 后端启动
```bash
# 进入后端目录
cd ruoyi-admin

# 修改数据库配置
# 编辑 ruoyi-admin/src/main/resources/application-druid.yml
# 修改数据库连接信息

# 启动项目
mvn spring-boot:run
# 或
java -jar ruoyi-admin.jar
```

### 前端启动
```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build:prod
```

### 默认账号
- 管理员：admin / admin123
- 普通用户：需在系统中创建

## 系统部署

### 生产环境部署
1. **后端部署**：
   ```bash
   mvn clean package
   # 生成的jar包在 ruoyi-admin/target/ruoyi-admin.jar
   java -jar ruoyi-admin.jar
   ```

2. **前端部署**：
   ```bash
   npm run build:prod
   # 将 dist 目录部署到 Nginx 或其他 Web 服务器
   ```

3. **Nginx 配置示例**：
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       root /path/to/dist;
       index index.html;
       
       location / {
           try_files $uri $uri/ /index.html;
       }
       
       location /prod-api/ {
           proxy_pass http://localhost:8080/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

### 定时任务配置
系统包含以下定时任务，需要在若依系统中配置：
- `ryActivityTask.autoFinishActivity` - 自动结束活动（建议每5分钟执行）
- `ryActivityTask.remindUser` - 活动开始前提醒（建议每30分钟执行）
- `ryActivityTask.autoArchiveActivity` - 自动归档过期活动（建议每天执行）
- `ryActivityTask.autoGrantCredits` - 自动发放学分（建议每小时执行）

## 系统文档

- 系统实现总结：`doc/系统实现总结.md`
- 完整功能清单：`doc/完整功能清单.md`
- 若依框架文档：http://doc.ruoyi.vip

## 系统架构

### 技术栈
- **前端框架**：Vue 2.x + Element UI
- **后端框架**：Spring Boot 2.5.15
- **ORM框架**：MyBatis-Plus 3.5.3.1（新模块）+ PageHelper（旧模块）
- **安全框架**：Spring Security + JWT
- **缓存**：Redis
- **定时任务**：Quartz
- **数据库**：MySQL 5.7+

### 混合分页架构
- **新业务模块**（活动/报名/学分等）：使用 MyBatis-Plus Page，代码简洁，性能更优
- **旧系统模块**（用户/角色等）：保留 PageHelper，确保若依原生功能稳定运行

### 数据库表结构
- `biz_activity` - 活动信息表
- `biz_registration` - 报名签到反馈表
- `biz_notification` - 通知记录表
- `biz_venue` - 场地资源表
- `biz_credit` - 学分认证表
- `biz_credit_rule` - 学分规则表
- `biz_activity_archive` - 活动归档表

## 核心API接口

### 活动管理
- `GET /biz/activity/list` - 查询活动列表
- `POST /biz/activity` - 新增活动
- `GET /biz/activity/statistics` - 获取统计数据
- `GET /biz/activity/heatmap` - 获取参与热力图
- `GET /biz/activity/feedback/trend` - 获取反馈趋势

### 报名管理
- `POST /biz/registration/apply` - 学生报名
- `POST /biz/registration/checkin` - 扫码签到
- `POST /biz/registration/checkin/face` - 人脸识别签到
- `POST /biz/registration/feedback` - 提交评价

### 学分管理
- `GET /biz/credit/myTotal` - 获取我的总学分
- `GET /biz/credit/ranking` - 获取学分排行榜

更多接口请参考：`doc/完整功能清单.md`

## 演示图

<table>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/cd1f90be5f2684f4560c9519c0f2a232ee8.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/1cbcf0e6f257c7d3a063c0e3f2ff989e4b3.jpg"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-8074972883b5ba0622e13246738ebba237a.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-9f88719cdfca9af2e58b352a20e23d43b12.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-39bf2584ec3a529b0d5a3b70d15c9b37646.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-936ec82d1f4872e1bc980927654b6007307.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-b2d62ceb95d2dd9b3fbe157bb70d26001e9.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-d67451d308b7a79ad6819723396f7c3d77a.png"/></td>
    </tr>	 
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/5e8c387724954459291aafd5eb52b456f53.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/644e78da53c2e92a95dfda4f76e6d117c4b.jpg"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-8370a0d02977eebf6dbf854c8450293c937.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-49003ed83f60f633e7153609a53a2b644f7.png"/></td>
    </tr>
	<tr>
        <td><img src="https://oscimg.oschina.net/oscnet/up-d4fe726319ece268d4746602c39cffc0621.png"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-c195234bbcd30be6927f037a6755e6ab69c.png"/></td>
    </tr>
    <tr>
        <td><img src="https://oscimg.oschina.net/oscnet/b6115bc8c31de52951982e509930b20684a.jpg"/></td>
        <td><img src="https://oscimg.oschina.net/oscnet/up-5e4daac0bb59612c5038448acbcef235e3a.png"/></td>
    </tr>
</table>


## 系统特色

### ✨ 全流程闭环
从活动申报→审核→发布→报名→签到→评价→归档的完整业务流程，实现数据全生命周期管理。

### 🤖 智能自动化
- 定时任务自动更新活动状态
- 活动开始前自动发送提醒（2小时前/1小时前）
- 活动结束后自动发放学分
- 自动归档过期活动

### 📊 数据可视化
- 参与热力图：按时间段统计参与人数
- 学科活跃度排名：按活动类型统计
- 反馈趋势统计：按周统计评价数据
- 多维度数据大屏：活动状态、热门活动、签到方式等

### 🔒 权限控制
基于若依RBAC的细粒度权限管理，支持：
- 学生角色：查看活动、报名、查看自己的记录
- 教师角色：申报活动、生成签到码、查看自己活动的数据
- 管理员：全审核权限、数据导出、全局统计查看

### 🚀 性能优化
- MyBatis-Plus Lambda表达式，代码简洁
- 数据库索引优化
- 分页查询优化
- 缓存支持（可扩展Redis缓存）

## 开发说明

### 代码结构
```
ruoyi-admin/src/main/java/com/ruoyi/biz/
├── controller/          # 控制器层
│   ├── BizActivityController.java
│   ├── BizRegistrationController.java
│   ├── BizCreditController.java
│   └── ...
├── service/            # 服务接口层
│   ├── IBizActivityService.java
│   └── ...
├── service/impl/       # 服务实现层
│   ├── BizActivityServiceImpl.java
│   └── ...
├── mapper/             # 数据访问层
│   ├── BizActivityMapper.java
│   └── ...
├── domain/entity/      # 实体类
│   ├── BizActivity.java
│   └── ...
└── task/               # 定时任务
    └── RyActivityTask.java
```

### 开发规范
1. **新模块使用MyBatis-Plus**：继承 `ServiceImpl<Mapper, Entity>`，使用 Lambda 表达式
2. **旧模块保留PageHelper**：使用 `startPage()` 开启分页
3. **统一异常处理**：使用 `ServiceException` 抛出业务异常
4. **事务管理**：关键业务方法使用 `@Transactional`
5. **日志记录**：关键操作使用 `@Log` 注解

## 常见问题

### Q: 如何配置定时任务？
A: 登录系统后，进入"系统监控"→"定时任务"，添加任务并配置Cron表达式。

### Q: 如何接入人脸识别服务？
A: 在 `BizRegistrationServiceImpl.checkInByFace()` 方法中接入AI服务API，如百度AI、腾讯云等。

### Q: 如何扩展通知渠道？
A: 在 `BizMessageService` 中扩展通知渠道，支持邮件、短信等。

### Q: 如何生成电子证书？
A: 可使用 iText 或 Apache PDFBox 生成PDF证书，在学分发放时调用。

## 更新日志

### v1.0.0 (2024-12)
- ✅ 完成活动发布与资源池模块
- ✅ 完成智能报名与排队模块
- ✅ 完成现场签到与核验模块
- ✅ 完成数据可视化驾驶舱
- ✅ 完成评价与反馈模块
- ✅ 完成第二课堂/学分认证模块
- ✅ 完成场地资源管理模块
- ✅ 完成消息通知中心
- ✅ 完成讲座归档与回放模块
- ✅ 完成定时任务系统

## 若依前后端分离交流群

QQ群： [![加入QQ群](https://img.shields.io/badge/已满-937441-blue.svg)](https://jq.qq.com/?_wv=1027&k=5bVB1og) [![加入QQ群](https://img.shields.io/badge/已满-887144332-blue.svg)](https://jq.qq.com/?_wv=1027&k=5eiA4DH) [![加入QQ群](https://img.shields.io/badge/已满-180251782-blue.svg)](https://jq.qq.com/?_wv=1027&k=5AxMKlC) [![加入QQ群](https://img.shields.io/badge/已满-104180207-blue.svg)](https://jq.qq.com/?_wv=1027&k=51G72yr) [![加入QQ群](https://img.shields.io/badge/已满-186866453-blue.svg)](https://jq.qq.com/?_wv=1027&k=VvjN2nvu) [![加入QQ群](https://img.shields.io/badge/已满-201396349-blue.svg)](https://jq.qq.com/?_wv=1027&k=5vYAqA05) [![加入QQ群](https://img.shields.io/badge/已满-101456076-blue.svg)](https://jq.qq.com/?_wv=1027&k=kOIINEb5) [![加入QQ群](https://img.shields.io/badge/已满-101539465-blue.svg)](https://jq.qq.com/?_wv=1027&k=UKtX5jhs) [![加入QQ群](https://img.shields.io/badge/已满-264312783-blue.svg)](https://jq.qq.com/?_wv=1027&k=EI9an8lJ) [![加入QQ群](https://img.shields.io/badge/已满-167385320-blue.svg)](https://jq.qq.com/?_wv=1027&k=SWCtLnMz) [![加入QQ群](https://img.shields.io/badge/已满-104748341-blue.svg)](https://jq.qq.com/?_wv=1027&k=96Dkdq0k) [![加入QQ群](https://img.shields.io/badge/已满-160110482-blue.svg)](https://jq.qq.com/?_wv=1027&k=0fsNiYZt) [![加入QQ群](https://img.shields.io/badge/已满-170801498-blue.svg)](https://jq.qq.com/?_wv=1027&k=7xw4xUG1) [![加入QQ群](https://img.shields.io/badge/已满-108482800-blue.svg)](https://jq.qq.com/?_wv=1027&k=eCx8eyoJ) [![加入QQ群](https://img.shields.io/badge/已满-101046199-blue.svg)](https://jq.qq.com/?_wv=1027&k=SpyH2875) [![加入QQ群](https://img.shields.io/badge/已满-136919097-blue.svg)](https://jq.qq.com/?_wv=1027&k=tKEt51dz) [![加入QQ群](https://img.shields.io/badge/已满-143961921-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=0vBbSb0ztbBgVtn3kJS-Q4HUNYwip89G&authKey=8irq5PhutrZmWIvsUsklBxhj57l%2F1nOZqjzigkXZVoZE451GG4JHPOqW7AW6cf0T&noverify=0&group_code=143961921) [![加入QQ群](https://img.shields.io/badge/已满-174951577-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=ZFAPAbp09S2ltvwrJzp7wGlbopsc0rwi&authKey=HB2cxpxP2yspk%2Bo3WKTBfktRCccVkU26cgi5B16u0KcAYrVu7sBaE7XSEqmMdFQp&noverify=0&group_code=174951577) [![加入QQ群](https://img.shields.io/badge/已满-161281055-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Fn2aF5IHpwsy8j6VlalNJK6qbwFLFHat&authKey=uyIT%2B97x2AXj3odyXpsSpVaPMC%2Bidw0LxG5MAtEqlrcBcWJUA%2FeS43rsF1Tg7IRJ&noverify=0&group_code=161281055) [![加入QQ群](https://img.shields.io/badge/已满-138988063-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=XIzkm_mV2xTsUtFxo63bmicYoDBA6Ifm&authKey=dDW%2F4qsmw3x9govoZY9w%2FoWAoC4wbHqGal%2BbqLzoS6VBarU8EBptIgPKN%2FviyC8j&noverify=0&group_code=138988063) [![加入QQ群](https://img.shields.io/badge/已满-151450850-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=DkugnCg68PevlycJSKSwjhFqfIgrWWwR&authKey=pR1Pa5lPIeGF%2FFtIk6d%2FGB5qFi0EdvyErtpQXULzo03zbhopBHLWcuqdpwY241R%2F&noverify=0&group_code=151450850) [![加入QQ群](https://img.shields.io/badge/已满-224622315-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=F58bgRa-Dp-rsQJThiJqIYv8t4-lWfXh&authKey=UmUs4CVG5OPA1whvsa4uSespOvyd8%2FAr9olEGaWAfdLmfKQk%2FVBp2YU3u2xXXt76&noverify=0&group_code=224622315) [![加入QQ群](https://img.shields.io/badge/已满-287842588-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=Nxb2EQ5qozWa218Wbs7zgBnjLSNk_tVT&authKey=obBKXj6SBKgrFTJZx0AqQnIYbNOvBB2kmgwWvGhzxR67RoRr84%2Bus5OadzMcdJl5&noverify=0&group_code=287842588) [![加入QQ群](https://img.shields.io/badge/已满-187944233-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=numtK1M_I4eVd2Gvg8qtbuL8JgX42qNh&authKey=giV9XWMaFZTY%2FqPlmWbkB9g3fi0Ev5CwEtT9Tgei0oUlFFCQLDp4ozWRiVIzubIm&noverify=0&group_code=187944233) [![加入QQ群](https://img.shields.io/badge/已满-228578329-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=G6r5KGCaa3pqdbUSXNIgYloyb8e0_L0D&authKey=4w8tF1eGW7%2FedWn%2FHAypQksdrML%2BDHolQSx7094Agm7Luakj9EbfPnSTxSi2T1LQ&noverify=0&group_code=228578329) [![加入QQ群](https://img.shields.io/badge/已满-191164766-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=GsOo-OLz53J8y_9TPoO6XXSGNRTgbFxA&authKey=R7Uy%2Feq%2BZsoKNqHvRKhiXpypW7DAogoWapOawUGHokJSBIBIre2%2FoiAZeZBSLuBc&noverify=0&group_code=191164766) [![加入QQ群](https://img.shields.io/badge/174569686-blue.svg)](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=PmYavuzsOthVqfdAPbo4uAeIbu7Ttjgc&authKey=p52l8%2FXa4PS1JcEmS3VccKSwOPJUZ1ZfQ69MEKzbrooNUljRtlKjvsXf04bxNp3G&noverify=0&group_code=174569686) 点击按钮入群。