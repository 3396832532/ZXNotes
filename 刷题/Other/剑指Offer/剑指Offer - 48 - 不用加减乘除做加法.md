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

***

另外，也可以用位运算实现**减法**，因为`a - b = a +(-b)`，而在二进制的表示中，得到一个数的相反数，就是一个数取反然后`+1`(补码)即可。

取反`+1`的代码看`negNum()`方法，而`Minus()`方法实现的是两数的减法运算。

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

    // a + (-b)
    public int Minus(int num1, int num2){
        return Add(num1, negNum(num2));
    }

    private int negNum(int n) {
        return Add(~n, 1); // 取反+1
    }

    public static void main(String[] args){
        System.out.println(new Solution().Minus(1000, 100));
    }
}
```

***

用位运算也可以实现乘法。做法如下:

**a * b = a*2<sup>0</sup> * b <sub>0</sub> + a *2<sup>1</sup> *b<sub>1</sub> + ... + a * 2<sup>i</sup> *b <sub>i</sub> + ... + a *2<sup>31</sup> *b<sub>31</sub>** 。

其中`bi`为`0`或者`1`代表整数`b`的二进制数表达中第`i`位的值。

下面就计算过程举一个例子:

`a = 22 = 000010110，b = 13 = 000001101，res = 0`：

```java
1、一开始的值
a  : 000010110
b  : 000001101
res: 000000000
2、b的最左侧为1，所以res = res + a,同时b右移一位, a左移一位
a  : 000101100
b  : 000000110
res: 000010110
3、b的最左侧为0，所以res不变, 同时b右移一位, a左移一位
a  : 001011000
b  : 000000011
res: 000010110
4、b的最左侧为1，所以res = res + a,同时b右移一位, a左移一位
a  : 010110000
b  : 000000001
res: 001101110
5、b的最左侧为1，所以res = res + a,同时b右移一位, a左移一位
a  : 101100000
b  : 000000000
res: 100011110
```

此时`b == 0`，过程停止，返回`res = 100011110`，即`286`。

不管`a、b`是正、负、0，以上过程都是对的。

代码：

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

    public int Multi(int a, int b){
        int res = 0;
        while(b != 0){
            if( (b & 1) != 0)
                res = Add(res, a);
            a <<= 1;
            b >>>= 1; // 无符号右移
        }
        return res;
    }

    public static void main(String[] args){
        System.out.println(new Solution().Multi(1000, 100));
    }
}
```



