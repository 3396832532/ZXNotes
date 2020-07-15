## 剑指Offer - 61 - 序列化二叉树

#### [题目链接](https://www.nowcoder.com/practice/cf7e25aa97c04cc1a68c8f040e71fb84?tpId=13&tqId=11214&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/cf7e25aa97c04cc1a68c8f040e71fb84?tpId=13&tqId=11214&tPage=4&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

请实现两个函数，分别用来序列化和反序列化二叉树

### 解析

这个题目在[**LeetCode - 297**](https://github.com/ZXZxin/ZXBlog/blob/master/%E5%88%B7%E9%A2%98/LeetCode/Tree/LeetCode%20-%20297.%20Serialize%20and%20Deserialize%20Binary%20Tree(%E4%BA%8C%E5%8F%89%E6%A0%91%E7%9A%84%E5%BA%8F%E5%88%97%E5%8C%96%E5%92%8C%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96).md)也写过，直接上代码，解释可以看那篇。

前序序列化。

```java
public class Solution {
    String Serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serHelper(root, sb);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void serHelper(TreeNode root, StringBuilder sb) {
        if(root == null) {
            sb.append("null,");
            return;
        }
        sb.append(root.val + ",");
        serHelper(root.left, sb);
        serHelper(root.right, sb);
    }

    TreeNode Deserialize(String str) {
        if(str == null || str.length() == 0) return null;
        String[] data = str.split(",");
        int[] idx = new int[1];
        return desHelper(data, idx);
    }

    private TreeNode desHelper(String[] arr, int[] idx){
        if(idx[0] >= arr.length) return null;
        String val = arr[idx[0]];
        if("null".equals(val)){
            return null;
        }
        TreeNode root = new TreeNode(Integer.parseInt(val));
        idx[0]++;
        root.left = desHelper(arr, idx);
        idx[0]++;
        root.right = desHelper(arr, idx);
        return root;
    }
}
```

层序序列化。

```java
import java.util.*;
public class Solution {
    String Serialize(TreeNode root) {
        StringBuilder sb = serHelper(root);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public StringBuilder serHelper(TreeNode root) {
        StringBuilder res = new StringBuilder();
        if (root == null) {
            res.append("null,");
            return res;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        TreeNode top = null;
        while (!queue.isEmpty()) {
            top = queue.poll();
            if (top != null) {
                res.append(top.val + ",");
                queue.add(top.left);
                queue.add(top.right);
            } else {
                res.append("null,");
            }
        }
        return res;
    }

    TreeNode Deserialize(String str) {
        if (str == null || str.length() == 0) return null;
        String[] arr = str.split(",");
        int idx = 0;
        TreeNode root = recon(arr[idx++]);
        if (root == null) return root;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        TreeNode top = null;
        while (!queue.isEmpty()) {
            top = queue.poll();
            top.left = recon(arr[idx++]);
            top.right = recon(arr[idx++]);
            if (null != top.left)
                queue.add(top.left);
            if (null != top.right)
                queue.add(top.right);
        }
        return root;
    }

    private TreeNode recon(String str) {
        return str.equals("null") ? null : new TreeNode(Integer.valueOf(str));
    }
}
```

