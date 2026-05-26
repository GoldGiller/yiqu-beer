# 调酒模拟器系统架构设计

## 技术栈

### 后端
- **Spring Boot 3.0** - 主要框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问
- **MySQL 8.0** - 数据库
- **Redis** - 缓存和会话存储
- **JWT** - 令牌认证
- **Swagger/OpenAPI** - API文档

### 前端
- **HTML5/CSS3/JavaScript ES6** - 基础技术
- **Tailwind CSS** - 样式框架
- **Axios** - HTTP客户端
- **Anime.js** - 动画效果
- **P5.js** - 创意编程效果

### 部署
- **Docker** - 容器化
- **Nginx** - 反向代理
- **Linux** - 操作系统

## 系统架构

### 整体架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │   Nginx         │    │   Spring Boot   │
│   (Frontend)    │◄──►│   (Reverse      │◄──►│   (Backend)     │
│                 │    │   Proxy)        │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                                               ┌─────────────────┐
                                               │   MySQL DB      │
                                               │   Redis Cache   │
                                               └─────────────────┘
```

### 模块划分

#### 后端模块
1. **用户管理模块**
   - 用户注册/登录
   - 用户信息管理
   - 密码重置

2. **配方管理模块**
   - 创建配方
   - 查询配方
   - 更新配方
   - 删除配方

3. **材料管理模块**
   - 材料分类
   - 材料CRUD
   - 库存管理

4. **社交功能模块**
   - 收藏配方
   - 评分系统
   - 评论功能
   - 分享功能

5. **统计分析模块**
   - 用户行为分析
   - 配方统计
   - 热门推荐

#### 前端模块
1. **用户界面**
   - 登录/注册页面
   - 个人中心
   - 用户设置

2. **调酒界面**
   - 材料选择
   - 心情设置
   - 口味调整
   - 配方生成

3. **配方展示**
   - 配方详情
   - 制作步骤
   - 分享功能

4. **配方库**
   - 我的配方
   - 收藏配方
   - 浏览历史

## 数据库设计

### 核心表结构

#### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    avatar VARCHAR(255),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 配方表 (recipes)
```sql
CREATE TABLE recipes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL,
    mood VARCHAR(20) NOT NULL,
    image_url VARCHAR(255),
    sweetness INT DEFAULT 5,
    sourness INT DEFAULT 5,
    alcohol INT DEFAULT 5,
    fruitiness INT DEFAULT 5,
    is_public BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_mood (mood),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 材料表 (ingredients)
```sql
CREATE TABLE ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('BASE', 'JUICE', 'SYRUP', 'GARNISH', 'OTHER') NOT NULL,
    category VARCHAR(50),
    emoji VARCHAR(10),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_type (type),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 配方材料关联表 (recipe_ingredients)
```sql
CREATE TABLE recipe_ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    amount VARCHAR(50) NOT NULL,
    unit VARCHAR(20) DEFAULT 'ml',
    order_num INT DEFAULT 0,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 制作步骤表 (recipe_steps)
```sql
CREATE TABLE recipe_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(255),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 收藏表 (favorites)
```sql
CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_recipe (user_id, recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 评分表 (ratings)
```sql
CREATE TABLE ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_recipe (user_id, recipe_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 浏览历史表 (view_history)
```sql
CREATE TABLE view_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_viewed_at (viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## API设计

### 用户相关API
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/users/profile` - 获取用户信息
- `PUT /api/users/profile` - 更新用户信息
- `POST /api/users/change-password` - 修改密码

### 配方相关API
- `GET /api/recipes` - 获取配方列表
- `GET /api/recipes/{id}` - 获取配方详情
- `POST /api/recipes` - 创建配方
- `PUT /api/recipes/{id}` - 更新配方
- `DELETE /api/recipes/{id}` - 删除配方
- `GET /api/recipes/my` - 获取我的配方
- `GET /api/recipes/popular` - 获取热门配方
- `GET /api/recipes/recommended` - 获取推荐配方

### 材料相关API
- `GET /api/ingredients` - 获取材料列表
- `GET /api/ingredients/{id}` - 获取材料详情
- `POST /api/ingredients` - 创建材料（管理员）
- `PUT /api/ingredients/{id}` - 更新材料（管理员）
- `DELETE /api/ingredients/{id}` - 删除材料（管理员）

### 社交功能API
- `POST /api/favorites/{recipeId}` - 收藏配方
- `DELETE /api/favorites/{recipeId}` - 取消收藏
- `GET /api/favorites` - 获取收藏列表
- `POST /api/ratings` - 评分配方
- `PUT /api/ratings/{id}` - 更新评分
- `DELETE /api/ratings/{id}` - 删除评分

### 统计相关API
- `GET /api/stats/user/{userId}` - 获取用户统计
- `GET /api/stats/popular-ingredients` - 获取热门材料
- `GET /api/stats/popular-moods` - 获取热门心情
- `GET /api/stats/system` - 获取系统统计（管理员）

## 安全设计

### 认证机制
- JWT令牌认证
- Refresh Token机制
- 令牌过期时间控制

### 权限控制
- 基于角色的访问控制（RBAC）
- 用户权限验证
- 资源访问权限控制

### 数据安全
- 密码加密存储
- SQL注入防护
- XSS攻击防护
- CSRF防护

## 性能优化

### 缓存策略
- Redis缓存热点数据
- 数据库查询缓存
- 页面静态资源缓存

### 数据库优化
- 索引优化
- 分页查询
- 连接池管理

### 前端优化
- 资源压缩
- 图片懒加载
- CDN加速

## 监控和日志

### 应用监控
- 性能监控
- 错误监控
- 用户行为监控

### 日志管理
- 访问日志
- 错误日志
- 业务日志
- 审计日志

## 部署方案

### 开发环境
- 本地Docker部署
- 热重载和调试
- 单元测试和集成测试

### 生产环境
- 容器化部署
- 负载均衡
- 自动扩展
- 备份和恢复

这个架构设计确保了系统的可扩展性、可维护性和高性能，为用户提供了一个完整的调酒体验平台。