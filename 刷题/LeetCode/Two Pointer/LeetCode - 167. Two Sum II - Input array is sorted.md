## LeetCode - 167. Two Sum II - Input array is sorted
#### [题目链接](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/)

> https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/description/

#### 题目
![在这里插入图片描述](images/167_t.png)
## 解析
两种做法，一种二分，一种双指针： 

 - 对于每一个`arr[i]`，在后面的有序数组中二分查找有没有可以匹配的，时间复杂度`n * logn`；
 - 使用双指针，因为是有序的，所以可以通过比较大小决定哪个指针的移动，时间复杂度` n` ；

图:

<div align="center"><img src="assets/1554796953696.png"></div><br>

代码:

```java
class Solution {
    //n * logn
    public int[] twoSum(int[] numbers, int target) {
        for (int i = 0; i < numbers.length; i++) {
            // binary search
            int L = i + 1, R = numbers.length - 1;
            while (L <= R) {
                int mid = L + (R - L) / 2;
                if (numbers[mid] + numbers[i] == target)
                    return new int[]{i + 1, mid + 1};
                else if (numbers[mid] + numbers[i] < target)
                    L = mid + 1;
                else
                    R = mid - 1;
            }

        }
        return null;
    }
}
```
双指针: 一个指针在开头位置，一个在结尾位置。不断往中间靠拢。

<div align="center"><img src="assets/1554797073969.png"></div><br>

代码:

```java
class Solution {
    public int[] twoSum(int[] numbers, int target) {
        for (int l = 0, r = numbers.length - 1; l < r; ) {
            if (numbers[l] + numbers[r] == target)
                return new int[]{l + 1, r + 1};
            else if (numbers[l] + numbers[r] < target)
                l++;
            else
                r--;
        }
        return null;
    }
}
```