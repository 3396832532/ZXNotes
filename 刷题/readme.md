## 刷题模板

#### 普通Java读入

`Main.java`

```java
import java.io.*;
import java.util.*;

public class Main {

    static PrintStream out = System.out;

    static void solve(Scanner in) {

    }

    public static void main(String[] args) {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        solve(in);
    }
}
```

`MainTest.java` : 从`in.txt`中读入测试用例:

```java
import java.io.*;
import java.util.*;

public class MainTest {

    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        String src = "/home/zxzxin/Java_Maven/Algorithm/src/main/java/in.txt";
        Scanner in = new Scanner(new FileInputStream(src));

        Main.solve(in);//调用solve

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

#### 快速Java读入

`Main.java`

```java
import java.io.*;
import java.util.*;

public class Main {

    static PrintStream out = System.out;

    static class FastReader {
        public BufferedReader br;
        public StringTokenizer token;

        public FastReader(InputStream in) {
            br = new BufferedReader(new InputStreamReader(in), 32768);
            token = null;
        }

        public String next() {
            while (token == null || !token.hasMoreTokens()) {
                try {
                    token = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return token.nextToken();
        }

        public int nextInt() {
            return Integer.parseInt(next());
        }
    }
	// write the solution
    static void solve(InputStream stream) {
        FastReader in = new FastReader(stream);


    }
    public static void main(String[] args) {
        solve(System.in);
    }
}
```

`MainTest.java`:

```java
import java.io.*;

public class MainTest {

    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        String src = "/home/zxzxin/Java_Maven/Algorithm/src/main/java/in.txt";
        FileInputStream in = new FileInputStream(src);//这里稍有改动

        Main.solve(in);//调用solve

        long end = System.currentTimeMillis();
        System.err.println("Time elapsed: " + (end - start) * 1.0 / 1000);
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

