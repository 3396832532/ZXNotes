## 剑指Offer - 33 - 丑数

#### [题目链接](https://www.nowcoder.com/practice/6aa9e04fc3794f68acf8778237ba065b?tpId=13&tqId=11186&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/6aa9e04fc3794f68acf8778237ba065b?tpId=13&tqId=11186&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 把只包含质因子2、3和5的数称作丑数（Ugly Number）。例如6、8都是丑数，但14不是，因为它包含质因子7。 习惯上我们把1当做是第一个丑数。求按从小到大的顺序的第N个丑数。

### 解析

第一种比较常规的方法，枚举`Integer.MAX_VALUE`范围内的所有丑数，打表之后排序，时间复杂度`O(NlogN)`。

这里将筛选部分写成静态代码块，就不会在测试的时候调用多次。

```java
import java.util.*;

public class Solution {
    static ArrayList<Integer> nums;
    static{
        nums = new ArrayList<>();
        for(long a = 1; a <= Integer.MAX_VALUE; a *= 2){
            for(long b = a; b <= Integer.MAX_VALUE; b *= 3){
                for(long c = b; c <= Integer.MAX_VALUE; c *= 5)
                    nums.add((int)c);
            }
        }
        Collections.sort(nums);// NlogN
    }
    public int GetUglyNumber_Solution(int index) {
        if(index == 0)
            return 0;
        return nums.get(index-1);
    }
}
```

O(N)的思路如下:

为了保持丑数数组的顺序，可以维护三个队列`q2、q3、q5`，分别存放每次由上一个最小的没有用过的丑数乘以`2、3、5`得到的丑数：

![](images/33_s.png)

过程:

* (1)、一开始第一个丑数为`1`，将 `1 * 2`放入`q2`，`1 * 3`放入`q3`，`1 * 5`放入`q5`；
* (2)、取三个队列中最小的为`2`，将`2 * 2 = 4`放入`q2`，`2 * 3 = 6`放入`q3`，`2 * 5 = 10`放入`q5`；
* (3)、取三个队列中最小的为`3`，将`3 * 2 = 6`放入`q2`，`3 * 3 = 9`放入`q3`，`3 * 5 = 15`放入`q5`；
* ....
* (6)、取三个队列中最小的为`6`，注意这里要将`q2、q3`都要弹出，因为`q2、q3`的对头都是`6`，然后。。。

然后我们只需要在丑数数组中取`index- 1`个即可，由于只是一个索引，所以丑数数字可以用一个变量`candi`和一个索引`count`记录即可，代码如下:

```java
import java.util.*;

public class Solution {

    public int GetUglyNumber_Solution(int index) {
        if(index == 0)
            return 0;
        Queue<Integer> q2 = new LinkedList<>(), q3 = new LinkedList<>(), q5 = new LinkedList<>();
        int candi = 1, cnt = 1;
        while(cnt < index){ 
            q2.add(candi * 2); 
            q3.add(candi * 3);  
            q5.add(candi * 5);
            int min = Math.min(q2.peek(), Math.min(q3.peek(), q5.peek()));
            if(q2.peek() == min) q2.poll();
            if(q3.peek() == min) q3.poll();
            if(q5.peek() == min) q5.poll();
            candi = min;
            cnt++;
        }
        return candi;
    }
}
```

另一种写法就是将3个队列替换成一个数组，然后就相应的需要三个索引`i1、i2、i3`了。代码如下:

```java
public class Solution {

    public int GetUglyNumber_Solution(int index) {
        if (index == 0)
            return 0;
        int[] res = new int[index + 1];
        int count = 0, i2 = 0, i3 = 0, i5 = 0;
        res[0] = 1;
        while (count < index) {
            int temp = Math.min(res[i2] * 2, Math.min(res[i3] * 3, res[i5] * 5));
            if (temp == res[i2] * 2) i2++;
            if (temp == res[i3] * 3) i3++;
            if (temp == res[i5] * 5) i5++;
            res[++count] = temp;
        }
        return res[index - 1];
    }
}

```

