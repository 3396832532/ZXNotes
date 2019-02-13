## 剑指Offer - 09 - 变态跳台阶

#### [题目链接](https://www.nowcoder.com/practice/22243d016f6b47f2a6928b4313c85387?tpId=13&tqId=11162&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/22243d016f6b47f2a6928b4313c85387?tpId=13&tqId=11162&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 一只青蛙一次可以跳上1级台阶，也可以跳上2级……**它也可以跳上n级**。求该青蛙跳上一个n级的台阶总共有多少种跳法。

#### 解析

这题不同于`剑指Offer - 08 - 跳台阶`的地方在于，`n`层可以由前面的任意一层跳过来:

* **第n个台阶可以由前面所有的台阶跳过来即f[n] = f[n-1] + f[n-2] + ... f[1]；**
* **然后加上直接跳到自己这个台阶(+1)；**

##### 1)、递归

```java
public class Solution {

    public int JumpFloorII(int target) {
        if (target < 1)
            return 0;
        if (target == 1 || target == 2)
            return target;
        int sum = 1; //加上自己一步直接跳到自己的台阶
        for (int i = 1; i < target; i++)
            sum += JumpFloorII(i);
        return sum;
    }
}
```

##### 2)、递推(DP)

```java
public class Solution {
    public int JumpFloorII(int target) {
        if (target < 1)
            return 0;
        if (target == 1 || target == 2)
            return target;
        int[] dp = new int[target + 1];
        dp[1] = 1;
        for (int i = 2; i <= target; i++) {
            dp[i] = 0;
            for (int j = 1; j < i; j++) //前面的和
                dp[i] += dp[j];
            dp[i] += 1;//加上自己的
        }
        return dp[target];
    }
}
```

稍微优化一下:

```java
public class Solution {
    public int JumpFloorII(int target) {
        if (target < 1)
            return 0;
        if (target == 1 || target == 2)
            return target;
        int[] dp = new int[target + 1];
        dp[1] = 1;
        dp[2] = 2;
        int preSum = 3;
        for (int i = 3; i <= target; i++) {
            dp[i] = preSum + 1;
            preSum += dp[i];
        }
        return dp[target];
    }
}
```

##### 3)、滚动优化

```java
public class Solution {
    public int JumpFloorII(int target) {
        if (target < 1)
            return 0;
        if (target == 1 || target == 2)
            return target;
        int preSum = 3, res = 0;//一开始  preSum = f1 + f2的值
        for (int i = 3; i <= target; i++) {
            res = preSum + 1;  //之前的和　加上自己的
            preSum += res;
        }
        return res;
    }
}
```

##### 4)、规律

推出前面几项也可以看出**其实就是一个等比数列求和** ，也就是2<sup>n-1</sup>。

```java
public class Solution {
    
//    public int JumpFloorII(int target) {
//        return (int) Math.pow(2, target - 1);
//    }

    public int JumpFloorII(int target) {
        return 1 << (target - 1);
    }
}
```


