# Nginx 部署指南

本指南将帮助你安装和配置 Nginx，以支持两个前端项目和一个后端项目，使用 `admin.pdcspace.com` 和 `www.pdcspace.com` 两个域名。

## 1. 安装 Nginx

### Ubuntu/Debian
```bash
sudo apt update
sudo apt install nginx
```

### CentOS/RHEL
```bash
sudo yum install epel-release
sudo yum install nginx
```

## 2. 目录结构设置

创建必要的目录结构：

```bash
# 创建网站根目录
sudo mkdir -p /var/www/html/client
sudo mkdir -p /var/www/html/admin

# 创建SSL证书目录
sudo mkdir -p /etc/nginx/ssl

# 设置目录权限
sudo chown -R nginx:nginx /var/www/html
sudo chmod -R 755 /var/www/html
```

## 3. 获取和配置 SSL 证书

### 使用 Let's Encrypt (推荐)

1. 安装 Certbot：

```bash
# Ubuntu/Debian
sudo apt install certbot python3-certbot-nginx

# CentOS/RHEL
sudo yum install certbot python3-certbot-nginx
```

2. 获取证书：

```bash
sudo certbot --nginx -d www.pdcspace.com -d admin.pdcspace.com
```

3. 证书自动续期：

```bash
sudo systemctl status certbot.timer  # 检查自动续期是否启用
```

### 使用自签名证书（仅用于测试）

```bash
# 为 www.pdcspace.com 生成证书
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout /etc/nginx/ssl/www.pdcspace.com.key \
  -out /etc/nginx/ssl/www.pdcspace.com.crt \
  -subj "/C=US/ST=State/L=City/O=Organization/CN=www.pdcspace.com"

# 为 admin.pdcspace.com 生成证书
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout /etc/nginx/ssl/admin.pdcspace.com.key \
  -out /etc/nginx/ssl/admin.pdcspace.com.crt \
  -subj "/C=US/ST=State/L=City/O=Organization/CN=admin.pdcspace.com"
```

## 4. 部署前端项目

将前端项目构建后的文件复制到相应目录：

```bash
# 用户端前端项目
sudo cp -r /path/to/client/build/* /var/www/html/client/

# 管理后台前端项目
sudo cp -r /path/to/admin/build/* /var/www/html/admin/
```

## 5. 配置 Nginx

1. 将提供的 `nginx.conf` 文件复制到 Nginx 配置目录：

```bash
sudo cp nginx.conf /etc/nginx/nginx.conf
```

2. 检查配置文件语法：

```bash
sudo nginx -t
```

3. 如果语法检查通过，重启 Nginx：

```bash
sudo systemctl restart nginx
```

## 6. 配置后端服务

确保你的后端服务运行在配置文件中指定的端口上（默认为 8080）。

如果后端服务运行在不同的端口或主机上，请修改 `nginx.conf` 中的 `upstream backend_server` 部分：

```nginx
upstream backend_server {
    server 127.0.0.1:8080;  # 修改为你的后端服务地址和端口
    keepalive 32;
}
```

## 7. 防火墙配置

确保防火墙允许 HTTP (80) 和 HTTPS (443) 端口：

```bash
# Ubuntu/Debian with UFW
sudo ufw allow 'Nginx Full'

# CentOS/RHEL with firewalld
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

## 8. 测试配置

1. 访问 https://www.pdcspace.com 确认用户端前端正常工作
2. 访问 https://admin.pdcspace.com 确认管理后台前端正常工作
3. 测试 API 请求是否正确代理到后端服务

## 9. 日志文件位置

- 访问日志：`/var/log/nginx/access.log`
- 错误日志：`/var/log/nginx/error.log`

## 10. 常见问题和解决方案

### 问题：502 Bad Gateway

**可能原因**：后端服务未运行或无法访问
**解决方案**：
- 确认后端服务正在运行：`sudo systemctl status your-backend-service`
- 检查后端服务端口是否正确：`netstat -tulpn | grep 8080`
- 检查 SELinux 是否阻止了连接（CentOS/RHEL）：`sudo setsebool -P httpd_can_network_connect 1`

### 问题：SSL 证书错误

**可能原因**：证书路径错误或证书过期
**解决方案**：
- 检查证书路径是否正确
- 检查证书是否过期：`openssl x509 -in /etc/nginx/ssl/www.pdcspace.com.crt -text -noout | grep "Not After"`
- 如果使用 Let's Encrypt，手动更新证书：`sudo certbot renew`

### 问题：前端路由不工作（刷新页面 404）

**可能原因**：Nginx 配置中缺少前端路由支持
**解决方案**：
- 确认配置中包含 `try_files $uri $uri/ /index.html;`

## 11. 性能优化建议

1. **启用浏览器缓存**：已在配置中设置静态资源缓存
2. **配置 Gzip 压缩**：已在配置中启用
3. **使用 HTTP/2**：已在配置中启用
4. **考虑使用 CDN**：对于静态资源，可以考虑使用 CDN 服务
5. **监控和调整**：
   - 使用 `nginx -V` 查看编译选项
   - 根据服务器性能调整 `worker_processes` 和 `worker_connections`

## 12. 安全加固建议

1. **定期更新**：
   ```bash
   sudo apt update && sudo apt upgrade  # Ubuntu/Debian
   sudo yum update  # CentOS/RHEL
   ```

2. **禁用不需要的 HTTP 方法**：
   ```nginx
   # 添加到 server 块中
   if ($request_method !~ ^(GET|POST|HEAD)$) {
       return 405;
   }
   ```

3. **配置 Content-Security-Policy**：已在配置中设置基本策略，可根据需要调整

4. **设置访问限制**：
   ```nginx
   # 限制特定路径的访问
   location /admin-only/ {
       allow 192.168.1.0/24;  # 允许内部网络
       deny all;              # 拒绝其他所有
   }
   ```

5. **使用 fail2ban 防止暴力攻击**：
   ```bash
   sudo apt install fail2ban  # Ubuntu/Debian
   sudo yum install fail2ban  # CentOS/RHEL
   ```
