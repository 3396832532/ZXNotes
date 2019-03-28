## 剑指Offer - 62 - 二叉搜索树的第k个结点

#### [题目链接](https://www.nowcoder.com/practice/ef068f602dde4d28aab2b210e859150a?tpId=13&tqId=11215&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/ef068f602dde4d28aab2b210e859150a?tpId=13&tqId=11215&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

给定一棵二叉搜索树，请找出其中的第k小的结点。例如， （5，3，7，2，4，6，8）    中，按结点数值大小顺序第三小结点的值为4。

### 解析

这题目也不难，二叉搜索树中序遍历是升序的，可以中序遍历然后计数即可。

非递归中序不懂的可以看[这篇博客](https://github.com/ZXZxin/ZXBlog/blob/master/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%AE%97%E6%B3%95/Tree/%E4%BA%8C%E5%8F%89%E6%A0%91%E7%9A%84%E5%90%84%E7%A7%8D%E6%93%8D%E4%BD%9C(%E9%80%92%E5%BD%92%E5%92%8C%E9%9D%9E%E9%80%92%E5%BD%92%E9%81%8D%E5%8E%86,%E6%A0%91%E6%B7%B1%E5%BA%A6,%E7%BB%93%E7%82%B9%E4%B8%AA%E6%95%B0%E7%AD%89%E7%AD%89).md#1%E9%80%92%E5%BD%92%E4%B8%AD%E5%BA%8F)。

```java
import java.util.*;

public class Solution {
    TreeNode KthNode(TreeNode pRoot, int k){
        Stack<TreeNode> stack = new Stack<>();
        TreeNode p = pRoot;
        int cnt = 0;
        while(!stack.isEmpty() || p != null){
            while(p != null){
                stack.push(p);
                p = p.left;
            }
            p = stack.pop();
            cnt++;
            if(k == cnt) 
                return p;
            p = p.right;
        }
        return null;
    }
}
```

递归可能稍微有点难以理解。

要注意的是， 先走到最左边，最下面如果没有到达k，就直接返回null，即可，只有在`k == cnt`的时候，才会返回找到的节点。

```java
import java.util.*;

public class Solution {
    int cnt;
    
    TreeNode KthNode(TreeNode pRoot, int k) {
        return in(pRoot, k);
    }
    
    private TreeNode in(TreeNode node, int k) {
        if (node == null) return null;
        TreeNode L = in(node.left, k);
        if (L != null) return L;//之前已经找到了
        return ++cnt == k ? node : in(node.right, k);
    }
}
```

