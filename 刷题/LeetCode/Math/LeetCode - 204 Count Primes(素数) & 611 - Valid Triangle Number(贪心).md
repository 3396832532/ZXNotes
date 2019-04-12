

### LeetCode - 611 - Valid Triangle Number

#### [题目链接](https://leetcode.com/problems/valid-triangle-number/)

> https://leetcode.com/problems/valid-triangle-number/

#### 题目大意
给定一个包含非负整数的数组，统计其中可以组成三角形三条边的三元组个数。

![在这里插入图片描述](images/611_t.png)

#### 解析
很巧妙的方法，使用贪心: 

* 先对数组排序（升序降序都可以，这里按照升序排列）；
* 然后初始化`c=num.length - 1`，`b = c-1`，`a = 0`，然后如果`arr[a]  + arr[b] > arr[c]`，那么所有`arr[a ~ b-1]`和`arr[b]`、`arr[c]`之间都可以构成三角形，所以可以加上`b-a`个；
* 否则说明`a`小了，就让`a++`，知道`a == b`退出；

图:

![在这里插入图片描述](images/611_s.png)

代码:

```java
class Solution {
    public int triangleNumber(int[] nums) {
        if(nums == null || nums.length < 3)
            return 0;
        int res = 0;
        Arrays.sort(nums);
        for(int c = nums.length-1; c >= 2; c--){
            for(int b = c-1; b >= 1; b--){
                int a = 0;
                while(a < b){
                    if(nums[a] + nums[b] > nums[c]){
                        res += b-a;
                        break;
                    }
                    a++;
                }
            }
        }
        return res;
    }
}
```

```java
class Solution {
    // more fast
    public int triangleNumber(int[] nums) {
        if(nums == null || nums.length < 3)
            return 0;
        int res = 0;
        Arrays.sort(nums);
        for(int c = nums.length-1; c >= 2; c--){
            int a = 0,b = c-1;
            while(a < b){
                if(nums[a] + nums[b] > nums[c]){
                    res += b-a;
                    b--;
                }else a++;
            }
        }
        return res;
    }
}
```

***
