-- 调酒模拟器数据库设计
-- MySQL 8.0

-- 创建数据库
CREATE DATABASE IF NOT EXISTS cocktail_maker 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE cocktail_maker;

-- 用户表
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

-- 用户详情表
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    bio TEXT,
    location VARCHAR(100),
    website VARCHAR(255),
    birth_date DATE,
    preference_sweetness INT DEFAULT 5,
    preference_sourness INT DEFAULT 5,
    preference_alcohol INT DEFAULT 5,
    preference_fruitiness INT DEFAULT 5,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 配方表
CREATE TABLE recipes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL,
    mood VARCHAR(20) NOT NULL,
    image_url VARCHAR(255),
    sweetness INT DEFAULT 5 CHECK (sweetness >= 1 AND sweetness <= 10),
    sourness INT DEFAULT 5 CHECK (sourness >= 1 AND sourness <= 10),
    alcohol INT DEFAULT 5 CHECK (alcohol >= 1 AND alcohol <= 10),
    fruitiness INT DEFAULT 5 CHECK (fruitiness >= 1 AND fruitiness <= 10),
    is_public BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_mood (mood),
    INDEX idx_created_at (created_at),
    INDEX idx_like_count (like_count),
    FULLTEXT idx_name_desc (name, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 材料分类表
CREATE TABLE ingredient_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 材料表
CREATE TABLE ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type ENUM('BASE', 'JUICE', 'SYRUP', 'GARNISH', 'OTHER') NOT NULL,
    category_id BIGINT,
    emoji VARCHAR(10),
    description TEXT,
    alcohol_content DECIMAL(3,1) DEFAULT 0.0,
    calories_per_100ml INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES ingredient_categories(id),
    INDEX idx_type (type),
    INDEX idx_category_id (category_id),
    FULLTEXT idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 配方材料关联表
CREATE TABLE recipe_ingredients (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    amount VARCHAR(50) NOT NULL,
    unit VARCHAR(20) DEFAULT 'ml',
    order_num INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_ingredient_id (ingredient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 制作步骤表
CREATE TABLE recipe_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    step_number INT NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(255),
    duration_seconds INT DEFAULT 0,
    temperature VARCHAR(20),
    technique VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(7) DEFAULT '#d4af37',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 配方标签关联表
CREATE TABLE recipe_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_recipe_tag (recipe_id, tag_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
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

-- 评分表
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

-- 评论表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    content TEXT NOT NULL,
    like_count INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE,
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 浏览历史表
CREATE TABLE view_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_viewed_at (viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 点赞表
CREATE TABLE likes (
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

-- 通知表
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('LIKE', 'COMMENT', 'FAVORITE', 'FOLLOW', 'SYSTEM') NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    data JSON,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 关注表
CREATE TABLE follows (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_follow (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统配置表
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description TEXT,
    is_system BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 操作日志表
CREATE TABLE operation_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    operation_type VARCHAR(50) NOT NULL,
    operation_desc TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    request_url VARCHAR(500),
    request_method VARCHAR(10),
    request_params JSON,
    response_result JSON,
    execution_time INT DEFAULT 0,
    status ENUM('SUCCESS', 'FAILURE') DEFAULT 'SUCCESS',
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认数据

-- 材料分类
INSERT INTO ingredient_categories (name, description) VALUES
('基酒', '鸡尾酒的基础酒精饮料'),
('果汁', '新鲜水果榨取的汁液'),
('糖浆', '各种风味的甜味糖浆'),
('装饰物', '用于装饰鸡尾酒的材料'),
('其他', '其他辅助材料');

-- 基酒材料
INSERT INTO ingredients (name, type, category_id, emoji, description, alcohol_content) VALUES
('伏特加', 'BASE', 1, '🍸', '无色无味的蒸馏酒，适合调制各种鸡尾酒', 40.0),
('威士忌', 'BASE', 1, '🥃', '用谷物酿造并在橡木桶中陈酿的烈酒', 43.0),
('朗姆酒', 'BASE', 1, '🏴‍☠️', '用甘蔗糖蜜酿造的烈酒，带有甜味', 40.0),
('金酒', 'BASE', 1, '🌿', '以谷物为原料，加入杜松子等香料蒸馏而成', 40.0),
('龙舌兰', 'BASE', 1, '🌵', '墨西哥特产，用龙舌兰植物酿造', 38.0),
('白兰地', 'BASE', 1, '🍇', '用葡萄酿造的烈酒，口感醇厚', 40.0);

-- 果汁材料
INSERT INTO ingredients (name, type, category_id, emoji, description, calories_per_100ml) VALUES
('柠檬汁', 'JUICE', 2, '🍋', '新鲜柠檬榨取的汁液，酸味强烈', 25),
('青柠汁', 'JUICE', 2, '🟢', '青柠榨取的汁液，比柠檬更酸', 30),
('橙汁', 'JUICE', 2, '🍊', '新鲜橙子榨取的汁液，甜味适中', 45),
('西柚汁', 'JUICE', 2, '🌸', '西柚榨取的汁液，略带苦味', 40),
('菠萝汁', 'JUICE', 2, '🍍', '菠萝榨取的汁液，热带风味', 53),
('蔓越莓汁', 'JUICE', 2, '🫐', '蔓越莓榨取的汁液，酸甜可口', 46);

-- 糖浆材料
INSERT INTO ingredients (name, type, category_id, emoji, description) VALUES
('简单糖浆', 'SYRUP', 3, '🍯', '糖和水按1:1比例制成的糖浆'),
('香草糖浆', 'SYRUP', 3, '🌸', '加入香草风味的糖浆'),
('榛果糖浆', 'SYRUP', 3, '🌰', '加入榛果风味的糖浆'),
('红石榴糖浆', 'SYRUP', 3, '🍒', '红石榴风味的糖浆，颜色鲜艳'),
('薄荷糖浆', 'SYRUP', 3, '🌿', '加入薄荷风味的糖浆'),
('焦糖糖浆', 'SYRUP', 3, '🍮', '焦糖风味的糖浆，甜味浓郁');

-- 装饰物材料
INSERT INTO ingredients (name, type, category_id, emoji, description) VALUES
('柠檬片', 'GARNISH', 4, '🍋', '新鲜柠檬切片，用于装饰'),
('青柠片', 'GARNISH', 4, '🟢', '新鲜青柠切片，用于装饰'),
('薄荷叶', 'GARNISH', 4, '🌿', '新鲜薄荷叶，增加香气'),
('樱桃', 'GARNISH', 4, '🍒', '马拉斯奇诺樱桃，经典装饰'),
('橄榄', 'GARNISH', 4, '🫒', '鸡尾酒橄榄，常用于马天尼'),
('橙皮', 'GARNISH', 4, '🍊', '橙子皮削成的装饰');

-- 标签
INSERT INTO tags (name, description, color) VALUES
('经典', '经典鸡尾酒配方', '#d4af37'),
('创新', '创新独特的配方', '#b87333'),
('清爽', '清爽口感的鸡尾酒', '#4ade80'),
('浓郁', '浓郁口感的鸡尾酒', '#f59e0b'),
('果味', '果味浓郁的鸡尾酒', '#ef4444'),
('烈酒', '酒精度较高的鸡尾酒', '#8b5cf6'),
('无酒精', '不含酒精的鸡尾酒', '#06b6d4'),
('夏日', '适合夏日饮用的鸡尾酒', '#f97316'),
('冬日', '适合冬日饮用的鸡尾酒', '#3b82f6'),
('派对', '适合派对场合的鸡尾酒', '#ec4899');

-- 系统配置
INSERT INTO system_configs (config_key, config_value, description) VALUES
('site_name', '调酒模拟器', '网站名称'),
('site_description', '创造你的专属饮品', '网站描述'),
('max_recipes_per_user', '100', '每个用户最多可创建的配方数'),
('max_upload_size', '10485760', '最大上传文件大小（字节）'),
('allow_registration', 'true', '是否允许用户注册'),
('require_email_verification', 'false', '是否需要邮箱验证'),
('enable_social_login', 'false', '是否启用社交登录'),
('default_recipe_visibility', 'public', '默认配方可见性'),
('enable_comments', 'true', '是否启用评论功能'),
('enable_ratings', 'true', '是否启用评分功能');

-- 创建视图
CREATE VIEW recipe_statistics AS
SELECT 
    r.id,
    r.name,
    r.user_id,
    r.mood,
    r.created_at,
    r.view_count,
    r.like_count,
    r.favorite_count,
    r.comment_count,
    COALESCE(AVG(ratings.rating), 0) as avg_rating,
    COUNT(DISTINCT ratings.id) as rating_count
FROM recipes r
LEFT JOIN ratings ON r.id = ratings.recipe_id
WHERE r.is_public = TRUE
GROUP BY r.id;

CREATE VIEW user_statistics AS
SELECT 
    u.id,
    u.username,
    u.nickname,
    u.created_at,
    COUNT(DISTINCT r.id) as recipe_count,
    COUNT(DISTINCT f.id) as favorite_count,
    COUNT(DISTINCT ratings.id) as rating_given,
    SUM(r.view_count) as total_views,
    SUM(r.like_count) as total_likes
FROM users u
LEFT JOIN recipes r ON u.id = r.user_id
LEFT JOIN favorites f ON u.id = f.user_id
LEFT JOIN ratings ON u.id = ratings.user_id
GROUP BY u.id;

-- 创建触发器更新统计数据
DELIMITER //

CREATE TRIGGER after_recipe_like_insert 
AFTER INSERT ON likes
FOR EACH ROW
BEGIN
    UPDATE recipes SET like_count = like_count + 1 WHERE id = NEW.recipe_id;
END//

CREATE TRIGGER after_recipe_like_delete 
AFTER DELETE ON likes
FOR EACH ROW
BEGIN
    UPDATE recipes SET like_count = like_count - 1 WHERE id = OLD.recipe_id;
END//

CREATE TRIGGER after_favorite_insert 
AFTER INSERT ON favorites
FOR EACH ROW
BEGIN
    UPDATE recipes SET favorite_count = favorite_count + 1 WHERE id = NEW.recipe_id;
END//

CREATE TRIGGER after_favorite_delete 
AFTER DELETE ON favorites
FOR EACH ROW
BEGIN
    UPDATE recipes SET favorite_count = favorite_count - 1 WHERE id = OLD.recipe_id;
END//

CREATE TRIGGER after_comment_insert 
AFTER INSERT ON comments
FOR EACH ROW
BEGIN
    UPDATE recipes SET comment_count = comment_count + 1 WHERE id = NEW.recipe_id;
END//

CREATE TRIGGER after_comment_delete 
AFTER DELETE ON comments
FOR EACH ROW
BEGIN
    UPDATE recipes SET comment_count = comment_count - 1 WHERE id = OLD.recipe_id;
END//

DELIMITER ;

-- 创建存储过程
DELIMITER //

-- 获取推荐配方
CREATE PROCEDURE get_recommended_recipes(
    IN user_id BIGINT,
    IN limit_count INT
)
BEGIN
    SELECT r.*, u.username, u.nickname, u.avatar,
           COALESCE(AVG(ratings.rating), 0) as avg_rating,
           COUNT(DISTINCT ratings.id) as rating_count
    FROM recipes r
    JOIN users u ON r.user_id = u.id
    LEFT JOIN ratings ON r.id = ratings.recipe_id
    WHERE r.is_public = TRUE 
    AND r.id NOT IN (SELECT recipe_id FROM view_history WHERE user_id = user_id)
    GROUP BY r.id
    ORDER BY r.like_count DESC, r.view_count DESC, r.created_at DESC
    LIMIT limit_count;
END//

-- 搜索配方
CREATE PROCEDURE search_recipes(
    IN search_term VARCHAR(255),
    IN mood_filter VARCHAR(20),
    IN ingredient_filter VARCHAR(100),
    IN sort_by VARCHAR(20),
    IN offset_val INT,
    IN limit_val INT
)
BEGIN
    SET @sql = 'SELECT DISTINCT r.*, u.username, u.nickname, u.avatar FROM recipes r 
                JOIN users u ON r.user_id = u.id 
                LEFT JOIN recipe_ingredients ri ON r.id = ri.recipe_id 
                LEFT JOIN ingredients i ON ri.ingredient_id = i.id 
                WHERE r.is_public = TRUE';
    
    IF search_term IS NOT NULL AND search_term != '' THEN
        SET @sql = CONCAT(@sql, ' AND (r.name LIKE ''%', search_term, '%'' OR r.description LIKE ''%', search_term, '%'')');
    END IF;
    
    IF mood_filter IS NOT NULL AND mood_filter != 'all' THEN
        SET @sql = CONCAT(@sql, ' AND r.mood = ''', mood_filter, '''');
    END IF;
    
    IF ingredient_filter IS NOT NULL AND ingredient_filter != '' THEN
        SET @sql = CONCAT(@sql, ' AND i.name LIKE ''%', ingredient_filter, '%''');
    END IF;
    
    CASE sort_by
        WHEN 'newest' THEN SET @sql = CONCAT(@sql, ' ORDER BY r.created_at DESC');
        WHEN 'oldest' THEN SET @sql = CONCAT(@sql, ' ORDER BY r.created_at ASC');
        WHEN 'popular' THEN SET @sql = CONCAT(@sql, ' ORDER BY r.view_count DESC');
        WHEN 'liked' THEN SET @sql = CONCAT(@sql, ' ORDER BY r.like_count DESC');
        ELSE SET @sql = CONCAT(@sql, ' ORDER BY r.created_at DESC');
    END CASE;
    
    SET @sql = CONCAT(@sql, ' LIMIT ', limit_val, ' OFFSET ', offset_val);
    
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//

-- ============================================
-- 评论回复系统增强 (楼中楼)
-- ============================================
CREATE TABLE comment_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_comment (user_id, comment_id),
    INDEX idx_comment_id (comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 用户活动动态表
-- ============================================
CREATE TABLE user_activities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    activity_type ENUM('CREATE_RECIPE', 'LIKE_RECIPE', 'FAVORITE_RECIPE', 'COMMENT_RECIPE', 'RATE_RECIPE', 'FOLLOW_USER', 'VERSION_RESTORE') NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    summary TEXT,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at DESC),
    INDEX idx_activity_type (activity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 配方版本管理表
-- ============================================
CREATE TABLE recipe_versions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    version_number INT NOT NULL,
    snapshot_data JSON NOT NULL,
    change_summary TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_recipe_version (recipe_id, version_number),
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 食物搭配表
-- ============================================
CREATE TABLE food_pairings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    emoji VARCHAR(10),
    description TEXT,
    category VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE recipe_food_pairings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    food_pairing_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (food_pairing_id) REFERENCES food_pairings(id) ON DELETE CASCADE,
    UNIQUE KEY uk_recipe_food (recipe_id, food_pairing_id),
    INDEX idx_recipe_id (recipe_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 季节/节日推荐池表
-- ============================================
CREATE TABLE seasonal_recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    season ENUM('SPRING', 'SUMMER', 'AUTUMN', 'WINTER', 'CHRISTMAS', 'VALENTINE', 'HALLOWEEN', 'NEW_YEAR', 'NATIONAL_DAY', 'MOON_FESTIVAL') NOT NULL,
    priority INT DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    UNIQUE KEY uk_recipe_season (recipe_id, season),
    INDEX idx_season_priority (season, priority DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 用户配方交互矩阵表 (协同过滤)
-- ============================================
CREATE TABLE user_recipe_interactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    interaction_type ENUM('VIEW', 'LIKE', 'FAVORITE', 'COMMENT', 'RATE') NOT NULL,
    weight DECIMAL(3,1) DEFAULT 1.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_type (interaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户相似度缓存表
CREATE TABLE user_similarities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    similar_user_id BIGINT NOT NULL,
    similarity_score DECIMAL(5,4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (similar_user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_pair (user_id, similar_user_id),
    INDEX idx_user_score (user_id, similarity_score DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 风味标签表
-- ============================================
CREATE TABLE flavor_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    emoji VARCHAR(10),
    category ENUM('TASTE', 'AROMA', 'TEXTURE', 'STRENGTH'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE recipe_flavor_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    flavor_tag_id BIGINT NOT NULL,
    intensity INT DEFAULT 5 CHECK (intensity >= 1 AND intensity <= 10),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
    FOREIGN KEY (flavor_tag_id) REFERENCES flavor_tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_recipe_flavor (recipe_id, flavor_tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 种子数据: 食物搭配
-- ============================================
INSERT INTO food_pairings (name, emoji, description, category) VALUES
('奶酪拼盘', '🧀', '各种奶酪搭配饼干和水果', '西式'),
('巧克力布朗尼', '🍫', '浓郁的巧克力蛋糕', '甜点'),
('烤三文鱼', '🐟', '香草烤制的三文鱼', '海鲜'),
('牛排', '🥩', '煎烤牛排配黑胡椒酱', '肉类'),
('炸鸡翅', '🍗', '香脆可口的炸鸡翅', '小吃'),
('鲜虾沙拉', '🥗', '新鲜大虾搭配蔬菜沙拉', '沙拉'),
('意大利面', '🍝', '蒜香番茄意大利面', '主食'),
('提拉米苏', '🍰', '经典意大利咖啡甜点', '甜点'),
('生蚝', '🦪', '新鲜生蚝配柠檬汁', '海鲜'),
('烤羊排', '🍖', '香草烤羊排配薄荷酱', '肉类'),
('西班牙火腿', '🥓', '伊比利亚火腿切片', '西式'),
('水果塔', '🥧', '新鲜水果塔配奶油', '甜点'),
('寿司', '🍣', '新鲜三文鱼寿司拼盘', '日式'),
('墨西哥玉米片', '🌮', '玉米片配番茄莎莎酱', '小吃'),
('春卷', '🫔', '越南鲜虾春卷', '亚洲');

-- 种子数据: 风味标签
INSERT INTO flavor_tags (name, emoji, category) VALUES
('甜味', '🍬', 'TASTE'),
('酸味', '🍋', 'TASTE'),
('苦味', '☕', 'TASTE'),
('咸味', '🧂', 'TASTE'),
('鲜味', '🦪', 'TASTE'),
('花香', '🌸', 'AROMA'),
('果香', '🍑', 'AROMA'),
('草本香', '🌿', 'AROMA'),
('烟熏味', '🔥', 'AROMA'),
('辛辣', '🌶', 'TASTE'),
('清爽', '💧', 'TEXTURE'),
('浓郁', '🍯', 'TEXTURE'),
('低度酒', '🍹', 'STRENGTH'),
('中度酒', '🍸', 'STRENGTH'),
('高度酒', '🥃', 'STRENGTH');

-- ============================================
-- 新增评论回复的递归查询存储过程
-- ============================================
DELIMITER //

CREATE PROCEDURE get_comment_tree(
    IN p_recipe_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
    WITH RECURSIVE comment_tree AS (
        -- 根评论 (parent_id IS NULL)
        SELECT
            c.id, c.user_id, c.recipe_id, c.parent_id, c.content,
            c.like_count, c.is_deleted, c.created_at, c.updated_at,
            u.username, u.nickname, u.avatar,
            CAST(c.id AS CHAR(200)) AS path,
            0 AS depth,
            (SELECT COUNT(*) FROM comment_likes cl WHERE cl.comment_id = c.id AND cl.user_id = p_user_id) > 0 AS liked_by_me
        FROM comments c
        JOIN users u ON c.user_id = u.id
        WHERE c.recipe_id = p_recipe_id AND c.parent_id IS NULL

        UNION ALL

        -- 子回复
        SELECT
            c.id, c.user_id, c.recipe_id, c.parent_id, c.content,
            c.like_count, c.is_deleted, c.created_at, c.updated_at,
            u.username, u.nickname, u.avatar,
            CONCAT(ct.path, ',', c.id),
            ct.depth + 1,
            (SELECT COUNT(*) FROM comment_likes cl WHERE cl.comment_id = c.id AND cl.user_id = p_user_id) > 0
        FROM comments c
        JOIN users u ON c.user_id = u.id
        JOIN comment_tree ct ON c.parent_id = ct.id
        WHERE ct.depth < 5
    )
    SELECT * FROM comment_tree
    ORDER BY SUBSTRING_INDEX(path, ',', 1) DESC, path ASC;
END//

-- 获取协同过滤推荐
CREATE PROCEDURE get_collaborative_recommendations(
    IN p_user_id BIGINT,
    IN p_limit INT
)
BEGIN
    -- 找到相似用户喜欢的、当前用户未交互过的配方
    SELECT r.*, u.username, u.nickname, u.avatar,
           COUNT(DISTINCT uri2.user_id) AS similar_user_count,
           COALESCE(AVG(ratings.rating), 0) AS avg_rating
    FROM recipes r
    JOIN users u ON r.user_id = u.id
    JOIN user_recipe_interactions uri1 ON r.id = uri1.recipe_id AND uri1.interaction_type IN ('LIKE', 'FAVORITE')
    JOIN user_recipe_interactions uri2 ON uri1.user_id = uri2.user_id
    LEFT JOIN ratings ON r.id = ratings.recipe_id
    WHERE r.is_public = TRUE
      AND r.id NOT IN (
          SELECT recipe_id FROM user_recipe_interactions WHERE user_id = p_user_id
      )
      AND r.id NOT IN (
          SELECT recipe_id FROM view_history WHERE user_id = p_user_id
      )
      AND uri2.user_id != p_user_id
    GROUP BY r.id
    ORDER BY similar_user_count DESC, r.like_count DESC
    LIMIT p_limit;
END//

DELIMITER ;
CREATE USER IF NOT EXISTS 'cocktail_user'@'localhost' IDENTIFIED BY 'cocktail_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON cocktail_maker.* TO 'cocktail_user'@'localhost';
FLUSH PRIVILEGES;

-- 显示创建结果
SHOW TABLES;

-- 显示表结构示例
DESCRIBE users;
DESCRIBE recipes;
DESCRIBE ingredients;

-- 显示视图
SHOW FULL TABLES WHERE TABLE_TYPE LIKE 'VIEW';

-- 显示触发器
SHOW TRIGGERS;

-- 显示存储过程
SHOW PROCEDURE STATUS WHERE Db = 'cocktail_maker';