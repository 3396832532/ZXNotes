# LeetCode - 106. Construct Binary Tree from Inorder and Postorder Traversal

#### [题目链接](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)

> https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/

#### 题目

![1554711156778](assets/1554711156778.png)

## 解析

和上一题[LeetCode - 105. ]

![1554711504651](assets/1554711504651.png)



```java
class Solution {
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        return rec(inorder, 0, inorder.length - 1, postorder, 0, postorder.length - 1);
    }

    public TreeNode rec(int[] in, int iL, int iR, int[] post, int poL, int poR) {
        if (iL > iR || poL > poR) return null;
        TreeNode root = new TreeNode(post[poR]);//最后一个是根
        int lLen = 0; // 左子树长度, 在in[]中找到 post[posR]的位置
        for (int i = iL; i <= iR && in[i] != post[poR]; i++, lLen++) ;
        root.left = rec(in, iL, iL + lLen - 1, post, poL, poL + lLen - 1);
        root.right = rec(in, iL + lLen + 1, iR, post, poL + lLen, poR - 1);
        return root;
    }
}
```

