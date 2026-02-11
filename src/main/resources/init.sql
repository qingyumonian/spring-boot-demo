-- Development environment
CREATE DATABASE IF NOT EXISTS `lxf-demo` DEFAULT CHARACTER SET utf8mb4;
USE `lxf-demo`;

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sys_role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role` (
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

CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_menu` (
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

CREATE TABLE `sys_role_menu` (
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

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sys_role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role` (
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

CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_menu` (
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

CREATE TABLE `sys_role_menu` (
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

CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
  `age` INT COMMENT 'Age',
  `password` VARCHAR(255) COMMENT 'Password',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `sys_role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN',
  `last_login_time` DATETIME COMMENT 'Last login time',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_role` (
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

CREATE TABLE `sys_user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` BIGINT NOT NULL COMMENT 'User ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sys_menu` (
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

CREATE TABLE `sys_role_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `role_id` BIGINT NOT NULL COMMENT 'Role ID',
  `menu_id` BIGINT NOT NULL COMMENT 'Menu ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ALTER statements for existing tables (run these if tables already exist)
-- ALTER TABLE `sys_user` ADD COLUMN `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled';
-- ALTER TABLE `sys_user` ADD COLUMN `sys_role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN';
-- ALTER TABLE `sys_user` ADD COLUMN `last_login_time` DATETIME COMMENT 'Last login time';

-- ============================================
-- Base Data for Development Environment
-- ============================================
USE `lxf-demo`;

-- Insert roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(1, '超级管理员', 'ADMIN', '拥有所有权限', 1, 1),
(2, '普通用户', 'USER', '普通用户权限', 1, 2);

-- Insert users (password: 123456 MD5 encoded = e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `sys_user` (`id`, `username`, `email`, `age`, `password`, `status`, `sys_role`) VALUES
(1, 'admin', 'admin@example.com', 30, 'e10adc3949ba59abbe56e057f20f883e', 1, 'ADMIN'),
(2, 'user', 'user@example.com', 25, 'e10adc3949ba59abbe56e057f20f883e', 1, 'USER');

-- Insert menus
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
-- System management directory
(1, 0, '系统管理', 'M', '/system', NULL, NULL, 'setting', 1, 1, 1),
-- User management menu
(2, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 1),
-- User management buttons
(3, 2, '用户查询', 'F', NULL, NULL, 'system:user:query', NULL, 1, 1, 1),
(4, 2, '用户新增', 'F', NULL, NULL, 'system:user:add', NULL, 1, 1, 2),
(5, 2, '用户修改', 'F', NULL, NULL, 'system:user:edit', NULL, 1, 1, 3),
(6, 2, '用户删除', 'F', NULL, NULL, 'system:user:delete', NULL, 1, 1, 4),
-- Role management menu
(7, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 1, 1, 2),
-- Role management buttons
(8, 7, '角色查询', 'F', NULL, NULL, 'system:role:query', NULL, 1, 1, 1),
(9, 7, '角色新增', 'F', NULL, NULL, 'system:role:add', NULL, 1, 1, 2),
(10, 7, '角色修改', 'F', NULL, NULL, 'system:role:edit', NULL, 1, 1, 3),
(11, 7, '角色删除', 'F', NULL, NULL, 'system:role:delete', NULL, 1, 1, 4),
-- Menu management menu
(12, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 1, 1, 3),
-- Menu management buttons
(13, 12, '菜单查询', 'F', NULL, NULL, 'system:menu:query', NULL, 1, 1, 1),
(14, 12, '菜单新增', 'F', NULL, NULL, 'system:menu:add', NULL, 1, 1, 2),
(15, 12, '菜单修改', 'F', NULL, NULL, 'system:menu:edit', NULL, 1, 1, 3),
(16, 12, '菜单删除', 'F', NULL, NULL, 'system:menu:delete', NULL, 1, 1, 4);

-- Assign users to roles
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin has ADMIN role
(2, 2);  -- user has USER role

-- Assign menus to roles
-- ADMIN role has all permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16);

-- USER role only has query permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 3),  -- User management - query only
(2, 7), (2, 8);          -- Role management - query only

-- ============================================
-- Base Data for Test Environment
-- ============================================
USE `lxf-demo-test`;

-- Insert roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(1, '超级管理员', 'ADMIN', '拥有所有权限', 1, 1),
(2, '普通用户', 'USER', '普通用户权限', 1, 2);

-- Insert users (password: 123456 MD5 encoded = e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `sys_user` (`id`, `username`, `email`, `age`, `password`, `status`, `sys_role`) VALUES
(1, 'admin', 'admin@example.com', 30, 'e10adc3949ba59abbe56e057f20f883e', 1, 'ADMIN'),
(2, 'user', 'user@example.com', 25, 'e10adc3949ba59abbe56e057f20f883e', 1, 'USER');

-- Insert menus
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
-- System management directory
(1, 0, '系统管理', 'M', '/system', NULL, NULL, 'setting', 1, 1, 1),
-- User management menu
(2, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 1),
-- User management buttons
(3, 2, '用户查询', 'F', NULL, NULL, 'system:user:query', NULL, 1, 1, 1),
(4, 2, '用户新增', 'F', NULL, NULL, 'system:user:add', NULL, 1, 1, 2),
(5, 2, '用户修改', 'F', NULL, NULL, 'system:user:edit', NULL, 1, 1, 3),
(6, 2, '用户删除', 'F', NULL, NULL, 'system:user:delete', NULL, 1, 1, 4),
-- Role management menu
(7, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 1, 1, 2),
-- Role management buttons
(8, 7, '角色查询', 'F', NULL, NULL, 'system:role:query', NULL, 1, 1, 1),
(9, 7, '角色新增', 'F', NULL, NULL, 'system:role:add', NULL, 1, 1, 2),
(10, 7, '角色修改', 'F', NULL, NULL, 'system:role:edit', NULL, 1, 1, 3),
(11, 7, '角色删除', 'F', NULL, NULL, 'system:role:delete', NULL, 1, 1, 4),
-- Menu management menu
(12, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 1, 1, 3),
-- Menu management buttons
(13, 12, '菜单查询', 'F', NULL, NULL, 'system:menu:query', NULL, 1, 1, 1),
(14, 12, '菜单新增', 'F', NULL, NULL, 'system:menu:add', NULL, 1, 1, 2),
(15, 12, '菜单修改', 'F', NULL, NULL, 'system:menu:edit', NULL, 1, 1, 3),
(16, 12, '菜单删除', 'F', NULL, NULL, 'system:menu:delete', NULL, 1, 1, 4);

-- Assign users to roles
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin has ADMIN role
(2, 2);  -- user has USER role

-- Assign menus to roles
-- ADMIN role has all permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16);

-- USER role only has query permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 3),  -- User management - query only
(2, 7), (2, 8);          -- Role management - query only

-- ============================================
-- Base Data for Production Environment
-- ============================================
USE `lxf-demo-prod`;

-- Insert roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(1, '超级管理员', 'ADMIN', '拥有所有权限', 1, 1),
(2, '普通用户', 'USER', '普通用户权限', 1, 2);

-- Insert users (password: 123456 MD5 encoded = e10adc3949ba59abbe56e057f20f883e)
INSERT INTO `sys_user` (`id`, `username`, `email`, `age`, `password`, `status`, `sys_role`) VALUES
(1, 'admin', 'admin@example.com', 30, 'e10adc3949ba59abbe56e057f20f883e', 1, 'ADMIN'),
(2, 'user', 'user@example.com', 25, 'e10adc3949ba59abbe56e057f20f883e', 1, 'USER');

-- Insert menus
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
-- System management directory
(1, 0, '系统管理', 'M', '/system', NULL, NULL, 'setting', 1, 1, 1),
-- User management menu
(2, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 1),
-- User management buttons
(3, 2, '用户查询', 'F', NULL, NULL, 'system:user:query', NULL, 1, 1, 1),
(4, 2, '用户新增', 'F', NULL, NULL, 'system:user:add', NULL, 1, 1, 2),
(5, 2, '用户修改', 'F', NULL, NULL, 'system:user:edit', NULL, 1, 1, 3),
(6, 2, '用户删除', 'F', NULL, NULL, 'system:user:delete', NULL, 1, 1, 4),
-- Role management menu
(7, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'peoples', 1, 1, 2),
-- Role management buttons
(8, 7, '角色查询', 'F', NULL, NULL, 'system:role:query', NULL, 1, 1, 1),
(9, 7, '角色新增', 'F', NULL, NULL, 'system:role:add', NULL, 1, 1, 2),
(10, 7, '角色修改', 'F', NULL, NULL, 'system:role:edit', NULL, 1, 1, 3),
(11, 7, '角色删除', 'F', NULL, NULL, 'system:role:delete', NULL, 1, 1, 4),
-- Menu management menu
(12, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'tree-table', 1, 1, 3),
-- Menu management buttons
(13, 12, '菜单查询', 'F', NULL, NULL, 'system:menu:query', NULL, 1, 1, 1),
(14, 12, '菜单新增', 'F', NULL, NULL, 'system:menu:add', NULL, 1, 1, 2),
(15, 12, '菜单修改', 'F', NULL, NULL, 'system:menu:edit', NULL, 1, 1, 3),
(16, 12, '菜单删除', 'F', NULL, NULL, 'system:menu:delete', NULL, 1, 1, 4);

-- Assign users to roles
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin has ADMIN role
(2, 2);  -- user has USER role

-- Assign menus to roles
-- ADMIN role has all permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),
(1, 12), (1, 13), (1, 14), (1, 15), (1, 16);

-- USER role only has query permissions
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 1), (2, 2), (2, 3),  -- User management - query only
(2, 7), (2, 8);          -- Role management - query only
