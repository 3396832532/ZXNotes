# 自定义starter

可以参考如下博客:

https://blog.csdn.net/vbirdbest/article/details/79863883

整体代码框架:

<div align="center"><img src="assets/1557130098011.png"></div><br>
第二个项目`zxzxin-spring-boot-starter-autoconfigurer`的`pom.xml`文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.4.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.zxzxin.starter</groupId>
	<artifactId>zxzxin-spring-boot-starter-autoconfigurer</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>zxzxin-spring-boot-starter-autoconfigurer</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

第一个项目`zxzxin-spring-boot-starter`的`pom.xml`文件要引入第二个项目的pom地址：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zxzxin.starter</groupId>
    <artifactId>zxzxin-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>

    <!--引入自动配置模块-->
    <dependencies>
        <dependency>
            <groupId>com.zxzxin.starter</groupId>
            <artifactId>zxzxin-spring-boot-starter-autoconfigurer</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```

编写三个重要的类: `MyProperties.java、MyService.java、MyAutoConfigurattion.java`:

```java
@ConfigurationProperties(prefix = "zxzxin.hello")
//@Component //如果这里添加了注解那么在自动配置类的时候就不用添加@enableConfigurationProperties(MyProperties.class)注解.
public class MyProperties {

    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
```

其中，属性可以给出默认值。
> 这是一个简单的属性值对象，那么相当于写死的字段就是SpringBoot为我们自动配置的配置，那么我们很多时候可以自己在`application.properties`中修改某些配置就是这样的道理，我们不设置就是默认的，设置了就是我们设置的属性。

```java
/**
 这里很重要，如果我们添加了这个注解，
 那么按照我们下面的设置SpringBoot会优先使用我们配置的这个Bean，
 这是符合SpringBoot框架优先使用自定义Bean的原则的。
 */
//@Component
public class MyService {

    private MyProperties myProperties;

    public MyProperties getMyProperties() {
        return myProperties;
    }

    public void setMyProperties(MyProperties myProperties) {
        this.myProperties = myProperties;
    }

    //为我们服务的方法
    public String say(String name) {
        return myProperties.getPrefix() + "-----" + name + "-----" + myProperties.getSuffix();
    }
}
```

```java
@Configuration //配置类
//这里就是前面说的，这个注解读入我们的配置对象类
@EnableConfigurationProperties(MyProperties.class)
//当类路径存在这个类时才会加载这个配置类，否则跳过,这个很有用, 比如不同jar包间类依赖，依赖的类不存在直接跳过，不会报错
@ConditionalOnClass(MyService.class)
public class MyAutoConfiguration {

    @Autowired
    private MyProperties myProperties;

    // 注入到容器中
    @Bean
    //这个配置就是SpringBoot可以优先使用自定义Bean的核心所在，如果没有我们的自定义Bean那么才会自动配置一个新的Bean
    @ConditionalOnMissingBean(MyService.class)
    public MyService auto() {
        MyService service = new MyService();
        service.setMyProperties(myProperties); // 在这里设置了 MyProperties
        return service;
    }
}
```

然后我们在`META-INF/spring.factories`中添加下面的代码:

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.zxzxin.starter.MyAutoConfiguration
```



## 总结

其实在很多时候我们的配置是在很多jar包里的，那么我们新的应用该怎么读入这些jar包里的配置文件呢，SpringBoot是这样管理的。

最主要的注解就是`@enableAutoConfiguration`，而这个注解会导入一个`EnableAutoConfigurationImportSelector`的类，而这个类会去读取一个`spring.factories`下key为`EnableAutoConfiguration`全限定名对应值。

![1557112575290](assets/1557112575290.png)

所以如果需要我们可以在我们的`resources`目录下创建`spring.factories`下添加类似的配置即可。