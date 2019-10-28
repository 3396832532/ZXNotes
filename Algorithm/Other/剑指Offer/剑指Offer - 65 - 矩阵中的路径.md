## 剑指Offer - 65 - 矩阵中的路径

#### [题目链接](https://www.nowcoder.com/practice/c61c6999eecb4b8f88a98f66b273a3cc?tpId=13&tqId=11218&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/c61c6999eecb4b8f88a98f66b273a3cc?tpId=13&tqId=11218&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

请设计一个函数，用来判断在一个矩阵中是否存在一条包含某字符串所有字符的路径。路径可以从矩阵中的任意一个格子开始，每一步可以在矩阵中向左，向右，向上，向下移动一个格子。如果一条路径经过了矩阵中的某一个格子，则之后不能再次进入这个格子。 例如 `a b c e s f c s a d e e` 这样的3 X 4 矩阵中包含一条字符串"bcced"的路径，但是矩阵中不包含"abcb"路径，因为字符串的第一个字符b占据了矩阵中的第一行第二个格子之后，路径不能再次进入该格子。

### 解析

比较简单的dfs。

注意边界判断`if (cur == str.length-1 && matrix[x * c + y] == str[cur]) return true;`。不要判断`cur == str.length`。。

```java
public class Solution {

    final int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private boolean vis[];
    private int r, c;

    private boolean dfs(char[] matrix, char[] str, int cur, int x, int y) {
        if (cur == str.length-1 && matrix[x * c + y] == str[cur]) return true;
        if (vis[x * c + y] || matrix[x * c + y] != str[cur]) return false;
        vis[x * c + y] = true;
        for (int i = 0; i < 4; i++) {
            int nx = x + dir[i][0];
            int ny = y + dir[i][1];
            if (nx >= 0 && nx < r && ny >= 0 && ny < c && !vis[nx * c + ny] &&
                    (dfs(matrix, str, cur + 1, nx, ny))) return true;
        }
        vis[x * c + y] = false;
        return false;
    }

    public boolean hasPath(char[] matrix, int rows, int cols, char[] str) {
        r = rows;
        c = cols;
        vis = new boolean[r * c];
        for (int i = 0; i < matrix.length; i++) {
            int x = i / c;
            int y = i % c;
            if (dfs(matrix, str, 0, x, y)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
//        char[] matrix = {'a', 'b' ,'c' ,'e' ,'s' ,'f','c' ,'s','a' ,'d','e','e'};
        char[] matrix = {'A','A','A','A','A','A','A','A','A','A','A','A'};
//        char[] str = {'b','c','c','e','d'};
//        char[] str = {'a', 'b','c','d'};
        char[] str = {'A','A','A','A','A','A','A','A','A','A','A','A','A'};
        System.out.println(new Solution().hasPath(matrix, 3, 4, str));
    }
}
```

