## LeetCode - 204. Count Primes

#### [题目链接](https://leetcode.com/problems/count-primes/)

> https://leetcode.com/problems/count-primes/

#### 题目
筛选`0~n`(`[0，n)`)之间的素数个数。

![在这里插入图片描述](images/204_t.png)

#### 解析
如果用经典的判断素数，时间复杂度为`O(n*sqrt(n))`，会超时。

于是使用经典的[**埃拉托斯特尼筛法(有动图演示)**](https://zh.wikipedia.org/wiki/%E5%9F%83%E6%8B%89%E6%89%98%E6%96%AF%E7%89%B9%E5%B0%BC%E7%AD%9B%E6%B3%95)，有关素数也可以看我[另一篇博客](https://github.com/ZXZxin/ZXBlog/blob/master/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%AE%97%E6%B3%95/Math/Hdu%20-%201431%E7%B4%A0%E6%95%B0%E5%9B%9E%E6%96%87%E4%BB%A5%E5%8F%8A%E7%B4%A0%E6%95%B0%E7%9B%B8%E5%85%B3%E6%80%BB%E7%BB%93.md)讲解。

超时: 
```java
class Solution {
    //TLE
    public boolean isPrime(int nums){
        if(nums == 0 || nums == 1)
            return false;
        for(int i = 2; i <= (int)Math.sqrt(nums); i++){ // <= not < 
            if(nums%i == 0)
                return false;
        }
        return true;
    }
    
    public int countPrimes(int n) {
        int res = 0;
        for(int i = 0; i < n; i++){
            if(isPrime(i))
                res++;
        }
        return res;
    }
}
```
正解: 
```java
class Solution {
    public int countPrimes(int n) {
        if(n < 3)
            return 0;
        int res = 0;
        boolean[] isPrime = new boolean[n];
        Arrays.fill(isPrime,true);
        isPrime[0] = isPrime[1] = false;
        for(int i = 2; i < n; i++){
            if(isPrime[i]){
                res++;
                for(int j = 2*i; j < n; j+=i) //筛选
                    isPrime[j] = false;
            }
        }
        return res;
    }
}
```
