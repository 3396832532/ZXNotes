## 问题

**是否可以在static环境中访问非static变量？**

## 解析

* `static`变量在Java中是属于类的，**它在所有的实例中的值是一样的**；
* 当类被**Java虚拟机载入**的时候，会对`static`变量进行初始化；
* **如果你的代码尝试不用实例来访问非`static`的变量，编译器会报错，因为这些变量还没有被创建出来，还没有跟任何实例关联上**；