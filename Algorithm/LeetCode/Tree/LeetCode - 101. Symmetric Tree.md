# LeetCode - 101. Symmetric Tree

#### [题目链接](https://leetcode.com/problems/symmetric-tree/)

> https://leetcode.com/problems/symmetric-tree/

#### 题目

![1554698074830](assets/1554698074830.png)

## 解析



递归思路。

* 首先根节点，只要`pRoot.left`和`pRoot.right`对称即可；

* 左右节点的**值相等**且对称子树`left.left 和 right.right对称` ，且`left.rigth和right.left也对称`。

<div align="center"><img src="assets/1554698812545.png"></div><br>

递归:

```java
public class Solution {

    public boolean isSymmetric(TreeNode root){ 
        return root == null ? true : mirror(root.left, root.right);
    }

    private boolean mirror(TreeNode left, TreeNode right) {
        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.val == right.val
                && mirror(left.left, right.right)
                && mirror(left.right, right.left);
    }
}
```

非递归:

层次遍历即可，注意队列中要成对成对的取。

```java
public class Solution {

    boolean isSymmetric(TreeNode root) { 
        if (root == null) return true;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root.left);
        queue.add(root.right);
        while (!queue.isEmpty()) {
            TreeNode right = queue.poll();
            TreeNode left = queue.poll();
            if (left == null && right == null) continue;
            if (left == null || right == null) return false;
            if (left.val != right.val) return false;
            //成对插入
            queue.add(left.left); queue.add(right.right);
            queue.add(left.right); queue.add(right.left);
        }
        return true;
    }
}
```

栈也可以:

```java
public class Solution {

    boolean isSymmetric(TreeNode root) {
        if (root == null) return true;
        Stack<TreeNode> s = new Stack<>();
        s.push(root.left);
        s.push(root.right);
        while (!s.isEmpty()) {
            TreeNode right = s.pop();
            TreeNode left = s.pop();
            if (left == null && right == null) continue;
            if (left == null || right == null) return false;
            if (left.val != right.val) return false;
            //成对插入
            s.push(left.left); s.push(right.right);
            s.push(left.right); s.push(right.left);
        }
        return true;
    }
}
```
