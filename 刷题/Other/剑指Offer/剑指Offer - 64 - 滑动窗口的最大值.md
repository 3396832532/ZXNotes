## 剑指Offer - 64 - 滑动窗口的最大值

#### [题目链接](https://www.nowcoder.com/practice/1624bc35a45c42c0bc17d17fa0cba788?tpId=13&tqId=11217&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/1624bc35a45c42c0bc17d17fa0cba788?tpId=13&tqId=11217&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

给定一个数组和滑动窗口的大小，找出所有滑动窗口里数值的最大值。例如，如果输入数组`{2,3,4,2,6,2,5,1}`及滑动窗口的大小3，那么一共存在6个滑动窗口，他们的最大值分别为`{4,4,6,6,6,5}`； 针对数组`{2,3,4,2,6,2,5,1}`的滑动窗口有以下6个：` {[2,3,4],2,6,2,5,1}， {2,[3,4,2],6,2,5,1}， {2,3,[4,2,6],2,5,1}， {2,3,4,[2,6,2],5,1}， {2,3,4,2,[6,2,5],1}， {2,3,4,2,6,[2,5,1]}`。

### 解析

也做过。。。

在[LintCode - 362](../LintCode/TwoPointer/LintCode - 362. Sliding Window Maximum滑动窗口的最大值.md)。具体看那篇博客，详细介绍了单调队列的使用。

```java
import java.util.*;

public class Solution {
    public ArrayList<Integer> maxInWindows(int [] num, int size){
        ArrayList<Integer> res = new ArrayList<>();
        if(num == null || size < 1 || num.length < size) return res;
        LinkedList<Integer> qmax = new LinkedList<>();
        for(int i = 0; i < num.length; i++){
            while(!qmax.isEmpty() && num[qmax.peekLast()] < num[i])
                qmax.pollLast();
            qmax.addLast(i);
            if(i - size == qmax.peekFirst()) qmax.pollFirst();
            if(i >= size-1) res.add(num[qmax.peekFirst()]);
        }
        return res;
    }
}
```

