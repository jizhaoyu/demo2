# DataGrip 导出 SQL 文件详细指南

## 🎯 目标
导出 `book_trading` 数据库的完整 SQL 文件，用于 Railway 部署

---

## 📝 方法一：一键导出（最简单）

### 步骤 1：右键数据库
在 DataGrip 左侧数据库树中：
```
book_trading (右键) → SQL 脚本 → 将数据导出到文件
```

### 步骤 2：配置导出选项

在弹出的 "导出数据" 对话框中：

#### 基本设置
- **提取器**: 选择 `SQL-INSERT`
- **输出文件**: 点击 `...` 选择保存位置
  - 文件名：`book_trading_railway.sql`
  - 位置：项目的 `backgrond/src/main/resources/sql/` 目录

#### 高级设置（点击 "配置" 按钮）

勾选以下选项：
```
☑ 添加表定义 (DDL)
  └─ 包含 CREATE TABLE 语句

☑ 添加 INSERT 语句
  └─ 包含数据插入语句

☑ 禁用外键检查
  └─ 添加 SET FOREIGN_KEY_CHECKS = 0;

☑ 添加 DROP TABLE 语句
  └─ 添加 DROP TABLE IF EXISTS 语句

☑ 添加数据库选择
  └─ 添加 USE database 语句

☐ 事务语句
  └─ 不需要事务（Railway 导入时不需要）
```

#### 格式设置
```
Row separator: \n (Unix style)
Quote identifiers: Backticks (`)
```

### 步骤 3：选择要导出的表

在表列表中，确保选中所有表：
```
☑ user
☑ category
☑ book
☑ favorite
☑ orders
☑ review
☑ message
```

### 步骤 4：点击 "导出" 开始导出

---

## 📝 方法二：使用 mysqldump（推荐专业用户）

### 步骤 1：打开 mysqldump 工具

```
book_trading (右键) → 使用 'mysqldump' 转储
```

### 步骤 2：配置 mysqldump 参数

在命令行参数中添加：
```bash
--add-drop-table
--add-locks
--create-options
--disable-keys
--extended-insert
--set-charset
--single-transaction
--no-create-db
--skip-comments
--default-character-set=utf8mb4
```

### 步骤 3：设置输出文件

```
Output file: backgrond/src/main/resources/sql/book_trading_railway.sql
```

### 步骤 4：点击 "运行" 执行

---

## 📝 方法三：分步导出（精细控制）

### 第一步：导出表结构

1. 展开数据库 → 展开 `表` 文件夹
2. `Ctrl+A` 全选所有表
3. 右键 → `SQL 脚本` → `生成 DDL 到剪贴板`
4. 在弹出的对话框中配置：
   ```
   ☑ CREATE TABLE
   ☑ 索引
   ☑ 外键
   ☑ 注释
   ☐ DROP 语句 (可选)
   ```
5. 点击 "复制到剪贴板" 或 "保存到文件"
6. 保存为 `schema.sql`

### 第二步：导出数据

1. 再次全选所有表
2. 右键 → `SQL 脚本` → `将数据导出到文件`
3. 配置：
   ```
   提取器: SQL-INSERT
   ☐ 添加表定义 (已有 schema.sql)
   ☑ 添加 INSERT 语句
   ☑ 禁用外键检查
   ```
4. 保存为 `data.sql`

### 第三步：合并文件（可选）

手动创建 `book_trading_railway.sql`：
```sql
-- =============================================
-- Railway 数据库初始化脚本
-- =============================================

USE `railway`;  -- Railway 默认数据库名

SET FOREIGN_KEY_CHECKS = 0;

-- 粘贴 schema.sql 的内容

-- 粘贴 data.sql 的内容

SET FOREIGN_KEY_CHECKS = 1;
```

---

## ✅ 导出后的检查清单

导出完成后，打开 SQL 文件检查：

### 1. 文件开头应该有：
```sql
SET FOREIGN_KEY_CHECKS = 0;
-- 或
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
```

### 2. 包含所有表的 CREATE TABLE 语句：
```sql
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  ...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3. 包含所有表的 INSERT 语句：
```sql
INSERT INTO `category` VALUES (1,'文学',1,'2025-01-01 00:00:00','2025-01-01 00:00:00',0);
INSERT INTO `user` VALUES (1,'admin','$2a$10$...','admin@booktrading.com',...);
```

### 4. 文件结尾应该有：
```sql
SET FOREIGN_KEY_CHECKS = 1;
```

### 5. 验证数据完整性：
```sql
-- 应该有 6 个分类
SELECT COUNT(*) FROM category;  -- 结果应该是 6

-- 应该有 6 个用户（1个管理员 + 5个测试用户）
SELECT COUNT(*) FROM user;  -- 结果应该是 6

-- 应该有 25+ 本书
SELECT COUNT(*) FROM book;  -- 结果应该 >= 25
```

---

## 🔧 常见问题解决

### 问题 1：导出的 SQL 文件没有 DROP TABLE 语句

**解决方案：**
在导出配置中勾选 `添加 DROP TABLE 语句`

或手动在每个 CREATE TABLE 前添加：
```sql
DROP TABLE IF EXISTS `table_name`;
```

### 问题 2：导出的 INSERT 语句太长

**解决方案：**
在 mysqldump 参数中添加：
```bash
--extended-insert=FALSE
```
这样每条记录会生成一个单独的 INSERT 语句。

### 问题 3：字符集问题

**解决方案：**
确保导出时指定字符集：
```sql
/*!40101 SET NAMES utf8mb4 */;
```

在 mysqldump 参数中添加：
```bash
--default-character-set=utf8mb4
```

### 问题 4：外键约束导致导入失败

**解决方案：**
确保 SQL 文件开头有：
```sql
SET FOREIGN_KEY_CHECKS = 0;
```
结尾有：
```sql
SET FOREIGN_KEY_CHECKS = 1;
```

### 问题 5：Railway 数据库名不是 book_trading

**解决方案：**
Railway 默认数据库名是 `railway`，需要修改 SQL 文件：

将：
```sql
CREATE DATABASE IF NOT EXISTS `book_trading`;
USE `book_trading`;
```

改为：
```sql
USE `railway`;
```

或者直接删除 CREATE DATABASE 和 USE 语句，在 Railway 的 Data 面板中执行时会自动使用当前数据库。

---

## 🎯 最终文件结构

导出完成后，你应该有：

```
backgrond/src/main/resources/sql/
├── schema.sql              # 表结构（已有）
├── data.sql                # 初始数据（已有）
└── book_trading_railway.sql  # 完整导出（新增，用于 Railway）
```

---

## 📤 导入到 Railway

导出完成后，在 Railway 中导入：

### 方法 1：使用 Data 面板（推荐）
1. 打开 Railway → 选择 MySQL 服务
2. 点击 "Data" 标签页
3. 点击 "Query" 按钮
4. 复制 `book_trading_railway.sql` 的内容
5. 粘贴到查询框
6. 点击 "Run" 执行

**注意**：如果 SQL 文件较大，建议使用方法 2 命令行导入

### 方法 2：使用命令行
```bash
mysql -h xxxxx.railway.app -P 3306 -u root -p railway < book_trading_railway.sql
```

---

## ✨ 小技巧

### 1. 快速导出单个表
```
右键表 → SQL 脚本 → 将数据导出到文件
```

### 2. 导出查询结果
```sql
SELECT * FROM book WHERE status = 1;
-- 右键结果集 → 导出数据 → SQL-INSERT
```

### 3. 比较两个数据库
```
工具 → 比较数据库
```

### 4. 自动格式化 SQL
导出后，打开 SQL 文件：
```
Ctrl+Alt+L (Windows/Linux)
Cmd+Option+L (Mac)
```

### 5. 中文界面快捷键
- 全选表：`Ctrl+A`
- 刷新数据库：`Ctrl+F5`
- 执行 SQL：`Ctrl+Enter`
- 格式化代码：`Ctrl+Alt+L`

---

完成导出后，你就可以将 SQL 文件导入到 Railway 了！
