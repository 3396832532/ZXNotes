### 什么时候进行 MinGC ， FullGC

MinGC

* 新生代中的垃圾收集动作，采用的是复制算法；
* 对于较大的对象，在 Minor GC 的时候可以直接进入老年代；

FullGC

* Full GC 是发生在老年代的垃圾收集动作，采用的是标记 - 清除 / 整理算法。
* 由于老年代的对象几乎都是在 Survivor 区熬过来的，不会那么容易死掉。因此 Full GC 发生的次数不会有 Minor GC 那么频繁，并且 Time(Full GC)>Time(Minor GC)