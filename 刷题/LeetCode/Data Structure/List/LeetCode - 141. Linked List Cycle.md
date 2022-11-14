# LeetCode - 141. Linked List Cycle

#### [题目链接](https://leetcode.com/problems/linked-list-cycle/)

> https://leetcode.com/problems/linked-list-cycle/

#### 题目
![在这里插入图片描述](images/141_t.png)
#### 解析
第一种做法:  使用`HashSet`记录已经走过的节点，如果再次碰到，则有环: 

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null)
            return false;
        HashSet<ListNode> set = new HashSet<>();
        ListNode cur = head;
        while (cur != null) {
            if (set.contains(cur))
                return true;
            set.add(cur);
            cur = cur.next;
        }
        return false;
    }
}
```
题目要求不能使用额外的空间，所以第二种解法: 

* 使用两个指针，一个快指针`fast`一次走两步，慢指针一次走一步；
* 如果快指针`fast`某个时刻走到空了，说明没有环， **因为如果有环，快指针一定不会走到空(慢指针也不会(因为是单链表))**；
* 所以如果`fast`没有走到空，那快指针`fast`和慢指针`slow`就会在环里面打圈，因为`fast`更快，所以一定会追上`slow`，当`fast == slow`的时候就返回`true`就可以了；

图:

<div align="center"><img src="images/141_s.png"></div><br>

代码:

```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null)
            return false;
        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow)
                return true;
        }
        return false;
    }
}
```




