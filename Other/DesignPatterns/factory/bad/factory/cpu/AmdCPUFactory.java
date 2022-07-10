package factory.laterchange.abstractFactory.bad.factory.cpu;

import factory.laterchange.abstractFactory.bad.product.AmdCPU;
import factory.laterchange.abstractFactory.bad.product.CPU;

public class AmdCPUFactory implements CPUFactory {
    @Override
    public CPU makeCPU() {
        return new AmdCPU();
    }
}
