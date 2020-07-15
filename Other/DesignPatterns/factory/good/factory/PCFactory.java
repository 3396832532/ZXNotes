package factory.laterchange.abstractFactory.good.factory;


import factory.laterchange.abstractFactory.good.product.CPU;
import factory.laterchange.abstractFactory.good.product.MainBoard;

public interface PCFactory {

    CPU makeCPU();
    MainBoard makeMB();
    // HardDisk makeHD();
}
