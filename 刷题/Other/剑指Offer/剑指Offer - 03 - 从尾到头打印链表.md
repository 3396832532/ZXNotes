## 剑指Offer - 03 - 从尾到头打印链表

#### [题目链接](https://www.nowcoder.com/practice/d0267f7f55b3412ba93bd35cfa8e8035?tpId=13&tqId=11156&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/d0267f7f55b3412ba93bd35cfa8e8035?tpId=13&tqId=11156&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个链表的头结点，按链表值**从尾到头**的顺序返回一个ArrayList。

#### 解析

这题比较简单，可以用栈，也可以递归。

递归的写法：

```java
import java.util.ArrayList;
import java.util.Stack;

public class Solution {
    private ArrayList<Integer> res;
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        res = new ArrayList<>();
        rec(listNode);
        return res;
    }
    private void rec(ListNode cur){
        if(cur == null)
            return;
        rec(cur.next);
        res.add(cur.val);
    }
}
```

使用栈保存 :

```java
import java.util.ArrayList;
import java.util.Stack;

public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        Stack<Integer> stack = new Stack<>();
        ListNode cur = listNode;
        while (cur != null) {
            stack.push(cur.val);
            cur = cur.next;
        }
        ArrayList<Integer> res = new ArrayList<>();
        while (!stack.isEmpty())
            res.add(stack.pop());
        return res;
    }
}
```

