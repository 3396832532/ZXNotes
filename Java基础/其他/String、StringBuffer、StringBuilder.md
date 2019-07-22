## 一、String、StringBuffer、StringBuilder对比



转载: <https://www.cnblogs.com/su-feng/p/6659064.html>



这三个类之间的区别主要是在两个方面，即运行速度和线程安全这两方面。

1、首先说运行速度，或者说是执行速度，**在这方面运行速度快慢为：StringBuilder > StringBuffer > String**

　　**String最慢的原因：**

　　**String为字符串常量，而StringBuilder和StringBuffer均为字符串变量，即String对象一旦创建之后该对象是不可更改的，但后两者的对象是变量，是可以更改的。**以下面一段代码为例：

```java
1 String str="abc";
2 System.out.println(str);
3 str=str+"de";
4 System.out.println(str);
```

　　如果运行这段代码会发现先输出“abc”，然后又输出“abcde”，好像是str这个对象被更改了，其实，这只是一种假象罢了，JVM对于这几行代码是这样处理的，首先创建一个String对象str，并把“abc”赋值给str，然后在第三行中，**其实JVM又创建了一个新的对象也名为str**，然后再把原来的str的值和“de”加起来再赋值给新的str，而原来的str就会被JVM的垃圾回收机制（GC）给回收掉了，所以，str实际上并没有被更改，也就是前面说的String对象一旦创建之后就不可更改了。所以，Java中对String对象进行的操作实际上是一个不断创建新的对象并且将旧的对象回收的一个过程，所以执行速度很慢。

　　而StringBuilder和StringBuffer的对象是变量，对变量进行操作就是直接对该对象进行更改，而不进行创建和回收的操作，所以速度要比String快很多。

　　另外，有时候我们会这样对字符串进行赋值

```java
1 String str="abc"+"de";
2 StringBuilder stringBuilder=new StringBuilder().append("abc").append("de");
3 System.out.println(str);
4 System.out.println(stringBuilder.toString());
```

　　这样输出结果也是“abcde”和“abcde”，但是String的速度却比StringBuilder的反应速度要快很多，这是因为第1行中的操作和 `String str="abcde";`是完全一样的，所以会很快，而如果写成下面这种形式

```java
1 String str1="abc";
2 String str2="de";
3 String str=str1+str2;
```

　　那么JVM就会像上面说的那样，不断的创建、回收对象来进行这个操作了。速度就会很慢。

2、再来说线程安全

　　**在线程安全上，StringBuilder是线程不安全的，而StringBuffer是线程安全的**

　　如果一个StringBuffer对象在字符串缓冲区被多个线程使用时，StringBuffer中很多方法可以带有synchronized关键字，所以可以保证线程是安全的，但StringBuilder的方法则没有该关键字，所以不能保证线程安全，有可能会出现一些错误的操作。所以如果要进行的操作是多线程的，那么就要使用StringBuffer，但是在单线程的情况下，还是建议使用速度比较快的StringBuilder。

3、总结一下

　　**String：适用于少量的字符串操作的情况**

　　**StringBuilder：适用于单线程下在字符缓冲区进行大量操作的情况**

　　**StringBuffer：适用多线程下在字符缓冲区进行大量操作的情况**

附阿里面经: 

String 、 StringBuffer 、 StringBuilder 以及对 String 不变性的理解

* 都是 final 类 , 都不允许被继承 ;
* String 长度是不可变的 , StringBuffer 、 StringBuilder 长度是可变的 ;
* StringBuffer 是线程安全的 , StringBuilder 不是线程安全的 ，但它们两个中的所有方法都是相同的， StringBuffer 在StringBuilder 的方法之上添加了 synchronized 修饰，保证线程安全。
* StringBuilder 比 StringBuffer 拥有更好的性能。
* **如果一个 String 类型的字符串，在编译时就可以确定是一个字符串常量，则编译完成之后，字符串会自动拼接成一个常量。此时 String 的速度比 StringBuffer 和 StringBuilder 的性能好的多**。

String 不变性的理解

* String 类是被 final 进行修饰的，不能被继承。
* 在用 + 号链接字符串的时候会创建新的字符串。
* `String s = new String("Hello world");` **可能创建两个对象也可能创建一个对象。如果静态区中有“ Hello world ”字符串常量对象的话，则仅仅在堆中创建一个对象。如果静态区中没有“ Hello world ”对象，则堆上和静态区中都需要创建对象**。
* 在 java 中 , **通过使用 "+" 符号来串联字符串的时候 , 实际上底层会转成通过 StringBuilder 实例的 append() 方法来实现** 。

---

## 二、String对象不可变的原因

<https://www.cnblogs.com/goody9807/p/6516374.html>

简单的来说：String 类中使用 final 关键字字符数组保存字符串，`private final char value[]`，所以 String 对象是不可变的。而 StringBuilder 与StringBuffer 都继承自 AbstractStringBuilder 类，在 AbstractStringBuilder 中也是使用字符数组保存字符串 char[]value **但是没有用 final 关键字修饰**，所以
这两种对象都是可变的。

```java
abstract class AbstractStringBuilder implements Appendable, CharSequence {
    /**
     * The value is used for character storage.
     */
    char[] value;

    /**
     * The count is the number of characters used.
     */
    int count;
    /**
     * This no-arg constructor is necessary for serialization of subclasses.
     */
    AbstractStringBuilder() {
    }
}
```

线程安全性String 中的对象是不可变的，也就可以理解为常量，线程安全。

AbstractStringBuilder 是 StringBuilder 与 StringBuffer 的公共父类，定义了一些字符串的基本操作，如 expandCapacity、 append、 insert、 indexOf 等公共方法。StringBuffer 对方法加了同步锁或者对调用的方法加了同步锁，所以是线程安全的。StringBuilder 并没有对方法进行加同步锁，所以是非线程安全
的。
性能 : **每次对 String 类型进行改变的时候，都会生成一个新的 String 对象，然后将指针指向新的String 对象**。StringBuffer 每次都会对 StringBuffer 对象本身进行操作，而不是生成新的对象并改变对象引用。相同情况下使用StringBuilder 相比使用 StringBuffer 仅能获得 10%~15% 左右的性能提升，但却要冒多线程不安全的风险。

对于三者使用的总结：

1. 操作少量的数据 = String
2. 单线程操作字符串缓冲区下操作大量数据 = StringBuilder
3. 多线程操作字符串缓冲区下操作大量数据 = StringBuffer