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

-- ALTER statements for existing tables (run these if tables already exist)
-- ALTER TABLE `user` ADD COLUMN `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled';
-- ALTER TABLE `user` ADD COLUMN `role` VARCHAR(50) DEFAULT 'USER' COMMENT 'Role: USER, ADMIN';
-- ALTER TABLE `user` ADD COLUMN `last_login_time` DATETIME COMMENT 'Last login time';
