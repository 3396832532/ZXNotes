## 剑指Offer - 14 - 链表中倒数第k个结点

#### [题目链接](https://www.nowcoder.com/practice/529d3ae5a407492994ad2a246518148a?tpId=13&tqId=11167&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/529d3ae5a407492994ad2a246518148a?tpId=13&tqId=11167&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个链表，输出该链表中倒数第k个结点。

### 解析

两种思路，一种常规思路，一种双指针。

#### 1)、常规思路

* 先遍历一遍求出链表长度`len`；
* 然后再从头开始走`len - k` 个就可以到倒数`k`个结点；

代码:

```java
public class Solution {
    //常规解法
    public ListNode FindKthToTail(ListNode head, int k) {
        ListNode cur = head;
        int len = 0;
        while (cur != null) {
            cur = cur.next;
            len++;
        }
        if (k > len)
            return null;
        cur = head;
        for (int i = 0; i < len - k; i++)
            cur = cur.next;
        return cur;
    }
}
```

#### 2)、双指针

* 设置两个指针一开始都指向`head`；
* 然后先让第一个指针`first`走`k-1`步，然后两个指针再一起走，当第二个指针`second`走到末尾(`second.next = null`)时，第一个指针`first`就刚好指向倒数第`k`个结点；
* 具体长度关系推一下就清楚了；

![](images/14_s.png)

代码:

```java
public class Solution {

    public ListNode FindKthToTail(ListNode head, int k) {
        if (head == null || k == 0) //注意要特判
            return null;
        ListNode fi = head, se = head;
        for (int i = 1; i <= k - 1; i++) // first 先走k-1步
            fi = fi.next;
        if (fi == null) //k > len 特判
            return null;
        while (fi.next != null) { // 注意是fi.next != null
            fi = fi.next;
            se = se.next;
        }
        return se;
    }
}
```

