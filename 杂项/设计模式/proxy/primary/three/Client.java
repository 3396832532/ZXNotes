package proxy.primary.three;


public class Client {
    public static void main(String[] args){
        Tank proxyTank = new MyCglibFactory(new Tank()).myCglibCreator();
        proxyTank.move();
    }
}
