package proxy.primary.two;

import java.util.Random;

public class Plane implements Flyable {
    @Override
    public void fly() {
        System.out.println("Plane Flying......");
        try {
            Thread.sleep(new Random().nextInt(5000)); // 随机产生 1~5秒, 飞机在飞行　
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
