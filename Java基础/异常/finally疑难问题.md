## 1、情况一


**在`finally`语句里面没有`return`的情况下**，finally是在return后面的表达式运算后执行的（此时并没有返回运算后的值，**而是先把要返回的值保存起来**(临时保存)，管finally中的代码怎么样，返回的值都不会改变，仍然是之前保存的值），所以函数返回值是在finally执行前确定的。

测试:

```java
public class TryFinally {

    public static void main(String[] args){
        System.out.println(m());
    }

    /**
       只有 try里面有return,先用一个临时变量保存return的值，尽管之后在finally对变量进行修改
       也不会改变在try中的return的值，因为最后返回的就是临时的值
     */
    static int m() {
        int a = 1;
        try {
            return a;
        } catch (Exception e) {
            System.out.println("100");
        } finally {
            ++a;
        }
        return a;
    }
}

```

返回: `1`

但是如果是对象的话，输出会不同，和JVM虚拟机中程序执行`exection_table`中的字节码指令时操作栈的的操作情况有关。

```java
public class TryFinally {

    public static void main(String[] args) {
        System.out.println(m().num);
    }

    static Num m() {
        Num number = new Num(1);
        try {
            return number;
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            number.num++;
        }
        return number;
    }

    static class Num {
         int num;

        public Num(int num) {
            this.num = num;
        }
    }
}
```

输出: `2`。

finally中没有return的情况: 

* 1）如果return的数据是基本数据类型或文本字符串，则在finally中对该基本数据的改变不起作用，try中的return语句依然会返回进入finally块之前保留的值。
* 2）如果return的数据是引用数据类型，而在finally中对该引用数据类型的属性值的改变起作用，try中的return语句返回的就是在finally中改变后的该属性的值。

## 2、情况二

但是如果在`finally`中有了`return`语句，就会在`finally`中`return`了。

finally块中的内容会先于try中的return语句执行，如果finall语句块中也有return语句的话，那么直接从finally中返回了，这也是不建议在finally中return的原因。

即: 会将try中的return语句”覆盖“掉，直接执行finally中的return语句，得到返回值。

```java
public class TryFinally {

    public static void main(String[] args){
        System.out.println(m());
    }

    /**
       如果finally中有return语句，那么程序就return了，
       所以finally中的return是一定会被return的，
       编译器把finally中的return实现为一个warning。
     * @return
     */
    static int m() {
        int a = 1;
        try {
            return a;
        } catch (Exception e) {
            System.out.println("100");
        } finally {
            ++a;
            return a;
        }
    }
}

```

输出: `2`。

## 3、情况三

在`try`中的`return`之前发生了异常，则try的`return`就不会执行，会执行`finally`中的`return`。

```java
public class TryFinally {

    public static void main(String[] args) {
        System.out.println(m());
    }

    static int m() {
        int a = 1;
        try {
            int b = 1 / 0; // 这里发生异常，下面的return a不会执行，会执行finally中的return a
            return a;
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            ++a;
            return a;
        }

    }
}
```

输出:

```java
error
2
```

## 4、情况四

如果在`catch`中返回，和`try`返回差不多。

```java
public class TryFinally {

    public static void main(String[] args) {
        System.out.println(m());
    }

    static int m() {
        int a = 1;
        try {
            int b = 1 / 0;
            return a;
        } catch (Exception e) {
            System.out.println("error");
            return a; // 和普通的try && finally中改变且没有return的情况一样
        } finally {
            ++a;
        }
    }
}
```

输出：

```java
error
1
```



## 5、情况五

最后还注意一下返回的时候，如果在`try`中返回的时候出现`a+=10`，会先进行`a=a+10`，然后执行`finally`和返回:

```java
public class TryFinally {

    public static void main(String[] args) {
        System.out.println(m());
    }

    static int m() {
        int a = 1;
        try {
            return a += 10; //注意这里会先 a=a+10，然后return a
        } catch (Exception e) {
            System.out.println("error");
        } finally {
            System.out.println(a);
            ++a;
        }
        return a;
    }
}

```

输出：

```
11
11
```

