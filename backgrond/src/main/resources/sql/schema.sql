-- =============================================
-- 二手书交易管理系统 - 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `book_trading` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

/*清除外键约束*/
SET FOREIGN_KEY_CHECKS = 0;

/**/
TRUNCATE TABLE review;
TRUNCATE TABLE message;
TRUNCATE TABLE orders;
TRUNCATE TABLE favorite;
TRUNCATE TABLE book;
TRUNCATE TABLE user;
TRUNCATE TABLE category;



USE `book_trading`;

-- =============================================
-- 1. 用户表 (user)
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `avg_rating` DECIMAL(2,1) DEFAULT 0.0 COMMENT '平均评分',
  `rating_count` INT DEFAULT 0 COMMENT '评价数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 分类表 (category)
-- =============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍分类表';

-- =============================================
-- 3. 书籍表 (book)
-- =============================================
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '书籍ID',
  `title` VARCHAR(200) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `condition_level` TINYINT NOT NULL COMMENT '成色：1-全新，2-九成新，3-八成新，4-七成新，5-六成新及以下',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片URL（JSON数组）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-在售，2-已预订，3-已售，4-下架',
  `user_id` BIGINT NOT NULL COMMENT '卖家ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_price` (`price`),
  CONSTRAINT `fk_book_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍表';

-- =============================================
-- 4. 收藏表 (favorite)
-- =============================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_book_id` (`book_id`),
  CONSTRAINT `fk_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_favorite_book` FOREIGN KEY (`book_id`) REFERENCES `book`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =============================================
-- 5. 订单表 (orders) - 避免使用MySQL保留字order
-- =============================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `price` DECIMAL(10,2) NOT NULL COMMENT '成交价格',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-待确认，2-已确认，3-已支付，4-已发货，5-已完成，6-已取消',
  `shipping_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
  `shipping_info` VARCHAR(200) DEFAULT NULL COMMENT '物流信息',
  `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  UNIQUE KEY `uk_order_no` (`order_no`),
  INDEX `idx_buyer_id` (`buyer_id`),
  INDEX `idx_seller_id` (`seller_id`),
  INDEX `idx_book_id` (`book_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`),
  CONSTRAINT `fk_order_book` FOREIGN KEY (`book_id`) REFERENCES `book`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 6. 评价表 (review)
-- =============================================
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `reviewer_id` BIGINT NOT NULL COMMENT '评价者ID',
  `reviewee_id` BIGINT NOT NULL COMMENT '被评价者ID',
  `rating` TINYINT NOT NULL COMMENT '评分：1-5星',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_order_reviewer` (`order_id`, `reviewer_id`),
  INDEX `idx_reviewee_id` (`reviewee_id`),
  INDEX `idx_reviewer_id` (`reviewer_id`),
  INDEX `idx_create_time` (`create_time`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_review_reviewee` FOREIGN KEY (`reviewee_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- =============================================
-- 7. 消息表 (message)
-- =============================================
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `deleted_by_sender` TINYINT DEFAULT 0 COMMENT '发送者是否删除：0-否，1-是',
  `deleted_by_receiver` TINYINT DEFAULT 0 COMMENT '接收者是否删除：0-否，1-是',
  INDEX `idx_sender_id` (`sender_id`),
  INDEX `idx_receiver_id` (`receiver_id`),
  INDEX `idx_create_time` (`create_time`),
  INDEX `idx_is_read` (`is_read`),
  CONSTRAINT `fk_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';
