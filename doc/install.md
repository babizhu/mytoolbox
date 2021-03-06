## docker安装mysql8.0
* 安装
```
docker pull registry.docker-cn.com/library/mysql
docker tag registry.docker-cn.com/library/mysql mysql   
docker run --name mysql -e MYSQL_ROOT_PASSWORD=liulaoye -d -i -p 3306:3306 -v /Users/liukun/docker/mysql/conf.d:/etc/mysql/conf.d -d mysql
```
* 增加用户
```
create user 'liulaoye'@'%' identified with mysql_native_password by 'liulaoye';
```
* 赋权
```
GRANT ALL ON *.* TO `liulaoye`@`$` WITH GRANT OPTION;
```

* 修改mysql缺省验证方式

在/Users/liukun/docker/mysql/conf.d/docker.conf中加上
> [mysqld]<br>
skip-host-cache<br>
skip-name-resolve<br>
default_authentication_plugin = mysql_native_password



## 杂项
* 删除容器
```
docker stop mysql
docker rm $(docker ps -a -q)
```
* 重启容器（mysql）
```
docker restart mysql
```
* 进入容器（mysql）
```
docker exec -it mysql /bin/bash
```
* 查看日志（mysql）
```
docker logs -f mysql
```