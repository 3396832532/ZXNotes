package factory.laterchange.abstractFactory.bad;

import factory.laterchange.abstractFactory.bad.computer.Computer;
import factory.laterchange.abstractFactory.bad.factory.cpu.CPUFactory;
import factory.laterchange.abstractFactory.bad.factory.cpu.IntelCPUFactory;
import factory.laterchange.abstractFactory.bad.factory.mainboard.AmdMainBoardFactory;
import factory.laterchange.abstractFactory.bad.factory.mainboard.MainBoardFactory;
import factory.laterchange.abstractFactory.bad.product.CPU;
import factory.laterchange.abstractFactory.bad.product.MainBoard;

public class MyTest {
    public static void main(String[] args){
        // 得到 Intel 的 CPU
        CPUFactory intelCPUFactory = new IntelCPUFactory();
        CPU cpu = intelCPUFactory.makeCPU();

        // 得到 AMD 的主板
        MainBoardFactory mainBoardFactory = new AmdMainBoardFactory();
        MainBoard mainBoard = mainBoardFactory.makeMB();

        Computer computer = new Computer(cpu, mainBoard);
        System.out.println(computer);
    }
}
