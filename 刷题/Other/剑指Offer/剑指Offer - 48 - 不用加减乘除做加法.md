## 剑指Offer - 48 - 不用加减乘除做加法

#### [题目链接](https://www.nowcoder.com/practice/59ac416b4b944300b617d4f7f111b215?tpId=13&tqId=11201&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

#### 题目

> https://www.nowcoder.com/practice/59ac416b4b944300b617d4f7f111b215?tpId=13&tqId=11201&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

### 解析

如果在不考虑进位的情况下，`a ^ b`就是正确结果。因为`0 + 0 = 0(0 ^ 0)`，`1 + 0 = 1(1 ^ 0)`，`0 + 1  = 1(0 ^ 1)`，`1 + 1 = 0 (1 ^ 1)`。

例如:

```java
a : 001010101
b : 000101111
    001111010
```

在只考虑进位的情况下，也就是只考虑`a + b`中进位产生的值的情况下，`(a & b) << 1`就是结果。因为第`i`位的结果只有可能是`i - 1`位都为`1`的情况下才会产生进位。

例如:

```java
a : 001010101
b : 000101111
    000001010
```

把完全不考虑进位和考虑进位的两种情况相加，就是最终的结果。也就是说一直重复这样的过程，直到最后的进位为`0`则说明完成了相加。

例如:

```java
1、一开始的值:
a    :   001010101
b    :   000101111
    
2、上面两个异或和&<<1的值:
^    :   001111010
&<<1 :   000001010
    
3、上面两个异或和&<<1的值:
^    :   001110000
&<<1 :   000010100
    
4、上面两个异或和&<<1的值:
^    :   001100100
&<<1 :   000100000
    
5、上面两个异或和&<<1的值:
^    :   001000100
&<<1 :   001000000
    
6、上面两个异或和&<<1的值:
^    :   000000100
&<<1 :   010000000
    
7、上面两个异或和&<<1的值:
^    :   010000100
&<<1 :   000000000    (num2 == 0)
```

代码:

```java
public class Solution {
    public int Add(int num1, int num2) {
        int sum = num1, carry = 0;//一开始sum = num1的原因是如果num2 == 0,后面我直接返回sum，而不是num1
        while(num2 != 0){
            sum = num1 ^ num2;
            carry = (num1 & num2) << 1;
            num1 = sum;
            num2 = carry;
        }
        return sum;
    }
}
```

也可以写成这样:

```java
public class Solution {
    public int Add(int num1, int num2) {
        int sum = 0, carry = 0;
        while(num2 != 0){
            sum = num1 ^ num2;
            carry = (num1 & num2) << 1;
            num1 = sum;
            num2 = carry;
        }
        return num1;
    }
}
```

