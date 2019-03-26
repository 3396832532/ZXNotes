## 剑指Offer - 59 - 按之字形顺序打印二叉树

#### [题目链接](https://www.nowcoder.com/practice/91b69814117f4e8097390d107d2efbe0?tpId=13&tqId=11212&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

#### 题目

请实现一个函数按照之字形打印二叉树，即第一行按照从左到右的顺序打印，第二层按照从右至左的顺序打印，第三行按照从左到右的顺序打印，其他行以此类推。

### 解析

这题是[剑指Offer - 60 - 把二叉树打印成多行](剑指Offer - 60 - 把二叉树打印成多行.md)的加强版，可以先做那一题。

然后只需要将偶数层的翻转一下即可。

非递归：

```java
import java.util.*;
public class Solution {

    public ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        if(pRoot == null) 
            return res;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(pRoot);
        boolean ok = false;
        ArrayList<Integer> list = new ArrayList<>();
        while(!queue.isEmpty()){
            int n = queue.size();
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int i = 0; i < n; i++){
                TreeNode cur = queue.poll();
                tmp.add(cur.val);
                if(cur.left != null) queue.add(cur.left);
                if(cur.right != null) queue.add(cur.right);
            }
            if(ok) Collections.reverse(tmp);
            ok = !ok;
            res.add(tmp);
        }
        return res;
    }
}
```

递归：

```java
import java.util.*;
public class Solution {
    
    ArrayList<ArrayList<Integer>> res;

    public ArrayList<ArrayList<Integer>> Print(TreeNode pRoot) {
        res = new ArrayList<>();
        rec(pRoot, 0);
        for(int i = 0; i < res.size(); i++) if( i % 2 == 1) Collections.reverse(res.get(i));
        return res;
    }
    
    private void rec(TreeNode node, int level){
        if(node == null) return;
        if(level >= res.size()){
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(node.val);
            res.add(tmp);
        }else {
            res.get(level).add(node.val);
        }
        rec(node.left, level+1);
        rec(node.right, level+1);
    }
}
```

