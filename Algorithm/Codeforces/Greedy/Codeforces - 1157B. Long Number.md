# Codeforces - 1157B. Long Number

#### [题目链接](https://codeforces.com/problemset/problem/1157/B)

> https://codeforces.com/problemset/problem/1157/B

#### 题目

![1556624729268](assets/1556624729268.png)

### 解析

模拟即可。注意情况`if(f[s[i] - '0'] == s[i] - '0') continue`。

```java
import java.util.*;
import java.io.*;

public class Main{

    static void solve(Scanner in, PrintWriter out){
        int n = in.nextInt();
        char[] s = in.next().toCharArray();
        int[] f = new int[10];
        for(int i = 1; i < 10; i++) f[i] = in.nextInt();
        boolean flag = true; 
        for(int i = 0; i < n; i++){
            if(f[s[i] - '0'] > s[i] - '0'){
                s[i] = (char)(f[s[i] - '0'] + '0');
                flag = false;
            }else {
                if(f[s[i] - '0'] == s[i] - '0') continue; // 注意这里
                if(!flag) break;
            }
        }
        out.println(s);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        PrintWriter out = new PrintWriter(System.out);
        solve(in, out);
        out.close();
    }
}

```

