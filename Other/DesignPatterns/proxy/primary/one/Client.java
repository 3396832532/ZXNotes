package proxy.primary.one;

public class Client {
    public static void main(String[] args){
        Movable target = new TankLogProxy(new TankTimeProxy(new Tank()));    //先记录时间，再记录日志
//        Movable target = new TankTimeProxy(new TankLogProxy(new Tank())); //先记录日志，再记录时间
        target.move();
    }
}
