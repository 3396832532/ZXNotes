## 剑指Offer - 63 - 数据流中的中位数

#### [题目链接](https://www.nowcoder.com/practice/9be0172896bd43948f8a32fb954e1be1?tpId=13&tqId=11216&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/9be0172896bd43948f8a32fb954e1be1?tpId=13&tqId=11216&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

如何得到一个数据流中的中位数？如果从数据流中读出奇数个数值，那么中位数就是所有数值排序之后位于中间的数值。如果从数据流中读出偶数个数值，那么中位数就是所有数值排序之后中间两个数的平均值。我们使用Insert()方法读取数据流，使用GetMedian()方法获取当前读取数据的中位数。

### 解析

也在[LeetCode - 295](https://github.com/ZXZxin/ZXBlog/blob/master/%E5%88%B7%E9%A2%98/LeetCode/Data%20Structure/Trie/LeetCode%20-%20676.%20Implement%20Magic%20Dictionary(%E5%AD%97%E5%85%B8%E6%A0%91)%20%26%20295.%20Find%20Median%20from%20Data%20Stream(%E5%A0%86).md#leetcode-295-find-median-from-data-stream)做过。具体可以看那篇博客，利用一个最大堆和一个最小堆即可。

代码:

```java
import java.util.PriorityQueue;

public class Solution {

    //堆顶最小，但是存的是最大的 n/2个元素
    private PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    //堆顶最大，但是存的是最小的 n/2个元素
    private PriorityQueue<Integer> maxHeap = new PriorityQueue<>((o1, o2) -> o2 - o1);

    public void Insert(Integer num) {
        if(maxHeap.isEmpty() || num <= maxHeap.peek()){
            maxHeap.add(num);
        }else{
            minHeap.add(num);
        }
        if(minHeap.size() - maxHeap.size() > 1)
            maxHeap.add(minHeap.poll());
        else if(maxHeap.size() - minHeap.size() > 1){
            minHeap.add(maxHeap.poll());
        }
    }

    public Double GetMedian() {
        if(minHeap.size() > maxHeap.size())
            return 1.0 * minHeap.peek();
        else if(maxHeap.size() > minHeap.size())
            return 1.0 * maxHeap.peek();
        else
            return 1.0 * (minHeap.peek() + maxHeap.peek())/2;
    }

}
```

