## LintCode - 862. Next Closest Time (暴力 | DFS)
* 暴力
* DFS

***
#### [题目链接](https://www.lintcode.com/problem/next-closest-time/description)

> https://www.lintcode.com/problem/next-closest-time/description

#### 题目
![在这里插入图片描述](images/862_t.png)

#### 暴力解法

暴力的解法就是，枚举所有的可能: 

* 每次累加<font  color = blue>一分钟</font> ，然后检查所有的数字是不是都在原来的数字中；
* 如果是就可以`break`，因为注意题目说的是<font color = red>下一个最近的时间</font>，不能往前面推；

```java
public class Solution {
    public String nextClosestTime(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(3, 5));
        while(true){
            if(++min == 60){
                min = 0;
                ++hour;
                hour %= 24;
            }
            String cur = String.format("%02d:%02d", hour, min);
            boolean ok = true;
            for(int i = 0; i < cur.length(); i++){
                if(time.indexOf(cur.charAt(i)) == -1){
                    ok = false;
                    break;
                }
            }
            if(ok)
                return cur;
        }
    }
}
```

***
#### DFS
* `DFS`就是先把原来的时间的四个数字存到一个数组中，然后`DFS`递归四层，没层在原来的数字上选择一个去递归就好，数字可以重复。
* 在递归的过程中记录一个最小的间隔即可，主要是时间转换的细节要注意；
```java
public class Solution {
    
    private int res;

    private void dfs(int dep, int[] digs, int[] curs, int oriTime){
        if(dep == 4){
            int nh = curs[0] * 10 + curs[1];
            int nm = curs[2] * 10 + curs[3];
            if(nh > 23 || nm > 59)
                return;
            int curTime = toMinute(nh, nm);
            if(timeDiff(oriTime, curTime) < timeDiff(oriTime, res)) // 更近 -> 更新
                res = curTime;
            return;
        }
        for(int dig : digs){
            curs[dep] = dig; // 选择其中的一个
            dfs(dep + 1, digs, curs, oriTime);
        }
    }

    private int toMinute(int h, int m){
        return h * 60 + m;
    }
    private int timeDiff(int t1, int t2){
        if(t1 == t2)
            return Integer.MAX_VALUE;
        return( (t2 - t1) + 24*60) % (24*60); // 旋转的问题一般都要这样
    }

    public String nextClosestTime(String time) {
        int[] digs = {time.charAt(0) - '0', time.charAt(1) - '0',
                time.charAt(3) - '0', time.charAt(4) - '0'}; // 原来的四个数，递归的时候每个位置都只有这四种选择
        int h = digs[0] * 10 + digs[1];
        int m = digs[2] * 10 + digs[3];
        int oriTime = toMinute(h, m); // 给定的当前的时间
        res = oriTime; // will be Integer.MAX_VALUE
        int[] cur = new int[4];
        dfs(0, digs, cur, oriTime);
        return String.format("%02d:%02d", res/60, res%60);
    }
}
```

