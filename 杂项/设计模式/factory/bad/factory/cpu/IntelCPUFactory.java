package factory.laterchange.abstractFactory.bad.factory.cpu;

import factory.laterchange.abstractFactory.bad.product.CPU;
import factory.laterchange.abstractFactory.bad.product.IntelCPU;

public class IntelCPUFactory implements CPUFactory {

    @Override
    public CPU makeCPU() {
        return new IntelCPU();
    }
}
