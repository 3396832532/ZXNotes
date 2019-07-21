原因: 

* 如果多个线程同时使用put方法添加元素，而且假设正好存在两个put的key值发生了碰撞，即hash值一样，那么根据hashmap的实现，这两个key会添加到数组的同一个位置，这样会造成其中一个线程的put的数据会被覆盖；
* 如果多个线程同时检测到元素个数超过`数组大小*loadfactor`，这样会发生多个线程同时对Node数组进行扩容，都在重新计算元素位置以及复制数据，但是最终只有一个线程扩容后的数组会赋值给table，换言之，其他线程的都会丢失，并且各自线程的put数据也丢失了；

### 为什么HashMap是线程不安全的

总说HashMap是线程不安全的，不安全的，不安全的，那么到底为什么它是线程不安全的呢？要回答这个问题就要先来简单了解一下HashMap源码中的使用的`存储结构`(这里引用的是Java 8的源码，与7是不一样的)和它的`扩容机制`。

#### HashMap的内部存储结构

下面是HashMap使用的存储结构:

可以看到HashMap内部存储使用了一个Node数组(默认大小是16)，而Node类包含一个类型为Node的next的变量，也就是相当于一个链表，所有hash值相同(即产生了冲突)的key会存储到同一个链表里，HashMap内部存储结果

> 需要注意的是，在Java 8中如果hash值相同的key数量大于指定值(默认是8)时使用平衡树来代替链表，这会将get()方法的性能从O(n)提高到O(logn)。

#### HashMap的自动扩容机制

HashMap内部的Node数组默认的大小是16，假设有100万个元素，那么最好的情况下每个hash桶里都有62500个元素，这时get(),put(),remove()等方法效率都会降低。为了解决这个问题，HashMap提供了自动扩容机制，当元素个数达到数组大小*loadFactor后会扩大数组的大小，在默认情况下，数组大小为16，loadFactor为0.75，也就是说当HashMap中的元素超过16\*0.75=12时，会把数组大小扩展为2*16=32，并且重新计算每个元素在新数组中的位置。

自动扩容，可以看到没扩容前，获取EntryE需要遍历5个元素，扩容之后只需要2次。

#### 为什么线程不安全

个人觉得HashMap在并发时可能出现的问题主要是两方面,首先如果多个线程同时使用put方法添加元素，而且假设正好存在两个put的key发生了碰撞(hash值一样)，那么根据HashMap的实现，这两个key会添加到数组的同一个位置，这样最终就会发生其中一个线程的put的数据被覆盖。第二就是如果多个线程同时检测到元素个数超过数组大小*loadFactor，这样就会发生多个线程同时对Node数组进行扩容，都在重新计算元素位置以及复制数据，但是最终只有一个线程扩容后的数组会赋给table，也就是说其他线程的都会丢失，并且各自线程put的数据也丢失。
关于HashMap线程不安全这一点，《Java并发编程的艺术》一书中是这样说的：

> HashMap在并发执行put操作时会引起死循环，导致CPU利用率接近100%。因为多线程会导致HashMap的Node链表形成环形数据结构，一旦形成环形数据结构，Node的next节点永远不为空，就会在获取Node时产生死循环。

哇，居然会产生死循环。。。。google了一下，才知道死循环并不是发生在put操作时，而是发生在扩容时。详细的解释可以看下面几篇博客：

- [酷壳-Java HashMap的死循环](http://coolshell.cn/articles/9606.html)
- [HashMap在java并发中如何发生死循环](http://firezhfox.iteye.com/blog/2241043)
- [How does a HashMap work in JAVA](http://coding-geek.com/how-does-a-hashmap-work-in-java/)



参考: 

<https://blog.csdn.net/wufaliang003/article/details/80219296>

<http://www.importnew.com/21396.html>