# Git

Git功能简单概述

- 可以随时回滚到之前的代码版本；
- 协同开发时不会覆盖别人的代码；
- 留下修改记录；
- 发版时可以方便的管理不同的版本；

### 1、Git工作模式

* 版本库初始化

* 个人计算机从版本服务器同步

操作

* 90%以上的操作在个人计算机上

* 添加文件

* 修改文件

* 提交变更

* 查看版本历史等

* 版本库同步

* 将本地修改推送到版本服务器

版本控制系统:

![1_1.png](images/1_1.png)



### 2、Git文件存储

注意文件存储和SVN不同:

![1_2.png](images/1_2.png)

特点:

* 直接记录快照`snapshoot`。而并非比较差异；
* 近乎所有操作都在本地执行；
* **时刻保持数据完整性**；
* 多数操作仅添加数据；
* **文件的三种状态**（只会处于这三种状态）
  * 已修改(modified)
  * 已暂存(staged)
  * 已提交(committed)

### 3、Git文件状态

* Git文件: 已被版本库管理的文件；
* 已修改: 在工作目录(`working directory`)修改Git文件；
* 已暂存: 对已修改的文件执行Git暂存操作，将文件存入**暂存区**(`staging area`)； (注意SVN中没有暂存区这个概念)
* 已提交: 将已暂存的文件执行Git提交操作，将文件存入版本库(`git directory`)；

![1_3.png](images/1_3.png)

> git add 放到暂存区；
>
> git commit 从暂存区放到版本仓库中；

### 4、本地版本库与服务器版本库

Git是分布式的。

![1_5.png](images/1_5.png)

### 5、实际操作Git

![1_4.png](images/1_4.png)

关于配置`user.name`和`user.email`:

可以有三个地方配置:

比如输入`git config`命令， 会出现下面三个信息，即系统级别`system`，全局`global`，和局部`local`。其中优先级不断升高。

```shell
zxzxin@zxzxin:~/Git/gitlearn$ git config
usage: git config [<options>]

Config file location
    --global              use global config file
    --system              use system config file
    --local               use repository config file
    -f, --file <file>     use given config file
    --blob <blob-id>      read config from given blob object

```

这里展示一下配置我们局部的`gitlearn`仓库:

![1_6.png](images/1_6.png)



> git 提交的id(`commit id`)是一个摘要值，这个摘要值实际上是一个`sha1`计算出来的。

