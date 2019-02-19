# Java虚拟机性能监控工具

* 一、JDK的命令行工具
* 二、JDK的可视化工具
* 三、性能调优

***
## 一、JDK的命令行工具
主要有以下几种: 

* jps (`Java Process Status Tool`): 虚拟机进程状态工具；
* jstat (`JVM Statistics Monitoring Tool`):  虚拟机统计信息监视工具；
* jinfo (`Configuration Info for Java`):  Java配置信息工具；
* jmap (`Memory Map for Java`):  Java内存映像工具；
* jhat (`JVM Heap Dump Browser`):  虚拟机堆转存储快照工具；
* jstack (`Stack Trace for Java`): Java堆栈跟踪工具； 

下面一个个来总结: 

### 1、jps : 虚拟机进程状态工具

* jps（JVM Process Status Tool，虚拟机进程监控工具），这个命令可以列出正在运行的虚拟机进程，并显示虚拟机执行主类名称，以及这些进程的本地虚拟机唯一ID。
* 这个 ID 被称为**本地虚拟机唯一 ID**（Local Virtual Machine Identifier，简写为LVMID）。如果你在 linux 的一台服务器上使用 jps 得到的 LVMID 其实就是和 ps 命令得到的 PID 是一样的。

命令格式: 

```txt
jps [options] [hostid]
```
jps工具主要选项：

* -q，只输出LVMID，省略主类的名称；
* -m，输出虚拟机进程启动时传给主类main()函数的参数；
* -l，输出主类的全名，如果进程执行的是Jar包，输出Jar路径；
* -v，输出虚拟机进程启动时JVM参数；


### 2、jstat : 虚拟机统计信息监视工具

* jstat（JVM Statistics Monitoring Tool，虚拟机统计信息监视工具），这个命令用于监视虚拟机各种运行状态信息。
* 它可以显示本地或者远程虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据，虽然没有GUI图形界面，只是提供了纯文本控制台环境的服务器上，但它是运行期间定位虚拟机性能问题的首选工具。


 命令格式: 

```txt
jstat [option vmid [interval [s | ms] [count ] ] ]
```
option代表着用户希望查询的虚拟机信息，主要分为3类: 类装载、垃圾收集、运行期编译状况，具体可以参照下表: 
![在这里插入图片描述](images/tool1.png)

示例: 

![在这里插入图片描述](images/tool2.png)

**更多jstat的详细讲解可以参考[这篇博客](https://blog.csdn.net/zhaozheng7758/article/details/8623549)。**

### 3、jinfo ：Java配置信息工具

* 实时地查看和调整虚拟机各项参数。

查看14838是否使用CMS收集器: 
```java
jinfo -flag UseConcMarkSweepGC 14836
```
查看2788的MaxPerm大小可以用
```java
jinfo -flag MaxPermSize 2788
```

### 4、jmap : Java内存映像工具

* jmap（Memory Map for Java，内存映像工具），用于生成堆转存的快照，一般是 heapdump 或者 dump 文件。如果不使用 jmap 命令，可以使用 -XX:+HeapDumpOnOutOfMemoryError 参数，当虚拟机发生内存溢出的时候可以产生快照。或者使用kill -3 pid也可以产生。
* jmap 的作用并不仅仅是为了获取 dump 文件，它可以查询 finalize 执行队列，java 堆和永久代的详细信息，如空间使用率，当前用的哪种收集器。

格式: 

```txt
jmap [option] vmid
```
生成某个pid的存储快照示例: 
![在这里插入图片描述](images/tool3.png)
![在这里插入图片描述](images/tool4.png)

### 5、jhat :  虚拟机堆转存储快照工具

* jhat（虚拟机堆转储快照分析工具），这个工具是用来分析 jmap dump 出来的文件。 由于这个工具功能比较简陋，运行起来也比较耗时，所以这个工具不推荐使用，推荐使用MAT。

分析示例: 

![在这里插入图片描述](images/tool5.png)

### 6、jstack ： Java堆栈跟踪工具
* jstack（Java Stack Trace，Java堆栈跟踪工具），这个命令用于查看虚拟机当前时刻的线程快照（一般是threaddump 或者 javacore文件）。<font color = green>线程快照就是当前虚拟机内每一条线程正在执行的方法堆栈的集合。
* <font color = blue>生成线程快照的主要目的是：定位线程出现长时间停顿的原因，入线程间死锁、死循环、请求外部资源导致的长时间等待都是导致线程长时间停顿的常见原因。
* 线程出现停顿的时候通过jstack来查看各个线程的调用堆栈，就可以知道没有响应的线程到底在后台做些什么事情。

命令格式：
```txt
jstack [option] vmid
```

使用：查看进程8024 的堆栈信息

```txt
jstack 8024
```

***
## 二、 JDK的可视化工具

* Jconsole : Java监视与管理控制台
* VisualVM: 多合一故障处理工具


下面具体看这个两个工具: 

### 1、 Jconsole : Java监视与管理控制台

* JConsole可以监视**JVM 内存的使用情况、线程堆栈跟踪、已装入的类和 VM 信息以及 CE MBean。**
* JConsole一个 Java GUI 监视工具，可以以图表化的形式显示各种数据。并可通过远程连接监视远程的服务器VM。
* 用 Java 写的 GUI 程序，用来监控 VM，并可监控远程的 VM，非常易用，而且功能非常强。命令行里打 Jconsole，选则进程就可以了。
* 可以监控内存和线程，以及检测是否出现死锁；

### 2、VisualVM ：多合一故障处理工具


* VisualVm 同 Jconsole 都是一个基于图形化界面的、可以查看本地及远程的 JAVA GUI 监控工具，VisualVm 同 Jconsole 的使用方式一样，<font color= red>直接在命令行打入JVisualVm 即可启动</font>，VisualVm 界面更美观一些，数据更实时。

***
### 性能调优
性能调优: 

* 知识
* 工具
* 数据
* 经验


**关于性能调优更多的可以看看这篇[JVM性能调优案例](https://tech.meituan.com/jvm_optimize.html)。**



