package proxy.primary.two;


import java.lang.reflect.Proxy;

public class Client {
    public static void main(String[] args){
        Movable tank = new Tank();
        //可以为所有对象产生时间代理的 InvocationHandler
        MyTimeProxyInvocationHandler myInvocationHandler = new MyTimeProxyInvocationHandler(tank);
        Movable tankProxy = (Movable) Proxy.newProxyInstance(
                tank.getClass().getClassLoader(),
                tank.getClass().getInterfaces(),
                myInvocationHandler
        );
        tankProxy.move();

        System.out.println("--------------------");

        Flyable plane = new Plane();
        myInvocationHandler = new MyTimeProxyInvocationHandler(plane);
        // 为飞机产生代理, 为..产生代理，这样可以为很多东西产生代理，静态代理做不到
        Flyable planeProxy = (Flyable) Proxy.newProxyInstance(
                plane.getClass().getClassLoader(),
                plane.getClass().getInterfaces(),
                myInvocationHandler
        );
        planeProxy.fly();
    }
}
