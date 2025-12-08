-- Admin管理微服务的数据库业务初始化
DROP DATABASE IF EXISTS admin;
CREATE DATABASE admin;
USE admin;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE `user` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` BIGINT(11) DEFAULT NULL COMMENT '部门ID',
  `username` VARCHAR(30) NOT NULL COMMENT '用户账号',
  `nickname` VARCHAR(30) DEFAULT NULL COMMENT '用户昵称',
  `password` VARCHAR(100) DEFAULT '' COMMENT '密码',
  `email` VARCHAR(50) DEFAULT '' COMMENT '用户邮箱',
  `mobile` VARCHAR(11) DEFAULT '' COMMENT '手机号码',
  `sex` TINYINT(1) DEFAULT 0 COMMENT '用户性别（0男 1女 2未知）',
  `avatar` VARCHAR(100) DEFAULT '' COMMENT '头像地址',
  `status` TINYINT(1) DEFAULT 1 COMMENT '帐号状态（0正常 1停用）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `registration_time` DATETIME DEFAULT NULL COMMENT '注册时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `last_login_device` TINYINT(1) DEFAULT 0 COMMENT '最后登录设备: 0-PC; 1-APP; 2-小程序',
  `is_del` TINYINT(1) DEFAULT 0 COMMENT '删除标识; 0-未删除; 1-已删除',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 角色表
-- ----------------------------
CREATE TABLE `role` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
  `role_sort` INT(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `data_scope` TINYINT(1) DEFAULT 1 COMMENT '数据范围：1-全部数据权限，2-自定义数据权限，3-本部门数据权限，4-本部门及以下数据权限',
  `menu_check_strictly` TINYINT(1) DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` TINYINT(1) DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '角色状态：0-正常，1-停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- 菜单权限表
-- ----------------------------
CREATE TABLE `menu` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `parent_id` BIGINT(11) DEFAULT 0 COMMENT '父菜单ID',
  `order_num` INT(4) DEFAULT 0 COMMENT '显示顺序',
  `path` VARCHAR(200) DEFAULT '' COMMENT '路由地址',
  `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `query` VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` TINYINT(1) DEFAULT 1 COMMENT '是否为外链：0-是，1-否',
  `is_cache` TINYINT(1) DEFAULT 0 COMMENT '是否缓存：0-缓存，1-不缓存',
  `menu_type` CHAR(1) DEFAULT '' COMMENT '菜单类型：M-目录，C-菜单，F-按钮',
  `visible` TINYINT(1) DEFAULT 0 COMMENT '菜单状态：0-显示，1-隐藏',
  `status` TINYINT(1) DEFAULT 0 COMMENT '菜单状态：0-正常，1-停用',
  `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `icon` VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 部门表
-- ----------------------------
CREATE TABLE `dept` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` BIGINT(11) DEFAULT 0 COMMENT '父部门ID',
  `ancestors` VARCHAR(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` VARCHAR(30) DEFAULT '' COMMENT '部门名称',
  `order_num` INT(4) DEFAULT 0 COMMENT '显示顺序',
  `leader` VARCHAR(20) DEFAULT NULL COMMENT '负责人',
  `phone` VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT(1) DEFAULT 0 COMMENT '部门状态：0-正常，1-停用',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
CREATE TABLE `user_role` (
  `user_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- 角色菜单关联表
-- ----------------------------
CREATE TABLE `role_menu` (
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ----------------------------
-- 角色部门关联表
-- ----------------------------
CREATE TABLE `role_dept` (
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  `dept_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';

-- ----------------------------
-- 操作日志记录
-- ----------------------------
CREATE TABLE `operation_log` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` VARCHAR(50) DEFAULT '' COMMENT '模块标题',
  `business_type` INT(2) DEFAULT 0 COMMENT '业务类型（0-其他，1-新增，2-修改，3-删除）',
  `method` VARCHAR(100) DEFAULT '' COMMENT '方法名称',
  `request_method` VARCHAR(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` INT(1) DEFAULT 0 COMMENT '操作类别（0-其他，1-后台用户，2-手机端用户）',
  `operator_name` VARCHAR(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` VARCHAR(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` VARCHAR(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` VARCHAR(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` VARCHAR(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` VARCHAR(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` VARCHAR(2000) DEFAULT '' COMMENT '返回参数',
  `status` INT(1) DEFAULT 0 COMMENT '操作状态（0-正常，1-异常）',
  `error_msg` VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- ----------------------------
-- 系统访问记录
-- ----------------------------
CREATE TABLE `login_log` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` VARCHAR(50) DEFAULT '' COMMENT '登录账号',
  `ipaddr` VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` VARCHAR(255) DEFAULT '' COMMENT '登录地点',
  `browser` VARCHAR(50) DEFAULT '' COMMENT '浏览器类型',
  `os` VARCHAR(50) DEFAULT '' COMMENT '操作系统',
  `status` TINYINT(1) DEFAULT 0 COMMENT '登录状态（0-成功，1-失败）',
  `msg` VARCHAR(255) DEFAULT '' COMMENT '提示消息',
  `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录';

-- ----------------------------
-- Seata分布式事务日志表
-- ----------------------------
CREATE TABLE `undo_log` (
  `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `branch_id`     BIGINT(20)   NOT NULL,
  `xid`           VARCHAR(100) NOT NULL,
  `context`       VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB     NOT NULL,
  `log_status`    INT(11)      NOT NULL,
  `log_created`   DATETIME     NOT NULL,
  `log_modified`  DATETIME     NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 初始化部门数据
-- ----------------------------
INSERT INTO `dept` VALUES
(1,  0,   '0',          '总公司',   0, 'admin', '15888888888', 'admin@company.com',0, 'admin', NOW(), '', NOW()),
(2,  1,   '0,1',        '深圳总公司', 1, 'admin', '15888888888', 'admin@company.com',0, 'admin', NOW(), '', NOW()),
(3,  2,   '0,1,2',      '研发部门',   1, 'admin', '15888888888', 'admin@company.com',0, 'admin', NOW(), '', NOW()),
(4,  2,   '0,1,2',      '市场部门',   2, 'admin', '15888888888', 'admin@company.com',0, 'admin', NOW(), '', NOW()),
(5,  2,   '0,1,2',      '测试部门',   3, 'admin', '15888888888', 'admin@company.com',0, 'admin', NOW(), '', NOW());

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO `role` VALUES
(1, '超级管理员', 'admin',  1, 1, 1, 1, 0, '超级管理员', 'admin', NOW(), '', NOW()),
(2, '管理员',     'common', 2, 2, 1, 1, 0, '管理员',     'admin', NOW(), '', NOW()),
(3, '普通角色',    'user',   3, 3, 1, 1, 0, '普通角色',    'admin', NOW(), '', NOW());

-- ----------------------------
-- 初始化菜单数据
-- ----------------------------
INSERT INTO `menu` VALUES
(1, '系统管理', 0, 1, 'system',           NULL,        '', 1, 0, 'M', 0, 0, '', 'system',           'admin', NOW(), '', NOW(), '系统管理目录'),
(2, '用户管理', 1, 1, 'user',             'system/user/index',        '', 1, 0, 'C', 0, 0, 'system:user:list',        'user',          'admin', NOW(), '', NOW(), '用户管理菜单'),
(3, '角色管理', 1, 2, 'role',             'system/role/index',        '', 1, 0, 'C', 0, 0, 'system:role:list',        'peoples',       'admin', NOW(), '', NOW(), '角色管理菜单'),
(4, '菜单管理', 1, 3, 'menu',             'system/menu/index',        '', 1, 0, 'C', 0, 0, 'system:menu:list',        'tree-table',    'admin', NOW(), '', NOW(), '菜单管理菜单'),
(5, '部门管理', 1, 4, 'dept',             'system/dept/index',        '', 1, 0, 'C', 0, 0, 'system:dept:list',        'tree',          'admin', NOW(), '', NOW(), '部门管理菜单'),
(6, '用户查询', 2, 1, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:user:query',        '#',             'admin', NOW(), '', NOW(), ''),
(7, '用户新增', 2, 2, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:user:add',          '#',             'admin', NOW(), '', NOW(), ''),
(8, '用户修改', 2, 3, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:user:edit',         '#',             'admin', NOW(), '', NOW(), ''),
(9, '用户删除', 2, 4, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:user:remove',       '#',             'admin', NOW(), '', NOW(), ''),
(10, '用户导出', 2, 5, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:user:export',       '#',             'admin', NOW(), '', NOW(), ''),
(11, '角色查询', 3, 1, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:role:query',        '#',             'admin', NOW(), '', NOW(), ''),
(12, '角色新增', 3, 2, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:role:add',          '#',             'admin', NOW(), '', NOW(), ''),
(13, '角色修改', 3, 3, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:role:edit',         '#',             'admin', NOW(), '', NOW(), ''),
(14, '角色删除', 3, 4, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:role:remove',       '#',             'admin', NOW(), '', NOW(), ''),
(15, '菜单查询', 4, 1, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:menu:query',        '#',             'admin', NOW(), '', NOW(), ''),
(16, '菜单新增', 4, 2, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:menu:add',          '#',             'admin', NOW(), '', NOW(), ''),
(17, '菜单修改', 4, 3, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:menu:edit',         '#',             'admin', NOW(), '', NOW(), ''),
(18, '菜单删除', 4, 4, '',                 '',                        '', 1, 0, 'F', 0, 0, 'system:menu:remove',       '#',             'admin', NOW(), '', NOW(), '');

-- ----------------------------
-- 初始化用户数据
-- ----------------------------
INSERT INTO `user` VALUES
(1, 103, 'admin', '管理员', '$2a$10$7JB720yubVSOfvVlc6aX5u/1qJPH9L9IjXeJQ.M.QQY8.h1R0sOqy', 'admin@company.com', '15888888888', 1, '', 1, '管理员', NOW(), NOW(), 0, 0, 'admin', NOW(), '', NOW()),
(2, 105, 'test',  '测试员', '$2a$10$7JB720yubVSOfvVlc6aX5u/1qJPH9L9IjXeJQ.M.QQY8.h1R0sOqy', 'test@company.com',  '15666666666', 1, '', 1, '测试员', NOW(), NOW(), 0, 0, 'admin', NOW(), '', NOW());

-- ----------------------------
-- 初始化用户角色关联表数据
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1), (1, 2), (2, 3);

-- ----------------------------
-- 初始化角色菜单关联表数据
-- ----------------------------
INSERT INTO `role_menu` VALUES
-- 超级管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18),
-- 管理员拥有部分权限
(2, 1), (2, 2), (2, 3), (2, 4), (2, 6), (2, 7), (2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14),
-- 普通角色只有查询权限
(3, 1), (3, 2), (3, 3), (3, 4), (3, 6), (3, 11), (3, 15);