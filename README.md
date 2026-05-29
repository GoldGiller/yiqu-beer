# 调酒模拟器 - Cocktail Maker

一个基于Spring Boot + MySQL的全栈调酒平台，支持社交媒体互动、智能推荐、风味可视化和配方版本管理。

## 🍸 功能特色

### 核心功能
- **智能配方生成** - 根据心情和口味偏好自动生成专属鸡尾酒配方
- **材料选择系统** - 丰富的基酒、果汁、糖浆和装饰物选择（含emoji和详细描述）
- **口味定制** - 甜度、酸度、酒精浓度、果味强度四维可调节
- **心情匹配** - 六种心情模式：开心/失落/兴奋/浪漫/疲惫/庆祝
- **配方管理** - 创建、编辑、删除、分享个人配方

### 社交互动（新增）
- **楼中楼评论回复** - 支持嵌套回复（parent_id递归查询），可点赞、删除评论
- **用户个人主页** - 作品墙、动态流、风味报告，瀑布流布局展示
- **收藏/点赞/评分** - 完整的用户互动体系
- **关注系统** - 关注其他调酒师，查看好友动态

### 智能推荐（新增）
- **协同过滤推荐** - 基于用户交互矩阵（点赞/收藏/评分）的Item-based CF算法
- **季节/节日推荐** - 自动识别当前季节（春/夏/秋/冬）和节日（圣诞/情人节等）
- **食物搭配推荐** - 智能匹配小食（奶酪、甜点、海鲜等15种），基于口味和酒精度
- **用户行为记录** - user_recipe_interactions表记录所有交互用于推荐计算

### 可视化分析（新增）
- **风味雷达图** - ECharts绘制四维风味画像（甜/酸/酒/果），对比平台均值
- **个人风味报告** - 聚合所有配方的平均偏好，识别"甜味主导"/"烈酒主导"等风味类型
- **风味分布条形图** - 直观展示用户在四个维度上的偏好强度

### 配方高级功能（新增）
- **配方版本管理** - 快照保存/历史浏览/一键回滚，支持版本Diff对比
- **配方JSON导入导出** - FileReader解析JSON导入，Blob下载导出，便于社区分享
- **配方图片上传** - MultipartFile上传成品照片，自动校验格式和大小
- **配方详情页** - 独立页面展示材料/步骤/风味/评论/搭配

### 已有功能（保留）
- **用户系统** - 注册、登录、JWT认证、个人资料管理
- **搜索筛选** - 多维度搜索（名称/材料/心情/标签）
- **数据统计** - 用户行为分析和配方统计视图

## 🏗️ 技术架构

### 后端技术栈
- **Spring Boot 3.0** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问层
- **MySQL 8.0** - 数据库（支持CTE递归查询、存储过程）
- **Redis** - 缓存和会话存储
- **JWT** - 令牌认证
- **Swagger/OpenAPI** - API文档
- **Lombok** - 代码简洁

### 前端技术栈
- **HTML5/CSS3/JavaScript ES6** - 原生前端（无框架依赖）
- **Tailwind CSS** - 样式框架
- **ECharts 5** - 数据可视化（雷达图）
- **Anime.js** - 动画效果
- **P5.js** - 创意粒子背景
- **Splide.js** - 轮播组件

### 部署架构
- **Docker** - 容器化部署
- **Nginx** - 反向代理 + 静态资源
- **Linux** - 生产环境

## 📁 项目结构

```
cocktail-maker/
├── backend/                    # Spring Boot后端
│   ├── src/main/java/com/cocktailmaker
│   │   ├── entity/            # 16个实体类（Comment/CommentLike/RecipeVersion等）
│   │   ├── repository/        # 13个仓储接口（含复杂查询）
│   │   ├── service/           # 8个服务接口 + 6个实现
│   │   ├── controller/        # 8个控制器（100+ API端点）
│   │   ├── dto/               # 10个DTO类
│   │   └── enums/             # 6个枚举（ActivityType/SeasonType等）
│   ├── src/main/resources     # application.yml（3环境配置）
│   ├── pom.xml                # Maven配置（15个依赖）
│   └── Dockerfile             # Docker镜像
├── frontend/                  # 前端文件
│   └── js/                    # JS模块
│       ├── api-client.js      # API客户端（60+接口方法）
│       ├── comments.js        # 评论系统组件
│       ├── flavor-chart.js    # 雷达图组件
│       └── recipe-toolkit.js  # 导入/导出/版本管理工具
├── index.html                 # 主调酒创作页面
├── recipes.html               # 配方库浏览器
├── recipe-detail.html         # 配方详情页（评论/版本/搭配）
├── profile.html               # 个人主页（雷达图/动态流/推荐）
├── result.html                # 调酒结果展示
├── login.html                 # 登录注册
├── database-schema.sql        # 完整数据库脚本（650+行，25张表）
├── docker-compose.yml         # Docker Compose编排
├── nginx.conf                 # Nginx配置
└── README.md                  # 项目说明
```

## 🗃️ 数据库设计亮点

### 评论楼中楼
```sql
-- 递归CTE查询评论树（MySQL 8.0）
WITH RECURSIVE comment_tree AS (
    SELECT ..., 0 AS depth, CAST(id AS CHAR(200)) AS path
    FROM comments WHERE parent_id IS NULL
    UNION ALL
    SELECT ..., ct.depth + 1, CONCAT(ct.path, ',', c.id)
    FROM comments c JOIN comment_tree ct ON c.parent_id = ct.id
    WHERE ct.depth < 5  -- 最多5层嵌套
)
SELECT * FROM comment_tree ORDER BY path;
```

### 协同过滤存储过程
- `get_collaborative_recommendations(user_id, limit)` — 基于用户交互矩阵的协同过滤
- `get_comment_tree(recipe_id, user_id)` — 递归评论树查询
- `search_recipes(...)` — 动态SQL多条件搜索

### 核心表一览 (25张)
| 表名 | 说明 | 亮点 |
|------|------|------|
| comments + comment_likes | 楼中楼评论 | parent_id递归 + 点赞 |
| recipe_versions | 配方快照 | JSON存储完整配方状态 |
| user_activities | 用户动态 | 用于个人主页动态流和协同过滤 |
| food_pairings + recipe_food_pairings | 食物搭配 | 基于口味智能匹配 |
| seasonal_recommendations | 季节推荐池 | 支持10种季节/节日 |
| user_recipe_interactions | 交互矩阵 | 协同过滤核心数据源 |
| flavor_tags + recipe_flavor_tags | 风味标签 | 多维度风味标注 |
| user_similarities | 用户相似度缓存 | 优化推荐计算性能 |

## 🎯 API接口 (8大模块 100+端点)

### 认证接口
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录

### 评论接口（新增）
- `GET /api/comments/recipe/{recipeId}` - 分页获取评论树
- `POST /api/comments` - 创建一级评论
- `POST /api/comments/{id}/reply` - 楼中楼回复
- `POST /api/comments/{id}/like` - 点赞评论

### 用户接口（新增）
- `GET /api/users/{id}/profile` - 个人主页数据
- `GET /api/users/{id}/activities` - 动态流
- `GET /api/users/{id}/flavor-profile` - 风味雷达图数据

### 配方接口
- `GET/POST/PUT/DELETE /api/recipes` - 配方CRUD
- `GET /api/recipes/{id}/versions` - 版本历史
- `POST /api/recipes/{id}/versions/{v}/restore` - 版本回滚

### 推荐接口（新增）
- `GET /api/recommendations/collaborative` - 协同过滤推荐
- `GET /api/recommendations/seasonal` - 季节推荐
- `GET /api/recommendations/food-pairings/{recipeId}` - 食物搭配

### 上传接口（新增）
- `POST /api/uploads/image` - 上传图片
- `POST /api/uploads/recipe/{id}/image` - 上传配方成品照

## 🎨 前端页面

| 页面 | 说明 | 核心组件 |
|------|------|---------|
| index.html | 调酒创作 | 材料/心情/口味选择器，p5.js粒子背景 |
| recipes.html | 配方库 | 搜索/筛选/排序，导入导出按钮 |
| recipe-detail.html | 配方详情 | 评论楼中楼/版本管理/食物搭配/图片上传 |
| profile.html | 个人主页 | ECharts雷达图/瀑布流作品墙/动态流/推荐列表 |
| result.html | 调酒结果 | 配方展示/保存API同步/查看详情 |
| login.html | 登录注册 | JWT认证 |

## 🚀 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+ (前端开发)

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/GoldGiller/yiqu-beer.git
cd cocktail-maker
```

2. **启动数据库**
```bash
# 启动MySQL和Redis
docker-compose up -d mysql redis
```

3. **初始化数据库**
```bash
# 执行数据库脚本
mysql -u root -p < database-schema.sql
```

4. **启动后端**
```bash
cd backend
mvn spring-boot:run
```

5. **启动前端**
```bash
# 使用Python启动简单的HTTP服务器
cd frontend
python -m http.server 8000
```

6. **访问应用**
- 前端: http://localhost:8000
- 后端API: http://localhost:8080/api
- API文档: http://localhost:8080/api/swagger-ui.html

### Docker部署

1. **构建并启动**
```bash
docker-compose up -d
```

2. **查看日志**
```bash
docker-compose logs -f
```

3. **停止服务**
```bash
docker-compose down
```

## 🔧 配置说明

### 应用配置
编辑 `backend/src/main/resources/application.yml`:

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cocktail_maker
    username: your_username
    password: your_password

# Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password

# JWT配置
jwt:
  secret: your_jwt_secret_key
```

### 环境变量
```bash
# 数据库配置
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=cocktail_maker
export DB_USERNAME=your_username
export DB_PASSWORD=your_password

# Redis配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=your_password

# JWT配置
export JWT_SECRET=your_jwt_secret_key
```

## 📊 数据库设计

### 核心表
- **users** - 用户表
- **recipes** - 配方表
- **ingredients** - 材料表
- **recipe_ingredients** - 配方材料关联表
- **favorites** - 收藏表
- **ratings** - 评分表
- **comments** - 评论表

### 关系图
```
User (1) ----< (N) Recipe
Recipe (N) ---< (N) Ingredient
User (1) ----< (N) Favorite
User (1) ----< (N) Rating
Recipe (1) ---< (N) Comment
```

## 🎯 API接口

### 认证接口
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 配方接口
- `GET /api/recipes` - 获取配方列表
- `POST /api/recipes` - 创建配方
- `GET /api/recipes/{id}` - 获取配方详情
- `PUT /api/recipes/{id}` - 更新配方
- `DELETE /api/recipes/{id}` - 删除配方

### 社交接口
- `POST /api/recipes/{id}/like` - 点赞配方
- `POST /api/recipes/{id}/favorite` - 收藏配方
- `POST /api/recipes/{id}/rate` - 评分配方



## 🎨 前端页面

### 主要页面
- **index.html** - 主调酒页面
- **login.html** - 登录/注册页面
- **result.html** - 配方结果页面
- **recipes.html** - 配方库页面
- **profile.html** - 个人中心页面

### 核心组件
- **材料选择器** - 可视化材料选择
- **心情选择器** - 心情标签选择
- **口味调节器** - 四维度口味调节
- **配方生成器** - 智能配方生成算法
- **结果展示** - 配方详情展示

## 🔐 安全特性

- **JWT认证** - 无状态身份认证
- **密码加密** - BCrypt密码哈希
- **CORS配置** - 跨域请求控制
- **SQL注入防护** - 参数化查询
- **XSS防护** - 输入验证和过滤
- **Rate Limiting** - 请求频率限制

## 📈 性能优化

### 后端优化
- **数据库索引** - 关键字段索引优化
- **连接池** - HikariCP连接池
- **缓存策略** - Redis多级缓存
- **分页查询** - 大数据集分页
- **异步处理** - 非阻塞操作

### 前端优化
- **资源压缩** - CSS/JS压缩
- **图片优化** - WebP格式支持
- **懒加载** - 按需加载资源
- **CDN加速** - 静态资源CDN
- **缓存策略** - 浏览器缓存优化

## 🧪 测试

### 单元测试
```bash
cd backend
mvn test
```

### 集成测试
```bash
cd backend
mvn verify
```

### 前端测试
```bash
cd frontend
npm test
```

## 📱 移动端支持

- **响应式设计** - 适配各种屏幕尺寸
- **触摸优化** - 触摸友好的交互设计
- **PWA支持** - 渐进式Web应用
- **离线缓存** - Service Worker缓存

## 🌐 国际化

- **多语言支持** - 中英文切换
- **本地化格式** - 日期、时间、数字格式
- **文化适配** - 符合本地文化的设计

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目！

### 开发规范
- 遵循Spring Boot最佳实践
- 使用约定俗成的代码格式
- 编写单元测试
- 更新相关文档

## 📞 联系方式

- **项目维护者**: [GoldGiller]
- **邮箱**: [1755373406@QQ.com]
- **GitHub**: https://github.com/GoldGiller/

## 🙏 致谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Tailwind CSS](https://tailwindcss.com/)
- [Anime.js](https://animejs.com/)
- [P5.js](https://p5js.org/)

---

**享受调酒的乐趣！🍸**
