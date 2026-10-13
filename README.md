# SmartCampusRecruitment - 校园智能招聘系统

## 📱 项目简介
一款面向校园招聘场景的全栈应用，采用 **Android客户端 + Spring Boot后端 + Web管理端** 三层架构，实现职位搜索、简历投递、智能推荐、数据可视化等完整招聘流程。

## 🛠 技术栈

### 前端（Android）
- **开发语言**：Java
- **网络通信**：Retrofit2 + OkHttp（类型安全的RESTful API请求 + 网络拦截）
- **页面导航**：Navigation Component（Fragment导航与页面跳转）
- **数据可视化**：MPAndroidChart（多类型图表渲染）
- **文件处理**：AndroidPdfViewer（在线PDF预览）、Apache POI + xdocreport（Office文档转PDF）
- **搜索优化**：pinyin4j（拼音模糊搜索）

### 后端（Spring Boot）
- **框架**：Spring Boot + Spring Security + Spring Data JPA
- **数据库**：MySQL
- **智能推荐**：协同过滤算法（基于用户 + 基于物品）
  - 改进皮尔逊相似度计算，加入用户行为与兴趣权重
    - Jaccard相似度计算岗位匹配
    - **简历生成**：Thymeleaf模板引擎 + Flying Saucer（HTML转PDF）
    - **数据导入**：Apache POI（Excel批量解析）
    - **Web管理端**：ECharts数据可视化

    ## ✨ 核心功能

    ### 🎓 求职者端
    - **职位搜索与筛选**：按城市、职位类型、薪资范围多维条件筛选，支持拼音模糊搜索
    - **一键投递申请**：填写求职意向、选择期望薪资、上传简历，一键提交
    - **成绩检索展示**：成绩单快速查询与可视化
    - **智能岗位推荐**：基于协同过滤算法的个性化岗位推荐，新用户热门岗位兜底
    - **在线简历生成**：自动生成标准化PDF简历，一键导出

    ### 🏢 招聘者端
    - **岗位发布管理**：发布、编辑、上下架职位
    - **简历查看**：查看投递简历与候选人信息
    - **沟通管理**：在线沟通候选人

    ### 📊 管理员Web端
    - **数据可视化大屏**：ECharts双轴柱状图 + 玫瑰饼图
    - **岗位投递分析**：Top10热门岗位投递量统计
    - **市场需求分析**：岗位发布量趋势与学生投递偏好分析
    - **数据导入**：批量导入成绩单等Excel数据

    ## 📂 项目结构
    ```
    SmartCampusRecruitment/
    ├── app/                    # Android客户端
    │   └── src/main/java/com/example/smartcampusrecruitment/
    │       ├── Adapter/        # 列表适配器
    │       ├── api/            # Retrofit网络请求层
    │       ├── model/          # 数据模型 + DTO
    │       ├── recruiter/      # 招聘者端模块
    │       ├── seeker/         # 求职者端模块
    │       └── view/          # 登录/注册/欢迎页
    ├── backend/                # Spring Boot后端
    │   └── src/main/java/com/example/demo/
    │       ├── controller/     # 接口控制层
    │       ├── service/        # 业务逻辑层
    │       ├── repository/      # 数据访问层
    │       ├── model/          # 实体类
    │       └── config/         # 配置类
    ├── gradle/                 # Gradle构建配置
    └── README.md
    ```

    ## 🚀 运行方式
    1. 克隆项目到本地
    2. **后端**：启动MySQL数据库，导入数据表，运行Spring Boot主类
    3. **Android端**：使用Android Studio打开项目，修改接口地址后运行

    ## 📝 备注
    本项目为毕业设计作品，完整实现了前后端分离的校园招聘系统，包含智能推荐算法与数据可视化分析。
    
