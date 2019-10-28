package proxy.advance.one;
public class TankTimeProxy implements Movable {
      private Movable tank;
      public TankTimeProxy(Movable tank) {
             this.tank = tank;
      }
     @Override
     public void move() {
          long start = System.currentTimeMillis();
          System.out.println("start time : " + start);
          tank.move();
          long end = System.currentTimeMillis();
          System.out.println("end time : " + end);
          System.out.println("spend all time : " + (end - start)/1000 + "s.");
      }
}