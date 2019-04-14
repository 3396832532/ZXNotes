## HashCode、Equals、HashMap

对于每一个对象，通过其`hashCode()`方法可为其生成一个整形值（散列码），该整型值被处理后，将会作为数组下标，存放该对象所对应的Entry（存放该对象及其对应值）。 
equals()方法则是在HashMap中插入值或查询时会使用到。当HashMap中插入值或查询值对应的散列码与数组中的散列码相等时，则会通过`equals()`方法比较key值是否相等，

总结：

* 1、如果两个对象相同（即用equals比较返回true），那么它们的hashCode值一定要相同；
* 2、如果两个对象的hashCode相同，它们并不一定相同(即用equals比较返回false)  ；


当 equals 方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明相对等的两个对象必须有相同的 hashCode
* `object1.euqal(object2) 时为 true` ， `object1.hashCode() == object2.hashCode() 为 true`；
* `object1.hashCode() == object2.hashCode()` 为 false 时， `object1.euqal(object2) `必定为` false`；
* `object1.hashCode() == object2.hashCode() `为 true 时，但 `object1.euqal(object2) `不一定定为 true；

重写 equals 不重写 hashcode 会出现什么问题

* 在存储散列集合时 ( 如 Set 类 ) ，如果原对象 .equals( 新对象 ) ，但没有对 hashCode 重写，即两个对象拥有不同的hashCode ，则在在集合中将会存储两个值相同的对象，从而导致混淆。**因此在重写 equals 方法时，必须重写 hashCode方法**。

