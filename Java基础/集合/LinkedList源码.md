# LinkedList源码

LinkedList还实现了队列接口Queue。

Queue扩展了Collection，它的主要操作有三个：

- 在尾部添加元素 (add, offer)
- 查看头部元素 (element, peek)，返回头部元素，但不改变队列
- 删除头部元素 (remove, poll)，返回头部元素，并且从队列中删除

区别在于，对于特殊情况的处理不同。特殊情况是指，队列为空或者队列为满，为空容易理解，为满是指队列有长度大小限制，而且已经占满了。LinkedList的实现中，队列长度没有限制，但别的Queue的实现可能有。

**在队列为空时，element和remove会抛出异常NoSuchElementException，而peek和poll返回特殊值null，在队列为满时，add会抛出异常IllegalStateException，而offer只是返回false**。

节点内部类:

```java
private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
        this.item = element;
        this.next = next;
        this.prev = prev;
    }
}
```

定义指向头、尾的指针以及一个`size`。

```java
transient int size = 0;

transient Node<E> first;

transient Node<E> last;
```



