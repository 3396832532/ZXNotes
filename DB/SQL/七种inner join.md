## 七种inner join

先给出案例表:

<div align="center"><img src="images/1_1.png"></div><br>

七种查询总结图:

![1_2.png](images/1_2.png)

第一种`inner join`:

![1_4.png](images/1_4.png)

第二种`left join`:

![1_5.png](images/1_5.png)

第三种`right join`:

![1_6.png](images/1_6.png)

第四种`left join where b.id is null`:

![1_7.png](images/1_7.png)

第五种`right join where a.deptId is null`:

![1_8.png](images/1_8.png)

第六种`FULL OUT JOIN`:  (使用`union`实现并集和去重)

![1_9.png](images/1_9.png)

第七种: `a、b两者的独有`:

![1_10.png](images/1_10.png)