# LeetCode - 63. Unique Paths II(有障碍物的不同路径)

 - 记忆化
 - 二维dp
 - 一维dp

#### [题目链接](https://leetcode.com/problems/unique-paths-ii/description/)

> https://leetcode.com/problems/unique-paths-ii/description/

#### 题目
![在这里插入图片描述](images/63_t.png)

## 1、记忆化
和`LeetCode - 62`唯一不同的就是这里当格子是`1`的时候直接不能走。返回`0`即可。

递归思路如下。

<div align="center"><img src="images/63_ss.png"></div><br>

代码:

```java
class Solution {

    private int[][] dp;
    private int n;
    private int m;

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        n = obstacleGrid.length;
        m = obstacleGrid[0].length;
        dp = new int[n][m];
        for (int i = 0; i < dp.length; i++)
            Arrays.fill(dp[i], -1);
        return rec(obstacleGrid, 0, 0);
    }

    public int rec(int[][] G, int i, int j) {
        if (i == n - 1 && j == m - 1) return G[i][j] == 0 ? 1 : 0;
        if (dp[i][j] != -1) return dp[i][j];
        if (i == n - 1)
            dp[i][j] = G[i][j] == 1 ? 0 : (G[i][j + 1] == 0 ? rec(G, i, j + 1) : 0);
        else if (j == m - 1) {
            dp[i][j] = G[i][j] == 1 ? 0 : (G[i + 1][j] == 0 ? rec(G, i + 1, j) : 0);
        } else {
            int right = G[i][j + 1] == 0 ? rec(G, i, j + 1) : 0;
            int down = G[i + 1][j] == 0 ? rec(G, i + 1, j) : 0;
            dp[i][j] = (G[i][j] == 1) ? 0 : (right + down);
        }
        return dp[i][j];
    }
}
```
简单优化: 

 - 由于右边和下面的不管是`0`还是真的`>0`的值，都是一个值，不影响；
 - 或者说每一个`(i,j)`没有必要重复判断`(i,j+1)`或者`(i+1,j)`位置的值，所以只需要判断自己`(i,j)`；

```java
class Solution {

    private int[][] dp;
    private int n;
    private int m;

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        n = obstacleGrid.length;
        m = obstacleGrid[0].length;
        dp = new int[n][m];
        for (int i = 0; i < dp.length; i++)
            Arrays.fill(dp[i], -1);
        return rec(obstacleGrid, 0, 0);
    }

    public int rec(int[][] G, int i, int j) {
        if (i == n - 1 && j == m - 1) return G[i][j] == 0 ? 1 : 0;
        if (dp[i][j] != -1) return dp[i][j];
        if (i == n - 1)
            dp[i][j] = G[i][j] == 1 ? 0 : rec(G, i, j + 1);
        else if (j == m - 1)
            dp[i][j] = G[i][j] == 1 ? 0 : rec(G, i + 1, j);
        else
            dp[i][j] = (G[i][j] == 1) ? 0 : (rec(G, i, j + 1) + rec(G, i + 1, j));
        return dp[i][j];
    }
} 
```


## 2、二维dp

根据递归可以改成dp。(下面从从右下角到左上角，也可以反过来)

```java
class Solution {

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int n = obstacleGrid.length;
        int m = obstacleGrid[0].length;
        int[][] dp = new int[n][m];
        dp[n - 1][m - 1] = obstacleGrid[n - 1][m - 1] == 1 ? 0 : 1;
        for (int i = n - 2; i >= 0; i--) dp[i][m - 1] = obstacleGrid[i][m - 1] == 1 ? 0 : dp[i + 1][m - 1];
        for (int j = m - 2; j >= 0; j--) dp[n - 1][j] = obstacleGrid[n - 1][j] == 1 ? 0 : dp[n - 1][j + 1];
        for (int i = n - 2; i >= 0; i--) {
            for (int j = m - 2; j >= 0; j--)
                dp[i][j] = obstacleGrid[i][j] == 1 ? 0 : (dp[i][j + 1] + dp[i + 1][j]);
        }
        return dp[0][0];
    }
}
```

## 3、一维dp
滚动优化。

```java
class Solution {

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int n = obstacleGrid.length;
        int m = obstacleGrid[0].length;
        int[] dp = new int[m];
        dp[m - 1] = obstacleGrid[n - 1][m - 1] == 1 ? 0 : 1;
        for (int j = m - 2; j >= 0; j--) dp[j] = obstacleGrid[n - 1][j] == 1 ? 0 : dp[j + 1];
        for (int i = n - 2; i >= 0; i--) {
            dp[m - 1] = obstacleGrid[i][m - 1] == 1 ? 0 : dp[m - 1];
            for (int j = m - 2; j >= 0; j--) {
                dp[j] = obstacleGrid[i][j] == 1 ? 0 : (dp[j + 1] + dp[j]);
            }
        }
        return dp[0];
    }
}
```

