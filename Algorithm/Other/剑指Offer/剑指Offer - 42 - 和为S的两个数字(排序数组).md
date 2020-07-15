## 剑指Offer - 42 - 和为S的两个数字(排序数组)

#### [题目链接](https://www.nowcoder.com/practice/390da4f7a00f44bea7c2f3d19491311b?tpId=13&tqId=11195&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/390da4f7a00f44bea7c2f3d19491311b?tpId=13&tqId=11195&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个**递增排序**的数组和一个数字S，在数组中查找两个数，使得他们的和正好是S，如果有多对数字的和等于S，**输出两个数的乘积最小的**。

### 解析

比较简单的双指针题目。肯定不是两重循环找。

思路

* 设两个指针`L、R`，分别是排序数组的开头和结尾；
* 然后下面就是两个指针`L、R`向中间靠拢的过程。① 如果`arr[L] + arr[R] > sum`，说明右边那个`arr[R]`大了，需要向左移动，看能不能找到更小的`arr[R]`来和`arr[L]`一起组成`sum`。② 同理，如果`arr[L] + arr[R] < sum`，说明左边那个`arr[L]`小了，需要向右移动，看能不能找到更大的`arr[L]`来和`arr[R]`一起组成`sum`。③否则等于就返回即可；
* 题目说要找到乘积最小的，可以发现，`L、R`隔的越远，`arr[L] * arr[R]`乘积越小，所以我们的做法没问题。

代码:

```java
import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> FindNumbersWithSum(int[] array, int sum) {
        ArrayList<Integer> res = new ArrayList<>();
        int L = 0, R = array.length - 1;
        while(L < R){
            if(array[L] + array[R] == sum){
                res.add(array[L]);
                res.add(array[R]);
                return res;
            }
            if(array[L] + array[R] < sum) L++;
            else R--;
        }
        return res;
    }
}
```

