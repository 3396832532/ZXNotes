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

而Hashtable的`enumerator`迭代器不是fail-fast的。

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

该方法主要就是检测`modCount == expectedModCount` 。 若不等则抛出ConcurrentModificationException 异常，从而产生fail-fast机制。所以要弄清楚为什么会产生fail-fast机制我们就必须要用弄明白为什么modCount != expectedModCount ，他们的值在什么时候发生改变的。

比如：

有两个线程（线程A，线程B），其中线程A负责遍历list、线程B修改list。线程A在遍历list过程的某个时候（此时`expectedModCount = modCount=N`，线程启动，同时线程B增加一个元素，这是modCount的值发生改变\（`modCount + 1 = N + 1`）。线程A继续遍历执行next方法时，通告checkForComodification方法发现expectedModCount  = N  ，而`modCount = N + 1`，两者不等，这时就抛出ConcurrentModificationException 异常，从而产生fail-fast机制。

## 三、扩容

* HashTable扩容: 默认初始size为**11**，加载因子`0.75`，`newsize = olesize*2+1`；
* HashMap扩容: 初始size为**16**，扩容：`newsize = oldsize*2`，size一定为2的n次幂；



## 四、计算index

- HashMap计算index方法：`index = hash & (tab.length – 1)`；
- HashTable计算index方法: `index = (hash & 0x7FFFFFFF) % tab.length`；

## 四、其他

HashTable基于Dictionary类，而HashMap是基于AbstractMap。

Dictionary是什么？它是任何可将键映射到相应值的类的抽象父类，而AbstractMap是基于Map接口的骨干实现，它以最大限度地减少实现此接口所需的工作。



`HashMap`中的`0`位置上放的是`null`值(键为`null`)。







