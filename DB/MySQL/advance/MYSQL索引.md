# MYSQL索引部分

### 1、性能下降分析

需要优化的原因：性能低、执行时间太长、等待时间太长、SQL语句欠佳（连接查询）、索引失效、服务器参数设置不合理（缓冲、线程数）	。

<div align="center"><img src="images/ad8_性能下降原因.png"></div><br>

先看SQL执行的顺序:

```mysql
编写过程：
select dinstinct  ..from  ..join ..on ..where ..group by ...having ..order by ..limit ..

解析过程：			
from .. on.. join ..where ..group by ....having ...select dinstinct ..order by limit ...
```

解析图:

![ad9_索引.png](images/ad9_索引.png)
> 详细参考这篇博客: https://www.cnblogs.com/annsshadow/p/5037667.html

SQL优化， 主要就是在优化索引

*  相当于书的目录；
*  index是帮助MYSQL高效获取数据的数据结构。索引是数据结构（树：B树(默认)、Hash树...）；

### 2、索引优缺点

索引的弊端：

 * 索引本身很大， 实际上索引也是一张表，该表保存了主键与索引字段，并指向实体表的记录，所以索引列也是要占用空间的；
 * 索引不是所有情况均适用： a. 少量数据，b.频繁更新的字段，c.很少使用的字段
 * 索引会降低增删改的效率；MySQL不仅要保存数据，还要保存一下索引文件每次更新添加了索引列的字段， 都会调整因为更新所带来的键值变化后的索引信息。

 优势：

 * 提高查询效率（降低IO使用率）
 * 降低CPU使用率 （...order by age desc，因为 B树索引 本身就是一个 好排序的结构，因此在排序时  可以直接使用）

### 3、索引分类

* 主键索引：  不能重复。**id  不能是null** (设定为主键后数据库会**自动建立索引**，innodb为聚簇索引)；
* 唯一索引  ：不能重复。**id   可以是null**
* 单值索引  ： 单列， 一个表可以多个单值索引。
* 复合索引  ：多个列构成的索引 （相当于二级目录 ：  z: zhao）  (name,age)   (a,b,c,d,...,n)

创建索引的两种方式:

```mysql
创建索引：
	方式一(创建)：
        create 索引类型  索引名  on 表(字段)
            单值(普通索引)：
            create index dept_index on tb(dept);
            唯一：
            create unique index name_index on tb(name) ;
            复合索引
            create index dept_name_index on tb(dept,name);

	方式二(添加)：
		alter table 表名 索引类型  索引名（字段）
            主键索引:
            ALTER TABLE `table_name` ADD PRIMARY KEY ( `column` ) 
            单值：
            alter table tb add index dept_index(dept) ;
            唯一：
            alter table tb add unique index name_index(name);
            复合索引
            alter table tb add index dept_name_index(dept,name);
            全文索引
            ALTER TABLE `table_name` ADD FULLTEXT ( `column`) 
            
    注意：如果一个字段是primary key，则改字段默认就是 主键索引	
```

删除索引

```mysql
删除索引：
drop index 索引名 on 表名 ;
drop index name_index on tb ;
```
查询索引
```mysql
查询索引：
show index from 表名 ;
show index from 表名 \G
```

### 4、哪些情况需要建立索引，哪些不需要

需要建立索引的情况: 

* 主键自动建立唯一索引(`primary key`)；
* 频繁作为查询条件的字段应该创建索引(`where` 后面的语句)；
* 查询中与其它表关联的字段，**外键关系建立索引**；
* 单键/组合索引的选择问题，`who？`(在高并发下倾向创建组合索引)；
* 查询中排序的字段，排序字段若通过索引去访问将大大提高排序速度；
* 查询中统计或者分组字段；(`group by....`)

不需要建立索引的情况:

* 表记录太少；
* 经常增删改的表；
* **Where条件里用不到的字段不创建索引**；
* 数据重复且**分布平均**的表字段，因此应该只为最经常查询和最经常排序的数据列建立索引。 注意，如果某个数据列包含许多重复的内容，为它建立索引就没有太大的实际效果(有一个比值，不同的个数和总个数的比值越大越好)；

### 5、Explain

具体可以参考这篇博客: [https://blog.csdn.net/drdongshiye/article/details/84546264](#https://blog.csdn.net/drdongshiye/article/details/84546264)。

#### 1)、概念和作用

概念: 使用EXPLAIN关键字可以模拟优化器执行SQL查询语句，从而知道MySQL是 如何处理你的SQL语句的。分析你的查询语句或是表结构的性能瓶颈；

作用: 

* 表的读取顺序；
* 哪些索引可以使用；
* 哪些索引被实际使用；
* 数据读取操作的**操作类型**；
* 表之间的引用；
* 每张表有多少行被优化器查询；

#### 2)、id

表示：**`select`查询的序列号**，包含一组数字，表示**查询中执行select子句或操作表的顺序**。

分为三种情况:

a)、第一种情况: **id相同，执行顺序由上至下**。

此例中 先执行where 后的第一条语句 `t1.id = t2.id` 通过 `t1.id` 关联 `t2.id` 。 而  t2.id 的结果建立在 `t2.id=t3.id` 的基础之上。

b)、**id不同，如果是子查询，id的序号会递增，id值越大优先级越高，越先被执行**。

c)、id相同不同，同时存在。

id如果相同，可以认为是一组，从上往下顺序执行；在所有组中，id值越大，优先级越高，越先执行。

衍生表 = `derived2 --> derived + 2` （2 表示由 id =2 的查询衍生出来的表。type 肯定是 all ，因为衍生的表没有建立索引）

#### 3)、select_type

![ad14_.png](images/ad14_.png)

#### 4)、type

![ad15_.png](images/ad15_.png)

#### 5)、possible_keys和key

possible_keys : 显示可能应用在这张表中的索引，一个或多个。 查询涉及到的字段上若存在索引，则该索引将被列出，但不一定被查询实际使用。

key:

* 实际使用的索引。如果为NULL，则没有使用索引；
* 查询中若使用了**覆盖索引**，则该索引和查询的select字段重叠；

> 覆盖索引:
>
> 如果一个索引包含 (或者说覆盖) 所有需要查询的字段的值，我们就称之为“覆盖索引”。我们知道在InnoDB存情引擎中，如果不是主键索引，叶子节点存储的是主键+列值。最终还是要"回表"，也就是要通过主键再查找一次。这样就会比较慢。
>
> 覆盖索引就是把要查询出的列和索引是对应的，不做回表操作!  
>
> 现在我创建了索引(username,age)，在查询数据的时候: `select username , age fromuser where username = Java' and age = 22`。要查词出的列在叶子节点都存在! 所以就不要回表。

#### 6)、key_len、ref、rows

key_len 

* 表示索引中使用的字节数，可通过该列计算查询中使用的索引的长度。
* **在不损失精确性的情况下，长度越短越好**。
* **key_len字段能够帮你检查是否充分的利用上了索引**。
* **具体使用到了多少个列的索引，这里就会计算进去**，没有使用到的列，这里不会计算进去。留意下这个列的值，算一下你的多列索引总长度就知道有没有使用到所有的列了。

ref:

* **显示索引的哪一列被使用了，如果可能的话，是一个常数**。哪些列或常量被用于查找索引列上的值；

rows:

* **rows列显示MySQL认为它执行查询时必须检查的行数**。
* 越少越好；

#### 7)、Extra

![ad16_5104.png](images/ad16_5104.png)

#### 8)、检测

![ad17_.png](images/ad17_.png)

答案:

![ad18_.png](images/ad18_.png)

### 6、SQL优化实战

#### 1)、实战一-单表

建表SQL:

```mysql
CREATE TABLE IF NOT EXISTS `article`(

`id` INT(10) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
`author_id` INT(10) UNSIGNED NOT NULL,
`category_id` INT(10) UNSIGNED NOT NULL,
`views` INT(10) UNSIGNED NOT NULL,
`comments` INT(10) UNSIGNED NOT NULL,
`title` VARBINARY(255) NOT NULL,
`content` TEXT NOT NULL
);

INSERT INTO `article` (author_id,category_id,views,comments,title,content) VALUES
(1,1,1,1,1,1),
(2,2,2,2,2,2),
(1,1,3,3,3,3); 
```

表中内容:

<div algin="center"><img  src="images/ad19_.png"></div><br>

实战一:

查询 `categoryid` 为1 且 `comments` 大于 1 的情况下，views 最多的文章。

![ad_20.png](images/ad_20.png) 

完整代码:

```mysql
mysql> select id, author_id from article where category_id = 1 AND comments > 1 ORDER BY views DESC LIMIT 1;
+----+-----------+
| id | author_id |
+----+-----------+
|  3 |         1 |
+----+-----------+
1 row in set (0.01 sec)

mysql> explain select id, author_id from article where category_id = 1 AND comments > 1 ORDER BY views DESC LIMIT 1\G
^[[A*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: article
   partitions: NULL
         type: ALL
possible_keys: NULL
          key: NULL
      key_len: NULL
          ref: NULL
         rows: 3
     filtered: 33.33
        Extra: Using where; Using filesort
1 row in set, 1 warning (0.00 sec)

```

第一版优化，建立索引:

![ad20_.png](images/ad20_.png)

代码:

```mysql
mysql> create index idx_article_ccv on article(category_id, comments, views);
Query OK, 0 rows affected (0.20 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> explain select id, author_id from article where category_id = 1 AND comments > 1 ORDER BY views DESC LIMIT 1\G
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: article
   partitions: NULL
         type: range
possible_keys: idx_article_ccv
          key: idx_article_ccv
      key_len: 8
          ref: NULL
         rows: 1
     filtered: 100.00
        Extra: Using index condition; Using filesort
1 row in set, 1 warning (0.02 sec)

```

结论:
type 变成了 range,这是可以忍受的。但是 extra 里使用 Using filesort 仍是无法接受的。
但是我们已经建立了索引为啥没用呢? 这是因为按照 BTree 索引的工作原理:
先排序 category_id， 如果遇到相同的 category_id 则再排序 comments,如果遇到相同的 comments 则再排序 views。当 comments 字段在联合素引里处于中间位置时，因comments > 1 条件是一个范围值(所谓 range)，
MySQL 无法利用索引再对后面的 views 部分进行检索,即 range 类型查询字段后面的索引无效。

第二版: 先删除上面那个不是很好的索引，然后只建立`(category_id, views)`之间的索引，而没有`comments`:

```mysql
mysql> drop index idx_article_ccv on article;
Query OK, 0 rows affected (0.09 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> create index article_cv on article(category_id, views);
Query OK, 0 rows affected (0.08 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> explain select id, author_id from article where category_id = 1 AND comments > 1 ORDER BY views DESC LIMIT 1\G
*************************** 1. row ***************************
           id: 1
  select_type: SIMPLE
        table: article
   partitions: NULL
         type: ref
possible_keys: article_cv
          key: article_cv
      key_len: 4
          ref: const
         rows: 2
     filtered: 33.33
        Extra: Using where
1 row in set, 1 warning (0.00 sec)

```

结论: 可以看到type变成了`ref`，Extra中的`Using fileSort`也消失了，结果非常理想。

#### 2)、实战二-双表

两个表:

<div align="center"> <img src="images/ad21_.png"></div><br>

使用

```mysql
mysql> EXPLAIN SELECT * FROM class LEFT JOIN book ON class.card = book.card;
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
| id | select_type | table | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra                                              |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
|  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   20 |   100.00 | NULL                                               |
|  1 | SIMPLE      | book  | NULL       | ALL  | NULL          | NULL | NULL    | NULL |   20 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
+----+-------------+-------+------------+------+---------------+------+---------+------+------+----------+----------------------------------------------------+
2 rows in set, 1 warning (0.04 sec)

```

结论：type 有All，不是很好。

可以看到第二行的 type 变为了 ref,rows 也变成了优化比较明显。

这是由左连接特性决定的。LEFT JOIN 条件用于确定如何从右表搜索行,左边一定都有,**所以右边是我们的关键点,一定需要建立索引**。(如果将索引建立在左边，不会有这么好)。

```mysql
mysql> ALTER TABLE `book` ADD INDEX Y ( `card`);
Query OK, 0 rows affected (0.12 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> EXPLAIN SELECT * FROM class LEFT JOIN book ON class.card = book.card;
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
| id | select_type | table | partitions | type | possible_keys | key  | key_len | ref                | rows | filtered | Extra       |
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
|  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL | NULL    | NULL               |   20 |   100.00 | NULL        |
|  1 | SIMPLE      | book  | NULL       | ref  | Y             | Y    | 4       | mysqlad.class.card |    1 |   100.00 | Using index |
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
2 rows in set, 1 warning (0.00 sec)

```

上面的索引建立在右边的表(`book`)。下面如果我们建立在`class`表，并使用左连接，就不会有这么好的效果，如下:

```mysql
mysql> DROP INDEX Y ON book;
Query OK, 0 rows affected (0.05 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> ALTER TABLE class ADD INDEX X (card);
Query OK, 0 rows affected (0.07 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> 
mysql> EXPLAIN SELECT * FROM class LEFT JOIN book ON class.card = book.card;
+----+-------------+-------+------------+-------+---------------+------+---------+------+------+----------+----------------------------------------------------+
| id | select_type | table | partitions | type  | possible_keys | key  | key_len | ref  | rows | filtered | Extra                                              |
+----+-------------+-------+------------+-------+---------------+------+---------+------+------+----------+----------------------------------------------------+
|  1 | SIMPLE      | class | NULL       | index | NULL          | X    | 4       | NULL |   20 |   100.00 | Using index                                        |
|  1 | SIMPLE      | book  | NULL       | ALL   | NULL          | NULL | NULL    | NULL |   20 |   100.00 | Using where; Using join buffer (Block Nested Loop) |
+----+-------------+-------+------------+-------+---------------+------+---------+------+------+----------+----------------------------------------------------+
2 rows in set, 1 warning (0.00 sec)


```

所以总结:

* 1、保证**被驱动表的join字段已经被索引**。被驱动表  join 后的表为被驱动表  (需要被查询)；
* 2、left join 时，选择小表作为驱动表，大表作为被驱动表(建立索引的表)。但是 left join 时一定是左边是驱动表，右边是被驱动表。
* 3、inner join 时，mysql会自己帮你把小结果集的表选为驱动表。
* 4、**子查询尽量不要放在被驱动表**，有可能使用不到索引。

#### 3)、实战三-三表

![ad_22.png](images/ad_22.png)

建立索引后的查询:

```mysql
mysql> alter table phone add index z(card);
Query OK, 0 rows affected (0.09 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> alter table book add index y(card);
Query OK, 0 rows affected (0.06 sec)
Records: 0  Duplicates: 0  Warnings: 0

mysql> explain select * from class left join book on class.card=book.card left join phone on book.card=phone.card;
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
| id | select_type | table | partitions | type | possible_keys | key  | key_len | ref                | rows | filtered | Extra       |
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
|  1 | SIMPLE      | class | NULL       | ALL  | NULL          | NULL | NULL    | NULL               |   20 |   100.00 | NULL        |
|  1 | SIMPLE      | book  | NULL       | ref  | y             | y    | 4       | mysqlad.class.card |    1 |   100.00 | Using index |
|  1 | SIMPLE      | phone | NULL       | ref  | z             | z    | 4       | mysqlad.book.card  |    1 |   100.00 | Using index |
+----+-------------+-------+------------+------+---------------+------+---------+--------------------+------+----------+-------------+
3 rows in set, 1 warning (0.00 sec)

```

结论: 后2行的`type`都是`ref`且总`rows`优化很好，效果不错，因此索引最好设置在需要经常查询的字段中。

相关建索引建议:

* 1、保证被驱动表的join字段已经被索引；
* 2、left join 时，选择小表作为驱动表，大表作为被驱动表；
* 3、inner join 时，mysql会自己帮你把小结果集的表选为驱动表；
* 4、子查询尽量不要放在被驱动表，有可能使用不到索引；

### 7、索引失效(应该避免)

表:

<div align="center"> <img src="images/ad22_.png"></div><br>

建表语句:

```mysql
CREATE TABLE staffs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  NAME VARCHAR (24)  NULL DEFAULT '' COMMENT '姓名',
  age INT NOT NULL DEFAULT 0 COMMENT '年龄',
  pos VARCHAR (20) NOT NULL DEFAULT '' COMMENT '职位',
  add_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入职时间'
) CHARSET utf8 COMMENT '员工记录表' ;

INSERT INTO staffs(NAME,age,pos,add_time) VALUES('z3',22,'manager',NOW());
INSERT INTO staffs(NAME,age,pos,add_time) VALUES('July',23,'dev',NOW());
INSERT INTO staffs(NAME,age,pos,add_time) VALUES('2000',23,'dev',NOW());
INSERT INTO staffs(NAME,age,pos,add_time) VALUES(null,23,'dev',NOW());

ALTER TABLE staffs ADD INDEX idx_staffs_nameAgePos(name, age, pos);
```

#### 1)、全值匹配我以及最佳前缀匹配

索引  idx_staffs_nameAgePos 建立索引时 以 name ， age ，pos 的顺序建立的。全值匹配表示 按顺序匹配的

```mysql
EXPLAIN SELECT * FROM staffs WHERE NAME = 'July';

EXPLAIN SELECT * FROM staffs WHERE NAME = 'July' AND age = 25;

EXPLAIN SELECT * FROM staffs WHERE NAME = 'July' AND age = 25 AND pos = 'dev';

```

结果:

![ad23_.png](images/ad23_.png)

但是如果我们只有`age和pos`或者只有`pos`， 查询结果就会很差，所以这就是**最佳左前缀法则**。

```mysql
mysql> explain select * from staffs where age=25 and pos='dev';
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table  | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | staffs | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    4 |    25.00 | Using where |
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

mysql> explain select * from staffs where  pos='dev';
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
| id | select_type | table  | partitions | type | possible_keys | key  | key_len | ref  | rows | filtered | Extra       |
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
|  1 | SIMPLE      | staffs | NULL       | ALL  | NULL          | NULL | NULL    | NULL |    4 |    25.00 | Using where |
+----+-------------+--------+------------+------+---------------+------+---------+------+------+----------+-------------+
1 row in set, 1 warning (0.00 sec)

```

 **如果索引了多列，要遵守最左前缀法则。指的是查询从索引的最左前列开始并且不跳过索引中的列**。

再看中间断的情况:

```mysql
mysql> explain select * from staffs where name='July' and pos='dev';
+----+-------------+--------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-----------------------+
| id | select_type | table  | partitions | type | possible_keys         | key                   | key_len | ref   | rows | filtered | Extra                 |
+----+-------------+--------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-----------------------+
|  1 | SIMPLE      | staffs | NULL       | ref  | idx_staffs_nameAgePos | idx_staffs_nameAgePos | 75      | const |    1 |    25.00 | Using index condition |
+----+-------------+--------+------------+------+-----------------------+-----------------------+---------+-------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

```

结论: 只用到了第一个。中间断了。

#### 2)、不在索引列上做任何操作

**不在索引列上做任何操作（计算、函数、(自动or手动)类型转换），会导致索引失效而转向全表扫描**。

下面在`name`使用了`left()`函数，就会失效。

![ad_24.png](images/ad_24.png)

#### 3)、存储引擎不能使用索引中范围条件右边的列

如果中间出现了范围的，就会变成`range`。后面就会失效:

![ad24_.png](images/ad24_.png)

#### 4)、尽量使用覆盖索引(只访问索引的查询(索引列和查询列一致))，减少select *

不用`select * `，而是`select `具体的字段。

![ad25_.png](images/ad25_.png)

#### 5)、使用不等于(!=或者<>)的时候无法使用索引

![ad26_.png](images/ad26_.png)

但是如果业务需要必须要写的话，那也没办法。

#### 6)、like以通配符开头('%abc...')mysql索引失效会变成全表扫描(最好在右边写%)

![ad27_.png](images/ad27_.png)

 **问题：解决like '%字符串%'时索引不被使用的方法？？**

答: **使用覆盖索引**。

```mysql
#before index

# 第一批 (建最下面的索引后可以被优化)
EXPLAIN SELECT NAME,age  FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT id FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT NAME FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT age FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT id,NAME FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT id,NAME,age FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT NAME,age FROM tbl_user WHERE NAME LIKE '%aa%';

# 第二批: 搅屎棍

EXPLAIN SELECT * FROM tbl_user WHERE NAME LIKE '%aa%';
EXPLAIN SELECT id,NAME,age,email  FROM tbl_user WHERE NAME LIKE '%aa%';

#create index  (上面的字符第一批在键了下面的索引后会优化，但是第二批搅屎棍不会,因为覆盖不了)
# 为啥第一批的id也能优化，因为Extra中的 Using Index (主键本身也是索引)

CREATE INDEX idx_user_nameAge ON tbl_user(NAME,age); 
```

#### 7)、字符串不加单引号索引失效(发生了类型转换)

![ad28_.png](images/ad28_.png)

#### 8)、总结和练习

![ad_29.png](images/ad_29.png)

索引建议总结:

* 1、对于单键索引，尽量选择针对当前query过滤性更好的索引；
* 2、在选择组合索引的时候，当前Query中过滤性最好的字段在索引字段顺序中，位置越靠前越好。(避免索引过滤性好的索引失效)；
* 3、在选择组合索引的时候，尽量选择可以能够包含当前query中的where字句中更多字段的索引；
* 4、尽可能通过分析统计信息和调整query的写法来达到选择合适索引的目的；

再来一波练习:

```mysql
mysql> select * from test03; # 表
+----+------+------+------+------+------+
| id | c1   | c2   | c3   | c4   | c5   |
+----+------+------+------+------+------+
|  1 | a1   | a2   | a3   | a4   | a5   |
|  2 | b1   | b2   | b3   | b4   | b5   |
|  3 | c1   | c2   | c3   | c4   | c5   |
|  4 | d1   | d2   | d3   | d4   | d5   |
|  5 | e1   | e2   | e3   | e4   | e5   |
+----+------+------+------+------+------+
5 rows in set (0.02 sec)

mysql> create index idx_test03_c1234 on test03(c1,c2,c3,c4); # 创建索引
Query OK, 0 rows affected (0.12 sec)
Records: 0  Duplicates: 0  Warnings: 0

#  1、全值匹配我最爱
mysql> explain select * from test03 where c1='a1' and c2='a2' and c3='a3' and c4='a4'; 
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref                     | rows | filtered | Extra |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 124     | const,const,const,const |    1 |   100.00 | NULL  |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
1 row in set, 1 warning (0.31 sec)

# 2、这种情况Mysql会底层会帮我们自动优化
mysql>  explain select * from test03 where c1='a1' and c2='a2' and c4='a4' and c3='a3'; 
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref                     | rows | filtered | Extra |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 124     | const,const,const,const |    1 |   100.00 | NULL  |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------------------+------+----------+-------+
1 row in set, 1 warning (0.00 sec)

# 3、 中间阶段 -> range 
mysql>  explain select * from test03 where c1='a1' and c2='a2' and c3>'a3' and c4='a4';
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
| id | select_type | table  | partitions | type  | possible_keys    | key              | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test03 | NULL       | range | idx_test03_c1234 | idx_test03_c1234 | 93      | NULL |    1 |    20.00 | Using index condition |
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

# 4、这个比上面那个好，多用了一个（key_len会大一点）,因为Mysql底层会调优将c4>'a4'放在后面
mysql>  explain select * from test03 where c1='a1' and c2='a2' and c4>'a4' and c3='a3';
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
| id | select_type | table  | partitions | type  | possible_keys    | key              | key_len | ref  | rows | filtered | Extra                 |
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
|  1 | SIMPLE      | test03 | NULL       | range | idx_test03_c1234 | idx_test03_c1234 | 124     | NULL |    1 |   100.00 | Using index condition |
+----+-------------+--------+------------+-------+------------------+------------------+---------+------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

# 5、注意 : c3作用在排序而不是查找
mysql>  explain select * from test03 where c1='a1' and c2='a2' and c4='a4' order by c3;
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                 |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |    20.00 | Using index condition |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
1 row in set, 1 warning (0.01 sec)

# 6、 和5一模一样
mysql>  explain select * from test03 where c1='a1' and c2='a2' order by c3;
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                 |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |   100.00 | Using index condition |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

# 6、出现了Using filesort
mysql> explain select * from test03 where c1='a1' and c2='a2' order by c4; 
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+---------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                                 |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+---------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |   100.00 | Using index condition; Using filesort |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+---------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 8.1、 只用c1一个字段索引，但是c2、c3用于排序,所有没有 filesort
mysql>  explain select * from test03 where c1='a1' and c5='a5' order by c2,c3; 
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref   | rows | filtered | Extra                              |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 31      | const |    1 |    20.00 | Using index condition; Using where |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 8.2、	 出现了filesort，我们建的索引是1234，它没有按照顺序来，3,2 颠倒了
mysql> explain select * from test03 where c1='a1' and c5='a5' order by c3,c2;
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref   | rows | filtered | Extra                                              |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 31      | const |    1 |    20.00 | Using index condition; Using where; Using filesort |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 9、
mysql> explain select * from test03 where c1='a1' and c2='a2' order by c2,c3;
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                 |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |   100.00 | Using index condition |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)

# 10.1、 和c5这个坑爹货没关系
mysql> explain select * from test03 where c1='a1' and c2='a2' and c5='a5' order by c2,c3;
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                              |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |    20.00 | Using index condition; Using where |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 10.2、 这里排序字段已经是一个常量 和8.2不同  
mysql>  explain select * from test03 where c1='a1' and c2='a2' and c5='a5' order by c3,c2; 
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref         | rows | filtered | Extra                              |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 62      | const,const |    1 |    20.00 | Using index condition; Using where |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------------+------+----------+------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 本例有常量c2的情况，和8.2对比  filesort (下面是8.2的)
mysql>  explain select * from test03 where c1='a1' and c5='a5' order by c3,c2;  
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
| id | select_type | table  | partitions | type | possible_keys    | key              | key_len | ref   | rows | filtered | Extra                                              |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
|  1 | SIMPLE      | test03 | NULL       | ref  | idx_test03_c1234 | idx_test03_c1234 | 31      | const |    1 |    20.00 | Using index condition; Using where; Using filesort |
+----+-------------+--------+------------+------+------------------+------------------+---------+-------+------+----------+----------------------------------------------------+
1 row in set, 1 warning (0.00 sec)


# 11、group 虽然是分组，但是分组之前必排序 (可能导致临时表)
 explain select * from test03 where c1='a1' and c4='a4' group by c2,c3; # 
# 12、灭绝师太!!! Using temporary; Using filesort 
 explain select * from test03 where c1='a1' and c4='a4' group by c3,c2; 
```

### 8、order by关键字排序优化

**主要讨论order by**会不会产生`fileSort`。

**MySQL支持二种方式的排序，FileSort和Index，Index效率高。它指MySQL扫描索引本身完成排序。FileSort方式效率较低**。

ORDER BY满足两情况，会使用Index方式排序:

* ORDER BY 语句使用索引最左前列；
* 使用Where子句与Order BY子句条件列组合满足索引最左前列；
* where子句中如果出**现索引的范围查询(即explain中出现range)会导致order by 索引失效**。

ORDER BY子句，**尽量使用Index方式排序,避免使用FileSort方式排序**

测试: 键表语句：

```mysql
CREATE TABLE tblA(
  id int primary key not null auto_increment,
  age INT,
  birth TIMESTAMP NOT NULL,
  name varchar(200)
);

INSERT INTO tblA(age,birth,name) VALUES(22,NOW(),'abc');
INSERT INTO tblA(age,birth,name) VALUES(23,NOW(),'bcd');
INSERT INTO tblA(age,birth,name) VALUES(24,NOW(),'def');

CREATE INDEX idx_A_ageBirth ON tblA(age,birth,name);

表的内容
mysql> select * from tblA;
+----+------+---------------------+------+
| id | age  | birth               | name |
+----+------+---------------------+------+
|  1 |   22 | 2019-03-21 19:10:29 | abc  |
|  2 |   23 | 2019-03-21 19:10:29 | bcd  |
|  3 |   24 | 2019-03-21 19:10:29 | def  |
+----+------+---------------------+------+
3 rows in set (0.00 sec)
```

注意我们建立的索引是`(age, birth, name)`。

然后看下面的查询，当我们`order by birth`或者`order by birth,age`的时候，就会出现`Using fileSort`:

```mysql
# 1、没有产生Using FileSort
mysql> explain select * from tblA where age>20 order by age;
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
| id | select_type | table | partitions | type  | possible_keys  | key            | key_len | ref  | rows | filtered | Extra                    |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
|  1 | SIMPLE      | tblA  | NULL       | index | idx_A_ageBirth | idx_A_ageBirth | 612     | NULL |    3 |   100.00 | Using where; Using index |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
1 row in set, 1 warning (0.00 sec)

# 2、没有产生Using FileSort
mysql> explain select * from tblA where age>20 order by age, birth;
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
| id | select_type | table | partitions | type  | possible_keys  | key            | key_len | ref  | rows | filtered | Extra                    |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
|  1 | SIMPLE      | tblA  | NULL       | index | idx_A_ageBirth | idx_A_ageBirth | 612     | NULL |    3 |   100.00 | Using where; Using index |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+--------------------------+
1 row in set, 1 warning (0.00 sec)

# 3、产生了Using FileSort
mysql> explain select * from tblA where age>20 order by birth;
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
| id | select_type | table | partitions | type  | possible_keys  | key            | key_len | ref  | rows | filtered | Extra                                    |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
|  1 | SIMPLE      | tblA  | NULL       | index | idx_A_ageBirth | idx_A_ageBirth | 612     | NULL |    3 |   100.00 | Using where; Using index; Using filesort |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
1 row in set, 1 warning (0.00 sec)

# 4、产生了Using FileSort
mysql> explain select * from tblA where age>20 order by birth, age;
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
| id | select_type | table | partitions | type  | possible_keys  | key            | key_len | ref  | rows | filtered | Extra                                    |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
|  1 | SIMPLE      | tblA  | NULL       | index | idx_A_ageBirth | idx_A_ageBirth | 612     | NULL |    3 |   100.00 | Using where; Using index; Using filesort |
+----+-------------+-------+------------+-------+----------------+----------------+---------+------+------+----------+------------------------------------------+
1 row in set, 1 warning (0.00 sec)

```

还要注意一个`ASC和DESC`的问题:

![ad29_.png](images/ad29_.png)

提高Order by速度:

1)、Order by时select * 是一个大忌只Query需要的字段， 这点非常重要。在这里的影响是：

* 当Query的字段大小总和小于max_length_for_sort_data 而且排序字段不是 TEXT|BLOB 类型时，会用改进后的算法——单路排序， 否则用老算法——多路排序。

*  两种算法的数据都有可能超出sort_buffer的容量，超出之后，会创建tmp文件进行合并排序，导致多次I/O，但是用单路排序算法的风险会更大一些,所以要提高sort_buffer_size。

2)、 尝试提高` sort_buffer_size`

不管用哪种算法，提高这个参数都会提高效率，当然，要根据系统的能力去提高，因为这个参数是针对每个进程的

3)、尝试提高 `max_length_for_sort_data`

提高这个参数， 会增加用改进算法的概率。但是如果设的太高，数据总容量超出sort_buffer_size的概率就增大，明显症状是高的磁盘I/O活动和低的处理器使用率. 

![ad_31.png](images/ad_31.png)

总结: **尽可能在索引列上完成排序操作，遵照索引建的最佳左前缀**。

<div align="center"><img src="images/ad30_.png"></div><br>

第二种中，`where a = const and b > const order by b , c` 不会出现 using filesort  b , c 两个衔接上了

但是：`where a = const and b > const order by  c `将会出现 using filesort 。因为 b 用了范围索引，断了。而上一个  order by 后的b 用到了索引，所以能衔接上 c 。

### 9、B+Tree与B-Tree 的区别

结论在内存有限的情况下，B+TREE 永远比 B-TREE好。无限内存则后者方便。 

* 1)、B-树的关键字和记录是放在一起的，叶子节点可以看作外部节点，不包含任何信息；**B+树叶子节点中只有关键字和指向下一个节点的索引**，记录只放在叶子节点中。(一次查询可能进行两次i/o操作)

* 2)、在B-树中，越靠近根节点的记录查找时间越快，只要找到关键字即可确定记录的存在；而B+树中每个记录的查找时间基本是一样的，都需要从根节点走到叶子节点，而且在叶子节点中还要再比较关键字。从这个角度看B-树的性能好像要比B+树好，而在实际应用中却是B+树的性能要好些。因为B+树的非叶子节点不存放实际的数据，**这样每个节点可容纳的元素个数比B-树多**，树高比B-树小，这样带来的好处是减少磁盘访问次数。尽管B+树找到一个记录所需的比较次数要比B-树多，但是一次磁盘访问的时间相当于成百上千次内存比较的时间，因此实际中B+树的性能可能还会好些，**而且B+树的叶子节点使用指针连接在一起，方便顺序遍历**（例如查看一个目录下的所有文件，一个表中的所有记录等），这也是很多数据库和文件系统使用B+树的缘故。 

思考：为什么说B+树比B-树更适合实际应用中操作系统的文件索引和数据库索引？ 

1) B+树的磁盘读写代价更低 

　　**B+树的内部结点并没有指向关键字具体信息的指针**。因此其内部结点相对B 树更小。如果把所有同一内部结点的关键字存放在同一盘块中，那么盘块所能容纳的关键字数量也越多。一次性读入内存中的需要查找的关键字也就越多。相对来说IO读写次数也就降低了。 

2) B+树的查询效率更加稳定 

　　由于非终结点并不是最终指向文件内容的结点，而只是叶子结点中关键字的索引。所以任何关键字的查找必须走一条从根结点到叶子结点的路。所有关键字查询的路径长度相同，导致每一个数据的查询效率相当。

> 索引建立成哪种索引类型？
>
> 根据数据引擎类型自动选择的索引类型
>
> * 除开 innodb 引擎主键默认为聚簇索引 外。 Innodb的索引都采用的 B+TREE。
> * MyIsam 则都采用的 **B-TREE**索引。

### 10、聚簇索引和非聚簇索引

聚簇索引并不是一种单独的索引类型，而是一种数据存储方式。

**术语‘聚簇’表示数据行和相邻的键值进错的存储在一起**。

 如下图，左侧的索引就是聚簇索引，因为**数据行在磁盘的排列和索引排序保持一致。**

<div algin="center"> <img src="images/ad10_聚簇索引.png"></div><br>

聚簇索引优点 : 按照聚簇索引排列顺序，查询显示一定范围数据的时候，**由于数据都是紧密相连，数据库不用从多个数据块中提取数据**，所以节省了大量的io操作。

聚簇索引限制 : 

- 对于mysql数据库目前只有innodb数据引擎支持聚簇索引，而MyIsam并不支持聚簇索引。
- 由于*数据物理存储排序方式只能有一种*，所以每个Mysql的表只能有一个聚簇索引。一般情况下就是该表的**主键**。
- **为了充分利用聚簇索引的聚簇的特性，所以innodb表的主键列尽量选用有序的顺序id，而不建议用无序的id，比如uuid这种。（参考聚簇索引优点。）**

### 11、全文索引、Hash索引

**全文索引** 

* MyISAM 存储引擎支持全文索引，用于查找文本中的关键词，而不是直接比较是否相等。
* 查找条件使用 MATCH AGAINST，而不是普通的 WHERE。
* 全文索引使用倒排索引实现，它记录着关键词到其所在文档的映射。

InnoDB 存储引擎在 MySQL 5.6.4 版本中也开始支持全文索引。

```mysql
不同于like方式的的查询：
SELECT * FROM article WHERE content LIKE ‘%查询字符串%’;

全文索引用match+against方式查询：(明显的提高查询效率。)
SELECT * FROM article WHERE MATCH(title,content) AGAINST (‘查询字符串’);

```

**Hash索引**

哈希索引能以 O(1) 时间进行查找，但是失去了有序性：

- **无法用于排序与分组**；
- 只支持精确查找，无法用于部分查找和范围查找。

InnoDB 存储引擎有一个特殊的功能叫“自适应哈希索引”，当某个索引值被使用的非常频繁时，会在 B+Tree 索引之上再创建一个哈希索引，这样就让 B+Tree 索引具有哈希索引的一些优点，比如快速的哈希查找。

### 12、查询截取分析

优化SQL步骤:

* 1)、观察，至少跑一天，看看生产的慢SQL情况；
* 2)、开启慢查询日志，设置阙值，比如超过5秒钟的就是慢SQL，并将它抓取出来；
* 3)、explain+ 慢SQL分析；
* 4)、`show profile`；

即：

* 1)、慢查询的开启并捕获；
* 2)、explain+慢SQL分析；
* 3)、show profile查询SQL在MYSQL服务器里面的执行细节和生命周期情况；
* 4)、SQL数据库服务器的参数调优；

原则: **小表驱动大表**。

GROUP BY关键字优化:

* group by实质是先排序后进行分组，遵照索引建的最佳左前缀；
* 当无法使用索引列，增大`max_length_for_sort_data`参数的设置+增大`sort_buffer_size`参数的设置；
* where高于having，能写在where限定的条件就不要去having限定了；













