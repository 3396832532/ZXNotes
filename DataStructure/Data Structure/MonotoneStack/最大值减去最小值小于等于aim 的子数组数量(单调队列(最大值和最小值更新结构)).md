## 最大值减去最小值小于等于aim的子数组数量(单调队列(最大值和最小值更新结构))

![这里写图片描述](images/ms6.png)

<font color = red>注意: 子数组必须是下标连续的，而且`i ~ i`自己也算一个子数组。</font>

### 解析

这个题目也是使用单调队列(窗口内更新最大值和最小值)的结构来做，如果不懂单调队列先看[**这个博客**](https://blog.csdn.net/zxzxzx0119/article/details/81586455)。

<font color = red>**先准备两个双端队列，分别是最大值更新结构和最小值更新结构：**</font>

 - **先生成两个双端队列`qmax`和`qmin`，当子数组为`arr[L...R]`时，`qmax`维护了窗口子数组`arr[L...R]`的最大值更新结构，`qmin`维护了窗口子数组`arr[L....R]`的最小值更新结构；**
 - **当子数组`arr[L....R]`向右扩一个位置变成`arr[L....R+1]`时，`qmax`和`qmin`可以在`O(1)`时间内完成更新；并且可以在`O(1)`时间内得到窗口的最大值和最小值；**
 - **当子数组`arr[L....R]`左边缩一个位置变成`arr[L+1....R]`是，`qmax`和`qmin`可以在`O(1)`时间内完成更新；并且可以在`O(1)`时间内得到窗口的最大值和最小值；**

然后，我们需要证明两个结论: 

![这里写图片描述](images/ms7.png)



#### 下面看具体过程: 

 - 找到一个`L`，此时令`R`不断向右移动，表示`arr[L...R]`一直向右扩大，并不断更新`qmax`和`qmin`的结构，保证`qmax`和`qmin`始终维持动态窗口最大值和最小值的更新结构；
 - 一旦出现`arr[L....R]`中出现`max - min > aim`的情况，`R`向右扩的过程停止(<font color = red>上面证明结论的第二条</font>)，此时`arr[L....R-1]，arr[L....R-2]，arr[L....R-3]....arr[L,L]`都是满足条件的子数组(<font color = red>上面证明结论的第一条</font>)。也就是说，所以必须以`arr[L]`开头的子数组，总共<font color = red>有`R - L `个，`res += R-L`</font>；
 - 然后要**注意两个队列中的过期的元素**，也就是说队头的元素考虑完了之后**要弹出**；
 - 然后，继续考虑下一个`L`，直到循环结束；

 **由于`L,R`的值是一直增加的(不会减小)，且所有的下标最多进`qmax、qmin`一次，出`qmax、qmin`一次，时间复杂度为`O(n)`。**

```java
    static int getNum2(int[] arr, int aim) {
        if (arr == null || arr.length == 0) return 0;
        LinkedList<Integer> qmax = new LinkedList<>();
        LinkedList<Integer> qmin = new LinkedList<>();
        int res = 0;
        int L = 0, R = 0;
        while (L < arr.length) {

            while (R < arr.length) {//这个While循环就是当L确定的时候，R往右扩到不能再扩
                while (!qmin.isEmpty() && arr[qmin.peekLast()] >= arr[R]) qmin.pollLast(); //最小值更新结构
                qmin.addLast(R);
                while (!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[R]) qmax.pollLast();//最大值更新结构
                qmax.addLast(R);
                //直接取得最大值和最小值判断一下
                if (arr[qmax.getFirst()] - arr[qmin.getFirst()] > aim) break; //直到扩到R不能再往右扩
                R++; //否则当L确定的是R就一直往右扩
            }

            if (qmin.peekFirst() == L) { //最小值的更新结构判断下标是否过期
                qmin.pollFirst();
            }
            if (qmax.peekFirst() == L) {//最大值的更新结构判断下标是否过期
                qmax.pollFirst();
            }
            res += R - L; //一次性的就榨取了所有以L开头的子数组的数量
            L++; //换一个L开头
        }
        return res;
    }
```
***
完整的测试代码如下(包括使用`O(n^3)`方法来测试我们的`O(n)`方法):

```java
import java.util.LinkedList;

/**
 * 最大值减去最小值　<= num 的子数组数量
 */
public class GetNumOfMaxMinusMinSubArray {

    static int getNum(int[] arr, int aim) {
        if (arr == null || arr.length == 0) return 0;
        int res = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i; j < arr.length; j++) {
                if (ok(arr, i, j, aim)) res++;
            }
        }
        return res;
    }

    static boolean ok(int[] arr, int start, int end, int aim) {
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = start; i <= end; i++) {
            max = Math.max(max, arr[i]);
            min = Math.min(min, arr[i]);
        }
        return max - min <= aim;
    }


    static int getNum2(int[] arr, int aim) {
        if (arr == null || arr.length == 0) return 0;
        LinkedList<Integer> qmax = new LinkedList<>();
        LinkedList<Integer> qmin = new LinkedList<>();
        int res = 0;
        int L = 0, R = 0;
        while (L < arr.length) {

            while (R < arr.length) {//这个While循环就是当L确定的时候，R往右扩到不能再扩
                while (!qmin.isEmpty() && arr[qmin.peekLast()] >= arr[R]) qmin.pollLast(); //最小值更新结构
                qmin.addLast(R);
                while (!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[R]) qmax.pollLast();//最大值更新结构
                qmax.addLast(R);
                //直接取得最大值和最小值判断一下
                if (arr[qmax.getFirst()] - arr[qmin.getFirst()] > aim) break; //直到扩到R不能再往右扩
                R++; //否则当L确定的是R就一直往右扩
            }

            if (qmin.peekFirst() == L) { //最小值的更新结构判断下标是否过期
                qmin.pollFirst();
            }
            if (qmax.peekFirst() == L) {//最大值的更新结构判断下标是否过期
                qmax.pollFirst();
            }
            res += R - L; //一次性的就榨取了所有以L开头的子数组的数量
            L++; //换一个L开头
        }
        return res;
    }

    //生成随机数组
    static int[] generateRandomArray(int size, int value) {  //生成的数组的最大长度和
        int[] arr = new int[(int) ((size + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++)
            arr[i] = (int) ((value + 1) * Math.random()) - (int) (value * Math.random());
        return arr;
    }

    //数组复制
    static int[] arrayCopy(int[] arr) {
        if (arr == null) return null;
        int[] copy = new int[arr.length];
        for (int i = 0; i < arr.length; i++) copy[i] = arr[i];
        return copy;
    }

    //打印出数组
    static void printArray(int[] arr) {
        if (arr == null) return;
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int testTime = 100; //测试100次
        boolean success = true;
        for (int i = 0; i < testTime; i++) {
            int size = 7, value = 10;
            int k = (int) (10 * Math.random());
            int[] arr = generateRandomArray(size, value);
            int[] arr2 = arrayCopy(arr);
            int res1 = getNum(arr, k);
            int res2 = getNum2(arr2, k);
            if (res1 != res2) {
                printArray(arr);
                System.out.println(k);
                System.out.println(res1 + " " + res2);
                System.out.println("Wrong!");
                success = false;
                break;
            }
        }
        if (success) System.out.println("Nice!");
    }
}
```
