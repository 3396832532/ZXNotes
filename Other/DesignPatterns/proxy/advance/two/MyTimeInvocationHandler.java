package proxy.advance.two;

import java.lang.reflect.Method;

public class MyTimeInvocationHandler implements MyInvocationHandler {

    private Object target; //注意是 Object,这样可以对任意对象进行时间的代理

    public MyTimeInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public void invoke(Object proxy, Method m) {
        // 在前面做一些事情: 记录开始时间
        long start = System.currentTimeMillis();
        System.out.println("start time : " + start);
        System.out.println("proxy : " + proxy.getClass().getName()); // 打印proxy 到底是什么
        System.out.println("target : " + target.getClass().getName()); // 打印 target 到底是什么
        try {
            m.invoke(target); // 调用 target的方法
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("end time : " + end);
        System.out.println("spend all time : " + (end - start) / 1000 + "s.");
    }
}
