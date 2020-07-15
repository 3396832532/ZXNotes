package factory.laterchange.abstractFactory.bad.computer;


import factory.laterchange.abstractFactory.bad.product.CPU;
import factory.laterchange.abstractFactory.bad.product.MainBoard;

public class Computer {

    private CPU cpu;

    private MainBoard mainBoard;

    public Computer(CPU cpu,  MainBoard mainBoard) {
        this.cpu = cpu;
        this.mainBoard = mainBoard;
    }
}
