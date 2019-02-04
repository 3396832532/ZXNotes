## LeetCode - 112. PathSum & Path SumII(树的路径和问题)(递归和非递归)

* [LeetCode - 112. Path Sum](#1)
* [LeetCode - 113. Path Sum II](#leetcode---113-path-sum-ii)

***
### <font id = "1">LeetCode - 112. Path Sum
#### [题目链接](https://leetcode.com/problems/path-sum/)

> https://leetcode-cn.com/problems/path-sum/

#### 题目
![在这里插入图片描述](images/112_t.png)
#### 解析

两种写法，从`sum`的角度去看，<font color = red>可以从`sum`减，也可以从`0`加到`sum`。</font>
第一种: 
```java
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null)
            return false;
        if (root.left == null && root.right == null)
            return root.val == sum;
        int newSum = sum - root.val;
        return hasPathSum(root.left, newSum) || hasPathSum(root.right, newSum);
    }
}
```
第二种: 
```java
class Solution {

    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null)
            return false;
        return helper(root, 0, sum);
    }

    private boolean helper(TreeNode node, int curSum, int sum) {
        if (node == null)
            return false;
        if (node.left == null && node.right == null)
            return curSum + node.val == sum;
        curSum += node.val;
        return helper(node.left, curSum, sum) || helper(node.right, curSum, sum);
    }
}
```
非递归，如果只使用一个栈(或者不使用辅助结构)的话，<font color = red>需要修改原来的树的结构: 

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null)
            return false;
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        TreeNode cur = null;
        while (!stack.isEmpty()) {
            cur = stack.pop();

            if (cur.left == null && cur.right == null && cur.val == sum)
                return true;

            if (cur.right != null) {
                cur.right.val += cur.val;
                stack.push(cur.right);
            }

            if (cur.left != null) {
                cur.left.val += cur.val;
                stack.push(cur.left);
            }
        }
        return false;
    }
}
```
写法可以有很多种，也可以使用一个栈存数，这样就不要改变树的结构:  

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null)
            return false;
        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Integer> sumStack = new Stack<>();//存上到当前节点的数的值
        nodeStack.push(root);
        sumStack.push(sum);
        while (!nodeStack.isEmpty()) {
            TreeNode curNode = nodeStack.pop();
            int curSum = sumStack.pop();

            if (curNode.left == null && curNode.right == null && curNode.val == curSum)
                return true;

            if (curNode.right != null) {
                nodeStack.push(curNode.right);
                sumStack.push(curSum - curNode.val);
            }

            if (curNode.left != null) {
                nodeStack.push(curNode.left);
                sumStack.push(curSum - curNode.val);
            }
        }
        return false;
    }
}
```

这里也可以使用类似`BFS`(层序)的遍历: 
写法真的可以说是多种多样吧，层序使用队列即可，代码和`BFS`差别不大，<font color = red>但是我这里使用了另一个类，没有使用多余的两个队列，也保证没有修改树的结构。

```java
class Solution {

    private class Comb {
        public int curSum;
        public TreeNode node;

        public Comb(TreeNode node, int curSum) {
            this.node = node;
            this.curSum = curSum;
        }
    }

    public boolean hasPathSum(TreeNode root, int sum) {
        if (root == null)
            return false;
        Queue<Comb> queue = new LinkedList<>();
        queue.add(new Comb(root, 0)); //注意对应的关系
        while (!queue.isEmpty()) {
            Comb cur = queue.poll();
            if (cur.node.left == null && cur.node.right == null && cur.curSum + cur.node.val == sum)
                return true;
            if (cur.node.left != null) 
                queue.add(new Comb(cur.node.left, cur.curSum + cur.node.val));
            if (cur.node.right != null) 
                queue.add(new Comb(cur.node.right, cur.curSum + cur.node.val));
        }
        return false;
    }
}
```
***
### <font id = "1">LeetCode - 113. Path Sum II
#### [题目链接](https://leetcode.com/problems/path-sum-ii/)

> https://leetcode.com/problems/path-sum-ii/

#### 题目
![在这里插入图片描述](images/113_t.png)
#### 解析

这题和上面唯一的不同，就是需要记录路径，递归写法很简单：　


* 就是每次先将当前节点加入中间集合(`path`)，然后深度优先遍历；
* 遍历完记得回溯的时候要在`path`集合中移除当前节点；
* 注意递归条件哪里一定不要`return `；


```java
class Solution {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null)
            return res;
        helper(root, 0, sum, new ArrayList<>(), res);
        return res;
    }

    private void helper(TreeNode node, int curSum, int sum, List<Integer> path, List<List<Integer>> res) {
        if (node == null)
            return;
        path.add(node.val);
        if (node.left == null && node.right == null && curSum + node.val == sum) {
            // why do we need new arrayList here?if we are using the same path variable path
            // path will be cleared after the traversal
            res.add(new ArrayList<>(path));
            // return ; // can't do this
        }
        helper(node.left, curSum + node.val, sum, path, res);
        helper(node.right, curSum + node.val, sum, path, res);
        path.remove(path.size() - 1);
    }
}
```

非递归写法自己没有出来，看了讨论区，方法很好: 

* 当前节点`cur`只要不为空，先走到树的最左边节点(第一个`while`循环)；
* 然后取栈顶元素，但是此时还要继续判断栈顶的右孩子的左子树，此时不能`pop()`，因为有孩子还有可能也是有左子树的；
* `pre`节点的作用是为了回溯，记录前一个访问的节点，如果`cur.right == pre`，则说明右子树正在回溯，下面的已经访问完了；
* 实在不懂可以画一个图看看。。。。

```java
class Solution {
    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null)
            return res;

        Stack<TreeNode> stack = new Stack<>();
        ArrayList<Integer> path = new ArrayList<>();
        TreeNode cur = root, pre = null;
        int curSum = 0;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {  //先到最左边
                stack.push(cur);
                curSum += cur.val;
                path.add(cur.val);
                cur = cur.left;
            }
            cur = stack.peek(); //此时cur = 最左边的没有左孩子的节点

            //此时已经到了最左边，但是这个节点还是有可能有右孩子,且这个右孩子又有自己的左子树
            if (cur.right != null && cur.right != pre) { //有孩子不为空且没有被访问过
                cur = cur.right;
            } else { // 右孩子为空　或者　已经访问过 此时先判断是否叶子 然后 开始回溯
                if (cur.left == null && cur.right == null && curSum == sum)
                    res.add(new ArrayList<>(path));
                stack.pop();//出栈
                pre = cur; // 更新pre
                path.remove(path.size() - 1);
                curSum -= cur.val;
                cur = null;//把当前的节点置为空，然后继续从栈中取别的节点
            }
        }
        return res;
    }
}
```

