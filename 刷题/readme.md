## 刷题模板

#### Java读入

```java
import java.io.*;
import java.util.*;

public class Main {

    static class FR {
        BufferedReader br;
        StringTokenizer tk;

        FR(InputStream stream) {
            br = new BufferedReader(new InputStreamReader(stream), 32768);
            tk = null;
        }

        String next() {
            while (tk == null || !tk.hasMoreElements()) {
                try {
                    tk = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return tk.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    static void solve(InputStream stream, PrintWriter out) {
        FR in = new FR(stream);
   
    }

    public static void main(String[] args) {
        OutputStream os = System.out;
        InputStream is = System.in;
        PrintWriter out = new PrintWriter(os);
        solve(is, out);
        out.close(); // 不关闭就没有输出
    }
}

```

`MainTest.java` : 从`in.txt`中读入测试用例:

```java
import java.io.*;

public class MainTest {

    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        String src = "/home/zxzxin/Java_Maven/Algorithm/src/main/java/in.txt";
        FileInputStream in = new FileInputStream(src);
        OutputStream os = System.out;
        PrintWriter out = new PrintWriter(os);

        Main.solve(in, out);//调用solve

        out.close();
        long end = System.currentTimeMillis();
        System.err.println("Time elapsed: " + (end - start) * 1.0 / 1000);
    }
}
```

`GenerateInTxt.java` : 用来生成`in.txt`的内容:

```java
import java.io.*;
import java.util.Random;

public class GenerateInTxt {

    static String str(int n){
        return n + "";
    }

    public static void main(String[] args) throws Exception {
        String src = "/home/zxzxin/Java_Maven/Algorithm/src/main/java/in.txt";
        FileWriter fw = new FileWriter(src);
        Random rnd = new Random();

        int n = rnd.nextInt(100);
        int m = rnd.nextInt(100);
        fw.append(str(n) + " " + str(m) + "\n");
        for(int i = 0; i < m; i++){
            int a = rnd.nextInt(n) + 1;
            int b = rnd.nextInt(n) + 1;
            fw.append(str(a) + " " + str(b) + "\n");
        }

        fw.flush();
        fw.close();
    }
}
```


## C++

```c++
#include <bits/stdc++.h>

using namespace std;
const int maxn = 100005;
const int P = 1000000000 + 7;

bool uin(int a, int b) { return a > b; }
bool uax(int a, int b) { return a < b; }

int main() {
    ios::sync_with_stdio(false);
    cin.tie(0);
#ifndef ONLINE_JUDGE
    freopen("in.txt","r",stdin);
#endif
// write code



// code end
#ifndef ONLINE_JUDGE
    cerr << "Time elapsed: " << 1.0 * clock() / CLOCKS_PER_SEC << " s.\n";
#endif
    return 0;
}

```

