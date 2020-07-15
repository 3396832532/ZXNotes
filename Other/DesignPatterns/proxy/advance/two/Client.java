package proxy.advance.two;

//　可以生成实现了任何接口的代理, 只要把接口传进去就可以了
public class Client {
    public static void main(String[] args) throws Exception {
        Movable tank = new Tank();
        MyInvocationHandler timeHandler = new MyTimeInvocationHandler(tank);
        Movable tankProxy = (Movable) MyProxy.newProxyInstance(Movable.class, timeHandler); // 传入类的.class即可
        tankProxy.move();

        System.out.println("--------------------");

        Flyable plane = new Plane();
        timeHandler = new MyTimeInvocationHandler(plane);
        Flyable planeProxy = (Flyable) MyProxy.newProxyInstance(Flyable.class, timeHandler);
        planeProxy.fly();
    }
}
