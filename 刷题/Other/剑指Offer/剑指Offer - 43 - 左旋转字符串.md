## 剑指Offer - 43 - 左旋转字符串

#### [题目链接](https://www.nowcoder.com/practice/12d959b108cb42b1ab72cef4d36af5ec?tpId=13&tqId=11196&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/12d959b108cb42b1ab72cef4d36af5ec?tpId=13&tqId=11196&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 汇编语言中有一种移位指令叫做循环左移（ROL），现在有个简单的任务，就是用字符串模拟这个指令的运算结果。对于一个给定的字符序列S，请你把其循环左移K位后的序列输出。例如，字符序列`S=”abcXYZdef”`,要求输出**循环左移3位**后的结果，即`“XYZdefabc”`。是不是很简单？OK，搞定它！

### 解析

两种思路。

#### 1、思路一

简单的做法:

* 直接将`[n, str.length()]`先加到`res`字符串；
* 然后将`[0, n]`之间的字符串加入到`res`即可；

```java
public class Solution {
    public String LeftRotateString(String str, int n) {
        if(str == null ||str.length() == 0 )
            return "";
        if(n == str.length() || n == 0)
            return str;
        StringBuilder res = new StringBuilder(str.substring(n));
        res.append(str.substring(0, n));
        return res.toString();
    }
}
```

#### 2、思路二

剑指Offer的解法:

* 将字符串分成两部分，第一部分记为前n个字符部分记为`A`，后面的部分记为`B`；
* 其实这个题目就是要你从`AB`转换到`BA`；
* 做法就是 (1)、先将A部分字符串翻转；(2)、然后将B字符串翻转；(3)、最后将整个字符串翻转；
* 也就是(A<sup>T</sup>B<sup>T</sup>)<sup>T </sup>= BA；

代码:

```java
public class Solution {
    public void reverse(char[] chs, int L, int R) {
        for (; L < R; L++, R--) {
            char c = chs[L];
            chs[L] = chs[R];
            chs[R] = c;
        }
    }
    public String LeftRotateString(String str, int n) {
        if (str == null || str.length() == 0) return "";
        if (n == str.length() || n == 0)
            return str;
        char[] chs = str.toCharArray();
        reverse(chs, 0, n - 1);
        reverse(chs, n, str.length() - 1);
        reverse(chs, 0, str.length() - 1);
        return new String(chs);
    }
}
```

