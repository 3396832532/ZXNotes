## 剑指Offer - 60 - 把二叉树打印成多行

#### [题目链接](https://www.nowcoder.com/practice/445c44d982d04483b04a54f298796288?tpId=13&tqId=11213&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/445c44d982d04483b04a54f298796288?tpId=13&tqId=11213&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

从上到下按层打印二叉树，同一层结点从左至右输出。每一层输出一行。

### 解析

[**LeetCode637**](https://github.com/ZXZxin/ZXBlog/blob/master/%E5%88%B7%E9%A2%98/LeetCode/Tree/LeetCode%20-%20637.%20Average%20of%20Levels%20in%20Binary%20Tree(%E6%B1%82%E6%A0%91%E7%9A%84%E6%AF%8F%E4%B8%80%E5%B1%82%E7%9A%84%E5%B9%B3%E5%9D%87%E5%80%BC).md)已经做过，而且稍微加强了一点。具体可以看那个题目解析。提供递归和非递归写法。

非递归，一次处理一层。

```java
import java.util.*;

public class Solution {
    ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        if(pRoot == null) return res;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(pRoot);
        while(!queue.isEmpty()){
            int n = queue.size();
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int i = 0; i < n; i++){
                TreeNode cur = queue.poll();
                tmp.add(cur.val);
                if(cur.left != null) queue.add(cur.left);
                if(cur.right != null) queue.add(cur.right);
            }
            res.add(new ArrayList<>(tmp));
        }
        return res;
    }
}
```

递归写法，可以前序，中序和后序（中序和后序要先建出所有中间的ArrayList）。

```java
import java.util.*;

public class Solution {

    ArrayList<ArrayList<Integer>> res;

    ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
        res = new ArrayList<>();
        rec(pRoot, 0);
        return res;
    }

    private void rec(TreeNode node, int level) {
        if (node == null) return;
        if (res.size() <= level) {//新建一个
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(node.val);
            res.add(tmp);
        } else {//已经建立过
            res.get(level).add(node.val);
        }
        rec(node.left, level + 1);
        rec(node.right, level + 1);
    }
}
```

