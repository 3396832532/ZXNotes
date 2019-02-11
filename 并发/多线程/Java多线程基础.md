# Java多线程基础

 - 线程介绍
 - 线程声明周期以及start和run方法区别等
 - 银行排队业务案例
 - Thread内部一些更加深入的东西
 - 守护线程

***

## 线程介绍

### 1、简单案例引入
> 模拟同时从数据库中读取数据和写入文件；

 - 模拟同时从数据库中读取数据和写入文件，这两个操作作为两个线程同时进行，不需要相互的等待；
 - 这里使用的是匿名类创建的线程，重写了Thread类中的run方法，当然也可以通过继承Thread类或者实现Runnable接口来创建线程；

```java
/**
 * 基本的创建线程
 * 模拟两个线程同时执行 读数据库和写文件
 */
public class Code_01_TryConcurrency {

    public static void main(String[] args) {

        //一边从数据库读取，一边写入文件

        new Thread("Read-Thread") {
            @Override
            public void run () {
                readFromData();
            }
        }.start();//start()方法是立刻返回的，不会阻塞

        new Thread("Write-Thread") {
            @Override
            public void run() {
                writeToFile() ;
            }
        }.start(); //只有调用start()方法才是线程,不然只是一个普通的类
    }


    static void readFromData() {
        try {
            println("Begin read data from db......");
            Thread.sleep(1000 * 5L);
            println("After read data !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println("Read data successfully!");
    }

    static void writeToFile() {
        try {
            println("Begin write data to File.......");
            Thread.sleep(1000 * 5L);
            println("After write date !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        println("Write data successfully!");
    }

    static void println(String msg) {
        System.out.println(msg);
    }

}

```
效果:

![这里写图片描述](images/t1.png) 

### 线程声明周期以及start和run方法区别等

 - **注意只有当Thread的实例调用start()方法时，才能真正的成为一个线程，调用run()方法不是一个线程；**

![这里写图片描述](https://img-blog.csdn.net/20180909232522964?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
下面的代码，如果是t.start()输出的线程名为"Read-Thread"，而如果调用的是t.run则会输出main线程名。
```java
	   Thread t = new Thread("Read-Thread"){
            @Override
            public void run() {
                println(Thread.currentThread().getName()); //如果调用start就是"Read-Thread"，如果调用的是run方法就是main
                readFromDataBase();
            }
        };

//        t.start();  //只有调用start()方法才是真正的线程
        t.run();
```

 - **Thread中使用了[模板方法设计模式](https://blog.csdn.net/zxzxzx0119/article/details/81709199)，也就是我们继承Thread类，重写的是run()方法(钩子方法)，但是调用的却是start()方法(最终方法)的原因。**
    ![在这里插入图片描述](https://img-blog.csdn.net/20181007190314719?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
    ![在这里插入图片描述](https://img-blog.csdn.net/20181007190629875?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
 - Java应用程序的main函数是一个线程，在JVM启动的时候调用，名字叫main；
 - **当你调用一个线程start()方法的时候，此时至少有两个线程，一个是调用你的线程(例如main)，还有一个是执行run()方法的线程；**
 - JVM启动时，实际上有多个线程，但是至少有一个**非守护线程**；



其中一个是 main，另一个是 Thread-0，之前
说过在操作系统启动一个 Java 虚拟机 (JVM) 的时候，其实是启动了一个进程，而在该进
程里面启动了一个以上的线程，其中 Thread-0 这个线程就是 1.2.2 节中创建的，main 线程
是由JVM 启动时创建的，我们都知道 J2SE 程序的人口就是 main 函数，虽然我们在 1.2.2
节中显式地创建了一个线程，事实上还有一个 main 线程，当然还有一些其他的守护线程，
比如垃圾回收线程、RMI 线程等。

关于守护线程和非守护线程:

> Java分为两种线程：用户线程和守护线程
>
> 所谓守护线程是指在程序运行的时候在后台提供一种通用服务的线程，比如**垃圾回收线程**就是一个很称职的守护者，并且这种线程并不属于程序中不可或缺的部分。因此，当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。反过来说，只要任何非守护线程还在运行，程序就不会终止。
>
> 守护线程和用户线程的没啥本质的区别：唯一的不同之处就在于**虚拟机的离开：如果用户线程已经全部退出运行了，只剩下守护线程存在了**，虚拟机也就退出了。 因为没有了被守护者，守护线程也就没有工作可做了，也就没有继续运行程序的必要了。
>
> 将线程转换为守护线程可以通过调用Thread对象的`setDaemon(true)`方法来实现。在使用守护线程时需要注意一下几点：
>
> * `thread.setDaemon(true)`必须在`thread.start()`之前设置，否则会抛出一个`IllegalThreadStateException`异常。你不能把正在运行的常规线程设置为守护线程。
> * 在Daemon线程中产生的新线程也是Daemon的。
> * 守护线程应该永远不去访问固有资源，如文件、数据库，因为它会在任何时候甚至在一个操作的中间发生中断。

### 3、线程生命周期
![这里写图片描述](images/t2_线程生命周期.png)

每种状态的解释: 

#### 1)、New状态

* 当我们用关键字 new 创建一个 Thread 对象时，**此时它并不处于执行状态**；
* 因为没有调用 `start` 方法启动该线程，那么线程的状态为`NEW` 状态。
* 准确地说，它只是 Thread 对象的状态，因为在没有 start 之前，该线程根本不存在，与你用关键字 new 创建一个普通的 Java对象没什么区别。
* NEW 状态通过 `start` 方法进入 `RUNNABLE` 状态。

#### 2)、Runnable状态

* 线程对象进入 RUNNABLE 状态必须调用 start 方法，那么此时才是真正地在 JVM 进程中创建了一个线程，线程一经启动就可以立即得到执行吗?

* 答案是否定的，线程的运行与否和进程一样都要听令于 CPU 的调度，那么我们把这个中间状态称为可执行状态(RUNNABLE)，**也就是说它具备执行的资格，但是并没有真正地执行起来而是在等待 CPU的调度**；
* 由于存在`Running` 状态，所以不会直接进入`BLOCKED` 状态和`TERMINATED `状态，即使是在线程的执行逻辑中调用 wait、sleep 或者其他 block 的 IO 操作等，也必须先获得 CPU 的调度执行权才可以，严格来讲，RUNNABLE 的线程只能**意外终止或者进入RUNNING 状态**；(即`Runnable`不能直接到`BLOCKED`和`TERMINATED`状态)

#### 3)、Running状态

一且 CPU 通过轮询或者其他方式从任务可执行队列中选中了线程，那么此时它才能真正地执行自己的逻辑代码(也就是RUNNING)，需要说明的一点是**一个正在 RUNNING 状态的线程事实上也是RUNNABLE 的，但是反过来则不成立**。在该状态中，线程的状态可以发生如下的状态转换。

* 直接进入TERMINATED 状态，比如调用 JDK 已经不推荐使用的 stop 方法或者判断某个逻辑标识；
* 进入 BLOCKED 状态，比如调用了 `sleep` ，或者 `wait` 方法而加入了 `waitSet` 中；
* 进行某个阻塞的 IO 操作，比如因网络数据的读写而进入了 BLOCKED 状态；
* 获取某个锁资源，从而加入到该锁的阻塞队列中而进入了 BLOCKED 状态；
* 由于 CPU 的调度器轮询使该线程放弃执行，进入RUNNABLE 状态；
* 线程主动调用 `yield` 方法，放弃 CPU 执行权，进入RUNNABLE 状态；

#### 4)、BLOCKED状态

在BLOCKED状态可以转换的状态:

*  直接进入TERMINATED 状态，比如调用 JDK 已经不推荐使用的 stop 方法或者意外死亡 (`JVM Crash` ) ；
*   线程阻塞的操作结束，比如读取了想要的数据字节进入到RUNNABLE 状态；
*  线程完成了指定时间的休眠，进入到了 RUNNABLE 状态；
*  Wait 中的线程被其他线程 `notify/notifyall` 唤醒，进入RUNNABLE 状态；
*  线程获取到了某个锁资源，进入RUNNABLE 状态；
*  线程在阻塞过程中被打断，比如其他线程调用了 `interrupt` 方法，进入RUNNABLE；

#### 5)、TERMINATED状态

TERMINATED 是一个线程的最终状态，在该状态中线程将**不会切换到其他任何状态，线程进入TERMINATED 状态，意味着该线程的整个生命周期都结束了。**

下列这些情况将会使线程进入 TERMINATED 状态。

* 线程运行正常结束，结束生命周期；
* 线程运行出错意外结束；
* `JVM Crash`，导致所有的线程都结束；

***
### 银行排队业务案例
![这里写图片描述](https://img-blog.csdn.net/20180908220950149?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#### 方案一(各个线程各搞各的)
```java
public class TicketWindow extends Thread{

    private String name;

    private static final int MAX = 5;

    private int index = 1;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while(index <= MAX){
            System.out.println("柜台: " + name + ",当前号码: " + (index++));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class MyTest {

    public static void main(String[] args) {

        TicketWindow t1 = new TicketWindow("一号");
        t1.start();

        TicketWindow t2 = new TicketWindow("二号");
        t2.start();

        TicketWindow t3 = new TicketWindow("三号");
        t3.start();
    }
}

```

![这里写图片描述](https://img-blog.csdn.net/20180908223225329?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#### 方案二(使用static关键字(顺序不对))

```java
public class TicketWindow extends Thread{

    private String name;

    private static final int MAX = 5;

    private static int index = 1;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while(index <= MAX){
            System.out.println("柜台: " + name + ",当前号码: " + (index++));
        }
        try {
            Thread.sleep(1000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

```
测试类不变: 
![这里写图片描述](https://img-blog.csdn.net/20180908223151372?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#### 方法三(使用Runnable接口)
**可以使用实现Runnable接口来传入到Thread的构造方法当中，完成和static关键字同样的效果。**
![这里写图片描述](https://img-blog.csdn.net/20180910001149250?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
```java
public class TickWindowRunnable implements Runnable {

    private int index = 1; //没有使用static关键字

    public final static int MAX = 50;

    @Override
    public void run() {
        while(index <= MAX){
            System.out.println("柜台: " + Thread.currentThread().getName() + ",当前号码: " + (index++));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


```
```java
public class MyTest {

    public static void main(String[] args) {
        final TickWindowRunnable ticketWindow = new TickWindowRunnable();
        
        Thread t1 = new Thread(ticketWindow,"一号");
        Thread t2 = new Thread(ticketWindow,"二号");
        Thread t3 = new Thread(ticketWindow,"三号");
public class ThreadConstruction {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1");

        ThreadGroup group = new ThreadGroup("TestGroup");
        Thread t2 = new Thread(group,"t1");

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        System.out.println("main group : "+ mainGroup.getName());

        System.out.println( t1.getThreadGroup() == mainGroup); 
        System.out.println( t2.getThreadGroup() == mainGroup);
        System.out.println( t2.getThreadGroup() == group);
    }
}


        t1.start();
        t2.start();
        t3.start();
    }
}

```

 - **这里注意Runnable接口使用的是设计模式中的[策略模式](https://blog.csdn.net/zxzxzx0119/article/details/81327444)**，Runnable接口类类似接口的行为族，具体的实现由我们自己创建的是实现Runnable接口的类来指定，并且重写方法run()方法，具体指定自己的实现。 

![这里写图片描述](https://img-blog.csdn.net/20180910001013264?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

***
### Thread内部更加深入的东西
![这里写图片描述](https://img-blog.csdn.net/20180909232233636?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![在这里插入图片描述](https://img-blog.csdn.net/2018100719245529?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
##### Thread与ThreadGroup

```java
public class ThreadConstruction {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1");

        ThreadGroup group = new ThreadGroup("TestGroup");
        Thread t2 = new Thread(group,"t1");

        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        System.out.println("main group : "+ mainGroup.getName());

        System.out.println( t1.getThreadGroup() == mainGroup); 
        System.out.println( t2.getThreadGroup() == mainGroup);
        System.out.println( t2.getThreadGroup() == group);
    }
}

```
输出: 
![在这里插入图片描述](https://img-blog.csdn.net/20181007193926569?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
结论：
![在这里插入图片描述](https://img-blog.csdn.net/20181007193846554?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
在构造Thread()的时候指定这个线程虚拟机栈(线程独占区)的大小: 
![这里写图片描述](https://img-blog.csdn.net/20180909200244839?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
stackSize中虚拟机栈和创建线程的关系：
![在这里插入图片描述](https://img-blog.csdn.net/20181007194530371?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#### 守护线程
![在这里插入图片描述](https://img-blog.csdn.net/20181007202220745?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![在这里插入图片描述](https://img-blog.csdn.net/20181007202419696?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
再看一个嵌套的例子: 
```java
public class TestDaemonThread {

    public static void main(String[] args) {

        Thread t = new Thread(){
            @Override
            public void run() {

                Thread innerThread = new Thread(){
                    @Override
                    public void run() {
                        while(true){
                            System.out.println("do something in innerThread...");
                            try {
                                Thread.sleep(1_000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };
                //设置了这个，当父线程结束了之后，子线程也就结束了，如果注释，父线程结束，子线程也不一定会结束
                innerThread.setDaemon(true); //不能在启动之后  setDaemon 不然会抛出异常
                innerThread.start();

                try {
                    Thread.sleep(1_000);
                    System.out.println("T thread is finished!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        t.start();
    }
}

```
运行结果: 
![在这里插入图片描述](https://img-blog.csdn.net/20181007201849894?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
也就是说: 当主线程main结束，里面的线程都结束了，但是如果注释掉setDaemon(true)
运行: 
![在这里插入图片描述](https://img-blog.csdn.net/20181007201931457?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)