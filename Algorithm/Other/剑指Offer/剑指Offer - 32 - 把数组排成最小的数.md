## 剑指Offer - 32 - 把数组排成最小的数

#### [题目链接](https://www.nowcoder.com/practice/8fecd3f8ba334add803bf2a06af1b993?tpId=13&tqId=11185&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

>https://www.nowcoder.com/practice/8fecd3f8ba334add803bf2a06af1b993?tpId=13&tqId=11185&tPage=2&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 输入一个正整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。例如输入数组{3，32，321}，则打印出这三个数字能排成的最小数字为321323。

### 解析

这题关键是找到一个排序规则。然后数组根据这个规则排序就能排成一个最小的数字。

也就是是给出两个数字 a 和 b，**我们需要确定一个规则判断 a 和 b 哪个应该排在前面，而不是仅仅比较这两个数字的值哪个更大**。

如果只看长度不行，但是只看数字大小也不行。但是如果我们限定长度相同呢?

* 根据题目的要求，两个数字 a 和 b 能拼接成数字 ab 和 ba(**此时ab和ba位数长度相同**)。如果`ab<ba`，那么我们应该打印出 ab，也就是 a 应该排在 b 的前面，我们定义此时 a 小于b；(若`ab < ba` 则 `a < b`)
* 反之，如果 `ba<ab`，我们定义b 小于a。 (若`ab > ba` 则 `a > b`)
* 如果` ab=ba`，a等于b。(若`ab = ba` 则 `a = b`)

如果直接用数字去拼接nm，可能会溢出。但是我们可以直接用字符串拼接，然后定义字符串的比较规则即可。

举个例子:

* `a = "3"、b = "31"`，不能简单的将a放在b的前面，
* 而是因为`331 > 313`( `ab > ba`)，应该将b放在a的前面；

代码如下:

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

也可以用字符串数组排序:

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

