## Codeforces - 1103A. Grid game

#### [题目链接](https://codeforces.com/problemset/problem/1103/A)

> https://codeforces.com/problemset/problem/1103/A



#### 题目

给一个`4∗4`的格子图和一个`01`串，你要根据`01`串放`1∗2`的木块，如果是`0`就**竖放**一个，是`1`就**横放**一个，一行或者一列满了可以直接消掉。**现在让你根据字符串输出放下木块的坐标，并保证所有操作中没有木块相交(保证游戏可以一直进行)。**

![](images/1103A_t.png)

![](images/1103A_t2.png)

#### 解析

只需要放三列即可:



* 竖的全部放在第**一列**，放满了**两个**就消去；
* 横的全部放在**二三**列，放满了**四个**就消去；



![](images/1103A_s.png)

```java
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args){
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        PrintStream out = System.out;
        String str = in.next();
        int r1 = 1, r2 = 1;
        for(int i = 0; i < str.length(); i++){ 
            char c = str.charAt(i);
            if(c == '0'){ 
                out.println(r1 + " 1");
                r1 = 4 - r1;
            }else { 
                out.println(r2 + " 2");
                if(++r2 > 4)
                    r2 %= 4;
            }
        }
    }
}
```

