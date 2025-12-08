# Admin服务RBAC权限体系设计

## 1. 权限体系概述

采用RBAC（Role-Based Access Control）模型，支持用户-角色-权限的灵活配置，实现对系统功能和数据的精细化权限控制。

## 2. 数据库表设计

### 2.1 核心表结构

#### 用户表 (user) - 已存在
```sql
-- 扩展用户表，添加权限相关字段
ALTER TABLE `user` ADD COLUMN `dept_id` BIGINT(11) COMMENT '部门ID';
ALTER TABLE `user` ADD COLUMN `nickname` VARCHAR(50) COMMENT '昵称';
ALTER TABLE `user` ADD COLUMN `avatar` VARCHAR(255) COMMENT '头像URL';
ALTER TABLE `user` ADD COLUMN `sex` TINYINT(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女';
ALTER TABLE `user` ADD COLUMN `remark` VARCHAR(500) COMMENT '备注';
```

#### 角色表 (role)
```sql
CREATE TABLE `role` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
  `role_sort` INT(4) NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `data_scope` TINYINT(1) DEFAULT 1 COMMENT '数据范围：1-全部数据权限，2-自定义数据权限，3-本部门数据权限，4-本部门及以下数据权限',
  `menu_check_strictly` TINYINT(1) DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` TINYINT(1) DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '角色状态：0-正常，1-停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';
```

#### 菜单权限表 (menu)
```sql
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
```

#### 部门表 (dept)
```sql
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
```

#### 用户角色关联表 (user_role)
```sql
CREATE TABLE `user_role` (
  `user_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '用户ID',
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';
```

#### 角色菜单关联表 (role_menu)
```sql
CREATE TABLE `role_menu` (
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  `menu_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';
```

#### 角色部门关联表 (role_dept)
```sql
CREATE TABLE `role_dept` (
  `role_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '角色ID',
  `dept_id` BIGINT(11) UNSIGNED NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和部门关联表';
```

### 2.2 操作日志表
```sql
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
```

### 2.3 登录日志表
```sql
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
```

## 3. 权限体系特性

### 3.1 数据权限控制
- **全部数据权限**：可以查看所有数据
- **自定义数据权限**：只能查看指定部门的数据
- **本部门数据权限**：只能查看本部门的数据
- **本部门及以下数据权限**：可以查看本部门及子部门的数据

### 3.2 菜单权限控制
- **目录(M)**：系统的功能模块
- **菜单(C)**：具体的页面功能
- **按钮(F)**：页面上的操作按钮权限

### 3.3 角色特性
- 支持多角色分配
- 角色权限继承
- 角色数据权限隔离
- 角色状态管理

## 4. 权限验证流程

1. **用户登录**：验证用户名密码，生成Token
2. **权限加载**：将用户角色权限加载到Redis缓存
3. **请求拦截**：网关统一拦截，验证Token有效性
4. **权限验证**：使用`@SaCheckPermission`注解验证具体权限
5. **数据过滤**：根据数据权限范围过滤查询结果

## 5. 初始化数据

### 5.1 默认角色
- **超级管理员**：拥有所有权限
- **管理员**：拥有大部分管理权限
- **普通用户**：基础查看权限

### 5.2 默认部门
- **总公司**：顶级部门
- **技术部**：技术研发部门
- **运营部**：运营管理部门
- **财务部**：财务管理部门

## 6. 扩展性设计

1. **租户隔离**：预留tenant_id字段支持多租户
2. **权限时效**：支持权限的生效时间范围
3. **动态权限**：支持运行时权限动态调整
4. **API权限**：支持接口级别的权限控制
5. **字段权限**：支持数据字段级别的权限控制（预留扩展）