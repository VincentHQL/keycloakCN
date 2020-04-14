# keycloakCN
基于keycloak定制化，实现适合cn的操作习惯。

# run

> 通过源码运行

```
mvn spring-boot:run
```

> 通过docker运行(需安装docker和maven环境)
```
./start.sh
```

访问地址 http://localhost:8080/auth 默认账号/密码: admin/admin


# 实现功能
- [ ] 移除注册firstname，lastname
- [ ] 修改默认主题
- [ ] 短信验证登录
- [X] 添加微信社交登录