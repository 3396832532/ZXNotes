## 剑指Offer - 40 - 数组中只出现一次的数字

#### [题目链接](https://www.nowcoder.com/practice/e02fdb54d7524710a7d664d082bb7811?tpId=13&tqId=11193&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/e02fdb54d7524710a7d664d082bb7811?tpId=13&tqId=11193&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 一个整型数组里除了**两个数字**之外，其他的数字都出现了偶数次。请写程序找出这两个只出现一次的数字。

### 解析

```java
public class Solution {
    // num1[0], num2[0]是返回的两个只出现一次的数
    public void FindNumsAppearOnce(int[] array, int num1[], int num2[]) {
        if (array.length < 2)
            return;
        int res = 0;
        for (int i = 0; i < array.length; i++)
            res ^= array[i];
        int digit = 1;
        while ((digit & res) == 0) digit *= 2; // digit的最高位为1
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & digit) == 0)  //第一组 (第一个子数组)
                num1[0] ^= array[i];
            else                          //第二组 (第二个子数组)
                num2[0] ^= array[i];
        }
    }
}
```

