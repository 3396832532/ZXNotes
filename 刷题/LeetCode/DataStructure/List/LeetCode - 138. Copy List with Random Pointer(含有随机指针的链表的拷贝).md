## LeetCode - 138. Copy List with Random Pointer(含有随机指针的链表的拷贝)
 - 方法一 : 使用HashMap保存
 - 方法二 : O(1)的空间复杂度
***
#### [题目链接](https://leetcode.com/problems/copy-list-with-random-pointer/description/)

> https://leetcode.com/problems/copy-list-with-random-pointer/description/

#### 题意
![1554790494580](assets/1554790494580.png)

链表结构:

```java
class Node {
    public int val;
    public Node next;
    public Node random;

    public Node() {}

    public Node(int _val,Node _next,Node _random) {
        val = _val;
        next = _next;
        random = _random;
    }
};
```

## 方法一、使用HashMap保存

方法:

* 从左到右遍历链表，对每个结点都复制生成相应的**副本结点**，然后将对应的关系(之前的结点和新的副本结点)放入哈希表中；
* 然后从左到右设置每一个副本结点的`next`和`random`指针，即找到原先`cur`的`next`和`random`的拷贝(从`Map`中获取)；
* 最后返回副本结点的头结点(`map.get(head)`)即可；

看一个例子: 

例如: 原链表 `1->2->3->null`，假设 `1 `的 `rand `指针指向 `3`，`2` 的 `rand` 指针指向 `null`，`3`的` rand `指针指向` 1`。

<div align="center"><img src="assets/1554791775847.png"></div><br>

遍历到节点` 1` 时，可以从 `map `中得到节点` 1` 的副本节点`1’`，节点 `1` 的`next `指向节点 `2`，所以从 `map `中得到节点 `2` 的副本节点` 2`，然后令 `1’.next=2'`，副本节点的 `next` 指针就设置好了。同时节点 `1`的 `rand `指向节点` 3`，所以从` map` 中得到节点` 3` 的副本节点 `3`，然后令 `1‘.rand=3'`，副本节点`1`的 `rand` 指针也设置好了。

以这种方式可以设置每一个副本节点的 `next` 与` rand` 指针。

```java
class Solution {

    //普通的使用一个HashMap的额外空间为O(n)的方法
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        HashMap<Node, Node> map = new HashMap<>();

        Node cur = head;
        while (cur != null) {
            map.put(cur, new Node(cur.val, null, null));
            cur = cur.next;
        }
        cur = head;
        while (cur != null) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).random = map.get(cur.random);
            cur = cur.next;
        }
        return map.get(head);
    }
}
```

方法一的另一种写法

思路:

 * 方法一的写法是第一次存储每个结点的时候没有直接找到拷贝结点的`next`域结点；
 * 这个方法是在拷贝原结点的时候，顺便拷贝了结点的`next`域，拷贝完`next`域之后，最后就只要拷贝`random`域了；
 * 注意这里使用`cur`指向原链表的`head`，使用`copyCur`指向复制链表的`head`，然后这两个指针同时完成的是两个工作: 先设置好副本拷贝结点的`next`域，然后将对应的原来链表的结点和拷贝的结点`put`进`map`，然后`cur`和`copyCur`都同时向后继续移动一个位置；

代码:

```java
class Solution {

    //使用Map的另一种写法，速度一般
    public Node copyRandomList(Node head) {
        if (head == null)
            return null;
        HashMap<Node, Node> map = new HashMap<>();
        Node copyHead = new Node(head.val, null, null);
        map.put(head, copyHead);

        Node cur = head, copyCur = copyHead;

        while (cur != null) {
            if (cur.next != null)
                copyCur.next = new Node(cur.next.val, null, null);
            map.put(cur.next, copyCur.next);
            cur = cur.next;
            copyCur = copyCur.next;
        }

        cur = head;
        while (cur != null) {
            map.get(cur).random = map.get(cur.random);
            cur = cur.next;
        }
        return copyHead;
    }
}
```
由于链表的天然的递归结构，也可以使用递归的写法: 

```java
class Solution {

    //上一种方法的递归的写法
    public Node copyRandomList(Node head) {
        if (head == null)
            return null;
        HashMap<Node, Node> map = new HashMap<>();
        Node copyHead = rec(head, map);

        Node cur = head;
        while (cur != null) {
            map.get(cur).random = map.get(cur.random);
            cur = cur.next;
        }
        return copyHead;
    }

    // 宏观来看: 就是返回拷贝以node为头结点的链表的拷贝 以及next的拷贝
    private Node rec(Node node, HashMap<Node, Node> map) {
        if (node == null)
            return null;
        Node copyNode = new Node(node.val, null, null);
        map.put(node, copyNode);
        copyNode.next = rec(node.next, map);
        return copyNode;
    }
}
```


## 方法二、O(1)的空间复杂度
这个方法是最好的解决办法，分为三个步骤: 
* 第一个步骤，先从左到右遍历一遍链表，对每个结点`cur`都复制生成相应的副本结点`copy`，然后把副本结点`copy`放在`cur`和下一个要遍历结点的中间；
* 再从左到右遍历一遍链表，在遍历时设置每一个结点的副本结点的`random`指针；
* 设置完`random`指针之后，将链表拆成两个链表，返回第二个链表的头部；

图:

![在这里插入图片描述](images/138_s2.png)

代码:

```java
class Solution {
    //O(1)的空间复杂度
    public Node copyRandomList(Node head) {
        if (head == null) return null;
        Node cur = head, next = null;
        //先拷贝一份原来的链表
        while (cur != null) {
            next = cur.next;  //先存着之前的next
            cur.next = new Node(cur.val, null, null);
            cur.next.next = next;
            cur = next;
        }

        //复制结点的random指针
        cur = head;
        Node copyCur = null;
        while (cur != null) {
            next = cur.next.next; //保存原来链表中的下一个
            copyCur = cur.next; //复制链表的cur
            copyCur.random = cur.random != null ? cur.random.next : null;
            cur = next;
        }

        //拆开两个链表
        Node copyHead = head.next;
        cur = head;
        while (cur != null) {
            next = cur.next.next;
            copyCur = cur.next;
            cur.next = next;
            copyCur.next = next != null ? next.next : null;
            cur = next;
        }
        return copyHead;
    }
}
```

