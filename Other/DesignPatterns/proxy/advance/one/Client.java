package proxy.advance.one;

public class Client {
    public static void main(String[] args) throws Exception {
        Movable tank = new Tank();
        // 现在就是说删除了TankTimeProxy，还是要能实现动态代理
        Movable tankProxy = (Movable) MyProxy.newProxyInstance(); // 动态代理不需要写出代理类的名字
        tankProxy.move();
    }
}
