# HashCode、Equals、HashMap

## 1、hashCode、Equals

对于每一个对象，通过其`hashCode()`方法可为其生成一个整形值（散列码），该整型值被处理后，将会作为数组下标，存放该对象所对应的Entry（存放该对象及其对应值）。 
equals()方法则是在HashMap中插入值或查询时会使用到。当HashMap中插入值或查询值对应的散列码与数组中的散列码相等时，则会通过`equals()`方法比较key值是否相等。

总结：

* 1、如果两个对象相同（即用equals比较返回true），那么它们的hashCode值一定要相同；
* 2、如果两个对象的hashCode相同，它们并不一定相同(即用equals比较返回false)  ；


当 equals 方法被重写时，通常有必要重写 hashCode 方法，以维护 hashCode 方法的常规协定，该协定声明相对等的两个对象必须有相同的 hashCode
* `object1.euqal(object2)` 时为 `true` ， `object1.hashCode() == object2.hashCode()`必为 `true`；
* `object1.hashCode() == object2.hashCode()` 为 false 时， `object1.euqal(object2) `必定为` false`；
* `object1.hashCode() == object2.hashCode() `为 true 时，但 `object1.euqal(object2) `不一定定为 `true`；

重写 equals 不重写 hashcode 会出现什么问题

* 在存储散列集合时 ( 如 Set 类 ) ，如果`原对象 .equals( 新对象 )` ，但没有对 hashCode 重写，即两个对象拥有不同的hashCode ，则在在集合中将会存储两个值相同的对象，从而导致混淆。**因此在重写 equals 方法时，必须重写 hashCode方法**。

(1)、hashCode()介绍

hashCode() 的作用是获取哈希码，也称为散列码；它实际上是返回一个 int 整数。这个哈希码的作用是确定该对象在哈希表中的索引位置。hashCode() 定义在 JDK 的 Object.java 中，这就意味着 Java 中的任何类都包含有 hashCode() 函数。
散列表存储的是键值对(key-value)，它的特点是：能根据"键"快速的检索出对应的 "值 "。这其中就利用到了散列码！（可以快速找到所需要的对象）

(2)、为什么要有 hashCode()?

当你把对象加入 HashSet 时，HashSet 会先计算对象的 hashcode 值来判断对象加入的位置，同时也会与其他已经加入的对象的 hashcode 值作比较，如果没有相符的 hashcode， HashSet 会假设对象没有重复出现。

**但是如果发现有相同 hashcode 值的对象，这时会调用 equals（）方法来检查 hashcode 相等的对象是否真的相同。如果两者相同，HashSet 就不会让其加入操作成功**。如果不同的话，就会重新散列到其他位置，这样我们就大大减少了 equals 的次数，相应就大大提高了执行速度。

(3)、`hashCode()`与 `equals()`的相关规定

如果两个对象相等，则 hashcode 一定也是相同的

两个对象相等, 对两个对象分别调用 equals 方法都返回 true

**两个对象有相同的 hashcode 值，它们也不一定是相等的，因此，equals 方法被覆盖过，则 hashCode 方法也必须被覆盖，hashCode() 的默认行为是对堆上的对象产生独特值**。如果没有重写hashCode()，则该 class 的两个对象无论如何都不会相等（即使这两个对象指向相同的数据）

## 2、重写equals方法的原因、方式和注意事项？

**为什么要重写equals()方法？** 

 Object类中equals()方法的默认实现主要是用于判断两个对象的引用是否相同。而在实际开发过程中，通常需要比较两个对象的对应属性是否完全相同，故需要重写equals()方法。 

  **如何重写equals()方法？** 

  假设equals()方法的形参名为`otherObj`，稍后需要将其转换为另一个叫做`other`的变量。 

* （1）、检测this与otherObj是否引用同一对象：  `  if(this == otherObject) return true; `

* （2）、检测otherObj是否为空： `if(otherObject == null) return false`; 
* （3）、判断this与otherObj是否属于同一个类，具体分两种情况： 
  * a)、如果equals()方法的语义在**每个子类中均有所改变**，则使用getClass()方法进行检测： `if(getClass() != otherObject.getClass()) return false; `
  * 如果equals()方法在所有子类中均有统一的语义，则使用instanceof关键字进行检测：  ` if (!(otherObject instanceof ClassName)) return false; `


* （4）、将otherObj转换为相应类的类型变量： `ClassName other = (ClassName) otherObject; `

* （5）、对所有需要比较的域进行一一比较，若全匹配则返回true，否则返回false。 

关于equals()语义的补充说明：假设现有Employee与Manager两个类，Manager类继承Employee类。若仅将ID作为相等的检测标准，则仅用在Employee类中重写equals()方法，并将该方法声明为final的即可，这就是所谓的「拥有统一的语义」。 

> **重写equals()方法需要注意什么？** 
> 归根结底，还是想问equals()方法的主要特性。Java语言规范要求equals()方法具有如下特性： 
> -    自反性：对于任何非空引用`x`，`x.equals(x)`应该返回true。    
> -    对称性：对于任何引用x和y，当且仅当`y.equals(x)` 返回true时，`x.equals(y)`也应该返回true。    
> -    传递性：对于任何引用x、y和z，如果`x.equals(y)` 返回true，`y.equals(z)`返回true，x.equals(z)也应该返回true。    
> -    一致性：如果x和y引用的对象没有发生变化，反复调用x.equals(y)应该返回同样的结果。    
> -    非空性：对于任何非空引用x，`x.equals(null)`应该返回false。

Object的默认`equals`实现:

```java
// Object类中equals()方法的默认实现
public boolean equals(Object obj) {
    return (this == obj);
}
```

## 3、重写hashCode方法的原因、方式和注意事项？

 **为什么要重写hashCode()方法？** 

 Object类中hashCode()方法默认是将对象的存储地址进行映射，并返回一个整形值作为哈希码。 

 **若重写equals()方法，使其比较两个对象的内容，并保留hashCode()方法的默认实现，那么两个明明「相等」的对象，哈希值却可能不同**。

所以注意: **如果两个对象通过equals()方法比较的结果为true，那么要保证这两个对象的哈希值相等**。

**因此，在重写equals()方法时，建议一定要重写hashCode()方法**。

 **如何重写hashCode()方法？** 

由于Object类的 hashCode() 方法是本地的（native），故其具体实现并不是由Java所完成的。 

 需要实现hashCode()方法时，可以直接调用`Objects.hash(Object... values)`方法来获取对应的哈希值。其内部的具体实现是调用`Arrays.hashCode(Object[])`方法来完成的。 

**重写hashCode()方法需要注意什么？** 

-    应用程序执行期间，只要一个对象用于`equals()`方法的属性未被修改，则该对象多次返回的哈希值应相等。    
-    **如果两个对象通过equals()方法比较的结果为true，那么要保证这两个对象的哈希值相等**。    
-    **如果两个对象通过equals()方法比较的结果为false，那么这两个对象的哈希值可以相等也可以不相等，但理想情况下是应该不相等，以提高散列表的性能**。

选用31的原因:

* 原因一：更少的乘积结果冲突

  31是质子数中一个“不大不小”的存在，如果你使用的是一个如2的较小质数，那么得出的乘积会在一个很小的范围，很容易造成哈希值的冲突。而如果选择一个100以上的质数，得出的哈希值会超出int的最大范围，这两种都不合适。而如果对超过50,000 个英文单词（由两个不同版本的 Unix 字典合并而成）进行 `hashcode()` 运算，并使用常数 31, 33, 37, 39 和 41 作为乘子，每个常数算出的哈希值冲突数都小于7个（国外大神做的测试），那么这几个数就被作为生成hashCode值得备选乘数了。

* 原因二：31可以被JVM优化

  JVM里最有效的计算方式就是进行位运算了：

    * 左移 << : 左边的最高位丢弃，右边补全0（把 << 左边的数据*2的移动次幂）。
    * 右移 >> : 把>>左边的数据/2的移动次幂。
    * 无符号右移 >>> : 无论最高位是0还是1，左边补齐0。 　　

    所以 ： `31 * i = (i << 5) - i`（ 验证:代入`i == 2` 得到:  左边 `31 * 2=62`，右边   `2 * 2 ^ 5 - 2=62`）  --> 两边相等，JVM就可以高效的进行计算了。
