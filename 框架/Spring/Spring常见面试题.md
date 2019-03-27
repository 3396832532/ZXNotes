## 一、Spring Bean的作用域

![1_1.png](images/1_1.png)

五种作用域中，`request、session和global session`三种作用域仅在基于web的应用中使用（不必关心你所采用的是什么web应用框架），只能用在基于web的`Spring ApplicationContext`环境。

(1)、Singleton: 那么Spring IoC容器中只会存在一个共享的bean实例，并且所有对bean的请求，只要id与该bean定义相匹配，则只会返回bean的同一实例**。Singleton是单例类型，就是在创建起容器时就同时自动创建了一个bean的对象，不管你是否使用，他都存在了，每次获取到的对象都是同一个对象**。注意，Singleton作用域是Spring中的缺省作用域（默认）。

(2)、Prototype: 表示一个bean定义对应多个对象实例。Prototype作用域的bean会导致在每次对该bean请求（将其注入到另一个bean中，或者以程序的方式调用容器的getBean()方法）时都会创建一个新的bean实例。**Prototype是原型类型，它在我们创建容器的时候并没有实例化，而是当我们获取bean的时候才会去创建一个对象，而且我们每次获取到的对象都不是同一个对象**。根据经验，对有状态的bean应该使用prototype作用域，而对无状态的bean则应该使用singleton作用域。

使用:

```xml
<bean id="account" class="com.zxin.AccountImpl" scope="prototype"/>  
```

(3)、Request: 表示在一次HTTP请求中，一个bean定义对应一个实例；**即每个HTTP请求都会有各自的bean实例，它们依据某个bean定义创建而成**。该作用域仅在基于web的Spring ApplicationContext情形下有效。

(4)、Session: 表示在**一个HTTP Session中，一个bean定义对应一个实例**。该作用域仅在基于web的Spring ApplicationContext情形下有效。

(5)、Global Session: 表示在一个全局的HTTP Session中，一个bean定义对应一个实例。



## 二、Bean的生命周期

Bean 实例从创建到最后销毁，需要经过很多过程，执行很多生命周期方法: 

<div align="center"><img src="images/1_2.png"></div><br>

* 1)、调用无参构造器，创建实例对象(new)。 
* 2)、调用参数的 setter，为属性注入值(IOC)。 
* 3)、若 Bean 实现了 `BeanNameAware `接口，则会执行接口方法 setBeanName(String beanId)，**使Bean 类可以获取其在容器中的 id 名称**。
* 4)、若 Bean 实现了 `BeanFactoryAware` 接口，则执行接口方法 setBeanFactory(BeanFactory factory)，**使 Bean 类可以获取到 BeanFactory 对象**。 
* 5)、若 定 义 并 注 册 了 Bean 后 处 理 器 BeanPostProcessor ， 则 执行接口方法`postProcessBeforeInitialization()`。 
* 6)、若 Bean 实现了 InitializingBean 接口，则执行接口方法 afterPropertiesSet ()。 该方法
  在 Bean 的所有属性的 set 方法执行完毕后执行，是 Bean 初始化结束的标志，即 Bean 实例
  化结束。 
* 7)、若设置了 init-method 方法，则执行。 
* 8)、若 定 义 并 注 册 了 Bean 后 处 理 器 BeanPostProcessor ， 则 执 行 接 口 方 法
  `postProcessAfterInitialization()`。 

注：以上工作完成以后就可以应用这个Bean了，那这个Bean是一个Singleton的，所以一般情况下我们调用同一个id的Bean会是在内容地址相同的实例，当然在Spring配置文件中也可以配置非Singleton，这里我们不做赘述。

* 9)、执行业务方法(就是应用Bean)。 
* 10)、若 Bean 实现了 DisposableBean 接口，则执行接口方法 destroy()。
* 11)、若设置了` destroy- method` 方法，则执行。 