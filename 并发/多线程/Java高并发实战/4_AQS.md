# AQS组件相关

AQS内部维护一个队列

![1556848061230](assets/1556848061230.png)

## 一、CountDownLatch

![1556848105954](assets/1556848105954.png)

案例: 

```java
@Slf4j
public class CountDownLatchDemo {

    private final static int threadCount = 200;

    public static void main(String[] args) throws InterruptedException {

        ExecutorService service = Executors.newCachedThreadPool();

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for(int i = 0; i < threadCount; i++){
            final int threadNum = i;
            service.execute(() -> {
                try{
                    test(threadNum);
                }catch (Exception e){
                    log.error("exception", e);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 等待上面的都要执行完，才会执行下面的逻辑，有一点join的意思
        countDownLatch.await();  //这里还可以指定等待的时间，如果超过这个时间，就可以执行下面的逻辑了

        log.info("finish!");

        service.shutdown();
    }

    private static void test (int threadNum) throws InterruptedException {
        Thread.sleep(100);

        log.info("{}", threadNum);

        Thread.sleep(100);
    }
}

```

执行结果:

![1556847350311](assets/1556847350311.png)

## 二、Semaphore

用于: **仅能提供有限访问的资源**。

比如: 项目中的数据库，连接数只能是20个，如果上层应用远远大于20，如果同时对数据库进行操作，可能会导致异常，这时就可以通过Semaphore做**并发访问控制**。

案例一:

```java
@Slf4j
public class SemaphoreDemo {

    private final static int threadCount = 20;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    semaphore.acquire(); // 获取一个许可
                    test(threadNum);
                    semaphore.release(); // 释放一个许可
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception {
        log.info("{}", threadNum);
        Thread.sleep(1000);
    }
}

```

输出(**发现每一秒执行三个**):

![1556849610524](assets/1556849610524.png)

再看`tryAcquire()`的使用: 尝试获取一个许可:

```java
/**
 * tryAcquire()尝试获取一个许可
 */
@Slf4j
public class SemaphoreExample2 {

    private final static int threadCount = 20;

    public static void main(String[] args) throws Exception {

        ExecutorService exec = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            exec.execute(() -> {
                try {
                    if (semaphore.tryAcquire()) { // 尝试获取一个许可
                        test(threadNum);
                        semaphore.release(); // 释放一个许可
                    }
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
    }

    private static void test(int threadNum) throws Exception {
        log.info("{}", threadNum);
        Thread.sleep(1000);
    }
}

```

注意观察输出:

![1556849876676](assets/1556849876676.png)

另外`tryAcquire()`也可以指定尝试获取许可的等待时间，超过这个时间就不等待了。

```java
if (semaphore.tryAcquire(5000, TimeUnit.MILLISECONDS)) { // 尝试获取一个许可
```

## CyclicBarrier

