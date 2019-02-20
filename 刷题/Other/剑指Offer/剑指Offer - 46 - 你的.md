## 孩子们的游戏(圆圈中最后剩下的数)(约瑟夫环)

#### [题目链接](https://www.nowcoder.com/practice/f78a359491e64a50bce2d89cff857eb6?tpId=13&tqId=11199&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/f78a359491e64a50bce2d89cff857eb6?tpId=13&tqId=11199&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

### 解析

常规解法，使用链表模拟。

```java
public class Solution {

    class Node {
        int val;
        Node next;

        Node(int v) {
            val = v;
        }
    }

    public int LastRemaining_Solution(int n, int m) {
        if (m == 0 || n == 0)
            return -1;
        // 构造环形链表
        Node head = new Node(0);
        Node pre = head;
        for (int i = 1; i < n; i++) {
            Node cur = new Node(i);
            pre.next = cur;
            pre = cur;
        }
        pre.next = head; // 环形
        Node last = pre;
        int cnt = 0;
        while (head != last) {
            if (++cnt == m) {
                last.next = head.next; // del head
                cnt = 0;
                head = last.next;
            } else {
                last = last.next; // del tail
                head = last.next;
            }
        }
        return head.val;
    }
}

```

