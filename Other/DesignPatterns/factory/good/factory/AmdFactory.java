package factory.laterchange.abstractFactory.good.factory;

import factory.laterchange.abstractFactory.good.product.AmdCPU;
import factory.laterchange.abstractFactory.good.product.AmdMainBoard;
import factory.laterchange.abstractFactory.good.product.CPU;
import factory.laterchange.abstractFactory.good.product.MainBoard;

public class AmdFactory implements PCFactory{

    @Override
    public CPU makeCPU() {
        return new AmdCPU();
    }

    @Override
    public MainBoard makeMB() {
        return new AmdMainBoard();
    }
}
