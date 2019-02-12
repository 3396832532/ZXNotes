# Java多线程基础

 - [一、线程介绍](#一线程介绍)
    - [1、简单案例引入](#1简单案例引入)
    - [2、start和run方法区别](#2start和run方法区别)
    - [3、线程生命周期](#3线程生命周期)
    - [4、银行排队业务案例](#4银行排队业务案例)
 - 二、深入理解Thread构造函数
 - 三、Thread API的详细介绍
 - 四、线程安全与数据同步
 - 五、线程间通信
 - 六、ThreadGroup详细讲解

***

## 一、线程介绍

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

### 2、start和run方法区别

 - 注意只有当Thread的实例调用`start()`方法时，才能真正的成为一个线程，否则`Thread`和其他普通的Java对象没有什么区别；
 - 调用`run()`方法不是一个线程；
 - `start()`方法是一个立即返回的方法，不会让程序陷入阻塞；

下面的代码，如果是`t.start()`输出的线程名为`"Read-Thread"`，而如果调用的是`t.run()`则会输出`main`线程名。
```java
Thread t = new Thread("Read-Thread"){
    @Override
    public void run() {
        println(Thread.currentThread().getName()); //如果调用start就是"Read-Thread"，如果调用的是run方法就是main
        readFromDataBase();
    }
};

//  t.start();  //只有调用start()方法才是真正的线程
t.run();
```

Thread中使用了[模板方法设计模式](https://blog.csdn.net/zxzxzx0119/article/details/81709199)，也就是我们继承Thread类，重写的是`run()`方法(钩子方法)，但是调用的却是`start()`方法(最终方法)的原因。

![在这里插入图片描述](https://img-blog.csdn.net/20181007190314719?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

关于模板方法，简单说: **就是父类写了一些固定的逻辑，但是给自己留了一个方法可以实现，有些逻辑不能改，有些可以改**，看一个简单的例子:

```java
public class Code_02_TemplateMethod {

    // 不能给子类实现
    public final void print(String msg){
        System.out.println("################");
        wrapPrint(msg);
        System.out.println("################");
    }

    // 给子类实现(也可以写成抽象方法，子类必须实现)
    protected void wrapPrint(String msg){

    }

    public static void main(String[] args){

        Code_02_TemplateMethod t1 = new Code_02_TemplateMethod(){
            @Override
            protected void wrapPrint(String msg) {
                System.out.println("**" + msg + "**");
            }
        };
        t1.print("Hello Thread");

        Code_02_TemplateMethod t2 = new Code_02_TemplateMethod(){
            @Override
            protected void wrapPrint(String msg) {
                System.out.println("++" + msg + "++");
            }
        };
        t2.print("Hello Thread");
    }
}

```
输出:
```c
################
**Hello Thread**
################
################
++Hello Thread++
################
```

* `print` 方法类似于 Thread 的 `start`方法，而 wrapPrint 则类似于 `run` 方法；
* 这样做的好处是，程序结构由父类控制，并且是 `final` 修饰的，不允许被重写，子类只需要实现想要的罗辑任务即可；

也就是说`start`方法中会调用`start0`方法(并没有调用`run`方法)，而重新的`run`方法何时被调用呢?

在开始执行这个线程时，JVM 将会调用该线程的 `run` 方法，换言之，**`run` 方法是被 JNI 方法 `start0()` 调用的**，仔细阅读 `start()` 的源码将会总结出如下几个知识要点。

* Thread 被构造后的NEW 状态，事实上 threadStatus 这个内部属性为 0。
* 不能两次启动 Thread，否则就会出现 IlegalThreadStateException 异常。
* 线程启动后将会被加入到一个 ThreadGroup 中；
* 一个线程生命周期结束，也就是到了 TERMINATED 状态，再次调用 start 方法是不允许的，也就是说 TERMINATED 状态是没有办法回到RUNNABLE/RUNNING 状态的。

其他总结:

 - Java应用程序的main函数是一个线程，在JVM启动的时候调用，名字叫`main`；
 - **当你调用一个线程`start()`方法的时候，此时至少有两个线程，一个是调用你的线程(例如`main`)，还有一个是执行`run()`方法的线程；**
 - JVM启动时，实际上有多个线程，但是至少有一个**非守护线程**；

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
### 4、银行排队业务案例

![这里写图片描述](https://img-blog.csdn.net/20180908220950149?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
#### 1)、方案一，各个线程各搞各的
```java
public class Code_03_TicketWindowTest01 {

    static class TicketWindow extends Thread {

        private String name;

        private static final int MAX = 5;

        private int index = 1;

        public TicketWindow(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (index <= MAX) {
                System.out.println("柜台: " + name + ",当前号码: " + (index++));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

输出如下，可以看到每个柜台都有`5`个号，显然不对，银行总共才`5`个号。

```java
柜台: 二号,当前号码: 1
柜台: 一号,当前号码: 1
柜台: 三号,当前号码: 1
柜台: 二号,当前号码: 2
柜台: 一号,当前号码: 2
柜台: 三号,当前号码: 2
柜台: 二号,当前号码: 3
柜台: 一号,当前号码: 3
柜台: 三号,当前号码: 3
柜台: 一号,当前号码: 4
柜台: 三号,当前号码: 4
柜台: 二号,当前号码: 4
柜台: 一号,当前号码: 5
柜台: 三号,当前号码: 5
柜台: 二号,当前号码: 5
```

#### 2)、方案二，使用static关键字

最简单的解决方案 : 将`index`设置成`static`，这样每个对象都是用这个值，总共就只有`MAX`个了。

但是这种方案也有一些缺点:

* `static`修饰的变量生命周期很长，浪费资源；
* 如果将号码`MAX`调整到`500、1000`等稍微大一点的数字就会出现线程安全问题； 

```java
static class TicketWindow extends Thread {

    private String name;

    private static final int MAX = 5;

    private static int index = 1;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (index <= MAX) {
            System.out.println("柜台: " + name + ",当前号码: " + (index++));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
测试类不变， 输出如下，可以看到总共只会输出`5`次，但是不是好的方案。

```java
柜台: 一号,当前号码: 1
柜台: 二号,当前号码: 2
柜台: 三号,当前号码: 3
柜台: 一号,当前号码: 4
柜台: 二号,当前号码: 5
```

#### 3)、方法三，使用Runnable接口以及策略

**可以使用实现Runnable接口来传入到Thread的构造方法当中，完成和static关键字同样的效果。**

```java
public class Code_03_TicketWindowTest02 {

    static class TicketWindow implements Runnable{

        private static final int MAX = 5;

        private int index = 1; // 没有做static修饰

        @Override
        public void run() {
            while (index <= MAX) {
                System.out.println(Thread.currentThread() + " 的号码是: " + (index++));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
         }
    }

    public static void main(String[] args){
        TicketWindow ticketWindow = new TicketWindow(); // 只有一个 TicketWindow实例
        Thread t1 = new Thread(ticketWindow, "一号窗口");
        t1.start();
        Thread t2 = new Thread(ticketWindow, "二号窗口");
        t2.start();
        Thread t3 = new Thread(ticketWindow, "三号窗口");
        t3.start();
    }
}


```
输出: (此时号码就是有序的):

```java
Thread[一号窗口,5,main] 的号码是: 1
Thread[二号窗口,5,main] 的号码是: 2
Thread[三号窗口,5,main] 的号码是: 3
Thread[一号窗口,5,main] 的号码是: 4
Thread[二号窗口,5,main] 的号码是: 5
```

重写Thread类的`run`方法和实现`Runnable`接口的`run`方法有一个很大的不同:

* Thread类的`run`方法是不能共享的，也就是说`A`线程不能把`B`线程的`run`方法当做自己的执行单元；
* 而使用`Runnable`接口则很任意就能实现这一点，**使用同一个`Runnable`的实例构造不同的Thread实例**；

**这里注意Runnable接口使用的是设计模式中的[策略模式](https://blog.csdn.net/zxzxzx0119/article/details/81327444)**: 

* Runnable接口类类似接口的行为族；
* 具体的实现由我们自己创建的是实现Runnable接口的类来指定，并且重写方法`run()`方法，具体指定自己的实现。 

> 很多书籍经常会提到，创建线程有两种方式，第一种是构造一个Thread，第二种是实现 Runnable 接口，这种说法是错误的，最起码是不严谨的，在 JDK 中代表线程的就只有 Thread 这个类，我们在前面分析过，线程的执行单元就是run方法，你可以通过继承 Thread 然后重写 run 方法实现自己的业务逻辑，也可以实现 Runnable 接口实现自己的业务逻辑，代码如下:
>
> ```java
> @Override
> public void run(){
>     // 如果构造Thread时传入了Runnable，则会执行runnable的run方法
>     if(target != null){
>         target.run();
>     }
>     // 否则需要重写Thread类的run()方法
> }
> ```
>
> **准确地讲，创建线程只有一种方式那就是构造Thread 类。**
>
> **而实现线程的执行单元则有两种方式，第一种是重写 Thread 的 run 方法，第二种是实现 Runnable 接口的 run 方法，并且将 Runnable 实例用作构造 Thread 的参数。**

## 二、深入理解Thread构造函数

### 1、线程的默认命名

打开JDK的源码可以看到我们构造Thread的时候，默认的线程的名字是

* 以`Thread-`开头，从`0`开始计数；
* 即`Thread-0、Thread-1、Thread-2...`；

```java
public Thread() {
    init(null, null, "Thread-" + nextThreadNum(), 0);
}
/* For autonumbering anonymous threads. */
private static int threadInitNumber;
private static synchronized int nextThreadNum() {
    return threadInitNumber++;
}
```

修改线程的名字，在线程启动之前，还有一个而已修改线程名字的机会，一旦线程启动，名字就不可以修改: 

下面是在Thread中修改名字的代码: 

```java
public final synchronized void setName(String name) {
    checkAccess();
    if (name == null) {
        throw new NullPointerException("name cannot be null");
    }

    this.name = name;
    if (threadStatus != 0) {
        setNativeName(name);
    }
}
```

### 2、线程的父子关系

Thread的所有构造函数，最终都会去调用一个静态方法`init`，我们截取片段代码对其进行分析，不难发现新创建的任何一个线程都会有一个父线程:

这里截取`Thread`的`init`的部分代码: 

```java
 private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
     if (name == null) {
         throw new NullPointerException("name cannot be null");
     }
     this.name = name;
     Thread parent = currentThread();//获取当前运行的线程作为赴现场
     SecurityManager security = System.getSecurityManager();
     this.group = g;// 设置线程组
     this.daemon = parent.isDaemon();// 当前线程是否为守护线程，取决于父线程
     this.priority = parent.getPriority(); // 设置优先级
     setPriority(priority);
     this.stackSize = stackSize;
     /* Set thread ID */
     tid = nextThreadID();
 }
```

上面代码中的 `currentThread() `是获取当前线程，在线程生命周期中，我们说过线程的最初状态为NEW，没有执行 start 方法之前，它只能算是一个 Thread 的实例，并不意味着一个新的线程被创建，因此 `currentThread()` 代表的将会是**创建它的那个线程**，因此我们可以得出以下结论。

*  一个线程的创建肯定是由另一个线程完成的。
*  **被创建线程的父线程是创建它的线程**。

**我们都知道 main 函数所在的线程是由 JVM 创建的，也就是 main 线程，那就意味着我们前面创建的所有线程，其父线程都是 main 线程。**

### 3、Thread和ThreadGroup

在Thread的构造函数中，可以显示的指定线程的Group，也就是ThreadGroup，下面看`init`方法的中间部分: 

```java
 if (g == null) {
     /* Determine if it's an applet or not */

     /* If there is a security manager, ask the security manager
               what to do. */
     if (security != null) {
         g = security.getThreadGroup();
     }

     /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
     if (g == null) {
         g = parent.getThreadGroup();
     }
 }
```

源码的意思: **如果在构造Thread的时候没有显示的指定一个ThreadGroup，那么子线程将会被加入父线程所在的线程组。**

简单测试代码: 

```java
public class Code_04_ThreadGroupTest {

    public static void main(String[] args){
        PrintStream out = System.out;

        Thread t1 = new Thread("t1");// 没有给t1指定group
        ThreadGroup group1 = new ThreadGroup("group1");
        Thread t2 = new Thread(group1, "t2");
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        out.println("Main Thread Group : " + mainGroup.getName());
        out.println(t1.getThreadGroup() == mainGroup); // true // 默认就是main线程的
        out.println(t2.getThreadGroup() == mainGroup); // false
        out.println(group1 == t2.getThreadGroup()); // true // 指定了就是这个了
    }
}

```

输出:

```java
Main Thread Group : main
true
false
true
```

得出结论: 

* main线程所在的`ThreadGroup`称为`main`；
* 构造一个线程的时候如果没有显示的指定`ThreadGroup`，那么它将会和父线程属于同一个`ThreadGroup`（且拥有同样的优先级）；

### 4、Thread和JVM虚拟机栈

#### 1)、Thread与Stacksize

#### 2)、JVM内存结构

#### 3)、Thread与虚拟机栈

![这里写图片描述](https://img-blog.csdn.net/20180909232233636?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![在这里插入图片描述](https://img-blog.csdn.net/2018100719245529?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
**Thread与ThreadGroup**

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

**守护线程**

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