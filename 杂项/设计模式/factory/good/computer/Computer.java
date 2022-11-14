package factory.laterchange.abstractFactory.good.computer;


import factory.laterchange.abstractFactory.good.product.CPU;
import factory.laterchange.abstractFactory.good.product.MainBoard;

public class Computer {

    private CPU cpu;

    private MainBoard mainBoard;

    public Computer(CPU cpu, MainBoard mainBoard) {
        this.cpu = cpu;
        this.mainBoard = mainBoard;
    }
}
