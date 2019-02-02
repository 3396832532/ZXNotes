## LeetCode - 91. Decode Ways & 639. Decode Ways II(DP)

* [LeetCode - 91. Decode Ways](#1)
* [LeetCode - 639. Decode Ways II](#2)

***
### <font id = "1">LeetCode - 91. Decode Ways 
#### [题目链接](https://leetcode.com/problems/decode-ways/)
#### 题目
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190202131941724.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
#### 解析

也是很明显的DP题目，递归思路

* 当前的字符串，当前的位置，总共的可能就是两种情况；
* 第一种情况，拆分出来当前的`str[i]`，然后求出`[i+1, s.length()]`的方法数`r1`，只要当前`str[i] != 0`，答案`res`就累加`r1`，然后我们还可以考虑将`str[i], str[i+1]`这两个字符拆分出来，这两个字符组成的数字只要满足`10 <= X <= 26 `，则递归`[i+2, s.length()]`得到的结果`r2`，则`res = r1 + r2`；
* 然后用记忆化记录结果即可；

![在这里插入图片描述](https://img-blog.csdnimg.cn/201902022127254.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
直观的递归代码: 
```java
class Solution {
    HashMap<String, Integer> dp;

    public int numDecodings(String s) {
        if(s == null || s.length() == 0)
            return 0;
        dp = new HashMap<>();
        return ways(s);
    }

    private int ways(String s){
        if(s.length() == 0)//找到一个了
            return 1; 
        if(s.charAt(0) == '0')
            return 0;
        if(s.length() == 1)//len = 1, 防止下面substr(0, 2)出错
            return 1;
        if(dp.containsKey(s))
            return dp.get(s);
        int res = ways(s.substring(1)); 
        int se = Integer.parseInt(s.substring(0, 2));
        if(se <= 26)
            res += ways(s.substring(2));
        dp.put(s, res);
        return res;
    }
}
```
也可以将字符串改成下标的方式，这样内存更少开销。
```java
class Solution {
    private int[] dp;

    public int numDecodings(String s) {
        if(s == null || s.length() == 0)
            return 0;
        dp = new int[s.length()];
        Arrays.fill(dp, -1);
        return ways(s.toCharArray(), 0);
    }

    private int ways(char[] chs, int pos){
        if(pos == chs.length)//越界/空串
            return 1;
        if(chs[pos] == '0')
            return 0;
        if(pos == chs.length-1)//一个字符返回1
            return 1;
        if(dp[pos] != -1)
            return dp[pos];
        int res = ways(chs, pos+1);
        int se = 10 * (chs[pos] - '0') + (chs[pos+1] - '0');
        if(se <= 26)
            res += ways(chs, pos+2);
        dp[pos] = res;
        return res;
    }
}
```
也可以改成递推的代码: (<font color = red>注意这里的特殊处理，空串是在`n == s.length()`的位置)。
```java
class Solution {
    
    public int numDecodings(String s) {
        if (s == null || s.length() == 0)
            return 0;
        int n = s.length();
        char[] chs = s.toCharArray();
        int[] dp = new int[n + 1];
        dp[n] = 1; // 空串要特殊处理
        dp[n-1] = chs[n-1] == '0' ? 0 : 1;
        for(int i = n-2; i >= 0; i--){
            if(chs[i] == '0')
                dp[i] = 0;
            else {
                dp[i] = dp[i+1];
                int se = 10 * (chs[i] - '0') + (chs[i+1] - '0');
                if(se <= 26)
                    dp[i] += dp[i+2];
            }
        }
        return dp[0];
    }
}
```
***
### <font id = "2">LeetCode - 639. Decode Ways II
#### [题目链接](https://leetcode.com/problems/decode-ways-ii/)
#### 题目
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019020213212088.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
#### 解析

大体思路和上一题相同:

* 不同点： 上面的情况当一个字符的时候直接返回`1`，但是这里需要考虑当只有一个字符的时候： ① `c == 0`返回`0`；②`c == {0~9}`返回`1`；③`c == *`返回`9`；
* 两个字符的时候更多的情况，我写在了下图的右边，可以自己推一下，不难；
* 然后就是注意结果的返回，上一题相当于是`1 * r1 + 1 * r2`，这一题就是上面的一些不同的结果导致，如果前缀不是`1`，就要和系数相乘，即`res = p1 * r1 + p2 * r2`，其中`r1`还是隔离`str[i]`之后的返回结果，`r2`是隔离`str[i]str[i+1]`之后的返回结果，不过要带上系数即可；


![在这里插入图片描述](https://img-blog.csdnimg.cn/20190202214318650.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
由于这一题数据规模比较大，所以递归会产生`StackOverflowError`，即递归深度太大，所以最好使用递推代码: 
```java
class Solution {

    final int mod = 1000000000 + 7;

    public int numDecodings(String s) {
        int n = s.length();
        char[] chs = s.toCharArray();
        long[] dp = new long[n+1];
        dp[n] = 1; // 空串
        dp[n-1] = ways1(chs[n-1]);
        for(int i = n - 2; i >= 0; i--){
            if(chs[i] == '0')
                dp[i] = 0;
            else {
                dp[i] = ways1(chs[i]) * dp[i+1] + ways2(chs[i], chs[i+1]) * dp[i+2];    
                dp[i] %= mod;
            }
        }
        return (int)dp[0];
    }

    // only one character
    private int ways1(char c){
        if(c == '0')
            return 0;
        if(c == '*')
            return 9;
        return 1;
    }

    // two characters
    private int ways2(char c1, char c2){
        if(c1 == '*' && c2 == '*')
            return 15;
        if(c1 == '*') // c1 == '*' && c2 != '*'
            return (c2 >= '0' && c2 <= '6') ? 2 : 1; // *0 ~ *6 --> 2
        if(c2 == '*') // c1 != '*' && c2 == '*'
            return c1 == '1' ? 9 : (c1 == '2' ? 6 : 0);
        int se = 10 * (c1 - '0') + (c2 - '0');
        return (se >= 10 && se <= 26) ? 1 : 0; // contains 01, 02...
    }
}
```



下面是递归的代码(`StackOverflowError`)

```java
class Solution {

    final int mod = 1000000000 + 7;

    private long[] dp;

    public int numDecodings(String s) {
        if(s == null || s.length() == 0)
            return 0;
        dp = new long[s.length()];
        Arrays.fill(dp, -1);
        return (int)recur(s.toCharArray(), 0);
    }

    private long recur(char[] chs, int pos){
        if(pos == chs.length) //空串
            return 1;
        if(chs[pos] == '0')
            return 0;
        if(pos == chs.length-1)
            return ways1(chs[pos]);
        if(dp[pos] != -1)
            return dp[pos];
        long res = ways1(chs[pos]) * recur(chs, pos+1) % mod;
        if(pos < chs.length-1)
            res += (ways2(chs[pos], chs[pos+1]) * recur(chs, pos+2)%mod) % mod;
        return dp[pos] = res%mod;
    }

    // only one character
    private int ways1(char c){
        if(c == '0')
            return 0;
        if(c == '*')
            return 9;
        return 1;
    }

    // two characters
    private int ways2(char c1, char c2){
        if(c1 == '*' && c2 == '*')
            return 15;
        if(c1 == '*') // c1 == '*' && c2 != '*'
            return (c2 >= '0' && c2 <= '6') ? 2 : 1; // *0 ~ *6 --> 2
        if(c2 == '*') // c1 != '*' && c2 == '*'
            return c1 == '1' ? 9 : (c1 == '2' ? 6 : 0);
        int se = 10 * (c1 - '0') + (c2 - '0');
        return (se >= 10 && se <= 26) ? 1 : 0; // contains 01, 02...
    }
}
```

