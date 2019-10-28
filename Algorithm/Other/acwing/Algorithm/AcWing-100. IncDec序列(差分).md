# AcWing-100. IncDec序列

## [题目链接](https://www.acwing.com/problem/content/102/)

> https://www.acwing.com/problem/content/102/

## 题目

给定一个长度为 nn 的数列 a1,a2,…,ana1,a2,…,an，每次可以选择一个区间 [l,r]，使下标在这个区间内的数都加一或者都减一。

求至少需要多少次操作才能使数列中的所有数都一样，并求出在保证最少次数的前提下，最终得到的数列可能有多少种。

#### 输入格式

第一行输入正整数nn。

接下来nn行，每行输入一个整数，第i+1行的整数代表aiai。

#### 输出格式

第一行输出最少操作次数。

第二行输出最终能得到多少种结果。

#### 数据范围

0<n≤1050<n≤105,
0≤ai<21474836480≤ai<2147483648

#### 输入样例：

```
4
1
1
2
2
```

#### 输出样例：

```
1
2
```
## 解析

思路：首先这题一个重点，就是区间`[l,r]`的修改操作。因为这道题目的修改操作有一个特性，就是只加一或者只减一，而不是+x，也不是−x，所以说我们并不需要用到高级的数据结构，线段树和树状数组，而只是需要用差分即可。

差分定义：对于一个给定的数列A，它的差分数列B定义为,` B[1]=A[1],B[i]=Ai−Ai−1(2<=i<=n)`这里只说性质，也就是把序列A的区间[L,R]加d，也就是把`[Al,Al+1....Ar]`都加上d，其实就是它的差分序列B中，`Bl+d,Br+1−d` 其他的位置统统不改变。



即给区间`[l, r]`加上一个常数c，则`b[l] += c, b[r+1] -= c`。



因此在这道题目中，我们就可以利用这个非常有用的性质，因为我们只要求A序列中所有的数相同，而不在意这些方案具体是什么，所以说我们就可以转化题目，也就是将对A序列的+1,−1操作，让A序列相同，改成目标把`B2,…,Bn`变成全0即可，也就是A序列全部相等。而且最后得到的序列，就是这n个B1贪心：因为我们有上面所说的性质，那么我们就可以，每一次选取Bi和Bj，`2<=i,j<=n`而且这两个数，一个为正数，一个为负数，至于为什么要是正负配对，因为我们是要这个B序列2~n都要为0，所以这样负数增加，正数减少，就可以最快地到达目标为0的状态。至于那些无法配对的数Bk可以选B1或者Bn+1，这两个不影响的数，进行修改。

```java
1、  2 <= i, j <= n
2、  i = 1, 2 <= j <= n
3、  2 <= i <= n, j = n+1
4、  i=1, j = n+1 （不影响）
```



所以说我们这道题目得出的答案就是，最少操作数`min(p,q)+abs(p−q)=max(p,q)`然后最终序列a可能会有`abs(p−q)+1`种情况。p为b序列中正数之和，而q为b序列中负数之和。


```java
import java.io.BufferedInputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    static Scanner in = new Scanner(new BufferedInputStream(System.in));
    static PrintWriter out = new PrintWriter(System.out);

    public static void main(String[] args) {

        int n = in.nextInt();
        int[] a = new int[n];
        for(int i = 0; i < n; i++) a[i] = in.nextInt();

        for(int i = n-1; i > 0; i--) a[i] -= a[i-1]; //差分，从后往前遍历

        long pos = 0, neg = 0;
        for(int i = 1 ; i < n; i++){
            if(a[i] > 0) pos += a[i];
            else neg -= a[i];
        }
        out.println(Math.min(pos, neg) + Math.abs(pos - neg));
        out.println(Math.abs(pos - neg) + 1);
        out.close();

    }

}

```

