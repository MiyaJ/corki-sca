# Corki-SCA 微服务脚手架实施指南

> **文档版本**: v1.0
> **更新日期**: 2025-12-24
> **目标**: 完善微服务脚手架的网关认证和核心管理功能

---

## 📊 项目当前状态

### 整体完成度：约 70%

| 模块 | 完成度 | 说明 |
|------|--------|------|
| 网关服务 | 85% | ✅ 路由、认证、跨域、异常处理已完善 |
| 管理后台 | 60% | ✅ 登录已完成，⚠️ CRUD功能被注释 |
| 会员服务 | 50% | 🟡 需完善业务逻辑 |
| 商品服务 | 20% | 🔴 需从头开发 |
| 订单服务 | 15% | 🔴 需从头开发 |
| 支付服务 | 10% | 🔴 需从头开发 |

---

## ✅ 本次会话已完成工作

### 1. 网关服务完善（corki-sca-gateway）

#### 新增文件：

**`CorsConfig.java`** - 跨域配置
```java
位置: corki-sca-gateway/src/main/java/com/corki/gateway/config/CorsConfig.java
功能: 配置CORS跨域策略，允许所有来源、方法和请求头
```

**`GlobalExceptionHandler.java`** - 全局异常处理
```java
位置: corki-sca-gateway/src/main/java/com/corki/gateway/handler/GlobalExceptionHandler.java
功能: 统一处理网关层的各种异常，返回标准响应格式
```

#### 优化文件：

**`SaTokenConfigure.java`** - 认证过滤器优化
- ✅ 根据路径前缀区分管理端和会员端认证
- ✅ 完善异常处理，区分未登录、角色不足、权限不足
- ✅ 添加详细的日志记录

### 2. 管理后台服务状态

#### 已完成功能：
- ✅ 登录接口（`/admin/login/login`）
- ✅ 验证码生成和验证
- ✅ 密码MD5加密
- ✅ Sa-Token认证集成
- ✅ 退出登录功能

#### 待激活功能（代码已存在但被注释）：
- ⚠️ 用户管理 CRUD（`UserController.java`）
- ⚠️ 角色管理 CRUD（`RoleController.java`）
- ⚠️ 菜单管理 CRUD（`MenuController.java`）

---

## 📋 剩余任务详细清单

### 优先级 P0（立即执行）

#### 任务1：激活用户管理功能

**文件位置**: `corki-sca-admin/src/main/java/com/corki/admin/controller/system/UserController.java`

**操作步骤**:
1. 取消整个文件的注释
2. 检查并修复依赖问题：
   - 确认 `UserService` 中所有方法存在
   - 确认密码加密工具类可用
   - 确认数据验证注解正确

**需要补充的Service方法**:
```java
// UserService中需要检查的方法
- page(Page, LambdaQueryWrapper) // MyBatis-Plus自带
- getById(Long) // MyBatis-Plus自带
- save(User) // MyBatis-Plus自带
- updateById(User) // MyBatis-Plus自带
- removeByIds(Collection) // MyBatis-Plus自带
```

**需要实现的工具方法**:
```java
// 检查用户名唯一性
private boolean checkUsernameUnique(User user) {
    long count = userService.lambdaQuery()
        .eq(User::getUsername, user.getUsername())
        .ne(user.getId() != null, User::getId, user.getId())
        .count();
    return count == 0;
}

// 密码加密
private String encryptPassword(String password) {
    return SaSecureUtil.md5(password);
}

// 从Sa-Token获取当前用户ID
private Long getCurrentUserId() {
    return StpAdminUtil.getLoginIdAsLong();
}
```

**API接口清单**:
| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 用户列表 | GET | /admin/system/user/list | system:user:list |
| 用户详情 | GET | /admin/system/user/{userId} | system:user:query |
| 新增用户 | POST | /admin/system/user | system:user:add |
| 修改用户 | PUT | /admin/system/user | system:user:edit |
| 删除用户 | DELETE | /admin/system/user/{userIds} | system:user:remove |
| 重置密码 | PUT | /admin/system/user/resetPwd | system:user:resetPwd |
| 修改状态 | PUT | /admin/system/user/changeStatus | system:user:edit |

---

#### 任务2：激活角色管理功能

**文件位置**: `corki-sca-admin/src/main/java/com/corki/admin/controller/system/RoleController.java`

**需要创建的Service**:
```java
// IRoleService.java - 需要创建或检查
public interface IRoleService extends IService<Role> {
    List<Role> selectRoleList(Role role);
    Role selectRoleById(Long roleId);
    int insertRole(Role role);
    int updateRole(Role role);
    int updateRoleStatus(Role role);
    int deleteRoleByIds(Long[] roleIds);
    boolean checkRoleNameUnique(Role role);
    boolean checkRoleKeyUnique(Role role);
    void checkRoleAllowed(Role role);
    void checkRoleDataScope(Long[] roleIds);
    int authDataScope(Role role);
    int deleteUserRoleInfo(Long userId, Long roleId);
    int deleteUserRoleInfos(Long roleId, Long[] userIds);
    int insertAuthRole(Long roleId, Long[] userIds);
    List<Role> selectRoleAll();
    List<Long> selectMenuListByRoleId(Long roleId);
}
```

**需要补充的实体类**:
- `Role.java` - 角色实体（可能已存在）
- `RoleMenu.java` - 角色菜单关联实体
- `UserRole.java` - 用户角色关联实体

**数据库表结构参考**:
```sql
-- 角色表
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色菜单关联表
CREATE TABLE `role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 用户角色关联表
CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

**API接口清单**:
| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 角色列表 | GET | /admin/system/role/list | system:role:list |
| 角色详情 | GET | /admin/system/role/{roleId} | system:role:query |
| 新增角色 | POST | /admin/system/role | system:role:add |
| 修改角色 | PUT | /admin/system/role | system:role:edit |
| 删除角色 | DELETE | /admin/system/role/{roleIds} | system:role:remove |
| 修改状态 | PUT | /admin/system/role/changeStatus | system:role:edit |

---

#### 任务3：激活菜单管理功能

**文件位置**: `corki-sca-admin/src/main/java/com/corki/admin/controller/system/MenuController.java`

**需要创建的Service**:
```java
// IMenuService.java - 需要创建或检查
public interface IMenuService extends IService<Menu> {
    List<Menu> selectMenuList(Menu menu, Long userId);
    Menu selectMenuById(Long menuId);
    int insertMenu(Menu menu);
    int updateMenu(Menu menu);
    int deleteMenuById(Long menuId);
    boolean checkMenuNameUnique(Menu menu);
    boolean hasChildByMenuId(Long menuId);
    boolean checkMenuExistRole(Long menuId);
    List<Menu> buildMenuTreeSelect(List<Menu> menus);
    List<Long> selectMenuListByRoleId(Long roleId);
}
```

**需要补充的实体类**:
- `Menu.java` - 菜单实体（可能已存在）

**数据库表结构参考**:
```sql
CREATE TABLE `menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `is_frame` int DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';
```

**API接口清单**:
| 接口 | 方法 | 路径 | 权限 |
|------|------|------|------|
| 菜单列表 | GET | /admin/system/menu/list | system:menu:list |
| 菜单详情 | GET | /admin/system/menu/{menuId} | system:menu:query |
| 菜单树 | GET | /admin/system/menu/treeselect | system:menu:query |
| 新增菜单 | POST | /admin/system/menu | system:menu:add |
| 修改菜单 | PUT | /admin/system/menu | system:menu:edit |
| 删除菜单 | DELETE | /admin/system/menu/{menuId} | system:menu:remove |

---

### 优先级 P1（1-2周内完成）

#### 任务4：完善登录功能的getInfo和getRouters

**文件位置**: `corki-sca-admin/src/main/java/com/corki/admin/service/LoginServiceImpl.java`

**需要实现的方法**:

```java
@Override
public R<LoginUserVO> getInfo() {
    // 1. 获取当前登录用户ID
    Long userId = StpAdminUtil.getLoginIdAsLong();

    // 2. 查询用户信息
    User user = userService.getById(userId);
    if (user == null) {
        return R.fail(ResponseEnum.USER_NOT_EXIST);
    }

    // 3. 查询用户角色
    List<Role> roles = roleService.selectRolesByUserId(userId);

    // 4. 查询用户权限
    Set<String> permissions = menuService.selectPermsByUserId(userId);

    // 5. 构建返回数据
    LoginUserVO loginUserVO = BeanUtil.copyProperties(user, LoginUserVO.class);
    loginUserVO.setRoles(roles.stream().map(Role::getRoleKey).collect(Collectors.toList()));
    loginUserVO.setPermissions(new ArrayList<>(permissions));

    return R.success(loginUserVO);
}

@Override
public R<List<RouterVO>> getRouters() {
    // 1. 获取当前登录用户ID
    Long userId = StpAdminUtil.getLoginIdAsLong();

    // 2. 查询用户菜单树
    List<Menu> menus = menuService.selectMenuTreeByUserId(userId);

    // 3. 构建路由树
    List<RouterVO> routers = buildRouters(menus);

    return R.success(routers);
}
```

**需要创建的VO类**:
```java
// RouterVO.java
@Data
public class RouterVO {
    private String name;
    private String path;
    private Boolean hidden;
    private String redirect;
    private String component;
    private String query;
    private MetaVO meta;
    private List<RouterVO> children;
}

// MetaVO.java
@Data
public class MetaVO {
    private String title;
    private String icon;
    private Boolean noCache;
    private String link;
}
```

---

#### 任务5：添加登录日志功能

**需要创建的实体**:
```java
// LoginLog.java
@Data
@TableName("login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private LocalDateTime loginTime;
    private String ipaddr;
    private String loginLocation;
    private String browser;
    private String os;
    private String status;
    private String msg;
}
```

**需要创建的Service**:
```java
// ILoginLogService.java
public interface ILoginLogService extends IService<LoginLog> {
    void recordLoginLog(Long userId, String status, String msg);
}
```

**集成到登录流程**:
```java
// 在 LoginServiceImpl.login() 方法中添加
try {
    // ... 登录逻辑
    loginLogService.recordLoginLog(user.getId(), "0", "登录成功");
} catch (Exception e) {
    loginLogService.recordLoginLog(null, "1", e.getMessage());
    throw e;
}
```

---

### 优先级 P2（2-4周内完成）

#### 任务6：会员服务开发

**待实现功能**:
- 会员信息管理 CRUD
- 会员登录功能
- 会员等级管理
- 积分系统
- 会员画像

**关键接口**:
| 模块 | 接口 | 路径 |
|------|------|------|
| 会员管理 | 列表 | /member/info/list |
| 会员管理 | 详情 | /member/info/{id} |
| 会员登录 | 登录 | /member/login |
| 积分系统 | 查询积分 | /member/points |
| 积分系统 | 积分变动 | /member/points/change |

---

#### 任务7：商品服务开发

**待实现功能**:
- 商品信息管理
- 商品分类管理
- 品牌管理
- 商品规格管理
- 库存管理

**数据库设计**:
```sql
-- 商品表
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `brand_id` bigint DEFAULT NULL COMMENT '品牌ID',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `stock` int NOT NULL DEFAULT 0 COMMENT '库存',
  `status` char(1) DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID',
  `order_num` int DEFAULT 0 COMMENT '显示顺序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

#### 任务8：订单服务开发

**待实现功能**:
- 订单创建流程
- 订单状态管理
- 订单查询
- 订单取消

**关键点**:
- 使用 Seata 分布式事务保证数据一致性
- 库存扣减需要使用分布式锁
- 订单状态机设计

---

#### 任务9：支付服务开发

**待实现功能**:
- 支付渠道对接（模拟）
- 支付状态管理
- 支付回调处理
- 退款流程
- 对账功能

---

## 🧪 测试验证方案

### 1. 网关功能测试

**启动命令**:
```bash
# 1. 启动Nacos（如果未启动）
cd docker-compose/env
docker-compose -f docker-compose-env.yml up -d nacos

# 2. 启动管理后台
mvn spring-boot:run -pl corki-sca-admin

# 3. 启动网关
mvn spring-boot:run -pl corki-sca-gateway
```

**测试用例**:

```bash
# 测试1：跨域测试
curl -X OPTIONS http://localhost:10006/admin/test \
  -H "Origin: http://localhost:8080" \
  -H "Access-Control-Request-Method: GET" \
  -v

# 预期结果：返回200，响应头包含 Access-Control-Allow-Origin: *

# 测试2：白名单测试
curl http://localhost:10006/admin/login/captchaImage?uuid=test \
  -v

# 预期结果：返回验证码数据，不需要认证

# 测试3：认证拦截测试
curl http://localhost:10006/admin/system/user/list \
  -v

# 预期结果：返回401或403，提示未登录

# 测试4：登录测试
curl -X POST http://localhost:10006/admin/login/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "code": "1234",
    "uuid": "test-uuid"
  }' \
  -v

# 预期结果：返回token信息

# 测试5：Token认证测试
curl http://localhost:10006/admin/system/user/list \
  -H "satoken: your-token-here" \
  -v

# 预期结果：返回用户列表数据
```

### 2. 管理后台功能测试

**API测试工具**:
- 推荐：Postman 或 Apifox
-Knife4j文档：http://localhost:10006/doc.html

**测试流程**:
1. 访问 Knife4j 文档页面
2. 找到登录接口，获取token
3. 配置全局token
4. 依次测试用户、角色、菜单接口

---

## 📝 开发规范建议

### 1. 代码提交规范

```bash
# 提交格式
git commit -m "类型(范围): 简短描述"

# 示例
git commit -m "feat(gateway): 添加跨域配置"
git commit -m "fix(admin): 修复登录验证码问题"
git commit -m "docs: 更新CLAUDE.md文档"
```

### 2. 分支管理

```bash
# 主分支
main          # 生产环境代码

# 功能分支
feature/gateway-auth      # 网关认证功能
feature/admin-crud        # 管理后台CRUD
feature/member-service    # 会员服务

# 修复分支
fix/login-bug            # 登录bug修复
```

### 3. 代码审查清单

在提交代码前检查：
- [ ] 代码格式化（使用IDE默认格式）
- [ ] 注释清晰易懂
- [ ] 异常处理完善
- [ ] 日志记录适当
- [ ] 敏感信息脱敏
- [ ] SQL注入防护
- [ ] XSS防护

---

## 🔍 常见问题处理

### 问题1：Nacos配置不生效

**解决方案**:
```bash
# 1. 检查Nacos是否启动
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=corki-sca-gateway

# 2. 检查配置是否导入
# 登录Nacos控制台：http://localhost:8848/nacos
# 查看"配置管理"->"配置列表"

# 3. 清除本地缓存
# 删除项目下的 logs 目录
# 重启服务
```

### 问题2：Redis连接失败

**解决方案**:
```bash
# 1. 检查Redis是否启动
docker ps | grep redis

# 2. 测试连接
redis-cli -h localhost -p 16379 ping

# 3. 检查配置
# 查看 Nacos 中的 redis 配置
```

### 问题3：网关路由404

**解决方案**:
```bash
# 1. 检查服务注册
curl http://localhost:8848/nacos/v1/ns/instance/list

# 2. 检查路由配置
# 查看网关的 application-dev.yml

# 3. 查看网关日志
tail -f logs/corki-sca-gateway.log
```

---

## 📈 进度跟踪

### 本周计划（第2周）

- [x] 网关服务完善
- [ ] 用户管理功能激活
- [ ] 角色管理功能激活
- [ ] 菜单管理功能激活

### 下周计划（第3周）

- [ ] getInfo和getRouters实现
- [ ] 登录日志功能
- [ ] 会员服务开发
- [ ] 单元测试补充

---

## 📚 参考资源

### 官方文档
- [Spring Cloud Alibaba](https://spring.io/projects/spring-cloud-alibaba)
- [Sa-Token](https://sa-token.cc/)
- [Nacos](https://nacos.io/zh-cn/)
- [MyBatis-Plus](https://baomidou.com/)

### 项目文档
- `CLAUDE.md` - 项目架构说明
- `DEVELOPMENT_PLAN.md` - 开发计划
- `README.md` - 项目简介

---

## 💡 最佳实践建议

1. **代码复用**：将通用功能抽取到 `corki-sca-common` 模块
2. **事务管理**：涉及多服务操作时使用 `@GlobalTransactional`
3. **异常处理**：使用全局异常处理器，避免try-catch泛滥
4. **日志规范**：使用合适的日志级别（DEBUG/INFO/WARN/ERROR）
5. **安全意识**：永远不要信任前端数据，后端必须再次校验
6. **性能优化**：合理使用缓存，避免N+1查询
7. **文档先行**：先写API文档，再写实现代码

---

**最后更新**: 2025-12-24
**维护者**: Corki Team
**联系方式**: 项目Issues
