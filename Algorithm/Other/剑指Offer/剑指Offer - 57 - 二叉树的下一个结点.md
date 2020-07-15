## 剑指Offer - 57 - 二叉树的下一个结点

#### [题目链接](https://www.nowcoder.com/practice/9023a0c988684a53960365b889ceaf5e?tpId=13&tqId=11210&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking)

> https://www.nowcoder.com/practice/9023a0c988684a53960365b889ceaf5e?tpId=13&tqId=11210&tPage=3&rp=1&ru=%2Fta%2Fcoding-interviews&qru=%2Fta%2Fcoding-interviews%2Fquestion-ranking

#### 题目

给定一个二叉树和其中的一个结点，请找出中序遍历顺序的下一个结点并且返回。注意，树中的结点不仅包含左右子结点，同时包含指向父结点的指针。

```java
public class TreeLinkNode {
    int val;
    TreeLinkNode left = null;
    TreeLinkNode right = null;
    TreeLinkNode next = null; //觉得改成parent更好一点

    TreeLinkNode(int val) {
        this.val = val;
    }
}
```

### 解析

这个题目我在另一篇博客[**在一颗二叉树中寻找一个结点的后继结点或者前驱节点**](https://github.com/ZXZxin/ZXBlog/blob/master/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%AE%97%E6%B3%95/Tree/%E5%9C%A8%E4%B8%80%E9%A2%97%E4%BA%8C%E5%8F%89%E6%A0%91%E4%B8%AD%E5%AF%BB%E6%89%BE%E4%B8%80%E4%B8%AA%E7%BB%93%E7%82%B9%E7%9A%84%E5%90%8E%E7%BB%A7%E7%BB%93%E7%82%B9(%E5%89%8D%E9%A9%B1%E7%BB%93%E7%82%B9).md)已经详细介绍。

分三种情况。不赘述。

```java
public class Solution {

    // next 最好写成 parent
    public TreeLinkNode GetNext(TreeLinkNode pNode) {
        if (pNode == null) return null;
        if (pNode.right != null) return getMostLeft(pNode.right); // 答案是: 右孩子的最左节点
        if (pNode.next != null && pNode.next.left != null && pNode.next.left == pNode) // 答案是: 父亲
            return pNode.next;
        while (pNode.next != null && pNode.next.right != null && pNode.next.right == pNode) //答案是不断的往上找
            pNode = pNode.next;
        return pNode.next;
    }
	//获得node的最左下角节点
    public TreeLinkNode getMostLeft(TreeLinkNode node) {
        while (node.left != null) node = node.left;
        return node;
    }
}
```

