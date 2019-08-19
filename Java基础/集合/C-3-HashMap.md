# HashMap

## 一、内部基本属性常量

```java
/**
 * 默认初始容量—必须是2的幂。
 * 为啥不直接写16？？
 */
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16：

/**
 * 最大容量，如果较高的值由带参数的任何构造函数隐式指定，则使用该值。必须是2的幂 <= 1<<30。
 */
static final int MAXIMUM_CAPACITY = 1 << 30;

/**
 * 构造函数中没有指定时使用的负载因子。
 */
static final float DEFAULT_LOAD_FACTOR = 0.75f;

/**
 * 使用树(而不是列表)来设置桶的计数阈值。当向至少具有这么多节点的桶添加元素时，桶将转换为树。
 * 该值必须大于2，并且应该至少为8，以便与树木移除中关于收缩后转换回普通桶的假设相吻合。
 * 为啥不是 1 << 3 ?
 */
static final int TREEIFY_THRESHOLD = 8;

/**
 * 用于在调整大小操作期间反树化(拆分)桶的计数阈值。
 * 应小于TREEIFY_THRESHOLD，且最多6个以配合收缩检测下删除。
 */
static final int UNTREEIFY_THRESHOLD = 6;

/**
 * 最小的表容量，其中的桶可以树化。(否则，如果一个桶中有太多节点，则会调整表的大小。)
 * 应至少为4 * TREEIFY_THRESHOLD，以避免调整大小和treeification阈值之间的冲突。
 */
static final int MIN_TREEIFY_CAPACITY = 64;


/**
 * 表，第一次使用时初始化，并根据需要调整大小。当分配时，长度总是2的幂。
 * (在某些操作中，我们还允许长度为零，以允许当前不需要的引导机制。)
 */
transient Node<K, V>[] table;

/**
 * 保存缓存entrySet ()。AbstractMap字段用于keySet()和values()。
 */
transient Set<Entry<K, V>> entrySet;

/**
 * 此映射中包含的键值映射的数目。
 */
transient int size;

/**
 * 这个HashMap在结构上被修改的次数，结构修改是指改变HashMap中映射的数量或修改其内部结构的次数(例如，rehash)。
 * 此字段用于使HashMap集合视图上的迭代器快速失效。(见ConcurrentModificationException)。
 */
transient int modCount;

/**
 * 要调整大小的下一个大小值=(capacity * load factor)
 */
int threshold;

/**
 * 哈希表的加载因子
 */
final float loadFactor;
```

hash计算:

1、**通过hashCode()的高16位异或低16位实现的**：( `h=k.hashCode())^(h>>>16)`)，主要是从速度、功效、质量来考虑的，这么做可以在数组table的length比较小的时候，也能保证考虑到高低Bit都参与到Hash的计算中，同时不会有太大的开销。

2、计算组数下标：`h&(table.length-1)`，HashMap底层数组的长度总是2的n次方，这是HashMap在速度上的优。当length总是2的n次方时，`h&(length-1)`运算等价于对length取模，也就是h%length，但是&比％具有更高的效率。

## 二、put

put过程:

![1566058147404](assets/1566058147404.png)

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

// 第三个参数 onlyIfAbsent 如果是 true，那么只有在不存在该 key 时才会进行 put 操作
// 第四个参数 evict 我们这里不关心
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 第一次 put 值的时候，会触发下面的 resize()，类似 java7 的第一次 put 也要初始化数组长度
    // 第一次 resize 和后续的扩容有些不一样，因为这次是数组从 null 初始化到默认的 16 或自定义的初始容量
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // 找到具体的数组下标，如果此位置没有值，那么直接初始化一下 Node 并放置在这个位置就可以了
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);

    else {// 数组该位置有数据
        Node<K,V> e; K k;
        // 首先，判断该位置的第一个数据和我们要插入的数据，key 是不是"相等"，如果是，取出这个节点
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        // 如果该节点是代表红黑树的节点，调用红黑树的插值方法，本文不展开说红黑树
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            // 到这里，说明数组该位置上是一个链表
            for (int binCount = 0; ; ++binCount) {
                // 插入到链表的最后面(Java7 是插入到链表的最前面)
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // TREEIFY_THRESHOLD 为 8，所以，如果新插入的值是链表中的第 8 个
                    // 会触发下面的 treeifyBin，也就是将链表转换为红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 如果在该链表中找到了"相等"的 key(== 或 equals)
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    // 此时 break，那么 e 为链表中[与要插入的新值的 key "相等"]的 node
                    break;
                p = e;
            }
        }
        // e!=null 说明存在旧值的key与要插入的key"相等"
        // 对于我们分析的put操作，下面这个 if 其实就是进行 "值覆盖"，然后返回旧值
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    // 如果 HashMap 由于新插入这个值导致 size 已经超过了阈值，需要进行扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

## 三、get

1、计算 key 的 hash 值，根据 hash 值找到对应数组下标: `hash & (length-1)`

2、判断数组该位置处的元素是否刚好就是我们要找的，如果不是，走第三步

3、判断该元素类型是否是 TreeNode，如果是，用红黑树的方法取数据，如果不是，走第四步

4、遍历链表，直到找到相等(==或equals)的 key

```java
public V get(Object key) {
    //当前节点
    Node<K, V> e;
    //获取当前节点，并返回节点的值
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K, V> getNode(int hash, Object key) {
    //当前表
    Node<K, V>[] tab;
    Node<K, V> first, e;
    int n;
    K k;
    //如果当前表不为null,且表长度大于0.并且找到桶的位置
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        //如果第一个就和key相等
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            //返回桶的第一个元素
            return first;
        //如果第一个节点后面还有接待你
        if ((e = first.next) != null) {
            //如果是红黑树
            if (first instanceof TreeNode)
                //获取树中的节点
                return ((TreeNode<K, V>) first).getTreeNode(hash, key);
            //迭代链表，获取匹配的节点
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    //没找到，返回null
    return null;
}
```



## 四、thresold、loadFactor

当 HashMap 中的 `size >= threshold` 时，HashMap 就要扩容。

- size：HashMap表中包含的键值映射的数目。
- threshold：要调整大小的下一个大小的阈值，等于(capacity * loadFactor)。
- capacity：HashMap容量
- loadFactor：哈希表的加载因子。



其中有一个`tableSizeFor()`方法的作用是：  **找到>=这个值的一个2的次方数**。

这个方法是在计算阈值`thresold`的时候调用的 (初始计算threshold的时候):

![1566107350581](assets/1566107350581.png)

举例:

```java
public class Main {
    public static void main(String[] args) {
        for(int i = 0; i < 35; i++){
            System.out.println(i + " -> " + tableSizeFor(i));
        }
    }

    static final int MAXIMUM_CAPACITY = 1 << 30;

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
```

输出:

```java
0 -> 1
1 -> 1
2 -> 2
3 -> 4
4 -> 4
5 -> 8
6 -> 8
7 -> 8
8 -> 8
9 -> 16
10 -> 16
11 -> 16
12 -> 16
13 -> 16
14 -> 16
15 -> 16
16 -> 16
17 -> 32
18 -> 32
19 -> 32
20 -> 32
21 -> 32
22 -> 32
23 -> 32
24 -> 32
25 -> 32
26 -> 32
27 -> 32
28 -> 32
29 -> 32
30 -> 32
31 -> 32
32 -> 32
33 -> 64
34 -> 64
```

添加第一个元素时，默认分配的大小为16，不过，并不是size大于16时再进行扩展，下次什么时候扩展与threshold有关。

threshold表示阈值，当键值对个数size大于等于threshold时考虑进行扩展。threshold是怎么算出来的呢？初始阈值就是上面的`tableSizeFor()`方法计算得到，也就是和`initCapity`有关。

后面一般而言，threshold等于table.length乘以loadFactor，比如，如果table.length为16，loadFactor为0.75，则threshold为12，看下面的源码。

![1566107424122](assets/1566107424122.png)

loadFactor是负载因子，表示整体上table被占用的程度，是一个浮点数，默认为0.75，可以通过构造方法进行修改。

## 五、基础部分源码

```java
public class HashMap<K, V> extends AbstractMap<K, V>
        implements Map<K, V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    /*
     * Implementation notes.
     *
     * 实现注意事项。
     *
     * This map usually acts as a binned (bucketed) hash table, but
     * when bins get too large, they are transformed into bins of
     * TreeNodes, each structured similarly to those in
     * java.util.TreeMap. Most methods try to use normal bins, but
     * relay to TreeNode methods when applicable (simply by checking
     * instanceof a node).  Bins of TreeNodes may be traversed and
     * used like any others, but additionally support faster lookup
     * when overpopulated. However, since the vast majority of bins in
     * normal use are not overpopulated, checking for existence of
     * tree bins may be delayed in the course of table methods.
     *
     * 这个映射通常充当一个装了好多桶的哈希表，但是当桶变得太大时，它们会被转换成树节点的桶
     * ，每个桶的结构都类似于java.util.TreeMap中的桶。
     * 大多数方法都尝试使用普通的桶（链表形式），但在适用时中继到TreeNode方法(只需检查节点的instanceof)。
     * 树节点的存储箱可以像其他存储箱一样被遍历和使用，但是在过度填充时支持更快的查找。
     * 但是，由于正常使用的大多数桶并没有过度填充，所以在表方法的过程中可能会延迟检查树桶是否存在。
     *
     * Tree bins (i.e., bins whose elements are all TreeNodes) are
     * ordered primarily by hashCode, but in the case of ties, if two
     * elements are of the same "class C implements Comparable<C>",
     * type then their compareTo method is used for ordering. (We
     * conservatively check generic types via reflection to validate
     * this -- see method comparableClassFor).  The added complexity
     * of tree bins is worthwhile in providing worst-case O(log n)
     * operations when keys either have distinct hashes or are
     * orderable, Thus, performance degrades gracefully under
     * accidental or malicious usages in which hashCode() methods
     * return values that are poorly distributed, as well as those in
     * which many keys share a hashCode, so long as they are also
     * Comparable. (If neither of these apply, we may waste about a
     * factor of two in time and space compared to taking no
     * precautions. But the only known cases stem from poor user
     * programming practices that are already so slow that this makes
     * little difference.)
     *
     * 树形桶(即其元素都是TreeNode的桶)主要由hashCode排序，
     * 但在链接的情况下，如果两个元素属于相同的“class C implementation Comparable<C>”，
     * 则键入它们的compareTo方法来排序。
     * (我们通过反射保守地检查泛型类型来验证这一点——请参见comparableClassFor方法)。
     * 当键具有不同的哈希值或可排序时，在提供最坏情况O(log n)操作时，树箱增加的复杂性是值得的。
     * 因此，在hashCode()方法返回分布很差的值的意外或恶意使用中，
     * 以及在许多键共享一个hashCode的情况下(只要它们也是可比较的)，性能会优雅地下降。
     * (如果这两种方法都不适用，与不采取预防措施相比，我们可能会浪费大约两倍的时间和空间。
     * 但目前所知的唯一案例来自于糟糕的用户编程实践，这些实践已经非常缓慢，以至于没有什么区别。)
     *
     * Because TreeNodes are about twice the size of regular nodes, we
     * use them only when bins contain enough nodes to warrant use
     * (see TREEIFY_THRESHOLD). And when they become too small (due to
     * removal or resizing) they are converted back to plain bins.  In
     * usages with well-distributed user hashCodes, tree bins are
     * rarely used.  Ideally, under random hashCodes, the frequency of
     * nodes in bins follows a Poisson distribution
     * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
     * parameter of about 0.5 on average for the default resizing
     * threshold of 0.75, although with a large variance because of
     * resizing granularity. Ignoring variance, the expected
     * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
     * factorial(k)). The first values are:
     *
     * 因为树节点的大小大约是普通节点的两倍，所以我们只在桶中包含足够的节点以保证使用时才使用它们(请参阅TREEIFY_THRESHOLD)。
     * 当它们变得太小(由于移除或调整大小)，就会被转换回普通的桶。
     * 在使用分布良好的用户哈希码时，很少使用树箱。
     * 理想情况下，在随机哈希码下，bin中节点的频率遵循泊松分布(http://en.wikipedia.org/wiki/Poisson_distribution)，
     * 默认调整阈值为0.75，平均参数约为0.5，尽管由于调整粒度而存在较大的差异。
     * 忽略方差，列表大小k的预期出现次数为(exp(-0.5) pow(0.5, k) / factorial(k))
     * 第一个值是：
     *
     * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     * more: less than 1 in ten million
     * more: 少于千万分之一
     *
     * The root of a tree bin is normally its first node.  However,
     * sometimes (currently only upon Iterator.remove), the root might
     * be elsewhere, but can be recovered following parent links
     * (method TreeNode.root()).
     *
     * 树状容器的根通常是它的第一个节点。
     * 但是，有时(目前仅在Iterator.remove之后)，根可能在其他地方，
     * 但是可以通过父链接(方法TreeNode.root())恢复。
     *
     * All applicable internal methods accept a hash code as an
     * argument (as normally supplied from a public method), allowing
     * them to call each other without recomputing user hashCodes.
     * Most internal methods also accept a "tab" argument, that is
     * normally the current table, but may be a new or old one when
     * resizing or converting.
     *
     * 所有适用的内部方法都接受散列代码作为参数(通常由公共方法提供)，允许它们在不重新计算用户散列代码的情况下相互调用。
     * 大多数内部方法也接受“tab”参数，这通常是当前表，但在调整大小或转换时可能是新的或旧的。
     *
     * When bin lists are treeified, split, or untreeified, we keep
     * them in the same relative access/traversal order (i.e., field
     * Node.next) to better preserve locality, and to slightly
     * simplify handling of splits and traversals that invoke
     * iterator.remove. When using comparators on insertion, to keep a
     * total ordering (or as close as is required here) across
     * rebalancings, we compare classes and identityHashCodes as
     * tie-breakers.
     *
     * 当bin列表被treeified、split或untreeified时，我们将它们保持相同的相对访问/遍历顺序(即为了更好地保存局部，
     * 并稍微简化对调用iterator.remove的分割和遍历的处理。
     * 当在插入时使用比较器时，为了保持整个重新平衡的顺序(或尽可能接近这里的要求)
     * ，我们将类和dentityhashcode作为连接符进行比较。
     *
     * The use and transitions among plain vs tree modes is
     * complicated by the existence of subclass LinkedHashMap. See
     * below for hook methods defined to be invoked upon insertion,
     * removal and access that allow LinkedHashMap internals to
     * otherwise remain independent of these mechanics. (This also
     * requires that a map instance be passed to some utility methods
     * that may create new nodes.)
     *
     * 由于LinkedHashMap子类的存在，普通vs树模式之间的使用和转换变得复杂。
     * 有关定义在插入、删除和访问时调用的钩子方法，请参见下面，这些方法允许LinkedHashMap内部保持独立于这些机制。
     * (这还要求将map实例传递给一些可能创建新节点的实用方法。)
     *
     * The concurrent-programming-like SSA-based coding style helps
     * avoid aliasing errors amid all of the twisty pointer operations.
     *
     * 基于并行编程的类似于ssa的编码风格有助于避免所有扭曲指针操作中的混叠错误。
     */

    /**
     * The default initial capacity - MUST be a power of two.
     * 默认初始容量—必须是2的幂。
     * 为啥不直接写16？？
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16：

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     * 最大容量，如果较高的值由带参数的任何构造函数隐式指定，则使用该值。必须是2的幂 <= 1<<30。
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     * 构造函数中没有指定时使用的负载因子。
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     * 使用树(而不是列表)来设置桶的计数阈值。当向至少具有这么多节点的桶添加元素时，桶将转换为树。
     * 该值必须大于2，并且应该至少为8，以便与树木移除中关于收缩后转换回普通桶的假设相吻合。
     * 为啥不是 1 << 3 ?
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     * 用于在调整大小操作期间反树化(拆分)桶的计数阈值。
     * 应小于TREEIFY_THRESHOLD，且最多6个以配合收缩检测下删除。
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     * 最小的表容量，其中的桶可以树化。(否则，如果一个桶中有太多节点，则会调整表的大小。)
     * 应至少为4 * TREEIFY_THRESHOLD，以避免调整大小和treeification阈值之间的冲突。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * Basic hash bin node, used for most entries.  (See below for
     * TreeNode subclass, and in LinkedHashMap for its Entry subclass.)
     * 基本哈希桶节点，用于大多数条目。(参见下面的TreeNode子类和LinkedHashMap中的Entry子类。)
     */
    static class Node<K, V> implements Entry<K, V> {
        // 哈希值
        final int hash;
        // 键
        final K key;
        // 值
        V value;
        // 写一个Node节点的引用
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            // 位异或运算（^）：两个数转为二进制，然后从高位开始比较，如果相同则为0，不相同则为1
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Entry<?, ?> e = (Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /* ---------------- Static utilities -------------- */
    /* ---------------- 静态工具 -------------- */
    /**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
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
     * 由于该表使用了2的幂掩码，因此仅在当前掩码之上以位为单位变化的散列集总是会发生冲突。
     * (已知的例子包括在小表中保存连续整数的浮点键集。)因此，我们应用一个转换，将更高位的影响向下传播。
     * 位扩展的速度、实用性和质量之间存在权衡。
     * <p>
     * 因为许多常见的散列集已经合理分布(所以不要受益于传播),
     * 在桶中我们用树来处理大型的碰撞,通过异或一些位的改变以最优的的方式来减少系统lossage,纳入最高位的影响,
     * 否则，由于表范围，将永远不会在索引计算中使用它。
     * <p>
     * 计算key的hashCode值h
     * h无符号右移16位，得到h的高16位
     * h与其高16位异或。
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Returns x's Class if it is of the form "class C implements
     * Comparable<C>", else null.
     * 如果x实现了Comparable接口，则返回x的类，否则返回null
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            //如果是String类型，直接返回String.class
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            //获取所有的实现接口，迭代
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    //如果为参数化类型，且为Comparable
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
     * 比较k与x
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable) k).compareTo(x));
    }

    /**
     * Returns a power of two size for the given target capacity.
     * 返回给定目标容量的2次幂。
     * 无符号右移  ， 按位或，很高明的做法
     * 假设cap=8
     * 第一行：n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0111
     * 第二行：n无符号右移1位： 0000 0000 0000 0000 0000 0000 0000 0011
     * 与上一步n或：  0000 0000 0000 0000 0000 0000 0000 0111
     * n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0111
     * 第三行：n无符号右移2位： 0000 0000 0000 0000 0000 0000 0000 0001
     * 与上一步n或： 0000 0000 0000 0000 0000 0000 0000 0111
     * n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0111
     * 第四行：n无符号右移4位： 0000 0000 0000 0000 0000 0000 0000 0000
     * 与上一步n或： 0000 0000 0000 0000 0000 0000 0000 0111
     * n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0111
     * 第五行：n无符号右移8位： 0000 0000 0000 0000 0000 0000 0000 0000
     * 与上一步n或： 0000 0000 0000 0000 0000 0000 0000 0111
     * n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0111
     * 第五行：n无符号右移16位： 0000 0000 0000 0000 0000 0000 0000 0000
     * 与上一步n或： 0000 0000 0000 0000 0000 0000 0000 0111
     * n=7     二进制： 0000 0000 0000 0000 0000 0000 0000 0101
     * 第六行：n不小于0，也不大于等于1<<30 ,所以 n=n+1=8
     * <p>
     * 假设           cap=0100 0000 0000 0000 0000 0000 0000 0000   1个1
     * 1:   无符号右移1位：0010 0000 0000 0000 0000 0000 0000 0000
     * 或操作：0110 0000 0000 0000 0000 0000 0000 0000   2个1
     * 2:   无符号右移2位：0001 1000 0000 0000 0000 0000 0000 0000
     * 或操作：0111 1000 0000 0000 0000 0000 0000 0000   4个1
     * 3:   无符号右移4位：0000 0111 1000 0000 0000 0000 0000 0000
     * 或操作：0111 1111 1000 0000 0000 0000 0000 0000   8个1
     * 4:   无符号右移8位：0000 0000 0111 1111 1000 0000 0000 0000
     * 或操作：0111 1111 1111 1111 1000 0000 0000 0000   16个1
     * 4:  无符号右移16位：0000 0000 0000 0000 0111 1111 1111 1111
     * 或操作：0111 1111 1111 1111 1111 1111 1111 1111   31个1
     * 5：        结果+1：1000 0000 0000 0000 0000 0000 0000 0000   即2^30;1 << 30。最大值
     * <p>
     * 发现一个规律：无符号右移再位或的最终结果会将二进制首个1的后面所有位都变成1，最后结果再加1，则向前进位（前提不溢出），
     * 结果必是2的幂
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /* ---------------- Fields -------------- */

    /**
     * The table, initialized on first use, and resized as
     * necessary. When allocated, length is always a power of two.
     * (We also tolerate length zero in some operations to allow
     * bootstrapping mechanics that are currently not needed.)
     * 表，第一次使用时初始化，并根据需要调整大小。当分配时，长度总是2的幂。
     * (在某些操作中，我们还允许长度为零，以允许当前不需要的引导机制。)
     */
    transient Node<K, V>[] table;

    /**
     * Holds cached entrySet(). Note that AbstractMap fields are used
     * for keySet() and values().
     * 保存缓存entrySet ()。AbstractMap字段用于keySet()和values()。
     */
    transient Set<Entry<K, V>> entrySet;

    /**
     * The number of key-value mappings contained in this map.
     * 此映射中包含的键值映射的数目。
     */
    transient int size;

    /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     * <p>
     * 这个HashMap在结构上被修改的次数，结构修改是指改变HashMap中映射的数量或修改其内部结构的次数(例如，rehash)。
     * 此字段用于使HashMap集合视图上的迭代器快速失效。(见ConcurrentModificationException)。
     */
    transient int modCount;

    /**
     * The next size value at which to resize (capacity * load factor).
     * 要调整大小的下一个大小阈值=(capacity * load factor)
     *
     * @serial
     */
    // (The javadoc description is true upon serialization.
    // Additionally, if the table array has not been allocated, this
    // field holds the initial array capacity, or zero signifying
    // DEFAULT_INITIAL_CAPACITY.)
    int threshold;

    /**
     * The load factor for the hash table.
     * 哈希表的加载因子
     *
     * @serial
     */
    final float loadFactor;

    /* ---------------- Public operations -------------- */

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     * 构造一个具有指定初始容量和负载因子的空HashMap
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *                                  or the load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {
        //初始容量不能小于0
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        //初始容量最大为2^30
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        //加载因子不能小于等于0
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        //扩容阈值，2的n次幂
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     * 构造一个空的HashMap，具有指定的初始容量和缺省负载因子(0.75)。
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     * 构造一个空的HashMap，默认初始容量(16)和默认负载因子(0.75)。
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     * 使用与指定的<tt>Map</tt>相同的映射构造一个新的<tt>HashMap</tt>。
     * 创建<tt>HashMap</tt>时使用了默认的负载因子(0.75)和足够容纳指定<tt>Map</tt>中的映射的初始容量。
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    /**
     * Implements Map.putAll and Map constructor
     * 实现了Map.putAl和Map构造函数
     *
     * @param m     the map
     * @param evict false when initially constructing this map, else
     *              true (relayed to method afterNodeInsertion).
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            //如果第一次初始化
            if (table == null) { // pre-size
                float ft = ((float) s / loadFactor) + 1.0F;
                int t = ((ft < (float) MAXIMUM_CAPACITY) ?
                        (int) ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    // 算出阈值
                    threshold = tableSizeFor(t);
            } else if (s > threshold)
                // 需要扩容
                resize();
            for (Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                //元素入map
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     * 返回此映射中键值映射的数目。
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * 如果此映射不包含键值映射，则返回<tt>true</tt>
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p>
     * 返回指定键映射到的值，如果此映射不包含键的映射，则返回{@code null}。
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * <p>
     * 更正式地说，如果这个映射包含一个键{@code k}到一个值{@code v}的映射，
     * 使得{@code (key==null ?k==null: key.equals(k))}，则该方法返回{@code v};
     * 否则返回{@code null}。(最多可以有一个这样的映射。)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     * <p>
     * 返回值{@code null}不一定表示映射不包含键的映射;
     * 也有可能映射显式地将键映射到{@code null}。
     * {@link #containsKey containsKey}操作可用于区分这两种情况。
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {
        //当前节点
        Node<K, V> e;
        //获取当前节点，并返回节点的值
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods
     * <p>
     * 获取节点
     *
     * @param hash hash for key
     * @param key  the key
     * @return the node, or null if none
     */
    final Node<K, V> getNode(int hash, Object key) {
        //当前表
        Node<K, V>[] tab;
        Node<K, V> first, e;
        int n;
        K k;
        //如果当前表不为null,且表长度大于0.并且找到桶的位置
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & hash]) != null) {
            //如果第一个就和key相等
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))
                //返回桶的第一个元素
                return first;
            //如果第一个节点后面还有接待你
            if ((e = first.next) != null) {
                //如果是红黑树
                if (first instanceof TreeNode)
                    //获取树中的节点
                    return ((TreeNode<K, V>) first).getTreeNode(hash, key);
                //迭代链表，获取匹配的节点
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        //没找到，返回null
        return null;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     * 如果此映射包含指定键的映射，则返回true。
     *
     * @param key The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     * <p>
     * 将指定值与此映射中的指定键关联。如果映射以前包含键的映射，则替换旧值。
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Implements Map.put and related methods
     * 实现了 Map.put及其他相关方法
     *
     * @param hash         键的Hash值
     * @param key          键
     * @param value        值
     * @param onlyIfAbsent 如果为真，则不要更改现有值
     * @param evict        如果为false，则该表处于创建模式。
     * @return 老的值，如果没有，则为空
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        //当前表
        Node<K, V>[] tab;
        //桶
        Node<K, V> p;
        //n:表长度
        //i:桶在表中的索引
        int n, i;
        //如果当前表为null或者表长度为0
        if ((tab = table) == null || (n = tab.length) == 0)
            //扩容操作,初始化
            n = (tab = resize()).length;
        //如果 键所在的桶 为null
        if ((p = tab[i = (n - 1) & hash]) == null)
            // 新建桶
            tab[i] = newNode(hash, key, value, null);

            //如果 键所在的桶 不为null
        else {
            //当前key节点
            Node<K, V> e;
            K k;
            //确认桶的位置
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
                //如果是红黑树
            else if (p instanceof TreeNode)
                //红黑树新增节点 后期分析
                e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            else {
                //循环
                for (int binCount = 0; ; ++binCount) {
                    //如果桶的下一个节点为null
                    if ((e = p.next) == null) {
                        //创建节点
                        p.next = newNode(hash, key, value, null);
                        //如果大于树化阈值
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            //将链表转为红黑树，后期分析
                            treeifyBin(tab, hash);
                        //跳出循环
                        break;
                    }
                    //在链表中找到了key
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        //跳出循环
                        break;
                    p = e;
                }
            }
            //存在key节点
            if (e != null) { // existing mapping for key
                //老的值
                V oldValue = e.value;
                // onlyIfAbsent如果为真，则不要更改现有值
                if (!onlyIfAbsent || oldValue == null)
                    //更改现有值
                    e.value = value;
                //回调
                afterNodeAccess(e);
                //返回旧的值
                return oldValue;
            }
        }
        //修改计数器加一
        ++modCount;
        //判断是否需要扩容
        if (++size > threshold)
            //扩容操作
            resize();
        //回调
        afterNodeInsertion(evict);
        //返回null
        return null;
    }

    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     * <p>
     * 初始化或两倍表大小。如果为空，则按照字段阈值中包含的初始容量目标分配。
     * 否则，因为我们使用的是2的幂展开，所以每个桶中的元素必须保持相同的索引，或者在新表中以2的幂偏移量移动。
     *
     * @return the table
     */
    final Node<K, V>[] resize() {
        //老的表，有可能为null
        Node<K, V>[] oldTab = table;
        //获取老的表的长度
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        //老的扩容阈值
        int oldThr = threshold;
        //新的表长度，扩容阈值
        int newCap, newThr = 0;
        // 老的表长度大于0
        if (oldCap > 0) {
            // 最大的时候，不管理，直接返回
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                // 新的表长度变为以前老表长度的2倍
                newThr = oldThr << 1; // double threshold
        } else if (oldThr > 0) // initial capacity was placed in threshold
            //如果老的阈值大于0，且老的表长度为0，则新表容量设置为老阈值
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            //初始阈值为零表示使用默认值：16
            newCap = DEFAULT_INITIAL_CAPACITY;
            //扩容阈值：16*0.75
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        //如果新的阈值为0，则根据新的表容量计算出。
        if (newThr == 0) {
            float ft = (float) newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
                    (int) ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        // 生成新的数组（表）
        @SuppressWarnings({"rawtypes", "unchecked"})
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        // 使用新表
        table = newTab;
        //如果老表内有数据，则取出来，放到新表中，耗时的操作
        if (oldTab != null) {
            //迭代老的表
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e;
                //如果表中桶内容不为null
                if ((e = oldTab[j]) != null) {
                    //清空，help GC
                    oldTab[j] = null;
                    //如果桶中不存在下一个节点
                    if (e.next == null)
                        //将此桶计算hash,重新放入新桶中
                        newTab[e.hash & (newCap - 1)] = e;
                        //如果桶中元素为树形节点
                    else if (e instanceof TreeNode)
                        // 将树仓中的节点拆分为上下树仓，如果太小，则取消树仓。仅从resize调用;
                        // 红黑树这后面专门分析
                        ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        //是链表结构，且后面有节点，进行链表复制
                        //它并没有重新计算元素在数组中的位置
                        //而是采用了原始位置加原数组长度的方法计算得到位置


                        //位置没有变化的，放到lo
                        Node<K, V> loHead = null, loTail = null;

                        //位置发生变化的，当到hi
                        Node<K, V> hiHead = null, hiTail = null;
                        //下一个节点
                        Node<K, V> next;
                        do {
                            //下一个节点
                            next = e.next;
                            // (e.hash & oldCap) 得到的是 元素的在数组中的位置是否需要移动,示例如下
                            // 示例1：
                            // e.hash=10 0000 1010
                            // oldCap=16 0001 0000
                            //	 &   =0	 0000 0000       比较高位的第一位 0
                            //结论：元素位置在扩容后数组中的位置没有发生改变

                            // 示例2：
                            // e.hash=17 0001 0001
                            // oldCap=16 0001 0000
                            //	 &   =1	 0001 0000      比较高位的第一位   1

                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            } else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        //元素位置在扩容后数组中的位置没有发生改变
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        //元素位置在扩容后数组中的位置发生了改变，新的下标位置是原下标位置+原数组长度
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    /**
     * Replaces all linked nodes in bin at index for given hash unless
     * table is too small, in which case resizes instead.
     * 链表转为红黑树，如果容量不够则扩容
     */
    final void treeifyBin(Node<K, V>[] tab, int hash) {
        int n, index;
        Node<K, V> e;
        //如果表为null 或者表容量小于 最小树化容量64
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            //扩容
            resize();
            //确定表中桶的位置，且不为null
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K, V> hd = null, tl = null;
            do {
                //将链表节点转为红黑树节点
                TreeNode<K, V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     * <p>
     * 将指定映射的所有映射复制到此映射。这些映射将替换当前指定映射中任意键的映射。
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        putMapEntries(m, true);
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * 如果存在，则从此映射中删除指定键的映射。
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public V remove(Object key) {
        Node<K, V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
                null : e.value;
    }

    /**
     * Implements Map.remove and related methods
     * 删除节点
     *
     * @param hash       hash for key
     * @param key        the key
     * @param value      the value to match if matchValue, else ignored
     * @param matchValue if true only remove if value is equal
     * @param movable    if false do not move other nodes while removing
     * @return the node, or null if none
     */
    final Node<K, V> removeNode(int hash, Object key, Object value,
                                boolean matchValue, boolean movable) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, index;
        //表不为空，且定位到桶的位置
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            Node<K, V> node = null, e;
            K k;
            V v;
            //获取到当前节点
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
                //如果存在下一个节点
            else if ((e = p.next) != null) {
                //是红黑树
                if (p instanceof TreeNode)
                    node = ((TreeNode<K, V>) p).getTreeNode(hash, key);
                else {
                    //迭代链表
                    do {
                        if (e.hash == hash &&
                                ((k = e.key) == key ||
                                        (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            //取到键
            if (node != null && (!matchValue || (v = node.value) == value ||
                    (value != null && value.equals(v)))) {
                if (node instanceof TreeNode)
                    //删除树节点
                    ((TreeNode<K, V>) node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    //链表节点置空
                    tab[index] = node.next;
                else
                    //将节点链接到删除节点的下一个
                    p.next = node.next;
                //修改次数+1
                ++modCount;
                //数量-1
                --size;
                //回调
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     * 从该映射中删除所有映射。这个调用返回后映射将为空。
     */
    public void clear() {
        Node<K, V>[] tab;
        modCount++;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     * <p>
     * 如果此映射将一个或多个键映射到指定值，则返回true。
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     * specified value
     */
    public boolean containsValue(Object value) {
        Node<K, V>[] tab;
        V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K, V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                            (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }
}
```

## 六、红黑树部分

一篇好文章: https://www.cnblogs.com/mfrank/p/9227097.html

```java
/**
 * Entry for Tree bins. Extends LinkedHashMap.Entry (which in turn
 * extends Node) so can be used as extension of either regular or
 * linked node.
 * 树形桶，继承自LinkedHashMap.Entry（继承自Node），所以能用来做基础的链表操作。
 */
static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {
    // 父节点
    TreeNode<K, V> parent;  // red-black tree
    // 左节点
    TreeNode<K, V> left;
    // 右节点
    TreeNode<K, V> right;
    // 删除后需要断开next链接
    TreeNode<K, V> prev;    // needed to unlink next upon deletion
    //是否为红节点
    boolean red;

    TreeNode(int hash, K key, V val, Node<K, V> next) {
        super(hash, key, val, next);
    }

    /**
     * Returns root of tree containing this node.
     * 返回包含此节点的树的根。
     */
    final TreeNode<K, V> root() {
        //向上迭代，获取到根节点，根节点的父节点为null
        for (TreeNode<K, V> r = this, p; ; ) {
            if ((p = r.parent) == null)
                return r;
            r = p;
        }
    }

    /**
     * Ensures that the given root is the first node of its bin.
     * 确保给定根是其桶的第一个节点。
     * 将根节点转移到树形桶的第一个位置
     */
    static <K, V> void moveRootToFront(Node<K, V>[] tab, TreeNode<K, V> root) {
        int n;
        //root不为空，表不为空
        if (root != null && tab != null && (n = tab.length) > 0) {
            //root在表中的位置（相当于取模）
            int index = (n - 1) & root.hash;
            //获取树形桶第一个节点
            TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
            //如果root引用不是第一个节点
            if (root != first) {
                Node<K, V> rn;
                //root放在第一个索引位置
                tab[index] = root;
                //根节点的前驱
                TreeNode<K, V> rp = root.prev;
                //如果根节点的后继不为空
                if ((rn = root.next) != null)
                    //跟节点后继的前驱指向根节点的前驱
                    ((TreeNode<K, V>) rn).prev = rp;
                //根节点的前驱不为空
                if (rp != null)
                    //根节点后继直接指向前驱
                    rp.next = rn;
                //如果树形桶第一个节点不为空
                if (first != null)
                    //第一个节点的前驱指向根节点
                    first.prev = root;
                //根节点的后继指向first
                root.next = first;
                //根节点的前驱置为null
                root.prev = null;
            }
            assert checkInvariants(root);
        }
    }

    /**
     * Finds the node starting at root p with the given hash and key.
     * The kc argument caches comparableClassFor(key) upon first use
     * comparing keys.
     * 使用给定的散列和键从根p开始查找节点。kc参数在第一次使用比较键时缓存comparableClassFor(key)。
     */
    final TreeNode<K, V> find(int h, Object k, Class<?> kc) {
        TreeNode<K, V> p = this;
        //只要p不为null就一直循环
        do {
            int ph, dir;
            K pk;
            TreeNode<K, V> pl = p.left, pr = p.right, q;
            //如果p的hash 大于 h
            if ((ph = p.hash) > h)
                //向左找
                p = pl;
            else if (ph < h)
                //如果p的hash 大于 h，向右找
                p = pr;
            else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                //找到了，则返回p
                return p;
            else if (pl == null)
                //左边全都遍历结束，找右边的
                p = pr;
            else if (pr == null)
                //右边全都遍历结束，找左边的
                p = pl;
            else if ((kc != null || (kc = comparableClassFor(k)) != null) && (dir = compareComparables(kc, k, pk)) != 0)
                p = (dir < 0) ? pl : pr;
            else if ((q = pr.find(h, k, kc)) != null) //递归调用
                return q;
            else
                p = pl;
        } while (p != null);
        //没找到返回null
        return null;
    }

    /**
     * Calls find for root node.
     * 以根节点调用find。
     */
    final TreeNode<K, V> getTreeNode(int h, Object k) {
        return ((parent != null) ? root() : this).find(h, k, null);
    }

    /**
     * Tie-breaking utility for ordering insertions when equal
     * hashCodes and non-comparable. We don't require a total
     * order, just a consistent insertion rule to maintain
     * equivalence across rebalancings. Tie-breaking further than
     * necessary simplifies testing a bit.
     * 当哈希码相等且不可比较时，用于排序插入的断开连接实用程序。
     * 我们不需要一个总顺序，只需要一个一致的插入规则来在重新平衡之间保持等价。
     * Tie-breaking更能简化测试。
     */
    static int tieBreakOrder(Object a, Object b) {
        //返回与默认方法hashCode()返回的相同的给定对象的散列代码，无论给定对象的类是否覆盖hashCode()。
        int d;
        if (a == null || b == null ||
                (d = a.getClass().getName().
                        compareTo(b.getClass().getName())) == 0)
            d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                    -1 : 1);
        return d;
    }

    /**
     * Forms tree of the nodes linked from this node.
     * 将链表节点转为树节点
     *
     * @return root of tree 树的根
     */
    final void treeify(Node<K, V>[] tab) {
        TreeNode<K, V> root = null;
        for (TreeNode<K, V> x = this, next; x != null; x = next) {
            //x节点的后继
            next = (TreeNode<K, V>) x.next;
            x.left = x.right = null;

            //如果根节点为空
            if (root == null) {
                //初始化根节点（黑色）
                x.parent = null;
                x.red = false;
                root = x;
            } else {
                //如果根节点非空
                K k = x.key;
                int h = x.hash;
                Class<?> kc = null;

                for (TreeNode<K, V> p = root; ; ) {
                    int dir, ph;
                    K pk = p.key;
                    //找到插入的位置
                    if ((ph = p.hash) > h)
                        dir = -1;
                    else if (ph < h)
                        dir = 1;
                    else if ((kc == null &&
                            (kc = comparableClassFor(k)) == null) ||
                            (dir = compareComparables(kc, k, pk)) == 0)
                        dir = tieBreakOrder(k, pk);

                    TreeNode<K, V> xp = p;
                    if ((p = (dir <= 0) ? p.left : p.right) == null) {
                        x.parent = xp;
                        if (dir <= 0)
                            //插入到左节点
                            xp.left = x;
                        else
                            //插入到右节点
                            xp.right = x;
                        //平衡插入，使红黑树保持其性质
                        root = balanceInsertion(root, x);
                        break;
                    }
                }
            }
        }
        //将根节点转移到树形桶的第一个位置
        moveRootToFront(tab, root);
    }

    /**
     * Returns a list of non-TreeNodes replacing those linked from
     * this node.
     * 将树转为链表
     *
     */
    final Node<K, V> untreeify(HashMap<K, V> map) {
        Node<K, V> hd = null, tl = null;
        for (Node<K, V> q = this; q != null; q = q.next) {
            Node<K, V> p = map.replacementNode(q, null);
            if (tl == null)
                hd = p;
            else
                tl.next = p;
            tl = p;
        }
        return hd;
    }

    /**
     * Tree version of putVal.
     * 树版本的插入
     */
    final TreeNode<K, V> putTreeVal(HashMap<K, V> map, Node<K, V>[] tab,
                                    int h, K k, V v) {
        Class<?> kc = null;
        boolean searched = false;
        //返回包含此节点的树的根
        TreeNode<K, V> root = (parent != null) ? root() : this;
        for (TreeNode<K, V> p = root; ; ) {
            int dir, ph;
            K pk;
            //找到待插入的位置
            if ((ph = p.hash) > h)
                dir = -1;
            else if (ph < h)
                dir = 1;
            else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                //返回插入的节点
                return p;
            else if ((kc == null &&
                    (kc = comparableClassFor(k)) == null) ||
                    (dir = compareComparables(kc, k, pk)) == 0) {
                if (!searched) {
                    TreeNode<K, V> q, ch;
                    searched = true;
                    if (((ch = p.left) != null &&
                            (q = ch.find(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                                    (q = ch.find(h, k, kc)) != null))
                        return q;
                }
                dir = tieBreakOrder(k, pk);
            }
            //判断插到哪个位置
            TreeNode<K, V> xp = p;
            if ((p = (dir <= 0) ? p.left : p.right) == null) {
                Node<K, V> xpn = xp.next;
                TreeNode<K, V> x = map.newTreeNode(h, k, v, xpn);
                if (dir <= 0)
                    xp.left = x;
                else
                    xp.right = x;
                xp.next = x;
                x.parent = x.prev = xp;
                if (xpn != null)
                    ((TreeNode<K, V>) xpn).prev = x;
                //将根节点转移到树形桶的第一个位置，平衡插入的节点
                moveRootToFront(tab, balanceInsertion(root, x));
                return null;
            }
        }
    }

    /**
     * Removes the given node, that must be present before this call.
     * This is messier than typical red-black deletion code because we
     * cannot swap the contents of an interior node with a leaf
     * successor that is pinned by "next" pointers that are accessible
     * independently during traversal. So instead we swap the tree
     * linkages. If the current tree appears to have too few nodes,
     * the bin is converted back to a plain bin. (The test triggers
     * somewhere between 2 and 6 nodes, depending on tree structure).
     *
     * 移除此调用之前必须存在的给定节点。
     * 这比典型的红黑删除代码更混乱，因为我们不能使用由“next”指针固定的叶子继承器来交换内部节点的内容，
     * 而“next”指针在遍历过程中是独立可访问的。所以我们交换树的连杆。
     * 如果当前树的节点似乎太少，则将该bin转换回普通bin。
     * （测试根据树结构触发2到6个节点）
     *
     */
    final void removeTreeNode(HashMap<K, V> map, Node<K, V>[] tab,
                              boolean movable) {
        int n;
        if (tab == null || (n = tab.length) == 0)
            return;
        int index = (n - 1) & hash;
        TreeNode<K, V> first = (TreeNode<K, V>) tab[index], root = first, rl;
        TreeNode<K, V> succ = (TreeNode<K, V>) next, pred = prev;
        if (pred == null)
            tab[index] = first = succ;
        else
            pred.next = succ;
        if (succ != null)
            succ.prev = pred;
        if (first == null)
            return;
        if (root.parent != null)
            root = root.root();
        if (root == null || root.right == null ||
                (rl = root.left) == null || rl.left == null) {
            //转为链表
            tab[index] = first.untreeify(map);  // too small
            return;
        }
        TreeNode<K, V> p = this, pl = left, pr = right, replacement;
        if (pl != null && pr != null) {
            TreeNode<K, V> s = pr, sl;
            while ((sl = s.left) != null) // find successor
                s = sl;
            boolean c = s.red;
            s.red = p.red;
            p.red = c; // swap colors
            TreeNode<K, V> sr = s.right;
            TreeNode<K, V> pp = p.parent;
            if (s == pr) { // p was s's direct parent
                p.parent = s;
                s.right = p;
            } else {
                TreeNode<K, V> sp = s.parent;
                if ((p.parent = sp) != null) {
                    if (s == sp.left)
                        sp.left = p;
                    else
                        sp.right = p;
                }
                if ((s.right = pr) != null)
                    pr.parent = s;
            }
            p.left = null;
            if ((p.right = sr) != null)
                sr.parent = p;
            if ((s.left = pl) != null)
                pl.parent = s;
            if ((s.parent = pp) == null)
                root = s;
            else if (p == pp.left)
                pp.left = s;
            else
                pp.right = s;
            if (sr != null)
                replacement = sr;
            else
                replacement = p;
        } else if (pl != null)
            replacement = pl;
        else if (pr != null)
            replacement = pr;
        else
            replacement = p;
        if (replacement != p) {
            TreeNode<K, V> pp = replacement.parent = p.parent;
            if (pp == null)
                root = replacement;
            else if (p == pp.left)
                pp.left = replacement;
            else
                pp.right = replacement;
            p.left = p.right = p.parent = null;
        }
        //平衡删除
        TreeNode<K, V> r = p.red ? root : balanceDeletion(root, replacement);

        if (replacement == p) {  // detach
            TreeNode<K, V> pp = p.parent;
            p.parent = null;
            if (pp != null) {
                if (p == pp.left)
                    pp.left = null;
                else if (p == pp.right)
                    pp.right = null;
            }
        }
        if (movable)
            moveRootToFront(tab, r);
    }

    /**
     * Splits nodes in a tree bin into lower and upper tree bins,
     * or untreeifies if now too small. Called only from resize;
     * see above discussion about split bits and indices.
     *
     * 将树仓中的节点拆分为上下树仓，如果太小，则取消树仓
     * 仅从resize调用;参见上面关于分割位和索引的讨论
     * @param map   the map
     * @param tab   the table for recording bin heads
     * @param index the index of the table being split
     * @param bit   the bit of hash to split on
     */
    final void split(HashMap<K, V> map, Node<K, V>[] tab, int index, int bit) {
        TreeNode<K, V> b = this;
        // Relink into lo and hi lists, preserving order
        TreeNode<K, V> loHead = null, loTail = null;
        TreeNode<K, V> hiHead = null, hiTail = null;
        int lc = 0, hc = 0;
        for (TreeNode<K, V> e = b, next; e != null; e = next) {
            next = (TreeNode<K, V>) e.next;
            e.next = null;
            if ((e.hash & bit) == 0) {
                if ((e.prev = loTail) == null)
                    loHead = e;
                else
                    loTail.next = e;
                loTail = e;
                ++lc;
            } else {
                if ((e.prev = hiTail) == null)
                    hiHead = e;
                else
                    hiTail.next = e;
                hiTail = e;
                ++hc;
            }
        }

        if (loHead != null) {
            if (lc <= UNTREEIFY_THRESHOLD)
                //变成链表
                tab[index] = loHead.untreeify(map);
            else {
                tab[index] = loHead;
                if (hiHead != null) // (else is already treeified)
                    loHead.treeify(tab);
            }
        }
        if (hiHead != null) {
            if (hc <= UNTREEIFY_THRESHOLD)
                //变成链表
                tab[index + bit] = hiHead.untreeify(map);
            else {
                tab[index + bit] = hiHead;
                if (loHead != null)
                    hiHead.treeify(tab);
            }
        }
    }

    /* ------------------------------------------------------------ */
    // Red-black tree methods, all adapted from CLR


    //树的左旋
    static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root,
                                            TreeNode<K, V> p) {
        TreeNode<K, V> r, pp, rl;
        if (p != null && (r = p.right) != null) {
            if ((rl = p.right = r.left) != null)
                rl.parent = p;
            if ((pp = r.parent = p.parent) == null)
                (root = r).red = false;
            else if (pp.left == p)
                pp.left = r;
            else
                pp.right = r;
            r.left = p;
            p.parent = r;
        }
        return root;
    }

    //右旋
    static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root,
                                             TreeNode<K, V> p) {
        TreeNode<K, V> l, pp, lr;
        if (p != null && (l = p.left) != null) {
            if ((lr = p.left = l.right) != null)
                lr.parent = p;
            if ((pp = l.parent = p.parent) == null)
                (root = l).red = false;
            else if (pp.right == p)
                pp.right = l;
            else
                pp.left = l;
            l.right = p;
            p.parent = l;
        }
        return root;
    }

    /**
     * Balance insertion tree node.
     * 平衡插入后的红黑树
     *
     * @param <K>  键的类型
     * @param <V>  值的类型
     * @param root 根节点
     * @param x    待插入节点
     * @return 返回根节点
     */
    static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
        //将插入的节点置为红色
        x.red = true;
        //xp 待插入节点的父节点
        //xpp 待插入节点的祖节点
        //xppl 祖节点的左孩子，左叔叔
        for (TreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
            //  待插入节点的父节点为空，则当前插入的节点为根节点
            if ((xp = x.parent) == null) {
                //置为黑色
                x.red = false;
                return x;
                //如果父节点是黑色的，或者不存在祖节点
            } else if (!xp.red || (xpp = xp.parent) == null)
                //返回根节点
                return root;
            //父节点是祖节点的左孩子
            if (xp == (xppl = xpp.left)) {
                if ((xppr = xpp.right) != null && xppr.red) {
                    xppr.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                } else {
                    if (x == xp.right) {

                        root = rotateLeft(root, x = xp);
                        xpp = (xp = x.parent) == null ? null : xp.parent;
                    }
                    if (xp != null) {
                        xp.red = false;
                        if (xpp != null) {
                            xpp.red = true;
                            root = rotateRight(root, xpp);
                        }
                    }
                }
            } else {
                //父节点是祖节点的右孩子
                if (xppl != null && xppl.red) {
                    xppl.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                } else {
                    if (x == xp.left) {
                        root = rotateRight(root, x = xp);
                        xpp = (xp = x.parent) == null ? null : xp.parent;
                    }
                    if (xp != null) {
                        xp.red = false;
                        if (xpp != null) {
                            xpp.red = true;
                            root = rotateLeft(root, xpp);
                        }
                    }
                }
            }
        }
    }

    //看的我脑壳疼，穷举出所有删除后影响的情况，一一解决
    static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root,
                                                 TreeNode<K, V> x) {
        for (TreeNode<K, V> xp, xpl, xpr; ; ) {
            if (x == null || x == root)
                return root;
            else if ((xp = x.parent) == null) {
                x.red = false;
                return x;
            } else if (x.red) {
                x.red = false;
                return root;
            } else if ((xpl = xp.left) == x) {
                if ((xpr = xp.right) != null && xpr.red) {
                    xpr.red = false;
                    xp.red = true;
                    root = rotateLeft(root, xp);
                    xpr = (xp = x.parent) == null ? null : xp.right;
                }
                if (xpr == null)
                    x = xp;
                else {
                    TreeNode<K, V> sl = xpr.left, sr = xpr.right;
                    if ((sr == null || !sr.red) &&
                            (sl == null || !sl.red)) {
                        xpr.red = true;
                        x = xp;
                    } else {
                        if (sr == null || !sr.red) {
                            if (sl != null)
                                sl.red = false;
                            xpr.red = true;
                            root = rotateRight(root, xpr);
                            xpr = (xp = x.parent) == null ?
                                    null : xp.right;
                        }
                        if (xpr != null) {
                            xpr.red = (xp == null) ? false : xp.red;
                            if ((sr = xpr.right) != null)
                                sr.red = false;
                        }
                        if (xp != null) {
                            xp.red = false;
                            root = rotateLeft(root, xp);
                        }
                        x = root;
                    }
                }
            } else { // symmetric
                if (xpl != null && xpl.red) {
                    xpl.red = false;
                    xp.red = true;
                    root = rotateRight(root, xp);
                    xpl = (xp = x.parent) == null ? null : xp.left;
                }
                if (xpl == null)
                    x = xp;
                else {
                    TreeNode<K, V> sl = xpl.left, sr = xpl.right;
                    if ((sl == null || !sl.red) &&
                            (sr == null || !sr.red)) {
                        xpl.red = true;
                        x = xp;
                    } else {
                        if (sl == null || !sl.red) {
                            if (sr != null)
                                sr.red = false;
                            xpl.red = true;
                            root = rotateLeft(root, xpl);
                            xpl = (xp = x.parent) == null ?
                                    null : xp.left;
                        }
                        if (xpl != null) {
                            xpl.red = (xp == null) ? false : xp.red;
                            if ((sl = xpl.left) != null)
                                sl.red = false;
                        }
                        if (xp != null) {
                            xp.red = false;
                            root = rotateRight(root, xp);
                        }
                        x = root;
                    }
                }
            }
        }
    }

    /**
     * Recursive invariant check
     * 递归 不变量 检查
     */
    static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
        TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right,
                tb = t.prev, tn = (TreeNode<K, V>) t.next;
        if (tb != null && tb.next != t)
            return false;
        if (tn != null && tn.prev != t)
            return false;
        if (tp != null && t != tp.left && t != tp.right)
            return false;
        if (tl != null && (tl.parent != t || tl.hash > t.hash))
            return false;
        if (tr != null && (tr.parent != t || tr.hash < t.hash))
            return false;
        if (t.red && tl != null && tl.red && tr != null && tr.red)
            return false;
        if (tl != null && !checkInvariants(tl))
            return false;
        if (tr != null && !checkInvariants(tr))
            return false;
        return true;
    }
}
```



