## 剑指Offer - 44 - 翻转单词顺序列

#### [题目链接](https://www.nowcoder.com/practice/3194a4f4cf814f63919d0790578d51f3?tpId=13&tqId=11197&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/3194a4f4cf814f63919d0790578d51f3?tpId=13&tqId=11197&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

> 牛客最近来了一个新员工Fish，每天早晨总是会拿着一本英文杂志，写些句子在本子上。同事Cat对Fish写的内容颇感兴趣，有一天他向Fish借来翻看，但却读不懂它的意思。例如，`“student. a am I”`。后来才意识到，这家伙原来把句子单词的顺序翻转了，正确的句子应该是`“I am a student.”`。Cat对一一的翻转这些单词顺序可不在行，你能帮助他么？
>
> 即:
>
> 输入一个英文句子，翻转句子中单词的顺序，但单词内字符的顺序不变。为简单起见，标点符号和普通字母一样处理。例如输入字符串`"I am a student. "`，则输出`"student. a am I"`。

### 解析

比较简单的题。两种方法做。

#### 1、方法一

直接从后面开始构造结果字符串即可，中间加上`" "`即可。很简单。

```java
public class Solution {
    public String ReverseSentence(String str) {
        if(str.trim().equals("")) return str;// 注意"    "这种空格多的情况
        StringBuilder sb = new StringBuilder();
        String[] strings = str.split(" ");
        for(int i = strings.length - 1; i > 0; i--)
            sb.append(strings[i]).append(" ");
        sb.append(strings[0]);
        return sb.toString();
    }
}
```

#### 2、方法二(剑指Offer书上的解法)

也很简单:

* 就是先把整个字符串先翻转一下，例如`"I am a student. "`翻转成`".tneduts a ma I"`；
* 然后再翻转每个单词中字符的顺序即可。
* 翻转某个字符的某个区间写成一个函数`reverse()`即可；

代码:

```java
public class Solution {
    public String ReverseSentence(String str) {
        if(str.trim().equals("")) return str;
        int n = str.length();
        char[] chs = str.toCharArray();
        // 1、先翻转整个字符串
        reverse(chs, 0, n-1);

        // 2、然后翻转其中的每一个单词
        for(int i = 0; i < n; ){
            while(i < n && chs[i] == ' ')i++; // 跳过空格
            int L = i, R = i;
            for(; i < n && chs[i] != ' '; i++, R++); // chs[R] = ' '
            reverse(chs, L, R-1); // notice is R - 1
        }
        return new String(chs);
    }
    // 翻转chs在[L, R]范围内的字符
    private void reverse(char[] chs, int L, int R){
        for(; L < R; L++, R--){
            char c = chs[L];
            chs[L] = chs[R];
            chs[R] = c;
        }
    }
}
```

