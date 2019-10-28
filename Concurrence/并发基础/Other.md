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

看下列`Thread`构造函数

```java
Thread(ThreadGroup group, Runnable target, String name, long stackSize) 
//分配新的 Thread 对象，以便将 target 作为其运行对象，将指定的 name 作为其名称，作为 group 所引用的线程组的一员，并具有指定的堆栈大小。
```

构造Thread的时候传入`stackSize`代表着线程占用的`stack`大小，如果没有指定`stackSize`的大小，默认是`0`，`0`代表着会忽略该参数，改参数会被JNI函数(`Native`)去使用。

#### 2)、JVM内存结构

详见`JVM`相关知识。可看我的[另一篇博客](../../Java基础/JVM/JVM总结(一) - 内存区域与内存管理.md)。

#### 3)、Thread与虚拟机栈

虚拟机栈的大小大概是可以存放21456个栈桢(栈桢中存放局部变量表、操作数栈、动态链接...)，而自己创建的线程的虚拟机栈只有大概15534个栈桢，但是我们可以在创建线程的时候指定`stackSize`；





### 16、实现一个容器监控另一个容器

实现一个容器，提供两个方法，`add`，`size `                                     

写两个线程，线程`1`添加`10`个元素到容器中，线程`2`实现监控元素的个数，当个数到`5`个时，线程`2`给出提示并结束。 

先看初步版本:

分析下面这个程序，能完成这个功能吗？
答: 不能，因为虽然 `t2`线程 和` t1`线程的共享变量 list不是内存可见的(没有加` volatile`关键字)


```java
/**
 * 分析下面这个程序，能完成这个功能吗？
 * 答: 不能，因为虽然 t2线程 和 t1线程的共享变量 list不是内存可见的(没有加 volatile关键字)
 */
public class MyContainer1 {

    List<Integer>list = new ArrayList<>();

    public void add(Integer o) {
        list.add(o);
    }

    public int size() {
        return list.size();
    }

    public static void main(String[] args) {
        MyContainer1 c = new MyContainer1();

        new Thread(() -> {
            for(int i=0; i<10; i++) {
                c.add(i);
                System.out.println("add " + i);
                
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            while(true) {
                if(c.size() == 5) {
                    break;
                }
            }
            System.out.println("t2 结束");
        }, "t2").start();
    }
}
```

解决上面问题的方法就是在`list`前面加上一个` volatile`关键字即可。

但是，还有一个问题，`t2`线程的死循环很浪费cpu，如果不用死循环，该怎么做呢？

这里使用wait和notif机制，wait会释放锁，而notify不会释放锁:

* 需要注意的是: 运用这种方法，必须要保证t2先执行，也就是首先让t2监听才可以；

阅读下面的程序，并分析输出结果
可以读到输出结果并不是size=5时t2退出，而是t1结束时t2才接收到通知而退出
想想这是为什么？

答: 因为notify不会放弃锁，所以最后程序结果不对，因为这个没有放弃锁，所以t2得不到执行。

```java
public class MyContainer3 {

    // 添加volatile，使t2能够得到通知
    volatile List<Integer> list = new ArrayList<>();

    public void add(Integer o) {
        list.add(o);
    }

    public int size() {
        return list.size();
    }

    public static void main(String[] args) {
        MyContainer3 c = new MyContainer3();

        final Object lock = new Object();// 随便创建一个锁

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("t2启动");
                if (c.size() != 5) {
                    try {
                        lock.wait(); // 放入条件等待队列，放弃当前锁, 被阻塞
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("t2 结束");
            }

        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("t1启动");
            synchronized (lock) {
                for (int i = 0; i < 10; i++) {
                    c.add(i);
                    System.out.println("add " + i);

                    if (c.size() == 5) {
                        lock.notify(); // 不会放弃当前锁，所以最后程序结果不对，因为这个没有放弃锁，所以t2得不到执行
                    }

                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "t1").start();
    }
}
```

解决办法:

* `notify`之后，`t1`必须释放锁，`t2`退出后，也必须`notify`，通知`t1`继续执行；
* 整个通信过程比较繁琐；

```java
public class MyContainer4 {

    //添加volatile，使t2能够得到通知
    volatile List<Integer> list = new ArrayList<>();

    public void add(Integer o) {
        list.add(o);
    }

    public int size() {
        return list.size();
    }

    public static void main(String[] args) {
        MyContainer4 c = new MyContainer4();

        final Object lock = new Object();

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("t2启动");
                if (c.size() != 5) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("t2 结束");
                //t1释放锁之后，我t2得到了执行，最后我还要通知t1继续执行
                lock.notify();
            }

        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("t1启动");
            synchronized (lock) {
                for (int i = 0; i < 10; i++) {
                    c.add(i);
                    System.out.println("add " + i);

                    if (c.size() == 5) {
                        lock.notify();
                        //再加一个wait(), ---> 释放锁，让t2得以执行
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "t1").start();
    }
}
```

更高效的方式:

* 使用Latch（门闩）替代wait notify来进行通知；
* 好处是通信方式简单，同时也可以指定等待时间；
* 使用`await()`和`countdown()`方法替代wait和notify；
* CountDownLatch**不涉及锁定**(这是和上面不同的，这比上面高效)， 当count的值为零时当前线程继续运行(`new CountDownCatch(count)`)；
* **当不涉及同步，只是涉及线程通信的时候，用`synchronized + wait/notify`就显得太重了**；
* 这时应该考虑countdownlatch/cyclicbarrier/semaphore

代码:

```java
public class MyContainer5 {

    //添加volatile，使t2能够得到通知
    volatile List<Integer> list = new ArrayList<>();

    public void add(Integer o) {
        list.add(o);
    }

    public int size() {
        return list.size();
    }

    public static void main(String[] args) {
        MyContainer5 c = new MyContainer5();

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            System.out.println("t2启动");
            if (c.size() != 5) {
                try {
                    latch.await();
                    //也可以指定等待时间
                    //latch.await(5000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("t2 结束");
        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("t1启动");
            for (int i = 0; i < 10; i++) {
                c.add(i);
                System.out.println("add " + i);

                if (c.size() == 5) {
                    // 打开门闩，让t2得以执行
                    latch.countDown(); // 调用一次countDown，构造函数中构造的那个值就-1，到了0，门栓就开了(通知t2运行)
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "t1").start();
    }
}
```

### 18、实现生产者消费者同步容器

使用`wait()`和`notify()`实现:

几点注意:

* `wait()`一般和`while`放在一起使用，具体原因如下图。

* 每次都是`notifyAll()`而不是`notify()`，因为`notify()`是通知任意一个线程，有可能通知不到**生产者/消费者**，所以需要`notifyAll()`。

![thread_02.png](images/thread_02.png)



### 19、ThreadLocal线程局部变量(空间换时间)

使用volatile可以实现 不同线程之间变量的可见性，但是我如果就是不要两个线程互相看到呢?

```java
public class ThreadLocal1 {

	// 使用volatile可以实现 不同线程之间变量的可见性，但是我如果就是不要两个线程互相看到呢?
    volatile static Person p = new Person();

    public static void main(String[] args) {
                
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println(p.name);
        }).start();
        
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.name = "lisi";
        }).start();

    }
	static class Person {
		String name = "zhangsan";
	}
}
```

用`ThreadLocal`可以实现两个线程的局部变量互不影响:

```java
public class ThreadLocal2 {

    //volatile static Person p = new Person();
    static ThreadLocal<Person> tl = new ThreadLocal<>(); // 这个就是在每一个线程里面都有一份独立的变量不会影响

    public static void main(String[] args) {
                
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println(tl.get());
        }).start();
        
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tl.set(new Person());
        }).start(); 
    }

    static class Person {
        String name = "zhangsan";
    }
}
```







# 线程安全性

三种特性:

* 原子性 : 提供了互斥访问，同一时刻只能有一个线程来对它进行操作； 例如`i++`不是原子的。
* 可见性 : 一个线程对主内存的修改可以及时的被其他线程观察到；
* 有序性 : 一个线程观察其他线程中的指令执行顺序，由于指令重排序的存在，该观察结果一般杂乱无序；

`Happens-before`原则:

1、程序次序规则：在一个单独的线程中，按照程序代码的执行流顺序，（时间上）先执行的操作happen—before（时间上）后执行的操作。

2、管理锁定规则：一个unlock操作happen—before后面（时间上的先后顺序，下同）对同一个锁的lock操作。

3、volatile变量规则：对一个volatile变量的写操作happen—before后面对该变量的读操作。

4、线程启动规则：Thread对象的`start()`方法happen—before此线程的每一个动作。

5、线程终止规则：线程的所有操作都happen—before对此线程的终止检测，可以通过`Thread.join()`方法结束、`Thread.isAlive()`的返回值等手段检测到线程已经终止执行。	

6、线程中断规则：对线程`interrupt()`方法的调用happen—before发生于被中断线程的代码检测到中断时事件的发生。

7、对象终结规则：一个对象的初始化完成（构造函数执行结束）happen—before它的`finalize()`方法的开始。

8、传递性：如果操作A happen—before操作B，操作B happen—before操作C，那么可以得出A happen—before操作C。

**如果两个操作之间的关系不在happens-before**原则里面，**它们就没有顺序性保障，虚拟机可以对它们进行随机地重排序**。