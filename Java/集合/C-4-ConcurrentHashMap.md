# ConcurrentHashMap

## 一、基本介绍

JDK8的HashMap中比JDK7多了一个红黑树以用来增加数据分布的平衡性。JDK8的ConcurrentHashMap相对于JDK7来说，锁的粒度变小了，也就是说并发性更大了。**用了synchronized+CAS代替了ReentrantLock** ，数据结构也简单了一些。

1、锁粒度：

JDK8的锁粒度相比于JDK7降低了,JDK1.7版本锁的粒度是基于Segment的，包含多个HashEntry，而JDK1.8锁的粒度就是HashEntry（首节点）.

2、代码复杂度

   **JDK8的虽然去掉了分段锁的概念，即数据结构变得简单了，但是相应的代码复杂度就上来了，比如红黑树**

3、锁的方式

   JDK8采用synchronized+CAS代替了ReentrantLock，这块的原因可能就是synchronized比较受重视。

- 因为粒度降低了，在相对而言的低粒度加锁方式，synchronized并不比ReentrantLock差，在粗粒度加锁中ReentrantLock可能通过Condition来控制各个低粒度的边界，更加的灵活，而在低粒度中，Condition的优势就没有了。
- 基于JVM的synchronized优化空间更大，使用内嵌的关键字比使用API更加自然。
- 在大量的数据操作下，对于JVM的内存压力，基于API的ReentrantLock会开销更多的内存。

## 二、put

```java
/** Implementation for put and putIfAbsent
    * 实现put和putIfAbsent
    * onlyIfAbsent含义：如果我们传入的key已存在我们是否去替换，true:不替换，false：替换。
    * */
final V putVal(K key, V value, boolean onlyIfAbsent) {
    //键值都不为空
    if (key == null || value == null) throw new NullPointerException();
    //发散键的hash值
    int hash = spread(key.hashCode());
    //桶数量 0
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        //检查表是否初始化
        if (tab == null || (n = tab.length) == 0)
            //初始化表
            tab = initTable();
        //检查指定键所在节点是否为空
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            //通过CAS方法添加键值对
            if (casTabAt(tab, i, null,
                         new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin 添加到空桶时没有锁
        }
        //如果当前节点的Hash值为MOVED
        else if ((fh = f.hash) == MOVED)
            //如果还在进行扩容操作就先进行扩容
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            //synchronized锁定此f节点
            synchronized (f) {
                //再次检测节点是否相同
                if (tabAt(tab, i) == f) {
                    //如果此节点hash值大于0
                    if (fh >= 0) {
                        //桶数量为1
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            //获取到指定的键
                            if (e.hash == hash &&
                                ((ek = e.key) == key ||
                                 (ek != null && key.equals(ek)))) {
                                //保存老的值
                                oldVal = e.val;
                                //onlyIfAbsent：如果我们传入的key已存在我们是否去替换，true:不替换，false：替换。
                                if (!onlyIfAbsent)
                                    //替换掉
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            //下一个节点为空
                            if ((e = e.next) == null) {
                                //新建节点
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;
                            }
                        }
                    }
                    //如果时树节点
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        //桶数量为2
                        binCount = 2;
                        //找到插入的位置，插入节点，平衡插入
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                              value)) != null) {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            //如果桶数量不为0
            if (binCount != 0) {
                //如果桶数量大于树化阈值
                if (binCount >= TREEIFY_THRESHOLD)
                    //将链表转为树
                    // 这个方法和 HashMap 中稍微有一点点不同，那就是它不是一定会进行红黑树转换，
                    // 如果当前数组的长度小于 64，那么会选择进行数组扩容，而不是转换为红黑树
                    treeifyBin(tab, i);
                //如果老的值存在，则返回
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    //增加数量
    addCount(1L, binCount);
    return null;
}
```

这个方法的大概思路就是：

1、初始化操作

2、如果待插入键的插入位置没有节点，则通过CAS方式创建一个节点。

3、如果当前节点的Hash值为 static final int MOVED = -1; // hash for forwarding nodes 转发节点的哈希，则调用helpTransfer方法，如果还在进行扩容操作就先进行扩容，Helps transfer if a resize is in progress。

4、再有就是发生Hash碰撞时通过synchronized关键字锁定当前节点，这里有两种情况，一种是链表形式：直接遍历到尾端插入，一种是红黑树：按照红黑树结构插入。

5、计数

这里面有两个地方保证了putVal的并发实现，一个是没有待插入节点时的CAS技术，一个是发现有存在Hash碰撞时的synchronized关键字。



## 三、get

ConcurrentHashMap中的get方法是没有锁的。

分析一下get方法是怎么实现的多线程下数据可见性。

1、通过tabAt的volatile读

2、通过结点Node的volatile属性next

3、通过结点Node的volatile属性val

JVM保证了volatile修饰的数据的内存可见性。

```java
/**
 * Returns the value to which the specified key is mapped,
 * or {@code null} if this map contains no mapping for the key.
 *
 * 返回指定键映射到的值，如果此映射不包含键的映射，则返回{@code null}。
 *
 * <p>More formally, if this map contains a mapping from a key
 * {@code k} to a value {@code v} such that {@code key.equals(k)},
 * then this method returns {@code v}; otherwise it returns
 * {@code null}.  (There can be at most one such mapping.)
 *
 * 更正式地说，如果这个映射包含一个键{@code k}到一个值{@code v}的映射，
 * 使得{@code key.equals(k)}，那么这个方法返回{@code v};否则返回{@code null}。
 * (最多可以有一个这样的映射。)
 *
 * @throws NullPointerException if the specified key is null 如果指定的键为空
 */
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    //扩展key的Hash值
    int h = spread(key.hashCode());
    //如果表格不为空且表格长度大于0且所查key的所在节点不为空，(n - 1) & h相当于取模操作，即获取其索引位置。
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        //如果获取到当前值
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                //返回当前节点值
                return e.val;
        }
        //如果当前节点hash值小于0，溢出的时候为负数
        else if (eh < 0)
            //调用find方法，返回值，或者没找到则返回空
            return (p = e.find(h, key)) != null ? p.val : null;
        //获取当前节点的下一个节点，如果非空，则判断是否相同，找到了即返回其值。
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```

Node类:

```java
static class Node<K,V> implements Entry<K,V> {
    //哈希码
    final int hash;
    //键
    final K key;
    //值
    volatile V val;
    //下一个节点
    volatile Node<K,V> next;

    Node(int hash, K key, V val, Node<K,V> next) {
        this.hash = hash;
        this.key = key;
        this.val = val;
        this.next = next;
    }

    public final K getKey()       { return key; }
    public final V getValue()     { return val; }
    public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
    public final String toString(){ return key + "=" + val; }
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public final boolean equals(Object o) {
        Object k, v, u; Entry<?,?> e;
        return ((o instanceof Map.Entry) &&
                (k = (e = (Entry<?,?>)o).getKey()) != null &&
                (v = e.getValue()) != null &&
                (k == key || k.equals(key)) &&
                (v == (u = val) || v.equals(u)));
    }

    /**
       * Virtualized support for map.get(); overridden in subclasses.
       * 对map.get()的虚拟化支持;在子类覆盖。
       * h:待查找key的hash
       * k:待查找key
       */
    Node<K,V> find(int h, Object k) {
        //当前节点
        Node<K,V> e = this;
        //当如果当前节点不为空
        if (k != null) {
            //迭代节点内的元素，获取制定的键的值
            do {
                K ek;
                if (e.hash == h &&
                    ((ek = e.key) == k || (ek != null && k.equals(ek))))
                    return e;
            } while ((e = e.next) != null);
        }
        return null;
    }
}
```

## 四、initTable

这里涉及到的知识点：

1、`Thread.yield()`：使当前线程从执行状态（运行状态）变为可执行态（就绪状态）。

2、`U.compareAndSwapInt(this, SIZECTL, sc, -1)`和`SIZECTL = U.objectFieldOffset(k.getDeclaredField(“sizeCtl”))`：通过反射获取sizeCtl，再通过CAS设置其值。

3、`sc = n - (n >>> 2)`：其实就是 0.75 * n，扩容阈值。

这个方法并发通过对SIZECTL+CAS实现。

```java
/**
 * Initializes table, using the size recorded in sizeCtl.
 * 使用sizeCtl中记录的大小初始化表。
 */
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    //只要表为空，就一直循环
    while ((tab = table) == null || tab.length == 0) {
        //如果sizeCtl小于0，
        if ((sc = sizeCtl) < 0)
            //用了yield方法后，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            Thread.yield(); // lost initialization race; just spin 失去了初始化CPU竞争;只是自旋
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) { //如果SIZECTL与sc相同，则把SIZECTL设置为-1，即当前线程获取到了初始化的工作。
            try {
                //再次确认 表为空
                if ((tab = table) == null || tab.length == 0) {
                    //如果初始化size大于0，则n为sc,否则为16.
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    //创建n个Node数组
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    //table指向空数组
                    table = tab = nt;
                    // 如果 n 为 16 的话，那么这里 sc = 12
                    // 其实就是 0.75 * n
                    sc = n - (n >>> 2);
                }
            } finally {
                //sizeCtl重新定义新的值，用于扩容。
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```

## 五、源码翻译

```java
/**
 * A hash table supporting full concurrency of retrievals and
 * high expected concurrency for updates. This class obeys the
 * same functional specification as {@link Hashtable}, and
 * includes versions of methods corresponding to each method of
 * {@code Hashtable}. However, even though all operations are
 * thread-safe, retrieval operations do <em>not</em> entail locking,
 * and there is <em>not</em> any support for locking the entire table
 * in a way that prevents all access.  This class is fully
 * interoperable with {@code Hashtable} in programs that rely on its
 * thread safety but not on its synchronization details.
 *
 * 支持检索的完全并发性和更新的高期望并发性的哈希表。该类遵循与{@link Hashtable}相同的功能规范，
 * 并包含与{@code Hashtable}的每个方法对应的方法版本。
 * 然而，即使所有操作都是线程安全的，检索操作也不需要锁定，而且不支持以阻止所有访问的方式锁定整个表。
 * 在依赖线程安全而不依赖同步细节的程序中，该类完全可以与{@code Hashtable}互操作。
 *
 *
 * <p>Retrieval operations (including {@code get}) generally do not
 * block, so may overlap with update operations (including {@code put}
 * and {@code remove}). Retrievals reflect the results of the most
 * recently <em>completed</em> update operations holding upon their
 * onset. (More formally, an update operation for a given key bears a
 * <em>happens-before</em> relation with any (non-null) retrieval for
 * that key reporting the updated value.)  For aggregate operations
 * such as {@code putAll} and {@code clear}, concurrent retrievals may
 * reflect insertion or removal of only some entries.  Similarly,
 * Iterators, Spliterators and Enumerations return elements reflecting the
 * state of the hash table at some point at or since the creation of the
 * iterator/enumeration.  They do <em>not</em> throw {@link
 * java.util.ConcurrentModificationException ConcurrentModificationException}.
 * However, iterators are designed to be used by only one thread at a time.
 * Bear in mind that the results of aggregate status methods including
 * {@code size}, {@code isEmpty}, and {@code containsValue} are typically
 * useful only when a map is not undergoing concurrent updates in other threads.
 * Otherwise the results of these methods reflect transient states
 * that may be adequate for monitoring or estimation purposes, but not
 * for program control.
 *
 * 检索操作(包括{@code get})通常不会阻塞，因此可能与更新操作(包括{@code put}和{@code remove})重叠。
 * 检索反映最近完成的更新操作在开始时的结果。(更正式地说，给定键的更新操作与报告更新值的键的任何(非null)检索都具有happens-before关系。)
 * 对于诸如{@code putAll}和{@code clear}之类的聚合操作，并发检索可能只反映插入或删除某些条目。
 * 类似地，迭代器、Spliterators和枚举返回反映哈希表在迭代器/枚举创建时或创建后的状态的元素。
 * 它们不会抛出{@link java.util.ConcurrentModificationException ConcurrentModificationException}。但是，迭代器一次只能被一个线程使用。
 * 请记住，聚合状态方法(包括{@code size}、{@code isEmpty}和{@code containsValue})的结果通常只有在映射没有在其他线程中进行并发更新时才有用。
 * 否则，这些方法的结果反映的瞬态状态可能足以用于监测或估计目的，但不用于程序控制。
 *
 * <p>The table is dynamically expanded when there are too many
 * collisions (i.e., keys that have distinct hash codes but fall into
 * the same slot modulo the table size), with the expected average
 * effect of maintaining roughly two bins per mapping (corresponding
 * to a 0.75 load factor threshold for resizing). There may be much
 * variance around this average as mappings are added and removed, but
 * overall, this maintains a commonly accepted time/space tradeoff for
 * hash tables.  However, resizing this or any other kind of hash
 * table may be a relatively slow operation. When possible, it is a
 * good idea to provide a size estimate as an optional {@code
 * initialCapacity} constructor argument. An additional optional
 * {@code loadFactor} constructor argument provides a further means of
 * customizing initial table capacity by specifying the table density
 * to be used in calculating the amount of space to allocate for the
 * given number of elements.  Also, for compatibility with previous
 * versions of this class, constructors may optionally specify an
 * expected {@code concurrencyLevel} as an additional hint for
 * internal sizing.  Note that using many keys with exactly the same
 * {@code hashCode()} is a sure way to slow down performance of any
 * hash table. To ameliorate impact, when keys are {@link Comparable},
 * this class may use comparison order among keys to help break ties.
 *
 * 当冲突太多时，表会动态扩展，每个映射维护大约两个桶的平均预期效果(对应于调整大小的0.75负载因子阈值)
 * 随着映射的添加和删除，这个平均值周围可能会有很大的差异，但总的来说，这维护了哈希表的普遍接受的时间/空间权衡。
 * 然而，调整这个或任何其他类型的散列 table可能是一个相对较慢的操作。
 * 如果可能，最好提供一个大小估计作为一个可选的{@code initialCapacity}构造函数参数。
 * 另外一个可选的{@code loadFactor}构造函数参数提供了定制初始表容量的进一步方法，它指定了用于计算给定元素数量分配的空间量的表密度。
 * 此外，为了与该类的以前版本兼容，构造函数可以选择指定一个预期的{@code concurrencyLevel}作为内部分级的额外提示。
 * 注意，使用具有完全相同的{@code hashCode()}的许多键肯定会降低任何散列表的性能。
 * 为了改善影响，当键是{@link Comparable}时，该类可以使用键之间的比较顺序来帮助断开连接。
 *
 *
 * <p>A {@link Set} projection of a ConcurrentHashMap may be created
 * (using {@link #newKeySet()} or {@link #newKeySet(int)}), or viewed
 * (using {@link #keySet(Object)} when only keys are of interest, and the
 * mapped values are (perhaps transiently) not used or all take the
 * same mapping value.
 *
 * 可以创建ConcurrentHashMap的{@link Set}投影(使用{@link #newKeySet()}或{@link #newKeySet(int)})，
 * 也可以查看(使用{@link #keySet(Object)}，如果只对键感兴趣，并且映射的值(可能暂时)没有使用，或者全部使用相同的映射值。
 *
 *
 * <p>A ConcurrentHashMap can be used as scalable frequency map (a
 * form of histogram or multiset) by using {@link
 * java.util.concurrent.atomic.LongAdder} values and initializing via
 * {@link #computeIfAbsent computeIfAbsent}. For example, to add a count
 * to a {@code ConcurrentHashMap<String,LongAdder> freqs}, you can use
 * {@code freqs.computeIfAbsent(k -> new LongAdder()).increment();}
 *
 * ConcurrentHashMap可以用作可伸缩的频率映射（直方图或多集的一种形式）。
 *
 * <p>This class and its views and iterators implement all of the
 * <em>optional</em> methods of the {@link Map} and {@link Iterator}
 * interfaces.
 *  这个类，视图，迭代器都是实现了Map和Iterator接口。
 *
 * <p>Like {@link Hashtable} but unlike {@link HashMap}, this class
 * does <em>not</em> allow {@code null} to be used as a key or value.
 * 这个类不允许null键与null值。
 *
 * <p>ConcurrentHashMaps support a set of sequential and parallel bulk
 * operations that, unlike most {@link Stream} methods, are designed
 * to be safely, and often sensibly, applied even with maps that are
 * being concurrently updated by other threads; for example, when
 * computing a snapshot summary of the values in a shared registry.
 * There are three kinds of operation, each with four forms, accepting
 * functions with Keys, Values, Entries, and (Key, Value) arguments
 * and/or return values. Because the elements of a ConcurrentHashMap
 * are not ordered in any particular way, and may be processed in
 * different orders in different parallel executions, the correctness
 * of supplied functions should not depend on any ordering, or on any
 * other objects or values that may transiently change while
 * computation is in progress; and except for forEach actions, should
 * ideally be side-effect-free. Bulk operations on {@link Entry}
 * objects do not support method {@code setValue}.
 *
 * ConcurrentHashMaps支持一组顺序的和并行的批量操作，与大多数{@link Stream}方法不同，
 * 这些操作的设计是安全的，而且通常是明智的，甚至可以应用于由其他线程并发更新的映射;
 * 例如，在计算共享注册表中值的快照摘要时。有三种操作，每种都有四种形式，接受带有键、值、条目和(键、值)参数和/或返回值的函数。
 * 因为ConcurrentHashMap的元素不是命令在任何特定的方式,并可能在不同的订单处理并行执行的正确性提供功能不应该依赖于任何命令,
 * 或任何其他对象或值可能是暂时性的变化而计算正在进行中;除了每个动作，最好是没有副作用的。
 * 对象上的批量操作不支持方法{@code setValue}。
 *
 * <ul>
 * <li> forEach: Perform a given action on each element.
 * A variant form applies a given transformation on each element
 * before performing the action.</li>
 *
 * 对每个元素执行给定的操作。变体形式在执行操作之前对每个元素应用给定的转换。
 *
 * <li> search: Return the first available non-null result of
 * applying a given function on each element; skipping further
 * search when a result is found.</li>
 *
 * 搜索:返回对每个元素应用给定函数的第一个可用的非空结果;找到结果时跳过进一步的搜索。
 *
 * <li> reduce: Accumulate each element.  The supplied reduction
 * function cannot rely on ordering (more formally, it should be
 * both associative and commutative).  There are five variants:
 *
 * 减少:积累每个元素。所提供的约简函数不能依赖于排序(更正式地说，它应该是结合的和交换的)。有五种变体:
 *
 * <ul>
 *
 * <li> Plain reductions. (There is not a form of this method for
 * (key, value) function arguments since there is no corresponding
 * return type.)</li>
 *
 * 普通的减少。(对于(key, value)函数参数没有这种方法的形式，因为没有相应的返回类型。)
 *
 * <li> Mapped reductions that accumulate the results of a given
 * function applied to each element.</li>
 *
 * 将给定函数的结果累积到每个元素上的映射约简。
 *
 * <li> Reductions to scalar doubles, longs, and ints, using a
 * given basis value.</li>
 *
 * 使用给定的基值将其缩减为标量双精度、长精度和整数。
 *
 * </ul>
 * </li>
 * </ul>
 *
 * <p>These bulk operations accept a {@code parallelismThreshold}
 * argument. Methods proceed sequentially if the current map size is
 * estimated to be less than the given threshold. Using a value of
 * {@code Long.MAX_VALUE} suppresses all parallelism.  Using a value
 * of {@code 1} results in maximal parallelism by partitioning into
 * enough subtasks to fully utilize the {@link
 * ForkJoinPool#commonPool()} that is used for all parallel
 * computations. Normally, you would initially choose one of these
 * extreme values, and then measure performance of using in-between
 * values that trade off overhead versus throughput.
 *
 * 这些批量操作接受一个{@code parallel elismthreshold}参数。如果当前映射大小估计小于给定阈值，则按顺序执行。
 * 使用值{@code Long。MAX_VALUE}抑制所有并行。使用{@code 1}的值将划分为足够多的子任务，
 * 从而充分利用用于所有并行计算的{@link ForkJoinPool#commonPool()}，从而获得最大的并行性。
 * 通常，您首先会选择这些极值中的一个，然后度量使用介于开销和吞吐量之间的值的性能.
 *
 * <p>The concurrency properties of bulk operations follow
 * from those of ConcurrentHashMap: Any non-null result returned
 * from {@code get(key)} and related access methods bears a
 * happens-before relation with the associated insertion or
 * update.  The result of any bulk operation reflects the
 * composition of these per-element relations (but is not
 * necessarily atomic with respect to the map as a whole unless it
 * is somehow known to be quiescent).  Conversely, because keys
 * and values in the map are never null, null serves as a reliable
 * atomic indicator of the current lack of any result.  To
 * maintain this property, null serves as an implicit basis for
 * all non-scalar reduction operations. For the double, long, and
 * int versions, the basis should be one that, when combined with
 * any other value, returns that other value (more formally, it
 * should be the identity element for the reduction). Most common
 * reductions have these properties; for example, computing a sum
 * with basis 0 or a minimum with basis MAX_VALUE.
 *
 * 批量操作的并发性属性遵循ConcurrentHashMap的并发性属性:
 * 从{@code get(key)}和相关的访问方法返回的任何非null结果都与相关的插入或更新保持事前关系。
 * 任何批量操作的结果都反映了这些每个元素之间关系的组成(但对于整个映射来说，并不一定是原子关系，除非知道它是静态的)。
 * 相反，由于映射中的键和值从来都不是null，所以null可以作为当前缺少任何结果的可靠原子指示器。
 * 要维护此属性，null作为所有非标量约简操作的隐式基。
 * 对于double、long和int版本，基应该是与任何其他值组合时返回该其他值的基(更正式地说，它应该是还原的恒等元素)。
 * 大多数常见的约简具有这些性质;例如，计算以0为基底的和或以MAX_VALUE为基底的最小值。
 *
 * <p>Search and transformation functions provided as arguments
 * should similarly return null to indicate the lack of any result
 * (in which case it is not used). In the case of mapped
 * reductions, this also enables transformations to serve as
 * filters, returning null (or, in the case of primitive
 * specializations, the identity basis) if the element should not
 * be combined. You can create compound transformations and
 * filterings by composing them yourself under this "null means
 * there is nothing there now" rule before using them in search or
 * reduce operations.
 *
 * 作为参数提供的搜索和转换函数应该类似地返回null，以指示缺少任何结果(在这种情况下不使用它)。
 * 在映射约简的情况下，这还允许转换充当过滤器，如果元素不应该组合，则返回null(或者，在原始专门化的情况下，返回标识基)。
 * 您可以通过自己在这个null下组合它们来创建复合转换和筛选，这意味着在搜索或reduce操作中使用它们之前没有任何“规则”。
 *
 * <p>Methods accepting and/or returning Entry arguments maintain
 * key-value associations. They may be useful for example when
 * finding the key for the greatest value. Note that "plain" Entry
 * arguments can be supplied using {@code new
 * AbstractMap.SimpleEntry(k,v)}.
 *
 * 接受和/或返回条目参数的方法维护键值关联。
 * 例如，当查找值最大的键时，它们可能很有用。
 * 注意，可以使用{@code new AbstractMap.SimpleEntry(k,v)}提供“普通”条目参数。
 *
 * <p>Bulk operations may complete abruptly, throwing an
 * exception encountered in the application of a supplied
 * function. Bear in mind when handling such exceptions that other
 * concurrently executing functions could also have thrown
 * exceptions, or would have done so if the first exception had
 * not occurred.
 *
 * 批量操作可能会突然完成，从而引发在所提供的函数的应用程序中遇到的异常。
 * 在处理此类异常时，请记住，其他并发执行的函数也可能抛出异常,或者，如果第一个异常没有发生，就会这样做。
 *
 * <p>Speedups for parallel compared to sequential forms are common
 * but not guaranteed.  Parallel operations involving brief functions
 * on small maps may execute more slowly than sequential forms if the
 * underlying work to parallelize the computation is more expensive
 * than the computation itself.  Similarly, parallelization may not
 * lead to much actual parallelism if all processors are busy
 * performing unrelated tasks.
 *
 * 与顺序形式相比，并行形式的加速比较常见，但不能保证。
 * 如果并行计算的底层工作比计算本身更昂贵，那么涉及小映射上的简短函数的并行操作执行起来可能比顺序形式慢。
 * 类似地，如果所有处理器都忙于执行不相关的任务，并行化可能不会导致太多实际的并行。
 *
 * <p>All arguments to all task methods must be non-null.
 *
 * 所有任务方法的所有参数必须是非空的。
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * 该类是Java集合框架的成员
 *
 * @since 1.5
 * @author Doug Lea
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */

public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable {

    /**
     * The largest possible table capacity.  This value must be
     * exactly 1<<30 to stay within Java array allocation and indexing
     * bounds for power of two table sizes, and is further required
     * because the top two bits of 32bit hash fields are used for
     * control purposes.
     * <p>
     * 最大的表容量。这个值必须恰好是1<<30，才能保持在Java数组分配和索引范围内，
     * 以获得两种表大小的幂，而且这个值是进一步需要的，因为32位哈希字段的前两位用于控制目的。
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The default initial table capacity.  Must be a power of 2
     * (i.e., at least 1) and at most MAXIMUM_CAPACITY.
     * 默认的表初始容量，必须是2的幂次方，最小是1，最大为MAXIMUM_CAPACITY
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * The largest possible (non-power of two) array size.
     * Needed by toArray and related methods.
     * 最大的数组大小(非2次幂)。toArray and related方法使用。
     */
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The default concurrency level for this table. Unused but
     * defined for compatibility with previous versions of this class.
     * 表默认的并发级别，未使用，但为与该类的以前版本兼容而定义。
     */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * The load factor for this table. Overrides of this value in
     * constructors affect only the initial table capacity.  The
     * actual floating point value isn't normally used -- it is
     * simpler to use expressions such as {@code n - (n >>> 2)} for
     * the associated resizing threshold.
     * 表的默认加载因子，在构造函数中重写此值只影响初始表容量。
     * 通常不使用实际的浮点值 —— 对于相关的调整阈值，使用{@code n - (n >>> 2)}等表达式更简单。
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2, and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     * 使用树(而不是列表)来设置bin计数阈值。当向至少具有这么多节点的bin添加元素时，bin将转换为树。
     * 该值必须大于2，并且应该至少为8，以便与树移除中关于收缩后转换回普通Bin的假设相吻合。
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     * 用于在调整大小操作期间反树化(拆分)bin的bin计数阈值，应该小于TREEIFY_THRESHOLD，
     * 最多6个，去吻合去缩检测。
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * The value should be at least 4 * TREEIFY_THRESHOLD to avoid
     * conflicts between resizing and treeification thresholds.
     * 最小的表容量，其中的箱子可以treeified。(否则，如果一个bin中有太多节点，则会调整表的大小。)
     * 该值应该至少为4 * TREEIFY_THRESHOLD，以避免调整大小和treeification阈值之间的冲突。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * Minimum number of rebinnings per transfer step. Ranges are
     * subdivided to allow multiple resizer threads.  This value
     * serves as a lower bound to avoid resizers encountering
     * excessive memory contention.  The value should be at least
     * DEFAULT_CAPACITY.
     * 每个转移步骤的最少复归数。范围被细分以允许多个调整大小的线程。
     * 此值用作下限，以避免调整大小器遇到过多的内存争用。
     * 该值至少应该是DEFAULT_CAPACITY。
     */
    private static final int MIN_TRANSFER_STRIDE = 16;

    /**
     * The number of bits used for generation stamp in sizeCtl.
     * Must be at least 6 for 32bit arrays.
     * 用于生成戳记的位的数目，单位为sizeCtl。
     * 32位数组必须至少为6。
     */
    private static int RESIZE_STAMP_BITS = 16;

    /**
     * The maximum number of threads that can help resize.
     * Must fit in 32 - RESIZE_STAMP_BITS bits.
     * 可以帮助调整大小的最大线程数。
     * 必须符合32-RESIZE_STAMP_BITS bits
     */
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    /**
     * The bit shift for recording size stamp in sizeCtl.
     * 用sizeCtl记录尺寸戳的位偏移。
     */
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    /*
    * Encodings for Node hash fields. See above for explanation.
    * 节点哈希字段的编码。见解释。
    */
    static final int MOVED = -1; // hash for forwarding nodes 转发节点的哈希
    static final int TREEBIN = -2; // hash for roots of trees 树根的哈希
    static final int RESERVED = -3; // hash for transient reservations 临时保留的哈希
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash 普通节点哈希的可用位

    /**
     * Number of CPUS, to place bounds on some sizings
     * cpu的数量，以限制某些大小
     */
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * For serialization compatibility.
     * 序列化的兼容性
     */
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("segments", Segment[].class),
            new ObjectStreamField("segmentMask", Integer.TYPE),
            new ObjectStreamField("segmentShift", Integer.TYPE)
    };

    /* ---------------- Nodes -------------- */

    /**
     * Key-value entry.  This class is never exported out as a
     * user-mutable Map.Entry (i.e., one supporting setValue; see
     * MapEntry below), but can be used for read-only traversals used
     * in bulk tasks.  Subclasses of Node with a negative hash field
     * are special, and contain null keys and values (but are never
     * exported).  Otherwise, keys and vals are never null.
     * 键值项。这个类永远不会作为用户可变的Map.Entry导出，但是可以用于批量任务中使用的只读遍历。
     * 具有负哈希字段的Node的子类是特殊的，包含空键和值(但从不导出)。否则，键和val永远不会为空。
     */
    static class Node<K, V> implements Entry<K, V> {
        //哈希码
        final int hash;
        //键
        final K key;
        //值
        volatile V val;
        //下一个节点
        volatile Node<K, V> next;

        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return val;
        }

        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public final String toString() {
            return key + "=" + val;
        }

        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u;
            Entry<?, ?> e;
            return ((o instanceof Map.Entry) &&
                    (k = (e = (Entry<?, ?>) o).getKey()) != null &&
                    (v = e.getValue()) != null &&
                    (k == key || k.equals(key)) &&
                    (v == (u = val) || v.equals(u)));
        }

        /**
         * Virtualized support for map.get(); overridden in subclasses.
         * 对map.get()的虚拟化支持;在子类覆盖。
         * h:待查找key的hash
         * k:待查找key
         */
        Node<K, V> find(int h, Object k) {
            //当前节点
            Node<K, V> e = this;
            //当如果当前节点不为空
            if (k != null) {
                //迭代节点内的元素，获取制定的键的值
                do {
                    K ek;
                    if (e.hash == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    /* ---------------- Static utilities 静态工具 -------------- */

    /**
     * Spreads (XORs) higher bits of hash to lower and also forces top
     * bit to 0. Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     * <p>
     * 将较高的哈希值扩展(XORs)为较低的哈希值，并将最高位强制为0。
     * 由于该表使用了2的幂掩码，因此仅在当前掩码之上以位为单位变化的散列集总是会发生冲突。
     * (已知的例子包括在小表中保存连续整数的浮点键集。)因此，我们应用一个转换，将更高位的影响向下传播。
     * 位扩展的速度、实用性和质量之间存在权衡。
     * 因为许多常见的散列集已经合理分布(所以不要受益于传播),在桶中我们用树来处理大型的碰撞,
     * 我们只是XOR一些改变以最划算的方式来减少系统lossage,以及最高位的影响,否则就不会因为表的边界而在索引计算中使用。
     */
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    /**
     * Returns a power of two table size for the given desired capacity.
     * See Hackers Delight, sec 3.2
     * 返回给定目标容量的2次幂
     */
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Returns x's Class if it is of the form "class C implements
     * Comparable<C>", else null.
     * 如果x实现了Comparable<C>接口，则返回此Class,否则返回null
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = (ParameterizedType) t).getRawType() ==
                                    Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     * 比较大小
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable) k).compareTo(x));
    }

    /* ---------------- Table element access 表元素访问，核心了-------------- */

    /*
    * Volatile access methods are used for table elements as well as
    * elements of in-progress next table while resizing.  All uses of
    * the tab arguments must be null checked by callers.  All callers
    * also paranoically precheck that tab's length is not zero (or an
    * equivalent check), thus ensuring that any index argument taking
    * the form of a hash value anded with (length - 1) is a valid
    * index.  Note that, to be correct wrt arbitrary concurrency
    * errors by users, these checks must operate on local variables,
    * which accounts for some odd-looking inline assignments below.
    * Note that calls to setTabAt always occur within locked regions,
    * and so in principle require only release ordering, not
    * full volatile semantics, but are currently coded as volatile
    * writes to be conservative.
    * 在调整大小时，易失性访问方法用于表元素以及正在进行中的下一个表的元素。
    * 调用者必须检查选项卡参数的所有使用是否为空。
    * 所有调用者还偏执地预先检查制表符的长度是否为零(或等效的检查)，从而确保任何以散列值形式出现的索引参数(长度- 1)都是有效的索引。
    * 注意，要纠正用户的wrt任意并发性错误，这些检查必须对本地变量进行操作，这就解释了下面一些奇怪的内联分配。
    * 注意，对setTabAt的调用总是发生在锁定区域内，
    * 因此原则上只需要释放顺序，而不需要完全的volatile语义，但是目前将其编码为volatile写，以保持保守性。
    */

    @SuppressWarnings("unchecked")
    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        //native 方法 获取当前节点
        return (Node<K, V>) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i,
                                        Node<K, V> c, Node<K, V> v) {
        //native 方法 cas 获取tab数组中索引为i的节点，如果是和c相同，则更新为节点v
        return U.compareAndSwapObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        // native 方法 更新节点
        U.putObjectVolatile(tab, ((long) i << ASHIFT) + ABASE, v);
    }

    /* ---------------- Fields 字段-------------- */

    /**
     * The array of bins. Lazily initialized upon first insertion.
     * Size is always a power of two. Accessed directly by iterators.
     * 桶的数组。在第一次插入时惰性初始化。大小总是2的幂。由迭代器直接访问。
     */
    transient volatile Node<K, V>[] table;

    /**
     * The next table to use; non-null only while resizing.
     * 要使用的下一个表;仅在调整大小时非空。
     */
    private transient volatile Node<K, V>[] nextTable;

    /**
     * Base counter value, used mainly when there is no contention,
     * but also as a fallback during table initialization
     * races. Updated via CAS.
     * 基本计数器值，主要在没有争用时使用，也可作为表初始化竞争期间的回退。通过CAS更新。
     */
    private transient volatile long baseCount;

    /**
     * Table initialization and resizing control.  When negative, the
     * table is being initialized or resized: -1 for initialization,
     * else -(1 + the number of active resizing threads).  Otherwise,
     * when table is null, holds the initial table size to use upon
     * creation, or 0 for default. After initialization, holds the
     * next element count value upon which to resize the table.
     * 表初始化和调整大小控制。当为负数时，表被初始化或调整大小:初始化为-1（1 +活动调整大小的线程的数量）
     * 否则，当表为空时，保留创建时使用的初始表大小，默认情况下为0。
     * 初始化之后，保存下一个元素count值，根据该值调整表的大小。
     * <p>
     * -1 :代表table正在初始化,其他线程应该交出CPU时间片
     * <p>
     * -N: 表示正有N-1个线程执行扩容操作（高 16 位是 length 生成的标识符，低 16 位是扩容的线程数）
     * <p>
     * 大于 0: 如果table已经初始化,代表table容量,默认为table大小的0.75,如果还未初始化,代表需要初始化的大小
     */
    private transient volatile int sizeCtl;

    /**
     * The next table index (plus one) to split while resizing.
     * 调整大小时要分割的下一个表索引(加上一个)。
     */
    private transient volatile int transferIndex;

    /**
     * Spinlock (locked via CAS) used when resizing and/or creating CounterCells.
     * 自旋锁(通过CAS锁定)，用于调整大小and/or 创建CounterCells。
     * cellsBusy是一个只有0和1两个状态的volatile整数
     * 它被当做一个自旋锁，0代表无锁，1代表加锁
     */
    private transient volatile int cellsBusy;

    /**
     * Table of counter cells. When non-null, size is a power of 2.
     * 计数器单元格表。当非空时，size是2的幂。
     */
    private transient volatile CounterCell[] counterCells;



    /* ---------------- Public operations -------------- */

    /**
     * Creates a new, empty map with the default initial table size (16).
     * 创建一个默认初始化容量为16的新的空的表
     */
    public ConcurrentHashMap() {
    }

    /**
     * Creates a new, empty map with an initial table size
     * accommodating the specified number of elements without the need
     * to dynamically resize.
     * 创建一个新的空映射，初始表大小可容纳指定数量的元素，而不需要动态调整大小。
     *
     * @param initialCapacity The implementation performs internal
     *                        sizing to accommodate this many elements. 初始容量
     * @throws IllegalArgumentException if the initial capacity of
     *                                  elements is negative
     */
    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        //如果初试容量大于最大初始容量无符号右移1位，则cap取值为最大初始容量，否则计算给定目标容量的最小2次幂的值
        int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ?
                MAXIMUM_CAPACITY :
                tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
        //将表初始和扩容控制值 赋值
        this.sizeCtl = cap;
    }

    /**
     * Creates a new map with the same mappings as the given map.
     * 创建与给定映射具有相同映射的新映射。
     *
     * @param m the map
     */
    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        //默认表容量为16
        this.sizeCtl = DEFAULT_CAPACITY;
        //批量插入
        putAll(m);
    }

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}) and
     * initial table density ({@code loadFactor}).
     * 用给定的初始容量和加载因子创建一个新的空的map，一个并发更新线程。
     *
     * @param initialCapacity the initial capacity. The implementation
     *                        performs internal sizing to accommodate this many elements,
     *                        given the specified load factor.  初始容量
     * @param loadFactor      the load factor (table density) for
     *                        establishing the initial table size 加载因子
     * @throws IllegalArgumentException if the initial capacity of
     *                                  elements is negative or the load factor is nonpositive
     * @since 1.6
     */
    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    /**
     * Creates a new, empty map with an initial table size based on
     * the given number of elements ({@code initialCapacity}), table
     * density ({@code loadFactor}), and number of concurrently
     * updating threads ({@code concurrencyLevel}).
     * 根据给定的元素数量({@code initialCapacity})、表密度({@code loadFactor})和并发更新线程数量({@code concurrencyLevel})创建一个新的空映射，初始表大小。
     *
     * @param initialCapacity  the initial capacity. The implementation
     *                         performs internal sizing to accommodate this many elements,
     *                         given the specified load factor. 初始容量
     * @param loadFactor       the load factor (table density) for
     *                         establishing the initial table size 加载因子
     * @param concurrencyLevel the estimated number of concurrently
     *                         updating threads. The implementation may use this value as
     *                         a sizing hint. 并发等级
     * @throws IllegalArgumentException if the initial capacity is
     *                                  negative or the load factor or concurrencyLevel are
     *                                  nonpositive
     */
    public ConcurrentHashMap(int initialCapacity,
                            float loadFactor, int concurrencyLevel) {
        //入参校验
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();

        //如果初始容量小于并发等级，则初始容量变为并发等级大小
        if (initialCapacity < concurrencyLevel)   // Use at least as many bins
            initialCapacity = concurrencyLevel;   // as estimated threads
        //计算表容量
        long size = (long) (1.0 + (long) initialCapacity / loadFactor);
        //如果表容量大于等于最大容量，则取最大容量值，否则取size值的最小2次幂的值
        int cap = (size >= (long) MAXIMUM_CAPACITY) ?
                MAXIMUM_CAPACITY : tableSizeFor((int) size);
        //赋值 sizeCtl
        this.sizeCtl = cap;
    }

    // Original (since JDK1.2) Map methods
    // 原始的map方法

    /**
     * 计算容量
     * {@inheritDoc}
     */
    public int size() {
        long n = sumCount();
        return ((n < 0L) ? 0 :
                (n > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE :
                        (int) n);
    }

    /**
     * 判断map是否为空
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return sumCount() <= 0L; // ignore transient negative values 忽略瞬时负值
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p>
     * 返回指定键映射到的值，如果此映射不包含键的映射，则返回{@code null}。
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key.equals(k)},
     * then this method returns {@code v}; otherwise it returns
     * {@code null}.  (There can be at most one such mapping.)
     * <p>
     * 更正式地说，如果这个映射包含一个键{@code k}到一个值{@code v}的映射，
     * 使得{@code key.equals(k)}，那么这个方法返回{@code v};否则返回{@code null}。
     * (最多可以有一个这样的映射。)
     *
     * @throws NullPointerException if the specified key is null 如果指定的键为空
     */
    public V get(Object key) {
        Node<K, V>[] tab;
        Node<K, V> e, p;
        int n, eh;
        K ek;
        //扩展key的Hash值
        int h = spread(key.hashCode());
        //如果表格不为空且表格长度大于0且所查key的所在节点不为空，(n - 1) & h相当于取模操作，即获取其索引位置。
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (e = tabAt(tab, (n - 1) & h)) != null) {
            //如果获取到当前值
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    //返回当前节点值
                    return e.val;
            }
            //如果当前节点hash值小于0，溢出的时候为负数
            else if (eh < 0)
                //调用find方法，返回值，或者没找到则返回空
                return (p = e.find(h, key)) != null ? p.val : null;
            //获取当前节点的下一个节点，如果非空，则判断是否相同，找到了即返回其值。
            while ((e = e.next) != null) {
                if (e.hash == h &&
                        ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    /**
     * Tests if the specified object is a key in this table.
     * 测试指定的对象是否是该表中的键。
     *
     * @param key possible key 指定的键
     * @return {@code true} if and only if the specified object
     * is a key in this table, as determined by the
     * {@code equals} method; {@code false} otherwise
     * @throws NullPointerException if the specified key is null
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value. Note: This method may require a full traversal
     * of the map, and is much slower than method {@code containsKey}.
     * <p>
     * 如果此映射将一个或多个键映射到指定值，则返回{@code true}。
     * 注意:这个方法可能需要遍历整个map，并且比方法{@code containsKey}慢得多。
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the
     * specified value
     * @throws NullPointerException if the specified value is null
     */
    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();
        Node<K, V>[] t;
        if ((t = table) != null) {

            //表遍历者,这个好消耗性能
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                V v;
                //存在此值，返回true
                if ((v = p.val) == value || (v != null && value.equals(v)))
                    return true;
            }
        }
        return false;
    }

    /**
     * Maps the specified key to the specified value in this table.
     * Neither the key nor the value can be null.
     * <p>
     * 将指定的键映射到该表中的指定值。键和值都不能为空。
     *
     * <p>The value can be retrieved by calling the {@code get} method
     * with a key that is equal to the original key.
     * <p>
     * 可以通过调用{@code get}方法来检索该值，方法的键值等于原始键值。
     *
     * @param key   key with which the specified value is to be associated 要与指定值关联的键
     * @param value value to be associated with the specified key 值与指定的键关联
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}
     * @throws NullPointerException if the specified key or value is null
     */
    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /**
     * Implementation for put and putIfAbsent
     * 实现put和putIfAbsent
     * onlyIfAbsent含义：如果我们传入的key已存在我们是否去替换，true:不替换，false：替换。
     */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        //键值都不为空
        if (key == null || value == null) throw new NullPointerException();
        //发散键的hash值
        int hash = spread(key.hashCode());
        //桶数量 0
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            //检查表是否初始化
            if (tab == null || (n = tab.length) == 0)
                //初始化表
                tab = initTable();
                //检查指定键所在节点是否为空
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                //通过CAS方法添加键值对
                if (casTabAt(tab, i, null,
                        new Node<K, V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin 添加到空桶时没有锁
            }
            //如果当前节点的Hash值为MOVED
            else if ((fh = f.hash) == MOVED)
                //如果还在进行扩容操作就先进行扩容
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                //synchronized锁定此f节点
                synchronized (f) {
                    //再次检测节点是否相同
                    if (tabAt(tab, i) == f) {
                        //如果此节点hash值大于0
                        if (fh >= 0) {
                            //桶数量为1
                            binCount = 1;
                            for (Node<K, V> e = f; ; ++binCount) {
                                K ek;
                                //获取到指定的键
                                if (e.hash == hash &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    //保存老的值
                                    oldVal = e.val;
                                    //onlyIfAbsent：如果我们传入的key已存在我们是否去替换，true:不替换，false：替换。
                                    if (!onlyIfAbsent)
                                        //替换掉
                                        e.val = value;
                                    break;
                                }
                                Node<K, V> pred = e;
                                //下一个节点为空
                                if ((e = e.next) == null) {
                                    //新建节点
                                    pred.next = new Node<K, V>(hash, key,
                                            value, null);
                                    break;
                                }
                            }
                        }
                        //如果时树节点
                        else if (f instanceof TreeBin) {
                            Node<K, V> p;
                            //桶数量为2
                            binCount = 2;
                            //找到插入的位置，插入节点，平衡插入
                            if ((p = ((TreeBin<K, V>) f).putTreeVal(hash, key,
                                    value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                //如果桶数量不为0
                if (binCount != 0) {
                    //如果桶数量大于树化阈值
                    if (binCount >= TREEIFY_THRESHOLD)
                        //将链表转为树
                        treeifyBin(tab, i);
                    //如果老的值存在，则返回
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        //增加数量
        addCount(1L, binCount);
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this one.
     * These mappings replace any mappings that this map had for any of the
     * keys currently in the specified map.
     * <p>
     * 将指定映射的所有映射复制到此映射，这些映射替换了当前指定映射中任意键的映射。
     *
     * @param m mappings to be stored in this map
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        //尝试调整表的大小以适应给定的元素数量。
        tryPresize(m.size());
        //迭代插入
        for (Entry<? extends K, ? extends V> e : m.entrySet())
            putVal(e.getKey(), e.getValue(), false);
    }

    /**
     * Removes the key (and its corresponding value) from this map.
     * This method does nothing if the key is not in the map.
     * <p>
     * 从映射中删除键(及其对应值)。如果键不在映射中，则此方法不执行任何操作。
     *
     * @param key the key that needs to be removed 指定移除的键
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key} 如果没有针对{@code key}的映射，则与{@code key}关联的前一个值或{@code null}
     * @throws NullPointerException if the specified key is null 如果指定的键为空
     */
    public V remove(Object key) {
        return replaceNode(key, null, null);
    }

    /**
     * Implementation for the four public remove/replace methods:
     * Replaces node value with v, conditional upon match of cv if
     * non-null.  If resulting value is null, delete.
     * 删除/替换的操作
     */
    final V replaceNode(Object key, V value, Object cv) {
        //发散hash
        int hash = spread(key.hashCode());
        //迭代表
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            //如果表为空，且键所在节点为空，跳出循环
            if (tab == null || (n = tab.length) == 0 ||
                    (f = tabAt(tab, i = (n - 1) & hash)) == null)
                break;
                //如果key所在节点的hash值为MOVED
            else if ((fh = f.hash) == MOVED)
                //如果还在进行扩容操作就先进行扩容
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                //是否检验
                boolean validated = false;
                //锁当前节点
                synchronized (f) {
                    //确认节点
                    if (tabAt(tab, i) == f) {
                        //节点的hash值大于0
                        if (fh >= 0) {
                            //校验
                            validated = true;
                            for (Node<K, V> e = f, pred = null; ; ) {
                                K ek;
                                //找到键位置
                                if (e.hash == hash &&
                                        ((ek = e.key) == key ||
                                                (ek != null && key.equals(ek)))) {
                                    //老的值
                                    V ev = e.val;
                                    //删除节点
                                    if (cv == null || cv == ev ||
                                            (ev != null && cv.equals(ev))) {
                                        oldVal = ev;
                                        if (value != null)
                                            e.val = value;
                                        else if (pred != null)
                                            pred.next = e.next;
                                        else
                                            setTabAt(tab, i, e.next);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)
                                    break;
                            }
                        }
                        //是树形节点
                        else if (f instanceof TreeBin) {
                            validated = true;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r, p;
                            //找到删除的key
                            if ((r = t.root) != null &&
                                    (p = r.findTreeNode(hash, key, null)) != null) {
                                V pv = p.val;
                                if (cv == null || cv == pv ||
                                        (pv != null && cv.equals(pv))) {
                                    oldVal = pv;
                                    if (value != null)
                                        p.val = value;
                                        //删除树节点
                                    else if (t.removeTreeNode(p))
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                //检查
                if (validated) {
                    if (oldVal != null) {
                        if (value == null)
                            //数量减一
                            addCount(-1L, -1);
                        return oldVal;
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Removes all of the mappings from this map.
     * 清空map
     */
    public void clear() {
        long delta = 0L; // negative number of deletions 删除次数为负数
        int i = 0;
        Node<K, V>[] tab = table;
        while (tab != null && i < tab.length) {
            int fh;
            //获取节点
            Node<K, V> f = tabAt(tab, i);
            if (f == null)
                ++i;
                //转发节点的哈希
            else if ((fh = f.hash) == MOVED) {
                //如果还在进行扩容操作就先进行扩容
                tab = helpTransfer(tab, f);
                i = 0; // restart
            } else {
                //锁定当前节点
                synchronized (f) {
                    //确认节点
                    if (tabAt(tab, i) == f) {
                        //获取节点，包括树节点
                        Node<K, V> p = (fh >= 0 ? f :
                                (f instanceof TreeBin) ?
                                        ((TreeBin<K, V>) f).first : null);
                        while (p != null) {
                            //数量减一
                            --delta;
                            //下一个节点
                            p = p.next;
                        }
                        //置空
                        setTabAt(tab, i++, null);
                    }
                }
            }
        }
        if (delta != 0L)
            //计数
            addCount(delta, -1);
    }


    /**
     * Returns the hash code value for this {@link Map}, i.e.,
     * the sum of, for each key-value pair in the map,
     * {@code key.hashCode() ^ value.hashCode()}.
     * <p>
     * 返回这个{@link Map}的哈希码值，即，对于映射中的每个键值对，{@code key.hashCode() ^ value.hashCode()}的和。
     *
     * @return the hash code value for this map
     */
    public int hashCode() {
        int h = 0;
        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; )
                //取键的Hash值与值的Hash值的异或 的 和
                h += p.key.hashCode() ^ p.val.hashCode();
        }
        return h;
    }


    /**
     * Compares the specified object with this map for equality.
     * Returns {@code true} if the given object is a map with the same
     * mappings as this map.  This operation may return misleading
     * results if either map is concurrently modified during execution
     * of this method.
     * <p>
     * 重写的equals方法
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    public boolean equals(Object o) {
        //如果栈中值不相等
        if (o != this) {
            //如果o未实现Map接口，则不相等
            if (!(o instanceof Map))
                return false;
            Map<?, ?> m = (Map<?, ?>) o;
            Node<K, V>[] t;
            int f = (t = table) == null ? 0 : t.length;
            Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
            //迭代，只要有一个值不相等，则返回false
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                V val = p.val;
                Object v = m.get(p.key);
                if (v == null || (v != val && !v.equals(val)))
                    return false;
            }
            //迭代视图
            for (Entry<?, ?> e : m.entrySet()) {
                Object mk, mv, v;
                if ((mk = e.getKey()) == null ||
                        (mv = e.getValue()) == null ||
                        (v = get(mk)) == null ||
                        (mv != v && !mv.equals(v)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Stripped-down version of helper class used in previous version,
     * declared for the sake of serialization compatibility
     * <p>
     * 以前版本中使用的helper类的简化版本，为了序列化兼容性而声明
     * 继承自重入锁
     */
    static class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }


    
    // Overrides of JDK8+ Map extension method defaults

    /**
     * Returns the value to which the specified key is mapped, or the
     * given default value if this map contains no mapping for the
     * key.
     * 返回指定键映射到的值，如果此映射不包含键的映射，则返回给定的默认值。
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the value to return if this map contains
     *                     no mapping for the given key
     * @return the mapping for the key, if present; else the default value
     * @throws NullPointerException if the specified key is null
     */
    public V getOrDefault(Object key, V defaultValue) {
        V v;
        return (v = get(key)) == null ? defaultValue : v;
    }



        /* ---------------- Special Nodes -------------- */

    /**
     * A node inserted at head of bins during transfer operations.
     * 在迭代操作期间插入到桶头的节点。
     */
    static final class ForwardingNode<K, V> extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K, V> find(int h, Object k) {
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer:
            for (Node<K, V>[] tab = nextTable; ; ) {
                Node<K, V> e;
                int n;
                if (k == null || tab == null || (n = tab.length) == 0 ||
                        (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (; ; ) {
                    int eh;
                    K ek;
                    if ((eh = e.hash) == h &&
                            ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K, V>) e).nextTable;
                            continue outer;
                        } else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }

  

    /* ---------------- Table Initialization and Resizing -------------- */

    /**
     * Returns the stamp bits for resizing a table of size n.
     * Must be negative when shifted left by RESIZE_STAMP_SHIFT.
     * 返回用于调整大小为n的表的戳记位，
     * 当通过RESIZE_STAMP_SHIFT左移时，必须为负。
     * 该函数返回一个用于数据校验的标志位，意思是对长度为n的table进行扩容。
     * 它将n的前导零（最高有效位之前的零的数量）和1 << 15做或运算，这时低16位的最高位为1，其他都为n的前导零。
     */
    static final int resizeStamp(int n) {
        //numberOfLeadingZeros方法的作用是返回无符号整型i的最高非零位前面的0的个数，包括符号位在内；
        //如果i为负数，这个方法将会返回0，符号位为1.
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    /**
     * Initializes table, using the size recorded in sizeCtl.
     * 使用sizeCtl中记录的大小初始化表。
     */
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        //只要表为空，就一直循环
        while ((tab = table) == null || tab.length == 0) {
            //如果sizeCtl小于0，
            if ((sc = sizeCtl) < 0)
                //用了yield方法后，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
                Thread.yield(); // lost initialization race; just spin 失去了初始化CPU竞争;只是自旋
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) { //如果SIZECTL与sc相同，则把SIZECTL设置为-1，即当前线程获取到了初始化的工作。
                try {
                    //再次确认 表为空
                    if ((tab = table) == null || tab.length == 0) {
                        //如果初始化size大于0，则n为sc,否则为16.
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        //创建n个Node数组
                        @SuppressWarnings("unchecked")
                        Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
                        //table指向空数组
                        table = tab = nt;
                        // 如果 n 为 16 的话，那么这里 sc = 12
                        // 其实就是 0.75 * n
                        sc = n - (n >>> 2);
                    }
                } finally {
                    //sizeCtl重新定义新的值，用于扩容。
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    /**
     * Adds to count, and if table is too small and not already
     * resizing, initiates transfer. If already resizing, helps
     * perform transfer if work is available.  Rechecks occupancy
     * after a transfer to see if another resize is already needed
     * because resizings are lagging additions.
     * <p>
     * 添加到count，如果表太小且尚未调整大小，则启动传输。
     * 如果已经调整大小，则在工作可用时帮助执行传输。
     * 在转移后重新检查占用情况，看看是否已经需要另一个大小调整，因为大小调整是滞后的添加。
     *
     * @param x     the count to add 添加的数量
     * @param check if <0, don't check resize, if <= 1 only check if uncontended 如果<0，不检查调整大小，如果<= 1，只检查是否无竞争
     */
    private final void addCount(long x, int check) {
        CounterCell[] as;
        long b, s;
        //如果当前计数表格中不为空 或者 设置BASECOUNT=baseCount+x 失败(存在其他线程再操作这个baseCount),
        // 即，尝试使用CAS更新baseCount失败就转用CounterCells进行更新
        if ((as = counterCells) != null ||
                !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            CounterCell a;
            long v;
            int m;
            boolean uncontended = true;
            //如果表格为空 或者 表格中探测值为空 或者 设置CELLVALUE为 探测值的a.value+x 失败（很倒霉，这个值也设置失败了）
            //即，尝试使用CAS更新当前线程的counterCell失败就转用fullAddCount进行更新
            if (as == null || (m = as.length - 1) < 0 ||
                    (a = as[ThreadLocalRandom.getProbe() & m]) == null ||
                    !(uncontended =
                            U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                //全部尝试一遍 没有人争的
                fullAddCount(x, uncontended);
                return;
            }
            //if <= 1 only check if uncontended
            if (check <= 1)
                return;
            //计数
            s = sumCount();
        }
        //检查，判断是否需要扩容
        if (check >= 0) {
            Node<K, V>[] tab, nt;
            int n, sc;
            //当sizeCtl小于总数量 且 table不为空 且 tabled的长度不为最大容量，进入循环
            while (s >= (long) (sc = sizeCtl) && (tab = table) != null &&
                    (n = tab.length) < MAXIMUM_CAPACITY) {
                //计算扩容标志
                int rs = resizeStamp(n);
                //表未初始化
                if (sc < 0) {
                    //如果sc右移16位不等于rs 或者 sc等于rs+1 或者 sc等于rs+最大扩容线程数 或者 nextTable未空 或者  transferIndex<=0
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                            sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                            transferIndex <= 0)
                        break;
                    //如果SIZECTL与sc相同，则将sc的值+1 设置成功的话，
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        //转移数据
                        transfer(tab, nt);

                    //如果设置SIZECTL为(rs << RESIZE_STAMP_SHIFT) + 2成功
                } else if (U.compareAndSwapInt(this, SIZECTL, sc,
                        (rs << RESIZE_STAMP_SHIFT) + 2))
                    //转移数据
                    transfer(tab, null);
                //计数
                s = sumCount();
            }
        }
    }

    /**
     * Helps transfer if a resize is in progress.
     * 如果还在进行扩容操作就先进行扩容
     */
    final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        Node<K, V>[] nextTab;
        int sc;
        //如果表非空 且 当前节点是ForwardingNode节点 且 nextTable不为空
        if (tab != null && (f instanceof ForwardingNode) &&
                (nextTab = ((ForwardingNode<K, V>) f).nextTable) != null) {
            //返回的是对 tab.length 的一个数据校验标识，占 16 位。而 RESIZE_STAMP_SHIFT 的值为 16，那么位运算后，整个表达式必然在右边空出 16 个零。
            //也正如我们所说的，sizeCtl 的高 16 位为数据校验标识，低 16 为表示正在进行扩容的线程数量。
            int rs = resizeStamp(tab.length);
            // 如果 nextTab 没有被并发修改 且 tab 也没有被并发修改
            // 且 sizeCtl  < 0 （说明还在扩容）
            while (nextTab == nextTable && table == tab &&
                    (sc = sizeCtl) < 0) {
                // 如果 sizeCtl 无符号右移  16 不等于 rs （ sc前 16 位如果不等于标识符，则标识符变化了）
                // 或者 sizeCtl == rs + 1  （扩容结束了，不再有线程进行扩容）（默认第一个线程设置 sc ==rs 左移 16 位 + 2，当第一个线程结束扩容了，就会将 sc 减一。这个时候，sc 就等于 rs + 1）
                // 或者 sizeCtl == rs + 65535  （如果达到最大帮助线程的数量，即 65535）
                // 或者 转移下标正在调整 （扩容结束）
                // 结束循环，返回 table
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                        sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                // 如果以上都不是, 将 sizeCtl + 1, （表示增加了一个线程帮助其扩容）
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    // 进行转移扩容
                    transfer(tab, nextTab);
                    //循环结束
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }

    /**
     * 协调多个线程如何调用transfer方法进行hash桶的迁移
     *
     * @param size the size
     */
    private final void tryPresize(int size) {
        //计算扩容的目标size
        // 给定的容量若>=MAXIMUM_CAPACITY的一半，直接扩容到允许的最大值，否则调用函数扩容
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY :
                tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        //没有正在初始化或扩容，或者说表还没有被初始化
        while ((sc = sizeCtl) >= 0) {
            Node<K, V>[] tab = table;
            int n;
            //tab没有初始化
            if (tab == null || (n = tab.length) == 0) {
                // 扩容阀值取较大者
                n = (sc > c) ? sc : c;
                // 期间没有其他线程对表操作，则CAS将SIZECTL状态置为-1，表示正在进行初始化
                //初始化之前，CAS设置sizeCtl=-1
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {
                            @SuppressWarnings("unchecked")
                            Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
                            table = nt;
                            //sc=0.75n,相当于扩容阈值,无符号右移2位，此即0.75*n
                            sc = n - (n >>> 2);
                        }
                    } finally {
                        // 此时并没有通过CAS赋值，因为其他想要执行初始化的线程，
                        // 发现sizeCtl=-1，就直接返回，从而确保任何情况，
                        // 只会有一个线程执行初始化操作。
                        sizeCtl = sc;
                    }
                }
            }
            // 若欲扩容值不大于原阀值，或现有容量>=最值，什么都不用做了
            //目标扩容size小于扩容阈值，或者容量超过最大限制时，不需要扩容
            else if (c <= sc || n >= MAXIMUM_CAPACITY)
                break;
                //扩容
            else if (tab == table) {
                int rs = resizeStamp(n);
                // sc<0表示，已经有其他线程正在扩容
                if (sc < 0) {
                    //RESIZE_STAMP_SHIFT=16,MAX_RESIZERS=2^15-1
                    Node<K, V>[] nt;
                    // 1. (sc >>> RESIZE_STAMP_SHIFT) != rs ：扩容线程数 > MAX_RESIZERS-1
                    // 2. sc == rs + 1 和 sc == rs + MAX_RESIZERS
                    // 3. (nt = nextTable) == null ：表示nextTable正在初始化
                    // transferIndex <= 0 ：表示所有hash桶均分配出去
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                            sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
                            transferIndex <= 0)
                        //如果不需要帮其扩容，直接返回
                        break;
                    //CAS设置sizeCtl=sizeCtl+1
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        //帮其扩容
                        transfer(tab, nt);
                }
                // 第一个执行扩容操作的线程，将sizeCtl设置为：
                // (resizeStamp(n) << RESIZE_STAMP_SHIFT) + 2)
                else if (U.compareAndSwapInt(this, SIZECTL, sc,
                        (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
            }
        }
    }

    /**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     * 将每个bin中的节点移动和/或复制到新表中。
     */
    private final void transfer(Node<K, V>[] tab, Node<K, V>[] nextTab) {
        int n = tab.length, stride;
        // 将 length / 8 然后除以 CPU核心数。如果得到的结果小于 16，那么就使用 16。
        // 这里的目的是让每个 CPU 处理的桶一样多，避免出现转移任务不均匀的现象，如果桶较少的话，默认一个 CPU（一个线程）处理 16 个桶
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range
        // 初始化nextTab，容量为旧数组的一倍
        if (nextTab == null) {            // initiating
            try {
                @SuppressWarnings("unchecked")
                // 扩容  2 倍
                        Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n << 1];
                // 赋值
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                // 扩容失败， sizeCtl 使用 int 最大值。
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            //赋值
            nextTable = nextTab;
            // 更新转移下标，就是 老的 tab 的 length
            transferIndex = n;
        }
        // 新 tab 的 length
        int nextn = nextTab.length;
        // 创建一个 ForwardingNode 节点，用于占位。
        // 当别的线程发现这个槽位中是 ForwardingNode 类型的节点，则跳过这个节点。
        ForwardingNode<K, V> fwd = new ForwardingNode<K, V>(nextTab);
        // 首次推进为 true，如果等于 true，说明需要再次推进一个下标（i--），
        // 反之，如果是 false，那么就不能推进下标，需要将当前的下标处理完毕才能继续推进
        boolean advance = true;
        // 完成状态，如果是 true，就结束此方法。
        boolean finishing = false; // to ensure sweep before committing nextTab
        // 死循环,i 表示下标，bound 表示当前线程可以处理的当前桶区间最小下标
        for (int i = 0, bound = 0; ; ) {
            Node<K, V> f;
            int fh;
            // 如果当前线程可以向后推进；这个循环就是控制 i 递减。
            // 同时，每个线程都会进入这里取得自己需要转移的桶的区间
            while (advance) {
                int nextIndex, nextBound;
                // 对 i 减一，判断是否大于等于 bound （正常情况下，如果大于 bound 不成立，
                // 说明该线程上次领取的任务已经完成了。那么，需要在下面继续领取任务）
                // 如果对 i 减一大于等于 bound（还需要继续做任务），
                // 或者完成了，修改推进状态为 false，不能推进了。
                // 任务成功后修改推进状态为 true。
                // 通常，第一次进入循环，i-- 这个判断会无法通过，
                // 从而走下面的 nextIndex 赋值操作（获取最新的转移下标）。
                // 其余情况都是：如果可以推进，将 i 减一，然后修改成不可推进。
                // 如果 i 对应的桶处理成功了，改成可以推进。
                if (--i >= bound || finishing)
                    // 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进
                    advance = false;
                    // 这里的目的是：
                    // 1. 当一个线程进入时，会选取最新的转移下标。
                    // 2. 当一个线程处理完自己的区间时，如果还有剩余区间的没有别的线程处理。再次获取区间。
                else if ((nextIndex = transferIndex) <= 0) {
                    // 如果小于等于0，说明没有区间了 ，i 改成 -1，推进状态变成 false，
                    // 不再推进，表示，扩容结束了，当前线程可以退出了
                    // 这个 -1 会在下面的 if 块里判断，从而进入完成状态判断
                    i = -1;
                    // 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进
                    advance = false;
                    // CAS 修改 transferIndex，即 length - 区间值，留下剩余的区间值供后面的线程使用
                } else if (U.compareAndSwapInt
                        (this, TRANSFERINDEX, nextIndex,
                                nextBound = (nextIndex > stride ?
                                        nextIndex - stride : 0))) {
                    // 这个值就是当前线程可以处理的最小当前区间最小下标
                    bound = nextBound;
                    // 初次对i 赋值，这个就是当前线程可以处理的当前区间的最大下标
                    i = nextIndex - 1;
                    // 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进，这样对导致漏掉某个桶。
                    // 下面的 if (tabAt(tab, i) == f) 判断会出现这样的情况。
                    advance = false;
                }
            }
            // 如果 i 小于0 （不在 tab 下标内，按照上面的判断，领取最后一段区间的线程扩容结束）
            //  如果 i >= tab.length
            //  如果 i + tab.length >= nextTable.length
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                // 如果完成了扩容
                if (finishing) {
                    // 删除成员变量
                    nextTable = null;
                    // 更新 table
                    table = nextTab;
                    // 更新阈值
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                // 如果没完成
                // 尝试将 sc -1. 表示这个线程结束帮助扩容了，将 sc 的低 16 位减一。
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    // 如果 sc - 2 不等于标识符左移 16 位。
                    // 如果他们相等了，说明没有线程在帮助他们扩容了。也就是说，扩容结束了。
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        // 不相等，说明没结束，当前线程结束方法。
                        return;
                    // 如果相等，扩容结束了，更新 finising 变量
                    finishing = advance = true;
                    // 再次循环检查一下整张表
                    i = n; // recheck before commit
                }
                // 获取老 tab i 下标位置的变量，如果是 null，就使用 fwd 占位。
            } else if ((f = tabAt(tab, i)) == null)
                // 如果成功写入 fwd 占位，再次推进一个下标
                advance = casTabAt(tab, i, null, fwd);
                // 如果不是 null 且 hash 值是 MOVED。
            else if ((fh = f.hash) == MOVED)
                // 说明别的线程已经处理过了，再次推进一个下标
                advance = true; // already processed
                // 到这里，说明这个位置有实际值了，且不是占位符。对这个节点上锁。为什么上锁，防止 putVal 的时候向链表插入数据
            else {
                synchronized (f) {
                    // 判断 i 下标处的桶节点是否和 f 相同
                    if (tabAt(tab, i) == f) {
                        Node<K, V> ln, hn;
                        // 如果 f 的 hash 值大于 0 。TreeBin 的 hash 是 -2
                        if (fh >= 0) {
                            // 对老长度进行与运算（第一个操作数的的第n位于第二个操作数的第n位如果都是1，那么结果的第n为也为1，否则为0）
                            // 由于 Map 的长度都是 2 的次方（000001000 这类的数字），那么取于 length 只有 2 种结果，一种是 0，一种是1
                            //  如果是结果是0 ，Doug Lea 将其放在低位，反之放在高位，目的是将链表重新 hash，放到对应的位置上，让新的取于算法能够击中他。
                            int runBit = fh & n;
                            // 尾节点，且和头节点的 hash 值取于不相等
                            Node<K, V> lastRun = f;
                            // 遍历这个桶
                            for (Node<K, V> p = f.next; p != null; p = p.next) {
                                // 取于桶中每个节点的 hash 值
                                int b = p.hash & n;
                                // 如果节点的 hash 值和首节点的 hash 值取于结果不同
                                if (b != runBit) {
                                    // 更新 runBit，用于下面判断 lastRun 该赋值给 ln 还是 hn。
                                    runBit = b;
                                    // 这个 lastRun 保证后面的节点与自己的取于值相同，避免后面没有必要的循环
                                    lastRun = p;
                                }
                            }
                            // 如果最后更新的 runBit 是 0 ，设置低位节点
                            if (runBit == 0) {
                                ln = lastRun;
                                hn = null;
                            } else {
                                // 如果最后更新的 runBit 是 1， 设置高位节点
                                hn = lastRun;
                                ln = null;
                            }
                            // 再次循环，生成两个链表，lastRun 作为停止条件，这样就是避免无谓的循环（lastRun 后面都是相同的取于结果）
                            for (Node<K, V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash;
                                K pk = p.key;
                                V pv = p.val;
                                // 如果与运算结果是 0，那么就还在低位
                                // 如果是0 ，那么创建低位节点
                                if ((ph & n) == 0)
                                    ln = new Node<K, V>(ph, pk, pv, ln);
                                else
                                    // 1 则创建高位
                                    hn = new Node<K, V>(ph, pk, pv, hn);
                            }
                            // 其实这里类似 hashMap
                            // 设置低位链表放在新链表的 i
                            setTabAt(nextTab, i, ln);
                            // 设置高位链表，在原有长度上加 n
                            setTabAt(nextTab, i + n, hn);
                            // 将旧的链表设置成占位符
                            setTabAt(tab, i, fwd);
                            // 继续向后推进
                            advance = true;
                            // 如果是红黑树
                        } else if (f instanceof TreeBin) {
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> lo = null, loTail = null;
                            TreeNode<K, V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            // 遍历
                            for (Node<K, V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K, V> p = new TreeNode<K, V>
                                        (h, e.key, e.val, null, null);
                                // 和链表相同的判断，与运算 == 0 的放在低位
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                    // 不是 0 的放在高位
                                } else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 如果树的节点数小于等于 6，那么转成链表，反之，创建一个新的树
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                    (hc != 0) ? new TreeBin<K, V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                    (lc != 0) ? new TreeBin<K, V>(hi) : t;
                            // 低位树
                            setTabAt(nextTab, i, ln);
                            // 高位树
                            setTabAt(nextTab, i + n, hn);
                            // 旧的设置成占位符
                            setTabAt(tab, i, fwd);
                            // 继续向后推进
                            advance = true;
                        }
                    }
                }
            }
        }
    }

    /* ---------------- Counter support -------------- */

    /**
     * A padded cell for distributing counts.  Adapted from LongAdder
     * and Striped64.  See their internal docs for explanation.
     * 用于分配计数的填充单元格。改编自LongAdder and Striped64
     * 请参阅他们的内部文档进行解释。
     * <p>
     * 注解@sun.misc.Contended用于解决伪共享问题。
     * 所谓伪共享，即是在同一缓存行（CPU缓存的基本单位）中存储了多个变量，
     * 当其中一个变量被修改时，就会影响到同一缓存行内的其他变量，
     * 导致它们也要跟着被标记为失效，其他变量的缓存命中率将会受到影响。
     * 解决伪共享问题的方法一般是对该变量填充一些无意义的占位数据，
     * 从而使它独享一个缓存行。
     */
    @sun.misc.Contended
    static final class CounterCell {
        //volatile修饰的值，内存可见
        volatile long value;

        CounterCell(long x) {
            value = x;
        }
    }

    /**
     * 计算ConcurrentHashMap中元素的个数
     *
     * @return the long
     */
    final long sumCount() {
        CounterCell[] as = counterCells;
        CounterCell a;
        //基本计数值
        long sum = baseCount;
        //计数单元格
        if (as != null) {
            //迭代技术单元格，计算总数量
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    //累加
                    sum += a.value;
            }
        }
        return sum;
    }

    // See LongAdder version for explanation 有关说明，请参阅LongAdder版本
    private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        // 当前线程的probe等于0，证明该线程的ThreadLocalRandom还未被初始化
        if ((h = ThreadLocalRandom.getProbe()) == 0) {
            // 初始化ThreadLocalRandom，当前线程会被设置一个probe
            ThreadLocalRandom.localInit();      // force initialization 强行初始化
            // probe用于在CounterCell数组中寻址
            h = ThreadLocalRandom.getProbe();
            // 未竞争标志
            wasUncontended = true;
        }
        // 冲突标志
        boolean collide = false;                // True if last slot nonempty
        //死循环
        for (; ; ) {
            CounterCell[] as;
            CounterCell a;
            int n;
            long v;
            //如果当前表格中存在数据，即已经初始化过
            if ((as = counterCells) != null && (n = as.length) > 0) {
                // 如果寻址到的Cell为空，那么创建一个新的Cell
                if ((a = as[(n - 1) & h]) == null) {
                    // cellsBusy是一个只有0和1两个状态的volatile整数
                    // 它被当做一个自旋锁，0代表无锁，1代表加锁
                    if (cellsBusy == 0) {            // Try to attach new Cell 尝试连接新的单元格
                        // 将传入的x作为初始值创建一个新的CounterCell
                        CounterCell r = new CounterCell(x); // Optimistic create
                        // 通过CAS尝试对自旋锁加锁
                        if (cellsBusy == 0 &&
                                U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            // 加锁成功，声明Cell是否创建成功的标志
                            boolean created = false;
                            //再核对下锁
                            try {               // Recheck under lock
                                CounterCell[] rs;
                                int m, j;
                                // 再次检查CounterCell数组是否不为空
                                // 并且寻址到的Cell为空
                                if ((rs = counterCells) != null &&
                                        (m = rs.length) > 0 &&
                                        rs[j = (m - 1) & h] == null) {
                                    // 将之前创建的新Cell放入数组
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                // 释放锁
                                cellsBusy = 0;
                            }
                            // 如果已经创建成功，中断循环
                            // 因为新Cell的初始值就是传入的增量，所以计数已经完毕了
                            if (created)
                                break;
                            // 如果未成功
                            // 代表as[(n - 1) & h]这个位置的Cell已经被其他线程设置
                            // 那么就从循环头重新开始
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;

                    // as[(n - 1) & h]非空
                    // 在addCount()函数中通过CAS更新当前线程的Cell进行计数失败
                    // 会传入wasUncontended = false，代表已经有其他线程进行竞争
                } else if (!wasUncontended)       // CAS already known to fail
                    // 设置未竞争标志，之后会重新计算probe，然后重新执行循环
                    wasUncontended = true;      // Continue after rehash
                    // 尝试进行计数，如果成功，那么就退出循环
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                    // 尝试更新失败，检查counterCell数组是否已经扩容
                    // 或者容量达到最大值（CPU的数量）
                else if (counterCells != as || n >= NCPU)
                    // 设置冲突标志，防止跳入下面的扩容分支
                    // 之后会重新计算probe
                    collide = false;            // At max size or stale
                    // 设置冲突标志，重新执行循环
                    // 如果下次循环执行到该分支，并且冲突标志仍然为true
                    // 那么会跳过该分支，到下一个分支进行扩容
                else if (!collide)
                    collide = true;
                    // 尝试加锁，然后对counterCells数组进行扩容
                else if (cellsBusy == 0 &&
                        U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        // 检查是否已被扩容
                        if (counterCells == as) {// Expand table unless stale
                            // 新数组容量为之前的1倍
                            CounterCell[] rs = new CounterCell[n << 1];
                            // 迁移数据到新数组
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        // 释放锁
                        cellsBusy = 0;
                    }
                    collide = false;
                    // 重新执行循环
                    continue;                   // Retry with expanded table
                }
                // 为当前线程重新计算probe
                h = ThreadLocalRandom.advanceProbe(h);
                // CounterCell数组未初始化，尝试获取自旋锁，然后进行初始化
            } else if (cellsBusy == 0 && counterCells == as &&
                    U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {                           // Initialize table
                    if (counterCells == as) {
                        // 初始化CounterCell数组，初始容量为2
                        CounterCell[] rs = new CounterCell[2];
                        // 初始化CounterCell
                        rs[h & 1] = new CounterCell(x);
                        counterCells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                // 初始化CounterCell数组成功，退出循环
                if (init)
                    break;
                // 如果自旋锁被占用，则只好尝试更新baseCount
            } else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break;                          // Fall back on using base
        }
    }

    /* ---------------- Conversion from/to TreeBins -------------- */

    /**
     * Replaces all linked nodes in bin at given index unless table is
     * too small, in which case resizes instead.
     */
    private final void treeifyBin(Node<K, V>[] tab, int index) {
        Node<K, V> b;
        int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
                tryPresize(n << 1);
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {
                        TreeNode<K, V> hd = null, tl = null;
                        for (Node<K, V> e = b; e != null; e = e.next) {
                            TreeNode<K, V> p =
                                    new TreeNode<K, V>(e.hash, e.key, e.val,
                                            null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        setTabAt(tab, index, new TreeBin<K, V>(hd));
                    }
                }
            }
        }
    }

    /**
     * Returns a list on non-TreeNodes replacing those in given list.
     * 返回非树节点上的列表，替换给定列表中的列表。
     */
    static <K, V> Node<K, V> untreeify(Node<K, V> b) {
        Node<K, V> hd = null, tl = null;
        for (Node<K, V> q = b; q != null; q = q.next) {
            Node<K, V> p = new Node<K, V>(q.hash, q.key, q.val, null);
            if (tl == null)
                hd = p;
            else
                tl.next = p;
            tl = p;
        }
        return hd;
    }

 



    /* ----------------Table Traversal -------------- */

    /**
     * Records the table, its length, and current traversal index for a
     * traverser that must process a region of a forwarded table before
     * proceeding with current table.
     */
    static final class TableStack<K, V> {
        int length;
        int index;
        Node<K, V>[] tab;
        TableStack<K, V> next;
    }

    /**
     * Encapsulates traversal for methods such as containsValue; also
     * serves as a base class for other iterators and spliterators.
     * <p>
     * 封装方法的遍历，如containsValue;还用作其他迭代器和spliterator的基类。
     * <p>
     * Method advance visits once each still-valid node that was
     * reachable upon iterator construction. It might miss some that
     * were added to a bin after the bin was visited, which is OK wrt
     * consistency guarantees. Maintaining this property in the face
     * of possible ongoing resizes requires a fair amount of
     * bookkeeping state that is difficult to optimize away amidst
     * volatile accesses.  Even so, traversal maintains reasonable
     * throughput.
     * <p>
     * 方法预先访问在迭代器构造时可访问的每个仍然有效的节点。
     * 它可能会遗漏一些在访问了bin之后添加到bin中的内容，这是wrt一致性的保证。
     * 面对可能正在进行的大小调整，维护此属性需要相当数量的簿记状态，而在易变访问中很难优化这些状态。
     * 即便如此，遍历仍然保持了合理的吞吐量。
     * <p>
     * Normally, iteration proceeds bin-by-bin traversing lists.
     * However, if the table has been resized, then all future steps
     * must traverse both the bin at the current index as well as at
     * (index + baseSize); and so on for further resizings. To
     * paranoically cope with potential sharing by users of iterators
     * across threads, iteration terminates if a bounds checks fails
     * for a table read.
     * <p>
     * 通常情况下，迭代按bin遍历列表。但是，如果表已经调整了大小，
     * 但是，如果表的大小已经调整，那么以后的所有步骤都必须遍历当前索引处的bin和at (index + baseSize);
     * 来进一步调整大小。进一步调整。为了偏执地处理线程间迭代器用户的潜在共享，如果读取的表的边界检查失败，迭代将终止。
     */
    static class Traverser<K, V> {
        Node<K, V>[] tab;        // current table; updated if resized 当前表;如果更新调整
        Node<K, V> next;         // the next entry to use 下一个要使用的entry
        TableStack<K, V> stack, spare; // to save/restore on Forwardin gNodes 转发节点时的存储与恢复
        int index;              // index of bin to use next  bin next 索引
        int baseIndex;          // current index of initial table 初始表的当前索引
        int baseLimit;          // index bound for initial table 初始表的索引边界
        final int baseSize;     // initial table size 初始表大小

        Traverser(Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        /**
         * Advances if possible, returning next valid node, or null if none.
         * 如果可能，则前进，返回下一个有效节点，如果没有，则返回null。
         */
        final Node<K, V> advance() {
            Node<K, V> e;
            //如果下一节点存在，则走下一步
            if ((e = next) != null)
                e = e.next;
            //死循环
            for (; ; ) {
                Node<K, V>[] t;
                int i, n;  // must use locals in checks
                //如果e不为空
                if (e != null)
                    return next = e;
                //如果初始索引超过边界或者表为空，或者已经迭代到头
                if (baseIndex >= baseLimit || (t = tab) == null ||
                        (n = t.length) <= (i = index) || i < 0)
                    return next = null;
                //获取到节点且不为空
                if ((e = tabAt(t, i)) != null && e.hash < 0) {
                    //如果是ForwardingNode节点
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode<K, V>) e).nextTable;
                        e = null;
                        //保存状态
                        pushState(t, i, n);
                        continue;
                    }
                    //如果时树节点
                    else if (e instanceof TreeBin)
                        e = ((TreeBin<K, V>) e).first;
                    else
                        e = null;
                }
                //如果栈不为空
                if (stack != null)
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    index = ++baseIndex; // visit upper slots if present
            }
        }

        /**
         * Saves traversal state upon encountering a forwarding node.
         * 保存遇到转发节点时的遍历状态。
         */
        private void pushState(Node<K, V>[] t, int i, int n) {
            TableStack<K, V> s = spare;  // reuse if possible
            if (s != null)
                spare = s.next;
            else
                s = new TableStack<K, V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        /**
         * Possibly pops traversal state.
         * pop遍历状态。
         *
         * @param n length of current table
         */
        private void recoverState(int n) {
            TableStack<K, V> s;
            int len;
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                TableStack<K, V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }


    // Unsafe mechanics
    private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                    (k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset
                    (k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset
                    (k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset
                    (k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset
                    (ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
```

