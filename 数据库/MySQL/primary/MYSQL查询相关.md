# MYSQL查询相关

演示的表和插入语句

```mysql
# 建立表
mysql> CREATE TABLE fruits
    -> (
    -> f_id    char(10)     NOT NULL, # 水果id
    -> s_id    INT        NOT NULL, # 供应商id
    -> f_name  char(255)  NOT NULL, # 水果名字
    -> f_price decimal(8,2)  NOT NULL, # 水果价格
    -> PRIMARY KEY(f_id) 
    -> );
Query OK, 0 rows affected (0.06 sec)

# 插入
mysql> INSERT INTO fruits (f_id, s_id, f_name, f_price)
    ->      VALUES('a1', 101,'apple',5.2),
    ->      ('b1',101,'blackberry', 10.2),
    ->      ('bs1',102,'orange', 11.2),
    ->      ('bs2',105,'melon',8.2),
    ->      ('t1',102,'banana', 10.3),
    ->      ('t2',102,'grape', 5.3),
    ->      ('o2',103,'coconut', 9.2),
    ->      ('c0',101,'cherry', 3.2),
    ->      ('a2',103, 'apricot',2.2),
    ->      ('l2',104,'lemon', 6.4),
    ->      ('b2',104,'berry', 7.6),
    ->      ('m1',106,'mango', 15.6),
    ->      ('m2',105,'xbabay', 2.6),
    ->      ('t4',107,'xbababa', 3.6),
    ->      ('m3',105,'xxtt', 11.6),
    ->      ('b5',107,'xxxx', 3.6);
Query OK, 16 rows affected (0.01 sec)
Records: 16  Duplicates: 0  Warnings: 0

# 查询所有
mysql> select * from fruits;
+------+------+------------+---------+
| f_id | s_id | f_name     | f_price |
+------+------+------------+---------+
| a1   |  101 | apple      |    5.20 |
| a2   |  103 | apricot    |    2.20 |
| b1   |  101 | blackberry |   10.20 |
| b2   |  104 | berry      |    7.60 |
| b5   |  107 | xxxx       |    3.60 |
| bs1  |  102 | orange     |   11.20 |
| bs2  |  105 | melon      |    8.20 |
| c0   |  101 | cherry     |    3.20 |
| l2   |  104 | lemon      |    6.40 |
| m1   |  106 | mango      |   15.60 |
| m2   |  105 | xbabay     |    2.60 |
| m3   |  105 | xxtt       |   11.60 |
| o2   |  103 | coconut    |    9.20 |
| t1   |  102 | banana     |   10.30 |
| t2   |  102 | grape      |    5.30 |
| t4   |  107 | xbababa    |    3.60 |
+------+------+------------+---------+
16 rows in set (0.00 sec)

mysql> 
```

## 一、分组查询

### 1、基本分组操作

对数据按照某个或多个字段进行分组，MYSQL中使用`group by `关键字对数据进行分组。基本形式为:

```mysql
group by 字段 having <条件表达式>
```

`group by `关键字通常和集合函数一起使用，例如`MAX()、MIN()、COUNT()、SUM()、AVG()`。

例如: 根据`si_d`对`fruits`表中的数据进行分组:

```mysql
mysql> select s_id, count(*) as Total from fruits group by s_id;
+------+-------+
| s_id | Total |
+------+-------+
|  101 |     3 |
|  102 |     3 |
|  103 |     2 |
|  104 |     2 |
|  105 |     3 |
|  106 |     1 |
|  107 |     2 |
+------+-------+
7 rows in set (0.00 sec)

```

`s_id`表示供应商的`ID`。`Total`字段使用`COUNT()`函数计算得出。

`GROUP BY`字句按照`s_id`先**排序**并对数据进行分组。

如果要查询所有种类的名称，可以使用`GROUP_CONCAT()`函数。

```java
mysql> select s_id, GROUP_CONCAT(f_name) AS Names FROM fruits GROUP BY s_id;
+------+-------------------------+
| s_id | Names                   |
+------+-------------------------+
|  101 | apple,blackberry,cherry |
|  102 | orange,banana,grape     |
|  103 | apricot,coconut         |
|  104 | berry,lemon             |
|  105 | melon,xbabay,xxtt       |
|  106 | mango                   |
|  107 | xxxx,xbababa            |
+------+-------------------------+
7 rows in set (0.00 sec)

mysql> 

```

使用`having`过滤分组：

**GROUP BY**可以和`HAVING`一起限定显示记录所需要满足的条件。只有满足条件的分组才会被显示。

例如，查询水果种类大于`>1`的分组信息。

```java
mysql> select s_id, GROUP_CONCAT(f_name) as Names from fruits GROUP BY s_id HAVING count(f_name) > 1;
+------+-------------------------+
| s_id | Names                   |
+------+-------------------------+
|  101 | apple,blackberry,cherry |
|  102 | orange,banana,grape     |
|  103 | apricot,coconut         |
|  104 | berry,lemon             |
|  105 | melon,xbabay,xxtt       |
|  107 | xxxx,xbababa            |
+------+-------------------------+
6 rows in set (0.01 sec)

```

可以看到由于`s_id = 106`的**供应商**的水果种类只有一种。所以不在结果中。

### 2、 `GROUP BY` **关键字和`WHERE`关键字都是用来过滤数据，有什么区别呢**？

 答: 

* `HAVING`在分组之后进行过滤。
*  `WHERE`在分组之前过滤。

看例子:

①都where和having都可以使用的场景:

```java
mysql> select f_id, f_price from fruits where f_price > 10.00;
+------+---------+
| f_id | f_price |
+------+---------+
| b1   |   10.20 |
| bs1  |   11.20 |
| m1   |   15.60 |
| m3   |   11.60 |
| t1   |   10.30 |
+------+---------+
5 rows in set (0.00 sec)

mysql> select f_id, f_price from fruits having f_price > 10.00;
+------+---------+
| f_id | f_price |
+------+---------+
| b1   |   10.20 |
| bs1  |   11.20 |
| m1   |   15.60 |
| m3   |   11.60 |
| t1   |   10.30 |
+------+---------+
5 rows in set (0.00 sec)

```

原因: `f_price`作为条件也出现在了查询字段中。

②只可以使用where，不可以使用having的情况：

```java
mysql> select f_id, f_name from fruits where f_price > 10.00;
+------+------------+
| f_id | f_name     |
+------+------------+
| b1   | blackberry |
| bs1  | orange     |
| m1   | mango      |
| m3   | xxtt       |
| t1   | banana     |
+------+------------+
5 rows in set (0.01 sec)

mysql> select f_id, f_name from fruits having f_price > 10.00;
ERROR 1054 (42S22): Unknown column 'f_price' in 'having clause'
mysql> 

```

原因: `f_price`没有在查询语句中出现，所以不能用`having`。

③只可以使用having，不可以使用where的情况：

查询供应商供应的种类`>2`的情况。

```java
mysql> select s_id, GROUP_CONCAT(f_name), COUNT(f_name) as al from fruits GROUP BY s_id HAVING al > 2;
+------+-------------------------+----+
| s_id | GROUP_CONCAT(f_name)    | al |
+------+-------------------------+----+
|  101 | apple,blackberry,cherry |  3 |
|  102 | orange,banana,grape     |  3 |
|  105 | melon,xbabay,xxtt       |  3 |
+------+-------------------------+----+
3 rows in set (0.00 sec)

mysql> select s_id, GROUP_CONCAT(f_name), COUNT(f_name) as al from fruits where al > 2 GROUP BY s_id;
ERROR 1054 (42S22): Unknown column 'al' in 'where clause'
mysql> 

```

原因: 因为`where`是在分组之前过滤，所以那个时候还没有`al`这个变量。

### 3、多字段分组，以及和order by一起使用

先按照`s_id`分组，然后按照`f_name`分组。这里在查询的时候，可能会报错。解决方案:

<https://blog.csdn.net/qq_31365675/article/details/81010650>

查询代码:

```mysql
mysql> select * from fruits GROUP BY s_id,f_name;
ERROR 1055 (42000): Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'lmysql.fruits.f_id' which is not functionally dependent on columns in GROUP BY clause; this is incompatible with sql_mode=only_full_group_by
# 解决错误的语句
mysql> SET sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));
Query OK, 0 rows affected (0.01 sec)
mysql> select * from fruits GROUP BY s_id,f_name;
+------+------+------------+---------+
| f_id | s_id | f_name     | f_price |
+------+------+------------+---------+
| a1   |  101 | apple      |    5.20 |
| b1   |  101 | blackberry |   10.20 |
| c0   |  101 | cherry     |    3.20 |
| t1   |  102 | banana     |   10.30 |
| t2   |  102 | grape      |    5.30 |
| bs1  |  102 | orange     |   11.20 |
| a2   |  103 | apricot    |    2.20 |
| o2   |  103 | coconut    |    9.20 |
| b2   |  104 | berry      |    7.60 |
| l2   |  104 | lemon      |    6.40 |
| bs2  |  105 | melon      |    8.20 |
| m2   |  105 | xbabay     |    2.60 |
| m3   |  105 | xxtt       |   11.60 |
| m1   |  106 | mango      |   15.60 |
| t4   |  107 | xbababa    |    3.60 |
| b5   |  107 | xxxx       |    3.60 |
+------+------+------------+---------+
16 rows in set (0.00 sec)

```

使用`group by`和`order by `结合:

要对求出来的`SUM(quantity * item_price) AS orderTotal`进行排序

```java
mysql> INSERT INTO orderitems(o_num, o_item, f_id, quantity, item_price)
    -> VALUES(30001, 1, 'a1', 10, 5.2),
    -> (30001, 2, 'b2', 3, 7.6),
    -> (30001, 3, 'bs1', 5, 11.2),
    -> (30001, 4, 'bs2', 15, 9.2),
    -> (30002, 1, 'b3', 2, 20.0),
    -> (30003, 1, 'c0', 100, 10),
    -> (30004, 1, 'o2', 50, 2.50),
    -> (30005, 1, 'c0', 5, 10),
    -> (30005, 2, 'b1', 10, 8.99),
    -> (30005, 3, 'a2', 10, 2.2),
    -> (30005, 4, 'm1', 5, 14.99);
Query OK, 11 rows affected (0.01 sec)
Records: 11  Duplicates: 0  Warnings: 0

mysql> SELECT o_num,  SUM(quantity * item_price) AS orderTotal
    -> FROM orderitems
    -> GROUP BY o_num
    -> HAVING SUM(quantity*item_price) >= 100;
+-------+------------+
| o_num | orderTotal |
+-------+------------+
| 30001 |     268.80 |
| 30003 |    1000.00 |
| 30004 |     125.00 |
| 30005 |     236.85 |
+-------+------------+
4 rows in set (0.01 sec)

mysql> SELECT o_num,  SUM(quantity * item_price) AS orderTotal
    -> FROM orderitems
    -> GROUP BY o_num
    -> HAVING SUM(quantity*item_price) >= 100
    -> ORDER BY orderTotal;
+-------+------------+
| o_num | orderTotal |
+-------+------------+
| 30004 |     125.00 |
| 30005 |     236.85 |
| 30001 |     268.80 |
| 30003 |    1000.00 |
+-------+------------+
4 rows in set (0.00 sec)

```

## 二、聚合函数

### 1、count

使用`count(*)`和`count(列)`的区别: `count(*)`是统计总的行数，而`count(列)`是统计这一列不为空的数目。

```java
mysql> select * from customers;
+-------+----------+---------------------+---------+--------+-----------+-------------------+
| c_id  | c_name   | c_address           | c_city  | c_zip  | c_contact | c_email           |
+-------+----------+---------------------+---------+--------+-----------+-------------------+
| 10001 | RedHook  | 200 Street          | Tianjin | 300000 | LiMing    | LMing@163.com     |
| 10002 | Stars    | 333 Fromage Lane    | Dalian  | 116000 | Zhangbo   | Jerry@hotmail.com |
| 10003 | Netbhood | 1 Sunny Place       | Qingdao | 266000 | LuoCong   | NULL              |
| 10004 | JOTO     | 829 Riverside Drive | Haikou  | 570000 | YangShan  | sam@hotmail.com   |
+-------+----------+---------------------+---------+--------+-----------+-------------------+
4 rows in set (0.00 sec)

mysql> select count(c_email) from customers;
+----------------+
| count(c_email) |
+----------------+
|              3 |
+----------------+
1 row in set (0.00 sec)

mysql> select count(*) from customers;
+----------+
| count(*) |
+----------+
|        4 |
+----------+
1 row in set (0.00 sec)

mysql> 

```



### 2、sum

```java
mysql> select * from orderitems;
+-------+--------+------+----------+------------+
| o_num | o_item | f_id | quantity | item_price |
+-------+--------+------+----------+------------+
| 30001 |      1 | a1   |       10 |       5.20 |
| 30001 |      2 | b2   |        3 |       7.60 |
| 30001 |      3 | bs1  |        5 |      11.20 |
| 30001 |      4 | bs2  |       15 |       9.20 |
| 30002 |      1 | b3   |        2 |      20.00 |
| 30003 |      1 | c0   |      100 |      10.00 |
| 30004 |      1 | o2   |       50 |       2.50 |
| 30005 |      1 | c0   |        5 |      10.00 |
| 30005 |      2 | b1   |       10 |       8.99 |
| 30005 |      3 | a2   |       10 |       2.20 |
| 30005 |      4 | m1   |        5 |      14.99 |
+-------+--------+------+----------+------------+
11 rows in set (0.00 sec)

mysql> select o_num, count(f_id) from orderitems group by o_num;
+-------+-------------+
| o_num | count(f_id) |
+-------+-------------+
| 30001 |           4 |
| 30002 |           1 |
| 30003 |           1 |
| 30004 |           1 |
| 30005 |           4 |
+-------+-------------+
5 rows in set (0.00 sec)

```

