## LeetCode - 692. Top K Frequent Words & 347. Top K Frequent Elements (堆  | 桶)
* [LeetCode - 692. Top K Frequent Words](#1)
* [LeetCode - 347. Top K Frequent Elements](#leetcode---347-top-k-frequent-elements)

***
### LeetCode - 692. Top K Frequent Words
#### [题目链接](https://leetcode.com/problems/top-k-frequent-words/)

> https://leetcode.com/problems/top-k-frequent-words/

#### 题目
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190126210219642.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
#### 解析

解法一: 直接排序取前`K`个。(主要是见识一下`Java8`的一些写法)

`O(N*logN)`: 
```java
class Solution {
    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> freqs = new HashMap();
        for (String word: words)
            freqs.put(word, 1 + freqs.getOrDefault(word, 0));
        List<String> res = new ArrayList(freqs.keySet()); 
        Collections.sort(res, (w1, w2) -> freqs.get(w1) == freqs.get(w2) ? w1.compareTo(w2) :
                freqs.get(w2) - freqs.get(w1));
        return res.subList(0, k);
    }
}
```
解法二: 维护一个`K`个数的堆。

`O(N*logK)`
```java
class Solution {

    private class Freq{
        String word;
        int freq;

        public Freq(String word, int freq) {
            this.word = word;
            this.freq = freq;
        }
    }

    public List<String> topKFrequent(String[] words, int k) {
        List<String> res = new ArrayList<>();
        if(words == null)
            return res;
        Queue<Freq>heap = new PriorityQueue<>((o1, o2) -> { // lambda will be slow
            if(o1.freq == o2.freq)
                return o1.word.compareTo(o2.word); 
            return -(o1.freq - o2.freq); // big heap
        });
        HashMap<String, Integer> counts = new HashMap<>();
        for(String word : words)
            counts.put(word,1 + counts.getOrDefault(word, 0));
        counts.forEach((key, val) -> heap.add(new Freq(key, val))); // java 8
        for(int i = 0; i < k; i++)
            res.add(heap.poll().word);
        return res;
    }
}
```

***
### LeetCode - 347. Top K Frequent Elements

#### [题目链接](https://leetcode.com/problems/top-k-frequent-elements/)
#### 题目

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190126210245274.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
#### 解析

`O(N*logK)`
```java
class Solution {

    private class Freq {
        int e;
        int freq; //元素和频次

        public Freq(int e, int freq) {
            this.e = e;
            this.freq = freq;
        }
    }

    //维护一个K个数的优先队列
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int num : nums)
            counts.put(num, 1 + counts.getOrDefault(num, 0));
        PriorityQueue<Freq> heap = new PriorityQueue<>((o1, o2) -> o1.freq - o2.freq);
        counts.forEach((key, val) -> {
            heap.add(new Freq(key, val));
            if(heap.size() > k) 
                heap.poll();
        });
        List<Integer> res = new ArrayList<>();
        while (!heap.isEmpty())
            res.add(heap.poll().e);
        return res;
    }
}
```

```java
class Solution {

    //维护一个K个数的优先队列
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        for (int num : nums)
            counts.put(num, 1 + counts.getOrDefault(num, 0));
        PriorityQueue< Map.Entry<Integer, Integer> > heap =
                new PriorityQueue<>((o1, o2) -> o1.getValue()  -o2.getValue());
        for (Map.Entry<Integer, Integer> entry: counts.entrySet()) {
            heap.add(entry);
            if(heap.size() > k)
                heap.poll();
        }
        List<Integer> res = new ArrayList<>();
        while (!heap.isEmpty())
            res.add(heap.poll().getKey());
        return res;
    }
}
```

`O(N)`解法: 

* 先用一个`HashMap`来统计每个值出现的频率；
* 然后记最大频率为`maxFreq`，然后生成`maxFreq`个桶，每个桶中放对应的频率的集合；
* <font color = red>然后从后向前取从高频率到低频率的桶中的元素即可(取到`k`个就退出)；

![在这里插入图片描述](https://img-blog.csdnimg.cn/20190126224142332.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3p4enh6eDAxMTk=,size_16,color_FFFFFF,t_70)
```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        HashMap<Integer, Integer> counts = new HashMap<>();
        int maxFreq = 1;
        for(int num : nums){
            counts.put(num, 1 + counts.getOrDefault(num, 0));
            maxFreq = Math.max(maxFreq, counts.get(num));
        }
        HashMap<Integer, ArrayList<Integer>> buckets = new HashMap<>();
        counts.forEach((key, val) -> {
            ArrayList<Integer> tmp = buckets.getOrDefault(val, new ArrayList<>());
            tmp.add(key);
            buckets.put(val, tmp);
        });
        ArrayList<Integer>res = new ArrayList<>();
        for(int freq = maxFreq; freq >= 1; freq--){
            if(buckets.containsKey(freq))
                res.addAll(buckets.get(freq));
            if(res.size() == k)
                break;
        }
        return res;
    }
}
```

还有一种和`Java8`更密切的写法(利用`streamAPI`): 
```java
class Solution {
    public List<Integer> topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Set<Integer>> buckets = new HashMap<>();

        IntStream.of(nums).forEach(n -> counts.put(n, counts.getOrDefault(n, 0) + 1));

        // Sort by occurrences, so we can later get the most frequent.
        counts.forEach((key, val) -> {
            Set<Integer> set = buckets.getOrDefault(val, new HashSet<>());
            set.add(key);
            buckets.put(val, set);
        });

        //buckets.forEach((key, value) -> System.out.format("[%d->%s]", key, Arrays.asList(value.toArray())));
        List<Integer> res = new ArrayList<>();

        // Sort in reverse order and get the first K items, but since this is a set we need to save into a list.
        buckets.keySet().stream().sorted(Comparator.reverseOrder()).limit(k).forEach(freq -> res.addAll(buckets.get(freq)));
         
        return res.stream().limit(k).collect(Collectors.toList());
    }
}
```
