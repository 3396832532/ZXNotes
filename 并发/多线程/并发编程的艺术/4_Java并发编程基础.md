# Java并发编程基础

## 一、启动和终止线程
### 1、关于InterruptedException异常

抛InterruptedException的代表方法有：

1、java.lang.Object 类的 wait 方法

2、java.lang.Thread 类的 sleep 方法

3、java.lang.Thread 类的 join 方法


三个方法有个共同点就是需要花点时间：

1、执行wait方法的线程，会进入等待区等待被notify/notify All。在等待期间，线程不会活动。

2、执行sleep方法的线程，会暂停执行参数内所设置的时间。

3、执行join方法的线程，会等待到指定的线程结束为止。

正是因为需要花时间的操作会降低程序的响应性，所以可能会取消/中途放弃执行这个方法。而取消主要是通过interrupt方法，所以可能抛出InterruptedException异常。


线程只是被interrupt是不会抛InterruptedException异常，而是线程处于阻塞状态的（sleep, wait, join）的同时被interrupt是才会抛该异常。

比如如下的代码：

```java
while(true){
    try {
     Thread.sleep(1000);
    }catch(InterruptedException ex)
    {
        throw new RuntimeException(ex);
    } 
}
```
当线程执行sleep(1000)之后会被立即阻塞，如果在阻塞时外面调用interrupt来中断这个线程，那么就会`throw new RuntimeException(ex);`这个时候变量interrupt没有被置为true。

如果代码中不检测标识变量，也不调用`Thread.currentThread().interrupt()`，那么其实线程并未中断，这不能保证上层程序真正停止并退出。上层可能捕获了运行时异常，所以这个线程还是存活的。

所以，在任何时候碰到InterruptedException，都要自己手动把这个线程中断。由于这个时候已经处于非阻塞状态，所以可以正常中断，最正确的代码如下：

```java

while(!Thread.currentThread().isInterrupted()){
    try {
     Thread.sleep(1000);
    }catch(InterruptedException ex)
    {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ex);
    } 

```
通过`Thread.currentThread().interrupt();`将标识变量重新设置为 true，这样可以保证线程一定能够被及时中断。

### 2、interrupt、isInterrupted和interrupted三种方法区别

```java
while (!Thread.currentThread().isInterrupted()) {   
    try {  
        Thread.sleep(5000);  
    } catch (InterruptedException e) {  
        Thread.currentThread().interrupt();//①：发出中断请求，设置中断状态  
        System.out.println(Thread.currentThread().isInterrupted());//②:判断中断状态（不清除中断状态）  
        System.out.println(Thread.interrupted());//③:判断中断状态（清除中断状态）  
    }  
}  
```
1、interrupt()会发出中断命令，设置中断状态。而isInterrupted（）和interrupted（）并不会发出中断线程的命令；

2、调用`Thread.currentThread().interrupt()`方法并不会立刻中断当前线程，只有等当前线程阻塞在sleep、wait和join这些方法的内部会不断的检查中断状态的值上才会执行；

3、interrupted方法，用来检查中断状态并清除中断状态。  isInterrupted（）和interrupted（）的区别在于 interrupted会清除中断的状态；所以上面实例程序    会一直运行。如果注释掉第三点(catch代码库第三条)，则程序会在下一次到达sleep的时候终止；