## 集合(Set)和映射(Map)基础总结

 - `Set`介绍
 - 基于链表实现`Set`
 - 基于二叉搜索树实现`Set`
 - 基于二叉平衡树实现`Set`
 - 使用`Set`解决LeetCode-804. Unique Morse Code Words
 - 使用`Set`解决LeetCode-349. Intersection of Two Arrays
 - `Map`介绍
 - 基于链表实现`Map`
 - 基于二叉搜索树实现`Map`
 - 基于二叉平衡树实现`Map`
 - 使用`Map`解决LeetCode-350. Intersection of Two Arrays II
 - `Set`和`Map`的一些对比和总结

***
### `Set`介绍

 - `Set`是不允许重复的集合容器；
 - `Set`可以分为有序集合和无需集合；
 - 有序集合基于搜索树实现，JDK底层是红黑树，即`TreeSet`；
 - 无序集合基于`Hash`表实现，JDK底层是`HashMap`包装之后，即`HashSet`；

***
### 基于链表实现`Set`
首先看一下[单链表的实现](https://blog.csdn.net/zxzxzx0119/article/details/79811308)，下面的实现中使用`SingleList`实现: 
先看`Set`接口中需要实现的方法: 

```java
/**
 * Set接口
 * @param <E>
 */
public interface Set<E> {
    void add(E e);
    boolean contains(E e);
    void remove(E e);
    int getSize();
    boolean isEmpty();
}
```
如果有单链表的相关方法，其中的`Set`就很容易实现，唯一要注意的就是 : 在添加元素的时候要注意先判断集合中有没有这个元素: 

```java
/**
 * 基于单链表实现 Set
 * @param <E>
 */
public class LinkedListSet<E> implements Set<E> {

    private SingleList<E> list;

    public LinkedListSet(){
        list = new SingleList<>();
    }

    @Override
    public int getSize(){
        return list.size();
    }

    @Override
    public boolean isEmpty(){
        return list.isEmpty();
    }

    @Override
    public void add(E e){
        if(!list.contains(e)) //这个就是效率低的原因
            list.addFirst(e);
    }

    @Override
    public boolean contains(E e){
        return list.contains(e);
    }

    @Override
    public void remove(E e){
        list.removeElement(e);
    }

}
```
***
### 基于二叉搜索树实现`Set`
先看一下[二叉搜索树](https://blog.csdn.net/zxzxzx0119/article/details/80012374)相关方法的实现。注意由于我实现的二叉搜索树没有重复元素，所有`Set`也没有重复元素。
```java
/**
 * 基于二叉搜索树实现的Set  类似JDK的TreeSet (有序集合)   而HashSet是无序集合
 * @param <E>
 */
public class BSTSet<E extends Comparable<E>> implements Set<E> {

    private BSTree<E> bst;

    public BSTSet(){
        bst = new BSTree<>();
    }

    @Override
    public int getSize(){
        return bst.size();
    }

    @Override
    public boolean isEmpty(){
        return bst.isEmpty();
    }

    @Override
    public void add(E e){
        bst.add(e);
    }

    @Override
    public boolean contains(E e){
        return bst.contains(e);
    }

    @Override
    public void remove(E e){
        bst.remove(e);
    }
}
```
***
### 基于二叉平衡树实现Set
在这之前先看一下[二叉平衡树](https://blog.csdn.net/zxzxzx0119/article/details/80012812)的总结和代码实现。
```java
public class AVLSet<E extends Comparable<E>> implements Set<E> {

    private AVLTree<E, Object> avl;

    public AVLSet(){
        avl = new AVLTree<>();
    }

    @Override
    public int getSize(){
        return avl.size();
    }

    @Override
    public boolean isEmpty(){
        return avl.isEmpty();
    }

    @Override
    public void add(E e){
        avl.add(e, null);
    }

    @Override
    public boolean contains(E e){
        return avl.contains(e);
    }

    @Override
    public void remove(E e){
        avl.remove(e);
    }
}
```

***
### 使用Set解决LeetCode-804. Unique Morse Code Words
#### [题目链接](https://leetcode.com/problems/unique-morse-code-words/description/)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181220225038289.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

很简单的题目，转换成对应的解码，然后存到一个不能重复的`set`集合中，返回集合中的元素个数即可；

```java
   public int uniqueMorseRepresentations(String[] words) {
        if (words == null || words.length == 0) {
            return 0;
        }
        String[] dict = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--.."};
        HashSet<String>set = new HashSet<>();
        for(String word : words){
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < word.length(); i++){
                sb.append(dict[word.charAt(i) - 'a']);
            } 
            set.add(sb.toString());
        }
        return set.size();
    }
```

***
### 使用Set解决LeetCode-349. Intersection of Two Arrays
#### [题目链接](https://leetcode.com/problems/intersection-of-two-arrays/description/)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181220225119734.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

解题思路: 我们可以使用一个`set`一开始去除`nums1`中的重复元素，遍历`nums2`中的元素，判断每个元素在`nums1`中是否出现，为了不重复，<font color = red>记录一个之后要在`set`中删除这个</font>。

```java
   public int[] intersection(int[] nums1, int[] nums2) {
        HashSet<Integer>set = new HashSet<>();
        for(int num : nums1) set.add(num);
        
        ArrayList<Integer>list = new ArrayList<>();
        for(int num : nums2) {
            if(set.contains(num)){
                list.add(num);
                set.remove(num);// important
            }
        }
        int[] res = new int[list.size()];
        for(int i = 0 ; i < list.size() ; i ++)
            res[i] = list.get(i);
        return res;
    }
```
另外这个题目基于双指针和二分`Ologn`的实现也不难，可以看下[这里](https://leetcode.com/problems/intersection-of-two-arrays/discuss/81969/Three-Java-Solutions)。</font>
***
### `Map`介绍

 * `Map`的键不允许重复，如果重复插入键相同的，则新的`value`覆盖原来的`value`；
 * `Map`可以分为有序`Map`和无序`Map`；
 * 有序`Map`基于搜索树实现，JDK底层使用红黑树实现，即`TreeMap`；
 * 无序`Map`基于`Hash`表实现，JDK底层使用`Hash`表底层实现，即`HashMap`；

***
### 基于链表实现Map
和普通的单链表不同的: 

 - 结点内部是`key,value`的键值对；
 - `getNode()`，返回`key`对应的结点，方便操作；
 - 在`add()`操作的时候，如果已经存在`key`对应的结点，就更新`value`即可；

```java
public class LinkedListMap<K, V> implements Map<K, V> {

    //结点结构
    private class Node {
        public K key;
        public V value;
        public Node next;

        public Node(K key, V value, Node next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public Node(K key, V value) {
            this(key, value, null);
        }

        public Node() {
            this(null, null, null);
        }

        @Override
        public String toString() {
            return key.toString() + " : " + value.toString();
        }
    }

    private Node dummyHead;
    private int size;

    public LinkedListMap() {
        dummyHead = new Node();
        size = 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    
    // important
    private Node getNode(K key) {
        Node cur = dummyHead.next;
        while (cur != null) {
            if (cur.key.equals(key))
                return cur;
            cur = cur.next;
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public void add(K key, V value) {
        Node node = getNode(key);
        if (node == null) {
            /**Node newNode = new Node(key,value);
            newNode.next = dummyHead.next;
            dummyHead.next = newNode;*/
            dummyHead.next = new Node(key, value, dummyHead.next);// == 上面三行
            size++;
        } else //already exist
            node.value = value;
    }

    @Override
    public void set(K key, V newValue) {
        Node node = getNode(key);
        if (node == null)
            throw new IllegalArgumentException(key + " doesn't exist!");

        node.value = newValue;
    }

    @Override
    public V remove(K key) {

        Node prev = dummyHead;
        while (prev.next != null) {
            if (prev.next.key.equals(key))
                break;
            prev = prev.next;
        }

        if (prev.next != null) {
            Node delNode = prev.next;
            prev.next = delNode.next;
            delNode.next = null;
            size--;
            return delNode.value;
        }

        return null;
    }
}
```
***
### 基于二叉搜索树实现Map
和[二叉搜索树](https://blog.csdn.net/zxzxzx0119/article/details/80012374)中的操作差不多，注意: 

 - 在添加的时候，如果已经存在，也就是`key`相等的话就直接更新`value`即可；
 - 增加`getNode()`方法: 返回以`node`为根节点的二分搜索树中，`key`所在的节点；
 - 大部分对于`e`(也就是结点值)的操作，就是对`key`的操作；

```java
public class BSTMap<K extends Comparable<K>, V> implements Map<K, V> {

    private class Node {
        public K key;
        public V value;
        public Node left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            left = null;
            right = null;
        }
    }

    private Node root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // 向二分搜索树中添加新的元素(key, value)
    @Override
    public void add(K key, V value) {//相当于JDK的put操作
        root = add(root, key, value);
    }

    // 向以node为根的二分搜索树中插入元素(key, value)，递归算法
    // 返回插入新节点后二分搜索树的根
    private Node add(Node node, K key, V value) {

        if (node == null) {
            size++;
            return new Node(key, value);
        }

        if (key.compareTo(node.key) < 0)
            node.left = add(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = add(node.right, key, value);
        else   // key.compareTo(node.key) == 0
            node.value = value;

        return node;
    }

    // 返回以node为根节点的二分搜索树中，key所在的节点
    private Node getNode(Node node, K key) {

        if (node == null)
            return null;

        if (key.equals(node.key))
            return node;
        else if (key.compareTo(node.key) < 0)
            return getNode(node.left, key);
        else // if(key.compareTo(node.key) > 0)
            return getNode(node.right, key);
    }

    @Override
    public boolean contains(K key) {
        return getNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(root, key);
        return node == null ? null : node.value;
    }

    @Override
    public void set(K key, V newValue) {
        Node node = getNode(root, key);
        if (node == null)
            throw new IllegalArgumentException(key + " doesn't exist!");

        node.value = newValue;
    }

    // 返回以node为根的二分搜索树的最小值所在的节点
    private Node minimum(Node node) {
        if (node.left == null)
            return node;
        return minimum(node.left);
    }

    // 删除掉以node为根的二分搜索树中的最小节点
    // 返回删除节点后新的二分搜索树的根
    private Node removeMin(Node node) {
        if (node.left == null) {
            Node rightNode = node.right;
            node.right = null;
            size--;
            return rightNode;
        }
        node.left = removeMin(node.left);
        return node;
    }

    // 从二分搜索树中删除键为key的节点
    @Override
    public V remove(K key) {

        Node node = getNode(root, key);
        if (node != null) {
            root = remove(root, key);
            return node.value;
        }
        return null;
    }

    private Node remove(Node node, K key) {

        if (node == null)
            return null;

        if (key.compareTo(node.key) < 0) {
            node.left = remove(node.left, key);
            return node;
        } else if (key.compareTo(node.key) > 0) {
            node.right = remove(node.right, key);
            return node;
        } else {   // key.compareTo(node.key) == 0

            // 待删除节点左子树为空的情况
            if (node.left == null) {
                Node rightNode = node.right;
                node.right = null;
                size--;
                return rightNode;
            }

            // 待删除节点右子树为空的情况
            if (node.right == null) {
                Node leftNode = node.left;
                node.left = null;
                size--;
                return leftNode;
            }

            /** 待删除节点左右子树均不为空的情况
             找到比待删除节点大的最小节点, 即待删除节点右子树的最小节点
             用这个节点顶替待删除节点的位置*/
            Node successor = minimum(node.right);
            successor.right = removeMin(node.right);
            successor.left = node.left;

            node.left = node.right = null;

            return successor;
        }
    }
}
```
***
### 基于二叉平衡树实现Map
在这之前先看[平衡二叉树](https://blog.csdn.net/zxzxzx0119/article/details/80012812)的原理和实现。
```java
public class AVLMap<K extends Comparable<K>, V> implements Map<K, V> {

    private AVLTree<K, V> avl;

    public AVLMap(){
        avl = new AVLTree<>();
    }

    @Override
    public int getSize(){
        return avl.size();
    }

    @Override
    public boolean isEmpty(){
        return avl.isEmpty();
    }

    @Override
    public void add(K key, V value){
        avl.add(key, value);
    }

    @Override
    public boolean contains(K key){
        return avl.contains(key);
    }

    @Override
    public V get(K key){
        return avl.get(key);
    }

    @Override
    public void set(K key, V newValue){
        avl.set(key, newValue);
    }

    @Override
    public V remove(K key){
        return avl.remove(key);
    }
}
```

***
### 使用Map解决LeetCode-350. Intersection of Two Arrays II
#### [题目链接](https://leetcode.com/problems/intersection-of-two-arrays-ii/description/)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20181220225410690.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)

解题思路： 也很简单，只需要用`map`记录一下次数。然后在`map`中更新维护次数即可。

```java
 public int[] intersect(int[] nums1, int[] nums2) {
        HashMap<Integer,Integer>map = new HashMap<>();
        
        
        for(int num : nums1){
            if(!map.containsKey(num)){
                map.put(num,1);
            }else {
                map.put(num,map.get(num) + 1);
            }
        }
        
        ArrayList<Integer>list = new ArrayList<>();
        for(int num : nums2){
            if(map.containsKey(num)){
                list.add(num);
                map.put(num,map.get(num) - 1);
                if(map.get(num) == 0)map.remove(num);
            }
        }
        
        int[] res = new int[list.size()];
        for(int i = 0; i < list.size(); i++) res[i] = list.get(i);
        return res;
    }
```
### Set和Map的一些对比和总结
* 集合`Set`和映射`Map`有很多的相似之处，实现都可以使用链表和搜索树实现；
* 其中JDK的`HashSet`也是`HashMap`包装之后的，虽然用的是`Hash`表。
* **使用搜索树实现的`Set`和`Map`平均时间复杂度为Ologn(Ologh)(Olog<sub>2</sub>n)，但是使用链表是`O(n)`；**

|`Set< E >`|`Map<K，V>`|
|-|-|
|`void add(E)`|`void add(K, V)`|
|`void remove(E)`|`V remove(K)`|
|`boolean contains(E)`|`boolean contains(K)`|
|`int getSize()`|`int getSize()`|
|`boolean isEmpty()`|`boolean isEmpty()`|
||`V get(K)`|
||`void set(K, V)`|


