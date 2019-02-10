## Codeforeces - 1111B. Average Superhero Gang Power(超人的最大平均战斗力)
#### [题目链接]()
#### 题目

`n`个超人，一开始的战斗力为`arr[i]`，要你用最多`m`次操作，每次操作两种选择: ①移除一个超人；②给某个超人战斗力`+1`。每个超人不能加超过`k`的战斗力。问你最大**平均**战斗力是多少?

![](images/1111B_t.png)

#### 解析

这题不能用贪心来做，必须要枚举所有的**添加的战斗力**和**移除的超人的数目**:

* 先对数组排序，然后用一个`sums`数组存`0~i`的超人的战斗力的和，方便后面的平均战斗力的计算；
* 然后枚举所有的添加战斗力和对应的移除超人的数目，每个数目计算出当前的平均战斗力，然后更新最大战斗力即可；
* **注意注意:** 代码中`k、m`一定要和后面的计算一定要用`long`，不然会错，应该是精度问题；

```java
import java.io.*;
import java.util.*;
import java.text.*;

public class Main {

    public static void main(String[] args){
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        PrintStream out = System.out;
        int n = in.nextInt();
        long k = in.nextInt();
        long m = in.nextInt();
        int[] arr = new int[n];
        for(int i = 0; i < n; i++)
            arr[i] = in.nextInt();
        Arrays.sort(arr);
        long[] sums = new long[n + 1];
        for(int i = 0; i < n; i++)
            sums[i+1] = sums[i] + arr[i];
        double best = ((double)sums[n]) / n; // don't miss this 
        for(int add = 0; add <= m; add++){ 
            long todel = m - add;
            int realdel = (int)(Math.min(n - 1, todel)); // at least 1 member
            long realadd = (Math.min((n - realdel) * k, add)); // 如果add足够就加，如果add不够就只能加add个
            double cur = ((double)(sums[n] - sums[realdel] + realadd)) / (n - realdel); // not divide realadd
            if(cur > best)
                best = cur;
        }
        out.println(best);
    }
}

```

提供一开始的思路的代码(相当于贪心)，但是是错误的。

```java
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args){
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        PrintStream out = System.out;
        int n = in.nextInt();
        int k = in.nextInt();
        int m = in.nextInt();
        int[] arr = new int[n];
        for(int i = 0; i < n; i++)
            arr[i] = in.nextInt();
        Arrays.sort(arr);
        double sum = 0;
        int num = 0;
        for(int i = 0; i < n; i++){ 
            if(m <= 0){ 
                num++;
                sum += arr[i];
                m--;
            }else{// m > 0 
                if(i != n-1 && arr[i] != arr[n-1])
                    m--;
                else {  
                    if(arr[i] == arr[n-1]){ 
                        sum += arr[i] * (n - i);
                        sum += m;
                        num += n - i;
                    }else { 
                        num++;
                        sum += m + arr[i];
                    }
                    break;
                }
            }
        }
        out.println(sum*1.0 / num);
    }
}

```

