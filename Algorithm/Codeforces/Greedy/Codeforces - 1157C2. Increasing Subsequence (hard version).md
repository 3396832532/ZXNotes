# Codeforces - 1157C2. Increasing Subsequence

#### [题目链接](https://codeforces.com/problemset/problem/1157/C2)

> https://codeforces.com/problemset/problem/1157/C2

#### 题目

![1556627939953](assets/1556627939953.png)

### 解析

注意一下相等的时候，直接计算一下返回即可。

```java
import java.util.*;
import java.io.*;

public class Main{

    static void solve(Scanner in, PrintWriter out){
        int n = in.nextInt();
        int[] a = new int[n];
        for(int i = 0; i < n; i++) a[i] = in.nextInt();
        
        int L = 0, R = n-1;
        int pre = -1;
        int res = 0;
        StringBuilder sb = new StringBuilder();
        while(L <= R && Math.max(a[L], a[R]) > pre ){
            if(a[L] == a[R]){ //judge
                int r1 = 1, r2 = 1;
                for(int t = L+1; t < R && a[t] > a[t-1]; t++, r1++);
                for(int t = R-1; t > L && a[t] > a[t+1]; t--, r2++);
                if(r1 > r2){
                    res += r1;
                    for(int i = 0; i < r1; i++) sb.append("L");
                }else {
                    res += r2;
                    for(int i = 0; i < r2; i++) sb.append("R");
                }
                break;
            }
            if(a[L] > pre && a[R] > pre){
                if(a[L] < a[R]){
                    res++;
                    sb.append("L");
                    pre = a[L];
                    L++;
                    if(L == n) break;
                }else {
                    res++;
                    sb.append("R");
                    pre = a[R];
                    R--;
                    if(R == -1)break;
                }
            }else {
                if(a[L] > pre){
                    res++;
                    sb.append("L");
                    pre = a[L];
                    L++;
                    if(L == n) break;
                }else {
                    res++;
                    sb.append("R");
                    pre = a[R];
                    R--;
                    if(R == -1)break;
                }
            }
        }
        out.println(res);
        out.println(sb.toString());
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        PrintWriter out = new PrintWriter(System.out);
        solve(in, out);
        out.close();
    }
}

```

