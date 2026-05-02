-- 创建数据库
CREATE DATABASE IF NOT EXISTS loveai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE loveai;

-- ============================================
-- 方式1：自动建表（推荐）
-- ============================================
-- 表会由 Hibernate 自动创建，无需手动执行建表语句
-- 只需启动项目，会自动生成以下表：
-- 1. users - 用户表
-- 2. target_profile - 对象档案表
-- 3. chat_history - 聊天记录表
-- 4. suggestion_log - 建议记录表

-- ============================================
-- 方式2：手动建表（可选）
-- ============================================
-- 如果你想手动创建表，可以执行以下SQL：

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（MD5加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：male/female',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `personality` VARCHAR(200) DEFAULT NULL COMMENT '性格标签，逗号分隔',
    `level` INT DEFAULT 1 COMMENT '用户等级 1-10',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 对象档案表
CREATE TABLE IF NOT EXISTS `target_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '档案ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `nickname` VARCHAR(50) NOT NULL COMMENT '对象昵称',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：male/female',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `personality` VARCHAR(500) DEFAULT NULL COMMENT '性格特点',
    `interests` VARCHAR(500) DEFAULT NULL COMMENT '兴趣爱好',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `important_dates` TEXT DEFAULT NULL COMMENT '重要日期（JSON格式）',
    `relationship_status` VARCHAR(20) DEFAULT NULL COMMENT '关系状态：stranger/friend/ambiguous/lover',
    `notes` TEXT DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对象档案表';

-- 3. 聊天记录表
CREATE TABLE IF NOT EXISTS `chat_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT DEFAULT NULL COMMENT '对象ID',
    `role` VARCHAR(10) NOT NULL COMMENT '角色：user/other',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `emotion` VARCHAR(50) DEFAULT NULL COMMENT '情绪标签',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_target` (`user_id`, `target_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';

-- 4. 建议记录表
CREATE TABLE IF NOT EXISTS `suggestion_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `input_message` TEXT DEFAULT NULL COMMENT '输入消息',
    `suggestions` TEXT DEFAULT NULL COMMENT 'JSON格式的建议列表',
    `selected` INT DEFAULT NULL COMMENT '用户选择的建议序号',
    `feedback` INT DEFAULT NULL COMMENT '用户反馈 1-5',
    `created_at` DATETIME DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='建议记录表';