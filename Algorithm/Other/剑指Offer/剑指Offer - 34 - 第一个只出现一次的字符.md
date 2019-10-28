## 剑指Offer - 34 - 第一个只出现一次的字符

#### [题目链接](https://www.nowcoder.com/practice/1c82e8cf713b4bbeb2a5b31cf5b0417c?tpId=13&tqId=11187&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/1c82e8cf713b4bbeb2a5b31cf5b0417c?tpId=13&tqId=11187&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

>  在一个字符串(0<=字符串长度<=10000，全部由字母组成)中找到**第一个只出现一次的字符**，并返回它的位置， 如果没有则返回 -1（需要区分大小写）.

### 解析

这题也算一个简单题了。

直接统计每个单词出现的次数，然后从头开始扫到第一个次数为`1`的返回就可以了。

因为字母`ascii`在`65 ~ 122`，所以开一个`58`的数组就可以了。

```java
public class Solution {
    public int FirstNotRepeatingChar(String str) {
        if (str == null || str.length() == 0) return -1;
        int[] counts = new int[58]; // 65('A') ~ 122 'z'
        for (int i = 0; i < str.length(); i++)
            counts[str.charAt(i) - 'A']++;
        for (int i = 0; i < str.length(); i++)
            if (counts[str.charAt(i) - 'A'] == 1)
                return i;
        return -1;
    }
}
```

也可以用Map的写法:

```java
import java.util.*;
public class Solution {
    public int FirstNotRepeatingChar(String str) {
        if (str == null || str.length() == 0) return -1;
        HashMap<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < str.length(); i++) 
            map.put(str.charAt(i), map.getOrDefault(str.charAt(i), 0) + 1);
        for (int i = 0; i < str.length(); i++) 
            if (map.get(str.charAt(i)) == 1)
                return i;
        return -1;
    }
}
```

