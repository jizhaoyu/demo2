# GitHub 推送指南

## 当前状态

✅ Git 仓库已初始化  
✅ 代码已提交（提交信息：第一次）  
✅ 远程仓库地址已配置：https://github.com/jizhaoyu/demo2.git  
❌ 推送失败（网络问题）

---

## 手动推送步骤

### 方法一：使用 HTTPS（推荐）

在项目根目录打开命令行，执行：

```bash
# 1. 确认远程地址
git remote -v

# 2. 如果不是 HTTPS，修改为 HTTPS
git remote set-url origin https://github.com/jizhaoyu/demo2.git

# 3. 推送到 GitHub
git push -u origin main
```

**首次推送会要求输入 GitHub 账号密码或 Personal Access Token**

---

### 方法二：使用 SSH（需要配置密钥）

#### 2.1 生成 SSH 密钥（如果还没有）

```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

按回车使用默认路径，设置密码（可选）。

#### 2.2 复制公钥

```bash
# Windows
type %USERPROFILE%\.ssh\id_ed25519.pub

# 或者手动打开文件
# C:\Users\你的用户名\.ssh\id_ed25519.pub
```

#### 2.3 添加到 GitHub

1. 登录 GitHub
2. 点击右上角头像 → Settings
3. 左侧菜单 → SSH and GPG keys
4. 点击 "New SSH key"
5. 粘贴公钥内容，保存

#### 2.4 推送

```bash
# 修改为 SSH 地址
git remote set-url origin git@github.com:jizhaoyu/demo2.git

# 推送
git push -u origin main
```

---

## 常见问题

### 问题1：网络连接失败

**症状：** `Empty reply from server` 或 `Connection timed out`

**解决方案：**
1. 检查网络连接
2. 如果在国内，可能需要配置代理
3. 尝试使用手机热点

### 问题2：认证失败

**症状：** `Authentication failed`

**解决方案（HTTPS）：**
GitHub 已不支持密码认证，需要使用 Personal Access Token：

1. 登录 GitHub
2. Settings → Developer settings → Personal access tokens → Tokens (classic)
3. Generate new token
4. 勾选 `repo` 权限
5. 生成后复制 token
6. 推送时用 token 代替密码

### 问题3：SSH 密钥权限被拒绝

**症状：** `Permission denied (publickey)`

**解决方案：**
1. 确认 SSH 密钥已添加到 GitHub
2. 测试连接：`ssh -T git@github.com`
3. 如果失败，重新生成并添加密钥

---

## 推送成功后

访问 https://github.com/jizhaoyu/demo2 查看你的代码。

接下来可以：
1. 按照 `Railway部署指南.md` 部署到 Railway
2. 继续开发新功能
3. 邀请团队成员协作

---

## 后续提交流程

每次修改代码后：

```bash
# 1. 查看修改
git status

# 2. 添加修改
git add .

# 3. 提交
git commit -m "描述你的修改"

# 4. 推送
git push
```

---

## 需要帮助？

如果推送仍然失败，可以：
1. 检查 GitHub 仓库是否存在
2. 确认你有该仓库的写入权限
3. 尝试在 GitHub Desktop 中操作
4. 联系网络管理员检查防火墙设置
