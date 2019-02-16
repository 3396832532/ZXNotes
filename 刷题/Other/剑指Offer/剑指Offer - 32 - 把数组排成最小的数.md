## 剑指Offer - 32 - 把数组排成最小的数

#### [题目链接](https://www.nowcoder.com/practice/8fecd3f8ba334add803bf2a06af1b993?tpId=13&tqId=11185&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

>https://www.nowcoder.com/practice/8fecd3f8ba334add803bf2a06af1b993?tpId=13&tqId=11185&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个正整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。例如输入数组{3，32，321}，则打印出这三个数字能排成的最小数字为321323。

### 解析

这题关键是找到一个排序规则。

```java
import java.util.*;

public class Solution {
    public String PrintMinNumber(int[] numbers) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < numbers.length; i++)
            list.add(numbers[i] + "");
        Collections.sort(list, (o1, o2) -> {
            return (o1 + o2).compareTo(o2 + o1);  //按照降序排列(第一个大于第二个返回1-->升序排列)
        });
        StringBuilder res = new StringBuilder();
        for (String s : list)
            res.append(s);
        return res.toString();
    }
}
```

```java
import java.util.Arrays;

public class Solution {
    public String PrintMinNumber(int[] numbers) {
        int n = numbers.length;
        String[] str = new String[n];
        for (int i = 0; i < n; i++)
            str[i] = String.valueOf(numbers[i]);
        Arrays.sort(str, (s1, s2) -> (s1 + s2).compareTo(s2 + s1));
        StringBuilder res = new StringBuilder();
        for (String s : str)
            res.append(s);
        return res.toString();
    }
}

```

