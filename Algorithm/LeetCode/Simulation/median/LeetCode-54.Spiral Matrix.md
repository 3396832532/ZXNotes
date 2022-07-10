## LeetCode - 54. Spiral Matrix(圈圈打印矩阵)
#### [题目链接](https://leetcode.com/problems/spiral-matrix/description/)

> https://leetcode.com/problems/spiral-matrix/description/

#### 题意
![在这里插入图片描述](images/54_t.png)

#### 解析
 题目不难，但是如果去一个个的打印的话会比较的麻烦。
* 好的方法是使用**矩阵分圈处理**的方式，在矩阵中使用`(ar,ac)`表示左上角，`(br,bc)`表示矩阵的右下角；
* 每次只需要通过这四个变量打印一个矩阵，然后用一个**宏观的函数来调用**打印的局部的函数，这样调理更加清晰；

看图很清晰:

![54_ss.png](images/54_ss.png)

代码：

```java
class Solution {

    private List<Integer> res;

    public List<Integer> spiralOrder(int[][] matrix) {
        res = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0)
            return res;
        int ar = 0,ac = 0;
        int br = matrix.length - 1,bc = matrix[0].length - 1;
        //直到ar > br 或者 ac > bc才不打印了
        while (ar <= br && ac <= bc)  edgePrint(matrix, ar++, ac++, br--, bc--);
        return res;
    }

    public void edgePrint(int[][] matrix, int ar, int ac, int br, int bc) {
        if (ar == br) { //只有一行
            for (int i = ac; i <= bc; i++) res.add(matrix[ar][i]);
        } else if (ac == bc) { // 只有一列
            for (int i = ar; i <= br; i++) res.add(matrix[i][ac]);
        } else {
            for (int i = ac; i <= bc - 1; i++) res.add(matrix[ar][i]);
            for (int i = ar; i <= br - 1; i++) res.add(matrix[i][bc]);
            for (int i = bc; i >= ac + 1; i--) res.add(matrix[br][i]);
            for (int i = br; i >= ar + 1; i--) res.add(matrix[i][ac]);
        }
    }
}
```
