## 剑指Offer - 28 - 数组中出现次数超过一半的数字

* [解析](#解析)
* [LeetCode - 229. MajorityElementII](#leetcode---229-majorityelementii)

#### [题目链接](https://www.nowcoder.com/practice/e8a1b01a2df14cb2b228b30ee6a92163?tpId=13&tqId=11181&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/e8a1b01a2df14cb2b228b30ee6a92163?tpId=13&tqId=11181&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 数组中有一个数字出现的次数超过数组长度的一半，请找出这个数字。例如输入一个长度为9的数组`{1,2,3,2,2,2,5,4,2}`。由于数字`2`在数组中出现了`5`次，超过数组长度的一半，因此输出`2`。如果不存在则输出0。

### 解析

三种写法。

#### 1、思路一

使用`map`来保存每个元素出现的次数，只要某个元素次数超过`array.length/2`就返回，很简单。

代码:

```java
import java.util.HashMap;

public class Solution {
    public int MoreThanHalfNum_Solution(int[] array) {
        if (array == null || array.length == 0)
            return 0;
        if (array.length == 1)
            return array[0];
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            map.put(array[i], map.getOrDefault(array[i], 0) + 1);
            if (map.get(array[i]) > array.length / 2)
                return array[i];
        }
        return 0;
    }
}
```

#### 2、思路二

使用类似快速排序`partition`的思想: 

 *  对于次数超过一半的数字，**则数组中的中位数一定是该数字**(**如果数组中真的存在次数超过一半的数字**)；
 *  所以我们可以利用`partition()`然后将找到那个数，这个数可以将数组划分成左右两边的数的个数相同的两部分。时间复杂度为O(n)。关于快速排序可以看[这篇博客](https://blog.csdn.net/zxzxzx0119/article/details/79826380#t8)；

* 注意这里不是三路快排(返回的不是等于区域的两个下标)，而是`<=key`的在`[L，border]`之间，`>key`的在`[border+1，R]`之间，而`arr[border] = key`(划分数)，因为我模仿的是快排，最后交换了`>`区域的最后一个数和`arr[more]`和划分数`arr[R]`；

具体实现看代码。

```java
public class Solution {

    //对于次数超过一半的数字，则数组中的中位数一定是该数字，(如果数组中真的存在次数超过一半的数字)，时间复杂度为O(n)
    public int MoreThanHalfNum_Solution(int[] array) {
        if (array.length == 0 || array == null)
            return 0;
        if (array.length == 1)
            return array[0];
        int L = 0, R = array.length - 1;
        int border = partition(array, L, R);
        int mid = array.length / 2; //中间位置
        while (border != mid) {
            if (mid < border) {//mid在左边,去左边找
                R = border - 1; //更新R   // array[border]那个一定等于那个划分数 我模仿的是三路快排，最后swap(R,more)
                border = partition(array, L, R);
            } else {
                L = border + 1;
                border = partition(array, L, R);
            }
        }
        int res = array[mid];
        int times = 0;
        for (int i = 0; i < array.length; i++)
            if (res == array[i])
                times++;
        if (times * 2 <= array.length)
            return 0;
        return res;
    }

    private int partition(int[] arr, int L, int R) {
        int less = L - 1;
        int more = R;
        swap(arr, L + (int) (Math.random() * (R - L + 1)), R);//随机选取一个数 用来和arr[R]划分
        int key = arr[R];//选取arr[R]作为划分数
        int cur = L;
        while (cur < more) {
            if (arr[cur] < key)
                swap(arr, ++less, cur++); //把这个比num小的数放到小于区域的下一个，并且把小于区域扩大一个单位
            else if (arr[cur] > key)
                //把这个比num大的数放到大于去余的下一个，并且把大于区域扩大一个单位
                //同时，因为从大于区域拿过来的数是未知的，所以不能cur++ 还要再次判断一下arr[cur]
                swap(arr, --more, cur);
            else //否则的话就直接移动
                cur++;
        }
        swap(arr, more, R); //把最后那个数(arr[R](划分数))放到中间
        return more;  //返回的是 <= 区域的右边界
    }

    private void swap(int[] arr, int i, int j) {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
```

#### 3、思路三－摩尔投票解法

和[**这题的第四种方法一样**](https://github.com/ZXZxin/ZXNotes/blob/master/%E5%88%B7%E9%A2%98/LeetCode/DivideConquer/LeetCode%20-%20169.%20Majority%20Element%20(%E8%AE%A1%E6%95%B0%20%2B%20%E4%BD%8D%E8%BF%90%E7%AE%97%20%2B%20Partition%20%2B%20%E5%88%86%E6%B2%BB).md#%E7%BB%B4%E6%8A%A4%E6%9B%B4%E6%96%B0%E6%96%B9%E6%B3%95)。

思路:

* 如果有符合条件的数字，**则它出现的次数比其他所有数字出现的次数和还要多**；
* 在遍历数组时保存两个值：一个是数组中每次遍历的候选值`candi`，另一个当前候选值的次数`times`；
* 遍历时，若当前值它与之前保存的候选值`candi`相同，则次数`times`加1，否则次数减`1`；若次数为`0`，则保存下一个新的数字`candi`，并将新的次数`times`置为1；
* 遍历结束后，所保存的数字(剩下的)即为所求。当然还需要判断它是否符合条件(因为有可能没有数字次数`>N/2)`；

> 详细说法:
>
> 我们把变量 `candi` 叫作候选，`times` 叫作次数，先看第一个for循环。
>
> * `times==0` 时，表示当前没有候选，则把当前数 `arr[i]`设成候选，同时把 `times` 设置成`1`。
>
> * `times!=0` 时，表示当前有候选，如果当前的数 `arr[i]`与候选一样，就把`times` 加 `1`；如果当前的数 `arr[i]`与候选不一样，就把` times` 减 `1`, 减到`0`则表示又没有候选了。
>
> 具体的意思是: 当没有候选时，我们把当前的数作为候选，说明我们找到了两个不同的数中的第一个，当有候选且当前的数和候选一样时，说明目前没有找到两个不同的数中的另外一个, 反而是同一种数反复出现了, 那么就把` times++`表示反复出现的数在累计自己的点数。当有候选且当前的数和候选不一样时，说明找全了两个不同的数，但是候选可能在之前多次出现，如果此时把候选完全换掉，候选的这个数相当于一下被删掉了多个，对吧? 所以这时候选“付出”一个自己的点数，即` times` 减 1，然后当前数也被删掉。这样还是相当于一次删掉了两个不同的数。当然，如果 `times` 被减到为`0`，说明候选的点数完全被消耗完，那么又表示候选空缺，`arr` 中的下一个数(`arr[i+1]`)就又被作为候选。
>
> 综上所述，第一个 for 循环的实质就是我们的核心解题思路，一次在数组中删掉两个不同的数，不停地删除，直到剩下的数只有一种，如果一个数出现次数大于一半，则这个数最后一定会被剩下来，也就是最后的 `candi` 值。
>
> 检验:
>
> 这里请注意，一个数出现次数虽然大于一半，它肯定会被剩下来，但那并不表示剩下来的数一定是符合条件的。例如，1，2，1。其中 1 符合出现次数超过了一半，所以1肯定会剩下来。再如 1，2，3，其中没有任何一个数出现的次数超过了一半，可 3 最后也剩下来了。所以 第二个 for 循环的工作就是检验最后剩下来的那个数(即` candi`) 是否真的是出现次数大于一半的数。如果 `candi` 都不符合条件，那么其他的数也一定都不符合，说明 `arr` 中没有任何一个数出现了一半以上。

代码:

```java
public class Solution {
    public int MoreThanHalfNum_Solution(int[] array) {
        if (array.length == 0 || array == null)
            return 0;
        if (array.length == 1)
            return array[0];
        int candi = 0, times = 0;
        for (int i = 0; i < array.length; i++) {
            if (times == 0) {
                candi = array[i];
                times = 1;
            } else if (array[i] == candi) {//又遇到一个同样的，累加
                times++;
            } else {// times != 0 && array[i] != res
                times--;
            }
        }
        // 最后一定要检验，不一定就是res
        times = 0;
        for (int i = 0; i < array.length; i++)
            if (array[i] == candi)
                times++;
        if (times * 2 > array.length)
            return candi;
        return 0;
    }
}
```

#### 4、摩尔投票两个变形题

#### LeetCode - 229. MajorityElementII

第三种解法有两种变形题目，且都可以用摩尔投票问题解决:

*  求数组中`>n/3`的次数的数(最多两个)；
*  求数组中`>n/k`的次数的数； 

先看第一个**求数组中`>n/3`的次数的数**：这个题目来自[LeetCode229MajorityElement II](https://leetcode.com/problems/majority-element-ii/description/)，求出数组中`>n/3`次数的数。

解析如下，和两个类似，只不过都多了一个变量:

* 和`>n/2`次数的数解题方法很相似，`>n/2`的候选人`candi`只有一个，统计次数只有一个`times`；
* 而`>n/3`次数的数解题是设置两个候选人`candi1`和`candi2`，并且设计两个统计次数`count1`和`count2`，按照类似的方法统计；
* 按照投票的说法，大于`n/3`次数的解题方法是: 先选出两个候选人`candi1、candi2`，如果投`candi1`，则`candi1`的票数`count1++`，如果投`candi2`，则`candi2`的票数`count2++`；
* 如果既不投`candi1`，也不投`candi2`，那么检查此时是否`candi1`和`candi2`候选人的票数是否已经为`0`，如果为`0`，则需要更换新的候选人；如果都不为`0`，则`candi1`和`candi2`的票数都要**减一**；当然最后也需要看看是否两个候选人的票数超过`nums.length / 3`;

`LeetCode - 229. Majority Element II`题解代码如下:

```java
class Solution {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> res = new ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;
        if (nums.length == 1) {
            res.add(nums[0]);
            return res; 
        } 

        int candi1 = 0, candi2 = 0;
        int count1 = 0, count2 = 0;

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == candi1) {
                count1++;
            } else if (nums[i] == candi2) {
                count2++;
            } else if (count1 == 0) {
                candi1 = nums[i];
                count1 = 1;
            } else if (count2 == 0) {
                candi2 = nums[i];
                count2 = 1;
            } else { // count1 != 0 && nums[i] != cand1 && count2 != 0 && cand2 != nums[i]
                count1--;
                count2--;
            }
        }

        //此时选出了两个候选人，需要检查
        count1 = 0;
        count2 = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == candi1) {
                count1++;
            } else if (nums[i] == candi2) {
                count2++;
            }
        }
        if (count1 > nums.length / 3)
            res.add(candi1);
        if (count2 > nums.length / 3)
            res.add(candi2);
        return res;
    }
}
```

然后看第二个问题**求数组中`>n/k`的次数的数**

思路:

一次在数组中删掉`K`个不同的数，不停地删除，直到剩下的数的种类不足 `K`，那么，如果某些数在数组中出现次数大于 `n/k`，则这些数最后一定会被剩下来。

在`>n/2`的问题中，解决的办法是立了 1个候选 `candi`，以及这个候选的` times` 统计。这个问题要立 `K-1` 个候
选，然后有` K-1` 个 `times` 统计。具体过程如下。

遍历到 `arr[i]`时，看 `arr[i]`是和否与已经被选出的某一个候选相同，如果与某一个候选相同，就把属于那个候选的点数统计加 `1`。如果与所有的候选都不相同，先看当前的候选是否选满了，`K-1` 就是满，否则就是不满；

* 如果不满，把 `arr[i]`作为一个新的候选，属于它的点数初始化为 1。
* 如果已满， 说明此时发现了天个不同的数，`arr[i]`就是第`K`个。此时把每一个候选各自的点数全部减 1，表示每个候选“付出”一个自己的点数。如果某些候选的点数在减 1之后等于0，则还需要把这些候选都删除，候选又变成不满的状态。
* 在遍历过程结束后，再遍历一次 `arr`，验证被选出来的所有候选有哪些出现次数真的大于 `n/k`，符合条件的候选就存入结果；

这里用**LeetCode - 229. Majority Element II**来测试我们的程序，可以发现是对的:

```java
class Solution {
    public List<Integer> majorityElement(int[] nums) {
        return printKMajority(nums, 3);
    }

    //找出数组中出现次数 > N/K的, 创建空间为O(k)的候选人的集合
    public List<Integer> printKMajority(int[] arr, int k) {
        ArrayList<Integer> res = new ArrayList<>();
        if (k < 2)
            return res;
        HashMap<Integer, Integer> candis = new HashMap<>();

        for (int i = 0; i < arr.length; i++) {
            if (candis.containsKey(arr[i])) { //在候选人的集合中有这个候选人了
                candis.put(arr[i], candis.get(arr[i]) + 1); //给他的票数+1
            } else {     //与所有的候选人都不同
                //候选人的集合已经满了(当前是第K个)，要把所有的候选人的票数减一，如果某些人的票数是1就要移除这个候选人
                if (candis.size() == k - 1) {
                    ArrayList<Integer> removeList = new ArrayList<>();
                    for (Map.Entry<Integer, Integer> entry : candis.entrySet()) {
                        Integer key = entry.getKey();
                        Integer value = entry.getValue();
                        if (value == 1) {
                            removeList.add(key);
                        } else {
                            candis.put(key, value - 1);
                        }
                    }
                    //删除那些value = 1的候选人
                    for (Integer removeKey : removeList)
                        candis.remove(removeKey);
                } else {     //没有满,把这个加入候选人的集合
                    candis.put(arr[i], 1);
                }
            }
        }

        //检查候选人是否真的满足条件
        for (Map.Entry<Integer, Integer> entry : candis.entrySet()) {
            Integer key = entry.getKey();
            int sum = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == key)
                    sum++;
            }
            if (sum > arr.length / k)
                res.add(key);
        }
        return res;
    }
}
```

