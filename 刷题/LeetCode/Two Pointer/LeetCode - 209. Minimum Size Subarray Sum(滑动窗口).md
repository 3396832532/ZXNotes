## LeetCode - 209. Minimum Size Subarray Sum(滑动窗口)
* 暴力**O(N<sup>3</sup>)**，超时
* 暴力优化**O(N<sup>2</sup>)**
* 二分**O(N*logN)**
* 滑动窗口**O(N)**

***
#### [题目链接](https://leetcode.com/problems/minimum-size-subarray-sum/description/)

> https://leetcode.com/problems/minimum-size-subarray-sum/description/

#### 题目
![在这里插入图片描述](images/209_t.png)
### 暴力O(N<sup>3</sup>)，超时
可以枚举两个边界`L`和`R`，然后计算`[L,R]`之间的和，然后判断是否`>=sum` ，并记录最小值即可。
```java
class Solution {
    // tle 超时
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int res = nums.length + 1;
        for (int l = 0; l < nums.length; l++) {
            for (int r = l; r < nums.length; r++) {
                int sum = 0;
                for (int k = l; k <= r; k++)
                    sum += nums[k];
                if (sum >= s)
                    res = Math.min(res, r - l + 1);
            }
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }
}
```
### 暴力优化O(N<sup>2</sup>)
可以先计算出从`0 `到每个位置`i`的和保存在一个`sums`数组中，然后枚举边界的时候，就可以直接取`sums`数组中的值相减即可，但是要注意: 
* **这里尽量不要用`sums[i]`表示`[0，i]`内的和；**
* **因为等下如果要计算`[L，R]`内的和，要使用`sums[R] - sums[L-1]`，这样的话对于`0`位置就不好处理，所以使用`sums[i]`表示`[0，i-1]`之间的和，这样方便一点；**


```java
class Solution {
    // O(n^2)
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int res = nums.length + 1;
        int[] sums = new int[nums.length + 1];        // sums[i]存放nums[0...i-1]的和  (最好不要sums[i]存放0...i的和，不好处理0位置)
        sums[0] = 0; // 0~-1的和 
        for (int i = 1; i <= nums.length; i++)
            sums[i] = sums[i - 1] + nums[i - 1];

        for (int l = 0; l < nums.length; l++) {
            for (int r = l; r < nums.length; r++) {
                if (sums[r + 1] - sums[l] >= s)  // 使用sums[r+1] - sums[l] 快速获得nums[l...r]的和
                    res = Math.min(res, r - l + 1);
            }
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }
}
```
***
### 二分O(N*logN)
还是上面的思路，不同的是：
* 我们可以利用二分查找求一个数组中`>=key`的位置，而我们的题目就是要求`>=s`的位置，所以对于左边界`L`，我们可以利用二分查找去查找第一个`>=sum[l]+s`的位置，这时就是我们要找的右边界`R`；
* **不过要注意: 我们每一个枚举的`L`，在`sums`数组查找到的`R`，其实是`sum[R+1]`位置代表的是`[0,R]`的和，所以要注意`res = Math.min(res,R-L)`，而不是`R-L+1`；**

二分查找的几种变形请看[这篇博客](https://blog.csdn.net/zxzxzx0119/article/details/82670761#t4)。
```java
class Solution {
    //ologn
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int res = nums.length + 1;
        int[] sums = new int[nums.length + 1];
        sums[0] = 0;
        for (int i = 1; i <= nums.length; i++)
            sums[i] = sums[i - 1] + nums[i - 1];
        for (int l = 0; l < nums.length; l++) {//必须从0开始 比如  s = 6,nums = {1,2,3}
            int r = firstLargeEqual(sums, sums[l] + s);
            if (r != sums.length)
                res = Math.min(res, r - l);      //注意这里不是r-l+1 ，因为寻找到的r实际上是r+1
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }

    public int firstLargeEqual(int[] arr, int key) {// 寻找第一个>= key的，不存在就返回arr.length
        int L = 0, R = arr.length - 1;
        int mid;
        while (L <= R) {
            mid = L + (R - L) / 2;
            if (arr[mid] >= key)
                R = mid - 1;
            else
                L = mid + 1;
        }
        return L;
    }
}
```
### 滑动窗口O(N)
滑动窗口的思想很简单，一直维护窗口的数:
* 如果当前窗口内的和`sum < s`我们就往右边扩一个位置，并且维护窗口的和`sum`的值，**但是要考虑`R`已经到达边界的情况，此时我们可以`break`了，因为就算`L`再往右边，也没用，因为此时`sum < s`；**
* 否则我们的窗口就左边缩一个，并且继续维护`sum`；
* 然后我们要做的就是不断的记录窗口的长度` R - L + 1`的最小值；


```java
class Solution {
    //O(n)
    public int minSubArrayLen(int s, int[] nums) {
        int L = 0, R = -1;  //一开始窗口内没有数
        int sum = 0;
        int res = nums.length + 1; //不可能的答案
        while (R < nums.length) {
            if (sum < s) {  //这里写成sum <= s也可以 ，看下面的方法
                if (++R == nums.length) break; //已经扩到最后一个数，可以退出了，因为此时已经sum < s，所以L你也更加不需要往右边扩了
                sum += nums[R];
            } else  // sum >= s
                sum -= nums[L++];

            if (sum >= s)
                res = Math.min(res, R - L + 1);
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }
}
```
上面的`sum < s`也可以写成`sum <= s`，效果是一样的: 

```java
class Solution {
    //O(n)
    public int minSubArrayLen(int s, int[] nums) {
        int L = 0, R = -1;  //一开始窗口内没有数
        int sum = 0;
        int res = nums.length + 1; //不可能的答案
        while (R < nums.length) {
            if (sum <= s) { //这里sum <= s也可以,因为下面已经判断了sum >= s就计算
                if (++R == nums.length) break;
                sum += nums[R];
            } else  // sum > s
                sum -= nums[L++];

            if (sum >= s)
                res = Math.min(res, R - L + 1);
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }
}
```
同时，也可以一开始窗口内有一个数，但是这样的话，<font color = red>要先维护`res`的值，然后再更新窗口: </font>

```java
class Solution {
    public int minSubArrayLen(int s, int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;
        int L = 0, R = 0;  //一开始窗口有一个数
        int sum = nums[0];
        int res = nums.length + 1;
        while (R < nums.length) {
            if (sum >= s) //这个必须放到上面，因为此时窗口已经有了一个数了
                res = Math.min(res, R - L + 1);
            if (sum < s) {
                if (++R == nums.length) break;
                sum += nums[R];
            } else  // sum >= s
                sum -= nums[L++];
        }
        if (res == nums.length + 1)
            return 0;
        return res;
    }
}
```

