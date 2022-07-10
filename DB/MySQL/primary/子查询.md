# 子查询

### 1、ANY、SOME、ALL

一个查询语句嵌套在另一个查询语句内部的查询。

常见操作符 : `ANY(SOME)、ALL、IN、EXISTS`。

ANY和SOME关键字是同义词，表示满足其中任一条件，它们允许创建一个表达式对子查询的返回值列表进行比较，只要满足内层子查询中的任何一个比较条件，就返回一个结果作为外层查询的条件。
下面定义两个表tb1和tb2：

```mysql
CREATE table tbl1 ( num1 INT NOT NULL);

CREATE table tbl2 ( num2 INT NOT NULL);
```

分别向两个表中插入数据：

```mysql
INSERT INTO tbl1 values(1), (5), (13), (27);

INSERT INTO tbl2 values(6), (14), (11), (20);

```

**ANY关键字接在一个比较操作符的后面，表示若与子查询返回的任何值比较为TRUE**，则返回TRUE。

【例7.53】返回tbl2表的所有num2列，然后将tbl1中的num1的值与之进行比较，只要大于num2的任何1个值，即为符合查询条件的结果。

 ```mysql
SELECT num1 FROM tbl1 WHERE num1 > ANY (SELECT num2 FROM tbl2);
 ```

【例7.54】返回tbl1表中比tbl2表num2 列所有值都大的值，SQL语句如下：

```mysql
 SELECT num1 FROM tbl1 WHERE num1 > ALL (SELECT num2 FROM tbl2);
```

### 2、EXIST

**Exits关键字后面的参数是一个任意的子查询，系统对子查询进行运算以判断它是否返回行，如果至少返回一行，那么EXISTS的结果为true，此时外层查询语句将进行查询；如果子查询没有返回任何行，那么EXISTS的返回结果是false，此时外层语句不会进行查询**；

【例7.55】查询suppliers表中是否存在`s_id=107`的供应商，如果存在，则查询fruits表中的记录，SQL语句如下：

```java
mysql>  SELECT * FROM fruits
    ->      WHERE EXISTS
    ->      (SELECT s_name FROM suppliers WHERE s_id = 107);
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

```

【例7.56】查询suppliers表中是否存在s_id=107的供应商，如果存在，则查询fruits表中的f_price大于10.20的记录，SQL语句如下：

```mysql
mysql>  SELECT * FROM fruits
    ->      WHERE f_price>10.20 AND EXISTS
    ->      (SELECT s_name FROM suppliers WHERE s_id = 107);
+------+------+--------+---------+
| f_id | s_id | f_name | f_price |
+------+------+--------+---------+
| bs1  |  102 | orange |   11.20 |
| m1   |  106 | mango  |   15.60 |
| m3   |  105 | xxtt   |   11.60 |
| t1   |  102 | banana |   10.30 |
+------+------+--------+---------+
4 rows in set (0.00 sec)

```

【例7.57】查询suppliers表中是否存在s_id=107的供应商，如果**不存在**则查询fruits表中的记录，SQL语句如下 (`NOT EXISTS`)：

```mysql
mysql>  SELECT * FROM fruits
    ->      WHERE NOT EXISTS
    ->      (SELECT s_name FROM suppliers WHERE s_id = 107);
Empty set (0.00 sec)

```

【例7.58】在`orderitems`表中查询f_id为c0的订单号，并根据订单号查询具有订单号的客户c_id，SQL语句如下：

```mysql
mysql> SELECT c_id FROM orders WHERE o_num IN      
       (SELECT o_num  FROM orderitems WHERE f_id = 'c0');
+-------+
| c_id  |
+-------+
| 10004 |
| 10001 |
+-------+
2 rows in set (0.01 sec)

```

内层：` SELECT o_num  FROM orderitems WHERE f_id = 'c0';` 

可以看到，符合条件的o_num列的值有两个：30003和30005，然后执行外层查询，在orders表中查询订单号等于30003或30005的客户c_id。嵌套子查询语句还可以写为如下形式，实现相同的效果：
 `SELECT c_id FROM orders WHERE o_num IN (30003, 30005);`

【例7.61】在suppliers表中查询s_city等于“Tianjin”的供应商s_id，然后在fruits表中查询所有非该供应商提供的水果的种类，SQL语句如下：

```mysql
mysql>  SELECT s_id, f_name FROM fruits
    ->      WHERE s_id <>
    ->      (SELECT s1.s_id FROM suppliers AS s1 WHERE s1.s_city = 'Tianjin');
+------+---------+
| s_id | f_name  |
+------+---------+
|  103 | apricot |
|  104 | berry   |
|  107 | xxxx    |
|  102 | orange  |
|  105 | melon   |
|  104 | lemon   |
|  106 | mango   |
|  105 | xbabay  |
|  105 | xxtt    |
|  103 | coconut |
|  102 | banana  |
|  102 | grape   |
|  107 | xbababa |
+------+---------+
13 rows in set (0.27 sec)

```

### 3、合并查询结果

【例7.62】查询所有价格小于9的水果的信息，查询s_id等于101和103所有的水果的信息，使用UNION连接查询结果，SQL语句如下：

```mysql
mysql> SELECT s_id, f_name, f_price 
    -> FROM fruits
    -> WHERE f_price < 9.0
    -> UNION ALL
    -> SELECT s_id, f_name, f_price 
    -> FROM fruits
    -> WHERE s_id IN(101,103);
+------+------------+---------+
| s_id | f_name     | f_price |
+------+------------+---------+
|  101 | apple      |    5.20 |
|  103 | apricot    |    2.20 |
|  104 | berry      |    7.60 |
|  107 | xxxx       |    3.60 |
|  105 | melon      |    8.20 |
|  101 | cherry     |    3.20 |
|  104 | lemon      |    6.40 |
|  105 | xbabay     |    2.60 |
|  102 | grape      |    5.30 |
|  107 | xbababa    |    3.60 |
|  101 | apple      |    5.20 |
|  103 | apricot    |    2.20 |
|  101 | blackberry |   10.20 |
|  101 | cherry     |    3.20 |
|  103 | coconut    |    9.20 |
+------+------------+---------+
15 rows in set (0.02 sec)
```



