## 剑指Offer - 12 - 数值的整数次方

#### [题目链接](https://www.nowcoder.com/practice/1a834e5e3e1a4b7ba251417554e07c00?tpId=13&tqId=11165&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/1a834e5e3e1a4b7ba251417554e07c00?tpId=13&tqId=11165&tPage=1&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 给定一个`double`类型的浮点数`base`和`int`类型的整数`exponent`。求`base`的`exponent`次方。

#### 解析

```java
public class Solution {

    public double Power(double base, int exponent) {
        return myPow(base, exponent);
    }

    public double myPow(double x, int n) {
        if (n > 0) {
            return pow(x, n);
        } else {
            if (n == Integer.MIN_VALUE) {
                // MAX_VALUE = -(Integer.MIN_VALUE + 1)
                return 1.0 / (pow(x, -(Integer.MIN_VALUE + 1)) * x);
            }
            return 1.0 / pow(x, -n);
        }
    }

    public double pow(double x, int n) {
        if (n == 0)
            return 1;
        double half = pow(x, n / 2);
        if (n % 2 == 0)
            return half * half;
        else
            return x * half * half;
    }
}
```

另一种写法:

```java
public class Solution {
    
    public double Power(double base, int exponent) {
        return myPow(base, exponent);
    }

    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                // return 1.0 / (myPow(x,-(Integer.MIN_VALUE+1)) * x);
                return 1.0 / (myPow(x, Integer.MAX_VALUE) * x);
            }
            return 1.0 / myPow(x, -n);
        }
        double half = myPow(x, n / 2);
        if (n % 2 == 0)
            return half * half;
        else
            return x * half * half;
    }
}
```

非递归:

下面三种写法都是利用非递归的快速幂乘法，唯一的不同就是处理`Integer.MIN_VALUE`的方式不同。

```java
public class Solution {

    public double Power(double base, int exponent) {
        return myPow(base, exponent);
    }

    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                return 1.0 / (myPow(x, Integer.MAX_VALUE) * x);
            } else {
                return 1.0 / myPow(x, -n);
            }
        }
        double res = 1.0;
        while (n > 0) {
            if ((n & 1) != 0)
                res *= x;
            x = x * x;
            n >>= 1;
        }
        return res;
    }
}
```

```java
public class Solution {

    public double Power(double base, int exponent) {
        return myPow(base, exponent);
    }

    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        double res = 1.0;
        // 处理最大数和最小数
        if (n < 0) {
            x = 1 / x;
            n = -(1 + n);  // for Integer.MIN_VALUE   
            res *= x;  // x is 1/x   because n is -(n+1) so should do this 
        }
        while (n > 0) {
            if ((n & 1) != 0)
                res *= x;
            x = x * x;
            n >>= 1;
        }
        return res;
    }
}
```

```java
public class Solution {

    public double Power(double base, int exponent) {
        return myPow(base, exponent);
    }

    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        long abs = Math.abs((long) n); // also for Integer.MIN_VALUE
        double res = 1.0;
        while (abs > 0) {
            if ((abs & 1) != 0)
                res *= x;
            x = x * x;
            abs >>= 1;
        }
        if (n < 0)
            return 1.0 / res;
        return res;
    }
}
```