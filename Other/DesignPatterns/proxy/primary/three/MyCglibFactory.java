package proxy.primary.three;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

//需要实现MethodInterceptor, 当前这个类的对象就是一个回调对象
// MyCglibFactory 是 类A，它调用了Enhancer(类B)的方法: setCallback(this)，而且将类A对象传给了类B
// 而类A 的 方法intercept会被类B的 setCallback调用，这就是回调设计模式
public class MyCglibFactory implements MethodInterceptor {  //public interface MethodInterceptor extends Callback

    private Tank target;

    public MyCglibFactory(Tank target) {
        this.target = target;
    }

    public Tank myCglibCreator() {
        Enhancer enhancer = new Enhancer();

        // 设置需要代理的对象 :  目标类(target) , 也是父类
        enhancer.setSuperclass(Tank.class);

        // 设置代理对象， 这是回调设计模式:  设置回调接口对象 :
        enhancer.setCallback(this); // this代表当前类的对象，因为当前类实现了Callback

        return (Tank) enhancer.create();
    }

    // 这个就是回调方法（类A的方法）
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 在前面做一些事情: 记录开始时间
        long start = System.currentTimeMillis();
        System.out.println("start time : " + start);

        method.invoke(target, args);

        // 在后面做一些事情: 记录结束时间,并计算move()运行时间
        long end = System.currentTimeMillis();
        System.out.println("end time : " + end);
        System.out.println("spend all time : " + (end - start)/1000 + "s.");
        return null;
    }
}
