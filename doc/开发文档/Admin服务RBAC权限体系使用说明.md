# Admin服务RBAC权限体系使用说明

## 1. 概述

Admin服务已实现完整的RBAC（基于角色的访问控制）权限体系，支持用户、角色、菜单、部门的灵活配置，通过Sa-Token框架实现权限验证。

## 2. 核心组件

### 2.1 实体类
- `User` - 用户实体，包含基本信息、部门关联
- `Role` - 角色实体，支持数据权限配置
- `Menu` - 菜单权限实体，支持目录、菜单、按钮三种类型
- `Dept` - 部门实体，支持树形结构

### 2.2 Service层
- `PermissionServiceImpl` - 权限管理核心服务
- `UserService` - 用户管理服务
- `IRoleService` - 角色管理服务
- `IMenuService` - 菜单管理服务

### 2.3 Controller层
- `LoginController` - 登录认证接口
- `UserController` - 用户管理接口
- `RoleController` - 角色管理接口
- `MenuController` - 菜单管理接口

## 3. 数据库初始化

执行以下SQL脚本初始化数据库：
```sql
-- 在docker-compose/config/sql/目录下
source admin_init.sql;
```

默认数据：
- 超级管理员：admin/admin
- 测试用户：test/test

## 4. API接口说明

### 4.1 认证相关

#### 登录
```http
POST /admin/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

#### 获取用户信息
```http
GET /admin/auth/getInfo
Authorization: Bearer {token}
```

#### 获取路由信息
```http
GET /admin/auth/getRouters
Authorization: Bearer {token}
```

#### 退出登录
```http
POST /admin/auth/logout
Authorization: Bearer {token}
```

### 4.2 用户管理

#### 查询用户列表
```http
GET /admin/system/user/list?page=1&size=10&username=admin
Authorization: Bearer {token}
```

#### 新增用户
```http
POST /admin/system/user
Authorization: Bearer {token}

{
  "username": "newuser",
  "nickname": "新用户",
  "password": "password",
  "email": "user@example.com",
  "mobile": "13800138000"
}
```

### 4.3 角色管理

#### 查询角色列表
```http
GET /admin/system/role/list
Authorization: Bearer {token}
```

#### 分配菜单权限
```http
PUT /admin/system/role
Authorization: Bearer {token}

{
  "id": 1,
  "menuIds": [1, 2, 3]
}
```

## 5. 权限注解使用

### 5.1 基础权限验证
```java
@RestController
@RequestMapping("/admin/system/user")
public class UserController {

    @SaCheckLogin  // 验证登录
    @SaCheckPermission("system:user:list")  // 验证权限
    @GetMapping("/list")
    public R<List<User>> list() {
        return R.ok(userService.list());
    }
}
```

### 5.2 角色验证
```java
@SaCheckRole("admin")  // 验证是否拥有admin角色
@DeleteMapping("/delete")
public R<Void> delete() {
    return R.ok();
}
```

### 5.3 多权限验证
```java
@SaCheckPermission(value = {"system:user:add", "system:user:edit"}, mode = SaMode.OR)
public R<Void> multiPermission() {
    return R.ok();
}
```

## 6. 工具类使用

### 6.1 权限检查
```java
// 检查权限
if (PermissionUtils.hasPermission("system:user:add")) {
    // 有权限
}

// 检查角色
if (PermissionUtils.hasRole("admin")) {
    // 是管理员
}

// 检查多个权限
if (PermissionUtils.hasAnyPermissions("system:user:add,system:user:edit")) {
    // 有任意一个权限
}
```

### 6.2 密码处理
```java
// 密码加密
String encodedPassword = PermissionUtils.encryptPassword("rawPassword");

// 密码验证
boolean matches = PermissionUtils.matchesPassword("rawPassword", encodedPassword);
```

## 7. 数据权限

### 7.1 数据权限范围
- **全部数据权限**：查看所有数据
- **自定义数据权限**：查看指定部门数据
- **本部门数据权限**：仅查看本部门数据
- **本部门及以下数据权限**：查看本部门及子部门数据

### 7.2 配置数据权限
```java
// 在角色中配置数据权限
role.setDataScope(3);  // 3-本部门数据权限

// 在Service中应用数据权限
public List<User> selectUserList(User user) {
    // 根据当前用户的数据权限过滤数据
    if (!PermissionUtils.isAdmin()) {
        // 非管理员，应用数据权限
        applyDataScope(user);
    }
    return userMapper.selectUserList(user);
}
```

## 8. 操作日志

系统自动记录关键操作日志，包括：
- 登录日志（login_log表）
- 操作日志（operation_log表）

### 8.1 开启日志记录
```java
@RestController
public class UserController {

    @PostMapping("/add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody User user) {
        return R.ok();
    }
}
```

## 9. 前端集成

### 9.1 路由配置
```javascript
// 获取动态路由
getRouters().then(res => {
  const routerData = res.data
  this.$router.addRoutes(routerData)
})
```

### 9.2 权限指令
```vue
<template>
  <!-- 按钮权限 -->
  <el-button v-permission="'system:user:add'">新增</el-button>

  <!-- 角色权限 -->
  <el-button v-role="'admin'">管理员功能</el-button>
</template>
```

## 10. 注意事项

1. **Token管理**：默认Token有效期为30天，可在配置文件中调整
2. **密码加密**：使用BCrypt加密，不可逆
3. **超级管理员**：用户ID为1的用户为超级管理员，拥有所有权限
4. **数据权限**：需要在具体的业务实现中应用数据权限过滤
5. **权限缓存**：权限信息缓存于Redis中，修改权限后需要清除缓存

## 11. 常见问题

### 11.1 权限不生效
- 检查是否正确使用`@SaCheckPermission`注解
- 确认用户是否拥有对应权限
- 检查Sa-Token配置是否正确

### 11.2 登录失败
- 检查用户状态是否正常
- 确认密码是否正确
- 检查数据库连接

### 11.3 数据权限问题
- 确认角色数据权限配置
- 检查用户所属部门
- 验证业务代码中是否应用数据权限