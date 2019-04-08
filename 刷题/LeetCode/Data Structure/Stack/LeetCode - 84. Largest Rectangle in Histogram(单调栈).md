# LeetCode - 84. Largest Rectangle in Histogram(单调栈)

#### [题目链接](https://leetcode.com/problems/largest-rectangle-in-histogram/)

> https://leetcode.com/problems/largest-rectangle-in-histogram/

#### 题目

![1554685911888](assets/1554685911888.png)

## 解析

给出两种解法，一种`O(N^2)`，一种单调栈的`O(N)`。

![1554687058708](assets/1554687058708.png)

代码:

```java
class Solution {
    public int largestRectangleArea(int[] heights) {
        if(heights == null || heights.length == 0) return 0;
        int maxArea = 0;
        for(int cur = 0; cur < heights.length; cur++){
            if(cur != heights.length - 1 && heights[cur] <= heights[cur+1]) continue;
            int minHeight = Integer.MAX_VALUE;
            for(int i = cur; i >= 0; i--){
                if(heights[i] < minHeight) minHeight = heights[i];
                maxArea = Math.max(maxArea, minHeight * (cur - i + 1));
            }
        }
        return maxArea;
    }
}
```

单调栈的解法。

单调栈的知识看[**这篇博客**](https://blog.csdn.net/zxzxzx0119/article/details/81629626)，注意这个栈是 **从栈底到栈顶依次是从小到大的**:

- 如果栈中的数比当前的数大(或者等于)就要处理栈顶的(记录左右两边的比它小的第一个数)；
- 然后如果遍历完之后，单独处理栈，此时所有元素右边都不存在比它小的；

看下图(上面的例子)栈的变化过程:  (**栈的左侧是索引，右侧是值，但是实际中栈只存索引**)

![1554689247272](assets/1554689247272.png)

代码:

```java
class Solution {
    public int largestRectangleArea(int[] heights) {
        if(heights == null || heights.length == 0) return 0;
        Stack<Integer> s = new Stack<>();
        int maxArea = 0;
        for(int i = 0; i < heights.length; i++) {
            while (!s.isEmpty() && heights[i] <= heights[s.peek()]){
                int top = s.pop();
                int L = s.isEmpty() ? -1 : s.peek(); //如果左边没有比height[top]小的就是-1
                maxArea = Math.max(maxArea, heights[top] * (i - L - 1));
            }
            s.push(i); //注意是下标入栈
        }
        while(!s.isEmpty()){
            int top = s.pop();
            int L = s.isEmpty() ? -1 : s.peek();
            maxArea = Math.max(maxArea, heights[top] * (heights.length - L - 1)); // 右边没有比height[top]大的,就是右边界height.length
        }
        return maxArea;
    }
}
```

