 # BBS论坛项目

  ## 一、环境要求

  - JDK 21
  - MySQL 8.0+
  - Tomcat 9+

  ## 二、数据库初始化

  1. 登录MySQL：`mysql -u root -p`
  2. 执行建表脚本：
     ```bash
     mysql -u root -p < sql/bbs_init.sql
     或进入MySQL后执行：source sql/bbs_init.sql

  三、项目配置

  数据库连接信息（已配置在代码中）：
  - 地址：127.0.0.1:3306
  - 数据库名：bbs_db
  - 用户名：root
  - 密码：123456

  四、运行项目

  1. 进入项目目录：
  cd D:\javaweb_code\bbs_test
  2. 清理并打包：
  mvn clean package
  3. 将 target/bbs_test-1.0-SNAPSHOT.war 部署到Tomcat的webapps目录
  4. 启动Tomcat，访问：http://localhost:8080/bbs_test/

  五、账号信息

  ┌────────┬────────┬──────────┐
  │  角色  │ 用户名 │   密码   │
  ├────────┼────────┼──────────┤
  │ 管理员 │ admin  │ admin123 │
  └────────┴────────┴──────────┘
普通用户可以自己注册

  六、功能页面

  - 首页：index.jsp
  - 后台管理：admin.jsp
  - 帖子详情：post.jsp?id=xxx
  - 发布帖子：publish.jsp
  - 用户注册：register.jsp
