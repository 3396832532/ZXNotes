## MYSQL高级(二)

数据库存放目录

`ps -ef|grep mysql`  可以看到：

* 数据库目录：   `  datadir=/var/lib/mysql `
* pid文件目录： `--pid-file=/var/lib/mysql/bigdata01.pid`

MySQL核心目录：

```shell

	/var/lib/mysql :mysql 安装目录
	/usr/share/mysql:  配置文件
	/usr/bin：命令目录（mysqladmin、mysqldump等）
	/etc/init.d/mysql启停脚本
```

  MySQL配置文件

```java
my-huge.cnf	高端服务器  1-2G内存
my-large.cnf   中等规模
my-medium.cnf  一般
my-small.cnf   较小
但是，以上配置文件mysql默认不能识别，默认只能识别 /etc/my.cnf
如果需要采用 my-huge.cnf ：
cp /usr/share/mysql/my-huge.cnf /etc/my.cnf
注意：mysql5.5默认配置文件/etc/my.cnf；Mysql5.6 默认配置文件/etc/mysql-default.cnf
```

Mysql字符编码

```mysql
sql  :  show variables like '%char%' ;
可以发现部分编码是 latin,需要统一设置为utf-8
设置编码：
vi /etc/my.cnf:
[mysql]
default-character-set=utf8
[client]
default-character-set=utf8

[mysqld]
character_set_server=utf8
character_set_client=utf8
collation_server=utf8_general_ci

重启Mysql:  service mysql restart
sql  :  show variables like '%char%' ;
注意事项：修改编码 只对“之后”创建的数据库生效，因此 我们建议 在mysql安装完毕后，第一时间 统一编码。
```

Mysql逻辑分层: 连接层 服务层 引擎层 存储层

两种索引的区别:
* InnoDB(默认) ：事务优先 （适合高并发操作；行锁）
* MyISAM ：性能优先  （表锁）
