## 剑指Offer - 15 - 反转链表

#### [题目链接](https://www.nowcoder.com/practice/75e878df47f24fdc9dc3e400ec6058ca?tpId=13&tqId=11168&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/75e878df47f24fdc9dc3e400ec6058ca?tpId=13&tqId=11168&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个链表，反转链表后，输出新链表的表头。

### 解析

这个题目和[LeetCode206](https://github.com/ZXZxin/ZXNotes/blob/master/%E5%88%B7%E9%A2%98/LeetCode/Data%20Structure/List/LeetCode%20-%20206.%20Reverse%20Linked%20List%E5%8D%95%E9%93%BE%E8%A1%A8%E5%8F%8D%E8%BD%AC(%E9%80%92%E5%BD%92%E5%92%8C%E9%9D%9E%E9%80%92%E5%BD%92)(%E4%BB%A5%E5%8F%8A%E5%8F%8C%E5%90%91%E9%93%BE%E8%A1%A8%E7%9A%84%E5%8F%8D%E8%BD%AC).md)是一样的。解析也可以看那篇博客。两种思路:

思路一:

* 很经典的翻转链表的题目，使用`pre、next`指针，`pre`指向当前`cur`的前一个，`next`是当前`cur`的下一个指针；
* 然后每次都改变`cur`的`next`为pre，循环递推，直到`cur = null`，最后返回`pre`；

```java
public class Solution {
    
    public ListNode ReverseList(ListNode head) {
        ListNode pre = null, cur = head, next;
        while (cur != null) {
            next = cur.next;
            cur.next = pre;//反转
            pre = cur;//继续下一次
            cur = next;
        }
        return pre;
    }
}
```

思路二: **递归**

思路和上面还是一样的，就是`pre = cur，cur = next`这两行替换成去递归了，没什么特殊的。

```java
public class Solution {

    public ListNode ReverseList(ListNode head) {
        return reverse(head, null);
    }

    private ListNode reverse(ListNode cur, ListNode pre) {
        if (cur == null)
            return pre;
        ListNode next = cur.next;
        cur.next = pre;
        return reverse(next, cur);
    }
}
```

