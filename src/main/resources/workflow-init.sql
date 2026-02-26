-- ============================================
-- Workflow Tables and Data for All Environments
-- ============================================

-- ============================================
-- Development Environment
-- ============================================
USE `lxf-demo`;

-- Form-Process Association Table
CREATE TABLE IF NOT EXISTS `wf_form_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `form_type` VARCHAR(50) NOT NULL COMMENT 'Form type',
  `form_id` BIGINT NOT NULL COMMENT 'Business form ID',
  `process_definition_key` VARCHAR(100) COMMENT 'Process definition KEY',
  `process_instance_id` VARCHAR(64) COMMENT 'Flowable process instance ID',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-completed, 3-cancelled, 4-rejected',
  `initiator_id` BIGINT COMMENT 'Initiator ID',
  `initiator_name` VARCHAR(50) COMMENT 'Initiator name',
  `title` VARCHAR(200) COMMENT 'Process title',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_form` (`form_type`, `form_id`),
  KEY `idx_process_instance` (`process_instance_id`),
  KEY `idx_initiator` (`initiator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Expense Application Table
CREATE TABLE IF NOT EXISTS `wf_expense` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `expense_no` VARCHAR(32) NOT NULL COMMENT 'Expense number',
  `expense_type` VARCHAR(50) NOT NULL COMMENT 'Expense type',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Amount',
  `reason` VARCHAR(500) COMMENT 'Reason',
  `applicant_id` BIGINT NOT NULL COMMENT 'Applicant ID',
  `applicant_name` VARCHAR(50) COMMENT 'Applicant name',
  `apply_time` DATETIME COMMENT 'Apply time',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-approved, 3-rejected, 4-cancelled',
  `attachments` VARCHAR(500) COMMENT 'Attachments (JSON array)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_expense_no` (`expense_no`),
  KEY `idx_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert workflow roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(3, '主管', 'MANAGER', '部门主管，可审批报销', 1, 3),
(4, '财务', 'FINANCE', '财务人员，可审批大额报销', 1, 4);

-- Insert workflow menus (starting from id 100)
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
-- Workflow management directory
(100, 0, '流程管理', 'M', '/workflow', NULL, NULL, 'Operation', 1, 1, 2),
-- Process definition menu
(101, 100, '流程定义', 'C', '/workflow/definition', 'workflow/definition/index', 'workflow:definition:list', 'Document', 1, 1, 1),
(102, 101, '流程定义查询', 'F', NULL, NULL, 'workflow:definition:query', NULL, 1, 1, 1),
(103, 101, '流程部署', 'F', NULL, NULL, 'workflow:definition:deploy', NULL, 1, 1, 2),
(104, 101, '流程删除', 'F', NULL, NULL, 'workflow:definition:delete', NULL, 1, 1, 3),
(105, 101, '流程编辑', 'F', NULL, NULL, 'workflow:definition:edit', NULL, 1, 1, 4),
-- My processes menu
(106, 100, '我的流程', 'C', '/workflow/instance', 'workflow/instance/index', NULL, 'List', 1, 1, 2),
(107, 106, '发起流程', 'F', NULL, NULL, 'workflow:instance:start', NULL, 1, 1, 1),
(108, 106, '撤销流程', 'F', NULL, NULL, 'workflow:instance:cancel', NULL, 1, 1, 2),
-- Todo tasks menu
(109, 100, '待办任务', 'C', '/workflow/task/todo', 'workflow/task/todo', NULL, 'Clock', 1, 1, 3),
-- Done tasks menu
(110, 100, '已办任务', 'C', '/workflow/task/done', 'workflow/task/done', NULL, 'CircleCheck', 1, 1, 4),
-- Expense management directory
(111, 0, '报销管理', 'M', '/expense', NULL, NULL, 'Money', 1, 1, 3),
-- Expense application menu
(112, 111, '报销申请', 'C', '/expense/apply', 'expense/apply', NULL, 'EditPen', 1, 1, 1),
-- My expenses menu
(113, 111, '我的报销', 'C', '/expense/list', 'expense/index', NULL, 'Tickets', 1, 1, 2);

-- Assign workflow menus to ADMIN role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
(1, 106), (1, 107), (1, 108), (1, 109), (1, 110),
(1, 111), (1, 112), (1, 113);

-- Assign workflow menus to USER role (can view and start processes)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 100), (2, 106), (2, 107), (2, 108), (2, 109), (2, 110),
(2, 111), (2, 112), (2, 113);

-- Assign workflow menus to MANAGER role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 100), (3, 106), (3, 107), (3, 108), (3, 109), (3, 110),
(3, 111), (3, 112), (3, 113);

-- Assign workflow menus to FINANCE role
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 100), (4, 106), (4, 107), (4, 108), (4, 109), (4, 110),
(4, 111), (4, 112), (4, 113);

-- ============================================
-- Test Environment
-- ============================================
USE `lxf-demo-test`;

-- Form-Process Association Table
CREATE TABLE IF NOT EXISTS `wf_form_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `form_type` VARCHAR(50) NOT NULL COMMENT 'Form type',
  `form_id` BIGINT NOT NULL COMMENT 'Business form ID',
  `process_definition_key` VARCHAR(100) COMMENT 'Process definition KEY',
  `process_instance_id` VARCHAR(64) COMMENT 'Flowable process instance ID',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-completed, 3-cancelled, 4-rejected',
  `initiator_id` BIGINT COMMENT 'Initiator ID',
  `initiator_name` VARCHAR(50) COMMENT 'Initiator name',
  `title` VARCHAR(200) COMMENT 'Process title',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_form` (`form_type`, `form_id`),
  KEY `idx_process_instance` (`process_instance_id`),
  KEY `idx_initiator` (`initiator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Expense Application Table
CREATE TABLE IF NOT EXISTS `wf_expense` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `expense_no` VARCHAR(32) NOT NULL COMMENT 'Expense number',
  `expense_type` VARCHAR(50) NOT NULL COMMENT 'Expense type',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Amount',
  `reason` VARCHAR(500) COMMENT 'Reason',
  `applicant_id` BIGINT NOT NULL COMMENT 'Applicant ID',
  `applicant_name` VARCHAR(50) COMMENT 'Applicant name',
  `apply_time` DATETIME COMMENT 'Apply time',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-approved, 3-rejected, 4-cancelled',
  `attachments` VARCHAR(500) COMMENT 'Attachments (JSON array)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_expense_no` (`expense_no`),
  KEY `idx_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert workflow roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(3, '主管', 'MANAGER', '部门主管，可审批报销', 1, 3),
(4, '财务', 'FINANCE', '财务人员，可审批大额报销', 1, 4);

-- Insert workflow menus
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
(100, 0, '流程管理', 'M', '/workflow', NULL, NULL, 'Operation', 1, 1, 2),
(101, 100, '流程定义', 'C', '/workflow/definition', 'workflow/definition/index', 'workflow:definition:list', 'Document', 1, 1, 1),
(102, 101, '流程定义查询', 'F', NULL, NULL, 'workflow:definition:query', NULL, 1, 1, 1),
(103, 101, '流程部署', 'F', NULL, NULL, 'workflow:definition:deploy', NULL, 1, 1, 2),
(104, 101, '流程删除', 'F', NULL, NULL, 'workflow:definition:delete', NULL, 1, 1, 3),
(105, 101, '流程编辑', 'F', NULL, NULL, 'workflow:definition:edit', NULL, 1, 1, 4),
(106, 100, '我的流程', 'C', '/workflow/instance', 'workflow/instance/index', NULL, 'List', 1, 1, 2),
(107, 106, '发起流程', 'F', NULL, NULL, 'workflow:instance:start', NULL, 1, 1, 1),
(108, 106, '撤销流程', 'F', NULL, NULL, 'workflow:instance:cancel', NULL, 1, 1, 2),
(109, 100, '待办任务', 'C', '/workflow/task/todo', 'workflow/task/todo', NULL, 'Clock', 1, 1, 3),
(110, 100, '已办任务', 'C', '/workflow/task/done', 'workflow/task/done', NULL, 'CircleCheck', 1, 1, 4),
(111, 0, '报销管理', 'M', '/expense', NULL, NULL, 'Money', 1, 1, 3),
(112, 111, '报销申请', 'C', '/expense/apply', 'expense/apply', NULL, 'EditPen', 1, 1, 1),
(113, 111, '我的报销', 'C', '/expense/list', 'expense/index', NULL, 'Tickets', 1, 1, 2);

-- Assign workflow menus to roles
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
(1, 106), (1, 107), (1, 108), (1, 109), (1, 110),
(1, 111), (1, 112), (1, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 100), (2, 106), (2, 107), (2, 108), (2, 109), (2, 110),
(2, 111), (2, 112), (2, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 100), (3, 106), (3, 107), (3, 108), (3, 109), (3, 110),
(3, 111), (3, 112), (3, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 100), (4, 106), (4, 107), (4, 108), (4, 109), (4, 110),
(4, 111), (4, 112), (4, 113);

-- ============================================
-- Production Environment
-- ============================================
USE `lxf-demo-prod`;

-- Form-Process Association Table
CREATE TABLE IF NOT EXISTS `wf_form_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `form_type` VARCHAR(50) NOT NULL COMMENT 'Form type',
  `form_id` BIGINT NOT NULL COMMENT 'Business form ID',
  `process_definition_key` VARCHAR(100) COMMENT 'Process definition KEY',
  `process_instance_id` VARCHAR(64) COMMENT 'Flowable process instance ID',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-completed, 3-cancelled, 4-rejected',
  `initiator_id` BIGINT COMMENT 'Initiator ID',
  `initiator_name` VARCHAR(50) COMMENT 'Initiator name',
  `title` VARCHAR(200) COMMENT 'Process title',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_form` (`form_type`, `form_id`),
  KEY `idx_process_instance` (`process_instance_id`),
  KEY `idx_initiator` (`initiator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Expense Application Table
CREATE TABLE IF NOT EXISTS `wf_expense` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `expense_no` VARCHAR(32) NOT NULL COMMENT 'Expense number',
  `expense_type` VARCHAR(50) NOT NULL COMMENT 'Expense type',
  `amount` DECIMAL(10,2) NOT NULL COMMENT 'Amount',
  `reason` VARCHAR(500) COMMENT 'Reason',
  `applicant_id` BIGINT NOT NULL COMMENT 'Applicant ID',
  `applicant_name` VARCHAR(50) COMMENT 'Applicant name',
  `apply_time` DATETIME COMMENT 'Apply time',
  `status` TINYINT DEFAULT 0 COMMENT 'Status: 0-draft, 1-in progress, 2-approved, 3-rejected, 4-cancelled',
  `attachments` VARCHAR(500) COMMENT 'Attachments (JSON array)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_expense_no` (`expense_no`),
  KEY `idx_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert workflow roles
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `description`, `status`, `sort_order`) VALUES
(3, '主管', 'MANAGER', '部门主管，可审批报销', 1, 3),
(4, '财务', 'FINANCE', '财务人员，可审批大额报销', 1, 4);

-- Insert workflow menus
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission`, `icon`, `visible`, `status`, `sort_order`) VALUES
(100, 0, '流程管理', 'M', '/workflow', NULL, NULL, 'Operation', 1, 1, 2),
(101, 100, '流程定义', 'C', '/workflow/definition', 'workflow/definition/index', 'workflow:definition:list', 'Document', 1, 1, 1),
(102, 101, '流程定义查询', 'F', NULL, NULL, 'workflow:definition:query', NULL, 1, 1, 1),
(103, 101, '流程部署', 'F', NULL, NULL, 'workflow:definition:deploy', NULL, 1, 1, 2),
(104, 101, '流程删除', 'F', NULL, NULL, 'workflow:definition:delete', NULL, 1, 1, 3),
(105, 101, '流程编辑', 'F', NULL, NULL, 'workflow:definition:edit', NULL, 1, 1, 4),
(106, 100, '我的流程', 'C', '/workflow/instance', 'workflow/instance/index', NULL, 'List', 1, 1, 2),
(107, 106, '发起流程', 'F', NULL, NULL, 'workflow:instance:start', NULL, 1, 1, 1),
(108, 106, '撤销流程', 'F', NULL, NULL, 'workflow:instance:cancel', NULL, 1, 1, 2),
(109, 100, '待办任务', 'C', '/workflow/task/todo', 'workflow/task/todo', NULL, 'Clock', 1, 1, 3),
(110, 100, '已办任务', 'C', '/workflow/task/done', 'workflow/task/done', NULL, 'CircleCheck', 1, 1, 4),
(111, 0, '报销管理', 'M', '/expense', NULL, NULL, 'Money', 1, 1, 3),
(112, 111, '报销申请', 'C', '/expense/apply', 'expense/apply', NULL, 'EditPen', 1, 1, 1),
(113, 111, '我的报销', 'C', '/expense/list', 'expense/index', NULL, 'Tickets', 1, 1, 2);

-- Assign workflow menus to roles
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(1, 100), (1, 101), (1, 102), (1, 103), (1, 104), (1, 105),
(1, 106), (1, 107), (1, 108), (1, 109), (1, 110),
(1, 111), (1, 112), (1, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(2, 100), (2, 106), (2, 107), (2, 108), (2, 109), (2, 110),
(2, 111), (2, 112), (2, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 100), (3, 106), (3, 107), (3, 108), (3, 109), (3, 110),
(3, 111), (3, 112), (3, 113);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 100), (4, 106), (4, 107), (4, 108), (4, 109), (4, 110),
(4, 111), (4, 112), (4, 113);
