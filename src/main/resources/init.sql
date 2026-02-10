-- Development environment
CREATE DATABASE IF NOT EXISTS `lxf-demo` DEFAULT CHARACTER SET utf8mb4;
USE `lxf-demo`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Role name',
  `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role key',
  `description` VARCHAR(255) COMMENT 'Description',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent menu ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu name',
  `menu_type` CHAR(1) COMMENT 'Menu type: M-directory, C-menu, F-button',
  `path` VARCHAR(200) COMMENT 'Route path',
  `component` VARCHAR(255) COMMENT 'Component path',
  `permission` VARCHAR(100) COMMENT 'Permission string',
  `icon` VARCHAR(100) COMMENT 'Menu icon',
  `visible` TINYINT DEFAULT 1 COMMENT 'Visible: 0-hidden, 1-visible',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT NOT NULL COMMENT 'Menu ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Test environment
CREATE DATABASE IF NOT EXISTS `lxf-demo-test` DEFAULT CHARACTER SET utf8mb4;
USE `lxf-demo-test`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Role name',
  `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role key',
  `description` VARCHAR(255) COMMENT 'Description',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent menu ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu name',
  `menu_type` CHAR(1) COMMENT 'Menu type: M-directory, C-menu, F-button',
  `path` VARCHAR(200) COMMENT 'Route path',
  `component` VARCHAR(255) COMMENT 'Component path',
  `permission` VARCHAR(100) COMMENT 'Permission string',
  `icon` VARCHAR(100) COMMENT 'Menu icon',
  `visible` TINYINT DEFAULT 1 COMMENT 'Visible: 0-hidden, 1-visible',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT NOT NULL COMMENT 'Menu ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Production environment
CREATE DATABASE IF NOT EXISTS `lxf-demo-prod` DEFAULT CHARACTER SET utf8mb4;
USE `lxf-demo-prod`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Role ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT 'Role name',
  `role_key` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role key',
  `description` VARCHAR(255) COMMENT 'Description',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Menu ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT 'Parent menu ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT 'Menu name',
  `menu_type` CHAR(1) COMMENT 'Menu type: M-directory, C-menu, F-button',
  `path` VARCHAR(200) COMMENT 'Route path',
  `component` VARCHAR(255) COMMENT 'Component path',
  `permission` VARCHAR(100) COMMENT 'Permission string',
  `icon` VARCHAR(100) COMMENT 'Menu icon',
  `visible` TINYINT DEFAULT 1 COMMENT 'Visible: 0-hidden, 1-visible',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sort_order` INT DEFAULT 0 COMMENT 'Sort order',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT NOT NULL COMMENT 'Menu ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ALTER statements for existing tables (run these if tables already exist)
-- ALTER TABLE `user` ADD COLUMN `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled';
-- ALTER TABLE `user` ADD COLUMN `role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN';
-- ALTER TABLE `user` ADD COLUMN `last_login_time` DATETIME COMMENT 'Last login time';
