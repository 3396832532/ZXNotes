package factory.laterchange.abstractFactory.good.factory;

import factory.laterchange.abstractFactory.good.product.CPU;
import factory.laterchange.abstractFactory.good.product.IntelCPU;
import factory.laterchange.abstractFactory.good.product.IntelMainBoard;
import factory.laterchange.abstractFactory.good.product.MainBoard;

public class IntelFactory implements PCFactory {

    @Override
    public CPU makeCPU() {
        return new IntelCPU();
    }

    @Override
    public MainBoard makeMB() {
        return new IntelMainBoard();
    }
}
