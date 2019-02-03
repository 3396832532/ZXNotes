
### LeetCode - 675. Cut Off Trees for Golf Event (排序BFS求最短路) 
#### [题目链接](https://leetcode.com/problems/cut-off-trees-for-golf-event/)

> https://leetcode.com/problems/cut-off-trees-for-golf-event/

#### 题目
![在这里插入图片描述](images/675_t.png)
#### 解析

看下面一个例子:

![在这里插入图片描述](images/675_s.png)

* 因为题目必须要按照树的高度来砍(访问)， 所以<font color = red>我们只需要将所有树按照高度`height`排序</font>，然后进行对按照顺序`bfs`访问所有的树即可；
* 结果就是所有`bfs`结果的和；

```java
class Solution {
    
    private class Tuple implements Comparable<Tuple> {
        public int height;
        public int x;
        public int y;

        public Tuple(int height, int x, int y) {
            this.height = height;
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Tuple o) {
            return height - o.height;
        }
    }

    private class State {
        public int x;
        public int y;
        public int step;

        public State(int x, int y, int step) {
            this.x = x;
            this.y = y;
            this.step = step;
        }
    }


    private int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private boolean checkBoundary(int x, int y, List<List<Integer>> forest){
        return x >= 0 && x < forest.size() && y >= 0 && y < forest.get(0).size();
    }

    private int bfs(State start, State end, List<List<Integer>> forest) {
        Queue<State> queue = new LinkedList<>();
        boolean[][] vis = new boolean[forest.size()][forest.get(0).size()];
        queue.add(start);
        vis[start.x][start.y] = true;
        while (!queue.isEmpty()) {
            State cur = queue.poll();
            if (cur.x == end.x && cur.y == end.y) {
                return cur.step;
            }
            for (int i = 0; i < 4; i++) {
                int nx = cur.x + dir[i][0];
                int ny = cur.y + dir[i][1];
                if (checkBoundary(nx, ny, forest) && !vis[nx][ny] && forest.get(nx).get(ny) > 0) {
                    vis[nx][ny] = true;
                    queue.add(new State(nx, ny, cur.step + 1));
                }
            }
        }
        return -1;
    }


    public int cutOffTree(List<List<Integer>> forest) {
        if (forest == null || forest.size() == 0)
            return 0;
        int n = forest.size();
        int m = forest.get(0).size();
        ArrayList<Tuple> lists = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (forest.get(i).get(j) > 1) {
                    lists.add(new Tuple(forest.get(i).get(j), i, j));
                }
            }
        }
        // sort by height
        Collections.sort(lists);

        int sx = 0, sy = 0;
        int res = 0;

        for (int i = 0; i < lists.size(); i++) {
            int ex = lists.get(i).x;
            int ey = lists.get(i).y;
            int step = bfs(new State(sx, sy, 0), new State(ex, ey, 0), forest);
            if (step == -1)
                return -1;
            res += step;
//            forest.get(sx).set(sy, 1);  // do it or not do both ok
            sx = ex;
            sy = ey;
        }
        return res;
    }
}
```

另一种用数组替代类的方法: 

```java
class Solution {

    private int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private boolean checkBoundary(int x, int y, List<List<Integer>> forest){
        return x >= 0 && x < forest.size() && y >= 0 && y < forest.get(0).size();
    }

    private int bfs(int sx, int sy, int ex, int ey, List<List<Integer>> forest) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] vis = new boolean[forest.size()][forest.get(0).size()];
        queue.add(new int[]{sx, sy, 0});
        vis[sx][sy] = true;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int curx = cur[0];
            int cury = cur[1];
            if (curx == ex && cury == ey) {
                return cur[2];
            }
            for (int i = 0; i < 4; i++) {
                int nx = curx + dir[i][0];
                int ny = cury + dir[i][1];
                if (checkBoundary(nx, ny, forest) && !vis[nx][ny] && forest.get(nx).get(ny) > 0) {
                    vis[nx][ny] = true;
                    queue.add(new int[]{nx, ny,  cur[2]+1});
                }
            }
        }
        return -1;
    }


    public int cutOffTree(List<List<Integer>> forest) {
        if (forest == null || forest.size() == 0)
            return 0;
        int n = forest.size();
        int m = forest.get(0).size();
       PriorityQueue<int[]>pq = new PriorityQueue<>((o1, o2) -> o1[2] - o2[2]);
        // PriorityQueue<int[]>pq = new PriorityQueue<>(Comparator.comparingInt(o -> o[2])); //按照height排序

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (forest.get(i).get(j) > 1) {
                    pq.add(new int[]{i, j, forest.get(i).get(j)});
                }
            }
        }
        int sx = 0, sy = 0;
        int res = 0;

        while(!pq.isEmpty()){
            int[] cur = pq.poll();
            int ex = cur[0];
            int ey = cur[1];
            int step = bfs(sx, sy, ex, ey, forest);
            if (step == -1)
                return -1;
            res += step;
            sx = ex;
            sy = ey;
        }
        return res;
    }
}
```

