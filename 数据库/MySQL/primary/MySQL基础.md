# <font color = red>MySQL基础
 - 数据库的基本操作
 - 数据表的基本操作
 - 数据类型和运算符
 - `Mysql`函数
 - 查询数据
 - 插入、更新与删除数据
 - 索引
***
## <font color = red>一、数据库的基本操作
### 1、基本命令
**登陆数据库命令:**
```mysql
mysql -h localhost -u root -p
```
**创建数据库命令:**
```sql
create database test_db;
```
**查看已经创建的数据库的定义**
```mysql
show create database test_db;
```
**查看已经存在的所有数据库:**
```mysql
show databases;
```
**删除数据库**

```sql
drop database test_db;
```
注意删除数据库时要小心，不会给出提示，数据和数据表会一同删除。


### 2、数据库储存引擎

#### 1)、查看引擎命令
使用如下命令查看系统所支持的引擎类型: 

```mysql
show engines;
```

#### 2)、InnoDB引擎

InnoDB 是事务型数据库的首选引擎，**支持事务安全表 (ACID ) ，支持行锁定和外键。**

InnoDB 作为**默认存储引擎**，特性有:

*  InnoDB 给 MySQL 提供了**具有提交、回滚和崩溃恢复能力的事务安全 (ACID 兼容)存储引擎**。InnoDB 锁定在**行级**并且也在 SELECT 语句中提供一个类似 Oracle 的**非锁定读**。这些功能增加了多用户部署和性能。在 SQL 查询中，可以自由地将 **InnoDB 类型的表与其他MySQL 的表的类型混合起来**，甚至在同一个查询中也可以混合。
*  InnoDB 是**为处理巨大数据量的最大性能设计**。它的 CPU 效率可能是任何其他基于磁盘的关系数据库引擎所不能匹敌的。
*  InnoDB 存储引擎完全与 MySQL 服务器整合，I**nnoDB 存储引擎为在主内存中缓存数据和索引而维持它自己的缓冲池**。InnoDB **将它的表和索引存在一个逻辑表空间中，表空间可以包含数个文件〈或原始磁盘分区) 。**这与 MyISAM 表不同，比如在 `MyISAM` 表中每个表被存在分离的文件中。InnoDB 表可以是任何尺寸,，即使在文件尺寸被限制为 2GB 的操作系统上。
*  InnoDB **支持外键完整性约束 (FOREIGN KEY)** 。存储表中的数据时, 每张表的存储都按主键顺序存放, 如果没有显示在表定义时指定主键，InnoDB 会为每一行生成一个 6B 的ROWID，并以此作为主键。
*  InnoDB 被用在众多需要高性能的大型数据库站点上。
*  InnoDB 不创建目录，使用 InnoDB 时，MySQL 将在 MySQL 数据目录下创建一个名为`ibdata1` 的 10MB 大小的自动扩展数据文件，以及两个名为` ib_logfile0` 和` ib_logfilel `的 `5MB`大小的日志文件。

InnoDB 不创建目录，使用 InnoDB 时，MySQL 将在 MySQL 数据目录下创建一个名为
ibdatal 的 10MB 大小的自动扩展数据文件，以及两个名为 ib_logfile0 和 ib_logfilel 的 SMB
大小的日志文件。

#### 3)、MyISAM引擎

MyISAM 基于 ISAM 的存储引擎，并对其进行扩展。它是在 **Web、数据存储**和其他应用
环境下最常使用的存储引擎之一。MyISAM 拥有较高的插入、查询速度，**但不支持事务**。在
MyISAM 主要特性有:

* **大文件** (达 63 位文件长度) 在支持大文件的文件系统和操作系统上被支持。
* 当把删除、更新及插入操作混合使用的时候，动态尺寸的行产生更少碎片。这要通过合并相邻被删除的块，以及若下一个块被删除，就扩展到下一块来自动完成。
* 每个 MyISAM 表最大索引数是 64，这可以通过重新编译来改变。每个索引最大的列数是 16 个。
* 最大的键长度是 1000B，这也可以通过编译来改变。对于键长度超过 250B 的情况，一个超过 1024B 的键将被用上。
* **BLOB 和TEXT 列可以被索引**。
* **NULL 值被允许在索引的列中。这个值占每个键的 0~1 个字节**。
* 所有数字键值以高字节优先被存储以允许一个更高的索引压缩。
* 每表一个`AUTO_INCREMENT` 列的内部处理。MyISAM 为 `INSERT` 和 `UPDATE` 操作自动更新这一列。这使得 `AUTO_INCREMENT `列更快〈至少 10%) 。在序列顶的值被删除之后就不能再利用。
* 可以把**数据文件和索引文件**放在不同目录。
* 每个字符列可以有不同的字符集。
* 有VARCHAR 的表可以固定或动态记录长度。
* VARCHAR 和CHAR 列可以多达 64KB。

> 使用 MyISAM 引擎创建数据库，将生产 3 个文件。文件的名字以**表的名字**开始，扩展名指出文件类型， `frm`文件存储表定义，数据文件的扩展名为`.MYD (MYData)`，索引文件的扩展名是`.MYI MYIndex)` 。

#### 4)、MEMORY引擎

MEMORY 存储引擎**将表中的数据存储到内存中，为查询和引用其他表数据提供快速访问**。MEMORY 主要特性有:

* MEMORY 表的每个表可以有多达 32 个索引，每个索引 16 列，以及 500B 的最大键长度。     
* MEMORY 存储引擎执行 **HASH 和 BTREE** 索引。
* 可以在一个MEMORY 表中有非唯一键。
* MEMORY 表使用一个固定的记录长度格式。
* MEMORY 不支持BLOB 或TEXT 列。
* MEMORY 支持 `AUTO_INCREMENT` 列和**对可包含NULL 值的列的索引**。
* MEMORY 表在所有客户端之间共享 (就像其他任何非 TEMPORARY 表) 。
* **MEMORY 表内容被存在内存中，内存是 MEMORY 表和服务器在查询处理时的空闲中创建的内部表共享**。
* 当不再需要 MEMORY 表的内容时，**要释放被 MEMORY 表使用的内存**，应该执行`DELETE FROM` 或TRUNCATE TABLE，或者删除整个表 〈使用DROP TABLE) 。

#### 5)、存储引擎的选择</font>

不同存储引擎都有各自的特点，以适应不同的需求。下面是各种引擎的不同的功能: 

![](images/1_存储引擎选择.png)

* 如果要提供提交、回滚和崩溃恢复能力的**事务安全** (ACID 兼容) 能力，并要求实现**并发控制**，InnoDB 是个很好的选择；

* 如果数据表主要用来**插入和查询记录**，则 MyISAM 引擎能提供较**高的处理效率**；

* 如果只是**临时存放数据**，数据量不大，并且**不需要较高的数据安全性**，可以选择将**数据保存在内存中**的 Memory 引擎，MySQL 中使用该引擎作为临时表，存放查询的中间结果；

* 如果只有 **INSERT 和 SELECT 操作**，可以选择 Archive 引擎，Archive 存储引擎支持高并发的插入操作，但是本身**并不是事务安全**的。Archive 存储引擎非常适合**存储归档数据**，如记录日志信息可以使用 Archive 引擎。

使用哪一种引擎要根据需要灵活选择, 一个数据库中多个表可以使用不同引擎以满足各种性能和实际需求。使用合适的存储引擎，将会提高整个数据库的性能。

>  顺便说一下`Mysql`中单行注释是`#`，而不是`--`。

***

## <font color = red>二、数据表的基本操作

### 1、创建数据表

```sql
use test_db;
create table tb_emp1
(
	id int(11),
	name varchar(15),
	deptID int(11),
	salary 	float
);
```
使用下面语句查看此数据库存在的表
```mysql
show tables;
```
#### 1)、主键约束

>  主键，又称主码，是表中**一列或多列的组合**。主键约束〈Primary Key Constraint) 要求**主键列的数据唯一，并且不允许为空`!= null`**。主键能够唯一地标识表中的一条记录，可以结合外键**来定义不同数据表之间的关系，** 并且可以加快数据库查询的速度。主键和记录之间的关系如同身份证和人之间的关系，它们之间是一一对应的。主键分为两种类型: **单字段主键和多字段联合主键。**

 - 单字段主键；
 - 在定义完所有列之后定义主键；
 - 多字段联合主键；

单字段约束: 
```sql
create table tb_emp2
(
	id int(11) primary key,
	name varchar(15),
	deptID int(11),
	salary 	float
);
```
后面约束: 
```sql
create table tb_emp3
(
	id int(11),
	name varchar(15),
	deptID int(11),
	salary 	float,
	primary key(id)
);
```
联合约束：假设没有主键`id`，可以通过`name`和`deptID`来确定一个唯一的员工。

```sql
create table tb_emp4
(
	id int(11),
	name varchar(15),
	deptID int(11),
	salary 	float,
	primary key(name,deptID)
);
```
#### 2)、外键约束

* 外键用来在两个表的数据之间建立链接， 它可以是一列或者多列。一个表可以有一个或多个外键。**外键对应的是参照完整性**，一个表的外键可以为空值，**若不为空值，则每一个外键值必须等于另一个表中主键的某个值。**
* 外键 : 首先它是表中的一个字段，**它可以不是本表的主键，但对应另外一个表的主键。**外键主要作用是保证数据引用的完整性， 定义外键后，**不允许删除在另一个表中具有关联关系的行**。外键的作用是保持数据的一致性、完整性。例如，部门表 `tb_dept `的主键是`id`，在员工表`tb_emp5`中有一个键 `deptId` 与这个` id` 关联。

有关主表和从表:

* 主表〈父表) : 对于两个具有关联关系的表而言，相关联字段中**主键所在的那个表**即是主

表。

* 从表〈子表) : 对于两个具有关联关系的表而言，相关联字段中**外键所在的那个表**即是从

表。

需要注意: 

 - <u>子表的外键必须要关联父表的**主键**</u>；
 - **相关联的数据类型必须匹配**；
 - **先删子表，再删父表**；

下面的例子**tb_emp5(员工表)中的deptID关联部门表中的ID(主键)**：
```sql
//父表
create table tb_dept1
(
	id int(11)primary key,
	name varchar(22) not null,
	location varchar(50)
)
```

```sql
//子表
create table tb_emp5
(
	id int(11) primary key,
	name varchar(25),
	deptID int(11),
	salary float,
	constraint fk_emp5_dept foreign key(deptID) references tb_dept1(id)
)
```
#### 3)、非空约束

非空约束指定的字段不能为空，如果添加数据的时候没有指定值，会报错。

```sql
create table tb_emp6
(
	id int(11) primary key,
	name varchar(15) not null,
	deptID int(11),
	salary 	float
);
```
#### 4)、唯一性约束

* 唯一性要求该列唯一；
* **允许为空，但只能出现一个空值；**
* 唯一性可以确保一列或几列不出现重复值；

```sql
create table tb_dept2
(
	id int(11)primary key,
	name varchar(22) unique,
	location varchar(50)
);
```
```sql
create table tb_dept3
(
	id int(11)primary key,
	name varchar(22),
	location varchar(50),
	constraint N_uq unique(name)  #N_uq是约束名
);
```
**注意`UNIQUE`和主键约束(`PRIMARY KEY `)的区别:** 

*  <u>一个表中可以有多个字段声明为`UNIQUE`，但只能有一个`PRIMARY KEY` 声明；</u>
*  <u>声明为 `PRIMAY KEY` 的列不允许有空值，但是声明为 `UNIQUE`的字段允许空值 (NULL) 的存在。</u>

#### 5)、默认约束

指定了默认约束之后，如果没有指定值，就用默认的。

```sql
create table tb_emp7
(
	id int(11) primary key,
	name varchar(15) not null,
	deptID int(11) default 111,
	salary 	float
); 
```
#### 6)、设置表的属性自加

![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205215623352.png)

```sql
create table tb_emp8
(
	id int(11) primary key auto_increment,
	name varchar(15) not null,
	deptID int(11),
	salary 	float
); 
```
#### 7)、查看表的结构

desc可以查看表的字段名，数据类型，是否为主键，是否默认值。

```sql
desc tb_emp8;
```
效果如图

![这里写图片描述](https://img-blog.csdn.net/20180412152855997?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

查看表的详细结构，可以看储存引擎，和字符编码

```sql
show create table tb_emp8;
```
### 2、修改数据表

#### 1)、修改表名

将表`tb_dept3`改为`tb_deptment3`

```sql
alter table tb_dept3 rename tb_deptment3;
```
查看数据库中的表
```sql
show tables;
```
修改表名不会改变结构，`desc`前后结果一样。

#### 2)、修改字段的数据类型

```sql
# 修改表字段的数据类型,把name列的数据类型改为varchar(33)
alter table tb_dept1 modify name varchar(33);
```
#### 3)、修改字段名

```sql
#修改表的字段名,不改数据类型
alter table tb_dept1 change location loc varchar(50);
```

```sql
#修改表的字段名,并且改变数据类型
alter table tb_dept1 change loc location varchar(60);
```
`change`也可以只改变数据类型，但是一般不要**轻易改变数据类型**。

#### 4)、添加字段



有三种添加方式，① 默认在最后面添加，②在第一个位置添加，③和指定的位置添加

```sql
#添加字段(默认在最后面添加)
alter table tb_dept1 add managerID int(10);
```
```sql
#添加字段(默认在最后面添加)(非空约束)
alter table tb_dept1 add column1 int(10) not null;
```

```sql
#添加字段(在第一个位置添加)
alter table tb_dept1 add column2 int(10) first;
```

```sql
#添加字段(在指定位置后面添加)
alter table tb_dept1 add column3 int(10) after name;
```
#### 5)、删除字段

```sql
#删除字段
alter table tb_dept1 drop column3;
```
#### 6)、修改字段的排列位置

```sql
#修改字段的排列位置(改到第一个位置)
alter table tb_dept1 modify column1 int(10) first;
#修改字段的位置为指定的位置
alter table tb_dept1 modify column2 int(10) after name;
```
#### 7)、更改表的储存引擎

```sql
#查看数据表的定义
show create table tb_deptment3;
#更改数据表的引擎
alter table tb_deptment3 engine = MyISAM;
```
#### 8)、删除表的外键约束

```sql
create table tb_emp9
(
	id int(11)primary key,
	deptID int(11),
	name varchar(25),
	salary float,
	constraint fk_emp9_dept foreign key(deptID) references tb_dept1(id)
)

#删除外键约束
alter table tb_emp9 drop foreign key fk_emp9_dept;
```
### 3、删除数据表

```sql
#删除表
drop table if exists tb_emp9;
```
注意**删除有关联的数据表的父表的时候，先删除外键再删除父表**

### 4、综合案例小结

```sql
create database company;
use company;
create table offices
(
	officeCode int(10) primary key not null unique,
	city varchar(50) not null,
	address varchar(50),
	country varchar(50) not null,
	postalCode varchar(15) unique
)

create table employees
(
	employeeNumber int(11) primary key not null unique auto_increment,
	lastName varchar(50) not null,
	firstName varchar(50) not null,
	mobile varchar(25) unique,
	officeCode int(10) not null,
	jobTitle varchar(50) not null,
	birth datetime not null,
	note varchar(255),
	sex varchar(5)
)

show tables;
desc employees;

#将mobile字段修改到officeCode后面
alter table employees modify mobile varchar(25) after officeCode;
#将birth的字段名改为employee_birth
alter table employees change birth employee_birth datetime;
#修改sex字段为char(1)类型，非空约束
alter table employees modify sex char(1) not null;
#删除字段note 
alter table employees drop note;
#增加字段名
alter table employees add favoriate_activity varchar(100);

#为employee增加一个外键
alter table employees add constraint fk_em_off foreign key(officeCode) references offices(officeCode);

#删除表的外键约束
alter table employees drop foreign key fk_em_off;

#更改employee的数据引擎
alter table employees engine = MyISAM;

#更改employee的表名
alter table employees rename employees_info;
```
***

## <font color = red>三、数据类型和运算符

数据类型主要有下面几种
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205220743641.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
**整数类型**
整数数据类型主要有一下几种：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205220807642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
不同的数据类型取值范围如下：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205220837614.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
注意`INT`(`num`)中的数和取值范围无关。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205234759642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

**浮点数类型和定点数类型**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205235051102.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

注意: 不论定点还是浮点类型，如果用户指定的精度超出精度范围，则会四舍五入进行处理。

注意浮点数和定点数的使用场合：
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205235647467.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

**时间和日期类型**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181205235720697.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
**Year**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000202874.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
**`0`表示`0000`，`‘0’`和`‘00’`表示`2000`，`‘78’`和`78`表示`1978`，`‘68’`和`68`表示`2068`**

**Time**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000447295.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```sql
create table tmp4(t Time);
delete  from tmp4;
insert into tmp4 values('10:05:05'),('23:23'),('2 10:10'),('3 02'),('10'),(now()),(current_time);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413182426669?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

**Date**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000522752.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```sql
create table tmp5(d Date);
insert into tmp5 values('1998-09-01'),('19980902'),('980903'),(19980904),(980905),(100906),(000907),(current_date);
```
效果
![这里写图片描述](https://img-blog.csdn.net/2018041318385773?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
**DateTime**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000608865.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```sql
create table tmp6(dt DateTime);
insert into tmp6 values('1998-08-08 08:08:08'),('19980809080808'),('98-08-08 08:08:08'),('980808080808'),(19980808080808),(980808080808);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413184554983?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
**TimeStamp**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000723281.png)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206000738572.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
`TimeStamp`把时区修改之后查询结果就会不同，但是`DateTime`不会。

**文本字符串类型**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206232137628.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

`char`和`varchar`类型
`char`数据类型长度不可变，`varchar`长度可变
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206232158420.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```sql
create table tmp8(ch char(4),vch varchar(4));
insert into tmp8 values('ab  ','ab  ');
select concat('(',ch,')'),concat('(',vch,')') from tmp8;
```
看效果vch中的空格没有被截取
![这里写图片描述](https://img-blog.csdn.net/2018041320052037?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
**Text类型**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206232327218.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
`Enum`类型
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206232423516.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```sql
create table tmp9(enm Enum('first','second','third'));
insert into tmp9 values('first'),('second'),('third'),(null);
select enm,enm+0 from tmp9;
```
![这里写图片描述](https://img-blog.csdn.net/20180413201755914?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
再看一个实例

```sql
create table tmp10(soc int ,level enum('excellent','good','bad'));
insert into tmp10 values(70,'good'),(90,1),(75,2),(50,3); #'excellent','good','bad'-->对应 1，2，3
select soc,level,level+0 from tmp10;
insert into tmp10 values(100,4); #没有4这个选项
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413203512394?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
**Set类型**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181206232555988.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

```
#自动排序去重
create table tmp11(s set('a','b','c','d'));  #只能插入a,b,c,d这四个值
insert into tmp11 values('a'),('a,b,a'),('c,a,d');
select *from tmp11;
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413204802629?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
####二进制字符串类型
![这里写图片描述](https://img-blog.csdn.net/20180413204919966?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#####Bit类型
保存的是数的二进制表示
![这里写图片描述](https://img-blog.csdn.net/20180413210002742?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

```
#bit
create table tmp12(b bit(4));
insert into tmp12 values(2),(9),(15);
insert into tmp12 values(16);#报错，只能存到0-15
select b,b+0 from tmp12;
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413210036151?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#####Binary和varBinary
![这里写图片描述](https://img-blog.csdn.net/20180413210206410?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

```
#binary和varbinary
create table tmp13(b binary(3),vb varbinary(30));
insert into tmp13 values(5,5);
select length(b),length(vb) from tmp13;
```
效果如图
![这里写图片描述](https://img-blog.csdn.net/20180413212908210?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#####Blob类型
![这里写图片描述](https://img-blog.csdn.net/20180413213100132?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
###如何选择数据类型
![这里写图片描述](https://img-blog.csdn.net/20180413213929136?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413214509984?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413214615254?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413214720537?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
###常见运算符介绍
![这里写图片描述](https://img-blog.csdn.net/20180413214916676?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
注意一下比较运算符
![这里写图片描述](https://img-blog.csdn.net/2018041321511361?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413215209417?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
注意两个相等运算符的微小差异
![这里写图片描述](https://img-blog.csdn.net/20180413215434876?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413215616466?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180413215740247?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
正则表达式也是很重要的
![这里写图片描述](https://img-blog.csdn.net/20180413220304443?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
看一个例子

```
select 'ssky' regexp '^s','ssky' regexp 'y$', 'ssky' regexp '.sky', 'ssky' regexp '[ab]';
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180413220519323?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
####逻辑运算符
![这里写图片描述](https://img-blog.csdn.net/20180413220712150?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/2018041322073142?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#####位运算
这个和别的语言里面差不多，不细说
####运算符的优先级
![这里写图片描述](https://img-blog.csdn.net/20180413221155814?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###综合案例-运算符的使用

```
create table tmp15(note varchar(100),price int);
insert into tmp15 values("Thisisgood",50);
#算术运算符
select price,price+10,price-10,price*2,price/2,price%3 from tmp15;
#比较运算符
select price,price>10,price<10,price != 10,price = 10,price <=>10,price <>10 from tmp15;
select price,price between 30 and 80,greatest(price,70,30),price in(10,20,50,35) from tmp15;

select note,note is null,note like 't%',note regexp '$y',note regexp '[gm]' from tmp15;

select price,price&2,price|4, ~price from tmp15;

select price,price<<2,price>>2 from tmp15;

```

***
## <font color = red>四、Mysql函数

###数学函数

```
#绝对值，π，平方根，去余函数(适用小数)
select abs(-1),pi(),sqrt(9),Mod(31,8),Mod(45.5,6);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414100041110?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#获取整数的函数
select ceil(-3.5),ceiling(3.5),floor(-3.5),floor(3.5);
```
效果![这里写图片描述](https://img-blog.csdn.net/20180414100448562?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#获取随机数的函数
select rand(),rand(),rand(10),rand(10);

```
![这里写图片描述](https://img-blog.csdn.net/20180414100756428?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#Round函数(四舍五入函数)，truncate()函数
select round(3.4),(3.6),round(3.16,1),round(3.16,0),round(232.28,-1),truncate(1.31,1),truncate(1.99,1),truncate(19.99,-1);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414101532820?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#符号函数，幂运算函数pow,power,exp()//e的x乘方
select sign(-21),sign(0),sign(21),pow(2,2),power(2,-2),exp(2);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414101833603?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#自然对数运算和以10为底的对数运算,弧度，角度 radians角度转弧度，弧度转角度
select log(3),log(-3),log10(100),log10(-100),radians(180),degrees(pi()/2);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414102846475?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#正弦函数余弦函数
select sin(pi()/2),degrees(asin(1)),cos(pi()),degrees(acos(-1)),round(tan(pi()/4)),degrees(atan(1)),cot(pi()/4);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414103636239?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###字符串函数

```

#字符串函数,concat_ws忽略空值null
select char_length('aab'),length('aabb'),concat('My sql ','5.7'),concat('My',null,'sql'),concat_ws('-','a','b','c'),concat_ws('*','aa',null,'bb');

```
效果
![这里写图片描述](https://img-blog.csdn.net/2018041410443992?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```

#替换字符串的函数
select insert('Quest',2,4,'What') as Coll,insert('Quest',-1,4,'What') as Coll2,insert('Quest',3,100,'Wh') as Coll3;
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414110146458?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#大小写转换,获取指定长度字符串的函数left,right;
select lower('ZHENGXIN'),lcase('ZHENGXIN'),upper('zhengxin'),ucase('zhengxin'),left('football',5),right('football',5);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414110548833?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#填充字符串的函数,删除空格的函数
select lpad('hello',4,'*'),lpad('hello',10,'*'),
rpad('hello',10,'*'),concat('(',ltrim('   book   '),')'),
concat('(',rtrim('   book   '),')'),
concat('(',trim('   book   '),')'),
trim('xy' from 'xyxyabababxyxy');
```
***
效果
![这里写图片描述](https://img-blog.csdn.net/20180414112006326?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#重复生成,空格函数，替换函数，比较大小的函数
select repeat('mysql',3),concat('(',space(6),')'),
replace('xxx.baidu.com','x','w'),strcmp('abc','abd');
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414133734950?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#获取字串的函数
select substring('breakfast',5) as coll,
substring('breakfast',3,5) as coll2,
substring('breakfast',-3) as coll3, #从后面开始截取3个
substring('breakfast',-1,4) as coll4; #从结尾开始第一个位置截取四个
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414134457483?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
<font color = crimson>注意还有一个MID函数和substring作用是一样的</font>
***

```
#匹配字串开始的位置,字符串逆序
select locate('ball','football'),position('ball'in'football'),
instr('football','ball'),reverse('abc');
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414140015303?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#返回指定位置的值,返回指定字符串的位置的函数
select elt(3,'a','b','c'),elt(2,'a'),
field('Hi','hihi','Hey','Hi','bas') as coll,
field('Hi','hihi','a','b') as coll2,
find_in_set('Hi','hihi,Hey,Hi,bas'); #返回字串位置的函数
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414141233663?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#make_set()函数的使用
select make_set(1,'a','b','c') as coll,#0001选第一个
make_set(1|4, 'hello','nice','word') as coll2, #0001 0100-->0101 -->选第一和第三
make_set(1|4,'hello','nice',null,'word') as coll3,#0001 0100-->0101 -->选第一和第三
make_set(0,'a','b','c') as coll4; 
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414142118936?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
###日期和时间函数

```
#获取日期时间函数
select current_date(),curdate(),curdate()+0,
current_time(),curtime(),curtime()+0,
current_timestamp(),localtime(),now(),sysdate();
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414142607410?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```

#获取时间的数字,根据时间获取日期(互为反函数)
select unix_timestamp(),unix_timestamp(now()),now(),
from_unixtime(1523689758);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414151053533?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#返回当前时区日期和时间的函数,日期月份时间函数
select utc_time(),utc_time()+0,
utc_date(),utc_date()+0,
month('2016-03-04'),monthname('2016-03-04'),
dayname('2018-04-14'),dayofweek('2018-04-14'),
weekday('2018-04-14');
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414152120121?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
注意dayofweek和weekday的差别
![这里写图片描述](https://img-blog.csdn.net/20180414152202919?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180414152209424?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#返回是这一年的第几周
select week('2018-4-16'),#默认0表示第一天从周末开始
week('2018-04-16',1), #周一#返回是这一年的第几周
dayofyear('2018-4-16'),dayofmonth('2018-4-14'), #返回一年中的第几天
year('2018-4-14'),quarter('2018-4-14'),
minute('10:10:02'),second("10:10:02");
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414161034181?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#获取指定日期的指定值的函数
select extract(year from '2018-07-06') as coll,
extract(year_month from '2018-08-06') as coll2,
extract(day_minute from '2018-07-06 10:11:05') as coll3;

```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414161511106?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#时间和秒钟转换的函数
select time_to_sec('01:00:40'),
sec_to_time(3600);
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414174423286?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#计算日期和时间的函数
select date_add('2010-12-31 23:59:59',interval 1 second) as coll,
adddate('2010-12-31 23:59:59',interval 1 second) as coll2,
date_add('2010-12-31 23:59:59',interval '0:0:1' hour_second) as coll3, #后面的hour_second要看表决定
date_sub('2011-01-02',interval 31 day) as coll4,
subdate('2011-01-02',interval 31 day) as coll5,
date_sub('2011-01-02 00:01:00',interval '0 0:1:1' day_second) as coll6; #对应位置的相减
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414174708360?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

```

#直接输入两个时间，计算
select addtime('2000-12-31 23:59:59','1:1:1') as coll,
subtime('2000-12-31 23:59:59','1:1:1')as coll2,
datediff('2000-12-28','2001-01-03') as coll3; #前面的减后面的
```
![这里写图片描述](https://img-blog.csdn.net/20180414175044280?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
注意看表
![这里写图片描述](https://img-blog.csdn.net/20180414174815850?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***
#####日期和时间格式化的函数
![这里写图片描述](https://img-blog.csdn.net/2018041417493265?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180414174940784?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180414174950614?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

```
#时间日期格式化函数
select date_format('1997-10-04 22:23:00','%W %M %Y') as coll,
date_format('1997-10-04 22:23:00','%D %y %a %d %m %b %j'),
time_format('16:00:00','%H %k %h %I %l'),
date_format('2000-10-05 22:23:00',get_format(date,'USA'));
```
![这里写图片描述](https://img-blog.csdn.net/20180414175142523?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###条件约束函数

```
#条件约束函数
select if(1>2,2,3),
ifNull(null,10),ifNull(1/0,100),
case 2 when 1 then 'one' when 2 then 'two' when 3 then 'three' else 'more' end, #2等于后面的2返回后面的then
case when 1>2 then 'a' else 'b' end;
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414190109796?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###系统信息函数

```
#系统信息函数
select version(),connection_id(),#版本号，连接次数
database(),schema(), #查看当前的数据库名
user(),current_user(),system_user(),session_user();
show processlist;#输出当前用户的连接信息
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414190945328?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```
#获取字符串的字符集和排列方式的函数
select charset('abc'),charset(convert('abc' using latin1)),
charset(version()), #获取字符集
collation('abc'),collation(convert('abc' using utf8));#获取排列方式
```
效果
还要注意Last_insert_id最后自动生成的ID值
![这里写图片描述](https://img-blog.csdn.net/20180414194735908?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###加/解密函数

```
#加密解密函数
select password('newpwd'),MD5('mypwd'), 
encode('secret','cry'),length(encode('secret','cry')),
decode(encode('secret','cry'),'cry');#加密后解密
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414195439560?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###其他函数

```
#其他函数
select format(123.1234,2),format(123.1,3),format(123.123,0),#格式化函数
#不同进制数之间的转换
conv('a',16,2),conv(15,10,2),conv(15,10,8),conv(15,10,16);

```
![这里写图片描述](https://img-blog.csdn.net/20180414200512674?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
***

```

#IP地址与数字相互转换的函数
select inet_aton('209.207.224.40'),inet_ntoa(3520061480),
#枷锁函数和解锁函数
get_lock('lock1',10),#这个锁持续10秒
is_used_lock('lock1'),  #返回当前连接ID
is_free_lock('lock1'), #是否是可用的
release_lock('lock1');
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414201609394?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

```
#重复执行指定操作的函数
select benchmark(5000,password('newpad')),
charset('abc'),charset(convert('abc' using latin1)),#改变字符集的函数
cast(100 as char(2)),convert('2010-10-11 12:12:12',time);#改变数据类型的函数
```
效果
![这里写图片描述](https://img-blog.csdn.net/20180414202751518?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
###综合案列-Mysql函数的使用

```
select round(rand() * 10),round(rand() * 10),round(rand() * 10);#产生三个1-10之间的随机数
select pi(),sin(pi()),cos(0),round(tan(pi()/4)),floor(cot(pi()/4));

create database test_db3;
use test_db3;
show tables;
create table member
(
	m_id int(11) primary key auto_increment,
	m_FN varchar(15),
	m_LN varchar(15),
	m_brith datetime,
	m_info varchar(15) null
);

insert into member values(null,'Halen','Park','1970-06-29','GoodMan');

select length(m_FN),#返回m_FN的长度
concat(m_FN,m_LN),#返回第一条记录中的全名
lower(m_info),#将m_info转换成小写
reverse(m_info) from member;

select year(curdate())-year(m_brith) as age,#计算年龄
dayofyear(m_brith) as days,
date_format(m_brith,'%W %D %M %Y') as birthDate from member;

insert into member values(null,'Samuel','Green',now(),null);

select last_insert_id(); #输出最后插入的自增的编号

select m_brith,case when year(m_brith) < 2000 then 'old' 
when year(m_brith) > 2000 then 'young' 
else 'not born' end as status from member;
```







