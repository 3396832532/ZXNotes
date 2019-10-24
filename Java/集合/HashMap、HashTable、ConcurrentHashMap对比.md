# HashMap、HashTable、ConcurrentHashMap对比

## 一、关于null值

### 1、HashMap

* HashMap中，`null`可以作为键，这样的键只有一个。
* 可以有一个或多个键所对应的值为`null`。
* 当`get()`方法返回`null`值时，可能是`HashMap`中没有该键，也可能是键所对应的值为`null`。
* 因此，在`HashMap`中不能由`get()`方法来判断`HashMap`中是否存在某个键，而应该用`containsKey()`方法来判断。

### 2、HashTable

* 既不支持`Null　Key`，也不支持`Null Value`，如果`put(null, ...)`，就会抛出空指针异常；

## 二、迭代

另一个区别是HashMap的迭代器(`Iterator`)是`fail-fast`迭代器。

而Hashtable的`enumerator`迭代器不是`fail-fast`的。

所以当有其它线程改变了HashMap的结构（增加或者移除元素），将会抛出`ConcurrentModificationException`，但迭代器本身的`remove()`方法移除元素则不会抛出`ConcurrentModificationException`异常。

但这并不是一个一定发生的行为，要看JVM。这条同样也是Enumeration和Iterator的区别。

> “快速失败”也就是`fail-fast`，它是Java集合的一种错误检测机制。
>
> 当多个线程对集合进行结构上的改变的操作时，有可能会产生fail-fast机制。
>
> 记住是有可能，而不是一定。
>
> 例如：假设存在两个线程（线程1、线程2），线程1通过Iterator在遍历集合A中的元素，在某个时候线程2修改了集合A的结构（**是结构上面的修改，而不是简单的修改集合元素的内容**），那么这个时候程序就会抛出 ConcurrentModificationException 异常，从而产生fail-fast机制。

fail-fast底层: 维护`modCount == expectModCount`:

迭代器在调用`next()、remove()`方法时都是调用checkForComodification()方法。

该方法主要就是检测`modCount == expectedModCount` 。 若不等则抛出ConcurrentModificationException 异常，从而产生fail-fast机制。所以要弄清楚为什么会产生fail-fast机制我们就必须要用弄明白为什么`modCount != expectedModCount` ，他们的值在什么时候发生改变的。

比如：

有两个线程（线程A，线程B），其中线程A负责遍历list、线程B修改list。线程A在遍历list过程的某个时候（此时`expectedModCount = modCount=N`，线程启动，同时线程B增加一个元素，这是modCount的值发生改变（`modCount + 1 = N + 1`）。线程A继续遍历执行next方法时，通告checkForComodification方法发现expectedModCount  = N  ，而`modCount = N + 1`，两者不等，这时就抛出ConcurrentModificationException 异常，从而产生fail-fast机制。

## 三、扩容

* HashTable扩容: 默认初始size为**11**，加载因子`0.75`，`newsize = olesize*2+1`；
* HashMap扩容: 初始size为**16**，扩容：`newsize = oldsize*2`，size一定为2的n次幂；

## 四、计算index

- HashMap计算index方法：`index = hash & (tab.length – 1)`；
- HashTable计算index方法: `index = (hash & 0x7FFFFFFF) % tab.length`；

## 五、其他

HashTable基于Dictionary类，而HashMap是基于AbstractMap。

Dictionary是什么？它是任何可将键映射到相应值的类的抽象父类，而AbstractMap是基于Map接口的骨干实现，它以最大限度地减少实现此接口所需的工作。

`HashMap`中的`0`位置上放的是`null`值(键为`null`)。

## 六、常见问题

### 1、Hash Map 和 Hash Table 的区别

Hash Map 和 Hash Table 的区别

* hashmap采用的是数组(桶位)+链表+红黑树结构实现，而hashtable中采用的是数组(桶位)+链表实现

* Hashtable 的方法是同步的， HashMap 未经同步，所以在多线程场合要手动同步。
* Hashtable 不允许 null 值 (key 和 value 都不可以 ) ， HashMap 允许 null 值 (key 和 value 都可以) 。
* 两者的遍历方式大同小异， Hashtable 仅仅比 HashMap 多一个 `elements()` 方法。Hashtable 和 HashMap 都能通过 values() 方法返回一个 Collection ，然后进行遍历处理。两者也都可以通过 entrySet() 方法返回一个 Set ， 然后进行遍历处理。
* HashTable 使用 Enumeration ， HashMap 使用 Iterator 。
* 哈希值的使用不同， Hashtable 直接使用对象的 hashCode 。而 HashMap 重新计算 hash 值，而且用于代替求模。hashmap中的寻址方法采用的是位运算按位与，而hashtable中寻址方式采用的是求余数。
* Hashtable 中 hash 数组默认大小是 11 ，增加的方式是 old*2+1 。 HashMap 中 hash 数组的默认大小是 16 ，而且一定是 2的指数(HashTable中可以为任意整数)。
* HashTable 基于 Dictionary 类，而 HashMap 基于 AbstractMap 类
* hashmap中出现hash冲突时，如果链表节点数小于8时是将新元素加入到链表的末尾，而hashtable中出现hash冲突时采用的是将新元素加入到链表的开头。

### 2、Hash Map 中的 key 可以是任何对象或数据类型吗

* 可以为 null ，但不能是可变对象，如果是可变对象的话，对象中的属性改变，则对象 HashCode 也进行相应的改变，导致下次无法查找到已存在 Map 中的数据。
* 如果可变对象在 HashMap 中被用作键，那就要小心在改变对象状态的时候，不要改变它的哈希值了。我们只需要保证成员变量的改变能保证该对象的哈希值不变即可。


### 3、HashMap和Concurrent HashMap区别， Concurrent HashMap 线程安全吗， Concurrent HashMap如何保证 线程安全？

HashMap 和 Concurrent HashMap 区别？

* HashMap 是非线程安全的， CurrentHashMap 是线程安全的。
* ConcurrentHashMap 将整个 Hash 桶进行了分段 segment ，也就是将这个大的数组分成了几个小的片段 segment ，而且每个小的片段 segment 上面都有锁存在，那么在插入元素的时候就需要先找到应该插入到哪一个片段 segment ，然后再在这个片段上面进行插入，而且这里还需要获取 segment 锁。
* ConcurrentHashMap 让锁的粒度更精细一些，并发性能更好。

Concurrent HashMap 如何保证 线程安全？

* HashTable 容器在竞争激烈的并发环境下表现出效率低下的原因是所有访问 HashTable 的线程都必须竞争同一把锁，**那假如容器里有多把锁，每一把锁用于锁容器其中一部分数据，那么当多线程访问容器里不同数据段的数据时，线程间就不会存在锁竞争，从而可以有效的提高并发访问效率**，这就是 ConcurrentHashMap 所使用的锁分段技术，首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。
* get 操作的高效之处在于整个 get 过程不需要加锁，除非读到的值是空的才会加锁重读 。 get 方法里将要使用的共享变都定义成 volatile ，如用于统计当前 Segement 大小的 count 字段和用于存储值的 HashEntry 的 value 。定义成 volatile 的变量，**能够在线程之间保持可见性，能够被多线程同时读，并且保证不会读到过期的值，但是只能被单线程写（有一种情况可以被多线程写，就是写入的值不依赖于原值），在 get 操作里只需要读不需要写共享变量 count 和 value ，所以可以不用加锁**。
* Put 方法首先定位到 Segment ，然后在 Segment 里进行插入操作。插入操作需要经历两个步骤，第一步判断是否需要对 Segment 里的 HashEntry 数组进行扩容，第二步定位添加元素的位置然后放在 `HashEntry` 数组里。



