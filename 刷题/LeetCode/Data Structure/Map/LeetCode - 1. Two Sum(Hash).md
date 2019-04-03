# LeetCode - 1. Two Sum(Hash)

#### [题目链接](https://leetcode.com/problems/two-sum/)

> https://leetcode.com/problems/two-sum/

#### 题目
![在这里插入图片描述](images/1_t.png)

暴力方法就不说了。这题用Hash。

## 一、方法一

时间复杂度`2 * O(N)`。

* 先遍历一边数组，使用`HashMap`存储每个数的**下标**；
* 然后再遍历一遍数组，寻找`target - nums[i]`在`map`中存不存在，如果存在且**对应的值不等于当前的下标`i`**(即目标元素不能是`nums[i]`本身)，就说明存在解，返回两个下标即可；

图: 

<div align="center"><img src="images/xin_1.png"></div><br>

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length; i++) map.put(nums[i], i); 
        for(int i = 0; i < nums.length; i++){
            int val = target - nums[i];
            if(map.containsKey(val) && map.get(val) != i)
                return new int[]{i, map.get(val)};
        }
        throw new RuntimeException("No such soultion!");
    }
}
```
***
## 二、方法二

时间复杂度`O(N)`。

在检查完当前元素`num[i]`之后(检查`target - nums[i]`)。

再顺便将`{nums[i], i}`放入哈希表，因为当前放入的元素**后面还会回过头来检查这个元素是否是目标元素**。

图: 

<div align="center"><img src="images/xin_1_2.png"></div><br>

```java
class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < nums.length; i++){
            int val = target - nums[i];
            if(map.containsKey(val)) // 肯定不会map.get(val) == i 
                return new int[]{i, map.get(val)};
            map.put(nums[i], i);
        }
        throw new RuntimeException("No such soultion!");
    }
}
```


> 类似的[进阶问题](https://github.com/ZXZxin/ZXBlog/blob/master/%E5%88%B7%E9%A2%98/Other/%E6%9D%82%E9%A2%98/%E5%AD%90%E6%95%B0%E7%BB%84%E7%B4%AF%E5%8A%A0%E5%92%8C%E4%B8%BAaim(%E5%B0%8F%E4%BA%8E%E7%AD%89%E4%BA%8Eaim)%E7%9A%84%E4%B8%89%E4%B8%AA%E9%97%AE%E9%A2%98.md)。